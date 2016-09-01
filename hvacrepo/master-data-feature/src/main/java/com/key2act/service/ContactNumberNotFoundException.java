package com.key2act.service;

public class ContactNumberNotFoundException extends Exception {

	private static final long serialVersionUID = -6500355324252455984L;

	public ContactNumberNotFoundException() {
		super();
	}

	public ContactNumberNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ContactNumberNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContactNumberNotFoundException(String message) {
		super(message);
	}

	public ContactNumberNotFoundException(Throwable cause) {
		super(cause);
	}

}
