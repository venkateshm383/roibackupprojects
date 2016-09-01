package com.key2act.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class ServiceLevel {

	private String serviceId;
	private String serviceLevelName;

	public ServiceLevel() {
	}

	public ServiceLevel(String serviceId, String serviceLevelName) {
		super();
		this.serviceId = serviceId;
		this.serviceLevelName = serviceLevelName;
	}

	public String getserviceId() {
		return serviceId;
	}

	public void setserviceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getserviceLevelName() {
		return serviceLevelName;
	}

	public void setserviceLevelName(String serviceLevelName) {
		this.serviceLevelName = serviceLevelName;
	}
	
	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("serviceId", serviceId);
			obj.put("serviceLevelName", serviceLevelName);
		} catch (JSONException e) {
		}
		return obj;
	}

}