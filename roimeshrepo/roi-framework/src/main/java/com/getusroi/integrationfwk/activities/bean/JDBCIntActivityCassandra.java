package com.getusroi.integrationfwk.activities.bean;

import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.Exchange;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.data.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.datastax.driver.core.Cluster;
import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.eventframework.abstractbean.util.CassandraClusterException;
import com.getusroi.eventframework.abstractbean.util.CassandraConnectionException;
import com.getusroi.eventframework.abstractbean.util.CassandraUtil;
import com.getusroi.eventframework.abstractbean.util.ConnectionConfigurationException;
import com.getusroi.integrationfwk.config.jaxb.FieldMapper;
import com.getusroi.integrationfwk.config.jaxb.JDBCIntActivity;
import com.getusroi.integrationfwk.config.jaxb.PipeActivity;
import com.getusroi.integrationfwk.config.jaxb.XmlFieldMapper;
import com.getusroi.integrationfwk.jdbcIntactivity.config.JdbcIntActivityConfigurationQueryProcessor;
import com.getusroi.integrationfwk.jdbcIntactivity.config.JdbcIntActivityQueryProcessingException;
import com.getusroi.integrationfwk.jdbcIntactivity.config.helper.JdbcIntActivityConfigHelper;
import com.getusroi.integrationfwk.jdbcIntactivity.config.helper.JdbcIntActivityConfigurationException;
import com.getusroi.integrationfwk.jdbcIntactivity.config.helper.JdbcIntActivityExecutionException;
import com.getusroi.integrationfwk.jdbcIntactivity.config.helper.JdbcIntActivityStringParserException;

public class JDBCIntActivityCassandra extends AbstractCassandraBean{
	private final Logger log = LoggerFactory.getLogger(JdbcIntActivityConfigurationQueryProcessor.class.getName());
	
	/**
	 * processor of the Sql query from the Configuration JAXBObject
	 * 
	 * @throws JdbcIntActivityQueryProcessingException
	 */
	@Override
	protected void processBean(Exchange exchange) throws JdbcIntActivityQueryProcessingException {
		log.debug(".processBean()..of JDBCIntActivityCassandra..");
		JdbcIntActivityConfigHelper configHelper = new JdbcIntActivityConfigHelper();
		PipeActivity pipeactivity = (PipeActivity) exchange.getIn().getHeader(ActivityConstant.PIPEACTIVITY_HEADER_KEY);
		log.debug(".inProcessBean jdbcIntActivity.." + pipeactivity);
		JDBCIntActivity configObject = pipeactivity.getJDBCIntActivity();
		Set<String> xpathExpression;
		String newQuery;
		String operation;
		try {
			/*
			 * @note: this snippet is before execution of Query, where
			 * substitution of field happens
			 */
			xpathExpression = getXpathExpressionFromJdbcActivityUnit(configObject);
			ArrayList<String> fieldKey = getListofFieldMapperKeys(configObject);
			String sqlQueryConfig = getSqlStringFromJdbcActivityConfigUnit(configObject);
			operation = getSqlOperationFromJdbcActivityConfigUnit(configObject);
			//String inputXml = XML_RAW_VALUE;
			String inputXml = retreiveXmlInputFromExchangeBody(exchange);
			Document xmlDocument = configHelper.generateDocumentFromString(inputXml);
			List<Object> setOfValuesProcessed = configHelper.xpathProcessingOnInputXml(xpathExpression, xmlDocument);

			newQuery = configHelper.processSqlFieldSubstitution(sqlQueryConfig, fieldKey, setOfValuesProcessed);
			log.debug("The new query: " + newQuery);
			try {
				executeSQLQuery(operation,newQuery,xmlDocument,configObject,exchange);
			} catch (JdbcIntActivityExecutionException
					| ActivityEnricherException e) {
				throw new JdbcIntActivityQueryProcessingException(
						"unable to execute the sql query", e);
			}
		} catch (JdbcIntActivityConfigurationException e) {
			throw new JdbcIntActivityQueryProcessingException(
					"Unable to process , as pre-processing failed! in preparation stage", e);
		}
			
	}// ..end of the method
	
