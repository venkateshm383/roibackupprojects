package com.key2act.service;

import java.util.List;

import com.key2act.pojo.SalesPerson;

public interface SalesPersonService {

	/**
	 * this method is used to get sales person
	 * 
	 * @param salesPerson
	 *            based on salesPerson data will be feched
	 * @return return the list of SalesPerson Object
	 * @throws SalesPersonNotFoundException
	 *             If sales person is not found it throw exception
	 * @throws InvalidSalesPersonRequest
	 *             if salesPerson is null then it throw
	 *             InvalidSalesPersonRequest
	 */
	public List<SalesPerson> getSalesPerson(String salesPerson)
			throws SalesPersonNotFoundException, InvalidSalesPersonRequest;

	/**
	 * this method is used to get sales person
	 * 
	 * @return return the list of SalesPerson Object
	 * @throws SalesPersonNotFoundException
	 *             If sales person is not found it throw exception
	 */
	public List<SalesPerson> getAllSalesPerson() throws SalesPersonNotFoundException;

}
