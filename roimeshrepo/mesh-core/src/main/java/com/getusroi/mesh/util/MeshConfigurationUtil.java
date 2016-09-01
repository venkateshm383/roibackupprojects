package com.getusroi.mesh.util;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import com.getusroi.datacontext.jaxb.DataContext;
import com.getusroi.datacontext.jaxb.FeatureDataContext;
import com.getusroi.datacontext.jaxb.RefDataContext;
import com.getusroi.datacontext.jaxb.RefDataContexts;
import com.getusroi.eventframework.config.EventRequestContext;
import com.getusroi.integrationfwk.config.IIntegrationPipeLineConfigurationService;
import com.getusroi.integrationfwk.config.IntegrationPipelineConfigException;
import com.getusroi.integrationfwk.config.IntegrationPipelineConfigUnit;
import com.getusroi.integrationfwk.config.impl.IntegrationPipelineConfigurationService;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.mesh.generic.MeshGenericConstant;
import com.getusroi.permastore.config.IPermaStoreConfigurationService;
import com.getusroi.permastore.config.PermaStoreConfigRequestException;
import com.getusroi.permastore.config.impl.PermaStoreConfigurationService;
import com.getusroi.policy.config.IPolicyConfigurationService;
import com.getusroi.policy.config.PolicyConfigurationException;
import com.getusroi.policy.config.PolicyConfigurationUnit;
import com.getusroi.policy.config.PolicyEvaluationConfigurationUnit;
import com.getusroi.policy.config.PolicyRequestContext;
import com.getusroi.policy.config.impl.PolicyConfigurationService;

public class MeshConfigurationUtil {

	final Logger logger = LoggerFactory.getLogger(MeshConfigurationUtil.class);

	public void getPermastoreConfiguration(String configName, Exchange exchange)
			throws PermaStoreConfigRequestException {
		logger.debug(".getPermastoreConfigurationFromCamel() of MeshConfigurationUtil");
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		// create the policy request context
		String tenant = meshHeader.getTenant();
		String site = meshHeader.getSite();
		String featureGroup = meshHeader.getFeatureGroup();
		String featureName = meshHeader.getFeatureName();
		String vendor = meshHeader.getVendor();
		String version = meshHeader.getVersion();
		logger.debug("vendor : " + vendor + ", version : " + version);
		RequestContext requestContext = new RequestContext(tenant, site, featureGroup, featureName, vendor, version);
		IPermaStoreConfigurationService permaConfigService = new PermaStoreConfigurationService();
		Object obj = permaConfigService.getPermaStoreCachedObject(requestContext, configName);
		Map<String, Object> permaCacheObjectInMap = meshHeader.getPermadata();
		permaCacheObjectInMap.put(configName, obj);

	}

	public void getPolicyConfiguration(String configName, Exchange exchange) throws PolicyConfigurationException {
		logger.debug(".getPolicyConfigurationFromCamel() of MeshConfigurationUtil");
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		// create the policy request context
		String tenant = meshHeader.getTenant();
		String site = meshHeader.getSite();
		String featureGroup = meshHeader.getFeatureGroup();
		String featureName = meshHeader.getFeatureName();
		String vendor = meshHeader.getVendor();
		String version = meshHeader.getVersion();
		logger.debug("vendor : " + vendor + ", version : " + version);
		Map<String, Object> policyReqVariableInMap = meshHeader.getPolicydata();

		PolicyRequestContext requestContext = new PolicyRequestContext(tenant, site, featureGroup, featureName, vendor,
				version);
		IPolicyConfigurationService policyConfigService = new PolicyConfigurationService();
		PolicyConfigurationUnit policyConfigUnit = policyConfigService.getPolicyConfigurationUnit(requestContext,
				configName);
		List<PolicyEvaluationConfigurationUnit> policyEvalConfigUnitList = policyConfigUnit.getEvaluationUnitList();
		for (PolicyEvaluationConfigurationUnit policyEvalConfigUnit : policyEvalConfigUnitList) {
			List<String> reqVarList = policyEvalConfigUnit.getReqVarList();
			for (String reqVar : reqVarList) {
				Object reqValue = getRequestVariableValueFromExchange(exchange, reqVar);
				policyReqVariableInMap.put("$" + reqVar, reqValue);
			}

		} // end of for on list of PolicyEvaluationConfigurationUnit

	}

	/**
	 * This method is used to initialize EventRequestContext object with data
	 * store in meshHeader
	 * 
	 * @param exchange
	 *            : Exchange object to getMeshHeader
	 */
	public EventRequestContext initializeEventRequestContext(Exchange exchange) {
		logger.debug(".initializeEventRequestContext method");
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		// initialize EventRequestContext with data in MeshHeader
		EventRequestContext eventRequestContext = new EventRequestContext();
		eventRequestContext.setTenant(meshHeader.getTenant());
		eventRequestContext.setServicetype(meshHeader.getServicetype());
		eventRequestContext.setFeatureGroup(meshHeader.getFeatureGroup());
		eventRequestContext.setFeatureName(meshHeader.getFeatureName());
		eventRequestContext.setHazelcastTransactionalContext(meshHeader.getHazelcastTransactionalContext());
		eventRequestContext.setRequestUUID(meshHeader.getRequestUUID());
		return eventRequestContext;
	}
	
	
	
