package com.getusroi.config;

public class ConfigurationContext {
	private String tenantId;
	private String siteId;
	private String featureGroup;
	private String featureName;
	private String vendorName;
	private String version;
	
	
	public ConfigurationContext(String tenantId, String siteId, String featureGroup, String featureName,String vendor,String version) {
		this.tenantId = tenantId;
		this.siteId = siteId;
		this.featureGroup = featureGroup;
		this.featureName = featureName;
		this.vendorName=vendor;
		this.version=version;
	}
	public ConfigurationContext(String tenantId, String siteId, String featureGroup, String featureName) {
		this.tenantId = tenantId;
		this.siteId = siteId;
		this.featureGroup = featureGroup;
		this.featureName = featureName;
	}

	public ConfigurationContext(String tenantId, String siteId, String featureGroup) {
		this.tenantId = tenantId;
		this.siteId = siteId;
		this.featureGroup = featureGroup;
	}
	
	
	public ConfigurationContext(String tenantId, String siteId) {
		this.tenantId = tenantId;
		this.siteId = siteId;
	}

	public ConfigurationContext(RequestContext reqContext) {
		this.tenantId=reqContext.getTenantId();
		this.siteId=reqContext.getSiteId();
		this.featureGroup=reqContext.getFeatureGroup();
		this.featureName=reqContext.getFeatureName();
		this.vendorName=reqContext.getVendor();
		this.version=reqContext.getVersion();
	}
	
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getFeatureGroup() {
		return featureGroup;
	}

	public void setFeatureGroup(String featureGroup) {
		this.featureGroup = featureGroup;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	@Override
	public String toString() {
		return "ConfigurationContext [tenantId=" + tenantId + ", siteId=" + siteId + ", featureGroup=" + featureGroup + ", featureName=" + featureName
				+ ", vendorName=" + (vendorName!=null?vendorName:"") + "]";
	}
	
	
	
}
