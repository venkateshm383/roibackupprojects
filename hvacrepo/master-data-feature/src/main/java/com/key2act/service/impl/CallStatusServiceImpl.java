package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.CallStatusDAO;
import com.key2act.pojo.CallStatus;
import com.key2act.service.CallStatusInvalidRequestException;
import com.key2act.service.CallStatusNotFoundException;
import com.key2act.service.CallStatusService;

/**
 * this class is used to perform service methods for call status
 * @author bizruntime
 *
 */
public class CallStatusServiceImpl implements CallStatusService {

	private static final Logger logger = LoggerFactory.getLogger(CallStatusServiceImpl.class);

	/**
	 * this method is used to get callStatus
	 * 
	 * @param callstatusId
	 *            based on callstatusId, list of callStatus objects will be
	 *            fetched
	 * @return It will return the list of billcustomer objects
	 * @throws CallStatusNotFoundException
	 *             if callstatusId is not found then it throw
	 *             CallStatusNotFoundException
	 * @throws CallStatusInvalidRequestException
	 *             If callstatusId is invalid request then it throw
	 *             CallStatusInvalidRequestException
	 */
	public List<CallStatus> getCallStatus(String callstatusId)
			throws CallStatusNotFoundException, CallStatusInvalidRequestException {

		if (callstatusId == null)
			throw new CallStatusInvalidRequestException("CallStatus is null");
		CallStatusDAO callstatusDAO = new CallStatusDAO();
		List<CallStatus> callstatusList = callstatusDAO.getCallStatus(callstatusId);
		return callstatusList;
	}

	/**
	 * this method is used to get all callSatatus
	 * 
	 * @return It will return the list of callstatus objects
	 * @throws CallStatusNotFoundException
	 *             if callStatus is not found then it throw
	 *             BillCustomerNotFoundException
	 */
	public List<CallStatus> getAllCallStatus() throws CallStatusNotFoundException {
		logger.debug("Inside getAllCallStatus of CallStatusServiceImpl");
		CallStatusDAO callstatusDAO = new CallStatusDAO();
		List<CallStatus> callstatusList = callstatusDAO.getAllCallStatus();
		return callstatusList;
	}
}
