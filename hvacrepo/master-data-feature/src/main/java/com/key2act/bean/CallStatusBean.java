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
import com.key2act.pojo.CallStatus;
import com.key2act.service.CallStatusNotFoundException;
import com.key2act.service.CallStatusService;
import com.key2act.service.impl.CallStatusServiceImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class CallStatusBean extends AbstractROICamelBean {
	
	private final Logger logger = LoggerFactory.getLogger(CallStatusBean.class);

	/**
	 * this method is used to get callStatus
	 * @param exchange
	 * @throws JSONException
	 * @throws CallStatusNotFoundException
	 */
	protected void processBean(Exchange exchange)  throws Exception  {

		logger.debug("callStatusBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("callStatusBean Body: " + body);
		CallStatusService callstatus=new  CallStatusServiceImpl();
		List<CallStatus> callStatusDetail;
		
		String callStatusId;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("callStatusBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");

			Iterator<?> it = array.getJSONObject(0).keys();
			while (it.hasNext())
				key = (String) it.next();
			if (!key.equals("callstatusId")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", " + key
						+ " is invalid key, It must be \"callstatusId\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
				
			}

			callStatusId = (array.getJSONObject(0).getString("callstatusId")).toString();
			logger.debug("callStatusBean: " + callStatusId);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		if (callStatusId.trim().equals("")) {
			try {
				callStatusDetail = callstatus.getAllCallStatus();
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < callStatusDetail.size(); i++) {
			        jsonArray.put(callStatusDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new CallStatusNotFoundException();
			}
		} else {
			try {
				callStatusDetail = callstatus.getCallStatus(callStatusId);
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < callStatusDetail.size(); i++) {
			        jsonArray.put(callStatusDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new CallStatusNotFoundException();
			}
		}
	}
}
