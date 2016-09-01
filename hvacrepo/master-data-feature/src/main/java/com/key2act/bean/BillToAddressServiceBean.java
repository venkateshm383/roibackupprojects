package com.key2act.bean;

import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.eventframework.abstractbean.AbstractMetaModelBean;
import com.key2act.pojo.BillToAddress;
import com.key2act.service.BillToAddressNotFoundException;
import com.key2act.service.BillToAddressService;
import com.key2act.service.impl.BillToAddressServiceImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class BillToAddressServiceBean extends AbstractMetaModelBean {

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

		logger.debug("billToAddressBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("billToAddressBean Body: " + body);
		BillToAddressService billToAddressService = new BillToAddressServiceImpl();
		List<BillToAddress> addressDetail;

		String address;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("billToAddressBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");
			Iterator<?> it = array.getJSONObject(0).keys();
			while (it.hasNext())
				key = (String) it.next();
			if (!key.equals("address")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", " + key
						+ " is invalid key, It must be \"address\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
			}
			address = (array.getJSONObject(0).getString("address")).toString();
			logger.debug("billToAddressBean CustomerName: " + address);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		if (address.trim().equals("")) {
			try {
				addressDetail = billToAddressService.getAllBillToAddress();

				// convert list into JSON
				JSONArray billToAddressJson = new JSONArray();
				for (BillToAddress billToAddress : addressDetail) {
					JSONObject billJsonObj = new JSONObject(billToAddress);
					billToAddressJson.put(billJsonObj);
				}

				logger.debug("billToAddressBean Cust Detail: " + addressDetail);
				exchange.getIn().setBody(billToAddressJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new BillToAddressNotFoundException();
			}
		} else {
			try {
				addressDetail = billToAddressService.getBillToAddress(address);

				// convert list into JSON
				JSONArray billToAddressJson = new JSONArray();
				for (BillToAddress billToAddress : addressDetail) {
					JSONObject billJsonObj = new JSONObject(billToAddress);
					billToAddressJson.put(billJsonObj);
				}

				logger.debug("billToAddressBean Cust Detail: " + addressDetail);
				exchange.getIn().setBody(billToAddressJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new BillToAddressNotFoundException();
			}
		}
	}
}
