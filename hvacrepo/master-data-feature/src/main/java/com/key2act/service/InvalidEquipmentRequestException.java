package com.key2act.service;

public class InvalidEquipmentRequestException extends Exception {

	private static final long serialVersionUID = -8051450407959761934L;

	public InvalidEquipmentRequestException() {
		super();
	}

	public InvalidEquipmentRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidEquipmentRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidEquipmentRequestException(String message) {
		super(message);
	}

	public InvalidEquipmentRequestException(Throwable cause) {
		super(cause);
	}

}
