package com.key2act.service;

public class PropertyFileNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public PropertyFileNotFoundException() {
		super();
	}

	public PropertyFileNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PropertyFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public PropertyFileNotFoundException(String message) {
		super(message);
	}

	public PropertyFileNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
