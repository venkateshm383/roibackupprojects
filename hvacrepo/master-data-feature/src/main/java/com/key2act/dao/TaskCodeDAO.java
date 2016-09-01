package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.TaskCode;
import com.key2act.service.InvalidTaskCodeRequestException;
import com.key2act.service.TaskCodeNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class is used to perform all dao operation of task code
 * @author bizruntime
 *
 */
public class TaskCodeDAO {

	private static final Logger logger = LoggerFactory.getLogger(TaskCodeDAO.class);
	private static Properties prop=PropertyFileLoader.getProperties();

	/** 
	 * this method is used to get taskCode
	 * @param taskName
	 * 					based on taskName, list of taskCode objects will be fetched 
	 * @return
	 * 			It will return the list of taskCode objects
	 * @throws TaskCodeNotFoundException	if taskName is not found then it throw TaskCodeNotFoundException
	 * @throws InvalidTaskCodeRequestException 	If taskName is invalid request then it throw InvalidTaskCodeRequestException
	 */
	public List<TaskCode> getTaskcode(String taskName)	throws TaskCodeNotFoundException,InvalidTaskCodeRequestException {
		logger.debug("Inside getTaskcode of TaskDao" + taskName);
		if(taskName==null)
			throw new InvalidTaskCodeRequestException("Invalid tascode request");
		List<TaskCode> taskLists = getTaskcodeData();
		List<TaskCode> taskcodeList = new ArrayList<TaskCode>();
		logger.debug("getTaskcodeById of TaskCodeDao : " + taskName);
		for (TaskCode task : taskLists) {
			if (task.gettaskcode() .equalsIgnoreCase(taskName.trim()))
				taskcodeList.add(task);
		}
		if (taskcodeList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.TASKCODE_CODE) + taskName);

			throw new TaskCodeNotFoundException("Error Code:" + ErrorCodeConstant.TASKCODE_CODE + ", " + prop.getProperty(ErrorCodeConstant.TASKCODE_CODE) + taskName);
		}
		return taskcodeList;
	}
	
	/** 
	 * this method is used to get all taskcode
	 * @return
	 * 			It will return the list of taskName objects
	 * @throws TaskCodeNotFoundException	if taskName is not found then it throw TaskCodeNotFoundException
	 */
	public List<TaskCode> getAllTaskcode()throws TaskCodeNotFoundException {
		logger.debug("Inside getAllTaskcode of TaskDao");
		List<TaskCode> taskcodeList = getTaskcodeData();
		logger.debug("getAllTaskcode of TaskCodeDao : " );
		
		if (taskcodeList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.TASKCODE_CODE));

			throw new TaskCodeNotFoundException("Error Code:" + ErrorCodeConstant.TASKCODE_CODE + ", " + prop.getProperty(ErrorCodeConstant.TASKCODE_CODE));
		}
		return taskcodeList;
	}

	/**
	 * this method contains taskcode dummy data, once we used database then we will fetched data from db
	 * @return It return the list of hardcoded values 
	 */
	public List<TaskCode> getTaskcodeData() {
		List<TaskCode> taskcodeList = new ArrayList<TaskCode>();
		TaskCode task = null;
		task = new TaskCode("1EM", "Electrical Maintainance");
		taskcodeList.add(task);
		task = new TaskCode("2SM", "Server Maintainance");
		taskcodeList.add(task);
		return taskcodeList;
	}

}
