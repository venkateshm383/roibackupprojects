package com.key2act.service;

import java.util.List;

import com.key2act.pojo.Division;

/**
 * this interface contains all service methods to get division data
 * @author bizruntime
 *
 */
public interface DivisionService {
	
	/** 
	 * this method is used to get division
	 * @param divisionName
	 * 					based on divisionName, list of division objects will be fetched 
	 * @return
	 * 			It will return the list of division objects
	 * @throws DivisionNotFoundException	if Division is not found then it throw DivisionNotFoundException
	 * @throws InvalidDivisionRequestException 	If divisionName is invalid request then it throw InvalidDivisionRequestException
	 */
	public List<Division> getDivision(String divisionName) throws DivisionNotFoundException, InvalidDivisionRequestException;
	
	/** 
	 * this method is used to get division
	 * @param divisionName
	 * 					based on divisionName, list of division objects will be fetched 
	 * @return
	 * 			It will return the list of division objects
	 * @throws DivisionNotFoundException	if Division is not found then it throw DivisionNotFoundException
	 */
	public List<Division> getAllDivision() throws DivisionNotFoundException; 

}
