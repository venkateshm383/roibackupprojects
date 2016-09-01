package com.key2act.service;

public class InvalidLocationRequest extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidLocationRequest() {
		super();
	}

	public InvalidLocationRequest(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidLocationRequest(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidLocationRequest(String message) {
		super(message);
	}

	public InvalidLocationRequest(Throwable cause) {
		super(cause);
	}

}
