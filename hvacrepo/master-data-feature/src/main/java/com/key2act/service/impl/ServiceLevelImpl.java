package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.ServiceLevelDAO;
import com.key2act.pojo.ServiceLevel;
import com.key2act.service.InvalidServiceLevelRequestException;
import com.key2act.service.ServiceLevelService;
import com.key2act.service.ServiceLevelNotFoundException;

/**
 * this class is used to perform service methods for Service Level
 * @author bizruntime
 *
 */
public class ServiceLevelImpl implements ServiceLevelService {

	private static final Logger logger = LoggerFactory.getLogger(ServiceLevelImpl.class);
	
	/** 
	 * this method is used to get serviceLevelId
	 * @param serviceLevelName
	 * 					based on serviceLevelName, list of serviceLevelId objects will be fetched 
	 * @return
	 * 			It will return the list of serviceLevelId objects
	 * @throws ServiceLevelNotFoundException	if serviceLevelName is not found then it throw ServiceLevelNotFoundException
	 * @throws InvalidServiceLevelRequestException 	If serviceLevelName is invalid request then it throw InvalidServiceLevelIdRequestException
	 */
	

	public List<ServiceLevel> getServiceLevel(String serviceLevelName)
			throws ServiceLevelNotFoundException, InvalidServiceLevelRequestException {
		if (serviceLevelName == null) 
			throw new InvalidServiceLevelRequestException("Invalid ServicelEvelId request");
		ServiceLevelDAO serviceLevelDAO=new ServiceLevelDAO();
		List<ServiceLevel> serviceLevelList = serviceLevelDAO.getServiceLevel(serviceLevelName);
		return serviceLevelList;
	}
	/** 
	 * this method is used to get all serviceLevelId
	 * @return
	 * 			It will return the list of serviceLevelId objects
	 * @throws ServiceLevelNotFoundException	if serviceLevelName is not found then it throw ServiceLevelNotFoundException
	 */
	

	public List<ServiceLevel> getAllServiceLevel()throws ServiceLevelNotFoundException {

		logger.debug("Inside getAllServiceLevel of ServiceLevelId");
		ServiceLevelDAO serviceLevelDAO=new ServiceLevelDAO();
		List<ServiceLevel> serviceLevelList = serviceLevelDAO.getAllServiceLevel();
		return serviceLevelList;
	}

}
