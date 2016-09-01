package com.key2act.service;

import java.util.List;

import com.key2act.pojo.PrimaryTechnician;

/**
 * this interface contains all service methods to get primary technician data
 * 
 * @author bizruntime
 *
 */
public interface PrimaryTechnicianService {

	/**
	 * this method is used to get primary technicians detail
	 * 
	 * @param primaryTechnicianType
	 *            based on primaryTechnicianType fields PrimaryTechnician data
	 *            will be fetched
	 * @return return the list of primary technician objects
	 * @throws PrimaryTechnicianNotFoundException,
	 *             If primary technician is not found then it throw
	 *             PrimaryTechnicianNotFoundException
	 * @throws InvalidPrimaryTechnicianRequest,
	 *             If primaryTechnicianType request is invalid then it throw
	 *             InvalidPrimaryTechnicianRequest
	 */
	public List<PrimaryTechnician> getPrimaryTechnician(String primaryTechnicianType)
			throws PrimaryTechnicianNotFoundException, InvalidPrimaryTechnicianRequest;

	/**
	 * this method is used to get primary technicians detail
	 * 
	 * @return return the list of primary technician objects
	 * @throws PrimaryTechnicianNotFoundException,
	 *             If primary technician is not found then it throw
	 *             PrimaryTechnicianNotFoundException
	 */
	public List<PrimaryTechnician> getAllPrimaryTechnician() throws PrimaryTechnicianNotFoundException;

}
