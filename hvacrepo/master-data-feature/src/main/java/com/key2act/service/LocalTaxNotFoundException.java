package com.key2act.service;

public class LocalTaxNotFoundException extends Exception {

	private static final long serialVersionUID = 6036850797191635866L;

	public LocalTaxNotFoundException() {
		super();
	}

	public LocalTaxNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LocalTaxNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public LocalTaxNotFoundException(String message) {
		super(message);
	}

	public LocalTaxNotFoundException(Throwable cause) {
		super(cause);
	}
}
