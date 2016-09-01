package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.PrimaryTechnicianDAO;
import com.key2act.pojo.PrimaryTechnician;
import com.key2act.service.InvalidPrimaryTechnicianRequest;
import com.key2act.service.PrimaryTechnicianNotFoundException;
import com.key2act.service.PrimaryTechnicianService;

/**
 * this class contains all service methods to get primary technician data 
 * @author bizruntime
 *
 */
public class PrimaryTechnicianServiceImpl implements PrimaryTechnicianService {

	private static final Logger logger = LoggerFactory.getLogger(PrimaryTechnicianServiceImpl.class);
	
	/**
	 * this method is used to get primary technicians detail
	 * @param primaryTechnicianType
	 * 				based on primaryTechnicianType fields PrimaryTechnician data will be fetched
	 * @return
	 * 			return the list of primary technician objects
	 * @throws PrimaryTechnicianNotFoundException, If primary technician is not found then it throw PrimaryTechnicianNotFoundException
	 * @throws InvalidPrimaryTechnicianRequest, If primaryTechnicianType request is invalid then it throw  InvalidPrimaryTechnicianRequest
	 */
	public List<PrimaryTechnician> getPrimaryTechnician(String primaryTechnicianType) throws PrimaryTechnicianNotFoundException, InvalidPrimaryTechnicianRequest {
		logger.debug("Inside getPrimaryTechnician of PrimaryTechnicianServiceImpl");
		PrimaryTechnicianDAO technicianDao = new PrimaryTechnicianDAO();
		List<PrimaryTechnician> primaryTechnicians = technicianDao.getPrimaryTechnician(primaryTechnicianType);
		return primaryTechnicians;
	}
	
	/**
	 * this method is used to get primary technicians detail
	 * @return
	 * 			return the list of primary technician objects
	 * @throws PrimaryTechnicianNotFoundException, If primary technician is not found then it throw PrimaryTechnicianNotFoundException
	 */
	@Override
	public List<PrimaryTechnician> getAllPrimaryTechnician() throws PrimaryTechnicianNotFoundException {
		logger.debug("Inside getAllPrimaryTechnician of PrimaryTechnicianServiceImpl");
		PrimaryTechnicianDAO technicianDao = new PrimaryTechnicianDAO();
		List<PrimaryTechnician> primaryTechnicians = technicianDao.getAllPrimaryTechnician();
		return primaryTechnicians;
	}

}
