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
import com.key2act.pojo.TaskCode;
import com.key2act.service.TaskCodeNotFoundException;
import com.key2act.service.impl.TaskcodeImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this bean method is used get TaskCode
 * @param exchange: exchange object to get post data
 * @throws JSONException: if json is invalid it throw exception
 * @throws TaskCodeNotFoundException
 */
public class TaskCodeBean extends AbstractROICamelBean {
	
	private final Logger logger = LoggerFactory.getLogger(TaskCodeBean.class);

	@Override
	protected void processBean(Exchange exchange) throws Exception {

		logger.debug("taskCodeBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("taskCodeBean Body: " + body);
		
		TaskcodeImpl taskCode = new TaskcodeImpl();
		List<TaskCode> taskDetail;
		
		String taskName;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("taskCodeBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");
			
			Iterator<?> it = array.getJSONObject(0).keys();
			while(it.hasNext())
				key = (String) it.next();
			if(!key.equals("taskName")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
						+ key + " is invalid key, It must be \"taskName\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
				
			}
			
			taskName = (array.getJSONObject(0).getString("taskName")).toString();
			logger.debug("taskCodeBean: " + taskName);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		
		if (taskName.trim().equals("")) {
			try {
				taskDetail = taskCode.getAllTaskCode();
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < taskDetail.size(); i++) {
			        jsonArray.put(taskDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new TaskCodeNotFoundException();
			}
		} else {
			try {
				taskDetail = taskCode.getTaskCode(taskName);
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < taskDetail.size(); i++) {
			        jsonArray.put(taskDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new TaskCodeNotFoundException();
			}
		}
	}
}