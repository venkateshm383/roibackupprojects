package com.key2act.service;

import java.util.List;

import com.key2act.pojo.CallType;

public interface CallTypeService {

	/**
	 * this method is used to get callType details based on customer name
	 * 
	 * @param callType
	 *            based on callType details will be fetched
	 * @return return the json data in string format
	 * @throws CallTypeNotFoundException
	 *             throw CallTypeNotFoundException when callType details are not
	 *             available
	 * @throws InvalidCallTypeRequestException
	 * 				if callType is null then it throw InvalidCallTypeRequestException
	 */

	public List<CallType> getCallType(String callType)
			throws CallTypeNotFoundException, InvalidCallTypeRequestException;

	/**
	 * this method is use to get all call type details
	 * 
	 * @throws CallTypeNotFoundException:when
	 *             calltype details not found
	 * 
	 */
	public List<CallType> getAllCallType() throws CallTypeNotFoundException;

}
