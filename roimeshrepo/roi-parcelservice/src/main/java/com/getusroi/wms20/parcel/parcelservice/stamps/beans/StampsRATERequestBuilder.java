package com.getusroi.wms20.parcel.parcelservice.stamps.beans;

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
import com.getusroi.wms20.parcel.parcelservice.stamps.service.StampsRateServiceBuilder;
import com.getusroi.wms20.parcel.parcelservice.ups.beans.UPSRATERequestBuilder;

public class StampsRATERequestBuilder extends AbstractROICamelJDBCBean {
	Logger logger = Logger.getLogger(StampsRATERequestBuilder.class.getName());
	private final String PROPERTIES_STAMPS_ERRORCODE = "STAMPS_ERROR_CODES.properties";

	/**
	 * the overriden method to process the response from Stamps RateRequest
	 * 
	 * @args exchange
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception {

		String xmlResp = exchange.getIn().getBody(String.class);
		JSONObject jsonObject = XML.toJSONObject(xmlResp);
		JSONObject jsonObject1 = (JSONObject) (jsonObject.getJSONObject("RateReply"));
		logger.debug("The inJson: "+jsonObject1);
		Map<String, String> maperrorProp = null;
		// if the json object has the ratereply as per the generic response
		if (jsonObject1.has("RateReplyDetails")) {
			JSONArray jsonArray = null;
			if (jsonObject1.get("RateReplyDetails") instanceof JSONArray) {
				jsonArray = new JSONArray(jsonObject1.get("RateReplyDetails").toString());
			} else if (jsonObject1.get("RateReplyDetails") instanceof JSONObject) {
				jsonArray = new JSONArray();
				jsonArray.put(jsonObject1.get("RateReplyDetails"));
			}
			StampsRateServiceBuilder stampsRateServiceBuilder = new StampsRateServiceBuilder();
			JSONArray stampsRateRespJsonArray = stampsRateServiceBuilder.replaceStampsServiceTypeNames(jsonArray);
			exchange.getIn().setBody(stampsRateRespJsonArray.toString());
		} else {
			logger.debug("The incoming in elseCondition: "+jsonObject1);
			maperrorProp = errorProperties(jsonObject1, exchange);
			logger.debug("When error props: "+maperrorProp);
			Message out = exchange.getOut();
			// out.setHeader(Exchange.HTTP_RESPONSE_CODE,
			// Integer.parseInt(maperrorProp.get("ErrorCode")));
			out.setHeader(org.apache.cxf.message.Message.RESPONSE_CODE,
					Integer.parseInt(maperrorProp.get(FedexConstant.PARCEL_SERVICE_ERROR_CODE)));
			JSONObject object = new JSONObject(maperrorProp);
			// ...sets the new body with the generic error code and message
			out.setBody(object);
		}

	}// ..end of the bean method

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
			String code = jsonObject1.get(FedexConstant.CODE).toString();
			logger.debug("The reply prpos: "+code);
			errorProp = loadStampsErrorCodePropertiesFile();
			maperrorProp = errorCodeHandler.errorCodeSetter(errorProp.getProperty(code),
					jsonObject1.getString(FedexConstant.MESSAGE));
			logger.debug(".inside roorProperties: "+maperrorProp);
		} catch (IOException e) {
			throw new ParcelServiceErrorCodePropertiesReaderException(
					"Unable to get the FedEx ErrorCodes properties file: ", e);
		}
		return maperrorProp;

	}//..end of the method

	
	
	/**
	 * to load - STAMPS_ERROR_CODES.properties - Properties file
	 * 
	 * @return Properties
	 * @throws IOException
	 */
	private Properties loadStampsErrorCodePropertiesFile() throws IOException {
		Properties prop = new Properties();
		InputStream input = UPSRATERequestBuilder.class.getClassLoader()
				.getResourceAsStream(PROPERTIES_STAMPS_ERRORCODE);
		prop.load(input);
		return prop;
	}// ..end of the method

}
