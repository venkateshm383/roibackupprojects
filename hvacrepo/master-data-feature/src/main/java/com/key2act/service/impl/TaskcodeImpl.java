package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.TaskCodeDAO;
import com.key2act.pojo.TaskCode;
import com.key2act.service.InvalidTaskCodeRequestException;
import com.key2act.service.TaskCodeNotFoundException;
import com.key2act.service.TaskcodeService;

/**
 * this class is used to perform service methods for Task Code
 * @author bizruntime
 *
 */
public class TaskcodeImpl implements TaskcodeService {

	private static final Logger logger = LoggerFactory.getLogger(TaskcodeImpl.class);

	/**
	 * this method is used to get taskCode
	 * 
	 * @param taskName
	 *            based on taskName, list of taskCode objects will be fetched
	 * @return It will return the list of taskCode objects
	 * @throws TaskCodeNotFoundException
	 *             if taskName is not found then it throw
	 *             TaskCodeNotFoundException
	 * @throws InvalidTaskCodeRequestException
	 *             If taskName is invalid request then it throw
	 *             InvalidTaskCodeRequestException
	 */

	public List<TaskCode> getTaskCode(String taskName)
			throws TaskCodeNotFoundException, InvalidTaskCodeRequestException {

		if (taskName == null)
			throw new InvalidTaskCodeRequestException("Invalid TaskCode request");
		TaskCodeDAO taskCodeDAO = new TaskCodeDAO();
		List<TaskCode> taskcodeList = taskCodeDAO.getTaskcode(taskName);
		return taskcodeList;
	}

	/**
	 * this method is used to get all taskcode
	 * 
	 * @return It will return the list of taskName objects
	 * @throws TaskCodeNotFoundException
	 *             if taskName is not found then it throw
	 *             TaskCodeNotFoundException
	 */

	public List<TaskCode> getAllTaskCode() throws TaskCodeNotFoundException {

		logger.debug("Inside getAllTaskCode of TaskcodeImpl");
		TaskCodeDAO taskCodeDAO = new TaskCodeDAO();
		List<TaskCode> taskcodeList = taskCodeDAO.getAllTaskcode();
		return taskcodeList;
	}

}