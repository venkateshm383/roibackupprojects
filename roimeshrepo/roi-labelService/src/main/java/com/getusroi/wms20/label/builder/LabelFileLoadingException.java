package com.getusroi.wms20.label.builder;

public class LabelFileLoadingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8521914598987109137L;
	
	
	public LabelFileLoadingException() {
		super();
	}

	public LabelFileLoadingException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public LabelFileLoadingException(String message, Throwable cause) {
		super(message, cause);
	}

	public LabelFileLoadingException(String message) {
		super(message);
		
	}

	public LabelFileLoadingException(Throwable cause) {
		super(cause);
	}

}
