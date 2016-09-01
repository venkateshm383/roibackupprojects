package com.getusroi.eventframework.abstractbean;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.metamodel.CompositeDataContext;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.schema.TableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.config.RequestContext;
import com.getusroi.config.persistence.ConfigurationTreeNode;
import com.getusroi.config.persistence.ITenantConfigTreeService;
import com.getusroi.config.persistence.UndefinedPrimaryVendorForFeature;
import com.getusroi.config.persistence.impl.TenantConfigTreeServiceImpl;
import com.getusroi.datacontext.config.DataContextConfigurationException;
import com.getusroi.datacontext.config.DataContextConfigurationUnit;
import com.getusroi.datacontext.config.IDataContextConfigurationService;
import com.getusroi.datacontext.config.impl.DataContextConfigurationService;
import com.getusroi.datacontext.jaxb.FeatureDataContext;
import com.getusroi.datacontext.jaxb.RefDataContext;
import com.getusroi.datacontext.jaxb.RefDataContexts;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;

public abstract class AbstractMetaModelBean extends AbstractROICamelBean{
	protected static final Logger logger = LoggerFactory.getLogger(AbstractROICamelBean.class);
	private DataSource dataSource=null;	
	private List<DataSource> dataSourceList;
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	
	public void setDataSourceList(List<DataSource> dataSourceList) {
		this.dataSourceList = dataSourceList;
	}

	protected JdbcDataContext getLocalDataContext(Exchange exchange) throws Exception{
		logger.debug(".getMetaModelJdbcDataContext method of AbstractMetaModelBean");
		Connection con=getConnection(dataSource, exchange);
		logger.debug("AbstractMetaModelBean.getMetaModelJdbcDataContext got the connection : "+con);
		JdbcDataContext metamodelJdbcContext=new JdbcDataContext(con);
		metamodelJdbcContext.setIsInTransaction(true);
		return metamodelJdbcContext;
	}	
	
	protected DataContext getCompositeMetaModelDataContext(Exchange exchange) throws Exception{
		logger.debug(".getCompositeMetaModelDataContext method of AbstractMetaModelBean");
		Collection<DataContext> coll=new ArrayList<>();
		logger.debug("DataSource List : "+dataSourceList);
		if(dataSourceList!=null && !(dataSourceList.isEmpty())){
			for(DataSource datasource:dataSourceList){
				Connection connection=getConnection(datasource, exchange);
				logger.debug("AbstractMetaModelBean.getCompositeMetaModelDataContext got the connection : "+connection);
				DataContext metamodelJdbcContext=new JdbcDataContext(connection);
				coll.add(metamodelJdbcContext);
			}
		}		
		DataContext dataContextComposite=new CompositeDataContext(coll);
		return dataContextComposite;
	}
	
	
	protected JdbcDataContext getMetaModelJdbcDataContext( TableType[] tableTypes, String catalogName,Exchange exchange) throws Exception{
		//#TODO Support is not yet Provided
		return null;
	}
	

	@Override
	protected abstract void processBean(Exchange exch) throws Exception;
	
	/**
	 * This method is used to get com.getusroi.datacontext.jaxb.DataContext Object 
	 * @param requestContext : RequestCOntext Object of a feature
	 * @return com.getusroi.datacontext.jaxb.FeatureDataContext
	 * @throws DataContextConfigurationException
	 */
	protected com.getusroi.datacontext.jaxb.DataContext getFeatureDataContext(Exchange exchange)
			throws DataContextConfigurationException {
		logger.debug(".getThisDataContext method of AbstractMetaModelBean");
		MeshHeader meshheader=(MeshHeader)exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		RequestContext requestContext=meshheader.getRequestContext();
		com.getusroi.datacontext.jaxb.DataContext datacontext=getDataContextObjectOfFeature(requestContext);
		return datacontext;
	}
	
