package com.key2act.service;

public class InvalidPriceMatrixRequest extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidPriceMatrixRequest() {
		super();
	}

	public InvalidPriceMatrixRequest(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidPriceMatrixRequest(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPriceMatrixRequest(String message) {
		super(message);
	}

	public InvalidPriceMatrixRequest(Throwable cause) {
		super(cause);
	}

}
