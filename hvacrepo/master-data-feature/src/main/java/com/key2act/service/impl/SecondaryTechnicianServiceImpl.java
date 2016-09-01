package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.SecondaryTechnicianDAO;
import com.key2act.pojo.SecondaryTechnician;
import com.key2act.service.InvalidSecondaryTechnicianRequest;
import com.key2act.service.SecondaryTechnicianNotFoundException;
import com.key2act.service.SecondaryTechnicianService;

/**
 * this class contains all methods to get secondary Technician objects
 * @author bizruntime
 *
 */
public class SecondaryTechnicianServiceImpl implements SecondaryTechnicianService {

	private static final Logger logger = LoggerFactory.getLogger(SecondaryTechnicianServiceImpl.class);
	
	/**
	 * this method is used to get secondary technician
	 * @param secondaryTechnicianType
	 * 				based on secondaryTechnicianType, SecondaryTechnician data will be fetched
	 * @return
	 * 			return the list of SecondaryTechnician objects
	 * @throws SecondaryTechnicianNotFoundException
	 * 				when secondary technician is not found the it throw SecondaryTechnicianNotFoundException
	 * @throws InvalidSecondaryTechnicianRequest
	 * 				when secondaryTechnicianType request is invalid then it throw InvalidSecondaryTechnicianRequest
	 */
	public List<SecondaryTechnician> getSecondaryTechnician(String secondaryTechnicianType) throws SecondaryTechnicianNotFoundException, InvalidSecondaryTechnicianRequest {
		logger.debug("Inside getPrimaryTechnician of SecondaryTechnicianServiceImpl");
		
		if(secondaryTechnicianType == null)
			throw new InvalidSecondaryTechnicianRequest("Invalid Secondary Technician");
		
		SecondaryTechnicianDAO technicianDao = new SecondaryTechnicianDAO();
		List<SecondaryTechnician> secondaryTechnicians = technicianDao.getSecondaryTechnician(secondaryTechnicianType);
		return secondaryTechnicians;
	}
	
	/**
	 * this method is used to get secondary technician
	 * @return
	 * 			return the list of SecondaryTechnician objects
	 * @throws SecondaryTechnicianNotFoundException
	 * 				when secondary technician is not found the it throw SecondaryTechnicianNotFoundException
	 */
	@Override
	public List<SecondaryTechnician> getAllSecondaryTechnician() throws SecondaryTechnicianNotFoundException {
		logger.debug("Inside getAllSecondaryTechnician of SecondaryTechnicianServiceImpl");
		SecondaryTechnicianDAO technicianDao = new SecondaryTechnicianDAO();
		List<SecondaryTechnician> secondaryTechnicians = technicianDao.getAllSecondaryTechnician();
		return secondaryTechnicians;
	}

}
