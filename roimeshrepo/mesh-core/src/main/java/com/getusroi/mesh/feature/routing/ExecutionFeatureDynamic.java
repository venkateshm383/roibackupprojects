package com.getusroi.mesh.feature.routing;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.config.RequestContext;
import com.getusroi.config.persistence.ConfigurationTreeNode;
import com.getusroi.config.persistence.ITenantConfigTreeService;
import com.getusroi.config.persistence.InvalidNodeTreeException;
import com.getusroi.config.persistence.UndefinedPrimaryVendorForFeature;
import com.getusroi.config.persistence.impl.TenantConfigTreeServiceImpl;
import com.getusroi.feature.config.FeatureConfigRequestContext;
import com.getusroi.feature.config.FeatureConfigRequestException;
import com.getusroi.feature.config.FeatureConfigurationException;
import com.getusroi.feature.config.FeatureConfigurationUnit;
import com.getusroi.feature.config.impl.FeatureConfigurationService;
import com.getusroi.feature.jaxb.Feature;
import com.getusroi.feature.jaxb.Service;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.mesh.feature.FeatureErrorPropertyConstant;
import com.getusroi.mesh.generic.UnableToLoadPropertiesException;

public class ExecutionFeatureDynamic {
	private static final Logger logger = LoggerFactory.getLogger(ExecutionFeatureDynamic.class);
	
	private static Properties errorcodeprop=null;
	private static Properties errormessageprop =null;
	
	
	
	static{
		logger.debug("static block");
		try {
			errorcodeprop=loadingPropertiesFile(FeatureErrorPropertyConstant.ERROR_CODE_FILE);
			errormessageprop=loadingPropertiesFile(FeatureErrorPropertyConstant.ERROR_MESSAGE_FILE);
		} catch (UnableToLoadPropertiesException e) {
			logger.error("Uable to read error code and error message property file");
		}
	}

	/**
	 * This method is for dynamically routing to Implementation route
	 * 
	 * @param exchange
	 *            : Exchange Object
	 * @return : String
	 * 
	 * @throws UnableToLoadPropertiesException
	 * @throws DynamicallyImplRoutingFailedException 
	 *
	 */
	public void route(Exchange exchange) throws UnableToLoadPropertiesException, DynamicallyImplRoutingFailedException{

		logger.debug("inside route() of ExecutionFeatureDynamic");
		//logger.error("entrying into execution feature dynamic is : "+System.currentTimeMillis());	
		//logger.error("before Loading error code :: " + System.currentTimeMillis());
		// Getting the status code from Property file
		//Properties errorcodeprop = loadingPropertiesFile(FeatureErrorPropertyConstant.ERROR_CODE_FILE);
		
		//logger.error("after Loading error code :: " + System.currentTimeMillis());


		// Getting the status code message from Property file
		//Properties errormessageprop = loadingPropertiesFile(FeatureErrorPropertyConstant.ERROR_MESSAGE_FILE);
		//logger.error("after Loading error messgae :: " + System.currentTimeMillis());

		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		//logger.error("time taken to get mesh herder :: " + System.currentTimeMillis());
		FeatureConfigRequestContext featureRequestContext =null;
		String servicetype = meshHeader.getServicetype();
		String tenant = meshHeader.getTenant();
		String siteId = meshHeader.getSite();
		String featureGroup = meshHeader.getFeatureGroup();
		String featureName = meshHeader.getFeatureName();	

		logger.debug("tenant : " + tenant + ", featuregroup key  : " + featureGroup + ", feature : " + featureName
				+ ",site id : " + siteId + ", service name : " + servicetype);
		//logger.error("time taken to set all data from mesh herder :: " + System.currentTimeMillis());

		ITenantConfigTreeService tenantTreeService= TenantConfigTreeServiceImpl.getTenantConfigTreeServiceImpl();
		ConfigurationTreeNode vendorTreeNode;
		try {
			vendorTreeNode = tenantTreeService.getPrimaryVendorForFeature(tenant,siteId,featureGroup,featureName);
			logger.debug("vendor tree node : "+vendorTreeNode);
			//logger.error("time taken to get feture vendor set as primary " + System.currentTimeMillis());

			//providing vendor and version support
			String vendor=vendorTreeNode.getNodeName();
			String version=vendorTreeNode.getVersion();
			//this will be removed later
			meshHeader.setVendor(vendor);
			meshHeader.setVersion(version);		
			//logger.error("time taken to set vendor and version in mesh header " + System.currentTimeMillis());
			//store into requestContext 
			RequestContext reqcontext=new RequestContext(tenant,siteId,featureGroup,featureName,vendor,version);
			meshHeader.setRequestContext(reqcontext);
		//	logger.error("time taken to set req context in mesh header " + System.currentTimeMillis());

			// create a feature RequestCOntext
			 featureRequestContext = new FeatureConfigRequestContext(tenant, siteId,
					featureGroup, featureName,vendor,version);

			FeatureConfigurationUnit featureConfigurationUnit;
			try {
				featureConfigurationUnit = getFeatureConfigurationUnit(featureRequestContext,
						featureName);
				logger.debug("feature config unit : "+featureConfigurationUnit);
				if(featureConfigurationUnit !=null){
				String routeToRedirect = routeToRedirect(featureConfigurationUnit, meshHeader, errorcodeprop, errormessageprop,
						exchange);	
				if(routeToRedirect != null && !(routeToRedirect.isEmpty())){
					exchange.getIn().setHeader("implroute", routeToRedirect.trim());
				}else{
					throw new DynamicallyImplRoutingFailedException("No implementation route name is configured for the service in feature  : "+featureRequestContext);
				}
				
				}else{
					throw new DynamicallyImplRoutingFailedException("No feature is configured with request context : "+featureRequestContext);

				}
			} catch (InvalidNodeTreeException | FeatureConfigRequestException | FeatureConfigurationException e) {
				throw new DynamicallyImplRoutingFailedException("Unable load the configuraion feature with request conetxt : "+featureRequestContext);

			}			
		} catch (UndefinedPrimaryVendorForFeature e) {
			throw new DynamicallyImplRoutingFailedException("Unable to route to Implementation route because no feature with request conetxt : "+featureRequestContext+" is marked as primary");
		}
		
		//logger.error("exiting into execution feature dynamic is : "+System.currentTimeMillis());

	}
	
