package com.key2act.timetracker.timesheet;

public class TimeSheetTrackerProcessingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TimeSheetTrackerProcessingException() {
		super();
	}

	public TimeSheetTrackerProcessingException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TimeSheetTrackerProcessingException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public TimeSheetTrackerProcessingException(String message) {
		super(message);
	}

	public TimeSheetTrackerProcessingException(Throwable cause) {
		super(cause);
	}

}
