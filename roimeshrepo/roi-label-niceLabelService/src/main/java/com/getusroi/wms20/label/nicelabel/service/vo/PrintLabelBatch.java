package com.getusroi.wms20.label.nicelabel.service.vo;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.getusroi.wms20.label.nicelabel.service.vo.PrintLabel;

public class PrintLabelBatch implements Serializable{
	
	
	private static final long serialVersionUID = -1090073489363876706L;
	private String batchid;
	private String outputType;
	private int printerId;
	private Map<String,PrintLabel> prinLabels= new HashMap<String,PrintLabel>();
	private boolean labelReady=false;
	private byte[] completeSubstitutedLabel;
	final static AtomicInteger squencenumber= new AtomicInteger();

	private byte[] printableFile;

	public String getBatchid() {
		return batchid;
	}

	public void setBatchid(String batchid) {
		this.batchid = batchid;
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	public int getPrinterId() {
		return printerId;
	}

	public void setPrinterId(int printerId) {
		this.printerId = printerId;
	}

	public Map<String, PrintLabel> getPrinLabels() {
		return prinLabels;
	}

	public void setPrinLabels(Map<String, PrintLabel> prinLabels) {
		this.prinLabels = prinLabels;
	}

	public byte[] getPrintableFile() {
		return printableFile;
	}

	public void setPrintableFile(byte[] printableFile) {
		this.printableFile = printableFile;
	}
	
	public void addPrintLabel(String labelid, PrintLabel value){
		
		if(prinLabels== null){
			prinLabels= new HashMap<String,PrintLabel>();
		}
		prinLabels.put(labelid, value);
	}
	
	public static int getSquenceId(){
	       int seq =squencenumber.incrementAndGet();
	         System.out.println("squencenumber  >> "+seq);
	 
		return seq;
		
	}

	public boolean isLabelReady() {
		return labelReady;
	}

	public void setLabelReady(boolean labelReady) {
		this.labelReady = labelReady;
	}

	public byte[] getCompleteSubstitutedLabel() {
		return completeSubstitutedLabel;
	}

	public void setCompleteSubstitutedLabel(byte[] completeSubstitutedLabel) {
		this.completeSubstitutedLabel = completeSubstitutedLabel;
	}
	
	

}
