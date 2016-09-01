package com.key2act.service;

public class ProblemTypeIsNotFoundException extends Exception {

	private static final long serialVersionUID = -2596612252342652346L;

	public ProblemTypeIsNotFoundException() {
		super();
	}

	public ProblemTypeIsNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ProblemTypeIsNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProblemTypeIsNotFoundException(String message) {
		super(message);
	}

	public ProblemTypeIsNotFoundException(Throwable cause) {
		super(cause);
	}

}
