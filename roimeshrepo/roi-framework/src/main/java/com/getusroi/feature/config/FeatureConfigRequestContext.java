package com.getusroi.feature.config;

import com.getusroi.config.RequestContext;


public class FeatureConfigRequestContext extends RequestContext{

	public FeatureConfigRequestContext(String tenant, String site, String featureGroup, String featureName,String vendor,String version) {
		super(tenant, site, featureGroup, featureName,vendor,version);
	}
	public FeatureConfigRequestContext(String tenant, String site, String featureGroup, String featureName) {
		super(tenant, site, featureGroup, featureName);
	}

	public FeatureConfigRequestContext(String tenantId, String siteId, String groupName) {
		super(tenantId, siteId, groupName);
	}
	

}
