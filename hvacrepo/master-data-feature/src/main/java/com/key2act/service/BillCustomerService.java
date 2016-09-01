package com.key2act.service;

import java.util.List;

import com.key2act.pojo.BillCustomer;

/**
 * this interface is used to get data from billcustomer dao
 * 
 * @author bizruntime-10
 * 
 */

public interface BillCustomerService {

	/**
	 * this method is used to get customer details based on customer name
	 * 
	 * @param customerName
	 *            based on customer name customer details will be fetched
	 * @return return the json data in string format
	 * @throws InvalidBillCustomerRequestException
	 *             If customerName is null then it throw
	 *             InvalidBillCustomerRequestException
	 * @throws BillCustomerNotFoundException
	 *             throw BillCustomerNotFoundException when customer details are
	 *             notfound
	 */

	public List<BillCustomer> getbillCustomer(String customerName)
			throws BillCustomerNotFoundException, InvalidBillCustomerRequestException;

	/**
	 * this method is use to get all customer details
	 * 
	 * @throws BillCustomerNotFoundException:
	 *             when billcustomer details not found
	 *
	 */
	public List<BillCustomer> getAllbillCustomer() throws BillCustomerNotFoundException;
}
