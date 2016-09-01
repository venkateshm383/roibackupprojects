package com.getusroi.mesh.base;


import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.mesh.generic.UnableToLoadPropertiesException;

/**
 * This class is for routing to specific execution route based of service type
 * @author bizruntime
 *
 */
public class MeshBaseRouting {
	private static final Logger logger = LoggerFactory.getLogger(MeshBaseRouting.class); 

	public static final String EXECUTIONROUTE_BASEDON_SERVICETYPE_FILE="routesendpoints.properties";

	/**
	 * This method is used to route to execution route based on servicetype
	 * @param exchange : Exchange object to get mesh header
	 * @return : execution route to send in string
	 * @throws UnableToLoadPropertiesException
	 */
	public void route(Exchange exchange){
		logger.debug(".route of MeshBaseRouting");	
		
		//get  the servicetype
		MeshHeader meshHeader=(MeshHeader)exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		String featureName=meshHeader.getFeatureName();
		String servicetype=meshHeader.getServicetype();
		

		
		//get the execution route based on servicetype from propertyfile
		String executionroute=featureName.trim()+"-"+servicetype.trim()+"-TR";
		
		logger.debug("execution route to send based on servicetype is : "+executionroute.trim());
		exchange.getIn().setHeader("exeroute",executionroute.trim());
		
	}//end of route method
	
	
	

}
