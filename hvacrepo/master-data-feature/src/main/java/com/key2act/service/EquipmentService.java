package com.key2act.service;

import java.util.List;

import com.key2act.pojo.Equipment;

public interface EquipmentService {

	/**
	 * this method is used to get equipmentId details based on customer name
	 * 
	 * @param equipmentName
	 *            based on equipmentName details will be fetched
	 * @return return the json data in string format
	 * @throws EquipmentNotFoundException
	 *             throw EquipmentIdNotFoundException when equipmentId details
	 *             are not available
	 * @throws InvalidEquipmentRequestException
	 * 				if equipmentName is invalid then it throw InvalidEquipmentRequestException            
	 *
	 */

	public List<Equipment> getEquipmentId(String equipmentName)
			throws EquipmentNotFoundException, InvalidEquipmentRequestException;

	/**
	 * this method is use to get all equipmentId details
	 * 
	 * @throws EquipmentNotFoundException:when
	 *             equipmentId details not found
	 * 
	 */
	public List<Equipment> getAllEquipmentId() throws EquipmentNotFoundException;

}
