package com.key2act.service;

public class BillCustomerNotFoundException extends Exception {

	private static final long serialVersionUID = 5920402256256106175L;

	public BillCustomerNotFoundException() {
		super();
	}

	public BillCustomerNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BillCustomerNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public BillCustomerNotFoundException(String message) {
		super(message);
	}

	public BillCustomerNotFoundException(Throwable cause) {
		super(cause);
	}

}
