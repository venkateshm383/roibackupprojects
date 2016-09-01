package com.key2act.bean;

import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.key2act.pojo.MasterTaxSchedule;
import com.key2act.service.MasterTaxScheduleNotFoundException;
import com.key2act.service.MasterTaxScheduleService;
import com.key2act.service.impl.MasterTaxScheduleServiceImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class MasterTaxScheduleServiceBean extends AbstractROICamelJDBCBean {

	/**
	 * this bean method is used to get master tax schedule
	 * 
	 * @param exchange,
	 *            contains camel exchange object
	 * @throws JSONException
	 *             If json format is Invalid then it throw JSONException
	 * @throws MasterTaxScheduleNotFoundException
	 *             If master tax schedule not found then it throw
	 *             MasterTaxScheduleNotFoundException
	 * @throws InvalidJSONKey 
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception {

		logger.debug("masterTaxScheduleBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("masterTaxScheduleBean Body: " + body);
		String masterTaxScheduleType;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("masterTaxScheduleBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");

			Iterator<?> it = array.getJSONObject(0).keys();
			while (it.hasNext())
				key = (String) it.next();
			if (!key.equals("masterTaxScheduleType")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", " + key
						+ " is invalid key, It must be \"masterTaxScheduleType\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
			}

			masterTaxScheduleType = (array.getJSONObject(0).getString("masterTaxScheduleType")).toString();
			logger.debug("masterTaxScheduleBean: " + masterTaxScheduleType);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		MasterTaxScheduleService locationService = new MasterTaxScheduleServiceImpl();
		List<MasterTaxSchedule> masterTaxScheduleTypeDetail;
		if (masterTaxScheduleType.trim().equals("")) {
			try {
				masterTaxScheduleTypeDetail = locationService.getAllMasterTaxSchedule();

				// convert list into JSON
				JSONArray masterTaxJson = new JSONArray();
				for (MasterTaxSchedule master : masterTaxScheduleTypeDetail) {
					JSONObject masterTaxJsonJsonObj = new JSONObject(master);
					masterTaxJson.put(masterTaxJsonJsonObj);
				}

				logger.debug("masterTaxScheduleBean Detail: " + masterTaxJson);
				exchange.getIn().setBody(masterTaxJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new MasterTaxScheduleNotFoundException(e.getMessage());
			}
		} else {
			try {
				masterTaxScheduleTypeDetail = locationService.getMasterTaxSchedule(masterTaxScheduleType);
				// convert list into JSON
				JSONArray masterTaxJson = new JSONArray();
				for (MasterTaxSchedule master : masterTaxScheduleTypeDetail) {
					JSONObject masterTaxJsonJsonObj = new JSONObject(master);
					masterTaxJson.put(masterTaxJsonJsonObj);
				}

				logger.debug("masterTaxScheduleBean Detail: " + masterTaxJson);
				exchange.getIn().setBody(masterTaxJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new MasterTaxScheduleNotFoundException(e.getMessage());
			}
		}
		
	}

}
