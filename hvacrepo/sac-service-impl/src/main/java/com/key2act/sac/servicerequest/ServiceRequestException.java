package com.key2act.sac.servicerequest;

public class ServiceRequestException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7560547873980889074L;

	public ServiceRequestException() {
		super();
	}

	public ServiceRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ServiceRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceRequestException(String message) {
		super(message);
	}

	public ServiceRequestException(Throwable cause) {
		super(cause);
	}
	

}
