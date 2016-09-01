package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.DivisionDAO;
import com.key2act.pojo.Division;
import com.key2act.service.DivisionNotFoundException;
import com.key2act.service.DivisionService;
import com.key2act.service.InvalidDivisionRequestException;

/**
 * this class contains all service methods to get division data
 * @author bizruntime
 *
 */
public class DivisionServiceImpl implements DivisionService {

	private static final Logger logger = LoggerFactory.getLogger(DivisionServiceImpl.class);
	
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
		logger.debug("Inside getDivision of DivisionServiceImpl");
		
		if(divisionName == null) 
			throw new InvalidDivisionRequestException("Invalid Division Request");
		
		DivisionDAO divisionDao = new DivisionDAO();
		List<Division> divisions = divisionDao.getDivision(divisionName);
		return divisions;
	}
	
	/** 
	 * this method is used to get division
	 * @param divisionName
	 * 					based on divisionName, list of division objects will be fetched 
	 * @return
	 * 			It will return the list of division objects
	 * @throws DivisionNotFoundException	if Division is not found then it throw DivisionNotFoundException
	 */
	@Override
	public List<Division> getAllDivision() throws DivisionNotFoundException {
		logger.debug("Inside getAllDivision of DivisionServiceImpl");
		DivisionDAO divisionDao = new DivisionDAO();
		List<Division> divisions = divisionDao.getAllDivision();
		return divisions;
	}

}
