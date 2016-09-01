package com.key2act.timetracker.service.helper;

import java.util.Map;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.key2act.timetracker.service.InvalidRequestDataException;

/**
 * 
 * @author bizruntime
 *
 */
public class ServiceRequestBuilder {
	Logger logger = LoggerFactory.getLogger(ServiceRequestBuilder.class);

	/**
	 * method to fetch the service request data if the 'data' keyword is present
	 * 
	 * @param exchange
	 * @return the service request object
	 * @throws InvalidRequestDataException
	 */
	public JSONObject getRequestData(Exchange exchange)
			throws InvalidRequestDataException {
		String request = exchange.getIn().getBody(String.class);
		JSONObject requestObj;
		try {
			requestObj = new JSONObject(request);
			if (requestObj.has(MeshHeaderConstant.DATA_KEY)) {
				JSONArray requestObjArray = (JSONArray) requestObj.get(MeshHeaderConstant.DATA_KEY);
				requestObj = requestObjArray.getJSONObject(0);
			}
		} catch (JSONException e) {
			exchange.getIn().setBody("Request data invalid, request data must start with 'data' keyword");
			throw new InvalidRequestDataException("Request data invalid, request data must start with 'data' keyword", e);
		}
		return requestObj;
	}

	/**
	 * method to fetch the permastore data from the mesh header 
	 * 
	 * @param exchange
	 * @return the permadata from the mesh header
	 */
	public Map<String, Object> getPermaStoreDataFromMeshHeader(Exchange exchange) {
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(
				MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> permadata = meshHeader.getPermadata();
		logger.info("The permaData in mesh header : " + permadata);
		// exchange.getIn().setBody(permadata);
		return permadata;
	}
}
