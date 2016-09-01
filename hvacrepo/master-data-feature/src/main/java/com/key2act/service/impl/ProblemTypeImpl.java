package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.ProblemTypeDAO;
import com.key2act.pojo.ProblemType;
import com.key2act.service.InvalidProblemTypeRequestException;
import com.key2act.service.ProblemTypeIsNotFoundException;
import com.key2act.service.ProblemTypeService;

/**
 * this class is used to perform service methods for problem type
 * 
 * @author bizruntime
 *
 */
public class ProblemTypeImpl implements ProblemTypeService {

	private static final Logger logger = LoggerFactory.getLogger(ProblemTypeImpl.class);

	/**
	 * this method is used to get problemtype
	 * 
	 * @param technicalIssues
	 *            based on technicalIssues, list of problemtype objects will be
	 *            fetched
	 * @return It will return the list of problemType objects
	 * @throws ProblemTypeIsNotFoundException
	 *             if technicalIssues is not found then it throw
	 *             ProblemTypeIsNotFoundException
	 * @throws InvalidProblemTypeRequestException
	 *             If technicalIssues is invalid request then it throw
	 *             InvalidProblemTypeRequestException
	 */

	public List<ProblemType> getProblemType(String technicalIssues)
			throws ProblemTypeIsNotFoundException, InvalidProblemTypeRequestException {

		if (technicalIssues == null)
			throw new InvalidProblemTypeRequestException("Invalid ProblemType");
		ProblemTypeDAO problemTypeDAO = new ProblemTypeDAO();
		List<ProblemType> technicalIssuesList = problemTypeDAO.getProblem(technicalIssues);
		return technicalIssuesList;
	}

	/**
	 * this method is used to get all problemType
	 * 
	 * @return It will return the list of problemType objects
	 * @throws ProblemTypeIsNotFoundException
	 *             if technicalIssues is not found then it throw
	 *             ProblemTypeIsNotFoundException
	 */

	@Override
	public List<ProblemType> getAllProblem() throws ProblemTypeIsNotFoundException {
		logger.debug("Inside getAllProblem of ProblemTypeImpl");
		ProblemTypeDAO problemTypeDAO = new ProblemTypeDAO();
		List<ProblemType> technicalIssuesList = problemTypeDAO.getAllProblem();
		return technicalIssuesList;
	}

}
