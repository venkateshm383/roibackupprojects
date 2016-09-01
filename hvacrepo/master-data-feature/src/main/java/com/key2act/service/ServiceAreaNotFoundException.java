package com.key2act.service;

public class ServiceAreaNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public ServiceAreaNotFoundException() {
		super();
	}

	public ServiceAreaNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ServiceAreaNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceAreaNotFoundException(String message) {
		super(message);
	}

	public ServiceAreaNotFoundException(Throwable cause) {
		super(cause);
	}

}
