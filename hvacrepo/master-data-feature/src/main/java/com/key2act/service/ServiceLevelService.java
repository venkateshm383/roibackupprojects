package com.key2act.service;

import java.util.List;

import com.key2act.pojo.ServiceLevel;

public interface ServiceLevelService {

	/**
	 * this method is used to get ServiceLevelService details based on customer
	 * name
	 * 
	 * @param serviceLevelName
	 *            based on ServiceLevelService details will be fetched
	 * @return return the json data in string format
	 * @throws ServiceLevelNotFoundException
	 *             throw ServiceLevelNotFoundException when serviceLevel details
	 *             are not available
	 * @throws InvalidServiceLevelRequestException
	 *             if serviceLevelName is null then it throw
	 *             InvalidServiceLevelRequestException
	 */

	public List<ServiceLevel> getServiceLevel(String serviceLevelName)
			throws ServiceLevelNotFoundException, InvalidServiceLevelRequestException;

	/**
	 * this method is use to get all ServiceLevel details
	 * 
	 * @throws ServiceLevelNotFoundException:when
	 *             Service Level not found
	 * 
	 */
	public List<ServiceLevel> getAllServiceLevel() throws ServiceLevelNotFoundException;

}
