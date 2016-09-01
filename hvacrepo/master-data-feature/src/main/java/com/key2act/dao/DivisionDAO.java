package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.Division;
import com.key2act.service.DivisionNotFoundException;
import com.key2act.service.InvalidDivisionRequestException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class DivisionDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(DivisionDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();
	
	/** 
	 * this method is used to get division
	 * @param divisionName
	 * 					based on divisionName, list of division objects will be fetched 
	 * @return
	 * 			It will return the list of division objects
	 * @throws DivisionNotFoundException	if Division is not found then it throw DivisionNotFoundException
	 * @throws InvalidDivisionRequestException 	If divisionName is invalid request then it throw InvalidDivisionRequestException
	 */
	public List<Division> getDivision(String divisionName) throws DivisionNotFoundException, InvalidDivisionRequestException {
		logger.debug("Inside getDivision of DivisionDao");
		if(divisionName == null) 
			throw new InvalidDivisionRequestException("Invalid Division Request");
		
		List<Division> divisionsData = getDivisionData();
		List<Division> resultDivisions = new ArrayList<Division>();
		for(Division division : divisionsData) {
			if(division.getDivisionName().equalsIgnoreCase(divisionName.trim()))
				resultDivisions.add(division);
		}
		if(resultDivisions.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.DIVISION_ERROR_CODE) + divisionName);
			throw new DivisionNotFoundException("Error Code:" + ErrorCodeConstant.DIVISION_ERROR_CODE + ", " + prop.getProperty(ErrorCodeConstant.DIVISION_ERROR_CODE) + divisionName);
		}
		return resultDivisions;
	}
	
	/** 
	 * this method is used to get division
	 * @param divisionName
	 * 					based on divisionName, list of division objects will be fetched 
	 * @return
	 * 			It will return the list of division objects
	 * @throws DivisionNotFoundException	if Division is not found then it throw DivisionNotFoundException
	 */
	public List<Division> getAllDivision() throws DivisionNotFoundException {
		logger.debug("Inside getDivision of DivisionDao");
		List<Division> resultDivisions = getDivisionData();
		if(resultDivisions.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.DIVISION_ERROR_CODE));
			throw new DivisionNotFoundException("Error Code:" + ErrorCodeConstant.DIVISION_ERROR_CODE + ", " + prop.getProperty(ErrorCodeConstant.DIVISION_ERROR_CODE));
		}
		return resultDivisions;
	} //end of method
	
	/**
	 * this method contains division dummy data, once we used database then we will fetched data from db
	 * @return It return the list of hardcoded values 
	 */
	public List<Division> getDivisionData() {
		List<Division> divisions = new ArrayList<Division>();
		Division division = new Division("1001", "Commercial");
		divisions.add(division);
		division = new Division("1002", "Government");
		divisions.add(division);
		division = new Division("1003", "Health Care");
		divisions.add(division);
		division = new Division("1004", "Education");
		divisions.add(division);
		division = new Division("1005", "Commercial");
		divisions.add(division);
		division = new Division("1006", "Manufacturing");
		divisions.add(division);
		return divisions;
	} // end of method

}
