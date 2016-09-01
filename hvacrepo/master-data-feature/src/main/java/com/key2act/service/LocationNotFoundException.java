package com.key2act.service;

public class LocationNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public LocationNotFoundException() {
		super();
	}

	public LocationNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LocationNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public LocationNotFoundException(String message) {
		super(message);
	}

	public LocationNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
