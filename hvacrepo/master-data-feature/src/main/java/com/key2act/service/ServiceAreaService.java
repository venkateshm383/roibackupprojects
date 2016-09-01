package com.key2act.service;

import java.util.List;

import com.key2act.pojo.ServiceArea;

public interface ServiceAreaService {

	/**
	 * this method is used to get service area
	 * 
	 * @param area
	 *            based on area, ServiceArea details will be fetched
	 * @return this method will return the list of ServiceArea objects
	 * @throws ServiceAreaNotFoundException,
	 *             If Service area is not found then It throw
	 *             ServiceAreaNotFoundException
	 * @throws InvalidServiceAreaRequest,
	 *             If request is invalid then it throw InvalidServiceAreaRequest
	 */
	public List<ServiceArea> getServiceArea(String area) throws ServiceAreaNotFoundException, InvalidServiceAreaRequest;

	/**
	 * this method is used to get service area
	 * 
	 * @return this method will return the list of ServiceArea objects
	 * @throws ServiceAreaNotFoundException,
	 *             If Service area is not found then It throw
	 *             ServiceAreaNotFoundException
	 */
	public List<ServiceArea> getAllServiceArea() throws ServiceAreaNotFoundException;
}
