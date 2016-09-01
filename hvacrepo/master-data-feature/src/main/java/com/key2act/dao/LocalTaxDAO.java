package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.LocalTax;
import com.key2act.service.InvalidLocalTaxRequestException;
import com.key2act.service.LocalTaxNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class is used to perform dao operation of local tax
 * 
 * @author bizruntime
 *
 */
public class LocalTaxDAO {

	private static final Logger logger = LoggerFactory.getLogger(LocalTaxDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();

	/**
	 * this method is used to get LocalTaxId
	 * 
	 * @param localTaxName
	 *            based on localTaxName, list of LocalTaxId objects will be
	 *            fetched
	 * @return It will return the list of localTax objects
	 * @throws LocalTaxNotFoundException
	 *             if localTaxName is not found then it throw
	 *             LocalTaxNotFoundException
	 * @throws InvalidLocalTaxRequestException
	 *             If localTaxName is invalid request then it throw
	 *             InvalidLocalTaxRequestException
	 */
	public List<LocalTax> getLocalTaxId(String localTaxName)
			throws LocalTaxNotFoundException, InvalidLocalTaxRequestException {
		logger.debug("Inside getLocalTaxId of localTypeDao");
		if (localTaxName == null)
			throw new InvalidLocalTaxRequestException("Invalid taskcode request");

		List<LocalTax> localists = getlocalTaxData();
		List<LocalTax> localtaxList = new ArrayList<LocalTax>();
		logger.debug("getLocalTaxId of  localTypeDao : " + localTaxName);
		for (LocalTax local : localists) {
			if (local.getlocalTaxId().equalsIgnoreCase(localTaxName.trim()))
				localtaxList.add(local);
			else if (local.getlocalTaxName().equalsIgnoreCase(localTaxName.trim()))
				localtaxList.add(local);
		}
		if (localtaxList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.LOCALTAX_ERROR_CODE) + localTaxName);
			throw new LocalTaxNotFoundException("Error Code:" + ErrorCodeConstant.LOCALTAX_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.LOCALTAX_ERROR_CODE) + localTaxName);
		}
		return localtaxList;
	}

	/**
	 * this method is used to get all localTaxId
	 * 
	 * @return It will return the list of localTaxId objects
	 * @throws LocalTaxNotFoundException
	 *             if localTaxName is not found then it throw
	 *             LocalTaxNotFoundException
	 */
	public List<LocalTax> getAllLocalTaxId() throws LocalTaxNotFoundException {
		logger.debug("Inside getAllLocalTaxId of localTypeDao");

		List<LocalTax> localtaxList = getlocalTaxData();
		logger.debug("getAllLocalTaxId of  localTypeDao : ");

		if (localtaxList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.LOCALTAX_ERROR_CODE));

			throw new LocalTaxNotFoundException("Error Code:" + ErrorCodeConstant.LOCALTAX_ERROR_CODE + ", "
					+ prop.getProperty(ErrorCodeConstant.LOCALTAX_ERROR_CODE));

		}
		return localtaxList;
	}

	/**
	 * this method contains localTaxId dummy data, once we used database then we
	 * will fetched data from db
	 * 
	 * @return It return the list of hardcoded values
	 */
	public List<LocalTax> getlocalTaxData() {
		List<LocalTax> localList = new ArrayList<LocalTax>();
		LocalTax local = null;
		local = new LocalTax("1VN", "Vendo Tax");
		localList.add(local);
		local = new LocalTax("2ST", "Sales Tax");
		localList.add(local);
		local = new LocalTax("3CT", "Consumer Tax");
		localList.add(local);
		return localList;
	}

}
