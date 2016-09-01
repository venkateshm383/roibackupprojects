package com.key2act.service;

public class InvalidJobNumberRequestException extends Exception{

	private static final long serialVersionUID = -7168513121892526231L;

	public InvalidJobNumberRequestException() {
		super();
	}

	public InvalidJobNumberRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public InvalidJobNumberRequestException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidJobNumberRequestException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidJobNumberRequestException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
