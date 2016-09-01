package com.key2act.service;

public class InvalidMasterTaxScheduleRequestException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidMasterTaxScheduleRequestException() {
		super();
	}

	public InvalidMasterTaxScheduleRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidMasterTaxScheduleRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidMasterTaxScheduleRequestException(String message) {
		super(message);
	}

	public InvalidMasterTaxScheduleRequestException(Throwable cause) {
		super(cause);
	}

}
