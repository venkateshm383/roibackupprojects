package com.key2act.service;

import java.util.List;

import com.key2act.pojo.CallStatus;

public interface CallStatusService {

	/**
	 * this method is used to get call status details based on customer name
	 * 
	 * @param callstatusId
	 *            based on callstatusId details will be fetched
	 * @return return the json data in string format
	 * @throws CallStatusNotFoundException
	 *             throw CallStatusNotFoundException when callStatus are not
	 *             available
	 * @throws CallStatusInvalidRequestException
	 * 				if callstatusId is null then it throw CallStatusInvalidRequestException
	 */
	public List<CallStatus> getCallStatus(String callstatusId)
			throws CallStatusNotFoundException, CallStatusInvalidRequestException;

	/**
	 * this method is use to get all call status details
	 * 
	 * @throws CallStatusNotFoundException:when
	 *             call status detail not found
	 * 
	 */
	public List<CallStatus> getAllCallStatus() throws CallStatusNotFoundException;

}
