package com.key2act.bean;

import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.key2act.pojo.PrimaryTechnician;
import com.key2act.service.PrimaryTechnicianNotFoundException;
import com.key2act.service.PrimaryTechnicianService;
import com.key2act.service.impl.PrimaryTechnicianServiceImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class PrimaryTechnicianServiceBean extends AbstractROICamelJDBCBean {

	/**
	 * this bean method is used to get primary technician
	 * 
	 * @param exchange,
	 *            contains camel exchange object
	 * @throws JSONException
	 *             If json format is Invalid then it throw JSONException
	 * @throws PrimaryTechnicianNotFoundException
	 *             If primary technician is not found then it throw
	 *             PrimaryTechnicianNotFoundException
	 * @throws InvalidJSONKey 
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception {
		
		logger.debug("primaryTechnicianBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("primaryTechnicianBean Body: " + body);
		String primaryTechnicianType;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("primaryTechnicianBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");

			Iterator<?> it = array.getJSONObject(0).keys();
			while (it.hasNext())
				key = (String) it.next();
			if (!key.equals("primaryTechnicianType")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", " + key
						+ " is invalid key, It must be \"primaryTechnicianType\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
			}

			primaryTechnicianType = (array.getJSONObject(0).getString("primaryTechnicianType")).toString();
			logger.debug("primaryTechnicianBean: " + primaryTechnicianType);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		PrimaryTechnicianService locationService = new PrimaryTechnicianServiceImpl();
		List<PrimaryTechnician> primaryTechnicianTypeDetail;
		if (primaryTechnicianType.trim().equals("")) {
			try {
				primaryTechnicianTypeDetail = locationService.getAllPrimaryTechnician();

				// convert list into JSON
				JSONArray technicianJson = new JSONArray();
				for (PrimaryTechnician technician : primaryTechnicianTypeDetail) {
					JSONObject technicianJsonObj = new JSONObject(technician);
					technicianJson.put(technicianJsonObj);
				}

				logger.debug("primaryTechnicianBean Detail: " + primaryTechnicianTypeDetail);
				exchange.getIn().setBody(technicianJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new PrimaryTechnicianNotFoundException(e.getMessage());
			}
		} else {
			try {
				primaryTechnicianTypeDetail = locationService.getPrimaryTechnician(primaryTechnicianType);
				// convert list into JSON
				JSONArray technicianJson = new JSONArray();
				for (PrimaryTechnician technician : primaryTechnicianTypeDetail) {
					JSONObject technicianJsonObj = new JSONObject(technician);
					technicianJson.put(technicianJsonObj);
				}

				logger.debug("primaryTechnicianBean Detail: " + primaryTechnicianTypeDetail);
				exchange.getIn().setBody(technicianJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new PrimaryTechnicianNotFoundException(e.getMessage());
			}
		}
		
	}

}
