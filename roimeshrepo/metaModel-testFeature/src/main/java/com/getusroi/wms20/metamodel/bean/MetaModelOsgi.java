package com.getusroi.wms20.metamodel.bean;

import org.apache.camel.Exchange;
import org.osgi.framework.BundleContext;

import com.getusroi.eventframework.abstractbean.AbstractMetaModelBean;

/**
 * Use this class only when you working on osgi. Uncomment all commentd lines and test
 * @author bizruntime
 *
 */
public class MetaModelOsgi extends AbstractMetaModelBean{

	@Override
	protected void processBean(Exchange exchange) throws Exception {
	/*	logger.debug("processBean of testMetaModelOsgi");
		CamelContext camelContext=exchange.getContext();
		if(camelContext instanceof BlueprintCamelContext){
			logger.debug("camelcontext instance of blueprintCamelContext");
			BlueprintCamelContext bluePrintCamelContext=(BlueprintCamelContext)camelContext;
			logger.debug("blueprint camelContext : "+bluePrintCamelContext);
			BundleContext bundleContext=bluePrintCamelContext.getBundleContext();
			logger.debug("bundleContext : "+bundleContext);
			ServiceReference[] srs=bundleContext.getServiceReferences(DataSource.class.getName(), "(datasource.name=roiMYSQL)");
			DataSource datasourceOsgi=(DataSource)bundleContext.getService(srs[0]);
			logger.debug("source form bundle context : "+datasourceOsgi);
			Connection conn=getConnection(datasourceOsgi,exchange);
			logger.debug("connection object using jndi name TestBeanPass : "+conn);
			JdbcDataContext datacontext=new JdbcDataContext(conn);
			datacontext.setIsInTransaction(true);
			logger.debug("datacontext1 object using jndi name TestBeanPass : "+datacontext);
			//JdbcDataContext datacontext=getMetaModelJdbcDataContext(exchange);
			Table table=datacontext.getDefaultSchema().getTableByName("AreaList");	
			logger.debug("Table Object for AreaList Table : "+table);
			datacontext.executeUpdate(new InsertInto(table).value("AreaId",18).value("AreaType","stagePic18").value("AreaName", "testBeanPass18"));
			insertByConfigLookup(bundleContext,exchange);
		}else if(camelContext instanceof SpringCamelContext){
			logger.debug("camelcontext instance of springCamelContext");
			DataSource datasource=(DataSource)exchange.getContext().getRegistry().lookupByName("roiXADataSource");
			logger.debug("dataSource by lookup : "+datasource);
			logger.debug("datasource object using jndi name TestBeanPass : "+datasource);
		}*/
		
	}
	
	public void updateData(Exchange exchange)throws Exception{
		/*logger.debug(".updateData of TestMetaModelOsgi");
		CamelContext camelContext=exchange.getContext();
		if(camelContext instanceof BlueprintCamelContext){
			logger.debug("camelcontext instance of blueprintCamelContext");
			BlueprintCamelContext bluePrintCamelContext=(BlueprintCamelContext)camelContext;
			logger.debug("blueprint camelContext : "+bluePrintCamelContext);
			BundleContext bundleContext=bluePrintCamelContext.getBundleContext();
			logger.debug("bundleContext : "+bundleContext);
			ServiceReference[] srs=bundleContext.getServiceReferences(DataSource.class.getName(), "(datasource.name=roiMYSQL)");
			DataSource datasourceOsgi=(DataSource)bundleContext.getService(srs[0]);
			logger.debug("source form bundle context : "+datasourceOsgi);
			Connection conn=getConnection(datasourceOsgi,exchange);
			logger.debug("connection object using jndi name TestBeanPass : "+conn);
			JdbcDataContext datacontext=new JdbcDataContext(conn);
			datacontext.setIsInTransaction(true);
			logger.debug("datacontext1 object using jndi name TestBeanPass : "+datacontext);
			//JdbcDataContext datacontext=getMetaModelJdbcDataContext(exchange);
			Table table=datacontext.getDefaultSchema().getTableByName("AreaList");	
			logger.debug("Table Object for AreaList Table : "+table);
			datacontext.executeUpdate(new Update(table).value("AreaType","stagePic18Update").value("AreaName", "testBeanPass18Update").where("AreaId").eq(18));
		}*/
		
	}
	
	public void insertByConfigLookup(BundleContext bundleContext,Exchange exchange) throws Exception{
		/*logger.debug(".insertByConfigLookup of TestMetaModelOsgi");
		MeshHeader meshHeader=(MeshHeader)exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		RequestContext requestContext=meshHeader.getRequestContext();
		logger.debug("requestContext in Test MetaModel : "+requestContext);
		MeshConfigurationUtil meshConfigUtil=new MeshConfigurationUtil();
		DataContext dataContext=meshConfigUtil.getDataContextForFeature(requestContext,"roiMYSQL");
		String expression="(datasource.name="+dataContext.getDbBeanRefName().trim()+")";
		logger.debug("filer expression : "+expression);
		ServiceReference[] srs=bundleContext.getServiceReferences(DataSource.class.getName(),expression);
		DataSource datasourceOsgi=(DataSource)bundleContext.getService(srs[0]);
		logger.debug("source form bundle context : "+datasourceOsgi);
		Connection conn=getConnection(datasourceOsgi,exchange);
		logger.debug("connection object using jndi name TestBeanPass : "+conn);
		JdbcDataContext datacontext=new JdbcDataContext(conn);
		datacontext.setIsInTransaction(true);
		logger.debug("datacontext1 object using jndi name TestBeanPass : "+datacontext);
		//JdbcDataContext datacontext=getMetaModelJdbcDataContext(exchange);
		Table table=datacontext.getDefaultSchema().getTableByName("AreaList");	
		logger.debug("Table Object for AreaList Table : "+table);
		datacontext.executeUpdate(new InsertInto(table).value("AreaId",19).value("AreaType","stagePic19").value("AreaName", "testBeanPass19"));
		
		DataContext dataContextPrint=meshConfigUtil.getDataContextForFeature(meshHeader.getTenant(), meshHeader.getSite(), "print", "printservice");
		String expForPrint="(datasource.name="+dataContextPrint.getDbBeanRefName().trim()+")";
		logger.debug("filer expForPrint : "+expForPrint);
		ServiceReference[] srs1=bundleContext.getServiceReferences(DataSource.class.getName(),expForPrint);
		DataSource datasourceOsgi1=(DataSource)bundleContext.getService(srs1[0]);
		logger.debug("source1 form bundle context : "+datasourceOsgi1);
		Connection conn1=getConnection(datasourceOsgi1,exchange);
		logger.debug("connection1 object using jndi name TestBeanPass : "+conn1);
		JdbcDataContext datacontext1=new JdbcDataContext(conn1);
		datacontext1.setIsInTransaction(true);
		logger.debug("datacontext2 object using jndi name TestBeanPass : "+datacontext1);
		//JdbcDataContext datacontext=getMetaModelJdbcDataContext(exchange);
		Table table1=datacontext1.getDefaultSchema().getTableByName("AreaList");	
		logger.debug("Table1 Object for AreaList Table : "+table1);
		datacontext1.executeUpdate(new Update(table1).value("AreaType","stagePic19updated").value("AreaName", "testBeanPass19updated").where("AreaId").eq(19));
		*/
	}//end of method

}