	/**
	 * This method is used to compare datacontext for this feature and other feature.
	 * If same then create Apache metamodel datacontext else create composite datacontext
	 * @param requestContext : Feature Request Context Object
	 * @param featureDataContext : FeatureDataContext Object of current feature
	 * @param refFeatureDataContext : FeatureDataContext Object of reference feature
	 * @return
	 */
	public boolean compareDataContext(RequestContext requestContext, DataContext featureDataContext,
			FeatureDataContext refFeatureDataContext) {
		logger.debug(".compareDataContext method of MeshConfigUtil");
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
	 * method to get IntegrationPipeContext
	 * 
	 * @param exchange
	 */
	public void getIntegrationPipeContext(Exchange exchange) {
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> body = meshHeader.getPipeContext();
		String exchangeBody = (String) body.get(MeshGenericConstant.PIPE_CONTEXT_KEY);
		exchange.getIn().setBody(exchangeBody);
	}// end of method

	/**
	 * method to set IntegrationPipeContext
	 * 
	 */
	public void setIntegrationPipeContext(String str, Exchange exchange) {
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> body = meshHeader.getPipeContext();
		String exchangeBody = str;
		body.put(MeshGenericConstant.PIPE_CONTEXT_KEY, exchangeBody);
	}

	/**
	 * Getting the oldmeshHeader key's value from the existing MeshHeader and
	 * setting it into the exchange's mesh header
	 * 
	 * @param exchange
	 */
	public void getOldMeshHeader(Exchange exchange) {
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> oldMesh = meshHeader.getOriginalMeshHeader();
		exchange.getIn().setHeader(MeshHeaderConstant.MESH_HEADER_KEY,
				oldMesh.get(MeshHeaderConstant.ORIGINAL_MESH_HEADER_KEY));
		// String exchangeBody = (String)
		// body.get(MeshGenericConstant.PIPE_CONTEXT_KEY);
	}
	
	/**
	 * Setting the Mesh header Object inside MeshHeader itself in a key.
	 * 
	 * @param exchange
	 */
	public void setOldMeshHeader(Exchange exchange) {
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> oldMesh = meshHeader.getOriginalMeshHeader();
		oldMesh.put(MeshHeaderConstant.ORIGINAL_MESH_HEADER_KEY, meshHeader);
	}

	/**
	 * the newly added integrationPipeline configuration object, retrieved from
	 * the mesh header
	 * 
	 * @param configName
	 * @param exchange
	 * @throws IntegrationPipelineConfigException
	 */
	public void getIntegrationPipelineConfiguration(String configName, RequestContext reqcontext, MeshHeader meshHeader,
			Exchange exchange) throws IntegrationPipelineConfigException {

		IIntegrationPipeLineConfigurationService pipeLineConfigurationService = new IntegrationPipelineConfigurationService();
		IntegrationPipelineConfigUnit pipelineConfigUnit = pipeLineConfigurationService
				.getIntegrationPipeConfiguration(reqcontext, configName);
		logger.debug(".inIPipelineMeshConfigUtil.." + pipelineConfigUnit + "ConfigurationName in meshConfigUtil.."
				+ configName);
		//......TestSnipet....
		exchange.getIn().setHeader("pipeActivityKey", pipelineConfigUnit);
		//.......TestSniper-Ends......
		
		
		Map<String, Object> integrationCahedObject = meshHeader.getIntegrationpipelineData();
		integrationCahedObject.put(MeshHeaderConstant.PIPELINE_CONFIG_KEY, pipelineConfigUnit);
		logger.debug(".inMeshUtil cachedObject..Check before putting" + integrationCahedObject);
	}// ..end of the method


	
	private Object getRequestVariableValueFromExchange(Exchange exchange, String reqVar) {
		logger.debug(".getRequestVariableValueFromExchange() of MeshConfigurationUtil");
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);

		// checking in exchange header
		logger.debug("checking in exchange header using " + reqVar);
		Object reqvalue = (Object) exchange.getIn().getHeader(reqVar);
		if (reqvalue == null) {
			logger.debug("checking in exchange header using " + "$" + reqVar);
			reqvalue = (Object) exchange.getIn().getHeader("$" + reqVar);
		}

		// checking into meshHeader data value
		if (reqvalue == null) {
			logger.debug(reqVar + " not found in exchange so checking in meshHeader data ");
			Map<String, Object> geniricdata = meshHeader.getGenricdata();
			try {
				JSONArray jsonArray = (JSONArray) geniricdata.get(MeshHeaderConstant.DATA_KEY);
				int jsonLen = jsonArray.length();
				logger.debug("data's in json array for key data is : " + jsonLen);
				for (int i = 0; i < jsonLen; i++) {
					JSONObject jobj = (JSONObject) jsonArray.get(i);
					logger.debug("checking in  meshheader using " + reqVar);
					reqvalue = (Object) jobj.get(reqVar);
					if (reqvalue == null) {
						logger.debug("checking in  meshheader using " + "$" + reqVar);
						reqvalue = (Object) jobj.get("$" + reqVar);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// checking in exchange.property
		if (reqvalue == null) {
			logger.debug(reqVar + " not found in exchange so checking in exchange property data ");
			reqvalue = (String) exchange.getProperty(reqVar);
			logger.debug("checking in  meshheader using " + reqVar);
			if (reqvalue == null) {
				logger.debug("checking in  meshheader using " + "$" + reqVar);
				reqvalue = (String) exchange.getProperty("$" + reqVar);
			}

		}

		return reqvalue;
	}// end of method

}
