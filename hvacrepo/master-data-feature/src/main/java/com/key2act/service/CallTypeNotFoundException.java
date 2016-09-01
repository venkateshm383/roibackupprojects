package com.key2act.service;

public class CallTypeNotFoundException extends Exception {

	private static final long serialVersionUID = -415065351277434503L;

	public CallTypeNotFoundException() {
		super();
	}

	public CallTypeNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CallTypeNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public CallTypeNotFoundException(String message) {
		super(message);
	}

	public CallTypeNotFoundException(Throwable cause) {
		super(cause);
	}

}
