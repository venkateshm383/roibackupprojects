package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.CallType;
import com.key2act.service.CallTypeNotFoundException;
import com.key2act.service.InvalidCallTypeRequestException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class CallTypeDAO {

	private static final Logger logger = LoggerFactory.getLogger(CallTypeDAO.class);
	private Properties prop = PropertyFileLoader.getProperties();

	/** 
	 * this method is used to get callType
	 * @param callType
	 * 					based on callType, list of callType objects will be fetched 
	 * @return
	 * 			It will return the list of callType objects
	 * @throws CallTypeNotFoundException	if callType is not found then it throw CallTypeNotFoundException
	 * @throws InvalidCallTypeRequestException 	If callType is invalid request then it throw InvalidCallTypeRequestException
	 */
	public List<CallType> getCallType(String callType) throws CallTypeNotFoundException,InvalidCallTypeRequestException {
		logger.debug("Inside getCallType of CallTypeDao");
		if(callType==null)
		throw new InvalidCallTypeRequestException("Invalid Calltype request");
		List<CallType>callLists = getcallTypeData();
		List<CallType> calltypeList = new ArrayList<CallType>();
		logger.debug("getVideoCallType of  CallTypeDao : " + callType);
		for (CallType call : callLists) {
			if (call.getcallType().equalsIgnoreCase(callType.trim()))
				calltypeList.add(call);
		}
		if (callType.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.CALLTYPE_PERSON_ERROR_CODE));
			throw new CallTypeNotFoundException("Error Code:" + ErrorCodeConstant.BILLADDRESS_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.CALLTYPE_PERSON_ERROR_CODE));
		}
		return calltypeList;
	}
	
	/** 
	 * this method is used to get all callType
	 * @return
	 * 			It will return the list of callType objects
	 * @throws CallTypeNotFoundException	if callType is not found then it throw CallTypeNotFoundException
	 */

	public List<CallType> getAllCallType()throws CallTypeNotFoundException {
		logger.debug("Inside getAllCallType of CallTypeDao");
		 List<CallType>calltypeList = getcallTypeData();
	
		if (calltypeList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.CALLTYPE_PERSON_ERROR_CODE));
			throw new CallTypeNotFoundException("Error Code:" + ErrorCodeConstant.CALLTYPE_PERSON_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.CALLTYPE_PERSON_ERROR_CODE));
		}
		return calltypeList;
	}
	
	/**
	 * this method contains callType dummy data, once we used database then we will fetched data from db
	 * @return It return the list of hardcoded values 
	 */

	public List<CallType> getcallTypeData() {
		List<CallType> callList = new ArrayList<CallType>();
		CallType call = null;
		call = new CallType("14SK","Skype");
		callList.add(call);
		call = new CallType("15","AirtelVoiceCall");
		callList.add(call);
		return callList;
	}
}