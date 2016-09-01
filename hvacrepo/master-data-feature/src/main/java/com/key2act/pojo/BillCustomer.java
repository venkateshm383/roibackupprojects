package com.key2act.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class BillCustomer {

	private String billId;
	private String customerName;

	public BillCustomer() {
	}

	public BillCustomer(String billId, String customerName) {
		super();
		this.billId = billId;
		this.customerName = customerName;
	}

	public String getbillId() {
		return billId;
	}

	public void setbillId(String equipmentId) {
		this.billId = equipmentId;
	}

	public String getcustomerName() {
		return customerName;
	}

	public void setcustomerName(String customerName) {
		this.customerName = customerName;
	}

	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("billId", billId);
			obj.put("customerName", customerName);
		} catch (JSONException e) {
		}
		return obj;
	}

}