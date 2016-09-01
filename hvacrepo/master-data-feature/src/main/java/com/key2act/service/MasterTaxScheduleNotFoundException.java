package com.key2act.service;

public class MasterTaxScheduleNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public MasterTaxScheduleNotFoundException() {
		super();
	}

	public MasterTaxScheduleNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MasterTaxScheduleNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public MasterTaxScheduleNotFoundException(String message) {
		super(message);
	}

	public MasterTaxScheduleNotFoundException(Throwable cause) {
		super(cause);
	}

}
