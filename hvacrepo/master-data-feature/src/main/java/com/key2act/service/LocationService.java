package com.key2act.service;

import java.util.List;

import com.key2act.pojo.Location;

public interface LocationService {

	/**
	 * this method is used to get location
	 * 
	 * @param locationName
	 *            based on locationName location will be fetched
	 * @return return the list of Location object
	 * @throws LocationNotFoundException
	 *             If location not found then it throw LocationNotFoundException
	 * @throws InvalidLocationRequest
	 *             If locationName is null then it throw InvalidLocationRequest
	 */
	public List<Location> getLocation(String locationName) throws LocationNotFoundException, InvalidLocationRequest;

	/**
	 * this method is used to get location
	 * 
	 * @return return the list of Location object
	 * @throws LocationNotFoundException
	 *             If location not found then it throw LocationNotFoundException
	 */
	public List<Location> getAllLocation() throws LocationNotFoundException;

}
