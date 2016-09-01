package com.key2act.service;

import java.util.List;

import com.key2act.pojo.LocalTax;

public interface LocalTaxService {

	/**
	 * this method is used to get LocalTaxService details based on customer name
	 * 
	 * @param localTaxName
	 *            based on localTaxName details will be fetched
	 * @return return the json data in string format
	 * @throws LocalTaxNotFoundException
	 *             throw LocalTaxNotFoundException when localTax details are not
	 *             available
	 * @throws InvalidLocalTaxRequestException
	 * 				if localTaxName is null then it throw InvalidLocalTaxRequestException
	 */
	public List<LocalTax> getLocalTaxId(String localTaxName)
			throws LocalTaxNotFoundException, InvalidLocalTaxRequestException;

	/**
	 * this method is use to get all local tax details
	 * 
	 * @throws LocalTaxNotFoundException:when
	 *             LocalTax details not found
	 * 
	 */
	public List<LocalTax> getAllLocalTaxId() throws LocalTaxNotFoundException;

}
