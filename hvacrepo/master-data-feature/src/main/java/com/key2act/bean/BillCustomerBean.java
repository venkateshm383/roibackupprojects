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
import com.key2act.pojo.BillCustomer;
import com.key2act.service.BillCustomerNotFoundException;
import com.key2act.service.BillCustomerService;
import com.key2act.service.impl.BillCustomerImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class BillCustomerBean extends AbstractROICamelJDBCBean {

	private final Logger logger = LoggerFactory.getLogger(BillCustomerBean.class);

	/**
	 * this bean method is used get bill customer
	 * 
	 * @param exchange:
	 *            exchange object to get post data
	 * @throws JSONException:
	 *             if json is invalid it throw exception
	 * @throws BillCustomerNotFoundException
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception {

		logger.debug("billCustomerCustomer Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("billCustomerCustomer Body: " + body);
		BillCustomerService billCustomer=new  BillCustomerImpl();
		List<BillCustomer> customerDetail;
		
		String customerName;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("billCustomerCustomer Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");

			Iterator<?> it = array.getJSONObject(0).keys();
			while (it.hasNext())
				key = (String) it.next();
			if (!key.equals("customerName")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", " + key
						+ " is invalid key, It must be \"customerName\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
				
			}

			customerName = (array.getJSONObject(0).getString("customerName")).toString();
			logger.debug("priceMatrixBean: " + customerName);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		if (customerName.trim().equals("")) {
			try {
				customerDetail = billCustomer.getAllbillCustomer();
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < customerDetail.size(); i++) {
			        jsonArray.put(customerDetail.get(i).getJSONObject());
				}

				logger.debug("BillCustomerBean customer Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new BillCustomerNotFoundException();
			}

		} else {
			try {
				customerDetail = billCustomer.getbillCustomer(customerName);
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < customerDetail.size(); i++) {
			        jsonArray.put(customerDetail.get(i).getJSONObject());
				}
				logger.debug("BillCustomerBean customer Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new BillCustomerNotFoundException();
			}
		}
	}
}