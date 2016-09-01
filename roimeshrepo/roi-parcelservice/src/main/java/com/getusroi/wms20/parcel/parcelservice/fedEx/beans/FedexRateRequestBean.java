package com.getusroi.wms20.parcel.parcelservice.fedEx.beans;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.wms20.parcel.parcelservice.exception.ParcelServiceErrorCodePropertiesReaderException;
import com.getusroi.wms20.parcel.parcelservice.fedEx.constant.FedexConstant;
import com.getusroi.wms20.parcel.parcelservice.generic.ParcelServiceErrorCodeHandler;

public class FedexRateRequestBean extends AbstractROICamelJDBCBean {
	Logger logger = Logger.getLogger(FedexRateRequestBean.class);
	private final String PROPERTIES_FEDEX_ERRORCODE = "FEDEX_ERROR_CODES.properties";

	/**
	 * The overridden method from AbstractROICamelJDBCBean
	 * 
	 * @throws ParcelServiceErrorCodePropertiesReaderException
	 */
	@Override
	protected void processBean(Exchange exchange) throws ParcelServiceErrorCodePropertiesReaderException {
		String xmlResp = exchange.getIn().getBody(String.class);
		JSONObject jsonObject = XML.toJSONObject(xmlResp);
		JSONObject jsonObject1 = (JSONObject) (jsonObject.getJSONObject(FedexConstant.RATE_REPLY));
		logger.debug("THe response from Fedex json: "+jsonObject1);
		Map<String, String> maperrorProp = null;
		if (jsonObject1.has(FedexConstant.RATE_REPLYDETAILS)) {
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(jsonObject1.get(FedexConstant.RATE_REPLYDETAILS));
			exchange.getIn().setBody(jsonArray.toString());
		} else {
			maperrorProp = errorProperties(jsonObject1, exchange);
			Message out = exchange.getOut();
			// out.setHeader(Exchange.HTTP_RESPONSE_CODE,
			// Integer.parseInt(maperrorProp.get("ErrorCode")));
			out.setHeader(org.apache.cxf.message.Message.RESPONSE_CODE,
					Integer.parseInt(maperrorProp.get(FedexConstant.PARCEL_SERVICE_ERROR_CODE)));
			JSONObject object = new JSONObject(maperrorProp);
			// ...sets the new body with the generic error code and message
			out.setBody(object);
		}
	
	}// ..end of the method

	/**
	 * method returns the error description of fedEX RateService in map~object format
	 * @param jsonObject1
	 * @param exchange
	 * @return map of customGeneric Error description
	 * @throws JSONException
	 * @throws ParcelServiceErrorCodePropertiesReaderException
	 */
	private Map<String, String> errorProperties(JSONObject jsonObject1, Exchange exchange)
			throws ParcelServiceErrorCodePropertiesReaderException {
		// ..gets the Generic roi-parcelService ErrorCodes and messages
		ParcelServiceErrorCodeHandler errorCodeHandler = new ParcelServiceErrorCodeHandler();
		Properties errorProp;
		Map<String, String> maperrorProp;
		try {
			Integer code = Integer.parseInt(jsonObject1.get(FedexConstant.CODE).toString());
			errorProp = loadFedexErrorCodePropertiesFile();
			maperrorProp = errorCodeHandler.errorCodeSetter(errorProp.getProperty(String.valueOf(code)),
					jsonObject1.getString(FedexConstant.MESSAGE));
		} catch (IOException e) {
			throw new ParcelServiceErrorCodePropertiesReaderException(
					"Unable to get the FedEx ErrorCodes properties file: ", e);
		}
		return maperrorProp;

	}//..end of the method

	/**
	 * to load - PARCEL_SERVICE_GENERIC_ERROR_CODES.properties - Properties file
	 * 
	 * @return Properties
	 * @throws ParcelServiceErrorCodePropertiesReaderException
	 * @throws IOException
	 */
	private Properties loadFedexErrorCodePropertiesFile() throws IOException {
		Properties prop = new Properties();
		InputStream input = FedexRateRequestBean.class.getClassLoader().getResourceAsStream(PROPERTIES_FEDEX_ERRORCODE);
		prop.load(input);
		return prop;
	}// ..end of the method
}
