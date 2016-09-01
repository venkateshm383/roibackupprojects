package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.Equipment;
import com.key2act.service.EquipmentNotFoundException;
import com.key2act.service.InvalidEquipmentRequestException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class is used to perform dao operation of equipment
 * 
 * @author bizruntime
 * 
 */
public class EquipmentDAO {

	private static final Logger logger = LoggerFactory.getLogger(EquipmentDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();

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
		logger.debug("Inside getEquipmentId of EquipmentDao");
		if (equipmentName == null)
			throw new InvalidEquipmentRequestException("Invalid EquipmentId request");

		List<Equipment> equipLists = getEquipmentData();
		List<Equipment> equipmentList = new ArrayList<Equipment>();
		logger.debug("getEquipmentId of EquipmentDao :" + equipmentName);
		for (Equipment equip : equipLists) {
			if (equip.getequipmentId().equalsIgnoreCase(equipmentName.trim()))
				equipmentList.add(equip);

		}
		if (equipmentList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.EQUIPMENTID_ERROR_CODE) + equipmentName);

			throw new EquipmentNotFoundException("Error Code:" + ErrorCodeConstant.EQUIPMENTID_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.EQUIPMENTID_ERROR_CODE) + equipmentName);
		}
		return equipmentList;
	}

	/**
	 * this method is used to get all equipmentid
	 * 
	 * @return It will return the list of equipment objects
	 * @throws EquipmentNotFoundException,if
	 *             equipmentName is not found then it throw
	 *             EquipmentIdNotFoundException
	 */
	public List<Equipment> getAllEquipmentId() throws EquipmentNotFoundException {
		logger.debug("Inside getAllEquipmentId of EquipmentDao");

		List<Equipment> equipmentList = getEquipmentData();
		logger.debug("getAllEquipmentId of EquipmentDao :");

		if (equipmentList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.EQUIPMENTID_ERROR_CODE));

			throw new EquipmentNotFoundException("Error Code:" + ErrorCodeConstant.EQUIPMENTID_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.EQUIPMENTID_ERROR_CODE));
		}
		return equipmentList;
	}

	/**
	 * this method contains equipmentId dummy data, once we used database then
	 * we will fetched data from db
	 * 
	 * @return It return the list of hardcoded values
	 */
	public List<Equipment> getEquipmentData() {
		List<Equipment> equipmentList = new ArrayList<Equipment>();
		Equipment equip = null;
		equip = new Equipment("1", "Dell Server");
		equipmentList.add(equip);
		equip = new Equipment("2", "Asus Laptop");
		equipmentList.add(equip);
		return equipmentList;
	}
}
