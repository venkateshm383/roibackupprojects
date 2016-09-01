package com.key2act.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class ProblemType {

	private String technicalIssues;
	private String problemId;

	public ProblemType() {
	}

	public ProblemType(String technicalIssues, String problemId) {
		super();
		this.technicalIssues = technicalIssues;
		this.problemId = problemId;
	}

	public String gettechnicalIssues() {
		return technicalIssues;
	}

	public void settechnicalIssues(String technicalIssues) {
		this.technicalIssues = technicalIssues;
	}

	public String getproblemId() {
		return problemId;
	}

	public void setproblemId(String problemId) {
		this.problemId = problemId;
	}

	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("problemId", problemId);
			obj.put("technicalIssues", technicalIssues);
		} catch (JSONException e) {
		}
		return obj;
	}
	
}
