package com.key2act.timetracker.service.helper;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.mesh.MeshHeaderConstant;
import com.key2act.timetracker.service.InvalidRequestDataException;

public class RequestBuilder {

	public JSONObject getRequestData(Exchange exchange)
			throws InvalidRequestDataException {
		String request = exchange.getIn().getBody(String.class);
		JSONObject requestObj;
		try {
			requestObj = new JSONObject(request);
			JSONArray requestObjArray = (JSONArray) requestObj.get(MeshHeaderConstant.DATA_KEY);
			requestObj = requestObjArray.getJSONObject(0);
		} catch (JSONException e) {
			exchange.getIn().setBody("Request data invalid, tenantID must be specified");
			throw new InvalidRequestDataException("Request data invalid, tenantID must be specified", e);
		}
		return requestObj;
	}
}
