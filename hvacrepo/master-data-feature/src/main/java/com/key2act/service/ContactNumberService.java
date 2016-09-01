package com.key2act.service;

import java.util.List;

import com.key2act.pojo.ContactNumber;

public interface ContactNumberService {

	/**
	 * this method is used to get contact number details based on customer name
	 * 
	 * @param contactName
	 *            based on customer name customer details will be fetched
	 * @return return the json data in string format
	 * @throws ContactNumberNotFoundException
	 *             throw ContactNumberNotFoundException when contactNumber
	 *             details are not available
	 * @throws	InvalidContactNumberRequestException
	 * 				if contactName is null then it throw InvalidContactNumberRequestException
	 */

	public List<ContactNumber> getContactNum(String contactName)
			throws ContactNumberNotFoundException, InvalidContactNumberRequestException;

	/**
	 * this method is use to get all contact number details
	 * 
	 * @throws ContactNumberNotFoundException
	 *             when contactnumber details not found
	 */
	public List<ContactNumber> getAllContactNum() throws ContactNumberNotFoundException;

}