	/**
	 * This method is used to decide if the method is of type select or other(insert|update|delete) and call method which suitable for operation
	 * @param operation : SQL operation like (select,update,delete,insert) in string format
	 * @param query : SQL query in string format
	 * @throws JdbcIntActivityQueryProcessingException
	 */
	private void executeSQLQuery(String operation,String query,Document xmlDocument,JDBCIntActivity configObject,Exchange exchange) throws JdbcIntActivityQueryProcessingException, JdbcIntActivityExecutionException, ActivityEnricherException{
		log.debug(".executeSQLQuery method of JDBCIntActivityCassandra");
		JdbcIntActivityConfigHelper configHelper = new JdbcIntActivityConfigHelper(operation,query);
		if(operation.equalsIgnoreCase("SELECT")){
			Object response=executeSQLQueryByDatContext(operation,query,configHelper);
			ArrayList<String> colums = configHelper.getColumnNamesFromSelectQuery(query);
			Row rowresp = (Row) response;
			List<Object> listOfValues = Arrays.asList(rowresp.getValues());
			if (listOfValues.size() == colums.size()) {
				String xmlEnriched = configHelper.processxmlEnrichment(xmlDocument, configObject, colums, listOfValues);
				exchange.getIn().setBody(xmlEnriched);
			}
		}else{
			executeSQLQueryByUpdateableDataContext(operation,query,configHelper);
		}
	}//end of method
	
	
	
	/**
	 * This method is used execute the select operation of sql using apache metamodel.
	 * To perform select, it require to get cluster object and pass it to metamodel api to get data context
	 * @param operation
	 * @param query
	 * @param configHelper
	 * @throws JdbcIntActivityQueryProcessingException
	 */
	private Object executeSQLQueryByDatContext(String operation,String query,JdbcIntActivityConfigHelper configHelper) throws JdbcIntActivityQueryProcessingException{
		log.debug(".executeSQLQueryByDatContext method of JDBCIntActivityCassandra");
		try {
			try {
				Properties prop=CassandraUtil.getCassandraConfigProperties();
				Cluster cluster=getCassandraCluster();
				String keyspace=prop.getProperty(CassandraUtil.KEYSPACE_KEY);
				log.debug("keyspace : "+keyspace);
				DataContext datacontext=getDataContextForCassandraByCluster(cluster,keyspace);
				configHelper.setDataContext(datacontext);
				try {
					//passing datacontext as select operation is done using datacontext object,therefore passsing updateableDatacontext as null
					Object reponse=configHelper.decideQueryToPerform(operation, query, null, datacontext);
					return reponse;
				} catch (JdbcIntActivityExecutionException | JdbcIntActivityStringParserException e) {
					throw new JdbcIntActivityQueryProcessingException("Unable to process the query string : "+query);
				}
			} catch (ConnectionConfigurationException e1) {
				throw new JdbcIntActivityQueryProcessingException("");
			}			
		} catch (CassandraClusterException e) {
			throw new JdbcIntActivityQueryProcessingException("Unable to process the query string : "+query+" due to unable to get the cluster with cassandra");

		}
	}//end of method executeSQLQueryByDatContext
	
	/**
	 * This method is used execute the update|insert|delete operation of sql using apache metamodel and "cassandra-jdbc" driver.
	 * To perform update|insert|delete, it require to get connection object and pass it to metamodel api to get updateable data context
	 * @param operation
	 * @param query
	 * @param configHelper
	 * @throws JdbcIntActivityQueryProcessingException
	 */
	private void executeSQLQueryByUpdateableDataContext(String operation,String query,JdbcIntActivityConfigHelper configHelper) throws JdbcIntActivityQueryProcessingException{
		log.debug(".executeSQLQueryByUpdateableDataContext method of JDBCIntActivityCassandra");
		Connection connection=null;
		try {
			log.debug("operation provided for sql is either insert|delete|update");
			connection=getCassandraConnection();
			UpdateableDataContext updateableDataContext=getUpdateableDataContextForCassandra(connection);
			configHelper.setUpdateableDataContext(updateableDataContext);
			try {
				//passing updateableDataContext because cassandra insert|delete|update workes using wrapper, so passing value for datacontext as null
				configHelper.decideQueryToPerform(operation,query,updateableDataContext,null);
			} catch (JdbcIntActivityExecutionException | JdbcIntActivityStringParserException e) {
				throw new JdbcIntActivityQueryProcessingException("Unable to process the query string : "+query);
			}
		} catch (CassandraConnectionException e) {
			throw new JdbcIntActivityQueryProcessingException("Unable to process the query string : "+query+" due to unable to get the connection with cassandra");

		}
	}//end of method executeSQLQueryByUpdateableDataContext
	

