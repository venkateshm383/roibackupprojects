package com.key2act.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.dao.LocationDAO;
import com.key2act.pojo.Location;
import com.key2act.service.InvalidLocationRequest;
import com.key2act.service.LocationNotFoundException;
import com.key2act.service.LocationService;

/**
 * this class is used to perform service methods for location service
 * @author bizruntime
 *
 */
public class LocationServiceImpl implements LocationService {

	private static final Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);
	
	/**
	 * this method is used to get location 
	 * @param locationName
	 * 			based on locationName location will be fetched
	 * @return
	 * 			return the list of Location object
	 * @throws LocationNotFoundException
	 * @throws InvalidLocationRequest 
	 */
	public List<Location> getLocation(String location) throws LocationNotFoundException, InvalidLocationRequest {
		logger.debug("Inside getLocation of LocationServiceImpl");
		LocationDAO loc = new LocationDAO();
		List<Location> locationLists = loc.getLocation(location);;
		return locationLists;
	}
	
	/**
	 * this method is used to get location 
	 * @return
	 * 			return the list of Location object
	 * @throws LocationNotFoundException
	 */
	@Override
	public List<Location> getAllLocation() throws LocationNotFoundException {
		logger.debug("Inside getLocation of LocationServiceImpl");
		LocationDAO loc = new LocationDAO();
		List<Location> locationLists = loc.getAllLocation();
		return locationLists;
	}

}
