package com.key2act.service;

import java.util.List;

import com.key2act.pojo.Customer;

/**
 * this interface is used to get data from customer dao
 * @author bizruntime
 *
 */
public interface CustomerService {
	
	/**
	 * this method is used to get customer details based on customer name
	 * @param custName
	 * 			based on customer name customer details will be fetched
	 * @return
	 * 			return the json data in string format
	 * @throws CustomerNotFoundException
	 * 				throw CustomerNotFoundException when customer details are not available
	 * @throws CustomerInvalidRequestException
	 * 				if custName is null then it throw CustomerInvalidRequestException
	 */
	public List<Customer> getCustomer(String custName) throws CustomerNotFoundException, CustomerInvalidRequestException;
	
	/**
	 * this method is used to get all customer details
	 * 
	 * @return
	 * 			return the json data in string format
	 * @throws CustomerNotFoundException
	 *  				throw CustomerNotFoundException when customer details are not available
	 */
	public List<Customer> getAllCustomer() throws CustomerNotFoundException;
	
}
