package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.CallTypeDAO;
import com.key2act.pojo.CallType;
import com.key2act.service.CallTypeNotFoundException;
import com.key2act.service.CallTypeService;
import com.key2act.service.InvalidCallTypeRequestException;

/**
 * this class is used to perform service methods for call type
 * 
 * @author bizruntime
 *
 */
public class CallTypeServiceImpl implements CallTypeService {

	private static final Logger logger = LoggerFactory.getLogger(CallTypeServiceImpl.class);

	/**
	 * this method is used to get callType
	 * 
	 * @param callType
	 *            based on callType, list of callType objects will be fetched
	 * @return It will return the list of callType objects
	 * @throws CallTypeNotFoundException
	 *             if callType is not found then it throw
	 *             CallTypeNotFoundException
	 * @throws InvalidCallTypeRequestException
	 *             If callType is invalid request then it throw
	 *             InvalidCallTypeRequestException
	 */
	public List<CallType> getCallType(String callType)
			throws CallTypeNotFoundException, InvalidCallTypeRequestException {
		if (callType == null)
			throw new InvalidCallTypeRequestException("Invalid CallType");
		CallTypeDAO callTypeDAO = new CallTypeDAO();
		List<CallType> calltypeList = callTypeDAO.getCallType(callType);
		return calltypeList;
	}

	/**
	 * this method is used to get all callType
	 * 
	 * @return It will return the list of callType objects
	 * @throws CallTypeNotFoundException
	 *             if callType is not found then it throw
	 *             CallTypeNotFoundException
	 */
	public List<CallType> getAllCallType() throws CallTypeNotFoundException {

		logger.debug("Inside getAllCallType of CallStatusServiceImpl");
		CallTypeDAO callTypeDAO = new CallTypeDAO();
		List<CallType> calltypeList = callTypeDAO.getAllCallType();
		return calltypeList;
	}
}
