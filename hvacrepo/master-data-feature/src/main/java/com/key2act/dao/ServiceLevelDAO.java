package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.ServiceLevel;
import com.key2act.service.InvalidServiceLevelRequestException;
import com.key2act.service.ServiceLevelNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class is used to perform all dao operation of service level
 * @author bizruntime
 * 
 */
public class ServiceLevelDAO {

	private static final Logger logger = LoggerFactory.getLogger(ServiceLevelDAO.class);
	private static Properties prop=PropertyFileLoader.getProperties();

	/** 
	 * this method is used to get serviceLevelId
	 * @param serviceLevelName
	 * 					based on serviceLevelName, list of serviceLevelId objects will be fetched 
	 * @return
	 * 			It will return the list of serviceLevelId objects
	 * @throws ServiceLevelNotFoundException	if serviceLevelName is not found then it throw ServiceLevelNotFoundException
	 * @throws InvalidServiceLevelRequestException 	If serviceLevelName is invalid request then it throw InvalidServiceLevelIdRequestException
	 */
	public List<ServiceLevel> getServiceLevel(String serviceLevelName)throws ServiceLevelNotFoundException,InvalidServiceLevelRequestException {
		logger.debug("Inside getServiceLevel of ServiceDao");
		if(serviceLevelName==null)
			throw new InvalidServiceLevelRequestException("Invalid servicelevel request");
			
		List<ServiceLevel> servLists = getServiceLevelData();
		List<ServiceLevel> serviceLevelList = new ArrayList<ServiceLevel>();
		logger.debug("getServiceLevel of ServiceDao : " + serviceLevelName);
		for (ServiceLevel serv : servLists) {
			if (serv.getserviceId() .equalsIgnoreCase(serviceLevelName.trim()))
				serviceLevelList.add(serv);
			else if (serv.getserviceLevelName() .equalsIgnoreCase(serviceLevelName.trim()))
				serviceLevelList.add(serv);
		}
		if (serviceLevelList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.  PROBLEMTYPE_ERROR_CODE ) + serviceLevelName);
			throw new ServiceLevelNotFoundException("Error Code:" + ErrorCodeConstant.SERVICELEVELID_ERROR_CODE + ", " + prop.getProperty(ErrorCodeConstant.SERVICELEVELID_ERROR_CODE) + serviceLevelName);
		}
		return serviceLevelList;
	}
	

	/** 
	 * this method is used to get all serviceLevelId
	 * @return
	 * 			It will return the list of serviceLevelId objects
	 * @throws ServiceLevelNotFoundException	if serviceLevelName is not found then it throw ServiceLevelNotFoundException
	 */
	
	public List<ServiceLevel> getAllServiceLevel()throws ServiceLevelNotFoundException {
		logger.debug("Inside getAllServiceLevel of ServiceDao");
		List<ServiceLevel> serviceLevelList = getServiceLevelData();
		if (serviceLevelList.isEmpty()) {
			 logger.debug(prop.getProperty(ErrorCodeConstant.SERVICELEVELID_ERROR_CODE));

				throw new ServiceLevelNotFoundException("Error Code:" + ErrorCodeConstant.SERVICELEVELID_ERROR_CODE + ", " + prop.getProperty(ErrorCodeConstant.SERVICELEVELID_ERROR_CODE));
		}
		return serviceLevelList;
	}
	/**
	 * this method contains serviceLevelId dummy data, once we used database then we will fetched data from db
	 * @return It return the list of hardcoded values 
	 */

	public List<ServiceLevel>getServiceLevelData() {
		
		List<ServiceLevel> serviceLevelList = new ArrayList<ServiceLevel>();
		ServiceLevel serv = null;
		serv = new ServiceLevel("1", "DataBase Maintainance");
		serviceLevelList.add(serv);
		serv = new ServiceLevel("2", "Server Maintainance");
		serviceLevelList.add(serv);
		return serviceLevelList;
	}

}	
	
	
