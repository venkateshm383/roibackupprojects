package com.key2act.service;

public class JobNumberNotFoundException extends Exception {

	private static final long serialVersionUID = -6297858464553651700L;

	public JobNumberNotFoundException() {
		super();
	}

	public JobNumberNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public JobNumberNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public JobNumberNotFoundException(String message) {
		super(message);
	}

	public JobNumberNotFoundException(Throwable cause) {
		super(cause);
	}

}
