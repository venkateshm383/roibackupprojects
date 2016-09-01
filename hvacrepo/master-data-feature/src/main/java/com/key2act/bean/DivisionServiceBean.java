package com.key2act.bean;

import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.key2act.pojo.Division;
import com.key2act.service.DivisionNotFoundException;
import com.key2act.service.impl.DivisionServiceImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class DivisionServiceBean extends AbstractROICamelJDBCBean {

	/**
	 * this bean method is used to get division
	 * 
	 * @param exchange,
	 *            contains exchange object
	 * @throws JSONException
	 * @throws DivisionNotFoundException
	 *             if Division data is not found then it throw
	 *             DivisionNotFoundException
	 * @throws InvalidJSONKey 
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception {
		
		logger.debug("divisionBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("divisionBean Body: " + body);
		String divisionName;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("divisionBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");

			Iterator<?> it = array.getJSONObject(0).keys();
			while (it.hasNext())
				key = (String) it.next();
			if (!key.equals("divisionName")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", " + key
						+ " is invalid key, It must be \"divisionName\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
			}

			divisionName = (array.getJSONObject(0).getString("divisionName")).toString();
			logger.debug("divisionBean CustomerName: " + divisionName);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		DivisionServiceImpl customerService = new DivisionServiceImpl();
		List<Division> divisionDetail;

		if (divisionName.trim().equals("")) {
			try {
				divisionDetail = customerService.getAllDivision();

				// convert list into JSON
				JSONArray divJson = new JSONArray();
				for (Division div : divisionDetail) {
					JSONObject divJsonObj = new JSONObject(div);
					divJson.put(divJsonObj);
				}

				logger.debug("divisionBean Detail: " + divisionDetail);
				exchange.getIn().setBody(divJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new DivisionNotFoundException(e.getMessage());
			}
		} else {
			try {
				divisionDetail = customerService.getDivision(divisionName);
				// convert list into JSON
				JSONArray divJson = new JSONArray();
				for (Division div : divisionDetail) {
					JSONObject divJsonObj = new JSONObject(div);
					divJson.put(divJsonObj);
				}

				logger.debug("divisionBean Detail: " + divisionDetail);
				exchange.getIn().setBody(divJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new DivisionNotFoundException(e.getMessage());
			}
		}
		
	}

}
