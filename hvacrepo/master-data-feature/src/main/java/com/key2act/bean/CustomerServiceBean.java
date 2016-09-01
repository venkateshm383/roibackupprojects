package com.key2act.bean;

import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.key2act.pojo.Customer;
import com.key2act.service.BillToAddressNotFoundException;
import com.key2act.service.CustomerNotFoundException;
import com.key2act.service.impl.CustomerServiceImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class CustomerServiceBean extends AbstractROICamelJDBCBean{

	private final Logger logger = LoggerFactory.getLogger(CustomerServiceBean.class);
	
	/**
	 * this bean method is used to get bill to address
	 * 
	 * @param exchange,
	 *            contains exchange values
	 * @throws JSONException,
	 *             throw Json Exception when json will be invalid
	 * @throws BillToAddressNotFoundException
	 *             if bill to address not found then it throw
	 *             BillToAddressNotFoundException
	 * @throws InvalidJSONKey 
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception {
		
		logger.debug("Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("Body: " + body);
		JSONObject jsonObject;
		String custName;
		String key = "";
		try {
			jsonObject = new JSONObject(body);
			logger.debug("JSon Object: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");
			Iterator<?> it = array.getJSONObject(0).keys();
			while (it.hasNext())
				key = (String) it.next();
			if (!key.equals("custName")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", " + key
						+ " is invalid key, It must be \"custName\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
			}
			custName = (array.getJSONObject(0).getString("custName")).toString();
			logger.debug("Key2ActBean CustomerName: " + custName);
			array.getJSONObject(0).keys();
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		CustomerServiceImpl customerService = new CustomerServiceImpl();
		List<Customer> custDetail;

		// if custName empty then it will return all data
		if (custName.trim().equals("")) {
			try {
				custDetail = customerService.getAllCustomer();
				// convert list into JSON
				JSONArray customerJson = new JSONArray();
				for (Customer cust : custDetail) {
					JSONObject custJsonObj = new JSONObject(cust);
					customerJson.put(custJsonObj);
				}
				logger.debug("All Cust Detail: " + custDetail);
				exchange.getIn().setBody(customerJson.toString());
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new CustomerNotFoundException();
			}
		} else {
			try {
				custDetail = customerService.getCustomer(custName.trim());
				// convert list into JSON
				JSONArray customerJson = new JSONArray();
				for (Customer cust : custDetail) {
					JSONObject custJsonObj = new JSONObject(cust);
					customerJson.put(custJsonObj);
				}
				logger.debug("Cust Detail: " + custDetail);
				exchange.getIn().setBody(customerJson.toString());
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new CustomerNotFoundException();
			}
		}
		
	}

}
