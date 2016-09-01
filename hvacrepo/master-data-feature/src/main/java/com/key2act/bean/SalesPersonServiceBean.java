package com.key2act.bean;

import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.key2act.pojo.SalesPerson;
import com.key2act.service.SalesPersonNotFoundException;
import com.key2act.service.SalesPersonService;
import com.key2act.service.impl.SalesPersonServiceImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class SalesPersonServiceBean extends AbstractROICamelJDBCBean {

	/**
	 * this method is used to get sales person
	 * 
	 * @param exchange,
	 *            contains camel exchange object
	 * @throws JSONException
	 *             If json format is Invalid then it throw JSONException
	 * @throws SalesPersonNotFoundException
	 *             If sales person is not found then it throw
	 *             SalesPersonNotFoundException
	 * @throws InvalidJSONKey 
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception {
		
		logger.debug("salesPersonBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("salesPersonBean Body: " + body);
		String salesPersonName;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("salesPersonBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");

			Iterator<?> it = array.getJSONObject(0).keys();
			while (it.hasNext())
				key = (String) it.next();
			if (!key.equals("salesPersonName")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", " + key
						+ " is invalid key, It must be \"salesPersonName\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
			}

			salesPersonName = (array.getJSONObject(0).getString("salesPersonName")).toString();
			logger.debug("salesPersonBean: " + salesPersonName);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		SalesPersonService salesPersonService = new SalesPersonServiceImpl();
		List<SalesPerson> salesPersonNameDetail;
		if (salesPersonName.trim().equals("")) {
			try {
				salesPersonNameDetail = salesPersonService.getAllSalesPerson();

				// convert list into JSON
				JSONArray salesJson = new JSONArray();
				for (SalesPerson salesPerson : salesPersonNameDetail) {
					JSONObject salesJsonObj = new JSONObject(salesPerson);
					salesJson.put(salesJsonObj);
				}

				logger.debug("salesPersonBean Detail: " + salesJson);
				exchange.getIn().setBody(salesPersonNameDetail);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new SalesPersonNotFoundException(e.getMessage());
			}
		} else {
			try {
				salesPersonNameDetail = salesPersonService.getSalesPerson(salesPersonName);
				// convert list into JSON
				JSONArray salesJson = new JSONArray();
				for (SalesPerson salesPerson : salesPersonNameDetail) {
					JSONObject salesJsonObj = new JSONObject(salesPerson);
					salesJson.put(salesJsonObj);
				}

				logger.debug("salesPersonBean Detail: " + salesJson);
				exchange.getIn().setBody(salesPersonNameDetail);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new SalesPersonNotFoundException(e.getMessage());
			}
		}
		
	}

}
