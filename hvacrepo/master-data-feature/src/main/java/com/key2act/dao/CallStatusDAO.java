package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.CallStatus;
import com.key2act.service.CallStatusInvalidRequestException;
import com.key2act.service.CallStatusNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class CallStatusDAO {

	private static final Logger logger = LoggerFactory.getLogger(CallStatusDAO.class);
	public Properties prop = PropertyFileLoader.getProperties();

	/** 
	 * this method is used to get callStatus
	 * @param callstatusId
	 * 					based on callstatusId, list of callStatus objects will be fetched 
	 * @return
	 * 			It will return the list of billcustomer objects
	 * @throws CallStatusNotFoundException	if callstatusId is not found then it throw CallStatusNotFoundException
	 * @throws CallStatusInvalidRequestException 	If callstatusId is invalid request then it throw CallStatusInvalidRequestException
	 */
	public List<CallStatus> getCallStatus(String callstatusId) throws CallStatusNotFoundException,CallStatusInvalidRequestException{
		logger.debug("Inside getCallStatus of CallStatusDao");
		if(callstatusId==null)
			throw new CallStatusInvalidRequestException("Invalid callStatusId");
		List<CallStatus>callLists = getcallStatusData();
		List<CallStatus> callstatusList = new ArrayList<CallStatus>();
		logger.debug("getCallstatus of  CallStatusDao : " + callstatusId);
		for (CallStatus call : callLists) {
			if (call.getcallstatus().equalsIgnoreCase(callstatusId.trim()))
				callstatusList.add(call);
		}
		if (callstatusList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.CALLSTATUS_CODE) + callstatusId);
			throw new CallStatusNotFoundException("Error Code:" + ErrorCodeConstant.CALLSTATUS_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.CALLSTATUS_CODE) + callstatusId);
		}
		return callstatusList;
	}

	/** 
	 * this method is used to get all callSatatus
	 * @return
	 * 			It will return the list of callstatus objects
	 * @throws CallStatusNotFoundException	if callStatus is not found then it throw BillCustomerNotFoundException
	 */

	public List<CallStatus> getAllCallStatus() throws CallStatusNotFoundException {
		logger.debug("Inside getAllCallStatus of CallStatusDao");
		List<CallStatus>callstatusList = getcallStatusData();
		if (callstatusList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.CALLSTATUS_CODE));
			throw new CallStatusNotFoundException("Error Code:" + ErrorCodeConstant.CALLSTATUS_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.CALLSTATUS_CODE));

		}
		return callstatusList;
	}
	
	/**
	 * this method contains bilcustomer dummy data, once we used database then we will fetched data from db
	 * @return It return the list of hardcoded values 
	 */

	public List<CallStatus> getcallStatusData() {
		List<CallStatus> callstatList = new ArrayList<CallStatus>();
		CallStatus call = null;
		call = new CallStatus("CB6", "CallBack");
		callstatList.add(call);
		call = new CallStatus("SB7", "Standbuy");
		callstatList.add(call);
		call = new CallStatus("DI8", "Diverted");
		callstatList.add(call);
		return callstatList;
	}

}
