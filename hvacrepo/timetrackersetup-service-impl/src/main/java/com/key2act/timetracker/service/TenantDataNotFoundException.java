package com.key2act.timetracker.service;

public class TenantDataNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8000755329920903740L;

	public TenantDataNotFoundException() {
		super();
	}

	public TenantDataNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TenantDataNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public TenantDataNotFoundException(String message) {
		super(message);
	}

	public TenantDataNotFoundException(Throwable cause) {
		super(cause);
	}

	
}
