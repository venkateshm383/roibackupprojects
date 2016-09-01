package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.BillToAddressDAO;
import com.key2act.pojo.BillToAddress;
import com.key2act.service.BillToAddressNotFoundException;
import com.key2act.service.BillToAddressService;
import com.key2act.service.CustomerInvalidRequestException;
import com.key2act.service.InvalidBillToAddressRequest;

/**
 * this class is used to perform service methods for Bill to address
 * @author bizruntime
 *
 */
public class BillToAddressServiceImpl implements BillToAddressService {

	private static final Logger logger = LoggerFactory.getLogger(BillToAddressServiceImpl.class);
	private BillToAddressDAO addressDao = new BillToAddressDAO();
	public BillToAddressServiceImpl() {
		addressDao = new BillToAddressDAO();
	}
	
	/**
	 * this method is used to get bill to address
	 * @param address
	 * 				based on address parameter billToAddress data will be fetched
	 * @return 
	 * 			return the list of BillToAddress object
	 * @throws BillToAddressNotFoundException
	 * @throws InvalidBillToAddressRequest 
	 * @throws CustomerInvalidRequestException 
	 */
	public List<BillToAddress> getBillToAddress(String address) throws BillToAddressNotFoundException, InvalidBillToAddressRequest {
		logger.debug("Inside getBillToAddress method of BillToAddressServiceImpl");
		
		if(address == null) {
			throw new InvalidBillToAddressRequest("Request is Invalid");
		}
		
		List<BillToAddress> billToAddresses = addressDao.getBillToAddress(address);;
		return billToAddresses;
	}
	
	/**
	 * this method is used to get all bill to address
	 * @return
	 * 			return the list of BillToAddress object
	 * @throws BillToAddressNotFoundException
	 */
	@Override
	public List<BillToAddress> getAllBillToAddress() throws BillToAddressNotFoundException {
		logger.debug("Inside getAllBillToAddress method of BillToAddressServiceImpl");
		List<BillToAddress> billToAddresses;
		billToAddresses = addressDao.getAllBillToAddress();
		return billToAddresses;
	}
	
}
