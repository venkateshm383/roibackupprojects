package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.ContactNumberDAO;
import com.key2act.pojo.ContactNumber;
import com.key2act.service.ContactNumberNotFoundException;
import com.key2act.service.ContactNumberService;
import com.key2act.service.InvalidContactNumberRequestException;

/**
 * this class is used to perform service methods for contact number
 * 
 * @author bizruntime
 *
 */
public class ContactNumberImpl implements ContactNumberService {
	private static final Logger logger = LoggerFactory.getLogger(ContactNumberImpl.class);

	/**
	 * this method is used to get contactNumber
	 * 
	 * @param contactName
	 *            based on contactName, list of contactNumber objects will be
	 *            fetched
	 * @return It will return the list of contactnumberList objects
	 * @throws ContactNumberNotFoundException
	 *             if contactName is not found then it throw
	 *             ContactNumberNotFoundException
	 * @throws InvalidContactNumberRequestException
	 *             If contactName is invalid request then it throw
	 *             InvalidContactNumberRequestException
	 */
	public List<ContactNumber> getContactNum(String cntNum)
			throws ContactNumberNotFoundException, InvalidContactNumberRequestException {
		if (cntNum == null)
			throw new InvalidContactNumberRequestException("Invalid ContactNumber request");
		ContactNumberDAO contactnumberDAO = new ContactNumberDAO();
		List<ContactNumber> contactnumberList = contactnumberDAO.getContactNum(cntNum);
		return contactnumberList;
	}

	/**
	 * this method is used to get all contactNumber
	 * 
	 * @return It will return the list of contactnumberList objects
	 * @throws ContactNumberNotFoundException
	 *             if contactName is not found then it throw
	 *             ContactNumberNotFoundException
	 */

	@Override
	public List<ContactNumber> getAllContactNum() throws ContactNumberNotFoundException {

		logger.debug("Inside getAllContactNum of CallStatusServiceImpl");
		ContactNumberDAO contactnumberDAO = new ContactNumberDAO();
		List<ContactNumber> contactnumberList = contactnumberDAO.getAllContactNum();
		return contactnumberList;
	}
}
