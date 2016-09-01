package com.key2act.service;

public class BillToAddressNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public BillToAddressNotFoundException() {
		super();
	}

	public BillToAddressNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BillToAddressNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public BillToAddressNotFoundException(String message) {
		super(message);
	}

	public BillToAddressNotFoundException(Throwable cause) {
		super(cause);
	}

}
