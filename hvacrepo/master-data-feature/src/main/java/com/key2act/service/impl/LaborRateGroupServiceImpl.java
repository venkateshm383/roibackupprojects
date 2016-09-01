package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.LaborRateGroupDAO;
import com.key2act.pojo.LaborRateGroup;
import com.key2act.service.InvalidLaborRateGroupException;
import com.key2act.service.LaborRateGroupNotFoundException;
import com.key2act.service.LaborRateGroupService;

/**
 * this class contains all service methods to get labor rate group
 * @author bizruntime
 *
 */
public class LaborRateGroupServiceImpl implements LaborRateGroupService {

	private static final Logger logger = LoggerFactory.getLogger(LaborRateGroupServiceImpl.class);
	
	/**
	 * this method is used to get labor groups data
	 * @param laborRateGroupName
	 * 					based on laborRateGroupName laborRateGroup data will be fetched
	 * @return
	 * 			return the list of LaborRateGroup objects
	 * @throws LaborRateGroupNotFoundException 	If labor rate group is not found then it throw LaborRateGroupNotFoundException
	 */
	public List<LaborRateGroup> getLaborRateGroup(String laborRateGroupName) throws LaborRateGroupNotFoundException, InvalidLaborRateGroupException {
		logger.debug("Inside getLaborRateGroup of LaborRateGroupServiceImpl");
		
		if(laborRateGroupName == null)
			throw new InvalidLaborRateGroupException("Invalid Labor Rate Group Exception");
		
		LaborRateGroupDAO groupDao = new LaborRateGroupDAO();
		List<LaborRateGroup> rateGroups = groupDao.getLaborRateGroup(laborRateGroupName);
		return rateGroups;
	}
	
	/**
	 * this method is used to get labor groups data
	 * @return
	 * 			return the list of LaborRateGroup objects
	 * @throws LaborRateGroupNotFoundException  If labor rate group is not found then it throw LaborRateGroupNotFoundException
	 */
	@Override
	public List<LaborRateGroup> getAllLaborRateGroup() throws LaborRateGroupNotFoundException {
		logger.debug("Inside getAllLaborRateGroup of LaborRateGroupServiceImpl");
		LaborRateGroupDAO groupDao = new LaborRateGroupDAO();
		List<LaborRateGroup> rateGroups = groupDao.getAllLaborRateGroup();
		return rateGroups;
	}

}
