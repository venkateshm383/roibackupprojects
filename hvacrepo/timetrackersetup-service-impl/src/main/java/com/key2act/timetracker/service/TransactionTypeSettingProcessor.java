package com.key2act.timetracker.service;

import static com.key2act.timetracker.util.TimeTrackerConstants.*;

import java.util.Map;

import org.apache.camel.Exchange;
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
public class TransactionTypeSettingProcessor extends AbstractCassandraBean{
	Logger logger = LoggerFactory.getLogger(TransactionTypeSettingProcessor.class);
	ServiceRequestBuilder requestbuilder = new ServiceRequestBuilder();
	
	/**
	 * method to fetch tenantid from request data and batch settings data
	 * finally setting the data on the exchange body
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception {
		logger.debug(".processBean method of TransactionTypeSettingProcessor ");
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
		
		JSONObject transactioTypeSettings = setTransactionTypeSettings(exchange, permaCacheObject, tenantID);
		exchange.getOut().setBody(transactioTypeSettings.toString());
		
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
	private JSONObject setTransactionTypeSettings(Exchange exchange,
			Map<String, Object> permadata, String tenantID) throws TenantDataNotFoundException {
		JSONObject transactionTypeSettingsObj = new JSONObject();

		JSONObject permaDataJsonObject;
		try {
			String permaData = (String) permadata.get(TRANSACTION_SETTINGS_CONFIG_KEY);
			permaDataJsonObject = new JSONObject(permaData);
			permaDataJsonObject = permaDataJsonObject.getJSONObject(tenantID);
			transactionTypeSettingsObj.put(TRANSACTION_TYPE_RESPONSE_KEY, permaDataJsonObject);
		} catch (JSONException e) {
			exchange.getIn().setBody("PermaStore Data sepecific to tenant not found");
			throw new TenantDataNotFoundException(
					"PermaStore Data sepecific to tenant not found", e);
		}

		return transactionTypeSettingsObj;
	}
}
