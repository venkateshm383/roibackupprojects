package com.key2act.pojo;

public class MasterTaxSchedule {
	
	private String masterTaxScheduleId;
	private String masterTaxScheduleType;
	public MasterTaxSchedule() {}
	public MasterTaxSchedule(String masterTaxScheduleId, String masterTaxScheduleType) {
		super();
		this.masterTaxScheduleId = masterTaxScheduleId;
		this.masterTaxScheduleType = masterTaxScheduleType;
	}
	public String getMasterTaxScheduleId() {
		return masterTaxScheduleId;
	}
	public void setMasterTaxScheduleId(String masterTaxScheduleId) {
		this.masterTaxScheduleId = masterTaxScheduleId;
	}
	public String getMasterTaxScheduleType() {
		return masterTaxScheduleType;
	}
	public void setMasterTaxScheduleType(String masterTaxScheduleType) {
		this.masterTaxScheduleType = masterTaxScheduleType;
	}

}
