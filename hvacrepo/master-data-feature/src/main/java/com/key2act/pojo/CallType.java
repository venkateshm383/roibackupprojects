package com.key2act.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class CallType {

	private String callType;
	private String callTypeId;

	public CallType() {
	}

	public CallType(String callType, String callTypeId) {
		super();
		this.callType = callType;
		this.callTypeId = callTypeId;
	}

	public String getcallType() {
		return callType;
	}

	public void setcallType(String callType) {
		this.callType = callType;
	}

	public String getCallTypeId() {
		return callTypeId;
	}

	public void setCallTypeId(String callTypeId) {
		this.callTypeId = callTypeId;
	}

	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("callTypeId", callTypeId);
			obj.put("callType", callType);
		} catch (JSONException e) {
		}
		return obj;
	}

}
