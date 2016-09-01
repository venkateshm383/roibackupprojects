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
import com.key2act.pojo.CallType;
import com.key2act.service.CallTypeNotFoundException;
import com.key2act.service.CallTypeService;
import com.key2act.service.impl.CallTypeServiceImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class CallTypeBean extends AbstractROICamelBean {

	private final Logger logger = LoggerFactory.getLogger(CallTypeBean.class);

	/**
	 * this bean method is used to get callType
	 * @param exchange
	 * @throws JSONException
	 * @throws CallTypeNotFoundException
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception{

		logger.debug("callTypeBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("callTypeBean Body: " + body);
		CallTypeService callTypeService = new CallTypeServiceImpl();
		List<CallType> callTypeDetail;
		
		String callType;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("callTypeBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");

			Iterator<?> it = array.getJSONObject(0).keys();
			while (it.hasNext())
				key = (String) it.next();
			if (!key.equals("callType")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", " + key
						+ " is invalid key, It must be \"callType\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
				
			}

			callType = (array.getJSONObject(0).getString("callType")).toString();
			logger.debug("callTypeBean: " + callType);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		
		if (callType.trim().equals("")) {
			try {
				callTypeDetail = callTypeService.getAllCallType();
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < callTypeDetail.size(); i++) {
			        jsonArray.put(callTypeDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new CallTypeNotFoundException();
			}
		} else {
			try {
				callTypeDetail = callTypeService.getCallType(callType);
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < callTypeDetail.size(); i++) {
			        jsonArray.put(callTypeDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new CallTypeNotFoundException();
			}
		}
	}
}