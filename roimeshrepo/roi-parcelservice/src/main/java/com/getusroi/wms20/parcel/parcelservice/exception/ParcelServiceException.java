package com.getusroi.wms20.parcel.parcelservice.exception;

public class ParcelServiceException extends Exception{

	/**
	 * exception class extends Exception
	 */
	private static final long serialVersionUID = 1L;

	public ParcelServiceException() {
		super();
	}

	public ParcelServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ParcelServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParcelServiceException(String message) {
		super(message);
	}

	public ParcelServiceException(Throwable cause) {
		super(cause);
	}

}
