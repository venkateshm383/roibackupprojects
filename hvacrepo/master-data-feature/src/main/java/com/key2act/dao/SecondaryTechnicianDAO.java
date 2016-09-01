package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.SecondaryTechnician;
import com.key2act.service.InvalidSecondaryTechnicianRequest;
import com.key2act.service.SecondaryTechnicianNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class is used to perform dao operation of secondary technician
 * 
 * @author bizruntime
 *
 */
public class SecondaryTechnicianDAO {

	private final Logger logger = LoggerFactory.getLogger(SecondaryTechnicianDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();

	/**
	 * this method is used to get secondary technician
	 * 
	 * @param secondaryTechnicianType
	 *            based on secondaryTechnicianType, SecondaryTechnician data
	 *            will be fetched
	 * @return return the list of SecondaryTechnician objects
	 * @throws SecondaryTechnicianNotFoundException
	 *             when secondary technician is not found the it throw
	 *             SecondaryTechnicianNotFoundException
	 * @throws InvalidSecondaryTechnicianRequest
	 *             when secondaryTechnicianType request is invalid then it throw
	 *             InvalidSecondaryTechnicianRequest
	 */
	public List<SecondaryTechnician> getSecondaryTechnician(String secondaryTechnicianType)
			throws SecondaryTechnicianNotFoundException, InvalidSecondaryTechnicianRequest {

		if (secondaryTechnicianType == null)
			throw new InvalidSecondaryTechnicianRequest("Invalid Secondary Technician");

		List<SecondaryTechnician> primaryTechnicians = getSecondaryTechnicianData();
		List<SecondaryTechnician> resultTechnicians = new ArrayList<SecondaryTechnician>();
		for (SecondaryTechnician primaryTechnician : primaryTechnicians) {
			if (primaryTechnician.getSecondayTechnicianType().equalsIgnoreCase(secondaryTechnicianType.trim()))
				resultTechnicians.add(primaryTechnician);
		}
		if (resultTechnicians.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.SECONDARY_TECHNICIAN_ERROR_CODE) + secondaryTechnicianType);
			throw new SecondaryTechnicianNotFoundException("Error Code:"
					+ ErrorCodeConstant.SECONDARY_TECHNICIAN_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.SECONDARY_TECHNICIAN_ERROR_CODE) + secondaryTechnicianType);
		}
		return resultTechnicians;
	}

	/**
	 * this method is used to get secondary technician
	 * 
	 * @return return the list of SecondaryTechnician objects
	 * @throws SecondaryTechnicianNotFoundException
	 *             when secondary technician is not found the it throw
	 *             SecondaryTechnicianNotFoundException
	 */
	public List<SecondaryTechnician> getAllSecondaryTechnician() throws SecondaryTechnicianNotFoundException {
		List<SecondaryTechnician> resultTechnicians = getSecondaryTechnicianData();
		if (resultTechnicians.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.SECONDARY_TECHNICIAN_ERROR_CODE));
			throw new SecondaryTechnicianNotFoundException(
					"Error Code:" + ErrorCodeConstant.SECONDARY_TECHNICIAN_ERROR_CODE + ", "
							+ prop.getProperty(ErrorCodeConstant.SECONDARY_TECHNICIAN_ERROR_CODE));
		}
		return resultTechnicians;
	}// end of method

	/**
	 * this method is contains secondary technician dummy data, once we use
	 * database then we will fetch data from db
	 * 
	 * @return, it return the SecondaryTechnician hardcoded values
	 */
	public List<SecondaryTechnician> getSecondaryTechnicianData() {
		List<SecondaryTechnician> secondaryTechnicians = new ArrayList<SecondaryTechnician>();
		SecondaryTechnician secondaryTechnician = new SecondaryTechnician("701", "Electrician", "John");
		secondaryTechnicians.add(secondaryTechnician);
		secondaryTechnician = new SecondaryTechnician("702", "Electrician", "David");
		secondaryTechnicians.add(secondaryTechnician);
		secondaryTechnician = new SecondaryTechnician("703", "Civil", "John");
		secondaryTechnicians.add(secondaryTechnician);
		secondaryTechnician = new SecondaryTechnician("704", "Civil", "Robin");
		secondaryTechnicians.add(secondaryTechnician);
		secondaryTechnician = new SecondaryTechnician("705", "Electrician", "Jack");
		secondaryTechnicians.add(secondaryTechnician);
		return secondaryTechnicians;
	}// end of method

}
