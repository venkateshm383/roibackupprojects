package com.key2act.pojo;

public class ServiceCall {
	
	private String serviceCallId;
	private String serviceCallName;
	public ServiceCall() {}
	public ServiceCall(String serviceCallId, String serviceCallName) {
		super();
		this.serviceCallId = serviceCallId;
		this.serviceCallName = serviceCallName;
	}
	public String getServiceCallId() {
		return serviceCallId;
	}
	public void setServiceCallId(String serviceCallId) {
		this.serviceCallId = serviceCallId;
	}
	public String getServiceCallName() {
		return serviceCallName;
	}
	public void setServiceCallName(String serviceCallName) {
		this.serviceCallName = serviceCallName;
	}
	
}
