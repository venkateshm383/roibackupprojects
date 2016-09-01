package com.key2act.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class LocalTax {

	private String localTaxId;
	private String localTaxName;

	public LocalTax() {
	}

	public LocalTax(String localTaxId, String localTaxName) {
		super();
		this.localTaxId = localTaxName;
		this.localTaxName = localTaxName;
	}

	public String getlocalTaxId() {
		return localTaxId;
	}

	public void setlocalTaxId(String localTaxId) {
		this.localTaxId = localTaxId;
	}

	public String getlocalTaxName() {
		return localTaxName;
	}

	public void setlocalTaxName(String localTaxName) {
		this.localTaxName = localTaxName;
	}
	
	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("localTaxId", localTaxId);
			obj.put("localTaxName", localTaxName);
		} catch (JSONException e) {
		}
		return obj;
	}


}
