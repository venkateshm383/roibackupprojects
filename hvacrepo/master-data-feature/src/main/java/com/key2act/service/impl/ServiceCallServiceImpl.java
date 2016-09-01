package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.ServiceCallDAO;
import com.key2act.pojo.ServiceCall;
import com.key2act.service.InvalidServiceCallRequest;
import com.key2act.service.ServiceCallNotFoundException;
import com.key2act.service.ServiceCallService;

/**
 * this class contains all service methods to get sercice call
 * @author bizruntime
 *
 */
public class ServiceCallServiceImpl implements ServiceCallService {

	private static final Logger logger = LoggerFactory.getLogger(ServiceCallServiceImpl.class);
	
	/**
	 * this method is used to get service call object
	 * @param serviceName
	 * 			based on serviceName list of ServiceCall objects will be fetched
	 * @return
	 * 			return the list of ServiceCall objects
	 * @throws ServiceCallNotFoundException
	 * 							throw exception when service call not found
	 * @throws InvalidServiceCallRequest
	 * 							throw exception when request is invalid
	 */
	public List<ServiceCall> getServiceCall(String serviceName) throws ServiceCallNotFoundException, InvalidServiceCallRequest {
		logger.debug("Inside getServiceCall method of ServiceCallServiceImpl");
		if(serviceName == null)
			throw new InvalidServiceCallRequest("Invalid Service Call Request");
		ServiceCallDAO serviceCallDao = new ServiceCallDAO();
		List<ServiceCall> serviceCallList = serviceCallDao.getServiceCall(serviceName);
		return serviceCallList;
	}
	
	/**
	 * this method is used to get service call object
	 * @return
	 * 			return the list of ServiceCall objects
	 * @throws ServiceCallNotFoundException
	 * 				throw exception when service call not found
	 */
	@Override
	public List<ServiceCall> getAllServiceCall() throws ServiceCallNotFoundException {
		logger.debug("Inside getAllServiceCall method of ServiceCallServiceImpl");
		ServiceCallDAO serviceCallDao = new ServiceCallDAO();
		List<ServiceCall> serviceCallList = serviceCallDao.getAllServiceCall();
		return serviceCallList;
	}
	
}
