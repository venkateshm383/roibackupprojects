package com.key2act.service;

import java.util.List;

import com.key2act.pojo.LaborRateGroup;

/**
 * this interface contains all service methods to get labor rate group
 * 
 * @author bizruntime
 *
 */
public interface LaborRateGroupService {

	/**
	 * this method is used to get labor groups data
	 * 
	 * @param laborRateGroupName
	 *            based on laborRateGroupName laborRateGroup data will be
	 *            fetched
	 * @return return the list of LaborRateGroup objects
	 * @throws LaborRateGroupNotFoundException
	 *             If labor rate group is not found then it throw
	 *             LaborRateGroupNotFoundException
	 */
	public List<LaborRateGroup> getLaborRateGroup(String laborRateGroupName)
			throws LaborRateGroupNotFoundException, InvalidLaborRateGroupException;

	/**
	 * this method is used to get labor groups data
	 * 
	 * @return return the list of LaborRateGroup objects
	 * @throws LaborRateGroupNotFoundException
	 *             If labor rate group is not found then it throw
	 *             LaborRateGroupNotFoundException
	 */
	public List<LaborRateGroup> getAllLaborRateGroup() throws LaborRateGroupNotFoundException;

}
