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
import com.key2act.pojo.ServiceLevel;
import com.key2act.service.ServiceLevelNotFoundException;
import com.key2act.service.impl.ServiceLevelImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this bean method is used get ServiceLevelId
 * @param exchange: exchange object to get post data
 * @throws JSONException: if json is invalid it throw exception
 * @throws ServiceLevelNotFoundException
 */
public class ServiceLevelBean extends AbstractROICamelBean  {
	
	private final Logger logger = LoggerFactory.getLogger(ServiceLevelBean.class);

	@Override
	protected void processBean(Exchange exchange) throws Exception {

		logger.debug("serviceLevelIdBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("serviceLevelIdBean Body: " + body);

		ServiceLevelImpl serviceLevel = new ServiceLevelImpl();
		List<ServiceLevel> serviceDetail;
		
		String serviceLevelName;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("serviceLevelIdBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");
			
			Iterator<?> it = array.getJSONObject(0).keys();
			while(it.hasNext())
				key = (String) it.next();
			if(!key.equals("serviceLevelName")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
						+ key + " is invalid key, It must be \"serviceLevelName\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
				
			}
			
			serviceLevelName = (array.getJSONObject(0).getString("serviceLevelName")).toString();
			logger.debug("serviceLevelIdBean: " + serviceLevelName);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		if (serviceLevelName.trim().equals("")) {
			try {
				serviceDetail = serviceLevel.getAllServiceLevel();
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < serviceDetail.size(); i++) {
			        jsonArray.put(serviceDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new ServiceLevelNotFoundException();
			}
		} else {
			try {
				serviceDetail = serviceLevel.getServiceLevel(serviceLevelName);
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < serviceDetail.size(); i++) {
			        jsonArray.put(serviceDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new ServiceLevelNotFoundException();
			}
		}
	}
}