package com.key2act.service;

public class InvalidPrimaryTechnicianRequest extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidPrimaryTechnicianRequest() {
		super();
	}

	public InvalidPrimaryTechnicianRequest(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidPrimaryTechnicianRequest(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPrimaryTechnicianRequest(String message) {
		super(message);
	}

	public InvalidPrimaryTechnicianRequest(Throwable cause) {
		super(cause);
	}

}
