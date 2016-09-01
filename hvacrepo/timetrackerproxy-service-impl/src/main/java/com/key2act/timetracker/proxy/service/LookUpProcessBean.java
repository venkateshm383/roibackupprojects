package com.key2act.timetracker.proxy.service;

import static com.key2act.timetracker.proxy.util.TimeTrackerProxyConstants.*;
import java.util.Map;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;

public class LookUpProcessBean {
	/**
	 * method to fetch the jsonObject from the request and get the tenantID and
	 * the service type
	 * 
	 * @param exchange
	 * @throws LookUpRequestInvalidException
	 */
	static Logger logger = LoggerFactory.getLogger(LookUpProcessBean.class); 
	public void serviceDeciderForListings(Exchange exchange) throws LookUpRequestInvalidException, PermaStoreParserExceptionForServiceData {
		// Check the request to be in JSON format
		String requestString = exchange.getIn().getBody(String.class);
		try {
			JSONObject requestJsonObject = new JSONObject(requestString);
			logger.debug("requestJsonObject : "+requestJsonObject.toString());
			JSONArray requestJsonArray =  requestJsonObject.getJSONArray(MeshHeaderConstant.DATA_KEY);
			logger.debug("requestJsonArray : "+requestJsonArray.toString());
			requestJsonObject = (JSONObject) requestJsonArray.get(0);
			logger.debug("requestJsonObject : "+requestJsonObject.toString());
			if (!requestJsonObject.has("tenantID") && !requestJsonObject.has("serviceType")) {
				throw new LookUpRequestInvalidException("The JsonObject must contain tenantID and serviceType as Tags");
			}
			logger.debug("before sending to load permaStore");
			logger.debug("Request : tenantID : "+requestJsonObject.getString("tenantID")+" : servicetype :"+requestJsonObject.getString("serviceType"));
			loadPermaDataForTenant(exchange, requestJsonObject.getString("tenantID"),
					requestJsonObject.getString("serviceType"));
			
		} catch (JSONException e) {
			throw new LookUpRequestInvalidException("The Request must be well formed JSON Object");
		}
	}

	/**
	 * method to get the name of the webserviceURL and connection String from
	 * the permaStore and pass the
	 * 
	 * @param exchange
	 * @param tenantID
	 * @throws LookUpRequestInvalidException
	 * @throws PermaStoreParserExceptionForServiceData 
	 */
	private void loadPermaDataForTenant(Exchange exchange, String tenantID, String serviceType)
			throws LookUpRequestInvalidException, PermaStoreParserExceptionForServiceData {
		JSONObject permaJsonObject = null;
		
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> permaCachedata = meshHeader.getPermadata();
		Object permaJsonObjectInString = permaCachedata.get(TENANT_TO_VERSION_WSURL_MAPPING_PERMA_CONFIG_KEY);
		try {
		permaJsonObject = new JSONObject(permaJsonObjectInString.toString());
		logger.debug(permaJsonObject.toString());
		// fetching the tenantID's associated jsonObject
			permaJsonObject = permaJsonObject.getJSONObject(tenantID);
			logger.debug(".inside loadPermaDataForTenant of LookUpProcessBean - PermaJsonObject for tenant : "+permaJsonObject.toString() );
		} catch (JSONException e) {
			throw new LookUpRequestInvalidException("TenantID not found in the request");
		}
		try {
			String connectionString = permaJsonObject.getString(CONNECTION_STRING_PERMA_KEY);
			String webServiceEndpoint = permaJsonObject.getString(WEBSERVICE_ENDPOINT_PERMA_KEY);
			String version = permaJsonObject.getString(VERSION_PERMA_KEY);
			loadPermaDataForVersion(exchange, version, connectionString, webServiceEndpoint, permaCachedata, serviceType);
		} catch (JSONException e) {
			throw new LookUpRequestInvalidException(
					"The ConnectionString or the Web Service URL in the permaStore is not configured Properly, it must containg connectionString and webServiceURL");
		}
	}

	/**
	 * 
	 * method to load the data from the VERSION_TO_XSL_CONNECTION_STRING
	 * permaStore create a JSONObject which will hold the connectionString for
	 * the version and will also hold the xslt files
	 * @throws PermaStoreParserExceptionForServiceData 
	 */
	public void loadPermaDataForVersion(Exchange exchange, String version, String connectionString,
			String webServiceEndpoint, Map<String, Object> permaCachedata, String serviceType) throws PermaStoreParserExceptionForServiceData {
		Object permaObject =  permaCachedata.get(VERSION_TO_XSL_CONNECTION_STRING);
		logger.debug(".loadPermaDataForVersion inside LookUpProcessBean - permaObject : "+permaObject.toString());
		try {
		JSONObject permaJsonObject = new JSONObject(permaObject.toString());
		permaJsonObject = permaJsonObject.getJSONObject(version);
		permaJsonObject = permaJsonObject.getJSONObject(serviceType);
			String requestXSLFileName = permaJsonObject.getString(REQUEST_TRANSFORMER_XSL_FILE_PERMA_KEY);
			String responseXSLFileName = permaJsonObject.getString(RESPONSE_TRANSFORMER_XSL_FILE_PERMA_KEY);
			JSONObject serviceRequest = new JSONObject();
			serviceRequest.put(CONNECTION_STRING_PERMA_KEY, connectionString);
			serviceRequest.put(REQUEST_TRANSFORMER_XSL_FILE_PERMA_KEY, requestXSLFileName);
			serviceRequest.put(RESPONSE_TRANSFORMER_XSL_FILE_PERMA_KEY, responseXSLFileName);
			serviceRequest.put(WS_URL_PERMA_KEY, webServiceEndpoint);
			JSONObject serviceRequestJsonObjectWithKey = new JSONObject();
			serviceRequestJsonObjectWithKey.put("ServiceData", serviceRequest);
			exchange.getIn().setHeader("servicetype", serviceType);
			exchange.getIn().setBody("{\"data\":["+serviceRequestJsonObjectWithKey+"]}");
		} catch (JSONException e) {
			throw new PermaStoreParserExceptionForServiceData("Unable to get the the Required Key from the PermaStore to create a JSON object for the Services");
		}

	}
}