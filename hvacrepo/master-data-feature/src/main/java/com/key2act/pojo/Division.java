package com.key2act.pojo;

public class Division {

	private String divisionId;
	private String divisionName;
	
	public Division(){}
	public Division(String divisionId, String divisionName) {
		super();
		this.divisionId = divisionId;
		this.divisionName = divisionName;
	}
	public String getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(String divisionId) {
		this.divisionId = divisionId;
	}
	public String getDivisionName() {
		return divisionName;
	}
	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}
	
}
