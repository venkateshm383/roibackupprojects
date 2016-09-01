package com.key2act.pojo;

public class LaborRateGroup {
	
	private String laborRateGroupId;
	private String laborRateGroupName;
	private String laborRateGroupAge;
	private String laborRateGroupGender;
	public LaborRateGroup(String laborRateGroupId, String laborRateGroupName, String laborRateGroupAge,
			String laborRateGroupGender) {
		super();
		this.laborRateGroupId = laborRateGroupId;
		this.laborRateGroupName = laborRateGroupName;
		this.laborRateGroupAge = laborRateGroupAge;
		this.laborRateGroupGender = laborRateGroupGender;
	}
	public String getLaborRateGroupId() {
		return laborRateGroupId;
	}
	public void setLaborRateGroupId(String laborRateGroupId) {
		this.laborRateGroupId = laborRateGroupId;
	}
	public String getLaborRateGroupName() {
		return laborRateGroupName;
	}
	public void setLaborRateGroupName(String laborRateGroupName) {
		this.laborRateGroupName = laborRateGroupName;
	}
	public String getLaborRateGroupAge() {
		return laborRateGroupAge;
	}
	public void setLaborRateGroupAge(String laborRateGroupAge) {
		this.laborRateGroupAge = laborRateGroupAge;
	}
	public String getLaborRateGroupGender() {
		return laborRateGroupGender;
	}
	public void setLaborRateGroupGender(String laborRateGroupGender) {
		this.laborRateGroupGender = laborRateGroupGender;
	}
	
}