	/**
	 * This method is used to get com.getusroi.datacontext.jaxb.DataContext Object 
	 * @param requestContext : RequestCOntext Object of a feature
	 * @return com.getusroi.datacontext.jaxb.FeatureDataContext
	 * @throws DataContextConfigurationException
	 */
	protected com.getusroi.datacontext.jaxb.DataContext getFeatureDataContext(String dbname,Exchange exchange)
			throws DataContextConfigurationException {
		logger.debug(".getThisDataContext method of AbstractMetaModelBean");
		MeshHeader meshheader=(MeshHeader)exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		RequestContext requestContext=meshheader.getRequestContext();
		com.getusroi.datacontext.jaxb.DataContext datacontext=getDataContextObjectOfFeature(requestContext,dbname);
		return datacontext;
	}
	
	
	/**
	 * This method is used to get com.getusroi.datacontext.jaxb.FeatureDataContext Object 
	 * @param requestContext : RequestCOntext Object of a feature
	 * @return com.getusroi.datacontext.jaxb.FeatureDataContext
	 * @throws DataContextConfigurationException
	 */
	protected FeatureDataContext getFeatureDataContextObject(RequestContext requestContext)
			throws DataContextConfigurationException {
		logger.debug(".getReferenceFeatureDataContext method of AbstractMetaModelBean");
		IDataContextConfigurationService dataContextConfigService = new DataContextConfigurationService();
		DataContextConfigurationUnit dataContextConfigurationUnit = dataContextConfigService
				.getDataContextConfiguration(requestContext);
		FeatureDataContext featureDataContext = (FeatureDataContext) dataContextConfigurationUnit.getConfigData();
		return featureDataContext;
	}
	
	/**
	 * This method is used to compare datacontext for this feature and other feature.
	 * If same then create Apache metamodel datacontext else create composite datacontext
	 * @param requestContext : Feature Request Context Object
	 * @param featureDataContext : FeatureDataContext Object of current feature
	 * @param refFeatureDataContext : FeatureDataContext Object of reference feature
	 * @return
	 */
	protected boolean compareDataContext(RequestContext requestContext, com.getusroi.datacontext.jaxb.DataContext featureDataContext,
			FeatureDataContext refFeatureDataContext) {
		logger.debug(".compareDataContext method of AbstractMetaModelBean");
		boolean flag = false;
		String dbBeanRefName = featureDataContext.getDbBeanRefName();
		String dbType = featureDataContext.getDbType();
		String dbHost = featureDataContext.getDbHost();
		String dbPort = featureDataContext.getDbPort();
		String dbSchema = featureDataContext.getDbSchema();
		List<RefDataContexts> refDataContextsList = refFeatureDataContext.getRefDataContexts();
		for (RefDataContexts refDataContexts : refDataContextsList) {
			String featureGroup = refDataContexts.getFeatureGroup();
			String featureName = refDataContexts.getFeatureName();
			if (featureGroup.equalsIgnoreCase(requestContext.getFeatureGroup())
					&& featureName.equalsIgnoreCase(requestContext.getFeatureName())) {
				List<RefDataContext> refDataContextList = refDataContexts.getRefDataContext();
				for (RefDataContext refDataContext : refDataContextList) {
					if (refDataContext.getDbBeanRefName().equalsIgnoreCase(dbBeanRefName)
							&& refDataContext.getDbType().equalsIgnoreCase(dbType)
							&& refDataContext.getDbHost().equalsIgnoreCase(dbHost)
							&& refDataContext.getDbPort().equalsIgnoreCase(dbPort)
							&& refDataContext.getDbSchema().equalsIgnoreCase(dbSchema)) {
						flag = true;
					} else {
						flag = false;
					}
				}
			} // end of if matching fetaureGroup and featureName
		} // end of for(RefDataContexts refDataContexts:refDataContextsList)

		return flag;
	}
	
	/**
	 * This method is used to get the com.getusroi.datacontext.jaxb.DataContext Object based on feature group and feature name
	 * @param featureGroup : Feature group in String
	 * @param feature : Feature in String
	 * @param exchange : Camel Exchange Object
	 * @return com.getusroi.datacontext.jaxb.DataContext Object
	 * @throws Exception 
	 */
	protected com.getusroi.datacontext.jaxb.DataContext getReferenceFeatureDataContext(String featureGroup, String feature, Exchange exchange)
			throws Exception {
		logger.debug(".getDataContextForFeature method of AbstractMetaModelBean");
	
		MeshHeader meshheader=(MeshHeader)exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		ITenantConfigTreeService tenantTreeService = TenantConfigTreeServiceImpl.getTenantConfigTreeServiceImpl();
		ConfigurationTreeNode fgconfigNodeTree = tenantTreeService.getPrimaryVendorForFeature(meshheader.getTenant(), meshheader.getSite(),
				featureGroup, feature);
		String vendorName = fgconfigNodeTree.getNodeName();
		String version = fgconfigNodeTree.getVersion();
		RequestContext requestContext = new RequestContext(meshheader.getTenant(), meshheader.getSite(), featureGroup, feature, vendorName, version);
		com.getusroi.datacontext.jaxb.DataContext dataContext = getDataContextObjectOfFeature(requestContext);		
		return dataContext;
	}
	
