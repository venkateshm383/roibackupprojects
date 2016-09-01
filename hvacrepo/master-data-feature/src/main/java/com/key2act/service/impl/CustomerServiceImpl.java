package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.CustomerDAO;
import com.key2act.service.CustomerInvalidRequestException;
import com.key2act.service.CustomerNotFoundException;
import com.key2act.pojo.Customer;
import com.key2act.service.CustomerService;

/**
 * this class is used to perform service methods for Customer
 * @author bizruntime
 *
 */
public class CustomerServiceImpl implements CustomerService {

	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
	private CustomerDAO customerDAO;
	
	public CustomerServiceImpl() {
		customerDAO = new CustomerDAO();
	}
	
	/**
	 * this method is used to get customer details based on customer name
	 * @param custName
	 * 			based on customer name customer details will be fetched
	 * @return
	 * 			return the json data in string format
	 * @throws CustomerNotFoundException
	 * 				throw CustomerNotFoundException when customer details are not available
	 * @throws CustomerInvalidRequestException 
	 */
	public List<Customer> getCustomer(String custName) throws CustomerNotFoundException, CustomerInvalidRequestException {
		logger.debug("Inside getCustomer of CustomerServiceImpl");
		
		if(custName == null) {
			throw new CustomerInvalidRequestException("Customer Name is null");
		}
		List<Customer> custLists = customerDAO.getCustomer(custName);
		return custLists;
	}
	
	/**
	 * this method is used to get all customer details
	 * 
	 * @return
	 * 			return the json data in string format
	 * @throws CustomerNotFoundException
	 *  				throw CustomerNotFoundException when customer details are not available
	 */
	@Override
	public List<Customer> getAllCustomer() throws CustomerNotFoundException {
		logger.debug("Inside getAllCustomer of CustomerServiceImpl");
		List<Customer> custLists = customerDAO.getAllCustomer();
		return custLists;
	}
	
}
