package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.MasterTaxScheduleDAO;
import com.key2act.pojo.MasterTaxSchedule;
import com.key2act.service.InvalidMasterTaxScheduleRequestException;
import com.key2act.service.MasterTaxScheduleNotFoundException;
import com.key2act.service.MasterTaxScheduleService;

/**
 * this class is used to perform service methods for Master Tax Schedule
 * @author bizruntime
 *
 */
public class MasterTaxScheduleServiceImpl implements MasterTaxScheduleService {

	private static final Logger logger = LoggerFactory.getLogger(MasterTaxScheduleServiceImpl.class);
	
	/**
	 * this method is used to get master tax schedule 
	 * @param masterTaxScheduleType
	 * 					based on masterTaxScheduleType, MasterTaxSchedule will be fetched
	 * @return
	 * 			return the list of MasterTaxSchedule object
	 * @throws MasterTaxScheduleNotFoundException
	 * 			when master tax schedule not found then it throw MasterTaxScheduleNotFoundException
	 * @throws InvalidMasterTaxScheduleRequestException
	 * 			when request is invalid then it throw InvalidMasterTaxScheduleRequestException
	 */
	public List<MasterTaxSchedule> getMasterTaxSchedule(String masterTaxScheduleType) throws MasterTaxScheduleNotFoundException, InvalidMasterTaxScheduleRequestException {
		logger.debug("Inside getMasterTaxSchedule of MasterTaxScheduleServiceImpl");
		if(masterTaxScheduleType == null)
			throw new InvalidMasterTaxScheduleRequestException("Invalid Master Schedule");
		MasterTaxScheduleDAO masterTaxScheduleDao = new MasterTaxScheduleDAO();
		List<MasterTaxSchedule> masterTaxSchedules = masterTaxScheduleDao.getMasterTaxSchedule(masterTaxScheduleType);
		return masterTaxSchedules;
	}//end of method
	
	/**
	 * this method is used to get master tax schedule 
	 * @return
	 * 			return the list of MasterTaxSchedule object
	 * @throws MasterTaxScheduleNotFoundException
	 * 			when master tax schedule not found then it throw MasterTaxScheduleNotFoundException
	 */
	@Override
	public List<MasterTaxSchedule> getAllMasterTaxSchedule() throws MasterTaxScheduleNotFoundException {
		logger.debug("Inside getAllMasterTaxSchedule of MasterTaxScheduleServiceImpl");
		MasterTaxScheduleDAO masterTaxScheduleDao = new MasterTaxScheduleDAO();
		List<MasterTaxSchedule> masterTaxSchedules = masterTaxScheduleDao.getAllMasterTaxSchedule();
		return masterTaxSchedules;
	}//end of method

}
