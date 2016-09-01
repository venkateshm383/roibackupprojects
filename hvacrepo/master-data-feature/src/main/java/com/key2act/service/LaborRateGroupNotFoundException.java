package com.key2act.service;

public class LaborRateGroupNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public LaborRateGroupNotFoundException() {
		super();
	}

	public LaborRateGroupNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LaborRateGroupNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public LaborRateGroupNotFoundException(String message) {
		super(message);
	}

	public LaborRateGroupNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
