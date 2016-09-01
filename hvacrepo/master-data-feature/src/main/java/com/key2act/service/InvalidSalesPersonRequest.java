package com.key2act.service;

public class InvalidSalesPersonRequest extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidSalesPersonRequest() {
		super();
	}

	public InvalidSalesPersonRequest(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidSalesPersonRequest(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidSalesPersonRequest(String message) {
		super(message);
	}

	public InvalidSalesPersonRequest(Throwable cause) {
		super(cause);
	}

}
