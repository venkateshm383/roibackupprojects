package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.JobNumberDAO;
import com.key2act.pojo.JobNumber;
import com.key2act.service.InvalidJobNumberRequestException;
import com.key2act.service.JobNumberNotFoundException;
import com.key2act.service.JobNumberService;

/**
 * this class is used to perform service methods for Job Number
 * @author bizruntime
 *
 */
public class JobNumberImpl implements JobNumberService {

	private static final Logger logger = LoggerFactory.getLogger(JobNumberImpl.class);

	/**
	 * this method is used to get jobNumber
	 * 
	 * @param jobName
	 *            based on jobName, list of jobNumber objects will be fetched
	 * @return It will return the list of jobnumber objects
	 * @throws JobNumberNotFoundException
	 *             if jobName is not found then it throw
	 *             JobNumberNotFoundException
	 * @throws InvalidJobNumberRequestException
	 *             If jobName is invalid request then it throw
	 *             InvalidJobNumberRequestException
	 */

	public List<JobNumber> getJobNum(String jobName)
			throws JobNumberNotFoundException, InvalidJobNumberRequestException {
		if (jobName == null)
			throw new InvalidJobNumberRequestException("Invalid jobNumber request");
		JobNumberDAO jobNumberDAO = new JobNumberDAO();
		List<JobNumber> jobnumberList = jobNumberDAO.getJobNum(jobName);
		return jobnumberList;
	}

	/**
	 * this method is used to get all jobNumber
	 * 
	 * @return It will return the list of jobnumber objects
	 * @throws JobNumberNotFoundException
	 *             if jobName is not found then it throw
	 *             JobNumberNotFoundException
	 */
	public List<JobNumber> getAllJobNum() throws JobNumberNotFoundException {
		logger.debug("Inside getAllJobNum of JobNumberImpl");
		JobNumberDAO jobNumberDAO = new JobNumberDAO();
		List<JobNumber> jobnumberList = jobNumberDAO.getAllJobNum();
		return jobnumberList;

	}
}
