package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.BillCustomerDAO;
import com.key2act.pojo.BillCustomer;
import com.key2act.service.InvalidBillCustomerRequestException;
import com.key2act.service.BillCustomerNotFoundException;
import com.key2act.service.BillCustomerService;

/**
 * this class is used to perform service methods for Bill Customer
 * @author bizruntime
 *
 */
public class BillCustomerImpl implements BillCustomerService {

	private static final Logger logger = LoggerFactory.getLogger(BillCustomerImpl.class);

	/**
	 * this method is used to get billcustomer
	 * 
	 * @param customerName
	 *            based on customerName, list of billcustomer objects will be
	 *            fetched
	 * @return It will return the list of billcustomer objects
	 * @throws BillCustomerNotFoundException
	 *             if customerName is not found then it throw
	 *             BillCustomerNotFoundException
	 * @throws InvalidBillCustomerRequestException
	 *             If customerName is invalid request then it throw
	 *             InvalidDivisionRequestException
	 */
	public List<BillCustomer> getbillCustomer(String customerName)
			throws BillCustomerNotFoundException, InvalidBillCustomerRequestException {

		if (customerName == null)
			throw new InvalidBillCustomerRequestException("Customer Name is null");
		BillCustomerDAO billcustomerDAO = new BillCustomerDAO();
		List<BillCustomer> billLists = billcustomerDAO.getbillCustomer(customerName);
		return billLists;
	}

	/**
	 * this method is used to get all billcustomer
	 * 
	 * @return It will return the list of billcustomer objects
	 * @throws BillCustomerNotFoundException
	 *             if customerName is not found then it throw
	 *             BillCustomerNotFoundException
	 */
	public List<BillCustomer> getAllbillCustomer() throws BillCustomerNotFoundException {
		logger.debug("Inside getAllbillCustomer of CustomerServiceImpl");
		BillCustomerDAO billcustomerDAO = new BillCustomerDAO();
		List<BillCustomer> billLists = billcustomerDAO.getAllbillCustomer();
		return billLists;
	}
	
	public static void main(String[] args) throws BillCustomerNotFoundException {
		logger.debug("Bill: " + new BillCustomerImpl().getAllbillCustomer());
	}

}