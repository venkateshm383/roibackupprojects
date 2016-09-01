package com.key2act.service;

public class InvalidLaborRateGroupException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidLaborRateGroupException() {
		super();
	}

	public InvalidLaborRateGroupException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidLaborRateGroupException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidLaborRateGroupException(String message) {
		super(message);
	}

	public InvalidLaborRateGroupException(Throwable cause) {
		super(cause);
	}
	
}
