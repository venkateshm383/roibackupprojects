package com.key2act.service;

import java.util.List;

import com.key2act.pojo.SecondaryTechnician;

/**
 * this interface contains all methods to get secondary Technician objects
 * @author bizruntime
 *
 */
public interface SecondaryTechnicianService {
	
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
	public List<SecondaryTechnician> getSecondaryTechnician(String secondaryTechnicianType) throws SecondaryTechnicianNotFoundException, InvalidSecondaryTechnicianRequest;
	
	/**
	 * this method is used to get secondary technician
	 * @return
	 * 			return the list of SecondaryTechnician objects
	 * @throws SecondaryTechnicianNotFoundException
	 * 				when secondary technician is not found the it throw SecondaryTechnicianNotFoundException
	 */
	public List<SecondaryTechnician> getAllSecondaryTechnician() throws SecondaryTechnicianNotFoundException;
}
