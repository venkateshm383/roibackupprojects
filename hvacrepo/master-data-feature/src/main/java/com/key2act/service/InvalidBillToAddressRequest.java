package com.key2act.service;

public class InvalidBillToAddressRequest extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidBillToAddressRequest() {
		super();
	}

	public InvalidBillToAddressRequest(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidBillToAddressRequest(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidBillToAddressRequest(String message) {
		super(message);
	}

	public InvalidBillToAddressRequest(Throwable cause) {
		super(cause);
	}

}
