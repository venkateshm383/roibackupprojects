package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.ServiceArea;
import com.key2act.service.InvalidServiceAreaRequest;
import com.key2act.service.ServiceAreaNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class is used to perform dao operation of Service Area
 * 
 * @author bizruntime
 *
 */
public class ServiceAreaDAO {

	private final static Logger logger = LoggerFactory.getLogger(ServiceAreaDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();

	/**
	 * this method is used to get service area
	 * 
	 * @param area
	 *            based on area, ServiceArea details will be fetched
	 * @return this method will return the list of ServiceArea objects
	 * @throws ServiceAreaNotFoundException, If Service area is not found then It throw ServiceAreaNotFoundException
	 * @throws InvalidServiceAreaRequest, If request is invalid then it throw InvalidServiceAreaRequest
	 */
	public List<ServiceArea> getServiceArea(String area)
			throws ServiceAreaNotFoundException, InvalidServiceAreaRequest {
		logger.debug("Inside getServiceArea method of ServiceAreaDao");
		if (area == null)
			throw new InvalidServiceAreaRequest("Invalid Service Area Request");
		List<ServiceArea> serviceAreasData = getServiceAreaData();
		List<ServiceArea> resultServiceAreas = new ArrayList<ServiceArea>();
		for (ServiceArea serviceArea : serviceAreasData) {
			if (serviceArea.getServiceAreaName().equalsIgnoreCase(area.trim()))
				resultServiceAreas.add(serviceArea);
		}
		if (resultServiceAreas.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.SERVICE_AREA_ERROR_CODE) + area);
			throw new ServiceAreaNotFoundException("Error Code:" + ErrorCodeConstant.SERVICE_AREA_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.SERVICE_AREA_ERROR_CODE) + area);
		}
		return resultServiceAreas;
	}// end of method

	/**
	 * this method is used to get service area
	 * 
	 * @return this method will return the list of ServiceArea objects
	 * @throws ServiceAreaNotFoundException, If Service area is not found then It throw ServiceAreaNotFoundException
	 */
	public List<ServiceArea> getAllServiceArea() throws ServiceAreaNotFoundException {
		logger.debug("Inside getAllServiceArea method of ServiceAreaDao");
		List<ServiceArea> resultServiceAreas = getServiceAreaData();
		if (resultServiceAreas.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.SERVICE_AREA_ERROR_CODE));
			throw new ServiceAreaNotFoundException("Error Code:" + ErrorCodeConstant.SERVICE_AREA_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.SERVICE_AREA_ERROR_CODE));
		}
		return resultServiceAreas;
	}// end of method

	/**
	 * this method contains dummy data of service area, once we will use
	 * database then we will fetched data from db
	 * 
	 * @return, it return the list of SericeArea objects values
	 */
	public List<ServiceArea> getServiceAreaData() {
		List<ServiceArea> areas = new ArrayList<ServiceArea>();
		ServiceArea area = new ServiceArea("501", "Bangalore");
		areas.add(area);
		area = new ServiceArea("502", "Hyderabad");
		areas.add(area);
		area = new ServiceArea("503", "Bangalore");
		areas.add(area);
		area = new ServiceArea("504", "Bangalore");
		areas.add(area);
		area = new ServiceArea("505", "Hyderabad");
		areas.add(area);
		return areas;
	}// end of method

}
