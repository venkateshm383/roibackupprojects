package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.JobNumber;
import com.key2act.service.InvalidJobNumberRequestException;
import com.key2act.service.JobNumberNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class is used to perform dao operation on job number
 * 
 * @author bizruntime
 *
 */
public class JobNumberDAO {

	private static final Logger logger = LoggerFactory.getLogger(JobNumberDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();

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
		logger.debug("Inside getJobNum of JobNumberDao");
		if (jobName == null)
			throw new InvalidJobNumberRequestException("Invalid Jobnumber request");
		List<JobNumber> jobnumber = getJobNumberData();
		List<JobNumber> jobnumberList = new ArrayList<JobNumber>();
		logger.debug("getJobNum ofJobNumberDao: " + jobName);
		for (JobNumber job : jobnumber) {
			if (job.getjobNum().equalsIgnoreCase(jobName.trim()))
				jobnumberList.add(job);

		}
		if (jobnumberList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.JOBNUMBER_ERROR_CODE) + jobName);
			throw new JobNumberNotFoundException("Error Code:" + ErrorCodeConstant.JOBNUMBER_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.JOBNUMBER_ERROR_CODE) + jobName);
		}
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
		logger.debug("Inside getAllJobNum of JobNumberDao");
		List<JobNumber> jobnumberList = getJobNumberData();
		logger.debug("getAllJobNum ofJobNumberDao: ");

		if (jobnumberList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.JOBNUMBER_ERROR_CODE));
			throw new JobNumberNotFoundException("Error Code:" + ErrorCodeConstant.JOBNUMBER_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.JOBNUMBER_ERROR_CODE));
		}
		return jobnumberList;
	}

	/**
	 * this method contains jobnumber dummy data, once we used database then we
	 * will fetched data from db
	 * 
	 * @return It return the list of hardcoded values
	 */
	public List<JobNumber> getJobNumberData() {
		List<JobNumber> jobnumberList = new ArrayList<JobNumber>();
		JobNumber job = null;
		job = new JobNumber("21AM", "Server Upgradation");
		jobnumberList.add(job);
		job = new JobNumber("31RS", "DataBase Maintainance");
		jobnumberList.add(job);
		return jobnumberList;
	}
}