	/**
	 * gets the exchange body and traverse the JsonObject to get the
	 * XmlToProcess
	 * 
	 * @param exchange,
	 *            is used to get the xml from exchange Body
	 * @return returns the xmlString
	 * @throws JdbcIntActivityQueryProcessingException
	 */
	private String retreiveXmlInputFromExchangeBody(Exchange exchange) throws JdbcIntActivityQueryProcessingException {
			log.debug(".retreiveXmlInputFromExchangeBody method of JdbcIntActivity");		
			String xmlInput = exchange.getIn().getBody(String.class);
			log.debug("xml input in exchange body : "+xmlInput);
			if(xmlInput !=null && !(xmlInput.isEmpty())){
				return xmlInput;
			}else{
				throw new JdbcIntActivityQueryProcessingException("exchange body is null,There is no data to process");
			}
			
	}// ..end of the method

	

	/**
	 * gets the xpath expressions from the JDBCIntActivity object
	 * 
	 * @param configObject,
	 *            is loaded with the XpathExpressions
	 * @return the setOfXpathExpressions from the ConfigurationObject
	 * @throws JdbcIntActivityConfigurationException
	 */
	private Set<String> getXpathExpressionFromJdbcActivityUnit(JDBCIntActivity configObject)
			throws JdbcIntActivityConfigurationException {
		log.debug(".getXpathExpressionSet()..getting xpath xpressions configured as list...");
		Set<String> set = new LinkedHashSet();
		ArrayList<FieldMapper> arr = (ArrayList<FieldMapper>) configObject.getDbmsMapper().getFieldMapper();
		for (int i = 0; i < arr.size(); i++) {
			String fieldStr = arr.get(i).getXPath();
			set.add(fieldStr);
		}
		if (!set.isEmpty() || set.size() != arr.size()) {
			return set;
		} else {
			throw new JdbcIntActivityConfigurationException(
					"Activity configuration is not formed well, as it encountered empty xpath Expression");
		}
	}// .. end of the method

	/**
	 * gets the list of field mappers available in the configuration
	 * 
	 * @param configObject,
	 *            is loaded with field Mappers
	 * @return list of field mapper Keys
	 * @throws JdbcIntActivityConfigurationException
	 */
	private ArrayList<String> getListofFieldMapperKeys(JDBCIntActivity configObject)
			throws JdbcIntActivityConfigurationException {
		log.debug(".getListofFieldMapperKeys()..getting fieldMapper keys from the configurations..");
		ArrayList<String> list = new ArrayList<>();
		ArrayList<FieldMapper> arr = (ArrayList<FieldMapper>) configObject.getDbmsMapper().getFieldMapper();
		if (!arr.isEmpty()) {
			for (int i = 0; i < arr.size(); i++) {
				String fieldStr = arr.get(i).getField();
				list.add(fieldStr);
			}
			return list;
		} else {
			throw new JdbcIntActivityConfigurationException("Unable to get non-empty list of fieldMapper..");
		}
	}// ..end of the method

	/**
	 * gets the sql query configured, as String
	 * 
	 * @param configObject,
	 *            the configuration object is loaded with the SQL
	 * @return sql queryString to be processed
	 * @throws JdbcIntActivityConfigurationException
	 */
	private String getSqlStringFromJdbcActivityConfigUnit(JDBCIntActivity configObject)
			throws JdbcIntActivityConfigurationException {
		String sqlQuery = configObject.getSQL();
		if (!sqlQuery.isEmpty()) {
			return sqlQuery;
		} else {
			throw new JdbcIntActivityConfigurationException("Unable to get the Sql query configured..");
		}
	}// ..end of the method

	/**
	 * gets the SQL operation specified will be processed
	 * 
	 * @param configObject,
	 *            loaded with the type of Operation
	 * @return Operation specified in String
	 * @throws JdbcIntActivityConfigurationException
	 */
	private String getSqlOperationFromJdbcActivityConfigUnit(JDBCIntActivity configObject)
			throws JdbcIntActivityConfigurationException {
		String sqlOperation = configObject.getDBConfig().getOperation();
		if (!sqlOperation.isEmpty()) {
			log.info("FrameWork is instructed to process the operation : " + sqlOperation
					+ " : Hence preparing the rest to progress..");
			return sqlOperation;
		} else {
			throw new JdbcIntActivityConfigurationException("Unable to get the Sql operation configured..");
		}
	}// ..end of the method
}