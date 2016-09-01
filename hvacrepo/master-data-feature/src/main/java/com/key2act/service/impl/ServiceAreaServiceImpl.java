package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.ServiceAreaDAO;
import com.key2act.pojo.ServiceArea;
import com.key2act.service.InvalidServiceAreaRequest;
import com.key2act.service.ServiceAreaNotFoundException;
import com.key2act.service.ServiceAreaService;

/**
 * this class is used to perform service methods for Service Area
 * @author bizruntime
 *
 */
public class ServiceAreaServiceImpl implements ServiceAreaService {

	private static final Logger logger = LoggerFactory.getLogger(ServiceAreaServiceImpl.class);

	/**
	 * this method is used to get service area
	 * @param area
	 * 			based on area, ServiceArea details will be fetched 
	 * @return
	 * 			this method will return the list of ServiceArea objects
	 * @throws ServiceAreaNotFoundException, If Service area is not found then It throw ServiceAreaNotFoundException
	 * @throws InvalidServiceAreaRequest, If request is invalid then it throw InvalidServiceAreaRequest
	 */
	public List<ServiceArea> getServiceArea(String area) throws ServiceAreaNotFoundException, InvalidServiceAreaRequest {
		logger.debug("Inside getServiceArea of ServiceAreaServiceImpl");
		if(area == null)
			throw new InvalidServiceAreaRequest("Invalid Service Area Request");
		ServiceAreaDAO serviceAreaDao = new ServiceAreaDAO();
		List<ServiceArea> areas = serviceAreaDao.getServiceArea(area);
		return areas;
	}

	/**
	 * this method is used to get service area
	 * @return
	 * 			this method will return the list of ServiceArea objects
	 * @throws ServiceAreaNotFoundException, If Service area is not found then It throw ServiceAreaNotFoundException
	 */
	@Override
	public List<ServiceArea> getAllServiceArea() throws ServiceAreaNotFoundException {
		logger.debug("Inside getAllServiceArea of ServiceAreaServiceImpl");
		ServiceAreaDAO serviceAreaDao = new ServiceAreaDAO();
		List<ServiceArea> areas = serviceAreaDao.getAllServiceArea();
		return areas;
	}

}
