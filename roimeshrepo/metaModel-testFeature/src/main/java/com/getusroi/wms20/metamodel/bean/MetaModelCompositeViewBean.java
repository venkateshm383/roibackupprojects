package com.getusroi.wms20.metamodel.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.metamodel.CompositeDataContext;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.query.Query;
import org.apache.metamodel.schema.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.eventframework.abstractbean.AbstractMetaModelBean;
import com.getusroi.integrationfwk.jdbcIntactivity.config.persistence.impl.JdbcIntActivityMetaModelUtil;

public class MetaModelCompositeViewBean extends AbstractMetaModelBean{
	protected static final Logger logger = LoggerFactory.getLogger(MetaModelCompositeViewBean.class);

	@Override
	protected void processBean(Exchange exchange) throws Exception {
		logger.debug(".processBean method of MetaModelCompositeViewBean");
		CamelContext context=exchange.getContext();
		//lookup for mssql datasource and get the connection
		DataSource msSQLDS=JdbcIntActivityMetaModelUtil.getDataSource(context,"msSQLDB");
		logger.debug("DataSOurce for mssql : "+msSQLDS);
		setDataSource(msSQLDS);
		DataContext msSQLDataContext=getLocalDataContext(exchange);
		
		//lookup for oracle datasource and get the connection
		DataSource oracleDS=JdbcIntActivityMetaModelUtil.getDataSource(context,"oracleDB");
		logger.debug("DataSOurce for oracleDB : "+oracleDS);
		setDataSource(oracleDS);
		DataContext oracleDataContext=getLocalDataContext(exchange);
		
		//create a collection of  datacontext for different types of data sources
		Collection<DataContext> dcCollection=new ArrayList<>();
		dcCollection.add(msSQLDataContext);
		dcCollection.add(oracleDataContext);
		
		//call composite view method
		compositeDataView(dcCollection);
		compositeViewUsingMetaModel(oracleDataContext,exchange);
	}//end of method
	
	/**
	 * This method is used for creating composite view using different data sources
	 * @param dcCollection : Collection Object containing data context object of data sources
	 */
	private void compositeDataView(Collection<DataContext> dcCollection){
		logger.debug(".compositeDataView method of MetaModelCompositeViewBean");
		DataContext dataContextComposite=new CompositeDataContext(dcCollection);
		Table oracleTable=dataContextComposite.getSchemaByName("ADMIN").getTableByName("users");
		logger.debug("Table oracle users : "+oracleTable.getName());
		logger.debug("Table oracle users column : "+oracleTable.getColumnNames());
		Table mssqlTable=dataContextComposite.getSchemaByName("dbo").getTableByName("AreaList");
		logger.debug("Table mssql AreaList : "+mssqlTable.getName());
		logger.debug("Table mssql AreaList column : "+mssqlTable.getColumnNames());	
		
		DataSet oracleDataSet=dataContextComposite.query().from(oracleTable).selectAll().execute();
		logger.debug("dataset oracleTable rows : "+oracleDataSet);
	    for ( Row row : oracleDataSet) {
	    	logger.debug("data : "+Arrays.asList(row.getValues()));
	    }
		DataSet msSQLDataSet=dataContextComposite.query().from(mssqlTable).selectAll().execute();
		logger.debug("dataset mssqlTable rows : "+msSQLDataSet);
	    for ( Row row : msSQLDataSet) {
	    	logger.debug("data : "+Arrays.asList(row.getValues()));
	    }
		Query q =dataContextComposite.query().from(oracleTable).leftJoin(mssqlTable).on(oracleTable.getColumnByName("userid"), mssqlTable.getColumnByName("AreaId")).selectAll().toQuery();
		  DataSet dataSet = dataContextComposite.executeQuery(q);
		  logger.debug("query created : "+q);			
			logger.debug("------------");
		    for ( Row row : dataSet) {		    	
		    	logger.debug("data : "+Arrays.asList(row.getValues()));
		    }

	}//end of method
	
	public void compositeViewUsingMetaModel(DataContext thisDataContext,Exchange exchange) throws Exception{
		logger.debug(".compositeViewUsingMetaModel method of MetaModelCompositeViewBean");
		CamelContext context=exchange.getContext();
		
		//get other feature datacontext 
		 com.getusroi.datacontext.jaxb.DataContext printServiceReferenceDataContext = getReferenceFeatureDataContext("print", "printservice",exchange);
		 DataSource printServiceReferenceDS=JdbcIntActivityMetaModelUtil.getDataSource(context,printServiceReferenceDataContext.getDbBeanRefName().trim());
		 setDataSource(printServiceReferenceDS);
		DataContext printServiceDataContext=getLocalDataContext(exchange);
		
		
		//create a collection of  datacontext for different types of data sources
		Collection<DataContext> dcCollection = new ArrayList<>();
		dcCollection.add(thisDataContext);
		dcCollection.add(printServiceDataContext);
		
		DataContext dataContextComposite=new CompositeDataContext(dcCollection);
		
		Table printserviceTable=dataContextComposite.getSchemaByName("ADMIN").getTableByName("users");
		logger.debug("Table oracle users : "+printserviceTable.getName());
		Table thisTable=dataContextComposite.getSchemaByName("roi").getTableByName("AreaList");
		logger.debug("Table current feature  AreaList : "+thisTable.getName());
		
		Query q =dataContextComposite.query().from(printserviceTable).leftJoin(thisTable).on(printserviceTable.getColumnByName("userid"), thisTable.getColumnByName("AreaId")).selectAll().toQuery();
		  DataSet dataSet = dataContextComposite.executeQuery(q);
		  logger.debug("query created : "+q);			
			logger.debug("------------");
		    for ( Row row : dataSet) {		    	
		    	logger.debug("data : "+Arrays.asList(row.getValues()));
		    }

		
	}

}
