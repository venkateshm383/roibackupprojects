package com.key2act.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class ContactNumber {

	private String contactNum;
	private String contactName;

	public ContactNumber() {
	}

	public ContactNumber(String contactNum, String contactName) {
		super();
		this.contactNum = contactNum;
		this.contactName = contactName;
	}

	public String getcontactNum() {
		return contactNum;
	}

	public void setcontactNum(String contactNum) {
		this.contactNum = contactNum;
	}

	public String getcontactName() {
		return contactName;
	}

	public void setcontactName(String contactName) {
		this.contactName = contactName;
	}
	
	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("contactNum", contactNum);
			obj.put("contactName", contactName);
		} catch (JSONException e) {
		}
		return obj;
	}

}
