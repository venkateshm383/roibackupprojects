package com.getusroi.mesh.integrationpipeline;

import org.apache.camel.Exchange;
import org.slf4j.LoggerFactory;

import com.getusroi.config.RequestContext;
import com.getusroi.config.persistence.ConfigurationTreeNode;
import com.getusroi.config.persistence.ITenantConfigTreeService;
import com.getusroi.config.persistence.UndefinedPrimaryVendorForFeature;
import com.getusroi.config.persistence.impl.TenantConfigTreeServiceImpl;
import com.getusroi.integrationfwk.config.IntegrationPipelineConfigException;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.mesh.util.MeshConfigurationUtil;

public class IntegrationPipelineInitializer {
	final org.slf4j.Logger logger = LoggerFactory.getLogger(IntegrationPipelineInitializer.class);
	
	/*public void testMethod(String name,Exchange exchange) throws InitializingPipelineException{
		logger.debug("Name being loaded: "+name);
		loadPipeConfiguration(name, exchange);
		
		
	}*/

	/**
	 * to initialize a few things, like MeshHeader params, before propagation
	 * 
	 * @param exchange
	 * @throws InitializingPipelineException
	 */
	public void loadPipeConfiguration(String piplineConfigName,Exchange exchange) throws InitializingPipelineException {
		ITenantConfigTreeService tenantTreeService = TenantConfigTreeServiceImpl.getTenantConfigTreeServiceImpl();
		ConfigurationTreeNode vendorTreeNode;
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		MeshConfigurationUtil meshConfigurationUtil = new MeshConfigurationUtil();
		RequestContext reqcontext;
		try {
			vendorTreeNode = tenantTreeService.getPrimaryVendorForFeature(meshHeader.getTenant(), meshHeader.getSite(),
					meshHeader.getFeatureGroup(), meshHeader.getFeatureName());
			String vendor = vendorTreeNode.getNodeName();
			String version = vendorTreeNode.getVersion();
			meshHeader.setVendor(vendor);
			meshHeader.setVersion(version);
			reqcontext = new RequestContext(meshHeader.getTenant(), meshHeader.getSite(), meshHeader.getFeatureGroup(),
					meshHeader.getFeatureName(), vendor, version);
			meshHeader.setRequestContext(reqcontext);
		} catch (UndefinedPrimaryVendorForFeature e1) {
			throw new InitializingPipelineException(
					"Unable to load the VendorTree for initializing PipeConfiguration..", e1);
		}
		logger.debug("To load we are calling: "+piplineConfigName+" "+meshHeader+" "+exchange.getExchangeId());
		try {
			if(piplineConfigName !=null && !(piplineConfigName.isEmpty())){
			meshConfigurationUtil.getIntegrationPipelineConfiguration(piplineConfigName, reqcontext, meshHeader, exchange);
			}else{
				throw new InitializingPipelineException("pipeline configuration name is null");
			}
		} catch (IntegrationPipelineConfigException e) {
			throw new InitializingPipelineException("Unable to load the IntegrationPipeline from cache to meshHeader..",
					e);
		}
	}// ..end of the method
}