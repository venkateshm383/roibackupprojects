package com.getusroi.mesh.randomuuid;

import java.util.UUID;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;



public class RandomStringUUID {
	final Logger logger = LoggerFactory.getLogger(RandomStringUUID.class);

	/**
	 * This method is to generate random uuid for request
	 * @param exchange :Exchange Object
	 */
	public  void uuidgenrate(Exchange exchange){
     logger.debug(".uuidgenerate() of RandomStringUUID");
     
     //generating the random uuid
		  UUID uuid = UUID.randomUUID();
	        String randomUUIDString = uuid.toString();
	        logger.debug("random uuid"+randomUUIDString);
	        
	        MeshHeader meshHeader=(MeshHeader)exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
	        
	        meshHeader.setRequestUUID(randomUUIDString);
	     
		
		
	}//end of method

}
