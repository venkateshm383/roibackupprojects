package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.LocalTaxDAO;
import com.key2act.pojo.LocalTax;
import com.key2act.service.InvalidLocalTaxRequestException;
import com.key2act.service.LocalTaxNotFoundException;
import com.key2act.service.LocalTaxService;

/**
 * this class is used to perform service methods for Local Tax
 * 
 * @author bizruntime
 *
 */
public class LocalTaxServImpl implements LocalTaxService {

	private static final Logger logger = LoggerFactory.getLogger(LocalTaxServImpl.class);

	/**
	 * this method is used to get LocalTaxId
	 * 
	 * @param localTaxName
	 *            based on localTaxName, list of LocalTaxId objects will be
	 *            fetched
	 * @return It will return the list of localTax objects
	 * @throws LocalTaxNotFoundException
	 *             if localTaxName is not found then it throw
	 *             LocalTaxNotFoundException
	 * @throws InvalidLocalTaxRequestException
	 *             If localTaxName is invalid request then it throw
	 *             InvalidLocalTaxRequestException
	 */

	public List<LocalTax> getLocalTaxId(String localTaxName)
			throws LocalTaxNotFoundException, InvalidLocalTaxRequestException {
		if (localTaxName == null)
			throw new InvalidLocalTaxRequestException("Invalid LocalTax request");

		LocalTaxDAO localTaxDAO = new LocalTaxDAO();
		List<LocalTax> localtaxList = localTaxDAO.getLocalTaxId(localTaxName);
		return localtaxList;
	}

	/**
	 * this method is used to get all localTaxId
	 * 
	 * @return It will return the list of localTaxId objects
	 * @throws LocalTaxNotFoundException
	 *             if localTaxName is not found then it throw
	 *             LocalTaxNotFoundException
	 */
	public List<LocalTax> getAllLocalTaxId() throws LocalTaxNotFoundException {

		logger.debug("Inside getAllLocalTaxId of LocalTaxServImpl");
		LocalTaxDAO localTaxDAO = new LocalTaxDAO();
		List<LocalTax> localtaxList = localTaxDAO.getAllLocalTaxId();
		return localtaxList;
	}

}
