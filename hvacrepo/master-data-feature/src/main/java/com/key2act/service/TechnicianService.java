package com.key2act.service;

import java.util.List;

import com.key2act.pojo.Technician;

public interface TechnicianService {

	/**
	 * this method is used to get TechnicianIdService details based on customer
	 * name
	 * 
	 * @param technicianName
	 *            based on TechnicianIdService details will be fetched
	 * @return return the json data in string format
	 * @throws TechnicianNotFoundException
	 *             throw TechnicianIdNotFoundException when technician details
	 *             are not available
	 * @throws InvalidTechnicianRequestException
	 *             if technicianName is null then it throw
	 *             InvalidTechnicianRequestException
	 */

	public List<Technician> getTechnicianId(String technicianName)
			throws TechnicianNotFoundException, InvalidTechnicianRequestException;

	/**
	 * this method is use to get all TechnicianId details
	 * 
	 * @throws TechnicianNotFoundException:when
	 *             Technician Id not found
	 */

	public List<Technician> getAllTechnicianId() throws TechnicianNotFoundException;

}