	/**
	 * This method is to load the properties file from the classpath
	 * @param filetoload : name of the file to be loaded
	 * @return Properties Object
	 * @throws UnableToLoadPropertiesException
	 */
	private static Properties loadingPropertiesFile(String filetoload) throws UnableToLoadPropertiesException {
		logger.debug("inside loadingPropertiesFile method of ExecutionFeatureDynamic Bean");
		Properties prop = new Properties();
		InputStream input1 = ExecutionFeatureDynamic.class.getClassLoader().getResourceAsStream(filetoload);
		try {
			prop.load(input1);
		} catch (IOException e) {
			throw new UnableToLoadPropertiesException(
					"unable to load property file = " + FeatureErrorPropertyConstant.ERROR_CODE_FILE, e);
		}
		return prop;
	}

	/**
	 * This method is used to get the feature configuration unit
	 * @param featureRequestContext : Request context object contain tenant,site,featuregroup and feature name
	 * @param configname : configuration name
	 * @return  FeatureConfigurationUnit Object
	 * @throws InvalidNodeTreeException
	 * @throws FeatureConfigRequestException
	 * @throws FeatureConfigurationException
	 */
	private FeatureConfigurationUnit getFeatureConfigurationUnit(FeatureConfigRequestContext featureRequestContext,
			String configname)
					throws InvalidNodeTreeException, FeatureConfigRequestException, FeatureConfigurationException {
		logger.debug("inside getFeatureConfigurationUnit method of ExecutionFeatureDynamic Bean");
	//	logger.error("before getting feature configuration " + System.currentTimeMillis());
		FeatureConfigurationService featureConfigurationService = new FeatureConfigurationService();
		FeatureConfigurationUnit featureConfigurationUnit = featureConfigurationService
				.getFeatureConfiguration(featureRequestContext, configname);
		//logger.error("after getting feature configuration " + System.currentTimeMillis());

		logger.debug("feature configuration unit in ExecutionFeature Dynamic  bean : " + featureConfigurationUnit);
		
		return featureConfigurationUnit;
	}

