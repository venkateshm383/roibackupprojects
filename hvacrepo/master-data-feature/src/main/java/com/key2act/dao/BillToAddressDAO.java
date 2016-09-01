package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.BillToAddress;
import com.key2act.service.BillToAddressNotFoundException;
import com.key2act.service.InvalidBillToAddressRequest;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class is used to perform dao operation of Bill to address
 * 
 * @author bizruntime
 *
 */
public class BillToAddressDAO {

	private static final Logger logger = LoggerFactory.getLogger(BillToAddressDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();

	/**
	 * this method is used to get bill to address
	 * 
	 * @param address:
	 *            based on this value bill to address data will be fetched
	 * @return: return arrayList object of billtoAddress
	 * @throws BillToAddressNotFoundException
	 *             if bill to address not found then it throw
	 *             BillToAddressNotFoundException
	 * @throws InvalidBillToAddressRequest
	 *             If requested address is invalid then it throw
	 *             InvalidBillToAddressRequest
	 */
	public List<BillToAddress> getBillToAddress(String address)
			throws BillToAddressNotFoundException, InvalidBillToAddressRequest {
		logger.debug("Inside getBillToAddress method of BillToAddressDao");

		if (address == null) {
			throw new InvalidBillToAddressRequest("Reques is Invalid");
		}

		List<BillToAddress> addresses = getBillToAddressData();
		List<BillToAddress> resultBillToAddress = new ArrayList<BillToAddress>();
		for (BillToAddress billToAddress : addresses) {
			if (billToAddress.getAddress().equalsIgnoreCase(address.trim())
					|| billToAddress.getCity().equalsIgnoreCase(address.trim())
					|| billToAddress.getState().equalsIgnoreCase(address.trim())
					|| billToAddress.getCountry().equalsIgnoreCase(address.trim()))
				resultBillToAddress.add(billToAddress);
		}
		if (resultBillToAddress.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.BILL_TO_ADDRESS_ERROR_CODE) + address);
			throw new BillToAddressNotFoundException("Error Code:" + ErrorCodeConstant.BILL_TO_ADDRESS_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.BILL_TO_ADDRESS_ERROR_CODE) + address);
		}
		return resultBillToAddress;
	}

	/**
	 * this method is used to get all bill address
	 * 
	 * @return: it'll return array list of bill to address
	 * @throws BillToAddressNotFoundException
	 *             if bill to address not found then it throw
	 *             BillToAddressNotFoundException
	 */
	public List<BillToAddress> getAllBillToAddress() throws BillToAddressNotFoundException {
		logger.debug("Inside getBillToAddress method of BillToAddressDao");
		List<BillToAddress> addresses = getBillToAddressData();
		if (addresses.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.BILL_TO_ADDRESS_ERROR_CODE));
			throw new BillToAddressNotFoundException("Error Code:" + ErrorCodeConstant.BILL_TO_ADDRESS_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.BILL_TO_ADDRESS_ERROR_CODE));
		}
		return addresses;
	}

	/**
	 * this method contains hardcoded data, once we use database then we will get data from db
	 * @return: return list of hard coded data
	 */
	public List<BillToAddress> getBillToAddressData() {
		List<BillToAddress> billToAddressList = new ArrayList<BillToAddress>();
		BillToAddress billToAddress = new BillToAddress("Sarjapura", "Bangalore", "KA", "5555", "India");
		billToAddressList.add(billToAddress);
		billToAddress = new BillToAddress("Sarjapura", "Bangalore", "KA", "5555", "India");
		billToAddressList.add(billToAddress);
		billToAddress = new BillToAddress("Jhotwara", "Jaipur", "Rajasthan", "302012", "India");
		billToAddressList.add(billToAddress);
		billToAddress = new BillToAddress("Sarjapura", "Bangalore", "KA", "5555", "India");
		billToAddressList.add(billToAddress);
		billToAddress = new BillToAddress("Jhotwara", "Jaipur", "Rajasthan", "302013", "India");
		billToAddressList.add(billToAddress);
		return billToAddressList;
	}

}
