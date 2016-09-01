package com.getusroi.wms20.printservice.bean;

public class PrintServiceException extends Exception {

	private static final long serialVersionUID = -5575101925107121356L;

	public PrintServiceException() {
		super();
	}

	public PrintServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PrintServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public PrintServiceException(String message) {
		super(message);
	}

	public PrintServiceException(Throwable cause) {
		super(cause);
	}

	
}
