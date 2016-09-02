package com.key2act.signatureproxy.util;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.key2act.signatureproxy.UnableToparseRequestDataException;


public class ExchangeTransformer {
	/**
	 * exchange transformation from JSON Exchange to XML Transformation
	 * 
	 * @param exchange
	 * @throws UnableToparseRequestDataException
	 */
	public void exchangeTransformationJSONtoXML(Exchange exchange) throws UnableToparseRequestDataException {
		String jsonInput = exchange.getIn().getBody(String.class);
		// unmarshalls only if the exchange contains json data
		try {
			if (jsonInput.startsWith("{")) {
				JSONObject jsonObj = new JSONObject(jsonInput);
				JSONArray jsonarray = (JSONArray) jsonObj.get("data");
				String xml;
				xml = XML.toString(jsonarray.get(0));
				exchange.getIn().setBody(xml);
				//System.out.println("The exchange body ============== "+exchange.getIn().getBody(String.class));
			} else {
				exchange.getIn().setBody(jsonInput);
			}
			
		} catch (JSONException e) {
			throw new UnableToparseRequestDataException("The data which has been passed is not correctly formatted");
		}
	}
}
