package com.key2act.service;

import java.util.List;

import com.key2act.pojo.MasterTaxSchedule;

public interface MasterTaxScheduleService {
	
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
	public List<MasterTaxSchedule> getMasterTaxSchedule(String masterTaxScheduleType) throws MasterTaxScheduleNotFoundException, InvalidMasterTaxScheduleRequestException;
	
	/**
	 * this method is used to get master tax schedule 
	 * @return
	 * 			return the list of MasterTaxSchedule object
	 * @throws MasterTaxScheduleNotFoundException
	 * 			when master tax schedule not found then it throw MasterTaxScheduleNotFoundException
	 */
	public List<MasterTaxSchedule> getAllMasterTaxSchedule() throws MasterTaxScheduleNotFoundException;
	
}