	/**
	 * This method is to find the route for the implementation route
	 * @param featureConfigurationUnit : FeatureCOnfigurationUnit  Object
	 * @param meshHeader : meshHeader Object
	 * @param errorcodeprop : Properties file for error code
	 * @param errormessageprop : Properties file for error message
	 * @param exchange : exchange to set body with error code and error message
	 * @return String route to Implementation
	 * @throws DynamicallyImplRoutingFailedException 
	 */
	private String routeToRedirect(FeatureConfigurationUnit featureConfigurationUnit, MeshHeader meshHeader,Properties errorcodeprop, Properties errormessageprop, Exchange exchange) throws DynamicallyImplRoutingFailedException {
		logger.debug("inside routeToRedirect1 method of ExecutionFeatureDynamic Bean");
		//logger.error("before getting feature service impl route  " + System.currentTimeMillis());
		String implRouteEndpoint = null;
		boolean status = false;		
		if (featureConfigurationUnit.getIsEnabled()) {
				status = true;
		}		
		if (status) {
			Feature feature = (Feature) featureConfigurationUnit.getConfigData();
			List<Service> serviceList = feature.getService();
			logger.debug("no of feature service in list : "+serviceList.size());
			for (Service service : serviceList) {
				if (service.getName().equalsIgnoreCase(meshHeader.getServicetype().trim())) {
					logger.debug("service name from cache : "+service.getName()+", and service store in mesh header : "+meshHeader.getServicetype().trim());
					if (service.isEnabled() == true) {
						implRouteEndpoint=setImplEndPointForService(service,meshHeader,exchange,errorcodeprop,errormessageprop);
						if(implRouteEndpoint != null && !(implRouteEndpoint.isEmpty())){
							return implRouteEndpoint.trim();
						}else{
							throw new DynamicallyImplRoutingFailedException("No implementation route name is configured service : "+service.getName());
						}
					} else {
						logger.debug("Not-supported : " + FeatureErrorPropertyConstant.UNAVAILABLE_KEY);
						String errorcode = errorcodeprop.getProperty(FeatureErrorPropertyConstant.UNAVAILABLE_KEY);
						logger.debug("errorcode : " + errorcode);
						String errormsg = errormessageprop.getProperty(errorcode);
						logger.debug("error : " + errormsg);
						exchange.getIn().setBody("status code : " + errorcode + " for feature "
								+ meshHeader.getFeatureName() + " \nReason : " + errormsg);
						implRouteEndpoint = "featureServiceNotExist";
					}
				} //end of if(servicename == mesh core service name)
				
			}//end of for (Service service : serviceList)
		}
		//logger.error("after getting feature service impl route " + System.currentTimeMillis());
		return implRouteEndpoint;
		
		
	}// end of if feature status is disabled
	
	/**
	 * This method is used to set the Implementation route from service
	 * @param service : Service Object specify the request for the type of service
	 * @param meshHeader : MeshHeader Object
	 * @param exchange : Camel Exchange
	 * @param errorcodeprop : Get the error code
	 * @param errormessageprop : get the error message for the error code
	 * @return endpoint in String
	 */
	private String setImplEndPointForService(Service service,MeshHeader meshHeader,Exchange exchange,Properties errorcodeprop, Properties errormessageprop){
		logger.debug("inside setImplEndPointForService method of ExecutionFeatureDynamic Bean");
		//logger.error("before setting service  endpoint type"+System.currentTimeMillis());
		String implRouteEndpoint=null;
		String endpointtype = meshHeader.getEndpointType();
		switch (endpointtype) {
		case MeshHeaderConstant.HTTP_JSON_KEY:
			logger.debug("Endpoint type is HTTP JSON type");
			implRouteEndpoint = service.getGenericRestEndpoint().getValue();
			break;
		case MeshHeaderConstant.HTTP_XML_KEY:
			logger.debug("Endpoint type is HTTP XML type");
			implRouteEndpoint = service.getGenericRestEndpoint().getValue();
			break;
		case MeshHeaderConstant.CXF_ENDPOINT_KEY:
			logger.debug("Endpoint type is cxf type");
			implRouteEndpoint = service.getConcreteSoapEndpoint().getValue();
			break;
		default: {
			logger.debug("Not-supported : " + FeatureErrorPropertyConstant.NOT_SUPPORTED_KEY);
			String errorcode = errorcodeprop
					.getProperty(FeatureErrorPropertyConstant.NOT_SUPPORTED_KEY);
			logger.debug("errorcode : " + errorcode);
			String errormsg = errormessageprop.getProperty(errorcode);
			logger.debug("error : " + errormsg);
			exchange.getIn()
					.setBody("status code : " + errorcode + " for feature "
							+ meshHeader.getFeatureName() + "with endpoint type : " + endpointtype
							+ " \nReason : " + errormsg);
			implRouteEndpoint = "direct:noFeature";
		}
			break;
		}// end of switch
		//logger.error("after setting service  endpoint type"+System.currentTimeMillis());

		return implRouteEndpoint.trim();
	}//end of method

}
