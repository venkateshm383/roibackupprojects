package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.service.CustomerInvalidRequestException;
import com.key2act.service.CustomerNotFoundException;
import com.key2act.pojo.Customer;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class contains all methods to get customer detail
 * @author bizruntime
 *
 */
public class CustomerDAO {

	private static final Logger logger = LoggerFactory.getLogger(CustomerDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();
	
	/**
	 * this method is used to get customer details from customer name
	 * @param custName: based on custName customer details will be fetched
	 * @return, return the list of customer objects
	 * @throws CustomerNotFoundException	If customer is not found then it throw CustomerNotFoundException
	 * @throws CustomerInvalidRequestException 	If customerName is null then it throw CustomerInvalidRequestException
	 */
	public List<Customer> getCustomer(String customerName) throws CustomerNotFoundException, CustomerInvalidRequestException {
		logger.debug("Inside getCustomer of CustomerDao");
		logger.debug("Customer Name getCustomer() : " + customerName);
		
		if(customerName == null) {
			throw new CustomerInvalidRequestException("Customer Name is null");
		}
		
		List<Customer> custLists = getCustomerData();
		List<Customer> resultCustList = new ArrayList<Customer>();
		for(Customer cust : custLists) {
			if(cust.getCustomerName().equalsIgnoreCase(customerName))
				resultCustList.add(cust);
		}
		if(resultCustList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.CUSTOMER_ERROR_CODE) + customerName);
			throw new CustomerNotFoundException("Error Code:" + ErrorCodeConstant.CUSTOMER_ERROR_CODE + ", " + prop.getProperty(ErrorCodeConstant.CUSTOMER_ERROR_CODE) + customerName);
		}
		logger.debug("Customer List: " + resultCustList);
		return resultCustList;
	} //end of method
	
	/**
	 * this method is used to get all customer
	 * @return: return the list of customer objects
	 * @throws CustomerNotFoundException	If customer is not found then it throw CustomerNotFoundException
	 */
	public List<Customer> getAllCustomer() throws CustomerNotFoundException {
		logger.debug("Inside getAllCustomer of CustomerDao");
		List<Customer> custLists = getCustomerData();
		
		if(custLists.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.CUSTOMER_ERROR_CODE));
			throw new CustomerNotFoundException("Error Code:" + ErrorCodeConstant.CUSTOMER_ERROR_CODE + ", " + prop.getProperty(ErrorCodeConstant.CUSTOMER_ERROR_CODE));
		}
		logger.debug("Customer List: " + custLists);
		return custLists;
	} //end of method
	
	/**
	 * this method contains hard coded data, once we will use database then, we'll fetch data from db
	 * @return It return the list of hardcoded values
	 */
	public List<Customer> getCustomerData() {
		List<Customer> customerList = new ArrayList<Customer>();
		Customer cust = new Customer("101", "John", "Martin", "9642222", "Ricky", "855555");
		customerList.add(cust);
		cust = new Customer("102", "David", "Martin", "9642222", "Michel", "887545");
		customerList.add(cust);
		cust = new Customer("103", "John", "Martin", "9642222", "Ricky", "857854");
		customerList.add(cust);
		cust = new Customer("104", "David", "Martin", "9642852", "Michel", "855897");
		customerList.add(cust);
		cust = new Customer("105", "David", "Martin", "96424572", "Ricky", "8558754");
		customerList.add(cust);
		return customerList;
	} //end of method

}
