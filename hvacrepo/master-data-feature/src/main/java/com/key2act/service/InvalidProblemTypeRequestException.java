package com.key2act.service;

public class InvalidProblemTypeRequestException extends Exception{

	private static final long serialVersionUID = 9000068516344246290L;
	
	public InvalidProblemTypeRequestException() {
		super();
	}

	public InvalidProblemTypeRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidProblemTypeRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidProblemTypeRequestException(String message) {
		super(message);
	}

	public InvalidProblemTypeRequestException(Throwable cause) {
		super(cause);
	}

}