package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.ContactNumber;
import com.key2act.service.ContactNumberNotFoundException;
import com.key2act.service.InvalidContactNumberRequestException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class ContactNumberDAO {

	private static final Logger logger = LoggerFactory.getLogger(ContactNumberDAO.class);
	private static Properties prop=PropertyFileLoader.getProperties();

	/** 
	 * this method is used to get contactNumber
	 * @param contactName
	 * 					based on contactName, list of contactNumber objects will be fetched 
	 * @return
	 * 			It will return the list of contactnumberList objects
	 * @throws ContactNumberNotFoundException	if contactName is not found then it throw ContactNumberNotFoundException
	 * @throws InvalidContactNumberRequestException 	If contactName is invalid request then it throw InvalidContactNumberRequestException
	 */
	public List<ContactNumber> getContactNum(String contactName)throws ContactNumberNotFoundException,InvalidContactNumberRequestException  {
		
		logger.debug("Inside getContactNum of ContactNumberDao");
		if(contactName==null)
			throw new InvalidContactNumberRequestException("Invalid contact number request");
		List<ContactNumber>cntctLists = getContactData();
		List<ContactNumber> contactnumberList = new ArrayList<ContactNumber>();
		logger.debug("getContactNumber of  ContactNumberDao : " + contactName);
		for (ContactNumber cntct : cntctLists) {
			if (cntct.getcontactNum() .equalsIgnoreCase(contactName.trim()))
				contactnumberList.add(cntct);
			
		}
		if (contactnumberList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.CONTACTNUMBER_ERROR_CODE ) + contactName);
			throw new ContactNumberNotFoundException("Error Code:" + ErrorCodeConstant.CONTACTNUMBER_ERROR_CODE + ", " + prop.getProperty(ErrorCodeConstant.CONTACTNUMBER_ERROR_CODE) + contactName);
		}
		return contactnumberList;
	}
	
	/** 
	 * this method is used to get all contactNumber
	 * @return
	 * 			It will return the list of contactnumberList objects
	 * @throws ContactNumberNotFoundException	if contactName is not found then it throw ContactNumberNotFoundException
	 */
	public List<ContactNumber> getAllContactNum()throws ContactNumberNotFoundException  {
		logger.debug("Inside getAllContactNum of ContactNumberDao" );
		List<ContactNumber>contactnumberList = getContactData();
		logger.debug("getContactNumber of  ContactNumberDao : ");
		
		if (contactnumberList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.CONTACTNUMBER_ERROR_CODE));
			throw new ContactNumberNotFoundException("Error Code:" + ErrorCodeConstant.CONTACTNUMBER_ERROR_CODE + ", " + prop.getProperty(ErrorCodeConstant.CONTACTNUMBER_ERROR_CODE));
		}
		return contactnumberList;
	}
	
	/**
	 * this method contains contactNumber dummy data, once we used database then we will fetched data from db
	 * @return It return the list of hardcoded values 
	 */
	public List<ContactNumber> getContactData() {
		List<ContactNumber> contactnumberList = new ArrayList<ContactNumber>();
		ContactNumber cntct = null;
		cntct = new ContactNumber("9223146789", "Adam wruth");
		contactnumberList.add(cntct);
		cntct = new ContactNumber("7743569231", "John paul John");
		contactnumberList.add(cntct);
		return contactnumberList;
	}
}