package com.key2act.service;

import java.util.List;

import com.key2act.pojo.JobNumber;

public interface JobNumberService {

	/**
	 * this method is used to get jobNumber details based on customer name
	 * 
	 * @param jobName
	 *            based on jobName details will be fetched
	 * @return return the json data in string format
	 * @throws JobNumberNotFoundException
	 *             throw JobNumberNotFoundException when jobNumber details are
	 *             not available
	 * @throws InvalidJobNumberRequestException
	 * 				if jobName is null then it throw InvalidJobNumberRequestException
	 */

	public List<JobNumber> getJobNum(String jobName)
			throws JobNumberNotFoundException, InvalidJobNumberRequestException;

	/**
	 * this method is use to get all jobNumber details
	 * 
	 * @throws JobNumberNotFoundException:when
	 *             JobNumber details not found
	 * 
	 */
	public List<JobNumber> getAllJobNum() throws JobNumberNotFoundException;

}
