package com.key2act.proxysignaturehandler;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.getusroi.mesh.MeshHeaderConstant;
import com.key2act.proxysignaturehandler.util.ProxySignatureOperationConstants;

public class ProxySignatureOperationImpl {
	
	public void configureOperationServiceToCall(Exchange exchange) throws UnableToParseRequestException{
		String operation=null;
		String tenantName = null;
		String request = exchange.getIn().getBody(String.class);
		try {
			JSONObject reqJsonObj = new JSONObject(request);
			JSONArray dataJsonArray = reqJsonObj.getJSONArray(MeshHeaderConstant.DATA_KEY);
			reqJsonObj = (JSONObject) dataJsonArray.get(0);
			reqJsonObj = (JSONObject) reqJsonObj.get(ProxySignatureOperationConstants.SIGNATURE_DATA_ROOT_KEY);
			operation = reqJsonObj.getString(ProxySignatureOperationConstants.OPERATION_KEY);
			tenantName = reqJsonObj.getString(ProxySignatureOperationConstants.TENANT_NAME_KEY);
			exchange.getIn().setHeader(ProxySignatureOperationConstants.OPERATION_KEY, operation);
			exchange.getIn().setHeader(ProxySignatureOperationConstants.TENANT_NAME_KEY, tenantName);
		} catch (JSONException e) {
			throw new UnableToParseRequestException("Unable to parse Request Data provided to the Service");
		}
	}
}