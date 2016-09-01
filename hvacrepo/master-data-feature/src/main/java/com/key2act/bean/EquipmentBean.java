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
import com.key2act.pojo.Equipment;
import com.key2act.service.EquipmentNotFoundException;
import com.key2act.service.EquipmentService;
import com.key2act.service.impl.EquipmentImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this bean method is used get EquipmentId
 * @param exchange: exchange object to get post data
 * @throws JSONException: if json is invalid it throw exception
 * @throws EquipmentNotFoundException
 */


public class EquipmentBean extends AbstractROICamelBean  {
	
	private final Logger logger = LoggerFactory.getLogger(EquipmentBean.class);

	@Override
	protected void processBean(Exchange exchange) throws Exception {

		logger.debug("equipmentBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("equipmentBean Body: " + body);
		EquipmentService equipment = new EquipmentImpl();
		List<Equipment> equipmentDetail;
		
		String equipmentName;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("equipmentBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");
			
			Iterator<?> it = array.getJSONObject(0).keys();
			while(it.hasNext())
				key = (String) it.next();
			if(!key.equals("equipmentName")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
						+ key + " is invalid key, It must be \"equipmentName\"");
				throw new InvalidJSONKey("JSON Key is Invalid");
			
			}
			
			equipmentName = (array.getJSONObject(0).getString("equipmentName")).toString();
			logger.debug("priceMatrixBean: " + equipmentName);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		
		if (equipmentName.trim().equals("")) {
			try {
				equipmentDetail = equipment.getAllEquipmentId();
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < equipmentDetail.size(); i++) {
			        jsonArray.put(equipmentDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new EquipmentNotFoundException();
			}
		} else {
			try {
				equipmentDetail = equipment.getEquipmentId(equipmentName);
				// convert list into JSON
				JSONArray jsonArray = new JSONArray();
				for (int i=0; i < equipmentDetail.size(); i++) {
			        jsonArray.put(equipmentDetail.get(i).getJSONObject());
				}

				logger.debug("CallStatusbean callstatus Detail : " + jsonArray);
				exchange.getIn().setBody(jsonArray);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new EquipmentNotFoundException();
			}
		}
	}
}