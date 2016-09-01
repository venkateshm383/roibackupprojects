package com.key2act.service;

import java.util.List;

import com.key2act.pojo.ServiceCall;

/**
 * this interface contains all service methods to get sercice call
 * @author bizruntime
 *
 */
public interface ServiceCallService {
	
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
	public List<ServiceCall> getServiceCall(String serviceName) throws ServiceCallNotFoundException, InvalidServiceCallRequest ;
	
	/**
	 * this method is used to get service call object
	 * @return
	 * 			return the list of ServiceCall objects
	 * @throws ServiceCallNotFoundException
	 * 				throw exception when service call not found
	 */
	public List<ServiceCall> getAllServiceCall() throws ServiceCallNotFoundException;

}
