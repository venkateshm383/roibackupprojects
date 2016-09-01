package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.SalesPerson;
import com.key2act.service.InvalidSalesPersonRequest;
import com.key2act.service.SalesPersonNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class is used to do all dao operation of sales person 
 * @author bizruntime
 *
 */
public class SalesPersonDAO {
	
	private final Logger logger = LoggerFactory.getLogger(SalesPersonDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();
	
	/**
	 * this method is used to get sales person name by sales person id
	 * @param salesPersonId
	 * @return
	 * 			return the list of SalesPerson Object
	 * @throws SalesPersonNotFoundException
	 * 				If sales person is not found it throw exception
	 * @throws InvalidSalesPersonRequest 
	 * 				If request is invalid then it throw InvalidSalesPersonRequest exception
	 */
	public List<SalesPerson> getSalesPerson(String salesPersonName) throws SalesPersonNotFoundException, InvalidSalesPersonRequest {
		logger.debug("Inside getSalesPersonNameById method of SalesPersonDao");
		
		if(salesPersonName == null)
			throw new InvalidSalesPersonRequest("Invalid Sales Person Request");
		
		List<SalesPerson> salesPersonsList = getSalesPersonData();
		List<SalesPerson> resultSalesPersonLists = new ArrayList<SalesPerson>();
		for(SalesPerson salesPerson : salesPersonsList) {
			if(salesPerson.getSalesPersonName().equalsIgnoreCase(salesPersonName.trim()))
				resultSalesPersonLists.add(salesPerson);
		}
		if(resultSalesPersonLists.isEmpty()) {
			logger.info(prop.getProperty(ErrorCodeConstant.SALES_PERSON_ERROR_CODE) + salesPersonName);
			throw new SalesPersonNotFoundException("Error Code:" + ErrorCodeConstant.SALES_PERSON_ERROR_CODE + ", " + prop.getProperty(ErrorCodeConstant.SALES_PERSON_ERROR_CODE) + salesPersonName);
		}
		return resultSalesPersonLists;
	}//end of method
	
	/**
	 * this method is used to get all sales person
	 * @return
	 * 			return the list of SalesPerson Object
	 * @throws SalesPersonNotFoundException
	 * 						If sales person is not found it throw exception
	 */
	public List<SalesPerson> getAllSalesPerson() throws SalesPersonNotFoundException {
		logger.debug("Inside getSalesPersonNameById method of SalesPersonDao");
		List<SalesPerson> resultSalesPersonsList = getSalesPersonData();
		if(resultSalesPersonsList.isEmpty()) {
			logger.info(prop.getProperty(ErrorCodeConstant.SALES_PERSON_ERROR_CODE));
			throw new SalesPersonNotFoundException("Error Code:" + ErrorCodeConstant.SALES_PERSON_ERROR_CODE + ", " + prop.getProperty(ErrorCodeConstant.SALES_PERSON_ERROR_CODE));
		}
		return resultSalesPersonsList;
	}//end of method
	
	/**
	 * this method is used to store hardcoded data, once we used database then we will fetched data from db
	 * @return It return the list of SalesPerson values
	 */
	public List<SalesPerson> getSalesPersonData() {
		logger.debug("Inside getSalesPersonDummyData method of SalesPersonDummyData");
		List<SalesPerson> salesPersonList = new ArrayList<SalesPerson>();
		SalesPerson salesPerson = null;
		salesPerson = new SalesPerson("201", "John");
		salesPersonList.add(salesPerson);
		salesPerson = new SalesPerson("202", "David");
		salesPersonList.add(salesPerson);
		salesPerson = new SalesPerson("203", "John");
		salesPersonList.add(salesPerson);
		return salesPersonList;
	}//end of method
	
}
