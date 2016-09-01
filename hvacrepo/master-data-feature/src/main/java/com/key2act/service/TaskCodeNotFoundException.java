package com.key2act.service;

public class TaskCodeNotFoundException extends Exception {

	private static final long serialVersionUID = -6585755770129019258L;

	public TaskCodeNotFoundException() {
		super();
	}

	public TaskCodeNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TaskCodeNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public TaskCodeNotFoundException(String message) {
		super(message);
	}

	public TaskCodeNotFoundException(Throwable cause) {
		super(cause);
	}

}
