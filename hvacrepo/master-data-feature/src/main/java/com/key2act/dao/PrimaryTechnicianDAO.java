package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.PrimaryTechnician;
import com.key2act.service.InvalidPrimaryTechnicianRequest;
import com.key2act.service.PrimaryTechnicianNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class is used to perform dao operation of primary technician
 * @author bizruntime
 *
 */
public class PrimaryTechnicianDAO {
	
	private final static Logger logger = LoggerFactory.getLogger(PrimaryTechnicianDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();
	
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
		
		if(primaryTechnicianType == null)
			throw new InvalidPrimaryTechnicianRequest("Invalid Primary Technician Request");
		
		List<PrimaryTechnician> primaryTechnicians = getPrimaryTechnicianData(); 
		List<PrimaryTechnician> resultTechnicians = new ArrayList<PrimaryTechnician>();
		for(PrimaryTechnician primaryTechnician : primaryTechnicians ) {
			if(primaryTechnician.getPrimaryTechnicianType().equalsIgnoreCase(primaryTechnicianType.trim()))
				resultTechnicians.add(primaryTechnician);
		}
		if(resultTechnicians.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.PRIMARY_TECHNICIAN_ERROR_CODE) + primaryTechnicianType);
			throw new PrimaryTechnicianNotFoundException("Error Code:" + ErrorCodeConstant.PRIMARY_TECHNICIAN_ERROR_CODE + ", " + prop.getProperty(ErrorCodeConstant.PRIMARY_TECHNICIAN_ERROR_CODE) + primaryTechnicianType);
		}
		return resultTechnicians;
	}
	
	/**
	 * this method is used to get primary technicians detail
	 * @return
	 * 			return the list of primary technician objects
	 * @throws PrimaryTechnicianNotFoundException, If primary technician is not found then it throw PrimaryTechnicianNotFoundException
	 */
	public List<PrimaryTechnician> getAllPrimaryTechnician() throws PrimaryTechnicianNotFoundException {
		List<PrimaryTechnician> resultPrimaryTechnicians = getPrimaryTechnicianData(); 
		if(resultPrimaryTechnicians.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.PRIMARY_TECHNICIAN_ERROR_CODE));
			throw new PrimaryTechnicianNotFoundException("Error Code:" + ErrorCodeConstant.PRIMARY_TECHNICIAN_ERROR_CODE + ", " + prop.getProperty(ErrorCodeConstant.PRIMARY_TECHNICIAN_ERROR_CODE));
		}
		return resultPrimaryTechnicians;
	}
	
	/**
	 * this method is contains primary technician dummy data, Once we use database then we'll fetch data from db
	 * @return, it return the list of PrimaryTechnician objects data
	 */
	public List<PrimaryTechnician> getPrimaryTechnicianData() {
		logger.debug("Inside getPrimaryTechnicianDummyData of PrimaryTechnicianDummyData");
		List<PrimaryTechnician> primaryTechnicians = new ArrayList<PrimaryTechnician>();
		PrimaryTechnician primaryTechnician = new PrimaryTechnician("601", "Software", "David");
		primaryTechnicians.add(primaryTechnician);
		primaryTechnician = new PrimaryTechnician("602", "Infra", "John");
		primaryTechnicians.add(primaryTechnician);
		primaryTechnician = new PrimaryTechnician("603", "Software", "David");
		primaryTechnicians.add(primaryTechnician);
		primaryTechnician = new PrimaryTechnician("604", "Hardware", "David");
		primaryTechnicians.add(primaryTechnician);
		primaryTechnician = new PrimaryTechnician("605", "Software", "David");
		primaryTechnicians.add(primaryTechnician);
		return primaryTechnicians;
	}//end of method

}
