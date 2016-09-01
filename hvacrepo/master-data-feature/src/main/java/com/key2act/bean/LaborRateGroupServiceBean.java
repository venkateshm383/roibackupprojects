package com.key2act.bean;

import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.key2act.pojo.LaborRateGroup;
import com.key2act.service.LaborRateGroupNotFoundException;
import com.key2act.service.LaborRateGroupService;
import com.key2act.service.impl.LaborRateGroupServiceImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class LaborRateGroupServiceBean extends AbstractROICamelJDBCBean {

	/**
	 * this method is used to get labor rate group
	 * 
	 * @param exchange,
	 *            contains camel exchange object
	 * @throws JSONException
	 *             If json format is Invalid then it throw JSONException
	 * @throws LaborRateGroupNotFoundException
	 *             If labor rate group not found then it throw
	 *             LaborRateGroupNotFoundException
	 * @throws InvalidJSONKey 
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception {

		logger.debug("laborRateGroupBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("laborRateGroupBean Body: " + body);
		String laborRateGroupName;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("laborRateGroupBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");

			Iterator<?> it = array.getJSONObject(0).keys();
			while (it.hasNext())
				key = (String) it.next();
			if (!key.equals("laborRateGroupName")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", " + key
						+ " is invalid key, It must be \"laborRateGroupName\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
			}

			laborRateGroupName = (array.getJSONObject(0).getString("laborRateGroupName")).toString();
			logger.debug("laborRateGroupBean laborDetail: " + laborRateGroupName);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		LaborRateGroupService customerService = new LaborRateGroupServiceImpl();
		List<LaborRateGroup> laborDetail;
		if (laborRateGroupName.trim().equals("")) {
			try {
				laborDetail = customerService.getAllLaborRateGroup();

				// convert list into JSON
				JSONArray laborJson = new JSONArray();
				for (LaborRateGroup labor : laborDetail) {
					JSONObject laborJsonObj = new JSONObject(labor);
					laborJson.put(laborJsonObj);
				}

				logger.debug("laborRateGroupBean Detail: " + laborJson);
				exchange.getIn().setBody(laborJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new LaborRateGroupNotFoundException(e.getMessage());
			}
		} else {
			try {
				laborDetail = customerService.getLaborRateGroup(laborRateGroupName);
				// convert list into JSON
				JSONArray laborJson = new JSONArray();
				for (LaborRateGroup labor : laborDetail) {
					JSONObject laborJsonObj = new JSONObject(labor);
					laborJson.put(laborJsonObj);
				}

				logger.debug("laborRateGroupBean Detail: " + laborJson);
				exchange.getIn().setBody(laborJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new LaborRateGroupNotFoundException(e.getMessage());
			}
		}
	}
}
