package com.key2act.service;

public class InvalidServiceCallRequest extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidServiceCallRequest() {
		super();
	}

	public InvalidServiceCallRequest(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidServiceCallRequest(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidServiceCallRequest(String message) {
		super(message);
	}

	public InvalidServiceCallRequest(Throwable cause) {
		super(cause);
	}

}
