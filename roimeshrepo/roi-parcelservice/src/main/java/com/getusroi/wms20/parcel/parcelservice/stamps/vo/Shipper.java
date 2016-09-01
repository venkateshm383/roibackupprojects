package com.getusroi.wms20.parcel.parcelservice.stamps.vo;

/**
 * Bean to set and get the shipper object
 * @author bizruntime
 *
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
