package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.ServiceCall;
import com.key2act.service.InvalidServiceCallRequest;
import com.key2act.service.ServiceCallNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class contains all dao methods to get sercice call
 * @author bizruntime
 *
 */
public class ServiceCallDAO {

	private final static Logger logger = LoggerFactory.getLogger(ServiceCallDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();

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
		logger.debug("Inside getServiceCall method of ServiceCallDao");
		
		if(serviceName == null)
			throw new InvalidServiceCallRequest("Invalid Service Call Request");
		
		List<ServiceCall> resultServiceCall = new ArrayList<ServiceCall>();
		List<ServiceCall> getList = getSalesCallData();
		for (ServiceCall service : getList) {
			if (service.getServiceCallName().equalsIgnoreCase(serviceName.trim()))
				resultServiceCall.add(service);
		}
		if (resultServiceCall.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.SERVICE_CALL_ERROR_CODE) + serviceName);
			throw new ServiceCallNotFoundException("Error Code:" + ErrorCodeConstant.SERVICE_CALL_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.SERVICE_CALL_ERROR_CODE) + serviceName);
		} else
			return resultServiceCall;
	}//end of method

	/**
	 * this method is used to get service call object
	 * @return
	 * 			return the list of ServiceCall objects
	 * @throws ServiceCallNotFoundException
	 * 				throw exception when service call not found
	 */
	public List<ServiceCall> getAllServiceCall() throws ServiceCallNotFoundException {
		logger.debug("Inside getServiceCall method of ServiceCallDao");
		List<ServiceCall> resultServiceCall = getSalesCallData();
		
		if (resultServiceCall.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.SERVICE_CALL_ERROR_CODE));
			throw new ServiceCallNotFoundException("Error Code:" + ErrorCodeConstant.SERVICE_CALL_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.SERVICE_CALL_ERROR_CODE));
		} else
			return resultServiceCall;
	}//end of method

	/**
	 * this method contains dummy data of sales call
	 * @return It return the list of hardcoded values, once we used database then we will fetched data from db
	 */
	public List<ServiceCall> getSalesCallData() {
		List<ServiceCall> serviceCallList = new ArrayList<ServiceCall>();
		ServiceCall serviceCall = new ServiceCall("101", "xyz");
		serviceCallList.add(serviceCall);
		serviceCall = new ServiceCall("102", "abc");
		serviceCallList.add(serviceCall);
		serviceCall = new ServiceCall("103", "xyz");
		serviceCallList.add(serviceCall);
		serviceCall = new ServiceCall("104", "abc");
		serviceCallList.add(serviceCall);
		serviceCall = new ServiceCall("105", "xyz");
		serviceCallList.add(serviceCall);
		return serviceCallList;
	}//end of method

}
