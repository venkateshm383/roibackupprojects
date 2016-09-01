package com.key2act.bean;

public class InvalidJSONKey extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidJSONKey() {
		super();
	}

	public InvalidJSONKey(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidJSONKey(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidJSONKey(String message) {
		super(message);
	}

	public InvalidJSONKey(Throwable cause) {
		super(cause);
	}
	
}
