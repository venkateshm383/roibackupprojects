package com.key2act.service;

public class InvalidContactNumberRequestException extends Exception {


	private static final long serialVersionUID = -2004872287309568383L;

	public InvalidContactNumberRequestException() {
		super();
	}

	public InvalidContactNumberRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidContactNumberRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidContactNumberRequestException(String message) {
		super(message);
	}

	public InvalidContactNumberRequestException(Throwable cause) {
		super(cause);
	}

}
