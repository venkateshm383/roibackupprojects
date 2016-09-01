package com.key2act.bean;

import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.key2act.pojo.SecondaryTechnician;
import com.key2act.service.SecondaryTechnicianNotFoundException;
import com.key2act.service.SecondaryTechnicianService;
import com.key2act.service.impl.SecondaryTechnicianServiceImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class SecondaryTechnicianServiceBean extends AbstractROICamelJDBCBean {

	/**
	 * this bean method used to get secondary technician
	 * 
	 * @param exchange,
	 *            contains camel exchange object
	 * @throws JSONException
	 *             If json format is Invalid then it throw JSONException
	 * @throws SecondaryTechnicianNotFoundException
	 *             If secondary technician is not found then it throw
	 *             SecondaryTechnicianNotFoundException
	 * @throws InvalidJSONKey 
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception {

		logger.debug("secondaryTechnicianBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("secondaryTechnicianBean Body: " + body);
		String secondaryTechnicianType;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("secondaryTechnicianBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");

			Iterator<?> it = array.getJSONObject(0).keys();
			while (it.hasNext())
				key = (String) it.next();
			if (!key.equals("secondaryTechnicianType")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", " + key
						+ " is invalid key, It must be \"secondaryTechnicianType\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
			}

			secondaryTechnicianType = (array.getJSONObject(0).getString("secondaryTechnicianType")).toString();
			logger.debug("secondaryTechnicianBean: " + secondaryTechnicianType);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		SecondaryTechnicianService secondaryTechnicianService = new SecondaryTechnicianServiceImpl();
		List<SecondaryTechnician> secondaryTechnicianTypeDetail;
		if (secondaryTechnicianType.trim().equals("")) {
			try {
				secondaryTechnicianTypeDetail = secondaryTechnicianService.getAllSecondaryTechnician();

				// convert list into JSON
				JSONArray technicianJson = new JSONArray();
				for (SecondaryTechnician technician : secondaryTechnicianTypeDetail) {
					JSONObject technicianJsonObj = new JSONObject(technician);
					technicianJson.put(technicianJsonObj);
				}

				logger.debug("secondaryTechnicianBean Detail: " + technicianJson);
				exchange.getIn().setBody(technicianJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new SecondaryTechnicianNotFoundException(e.getMessage());
			}
		} else {
			try {
				secondaryTechnicianTypeDetail = secondaryTechnicianService
						.getSecondaryTechnician(secondaryTechnicianType);
				// convert list into JSON
				JSONArray technicianJson = new JSONArray();
				for (SecondaryTechnician technician : secondaryTechnicianTypeDetail) {
					JSONObject technicianJsonObj = new JSONObject(technician);
					technicianJson.put(technicianJsonObj);
				}

				logger.debug("secondaryTechnicianBean Detail: " + technicianJson);
				exchange.getIn().setBody(technicianJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new SecondaryTechnicianNotFoundException(e.getMessage());
			}
		}
		
	}

}
