package com.getusroi.wms20.parcel.parcelservice.fedEx.vo;

/**
 * Bean to get and set the Shipper details
 * @author BizRuntimeIT Services Pvt-Ltd
 */
public class Shipper {

	private int shipperId;
	private ContactAddress contactaddress;

	public int getShipperId() {
		return shipperId;
	}

	public void setShipperId(int shipperId) {
		this.shipperId = shipperId;
	}

	public ContactAddress getContactaddress() {
		return contactaddress;
	}

	public void setContactaddress(ContactAddress contactaddress) {
		this.contactaddress = contactaddress;
	}

}
