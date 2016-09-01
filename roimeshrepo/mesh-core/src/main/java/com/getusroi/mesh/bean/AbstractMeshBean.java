package com.getusroi.mesh.bean;


import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.mesh.util.MeshConfigurationUtil;
import com.getusroi.permastore.config.PermaStoreConfigRequestException;
import com.getusroi.policy.config.PolicyConfigurationException;

public abstract  class AbstractMeshBean {
	final Logger logger = LoggerFactory.getLogger(AbstractMeshBean.class);


	
	public void callGetPermaStoreConfigFromUtil(Exchange exchange,String configName) throws PermaStoreConfigRequestException{
		logger.debug(".callGetPermaStoreConfigFromUtil() of  AbstractMeshBean");
		MeshConfigurationUtil meshConfigUtil=new MeshConfigurationUtil();
		meshConfigUtil.getPermastoreConfiguration(configName,exchange);
		
	}
	
	public void callGetPolicyConfigFromUtil(Exchange exchange,String configName) throws PermaStoreConfigRequestException, PolicyConfigurationException{
		logger.debug(".callGetPermaStoreConfigFromUtil() of  AbstractMeshBean");
		MeshConfigurationUtil meshConfigUtil=new MeshConfigurationUtil();
		meshConfigUtil.getPolicyConfiguration(configName,exchange);
		
	}

	
}
