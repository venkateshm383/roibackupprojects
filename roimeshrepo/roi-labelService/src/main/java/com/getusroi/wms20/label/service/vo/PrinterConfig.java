package com.getusroi.wms20.label.service.vo;

import java.io.Serializable;

public class PrinterConfig implements Serializable{
	private int printerID;
	private  String outputType;
	public int getPrinterID() {
		return printerID;
	}
	public void setPrinterID(int printerID) {
		this.printerID = printerID;
	}
	public String getOutputType() {
		return outputType;
	}
	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}
	
	
	@Override
	public String toString() {
		return "PrinterConfig [printerID=" + printerID + ", outputType=" + outputType + "]";
	}
	
	

}
