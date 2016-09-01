package com.key2act.bean;

import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.key2act.pojo.Location;
import com.key2act.service.LocationNotFoundException;
import com.key2act.service.LocationService;
import com.key2act.service.impl.LocationServiceImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class LocationServiceBean extends AbstractROICamelJDBCBean {

	/**
	 * this bean method is used to get location detail
	 * 
	 * @param exchange,
	 *            contains camel exchange object
	 * @throws JSONException
	 *             If json format is Invalid then it throw JSONException
	 * @throws LocationNotFoundException,
	 *             If location is not found then it throw
	 *             LocationNotFoundException
	 * @throws InvalidJSONKey 
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception {

		logger.debug("locationBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("locationBean Body: " + body);
		String location;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("locationBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");

			Iterator<?> it = array.getJSONObject(0).keys();
			while (it.hasNext())
				key = (String) it.next();
			if (!key.equals("location")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", " + key
						+ " is invalid key, It must be \"location\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
			}

			location = (array.getJSONObject(0).getString("location")).toString();
			logger.debug("locationBeanl: " + location);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		LocationService locationService = new LocationServiceImpl();
		List<Location> locationDetail;
		if (location.trim().equals("")) {
			try {
				locationDetail = locationService.getAllLocation();

				// convert list into JSON
				JSONArray locationJson = new JSONArray();
				for (Location loc : locationDetail) {
					JSONObject locationJsonObj = new JSONObject(loc);
					locationJson.put(locationJsonObj);
				}

				logger.debug("locationBean Detail: " + locationJson);
				exchange.getIn().setBody(locationJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new LocationNotFoundException(e.getMessage());
			}
		} else {
			try {
				locationDetail = locationService.getLocation(location);
				JSONArray locationJson = new JSONArray();
				for (Location loc : locationDetail) {
					JSONObject locationJsonObj = new JSONObject(loc);
					locationJson.put(locationJsonObj);
				}

				logger.debug("locationBean Detail: " + locationJson);
				exchange.getIn().setBody(locationJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new LocationNotFoundException(e.getMessage());
			}
		}
	}

}
