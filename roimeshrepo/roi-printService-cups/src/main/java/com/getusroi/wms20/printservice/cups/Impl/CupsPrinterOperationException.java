package com.getusroi.wms20.printservice.cups.Impl;

public class CupsPrinterOperationException extends Exception{

	private static final long serialVersionUID = 14546576787214L;

	public CupsPrinterOperationException() {
		super();
	}

	public CupsPrinterOperationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CupsPrinterOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public CupsPrinterOperationException(String message) {
		super(message);
	}

	public CupsPrinterOperationException(Throwable cause) {
		super(cause);
	}
	

}
