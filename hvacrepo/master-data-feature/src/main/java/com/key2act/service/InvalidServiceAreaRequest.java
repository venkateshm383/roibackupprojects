package com.key2act.service;

public class InvalidServiceAreaRequest extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidServiceAreaRequest() {
		super();
	}

	public InvalidServiceAreaRequest(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidServiceAreaRequest(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidServiceAreaRequest(String message) {
		super(message);
	}

	public InvalidServiceAreaRequest(Throwable cause) {
		super(cause);
	}

}
