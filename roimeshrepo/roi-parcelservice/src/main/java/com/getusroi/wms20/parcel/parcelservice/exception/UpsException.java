package com.getusroi.wms20.parcel.parcelservice.exception;

public class UpsException extends Exception{

	/**
	 * custom exception class extends Exception
	 */
	private static final long serialVersionUID = 1L;

	public UpsException() {
		super();
	}

	public UpsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UpsException(String message, Throwable cause) {
		super(message, cause);
	}

	public UpsException(String message) {
		super(message);
	}

	public UpsException(Throwable cause) {
		super(cause);
	}

}
