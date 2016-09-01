package com.key2act.service;

public class InvalidSecondaryTechnicianRequest extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidSecondaryTechnicianRequest() {
		super();
	}

	public InvalidSecondaryTechnicianRequest(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidSecondaryTechnicianRequest(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidSecondaryTechnicianRequest(String message) {
		super(message);
	}

	public InvalidSecondaryTechnicianRequest(Throwable cause) {
		super(cause);
	}

}
