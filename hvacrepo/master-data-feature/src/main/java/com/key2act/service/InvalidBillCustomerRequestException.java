package com.key2act.service;

public class InvalidBillCustomerRequestException extends Exception {

	private static final long serialVersionUID = 5490146235295618163L;

	public InvalidBillCustomerRequestException() {
		super();
	}

	public InvalidBillCustomerRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidBillCustomerRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidBillCustomerRequestException(String message) {
		super(message);
	}

	public InvalidBillCustomerRequestException(Throwable cause) {
		super(cause);
	}
}

	