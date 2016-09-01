package com.key2act.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class Technician {

	private String technicianId;
	private String technicianName;

	public Technician() {
	}

	public Technician(String technicianId, String technicianName) {
		super();
		this.technicianId = technicianId;
		this.technicianName = technicianName;
	}

	public String gettechnicianId() {
		return technicianId;
	}

	public void settechnicianId(String technicianId) {
		this.technicianId = technicianId;
	}

	public String gettechnicianName() {
		return technicianName;
	}

	public void settechnicianName(String technicianName) {
		this.technicianName = technicianName;
	}
	
	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("technicianId", technicianId);
			obj.put("technicianName", technicianName);
		} catch (JSONException e) {
		}
		return obj;
	}

}