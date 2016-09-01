package com.key2act.bean;

import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.eventframework.abstractbean.AbstractROICamelBean;
import com.key2act.pojo.LocalTax;
import com.key2act.service.LocalTaxNotFoundException;
import com.key2act.service.impl.LocalTaxServImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this bean method is used get Local Tax
 * @param exchange: exchange object to get post data
 * @throws JSONException: if json is invalid it throw exception
 * @throws LocalTaxNotFoundException
 */

public class LocalTaxBean extends AbstractROICamelBean {
	
	private final Logger logger = LoggerFactory.getLogger(LocalTaxBean.class);

	@Override
	protected void processBean(Exchange exchange) throws Exception {

		logger.debug("localTaxBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("localTaxBean Body: " + body);

		LocalTaxServImpl localTax = new LocalTaxServImpl();
		List<LocalTax> locaTaxDetail;
		
		String localTaxName;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("localTaxBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");
			
			Iterator<?> it = array.getJSONObject(0).keys();
			while(it.hasNext())
				key = (String) it.next();
			if(!key.equals("localTaxName")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
						+ key + " is invalid key, It must be \"localTaxName\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
				
			}
			
			localTaxName = (array.getJSONObject(0).getString("localTaxName")).toString();
			logger.debug("localTaxBean: " + localTaxName);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		if (localTaxName.trim().equals("")) {
			try {
				locaTaxDetail = localTax.getAllLocalTaxId();
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < locaTaxDetail.size(); i++) {
			        jsonArray.put(locaTaxDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new LocalTaxNotFoundException();
			}
		} else {
			try {
				locaTaxDetail = localTax.getAllLocalTaxId();
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < locaTaxDetail.size(); i++) {
			        jsonArray.put(locaTaxDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new LocalTaxNotFoundException();
			}
		}
	}
}