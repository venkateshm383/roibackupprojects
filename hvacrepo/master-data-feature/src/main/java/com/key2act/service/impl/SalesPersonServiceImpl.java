package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.SalesPersonDAO;
import com.key2act.pojo.SalesPerson;
import com.key2act.service.InvalidSalesPersonRequest;
import com.key2act.service.SalesPersonNotFoundException;
import com.key2act.service.SalesPersonService;

/**
 * this class is used to perform service methods for Sales Person
 * @author bizruntime
 *
 */
public class SalesPersonServiceImpl implements SalesPersonService {

	private static final Logger logger = LoggerFactory.getLogger(SalesPersonServiceImpl.class);
	
	/**
	 * this method is used to get sales person
	 * @param salesPerson
	 * 					based on salesPerson data will be feched
	 * @return
	 * 			return the list of SalesPerson Object
	 * @throws SalesPersonNotFoundException
	 * 				If sales person is not found it throw exception
	 */
	public List<SalesPerson> getSalesPerson(String salesPersonName) throws SalesPersonNotFoundException, InvalidSalesPersonRequest {
		logger.debug("Inside getSalesPerson of SalesPersonServiceImpl");
		if(salesPersonName == null)
			throw new InvalidSalesPersonRequest("Invalid Sales Person Requesr");
		SalesPersonDAO salesPersonDao = new SalesPersonDAO();
		List<SalesPerson> salesPersonLists = salesPersonDao.getSalesPerson(salesPersonName);
		return salesPersonLists;
	}
	
	/**
	 * this method is used to get sales person
	 * @return
	 * 			return the list of SalesPerson Object
	 * @throws SalesPersonNotFoundException
	 * 					If sales person is not found it throw exception
	 */
	@Override
	public List<SalesPerson> getAllSalesPerson() throws SalesPersonNotFoundException {
		logger.debug("Inside getSalesPerson of SalesPersonServiceImpl");
		SalesPersonDAO salesPersonDao = new SalesPersonDAO();
		List<SalesPerson> salesPersonLists = salesPersonDao.getAllSalesPerson();
		return salesPersonLists;
	}

}
