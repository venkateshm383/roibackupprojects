package com.key2act.service;

public class CallStatusNotFoundException extends Exception {

	private static final long serialVersionUID = 8024952966027684616L;

	public CallStatusNotFoundException() {
		super();
	}

	public CallStatusNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CallStatusNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public CallStatusNotFoundException(String message) {
		super(message);
	}

	public CallStatusNotFoundException(Throwable cause) {
		super(cause);
	}
}