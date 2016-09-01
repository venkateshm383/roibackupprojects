package com.key2act.pojo;

public class SalesPerson {
	
	private String salesPersonId;
	private String salesPersonName;
	
	public SalesPerson() {}
	
	public SalesPerson(String salesPersonId, String salesPersonName) {
		super();
		this.salesPersonId = salesPersonId;
		this.salesPersonName = salesPersonName;
	}

	public String getSalesPersonId() {
		return salesPersonId;
	}

	public void setSalesPersonId(String salesPersonId) {
		this.salesPersonId = salesPersonId;
	}

	public String getSalesPersonName() {
		return salesPersonName;
	}

	public void setSalesPersonName(String salesPersonName) {
		this.salesPersonName = salesPersonName;
	}
	
}
