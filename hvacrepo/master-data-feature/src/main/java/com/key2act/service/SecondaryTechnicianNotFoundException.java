package com.key2act.service;

public class SecondaryTechnicianNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public SecondaryTechnicianNotFoundException() {
		super();
	}

	public SecondaryTechnicianNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SecondaryTechnicianNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public SecondaryTechnicianNotFoundException(String message) {
		super(message);
	}

	public SecondaryTechnicianNotFoundException(Throwable cause) {
		super(cause);
	}

}
