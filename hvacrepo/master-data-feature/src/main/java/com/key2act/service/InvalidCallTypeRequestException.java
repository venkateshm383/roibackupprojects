package com.key2act.service;

public class InvalidCallTypeRequestException extends Exception {
	
	private static final long serialVersionUID = 2974221593873861713L;

	public InvalidCallTypeRequestException() {
		super();
	}

	public InvalidCallTypeRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidCallTypeRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidCallTypeRequestException(String message) {
		super(message);
	}

	public InvalidCallTypeRequestException(Throwable cause) {
		super(cause);
	}
	
	

}
