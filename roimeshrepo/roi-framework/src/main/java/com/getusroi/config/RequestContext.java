package com.getusroi.config;

import java.io.Serializable;

import com.hazelcast.transaction.TransactionContext;

public class RequestContext implements Serializable{
	private static final long serialVersionUID = -5504878973750093570L;

	private String tenantId;
	private String siteId;
	private String featureGroup;
	private String featureName;
	private String vendor;
	private String version;
	private transient TransactionContext hcTransactionalContext;
	private String requestId;
	
	
	public RequestContext(){
		
	}
	
	public RequestContext(String tenantId, String siteId, String featureGroup, String featureName, String vendor,
			String version) {
		super();
		this.tenantId = tenantId;
		this.siteId = siteId;
		this.featureGroup = featureGroup;
		this.featureName = featureName;
		this.vendor = vendor;
		this.version = version;
	}

	public RequestContext(String tenantId, String siteId, String featureGroup) {
		this.tenantId = tenantId;
		this.siteId = siteId;
		this.featureGroup = featureGroup;
	}

	public RequestContext(String tenantId, String siteId, String featureGroup, String featureName) {
		super();
		this.tenantId = tenantId;
		this.siteId = siteId;
		this.featureGroup = featureGroup;
		this.featureName = featureName;
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
	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	
	public TransactionContext getHcTransactionalContext() {
		return hcTransactionalContext;
	}

	public void setHcTransactionalContext(TransactionContext hcTransactionalContext) {
		this.hcTransactionalContext = hcTransactionalContext;
	}
		
	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public boolean isValid(){
		if(this.tenantId==null||this.tenantId.isEmpty())
			return false;
		if(this.siteId==null||this.siteId.isEmpty())
			return false;
		if(this.featureGroup==null||this.featureGroup.isEmpty())
			return false;
		
		return true;
	}

	public ConfigurationContext getConfigurationContext (){
		return new ConfigurationContext(this.tenantId,this.siteId,this.featureGroup,this.featureName,this.vendor,this.version);
	}

	@Override
	public String toString() {
		return "RequestContext [tenantId=" + tenantId + ", siteId=" + siteId + ", featureGroup=" + featureGroup
				+ ", featureName=" + featureName + ", vendor=" + vendor + ", version=" + version + "]";
	}
	
}
