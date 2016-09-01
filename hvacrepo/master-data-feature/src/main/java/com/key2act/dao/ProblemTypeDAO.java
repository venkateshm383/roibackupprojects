package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.ProblemType;
import com.key2act.service.InvalidProblemTypeRequestException;
import com.key2act.service.ProblemTypeIsNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class is used to perform all dao operation of problem type
 * 
 * @author bizruntime
 *
 */
public class ProblemTypeDAO {

	private static final Logger logger = LoggerFactory.getLogger(ProblemTypeDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();

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
	public List<ProblemType> getProblem(String technicalIssues)
			throws ProblemTypeIsNotFoundException, InvalidProblemTypeRequestException {
		logger.debug("Inside getProblemType of ProblemTypeDao" + technicalIssues);
		if (technicalIssues == null)
			throw new InvalidProblemTypeRequestException("Invalid problemtype request");
		List<ProblemType> issueLists = getproblemTypeData();
		List<ProblemType> technicalIssuesList = new ArrayList<ProblemType>();
		logger.debug("getProblemType of  ProblemTypeDao : " + technicalIssues);
		for (ProblemType issue : issueLists) {
			if (issue.gettechnicalIssues().equalsIgnoreCase(technicalIssues.trim()))
				technicalIssuesList.add(issue);
		}
		if (technicalIssuesList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.PROBLEMTYPE_ERROR_CODE) + technicalIssues);
			throw new ProblemTypeIsNotFoundException("Error Code:" + ErrorCodeConstant.PROBLEMTYPE_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.PROBLEMTYPE_ERROR_CODE) + technicalIssues);
		}
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
	public List<ProblemType> getAllProblem() throws ProblemTypeIsNotFoundException {
		logger.debug("Inside getAllProblem of ProblemTypeDao");
		List<ProblemType> technicalIssuesList = getproblemTypeData();
		if (technicalIssuesList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.PROBLEMTYPE_ERROR_CODE));
			throw new ProblemTypeIsNotFoundException("Error Code:" + ErrorCodeConstant.BILL_TO_ADDRESS_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.BILL_TO_ADDRESS_ERROR_CODE));
		}
		return technicalIssuesList;
	}// end of method

	/**
	 * this method contains problemType dummy data, once we used database then
	 * we will fetched data from db
	 * 
	 * @return It return the list of hardcoded values
	 */
	public List<ProblemType> getproblemTypeData() {
		List<ProblemType> issueList = new ArrayList<ProblemType>();
		ProblemType issue = null;
		issue = new ProblemType("1HE", "High Elecectric Bills");
		issueList.add(issue);
		issue = new ProblemType("2HM", "Humidifier not working");
		issueList.add(issue);
		issue = new ProblemType("3ST", "Steam coming from outdoor unit");
		issueList.add(issue);
		return issueList;
	}// end of method

}
