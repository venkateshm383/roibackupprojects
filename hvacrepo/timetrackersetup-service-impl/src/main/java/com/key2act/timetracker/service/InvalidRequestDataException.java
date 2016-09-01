package com.key2act.timetracker.service;

public class InvalidRequestDataException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7008297194308045399L;
	
	public InvalidRequestDataException() {
		super();
	}

	public InvalidRequestDataException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidRequestDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidRequestDataException(String message) {
		super(message);
	}

	public InvalidRequestDataException(Throwable cause) {
		super(cause);
	}


}
