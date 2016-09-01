package com.getusroi.wms20.metamodel.bean;

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.metamodel.insert.InsertInto;
import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.schema.Table;

import com.getusroi.config.RequestContext;
import com.getusroi.datacontext.jaxb.DataContext;
import com.getusroi.datacontext.jaxb.FeatureDataContext;
import com.getusroi.eventframework.abstractbean.AbstractMetaModelBean;
import com.getusroi.integrationfwk.jdbcIntactivity.config.persistence.impl.JdbcIntActivityMetaModelUtil;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.mesh.util.MeshConfigurationUtil;

public class MetaModelFeatureDataContext extends AbstractMetaModelBean {

	@Override
	protected void processBean(Exchange exchange) throws Exception {
		logger.debug(".processBean method of MetaModelFeatureDataContext");
		// how to access other feature dataContext
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		RequestContext reqContext = meshHeader.getRequestContext();
		logger.debug("requestContext : " + reqContext);
		FeatureDataContext thisFeatureDataContext = getFeatureDataContextObject(reqContext);
		getRefFeatureDataContext(reqContext, thisFeatureDataContext, exchange);
	}

	private void getRefFeatureDataContext(RequestContext reqContext,
			FeatureDataContext featureDataContext, Exchange exchange) throws Exception {
		// 1) get datacontext by feature group and feature name
		getRefDataContextByFeatureGroupAndFeature(exchange);

		// 2)create a newRequestContext
		getRefDataContextByRequestContext(featureDataContext, exchange);
	}

	/**
	 * This method used get data context by feature  group and feature name
	 * @param meshConfigUtil : MeshConfigurationUtil class object
	 * @param reqContext : request context object
	 * @param exchange : camel exchange object
	 * @throws Exception
	 */
	private void getRefDataContextByFeatureGroupAndFeature(Exchange exchange) throws Exception {
		logger.debug(".getRefDataContextByFeatureGroupAndFeature method of MetaModelFeatureDataContext");
		CamelContext context = exchange.getContext();
		 DataContext printServiceReferenceDataContext  = getReferenceFeatureDataContext("print", "printservice",exchange);
		 DataSource printServiceReferenceDS=JdbcIntActivityMetaModelUtil.getDataSource(context,printServiceReferenceDataContext.getDbBeanRefName().trim());
		 setDataSource(printServiceReferenceDS);
		JdbcDataContext printServiceDataContext=getLocalDataContext(exchange);
		 Table mysqlTable = printServiceDataContext.getDefaultSchema().getTableByName("AreaList");
		logger.debug("Table Object for AreaList Table2 : " + mysqlTable);
		printServiceDataContext.executeUpdate(new InsertInto(mysqlTable).value("AreaId", 472).value("AreaType", "stagePic472")
				.value("AreaName", "testBeanPass472"));

	}
	
	/**
	 * This method is used to get data context with new request context of other feature
	 * @param meshConfigUtil : MeshConfigurationUtil class object
	 * @param reqContext : request context object
	 * @param featureDataContext : Feature DataContext object
	 * @param exchange : camel exchange object
	 * @throws Exception
	 */
	private void getRefDataContextByRequestContext(FeatureDataContext featureDataContext, Exchange exchange) throws Exception {
		logger.debug(".getRefDataContextByRequestContext method of MetaModelFeatureDataContext");
		CamelContext context = exchange.getContext();
		DataContext printServiceReferenceDataContext =getReferenceFeatureDataContext("print", "printservice", "printnode", "1.0", "mysqlDB",exchange);
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		RequestContext reqContext = new RequestContext(meshHeader.getTenant(),meshHeader.getSite(),"print", "printservice", "printnode", "1.0");
		boolean bool =compareDataContext(reqContext, printServiceReferenceDataContext, featureDataContext);
		if (bool) {
			logger.debug("db name : " + printServiceReferenceDataContext.getDbBeanRefName().trim());
			DataSource printServiceRefDataSource = (DataSource) context.getRegistry()
					.lookupByName(printServiceReferenceDataContext.getDbBeanRefName().trim());
			setDataSource(printServiceRefDataSource);
			JdbcDataContext mysqlDataContext = getLocalDataContext(exchange);
			Table mysqlTable = mysqlDataContext.getDefaultSchema().getTableByName("AreaList");
			logger.debug("Table Object for AreaList mysqlTable : " + mysqlTable);
			mysqlDataContext.executeUpdate(new InsertInto(mysqlTable).value("AreaId", 47)
					.value("AreaType", "stagePic47").value("AreaName", "testBeanPass47"));
		} else {
			logger.debug("we need to create composite view");
		}

	}

}
