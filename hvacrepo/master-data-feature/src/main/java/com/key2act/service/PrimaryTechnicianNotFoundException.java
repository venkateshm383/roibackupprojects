package com.key2act.service;

public class PrimaryTechnicianNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public PrimaryTechnicianNotFoundException() {
		super();
	}

	public PrimaryTechnicianNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PrimaryTechnicianNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public PrimaryTechnicianNotFoundException(String message) {
		super(message);
	}

	public PrimaryTechnicianNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
