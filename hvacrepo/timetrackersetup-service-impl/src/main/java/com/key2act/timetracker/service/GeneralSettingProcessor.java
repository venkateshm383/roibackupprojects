package com.key2act.timetracker.service;

import static com.key2act.timetracker.util.TimeTrackerConstants.GENERALSETTINGS_RESPONSE_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.GENERAL_SETUP_PERMA_CONFIG_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.HOLIDAYSETTINGS_RESPONSE_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.HOLIDAY_PAY_PER_HOUR_PERMA_CONFIG_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.TENANTID_COLUMN_KEY;

import java.util.Map;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.key2act.timetracker.service.helper.ServiceRequestBuilder;

/**
 * 
 * @author bizruntime
 *
 */
public class GeneralSettingProcessor extends AbstractCassandraBean {
	Logger logger = LoggerFactory.getLogger(GeneralSettingProcessor.class);
	ServiceRequestBuilder requestbuilder = new ServiceRequestBuilder();

	/**
	 * method to fetch tenantid from request data and batch settings data
	 * finally setting the data on the exchange body
	 */
	@Override
	protected void processBean(Exchange exchange) throws InvalidRequestDataException, TenantDataNotFoundException  {
		logger.debug(".processBean method of GeneralSettingProcessor ");
		Map<String, Object> permaCacheObject = requestbuilder.getPermaStoreDataFromMeshHeader(exchange);
		JSONObject requestObj = requestbuilder.getRequestData(exchange);
		String tenantID;
		try {
			tenantID = (String) requestObj.get(TENANTID_COLUMN_KEY);
		} catch (JSONException e) {
			exchange.getIn().setBody("Request data invalid, tenantId must be specified");
			throw new InvalidRequestDataException(
					"Request data invalid, tenantID must be specified", e);
		}
		
		JSONObject generalSettings = setGeneralSettings(exchange, permaCacheObject, tenantID);
		exchange.getOut().setBody(generalSettings.toString());
	}

	/**
	 * method to configure the permadata and set it in the json object
	 * 
	 * @param exchange
	 * @param permaCacheObject
	 * @param tenantID
	 * @return json obejct which holds the permadata for batch setting
	 * @throws TenantDataNotFoundException
	 */
	private JSONObject setGeneralSettings(Exchange exchange,
			Map<String, Object> permaCacheObject, String tenantID) throws TenantDataNotFoundException {
		JSONObject generalSettingsObj = new JSONObject();

		String permaData = (String) permaCacheObject.get(GENERAL_SETUP_PERMA_CONFIG_KEY);
		JSONObject permaDataJsonObject;
		try {
			permaDataJsonObject = new JSONObject(permaData);
			permaDataJsonObject = (JSONObject) permaDataJsonObject.get(tenantID);
			generalSettingsObj.put(GENERALSETTINGS_RESPONSE_KEY, permaDataJsonObject);
			logger.info("General Settings of General Setup: " + generalSettingsObj.toString());
		} catch (JSONException e) {
			exchange.getOut().setBody("PermaStore Data sepecific to tenant not found");
			throw new TenantDataNotFoundException("PermaStore Data sepecific to tenant not found",e);
		}

		permaData = (String) permaCacheObject.get(HOLIDAY_PAY_PER_HOUR_PERMA_CONFIG_KEY);
		logger.debug("taken permadata for holiday : " + permaData);
		try {
			permaDataJsonObject = new JSONObject(permaData);
			logger.debug("PermaDataJsonObject : " + permaDataJsonObject.toString());
			
			generalSettingsObj.put(HOLIDAYSETTINGS_RESPONSE_KEY, (JSONArray) permaDataJsonObject.get(tenantID));
			logger.info("General Settings of Holiday Setup: " + generalSettingsObj.toString());
		} catch (JSONException e) {
			exchange.getOut().setBody("PermaStore Data sepecific to tenant not found");
			throw new TenantDataNotFoundException("PermaStore Data sepecific to tenant not found",e);
		}
		return generalSettingsObj;
	}
}
