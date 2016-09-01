package com.key2act.service;

public class ServiceCallNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public ServiceCallNotFoundException() {
		super();
	}

	public ServiceCallNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ServiceCallNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceCallNotFoundException(String message) {
		super(message);
	}

	public ServiceCallNotFoundException(Throwable cause) {
		super(cause);
	}

}
