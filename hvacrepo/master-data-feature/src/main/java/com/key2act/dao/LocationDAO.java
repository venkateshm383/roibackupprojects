package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.Location;
import com.key2act.service.InvalidLocationRequest;
import com.key2act.service.LocationNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class is used to do all dao operation related to location
 * @author bizruntime
 *
 */
public class LocationDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(LocationDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();
	
	/**
	 * this method is used to get locationId by location name
	 * @param location, based on location, location details will be fetched 
	 * @return return the list of Location object
	 * @throws LocationNotFoundException	throw LocationNotFoundException if location is not found
	 * @throws InvalidLocationRequest If location is null then it throw InvalidLocationRequest
	 */
	public List<Location> getLocation(String location) throws LocationNotFoundException, InvalidLocationRequest {
		logger.debug("Inside getLocationIdByName of LocationDao");
		
		if(location == null)
			throw new InvalidLocationRequest("Invalid location request");
		
		List<Location> locationLists = getLocationData();
		List<Location> resultLocationList = new ArrayList<Location>();
		for(Location loc : locationLists) {
			if(loc.getLocation().equalsIgnoreCase(location.trim()))
				resultLocationList.add(loc);
		}
		if(resultLocationList.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.LOCATION_ERROR_CODE) + location);
			throw new LocationNotFoundException("Error Code:" + ErrorCodeConstant.LOCATION_ERROR_CODE + "," + prop.getProperty(ErrorCodeConstant.LOCATION_ERROR_CODE) + location);
		}
		return resultLocationList;
	}
	
	/**
	 * this method is used to get all location
	 * @return return the list of Location object
	 * @throws LocationNotFoundException	throw LocationNotFoundException if location is not found
	 */
	public List<Location> getAllLocation() throws LocationNotFoundException {
		logger.debug("Inside getLocationIdByName of LocationDao");
		List<Location> resultLocationLists = getLocationData();
		if(resultLocationLists.isEmpty()) {
			logger.debug(prop.getProperty(ErrorCodeConstant.LOCATION_ERROR_CODE));
			throw new LocationNotFoundException("Error Code:" + ErrorCodeConstant.LOCATION_ERROR_CODE + "," + prop.getProperty(ErrorCodeConstant.LOCATION_ERROR_CODE));
		}
		return resultLocationLists;
	}//end of method
	
	/**
	 * this method is contains all dummy data
	 * @return   It return the list of hardcoded values, once we used database then we will fetched data from db
	 */
	public List<Location> getLocationData() {
		List<Location> locationList = new ArrayList<Location>();
		Location location = null;
		location = new Location("1001", "London");
		locationList.add(location);
		location = new Location("1002", "Sichago");
		locationList.add(location);
		location = new Location("1003", "London");
		locationList.add(location);
		return locationList;
	}//end of method
	
}
