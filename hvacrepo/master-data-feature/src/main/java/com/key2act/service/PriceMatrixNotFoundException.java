package com.key2act.service;

public class PriceMatrixNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public PriceMatrixNotFoundException() {
		super();
	}

	public PriceMatrixNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PriceMatrixNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public PriceMatrixNotFoundException(String message) {
		super(message);
	}

	public PriceMatrixNotFoundException(Throwable cause) {
		super(cause);
	}

}
