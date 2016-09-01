package com.key2act.service;

public class SalesPersonNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public SalesPersonNotFoundException() {
		super();
	}

	public SalesPersonNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SalesPersonNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public SalesPersonNotFoundException(String message) {
		super(message);
	}

	public SalesPersonNotFoundException(Throwable cause) {
		super(cause);
	}

}
