package com.key2act.service;

public class CallStatusInvalidRequestException extends Exception {

	private static final long serialVersionUID = -4700229917993034353L;
	
	public CallStatusInvalidRequestException() {
		super();
	}

	public CallStatusInvalidRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CallStatusInvalidRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public CallStatusInvalidRequestException(String message) {
		super(message);
	}

	public CallStatusInvalidRequestException(Throwable cause) {
		super(cause);
	}


}
