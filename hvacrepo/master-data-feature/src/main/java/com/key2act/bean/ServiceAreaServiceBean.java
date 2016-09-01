package com.key2act.bean;

import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.key2act.pojo.ServiceArea;
import com.key2act.service.ServiceAreaNotFoundException;
import com.key2act.service.ServiceAreaService;
import com.key2act.service.impl.ServiceAreaServiceImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class ServiceAreaServiceBean extends AbstractROICamelJDBCBean {

	/**
	 * this bean method is used to get service area
	 * 
	 * @param exchange,
	 *            contains camel exchange object
	 * @throws JSONException
	 *             If json format is Invalid then it throw JSONException
	 * @throws ServiceAreaNotFoundException
	 *             If service area is not found then it throw
	 *             ServiceAreaNotFoundException
	 * @throws InvalidJSONKey 
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception {
		
		logger.debug("serviceAreaBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("serviceAreaBean Body: " + body);
		String area;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("serviceAreaBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");

			Iterator<?> it = array.getJSONObject(0).keys();
			while (it.hasNext())
				key = (String) it.next();
			if (!key.equals("area")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", " + key
						+ " is invalid key, It must be \"area\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
			}

			area = (array.getJSONObject(0).getString("area")).toString();
			logger.debug("serviceAreaBean: " + area);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		ServiceAreaService serviceAreaService = new ServiceAreaServiceImpl();
		List<ServiceArea> areaDetail;
		if (area.trim().equals("")) {
			try {
				areaDetail = serviceAreaService.getAllServiceArea();

				// convert list into JSON
				JSONArray serviceAreaJsonObjJson = new JSONArray();
				for (ServiceArea serviceArea : areaDetail) {
					JSONObject serviceAreaJsonObj = new JSONObject(serviceArea);
					serviceAreaJsonObjJson.put(serviceAreaJsonObj);
				}
				logger.debug("serviceAreaBean Detail: " + serviceAreaJsonObjJson);
				exchange.getIn().setBody(serviceAreaJsonObjJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new ServiceAreaNotFoundException(e.getMessage());
			}
		} else {
			try {
				areaDetail = serviceAreaService.getServiceArea(area);
				// convert list into JSON
				JSONArray serviceAreaJsonObjJson = new JSONArray();
				for (ServiceArea serviceArea : areaDetail) {
					JSONObject serviceAreaJsonObj = new JSONObject(serviceArea);
					serviceAreaJsonObjJson.put(serviceAreaJsonObj);
				}
				logger.debug("serviceAreaBean Detail: " + serviceAreaJsonObjJson);
				exchange.getIn().setBody(serviceAreaJsonObjJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new ServiceAreaNotFoundException(e.getMessage());
			}
		}
		
	}

}
