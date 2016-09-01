package com.key2act.service;

public class CustomerInvalidRequestException extends Exception {

	private static final long serialVersionUID = 1L;

	public CustomerInvalidRequestException() {
		super();
	}

	public CustomerInvalidRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CustomerInvalidRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public CustomerInvalidRequestException(String message) {
		super(message);
	}

	public CustomerInvalidRequestException(Throwable cause) {
		super(cause);
	}

}
