package com.getusroi.wms20.metamodel.bean;

import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.metamodel.delete.DeleteFrom;
import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.schema.Table;
import org.json.JSONObject;

import com.getusroi.eventframework.abstractbean.AbstractMetaModelBean;

public class MetaModelCRUDFailedBean extends AbstractMetaModelBean{

	@Override
	protected void processBean(Exchange exchange) throws Exception {
		logger.debug("inside process bean method of MetaModelCRUDFailedBean");
		//JdbcDataContext datacontext=getMetaModelJdbcDataContext(exchange);
		CamelContext context=exchange.getContext();
		DataSource oracledatasource=(DataSource) context.getRegistry().lookupByName("oracleDB");
		setDataSource(oracledatasource);
		JdbcDataContext datacontext=getLocalDataContext(exchange);
		logger.debug("datacontext object using jndi name MetaModelCRUDFailedBean : "+datacontext);
		Table table=datacontext.getDefaultSchema().getTableByName("AreaList");	
		logger.debug("Table Object for AreaList Table : "+table);
		datacontext.executeUpdate(new DeleteFrom(table).where("AreaId").eq(77));
		
	}
	
	public void deleteFail(Exchange exchange) throws Exception{
		logger.debug("inside deleteFail method of MetaModelCRUDFailedBean");
		try {
			CamelContext context=exchange.getContext();
			DataSource oracledatasource=(DataSource) context.getRegistry().lookupByName("oracleDB");
			setDataSource(oracledatasource);
			JdbcDataContext datacontext=getLocalDataContext(exchange);
			Table table=datacontext.getDefaultSchema().getTableByName("AreaList");	
			logger.debug("Table Object for AreaList Table : "+table);
			datacontext.executeUpdate(new DeleteFrom(table).where("AreaIddfds").eq(77));
		} catch (Exception e) {
			Message msg=exchange.getOut();
			msg.setHeader(Exchange.HTTP_RESPONSE_CODE, new Integer(500));
			  JSONObject errorJsonObject = new JSONObject();
			  errorJsonObject.put("InternalSystemError: ", e.getMessage());
			  msg.setBody(errorJsonObject);
			  throw new Exception(errorJsonObject.toString());
			  
		}
		
	}

}
