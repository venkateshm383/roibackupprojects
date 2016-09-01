package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.LaborRateGroup;
import com.key2act.service.InvalidLaborRateGroupException;
import com.key2act.service.LaborRateGroupNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class is used to perform all dao operation of labor rate group
 * @author bizruntime
 *
 */
public class LaborRateGroupDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(LaborRateGroupDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();
	
	/**
	 * this method is used to get labor groups data
	 * @param laborRateGroupName
	 * 					based on laborRateGroupName laborRateGroup data will be fetched
	 * @return
	 * 			return the list of LaborRateGroup objects
	 * @throws LaborRateGroupNotFoundException 	If labor rate group is not found then it throw LaborRateGroupNotFoundException
	 */
	public List<LaborRateGroup> getLaborRateGroup(String laborRateGroupName) throws LaborRateGroupNotFoundException, InvalidLaborRateGroupException {
		logger.debug("Inside getLaborRateGroup method of LaborRateGroupDao");
		
		if(laborRateGroupName == null)
			throw new InvalidLaborRateGroupException("Invalid Labor Rate Group Exception");
		
		List<LaborRateGroup> resultGroups = new ArrayList<LaborRateGroup>();
		List<LaborRateGroup> groupsData = getLaborRateGroupData();
		for(LaborRateGroup group : groupsData) {
			if(group.getLaborRateGroupName().equalsIgnoreCase(laborRateGroupName.trim()))
				resultGroups.add(group);
		}
		if(resultGroups.isEmpty()) {
			logger.info(prop.getProperty(ErrorCodeConstant.LABOR_RATE_GROUP_ERROR_CODE) + laborRateGroupName);
			throw new LaborRateGroupNotFoundException("Error Code:" + ErrorCodeConstant.LABOR_RATE_GROUP_ERROR_CODE + ", " + prop.getProperty(ErrorCodeConstant.LABOR_RATE_GROUP_ERROR_CODE) + laborRateGroupName);
		}
		return resultGroups;
	}
	
	/**
	 * this method is used to get labor groups data
	 * @return
	 * 			return the list of LaborRateGroup objects
	 * @throws LaborRateGroupNotFoundException 	If labor rate group is not found then it throw LaborRateGroupNotFoundException
	 */
	public List<LaborRateGroup> getAllLaborRateGroup() throws LaborRateGroupNotFoundException {
		logger.debug("Inside getLaborRateGroup method of LaborRateGroupDao");
		List<LaborRateGroup> resultGroups = new ArrayList<LaborRateGroup>();
		
		if(resultGroups.isEmpty()) {
			logger.info(prop.getProperty(ErrorCodeConstant.LABOR_RATE_GROUP_ERROR_CODE));
			throw new LaborRateGroupNotFoundException("Error Code:" + ErrorCodeConstant.LABOR_RATE_GROUP_ERROR_CODE + ", " + prop.getProperty(ErrorCodeConstant.LABOR_RATE_GROUP_ERROR_CODE));
		}
		return resultGroups;
	}
	
	/**
	 * this method contains all hard coded data, once we used database then we will fetched data from db
	 * @return  It return the list of hardcoded values
	 */
	public List<LaborRateGroup> getLaborRateGroupData() {
		List<LaborRateGroup> laborRateGroups = new ArrayList<LaborRateGroup>();
		LaborRateGroup group = new LaborRateGroup("10001", "A", "50", "Male");
		laborRateGroups.add(group);
		group = new LaborRateGroup("10002", "A", "40", "Male");
		laborRateGroups.add(group);
		group = new LaborRateGroup("10003", "C", "30", "Female");
		laborRateGroups.add(group);
		group = new LaborRateGroup("10004", "B", "50", "Male");
		laborRateGroups.add(group);
		group = new LaborRateGroup("10005", "A", "50", "Female");
		laborRateGroups.add(group);
		return laborRateGroups;
	}//end of method

}
