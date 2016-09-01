package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.TechnicianDAO;
import com.key2act.pojo.Technician;
import com.key2act.service.InvalidTechnicianRequestException;
import com.key2act.service.TechnicianNotFoundException;
import com.key2act.service.TechnicianService;

/**
 * this class is used to perform service methods for Technician
 * @author bizruntime
 *
 */
public class TechnicianImpl implements TechnicianService {
	private static final Logger logger = LoggerFactory.getLogger(ServiceLevelImpl.class);

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
		if (technicianName == null)
			throw new InvalidTechnicianRequestException("Invalid TechnicianId request");
		TechnicianDAO technicianIdDAO = new TechnicianDAO();
		List<Technician> technicianList = technicianIdDAO.getTechnicianId(technicianName);
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

	@Override
	public List<Technician> getAllTechnicianId() throws TechnicianNotFoundException {
		logger.debug("Inside getAllTechnicianId of TechnicianIdImpl");
		TechnicianDAO technicianIdDAO = new TechnicianDAO();
		List<Technician> technicianList = technicianIdDAO.getAllTechnician();
		return technicianList;

	}
}
