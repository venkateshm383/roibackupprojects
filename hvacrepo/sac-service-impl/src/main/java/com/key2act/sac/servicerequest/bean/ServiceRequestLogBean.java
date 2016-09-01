package com.key2act.sac.servicerequest.bean;

import static com.key2act.sac.servicerequest.ServiceRequestConstant.*;
import static com.key2act.sac.util.SACConstant.*;
import java.sql.Connection;

import org.apache.camel.Exchange;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.insert.InsertInto;
import org.apache.metamodel.schema.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.key2act.sac.util.ServiceRequestUtil;
import com.key2act.sac.util.UnableToParseServiceRequestException;

public class ServiceRequestLogBean extends AbstractCassandraBean{
	Logger logger=LoggerFactory.getLogger(ServiceRequestLogBean.class);

	@Override
	protected void processBean(Exchange exchange) throws Exception {
		logger.debug(".processBean method of ServiceRequestLogBean");
		Connection connection=getCassandraConnection();
		UpdateableDataContext dataContext = getUpdateableDataContextForCassandra(connection);
		Table table=getTableForDataContext(dataContext, "servicechanellog");
		insertServiceRequestLog(dataContext, table, exchange);		
		
	}
	
	/**
	 * This method is used to store the temp log data about service request
	 * @param datacontext : UpdateableDataContextontext Object
	 * @param table : Table Object
	 * @param exchange : Camel Exchange Object
	 * @throws UnableToParseServiceRequestException 
	 */
	private void insertServiceRequestLog(UpdateableDataContext datacontext,Table table,Exchange exchange) throws UnableToParseServiceRequestException{
		logger.debug(".insertServiceRequestLog method of ServiceRequestLogBean");
		String bodyIn=exchange.getIn().getBody(String.class);
		ServiceRequestUtil serviceRequestUtil=new ServiceRequestUtil();
		String uid=serviceRequestUtil.generateUID();
		MeshHeader meshHeader=(MeshHeader)exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		String tenant=meshHeader.getTenant();
		if(bodyIn !=null){
			Document document=serviceRequestUtil.generateDocumentFromString(bodyIn);
			String trNum=null;
			if(document !=null){
				NodeList nodeList=document.getElementsByTagName(TRANSACTION_NUMBER_XML_ELEEMNT);
				//checking if 
				if(nodeList !=null && nodeList.getLength()>0){
					Node node =nodeList.item(0);								
					trNum=node.getTextContent();
					logger.debug("textcontext of trNum node : "+trNum);				
				}
			}
			datacontext.executeUpdate(new InsertInto(table)
					.value(TENANTID,tenant)					
					.value(SOURCE_REQUEST_ID_DB_COLUMN_KEY,trNum)
					.value(SOURCE_REQUEST_LOG_UID_DB_COLUMN_KEY,uid)
					.value(SOURCE_XMLPLAYLOAD_DB_COLUMN_KEY,bodyIn));
					
			logger.debug("==> ServiceChanelLog data inserted successfully...");
		}
		
	}//end of method insertServiceRequestLog
	
	
	

}
