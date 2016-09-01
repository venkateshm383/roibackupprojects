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
import com.key2act.pojo.Technician;
import com.key2act.service.TechnicianNotFoundException;
import com.key2act.service.TechnicianService;
import com.key2act.service.impl.TechnicianImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this bean method is used get TechnicianId
 * @param exchange: exchange object to get post data
 * @throws JSONException: if json is invalid it throw exception
 * @throws TechnicianNotFoundException
 */
public class TechnicianBean  extends AbstractROICamelBean{
	
	private final Logger logger = LoggerFactory.getLogger(TechnicianBean.class);

	@Override
	protected void processBean(Exchange exchange) throws Exception {

		logger.debug("technicianBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("technicianBean Body: " + body);
		
		TechnicianService techService = new TechnicianImpl();
		List<Technician> techDetail;
		
		String technicianName;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("technicianBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");
			
			Iterator<?> it = array.getJSONObject(0).keys();
			while(it.hasNext())
				key = (String) it.next();
			if(!key.equals("technicianName")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
						+ key + " is invalid key, It must be \"technicianName\"");
				throw new InvalidJSONKey("JSON Key is Invalid");

			}
			
			technicianName = (array.getJSONObject(0).getString("technicianName")).toString();
			logger.debug("technicianBean: " + technicianName);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		
		if (technicianName.trim().equals("")) {
			try {
				techDetail = techService.getAllTechnicianId();
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < techDetail.size(); i++) {
			        jsonArray.put(techDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new TechnicianNotFoundException();
			}
		} else {
			try {
				techDetail = techService.getTechnicianId(technicianName);
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < techDetail.size(); i++) {
			        jsonArray.put(techDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new TechnicianNotFoundException(e.getMessage());
			}
		}
	}
}