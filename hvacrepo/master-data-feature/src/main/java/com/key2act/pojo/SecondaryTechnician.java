package com.key2act.pojo;

public class SecondaryTechnician {
	
	private String secondayTechnicianId;
	private String secondayTechnicianType;
	private String secondayTechnicianName;
	
	public SecondaryTechnician() {}
	public SecondaryTechnician(String secondayTechnicianId, String secondayTechnicianType,
			String secondayTechnicianName) {
		super();
		this.secondayTechnicianId = secondayTechnicianId;
		this.secondayTechnicianType = secondayTechnicianType;
		this.secondayTechnicianName = secondayTechnicianName;
	}
	public String getSecondayTechnicianId() {
		return secondayTechnicianId;
	}
	public void setSecondayTechnicianId(String secondayTechnicianId) {
		this.secondayTechnicianId = secondayTechnicianId;
	}
	public String getSecondayTechnicianType() {
		return secondayTechnicianType;
	}
	public void setSecondayTechnicianType(String secondayTechnicianType) {
		this.secondayTechnicianType = secondayTechnicianType;
	}
	public String getSecondayTechnicianName() {
		return secondayTechnicianName;
	}
	public void setSecondayTechnicianName(String secondayTechnicianName) {
		this.secondayTechnicianName = secondayTechnicianName;
	}
	
}
