package com.getusroi.wms20.printservice.cups.Impl;

public class CupsPrinterServiceExcpetion extends Exception{

	private static final long serialVersionUID = 11L;

	public CupsPrinterServiceExcpetion() {
		super();
	}

	public CupsPrinterServiceExcpetion(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CupsPrinterServiceExcpetion(String message, Throwable cause) {
		super(message, cause);
	}

	public CupsPrinterServiceExcpetion(String message) {
		super(message);
	}

	public CupsPrinterServiceExcpetion(Throwable cause) {
		super(cause);
	}
	
	

}
