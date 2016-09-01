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
import com.key2act.pojo.ContactNumber;
import com.key2act.service.ContactNumberNotFoundException;
import com.key2act.service.ContactNumberService;
import com.key2act.service.impl.ContactNumberImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class ContactNumberBean extends AbstractROICamelBean {
	
	private final Logger logger = LoggerFactory.getLogger(ContactNumberBean.class);

	/**
	 * this bean method is used to get contact number
	 * @param exchange
	 * @throws JSONException
	 * @throws ContactNumberNotFoundException
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception{
		logger.debug("contactNumberBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("contactNumberBean Body: " + body);
		ContactNumberService contactNumberService = new ContactNumberImpl();
		List<ContactNumber> contactNumberServiceDetail;
		
		String contactName;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("contactNumberBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");

			Iterator<?> it = array.getJSONObject(0).keys();
			while (it.hasNext())
				key = (String) it.next();
			if (!key.equals("contactName")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", " + key
						+ " is invalid key, It must be \"contactName\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
			
			}

			contactName = (array.getJSONObject(0).getString("contactName")).toString();
			logger.debug("contactNumberBean: " + contactName);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}


		if (contactName.trim().equals("")) {
			try {
				contactNumberServiceDetail = contactNumberService.getAllContactNum();
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < contactNumberServiceDetail.size(); i++) {
			        jsonArray.put(contactNumberServiceDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new ContactNumberNotFoundException();
			}
		} else {
			try {
				contactNumberServiceDetail = contactNumberService.getAllContactNum();
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < contactNumberServiceDetail.size(); i++) {
			        jsonArray.put(contactNumberServiceDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new ContactNumberNotFoundException();
			}
			}
		}
	}

