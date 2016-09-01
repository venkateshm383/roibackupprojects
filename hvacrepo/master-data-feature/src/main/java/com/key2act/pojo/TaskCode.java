package com.key2act.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class TaskCode {

	private String taskcode;
	private String taskName;

	public TaskCode() {
	}

	public TaskCode(String taskcode, String taskName) {
		super();
		this.taskcode = taskcode;
		this.taskName = taskName;
	}

	public String gettaskcode() {
		return taskcode;
	}

	public void settaskcode(String taskcode) {
		this.taskcode = taskcode;
	}

	public String gettaskName() {
		return taskName;
	}

	public void settaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("taskcode", taskcode);
			obj.put("taskName", taskName);
		} catch (JSONException e) {
		}
		return obj;
	}

}
