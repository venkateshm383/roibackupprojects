package com.key2act.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class Equipment {

	private String equipmentId;
	private String equipmentName;

	public Equipment() {
	}

	public Equipment(String equipmentId, String equipmentName) {
		super();
		this.equipmentId = equipmentId;
		this.equipmentName = equipmentName;
	}

	public String getequipmentId() {
		return equipmentId;
	}

	public void setequipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public String getequipmentName() {
		return equipmentName;
	}

	public void setequipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}
	
	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("equipmentId", equipmentId);
			obj.put("equipmentName", equipmentName);
		} catch (JSONException e) {
		}
		return obj;
	}

}
