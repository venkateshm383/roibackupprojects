package com.key2act.service;

public class TechnicianNotFoundException extends Exception {

	private static final long serialVersionUID = -6916282096866830509L;

	public TechnicianNotFoundException() {
		super();
	}

	public TechnicianNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TechnicianNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public TechnicianNotFoundException(String message) {
		super(message);
	}

	public TechnicianNotFoundException(Throwable cause) {
		super(cause);
	}

}