package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.EquipmentDAO;
import com.key2act.pojo.Equipment;
import com.key2act.service.BillCustomerNotFoundException;
import com.key2act.service.EquipmentNotFoundException;
import com.key2act.service.EquipmentService;
import com.key2act.service.InvalidEquipmentRequestException;

/**
 * this class is used to perform service methods for Equipment
 * 
 * @author bizruntime
 *
 */
public class EquipmentImpl implements EquipmentService {

	private static final Logger logger = LoggerFactory.getLogger(EquipmentImpl.class);

	/**
	 * this method is used to get euipmentId
	 * 
	 * @param equipmentName
	 *            based on equipmentName, list of euipmentId objects will be
	 *            fetched
	 * @return It will return the list of euipment objects
	 * @throws EquipmentNotFoundException
	 *             if customerName is not found then it throw
	 *             EquipmentIdNotFoundException
	 * @throws InvalidEquipmentRequestException
	 *             If equipmentName is invalid request then it throw
	 *             InvalidEquipmentIdRequestException
	 */

	public List<Equipment> getEquipmentId(String equipmentName)
			throws EquipmentNotFoundException, InvalidEquipmentRequestException {
		if (equipmentName == null)
			throw new InvalidEquipmentRequestException(" Invalid EquipmentId request");
		EquipmentDAO equipmentDAO = new EquipmentDAO();
		List<Equipment> equipmentList = equipmentDAO.getEquipmentId(equipmentName);
		return equipmentList;
	}

	/**
	 * this method is used to get all equipmentid
	 * 
	 * @return It will return the list of equipment objects
	 * @throws BillCustomerNotFoundException,if
	 *             equipmentName is not found then it throw
	 *             EquipmentIdNotFoundException
	 */
	@Override
	public List<Equipment> getAllEquipmentId() throws EquipmentNotFoundException {
		logger.debug("Inside getAllEquipmentId of EquipmentIdImpl");
		EquipmentDAO equipmentDAO = new EquipmentDAO();
		List<Equipment> equipmentList = equipmentDAO.getAllEquipmentId();
		return equipmentList;
	}
}