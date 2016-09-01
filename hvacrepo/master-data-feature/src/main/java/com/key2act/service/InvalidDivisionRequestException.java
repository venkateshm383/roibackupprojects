package com.key2act.service;

public class InvalidDivisionRequestException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidDivisionRequestException() {
		super();
	}

	public InvalidDivisionRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidDivisionRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidDivisionRequestException(String message) {
		super(message);
	}

	public InvalidDivisionRequestException(Throwable cause) {
		super(cause);
	}

}
