package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.Technician;
import com.key2act.service.InvalidTechnicianRequestException;
import com.key2act.service.TechnicianNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class is used to perform all dao operation of technician
 * 
 * @author bizruntime
 * 
 */
public class TechnicianDAO {

	private static final Logger logger = LoggerFactory.getLogger(TechnicianDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();

	/**
	 * this method is used to get technicianId
	 * 
	 * @param technicianName
	 *            based on technicianName, list of technicianId objects will be
	 *            fetched
	 * @return It will return the list of technicianId objects
	 * @throws TechnicianNotFoundException
	 *             if technicianName is not found then it throw
	 *             TechnicianIdNotFoundException
	 * @throws InvalidTechnicianRequestException
	 *             If technicianName is invalid request then it throw
	 *             InvalidTechnicianIdRequestException
	 */
	public List<Technician> getTechnicianId(String technicianName)
			throws TechnicianNotFoundException, InvalidTechnicianRequestException {
		logger.debug("Inside getTechnicianId of TechnicianDao");
		if (technicianName == null)
			throw new InvalidTechnicianRequestException("Invalid TechnicianId Request");
		List<Technician> techLists = getTechnicianData();
		List<Technician> technicianList = new ArrayList<Technician>();
		logger.debug("getTechnicianId of TechnicianDao : " + technicianName);
		for (Technician tech : techLists) {
			if (tech.gettechnicianId().equalsIgnoreCase(technicianName.trim()))
				technicianList.add(tech);

		}
		if (technicianList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.TECHNICIAN_CODE));

			throw new TechnicianNotFoundException("Error Code:" + ErrorCodeConstant.TECHNICIAN_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.TECHNICIAN_CODE) + technicianName);
		}
		return technicianList;
	}

	/**
	 * this method is used to get all technicianId
	 * 
	 * @return It will return the list of technicianId objects
	 * @throws TechnicianNotFoundException
	 *             if technicianName is not found then it throw
	 *             TechnicianIdNotFoundException
	 */
	public List<Technician> getAllTechnician() throws TechnicianNotFoundException {
		logger.debug("Inside getAllTechnician of TechnicianDao");
		List<Technician> technicianList = getTechnicianData();
		logger.debug("getAllTechnician of TechnicianDao : ");
		if (technicianList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.TECHNICIAN_CODE));

			throw new TechnicianNotFoundException("Error Code:" + ErrorCodeConstant.TECHNICIAN_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.TECHNICIAN_CODE));
		}
		return technicianList;
	}

	/**
	 * this method contains technicianId dummy data, once we used database then
	 * we will fetched data from db
	 * 
	 * @return It return the list of hardcoded values
	 */
	public List<Technician> getTechnicianData() {
		List<Technician> technicianList = new ArrayList<Technician>();
		Technician tech = null;
		tech = new Technician("1", "Electrical Technician");
		technicianList.add(tech);
		tech = new Technician("2", "Server Technician");
		technicianList.add(tech);
		return technicianList;
	}
}
