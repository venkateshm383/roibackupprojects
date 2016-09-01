package com.key2act.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class CallStatus {

	private String callstatusId;
	private String callstatus;

	public CallStatus() {
	}

	public CallStatus(String callstatus, String callstatusId) {
		super();
		this.setCallstatusId(callstatusId);
		this.callstatus = callstatus;
	}

	public String getCallstatusId() {
		return callstatusId;
	}

	public void setCallstatusId(String callstatusId) {
		this.callstatusId = callstatusId;
	}

	public String getcallstatus() {
		return callstatus;
	}

	public void setcallstatus(String callstatus) {
		this.callstatus = callstatus;
	}
	
	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("callstatusId", callstatusId);
			obj.put("callstatus", callstatus);
		} catch (JSONException e) {
		}
		return obj;
	}

}
