package com.getusroi.wms20.parcel.parcelservice.generic;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParcelServiceHelperVoid {

	final static Logger logger = LoggerFactory.getLogger(ParcelServiceHelperVoid.class);

	/**
	 * 
	 * @param exchange
	 * @return
	 * @throws IOException
	 */
	public String readGenericFile(Exchange exchange) throws IOException {
		String bodyIn = exchange.getIn().getBody(String.class);
		JSONObject jsonObjectin = new JSONObject(bodyIn);
		JSONObject jsonObject = new JSONObject(jsonObjectin.toString());
		JSONArray jsonArray = new JSONArray(jsonObject.get("data").toString());
		String xml = XML.toString(jsonArray.get(0));

		return xml.toString();
	}

}
