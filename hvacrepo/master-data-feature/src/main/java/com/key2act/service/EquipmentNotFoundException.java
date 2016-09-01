package com.key2act.service;

public class EquipmentNotFoundException extends Exception {

	private static final long serialVersionUID = 1931726621474452484L;

	public EquipmentNotFoundException() {
		super();
	}

	public EquipmentNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EquipmentNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public EquipmentNotFoundException(String message) {
		super(message);
	}

	public EquipmentNotFoundException(Throwable cause) {
		super(cause);
	}
}
