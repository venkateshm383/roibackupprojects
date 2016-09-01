package com.key2act.pojo;

public class ServiceArea {
	
	private String serviceAreaId;
	private String serviceAreaName;
	
	public ServiceArea() {}
	public ServiceArea(String serviceAreaId, String serviceAreaName) {
		super();
		this.serviceAreaId = serviceAreaId;
		this.serviceAreaName = serviceAreaName;
	}
	public String getServiceAreaId() {
		return serviceAreaId;
	}
	public void setServiceAreaId(String serviceAreaId) {
		this.serviceAreaId = serviceAreaId;
	}
	public String getServiceAreaName() {
		return serviceAreaName;
	}
	public void setServiceAreaName(String serviceAreaName) {
		this.serviceAreaName = serviceAreaName;
	}
	
}
