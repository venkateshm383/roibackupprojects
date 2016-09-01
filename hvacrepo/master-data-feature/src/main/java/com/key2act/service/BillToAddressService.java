package com.key2act.service;

import java.util.List;

import com.key2act.pojo.BillToAddress;

public interface BillToAddressService {
	
	/**
	 * this method is used to get bill to address
	 * @param address
	 * 				based on address parameter billToAddress data will be fetched
	 * @return 
	 * 			return the list of BillToAddress object
	 * @throws BillToAddressNotFoundException	if bill to address not found then it throw BillToAddressNotFoundException
	 * @throws	InvalidBillToAddressRequest		if address is null then it throw InvalidBillToAddressRequest
	 */
	public List<BillToAddress> getBillToAddress(String address) throws BillToAddressNotFoundException, InvalidBillToAddressRequest;
	
	/**
	 * this method is used to get all bill to address
	 * @return
	 * 			return the list of BillToAddress object
	 * @throws BillToAddressNotFoundException	if bill to address not found then it throw BillToAddressNotFoundException
	 */
	public List<BillToAddress> getAllBillToAddress() throws BillToAddressNotFoundException; 

}
