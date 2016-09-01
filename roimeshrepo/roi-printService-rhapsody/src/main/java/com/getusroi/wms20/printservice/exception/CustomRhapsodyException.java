package com.getusroi.wms20.printservice.exception;

public class CustomRhapsodyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CustomRhapsodyException() {
		super();
	}

	public CustomRhapsodyException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CustomRhapsodyException(String message, Throwable cause) {
		super(message, cause);
	}

	public CustomRhapsodyException(String message) {
		super(message);
	}

	public CustomRhapsodyException(Throwable cause) {
		super(cause);
	}

}
