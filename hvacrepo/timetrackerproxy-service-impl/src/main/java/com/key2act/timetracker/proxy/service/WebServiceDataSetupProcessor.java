package com.key2act.timetracker.proxy.service;

import static com.key2act.timetracker.proxy.util.TimeTrackerProxyConstants.*;
import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.config.RequestContext;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.staticconfig.StaticConfigInitializationException;
import com.getusroi.staticconfig.impl.AccessProtectionException;
import com.getusroi.staticconfig.util.LocalfileUtil;

public class WebServiceDataSetupProcessor {
	Logger logger = LoggerFactory.getLogger(WebServiceDataSetupProcessor.class);

	/**
	 * method to process the JsonObject provided to it and get
	 * 
	 * @param exchange
	 * @throws IllegalRequestJSONObjectException
	 * @throws AccessProtectionException 
	 * @throws StaticConfigInitializationException 
	 */
	public void processBean(Exchange exchange) throws IllegalRequestJSONObjectException, StaticConfigInitializationException, AccessProtectionException {
		String input = exchange.getIn().getBody(String.class);
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		JSONObject requestObj = null;
		try {
			requestObj = new JSONObject(input);
			JSONArray requestObjArray = requestObj.getJSONArray(MeshHeaderConstant.DATA_KEY);
			requestObj = requestObjArray.getJSONObject(0);
			logger.debug("requestObj : " + requestObj.toString());
			if (requestObj.has(SERVICE_DATA_KEY)) {
				JSONObject requestOperationObject = new JSONObject();
				requestOperationObject = requestObj.getJSONObject(SERVICE_DATA_KEY);
				RequestContext reqCtx = meshHeader.getRequestContext();
				LocalfileUtil localfileUtil = new LocalfileUtil();
				
				String requestXsl = requestOperationObject.getString(REQUEST_XSLT_FILE_KEY);
				requestXsl = localfileUtil.getStaticFilePath(reqCtx,requestXsl);
				String responseXsl = requestOperationObject.getString(RESPONSE_XSLT_FILE_KEY);
				responseXsl = localfileUtil.getStaticFilePath(reqCtx,responseXsl);
				String wsURL = requestOperationObject.getString(WS_URL_PERMA_KEY);
				
				exchange.getOut().setHeader("requestXSL", requestXsl);
				exchange.getOut().setHeader("responseXSL", responseXsl);
				exchange.getOut().setHeader("webServiceURL", wsURL);
				try {
					String xml = XML.toString(requestObj);
					exchange.getOut().setBody(xml);
				} catch (JSONException e) {
					throw new IllegalRequestJSONObjectException(
							"The request JSONObject passed from the handler service is not well Formed to be converted into XML");
				}
			} else {
				throw new IllegalRequestJSONObjectException(
						"Unable to find ServiceData Key in the JsonObject passed to the service, Check for the ServiceHandler passing proper JSONObject or not");
			}
		} catch (JSONException e) {
			throw new IllegalRequestJSONObjectException(
					"The JSON Object passed doesnt have the keys required to store into the Exchange Headers, which will be needed later, please check with the JSONObject being passed");
		}
	}
}