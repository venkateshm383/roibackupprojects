package com.key2act.service;

import java.util.List;

import com.key2act.pojo.ProblemType;

public interface ProblemTypeService {

	/**
	 * this method is used to get ProblemTypeService details based on customer
	 * name
	 * 
	 * @param technicalIssues
	 *            based on technicalIssues details will be fetched
	 * @return return the json data in string format
	 * @throws ProblemTypeIsNotFoundException
	 *             throw ProblemTypeIsNotFoundException when problemType details
	 *             are not available
	 * @throws InvalidProblemTypeRequestException
	 *             if technicalIssues is null then it throw
	 *             InvalidProblemTypeRequestException
	 * 
	 */

	public List<ProblemType> getProblemType(String technicalIssues)
			throws ProblemTypeIsNotFoundException, InvalidProblemTypeRequestException;

	/**
	 * this method is use to get all problem Type details
	 * 
	 * @throws ProblemTypeIsNotFoundException:when
	 *             ProblemType details not found
	 * 
	 */
	public List<ProblemType> getAllProblem() throws ProblemTypeIsNotFoundException, InvalidProblemTypeRequestException;

}
