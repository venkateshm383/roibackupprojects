package com.getusroi.wms20.label.bartender.service.vo;

import java.io.Serializable;

public class PrintLabel  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String templateID;
	private String internalDataType;
	private String labeldata;
	private int groupSequence;
	private String externalType;
	private int copies;
	private int internalId;
	
	
	public String getLabeldata() {
		return labeldata;
	}
	public void setLabeldata(String labeldata) {
		this.labeldata = labeldata;
	}
	
	public String getTemplateID() {
		return templateID;
	}
	public void setTemplateID(String templateID) {
		this.templateID = templateID;
	}
	public String getInternalDataType() {
		return internalDataType;
	}
	public void setInternalDataType(String internalDataType) {
		this.internalDataType = internalDataType;
	}
	public int getCopies() {
		return copies;
	}
	public void setCopies(int copies) {
		this.copies = copies;
	}
	public int getInternalId() {
		return internalId;
	}
	public void setInternalId(int internalId) {
		this.internalId = internalId;
	}
	public int getGroupSequence() {
		return groupSequence;
	}
	public void setGroupSequence(int groupSequence) {
		this.groupSequence = groupSequence;
	}
	public String getExternalType() {
		return externalType;
	}
	public void setExternalType(String externalType) {
		this.externalType = externalType;
	}
	@Override
	public String toString() {
		return "PrintLabel [templateID=" + templateID + ", internalDataType=" + internalDataType + ", labeldata=" + labeldata + ", groupSequence="
				+ groupSequence + ", externalType=" + externalType + ", copies=" + copies + ", internalId=" + internalId + "]";
	}
	
	

}
