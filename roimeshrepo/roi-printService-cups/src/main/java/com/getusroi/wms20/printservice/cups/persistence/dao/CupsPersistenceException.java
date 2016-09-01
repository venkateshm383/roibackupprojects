package com.getusroi.wms20.printservice.cups.persistence.dao;

public class CupsPersistenceException extends Exception {

	private static final long serialVersionUID = 1L;

	public CupsPersistenceException() {
		super();
	}

	public CupsPersistenceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CupsPersistenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public CupsPersistenceException(String message) {
		super(message);
	}

	public CupsPersistenceException(Throwable cause) {
		super(cause);
	}

	

}
