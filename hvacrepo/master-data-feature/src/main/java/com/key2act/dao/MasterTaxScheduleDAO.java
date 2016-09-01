package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.MasterTaxSchedule;
import com.key2act.service.InvalidMasterTaxScheduleRequestException;
import com.key2act.service.MasterTaxScheduleNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class is used to perform dao operation of master tax scheduler
 * @author bizruntime
 *
 */
public class MasterTaxScheduleDAO {

	private static final Logger logger = LoggerFactory.getLogger(MasterTaxScheduleDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();

	/**
	 * this method is used to get master tax schedule 
	 * @param masterTaxScheduleType
	 * 					based on masterTaxScheduleType, MasterTaxSchedule will be fetched
	 * @return
	 * 			return the list of MasterTaxSchedule object
	 * @throws MasterTaxScheduleNotFoundException
	 * 			when master tax schedule not found then it throw MasterTaxScheduleNotFoundException
	 * @throws InvalidMasterTaxScheduleRequestException
	 * 			when request is invalid then it throw InvalidMasterTaxScheduleRequestException
	 */
	public List<MasterTaxSchedule> getMasterTaxSchedule(String masterTaxScheduleType) throws MasterTaxScheduleNotFoundException, InvalidMasterTaxScheduleRequestException {
		logger.debug("Inside getMasterTaxSchedule method of MasterTaxScheduleDao");
		
		if(masterTaxScheduleType == null)
			throw new InvalidMasterTaxScheduleRequestException("Invalid Master Schedule");
		
		List<MasterTaxSchedule> resultMasterTaxSchedules = new ArrayList<MasterTaxSchedule>();
		List<MasterTaxSchedule> masterTaxScheduleData = getMasterTaxScheduleData();
		for(MasterTaxSchedule masterTaxSchedule : masterTaxScheduleData) {
			if(masterTaxSchedule.getMasterTaxScheduleType().equalsIgnoreCase(masterTaxScheduleType.trim()))
				resultMasterTaxSchedules.add(masterTaxSchedule);
		}
		if(resultMasterTaxSchedules.isEmpty()) {
			logger.info(prop.getProperty(ErrorCodeConstant.MASTER_TAX_SCHEDULE_ERROR_CODE) + masterTaxScheduleType);
			throw new MasterTaxScheduleNotFoundException("Error Code:" + ErrorCodeConstant.MASTER_TAX_SCHEDULE_ERROR_CODE + "," + prop.getProperty(ErrorCodeConstant.MASTER_TAX_SCHEDULE_ERROR_CODE) + masterTaxScheduleType);
		}
		return resultMasterTaxSchedules;
	}//end of method
	
	/**
	 * this method is used to get master tax schedule 
	 * @return
	 * 			return the list of MasterTaxSchedule object
	 * @throws MasterTaxScheduleNotFoundException
	 * 			when master tax schedule not found then it throw MasterTaxScheduleNotFoundException
	 */
	public List<MasterTaxSchedule> getAllMasterTaxSchedule() throws MasterTaxScheduleNotFoundException {
		logger.debug("Inside getAllMasterTaxSchedule method of MasterTaxScheduleDao");
		List<MasterTaxSchedule> masterTaxScheduleData = getMasterTaxScheduleData();
		if(masterTaxScheduleData.isEmpty()) {
			logger.info(prop.getProperty(ErrorCodeConstant.MASTER_TAX_SCHEDULE_ERROR_CODE));
			throw new MasterTaxScheduleNotFoundException("Error Code:" + ErrorCodeConstant.MASTER_TAX_SCHEDULE_ERROR_CODE + "," + prop.getProperty(ErrorCodeConstant.MASTER_TAX_SCHEDULE_ERROR_CODE));
		}
		return masterTaxScheduleData;
	}//end of method
	
	/**
	 * this method contains dummy data, once we will connect with database then we'll fetch data from db
	 * @return, it return the list of MasterTaxSchedule objects
	 */
	public List<MasterTaxSchedule> getMasterTaxScheduleData() {
		ArrayList<MasterTaxSchedule> masterTaxSchedules = new ArrayList<MasterTaxSchedule>();
		MasterTaxSchedule masterTaxSchedule = new MasterTaxSchedule("1001", "AVATAX");
		masterTaxSchedules.add(masterTaxSchedule);
		masterTaxSchedule = new MasterTaxSchedule("1002", "AVATAX");
		masterTaxSchedules.add(masterTaxSchedule);
		masterTaxSchedule = new MasterTaxSchedule("1003", "PayrollTax");
		masterTaxSchedules.add(masterTaxSchedule);
		masterTaxSchedule = new MasterTaxSchedule("1004", "PayrollTax");
		masterTaxSchedules.add(masterTaxSchedule);
		masterTaxSchedule = new MasterTaxSchedule("1005", "AVATAX");
		masterTaxSchedules.add(masterTaxSchedule);
		return masterTaxSchedules;
	}//end of method

}
