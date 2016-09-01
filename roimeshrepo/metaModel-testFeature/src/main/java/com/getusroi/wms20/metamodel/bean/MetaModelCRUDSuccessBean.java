package com.getusroi.wms20.metamodel.bean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.insert.InsertInto;
import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.schema.Table;
import org.apache.metamodel.update.Update;

import com.getusroi.config.RequestContext;
import com.getusroi.config.persistence.UndefinedPrimaryVendorForFeature;
import com.getusroi.datacontext.config.DataContextConfigurationException;
import com.getusroi.datacontext.jaxb.DataContext;
import com.getusroi.datacontext.jaxb.FeatureDataContext;
import com.getusroi.eventframework.abstractbean.AbstractMetaModelBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.mesh.util.MeshConfigurationUtil;

public class MetaModelCRUDSuccessBean extends AbstractMetaModelBean{

	@Override
	protected void processBean(Exchange exchange) throws Exception {
		logger.debug("inside process bean method of MetaModelCRUDSuccessBean");
		CamelContext context=exchange.getContext();		
		DataSource oracledatasource=(DataSource) context.getRegistry().lookupByName("oracleDB");
		setDataSource(oracledatasource);		
		JdbcDataContext datacontext=getLocalDataContext(exchange);
		Table table=datacontext.getDefaultSchema().getTableByName("AreaList");	
		logger.debug("Table Object for AreaList Table : "+table);
		datacontext.executeUpdate(new InsertInto(table).value("AreaId",7).value("AreaType","stagePic1").value("AreaName", "testBeanPass1"));
		datacontext.executeUpdate(new InsertInto(table).value("AreaId",77).value("AreaType","stagePic2").value("AreaName", "testBeanPass2"));
		logger.debug("Table Object for Arealist Table : "+table);
		datacontext.executeUpdate(new Update(table).value("AreaType","stagePic1Update").value("AreaName", "testBeanPass1Update").where("AreaId").eq(7));
	}
	
	public void updateTable(Exchange exchange) throws Exception{
		logger.debug("inside updateTable method of MetaModelCRUDSuccessBean");		
		CamelContext context=exchange.getContext();
		DataSource oracledatasource=(DataSource) context.getRegistry().lookupByName("oracleDB");
		setDataSource(oracledatasource);
		JdbcDataContext datacontext=getLocalDataContext(exchange);
		Table table=datacontext.getDefaultSchema().getTableByName("AreaList");	
		logger.debug("Table Object for Arealist Table : "+table);
		datacontext.executeUpdate(new Update(table).value("AreaType","stagePic1121Update").value("AreaName", "testBeanPass1123Update").where("AreaId").eq(7));
	}
	
	public void selectTable(Exchange exchange) throws Exception{
		logger.debug("inside selectTable method of MetaModelCRUDSuccessBean");
		//JdbcDataContext datacontext=getMetaModelJdbcDataContext(exchange);
		CamelContext context=exchange.getContext();
		DataSource oracledatasource=(DataSource) context.getRegistry().lookupByName("oracleDB");
		setDataSource(oracledatasource);
		JdbcDataContext datacontext=getLocalDataContext(exchange);
		DataSet dataSet = datacontext.query()
		        .from(datacontext.getDefaultSchema().getTableByName("AreaList"))
		        .selectAll()
		        .execute();
		logger.debug("dataset : "+dataSet);
		    for (final Row row : dataSet) {
		    	logger.debug(""+Arrays.asList(row.getValues()));
		    }   
	}
	
	
	
}
