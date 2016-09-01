package com.key2act.service;

public class DivisionNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public DivisionNotFoundException() {
		super();
	}

	public DivisionNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DivisionNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public DivisionNotFoundException(String message) {
		super(message);
	}

	public DivisionNotFoundException(Throwable cause) {
		super(cause);
	}

}
