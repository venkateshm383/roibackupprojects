package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.BillCustomer;
import com.key2act.service.InvalidBillCustomerRequestException;
import com.key2act.service.BillCustomerNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class BillCustomerDAO {

	private static final Logger logger = LoggerFactory.getLogger(BillCustomerDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();

	/** 
	 * this method is used to get billcustomer
	 * @param customerName
	 * 					based on customerName, list of billcustomer objects will be fetched 
	 * @return
	 * 			It will return the list of billcustomer objects
	 * @throws BillCustomerNotFoundException	if customerName is not found then it throw BillCustomerNotFoundException
	 * @throws InvalidBillCustomerRequestException 	If customerName is invalid request then it throw InvalidDivisionRequestException
	 */
	public List<BillCustomer> getbillCustomer(String customerName) throws BillCustomerNotFoundException,InvalidBillCustomerRequestException {

		logger.debug("Inside getbillCustomer of BillCustomerDao");
		if(customerName == null) 
			throw new InvalidBillCustomerRequestException("Invalid billcustomer Request");
		
		List<BillCustomer>billLists = getCustomerData();
		List<BillCustomer> customerList = new ArrayList<BillCustomer>();
		logger.debug("getbillCustomer of BillCustomerDao : " + customerName);
		for (BillCustomer cust : billLists) {
			if (cust.getcustomerName().equalsIgnoreCase(customerName.trim()))
				customerList.add(cust);
		}
		if (customerList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.BILLCUSTOMER_ERROR_CODE) + customerName);

			throw new BillCustomerNotFoundException("Error Code:" + ErrorCodeConstant.BILLCUSTOMER_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.BILLCUSTOMER_ERROR_CODE + customerName));
		}
		return customerList;
	}
	/** 
	 * this method is used to get all billcustomer
	 * @return
	 * 			It will return the list of billcustomer objects
	 * @throws BillCustomerNotFoundException	if customerName is not found then it throw BillCustomerNotFoundException
	 */

	public List<BillCustomer> getAllbillCustomer() throws BillCustomerNotFoundException {
		logger.debug("Inside getAllbillCustomer of BillCustomerDao");
		List<BillCustomer>customerList = getCustomerData();
		
		if (customerList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.BILLCUSTOMER_ERROR_CODE));

			throw new BillCustomerNotFoundException("Error Code:" + ErrorCodeConstant.BILLCUSTOMER_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.BILLCUSTOMER_ERROR_CODE));
		}
		return customerList;
	}

	/**
	 * this method contains bilcustomer dummy data, once we used database then we will fetched data from db
	 * @return It return the list of hardcoded values 
	 */

	public List<BillCustomer> getCustomerData() {
		List<BillCustomer> customerList = new ArrayList<BillCustomer>();
		BillCustomer cust = null;

		cust = new BillCustomer("3145AJ", "Adamwruth");
		customerList.add(cust);
		cust = new BillCustomer("4623ZA", "Johnpaul");
		customerList.add(cust);
		return customerList;
	}

}