	/**
	 * This method is used to get the com.getusroi.datacontext.jaxb.DataContext Object based on feature group,feature name,vendor,version,db name
	 * @param featureGroup : Feature group in String
	 * @param feature : Feature in String
	 * @param vendor : vendor in String
	 * @param version : version in String
	 * @param db name : dbname in String
	 * @param exchange : Camel Exchange Object
	 * @return com.getusroi.datacontext.jaxb.DataContext Object
	 * @throws Exception 
	 */
	protected com.getusroi.datacontext.jaxb.DataContext getReferenceFeatureDataContext(String featureGroup, String feature,String vendor,String version,String dbName, Exchange exchange)
			throws Exception {
		logger.debug(".getDataContextForFeature method of AbstractMetaModelBean");
		MeshHeader meshheader=(MeshHeader)exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		RequestContext requestContext = new RequestContext(meshheader.getTenant(), meshheader.getSite(), featureGroup, feature, vendor, version);
		com.getusroi.datacontext.jaxb.DataContext dataContext = getDataContextObjectOfFeature(requestContext,dbName);		
		return dataContext;
	}
	
	/**
	 * This method is used to get the com.getusroi.datacontext.jaxb.DataContext of reference feature 
	 * #TODO,till now its not decided how many data context a feature can have,in xml we have provided a support for multiple 
	 * but in code implementation we are taking it as only one.
	 * @param requestContext : Request Context Object of current feature
	 * @return com.getusroi.datacontext.jaxb.DataContext
	 * @throws DataContextConfigurationException
	 */
	private com.getusroi.datacontext.jaxb.DataContext getDataContextObjectOfFeature(RequestContext requestContext)
			throws DataContextConfigurationException {
		logger.debug(".getDataContextForFeature method of AbstractMetaModelBean");
		com.getusroi.datacontext.jaxb.DataContext dataContextforFeature = null;
		IDataContextConfigurationService dataContextConfigService = new DataContextConfigurationService();
		DataContextConfigurationUnit dataContextConfigurationUnit = dataContextConfigService
				.getDataContextConfiguration(requestContext);
		FeatureDataContext featureDataContext = (FeatureDataContext) dataContextConfigurationUnit.getConfigData();
		List<com.getusroi.datacontext.jaxb.DataContext> dataContextList = featureDataContext.getDataContexts().getDataContext();
		for (com.getusroi.datacontext.jaxb.DataContext dataContext : dataContextList) {
			dataContextforFeature = dataContext;
		}
		return dataContextforFeature;
	}

	/**
	 * This method is used to get the com.getusroi.datacontext.jaxb.DataContext of reference feature by passing db reference name 
	 * #TODO,till now its not decided how many data context a feature can have,in xml we have provided a support for multiple 
	 * but in code implementation we are taking it as only one.
	 * @param requestContext : Request Context Object of current feature
	 * @return com.getusroi.datacontext.jaxb.DataContext
	 * @throws DataContextConfigurationException
	 */
	private com.getusroi.datacontext.jaxb.DataContext getDataContextObjectOfFeature(RequestContext requestContext, String name)
			throws DataContextConfigurationException {
		logger.debug(".getDataContextForFeature method of AbstractMetaModelBean");
		IDataContextConfigurationService dataContextConfigService = new DataContextConfigurationService();
		DataContextConfigurationUnit dataContextConfigurationUnit = dataContextConfigService
				.getDataContextConfiguration(requestContext);
		FeatureDataContext featureDataContext = (FeatureDataContext) dataContextConfigurationUnit.getConfigData();
		List<com.getusroi.datacontext.jaxb.DataContext> dataContextList = featureDataContext.getDataContexts().getDataContext();
		for (com.getusroi.datacontext.jaxb.DataContext dataContext : dataContextList) {
			if (dataContext.getDbBeanRefName().equalsIgnoreCase(name)) {
				return dataContext;
			}
		}
		return null;
	}

}
