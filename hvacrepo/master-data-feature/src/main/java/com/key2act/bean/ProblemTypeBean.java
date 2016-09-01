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
import com.key2act.pojo.ProblemType;
import com.key2act.service.ProblemTypeIsNotFoundException;
import com.key2act.service.impl.ProblemTypeImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this bean method is used to get problemType
 * @param exchange
 * @throws JSONException
 * @throws ProblemTypeIsNotFoundException
 */

public class ProblemTypeBean extends AbstractROICamelBean {

	private final Logger logger = LoggerFactory.getLogger(ProblemTypeBean.class);

	@Override
	protected void processBean(Exchange exchange) throws Exception {

		logger.debug("problemTypeBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("problemTypeBean Body: " + body);
		ProblemTypeImpl problemType = new ProblemTypeImpl();
		List<ProblemType> problemTypeDetail;
		
		String technicalIssues;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("problemTypeBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");

			Iterator<?> it = array.getJSONObject(0).keys();
			while (it.hasNext())
				key = (String) it.next();
			if (!key.equals("technicalIssues")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", " + key
						+ " is invalid key, It must be \"technicalIssues\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
			}

			technicalIssues = (array.getJSONObject(0).getString("technicalIssues")).toString();
			logger.debug("problemTypeBean: " + technicalIssues);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		
		if (technicalIssues.trim().equals("")) {
			try {
				problemTypeDetail = problemType.getAllProblem();
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < problemTypeDetail.size(); i++) {
			        jsonArray.put(problemTypeDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new ProblemTypeIsNotFoundException();
			}
		} else {
			try {
				problemTypeDetail = problemType.getAllProblem();
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < problemTypeDetail.size(); i++) {
			        jsonArray.put(problemTypeDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new ProblemTypeIsNotFoundException();
			}
		}
	}
}
