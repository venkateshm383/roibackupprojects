package com.key2act.timetracker.service;

import static com.key2act.timetracker.util.TimeTrackerConstants.CREWSETTINGS_EMPLOYEE_POSITION_RESPONSE_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.CREWSETTINGS_EMPLOYEE_RESPONSE_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.CREW_POSITIONS_TO_LOG_SETTINGS_CONFIGURATION_PERMA_CONFIG_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.CREW_SETUP_PERMA_CONFIG_KEY;
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
public class CrewSettingProcessor extends AbstractCassandraBean {
	Logger logger = LoggerFactory.getLogger(CrewSettingProcessor.class);
	ServiceRequestBuilder requestbuilder = new ServiceRequestBuilder();

	/**
	 * method to fetch tenantid from request data and batch settings data
	 * finally setting the data on the exchange body
	 */
	@Override
	protected void processBean(Exchange exchange)
			throws InvalidRequestDataException, TenantDataNotFoundException {
		logger.debug(".processBean method of CrewSettingProcessor ");
		Map<String, Object> permaCacheObject = requestbuilder.getPermaStoreDataFromMeshHeader(exchange);
		JSONObject requestObj = requestbuilder.getRequestData(exchange);
		String tenantID = null;
		try {
			tenantID = (String) requestObj.get(TENANTID_COLUMN_KEY);
		} catch (JSONException e) {
			exchange.getIn().setBody("Request data invalid, tenantId must be specified");
			throw new InvalidRequestDataException(
					"Request data invalid, tenantID must be specified", e);
		}

		JSONObject crewSettings = setCrewSettings(exchange, permaCacheObject, tenantID);
		exchange.getIn().setBody(crewSettings);
	}

	/**
	 * method to configure the permadata and set it in the json object
	 * 
	 * @param exchange
	 * @param permaCacheObject
	 * @param tenantID
	 * @return json obejct which holds the permadata for crew setting
	 * @throws TenantDataNotFoundException
	 */
	public JSONObject setCrewSettings(Exchange exchange,
			Map<String, Object> permaCacheObject, String tenantID)
			throws TenantDataNotFoundException {
		JSONObject crewSettingsObj = new JSONObject();

		String permaData = (String) permaCacheObject.get(CREW_SETUP_PERMA_CONFIG_KEY);
		JSONObject permaDataJsonObject;
		try {
			permaDataJsonObject = new JSONObject(permaData);
			JSONArray permaDataJsonArray = (JSONArray) permaDataJsonObject.getJSONArray(tenantID);
			crewSettingsObj.put(CREWSETTINGS_EMPLOYEE_RESPONSE_KEY,	permaDataJsonArray);
		} catch (JSONException e) {
			exchange.getIn().setBody("PermaStore Data sepecific to tenant not found");
			throw new TenantDataNotFoundException(
					"PermaStore Data sepecific to tenant not found", e);
		}

		permaData = (String) permaCacheObject.get(CREW_POSITIONS_TO_LOG_SETTINGS_CONFIGURATION_PERMA_CONFIG_KEY);
		logger.debug("taken permadata for crewPositionsToLog : " + permaData);
		try {
			permaDataJsonObject = new JSONObject(permaData);
			logger.debug("PermaDataJsonObject : " + permaDataJsonObject);
			crewSettingsObj.put(CREWSETTINGS_EMPLOYEE_POSITION_RESPONSE_KEY,
					(JSONArray) permaDataJsonObject.get(tenantID));
		} catch (JSONException e) {
			exchange.getIn().setBody("PermaStore Data sepecific to tenant not found");
			throw new TenantDataNotFoundException(
					"PermaStore Data sepecific to tenant not found", e);
		}
		return crewSettingsObj;
	}
}
