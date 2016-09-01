package com.key2act.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class JobNumber {

	private String jobNum;
	private String jobName;

	public JobNumber() {
	}

	public JobNumber(String jobNum, String jobName) {
		super();
		this.jobNum = jobNum;
		this.jobName = jobName;
	}

	public String getjobNum() {
		return jobNum;
	}

	public void setjobNum(String jobNum) {
		this.jobNum = jobNum;
	}

	public String getjobName() {
		return jobName;
	}

	public void setjobName(String jobName) {
		this.jobName = jobName;
	}
	
	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("jobNum", jobNum);
			obj.put("jobName", jobName);
		} catch (JSONException e) {
		}
		return obj;
	}

}
