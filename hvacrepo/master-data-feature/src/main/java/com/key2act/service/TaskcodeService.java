package com.key2act.service;

import java.util.List;

import com.key2act.pojo.TaskCode;

public interface TaskcodeService {

	/**
	 * this method is used to get TaskcodeService details based on customer name
	 * 
	 * @param taskName
	 *            based on TaskcodeService details will be fetched
	 * @return return the json data in string format
	 * @throws TaskCodeNotFoundException
	 *             throw TaskCodeNotFoundException when taskcode details are not
	 *             available
	 * @throws InvalidTaskCodeRequestException
	 * 				if taskName is null then it throw InvalidTaskCodeRequestException
	 */
	public List<TaskCode> getTaskCode(String taskName)
			throws TaskCodeNotFoundException, InvalidTaskCodeRequestException;

	/**
	 * this method is use to get all Taskcode details
	 * 
	 * @throws TaskCodeNotFoundException:when
	 *             TaskCode not found
	 */
	public List<TaskCode> getAllTaskCode() throws TaskCodeNotFoundException;
}
