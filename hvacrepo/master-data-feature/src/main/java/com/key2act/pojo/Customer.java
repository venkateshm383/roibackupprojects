package com.key2act.pojo;

public class Customer {

	private String customerId;
	private String customerName;
	private String contactPerson1;
	private String phone1;
	private String contactPerson2;
	private String faxNumber;
	
	public Customer() {}

	public Customer(String customerId, String customerName, String contactPerson1, String phone1, String contactPerson2,
			String faxNumber) {
		super();
		this.customerId = customerId;
		this.customerName = customerName;
		this.contactPerson1 = contactPerson1;
		this.phone1 = phone1;
		this.contactPerson2 = contactPerson2;
		this.faxNumber = faxNumber;
	}

	public Customer(String customerId, String customerName) {
		super();
		this.customerId = customerId;
		this.customerName = customerName;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getContactPerson1() {
		return contactPerson1;
	}

	public void setContactPerson1(String contactPerson1) {
		this.contactPerson1 = contactPerson1;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getContactPerson2() {
		return contactPerson2;
	}

	public void setContactPerson2(String contactPerson2) {
		this.contactPerson2 = contactPerson2;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}
}
