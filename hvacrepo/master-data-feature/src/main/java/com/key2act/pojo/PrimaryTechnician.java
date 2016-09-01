package com.key2act.pojo;

public class PrimaryTechnician {
	
	private String primaryTechnicianId;
	private String primaryTechnicianType;
	private String primaryTechnicianName;
	public PrimaryTechnician() {}
	public PrimaryTechnician(String primaryTechnicianId, String primaryTechnicianType, String primaryTechnicianName) {
		super();
		this.primaryTechnicianId = primaryTechnicianId;
		this.primaryTechnicianType = primaryTechnicianType;
		this.primaryTechnicianName = primaryTechnicianName;
	}
	public String getPrimaryTechnicianId() {
		return primaryTechnicianId;
	}
	public void setPrimaryTechnicianId(String primaryTechnicianId) {
		this.primaryTechnicianId = primaryTechnicianId;
	}
	public String getPrimaryTechnicianType() {
		return primaryTechnicianType;
	}
	public void setPrimaryTechnicianType(String primaryTechnicianType) {
		this.primaryTechnicianType = primaryTechnicianType;
	}
	public String getPrimaryTechnicianName() {
		return primaryTechnicianName;
	}
	public void setPrimaryTechnicianName(String primaryTechnicianName) {
		this.primaryTechnicianName = primaryTechnicianName;
	}
	
}
