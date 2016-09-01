package com.key2act.bean;

import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.key2act.pojo.PriceMatrix;
import com.key2act.service.ContactNumberNotFoundException;
import com.key2act.service.PriceMatrixNotFoundException;
import com.key2act.service.PriceMatrixService;
import com.key2act.service.impl.PriceMatrixServiceImpl;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

public class PriceMatrixServiceBean extends AbstractROICamelJDBCBean {

	/**
	 * this bean method is used get price matrix
	 * 
	 * @param exchange:
	 *            exchange object to get post data
	 * @throws JSONException:
	 *             if json is invalid it throw exception
	 * @throws ContactNumberNotFoundException
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception {

		logger.debug("priceMatrixBean Exchange: " + exchange);
		String body = exchange.getIn().getBody(String.class);
		logger.debug("priceMatrixBean Body: " + body);
		String priceMatrixItem;
		String key = "";
		try {
			JSONObject jsonObject = new JSONObject(body);
			logger.debug("priceMatrixBean Body: " + jsonObject);
			JSONArray array = jsonObject.getJSONArray("data");

			Iterator<?> it = array.getJSONObject(0).keys();
			while (it.hasNext())
				key = (String) it.next();
			if (!key.equals("priceMatrixItem")) {
				exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", " + key
						+ " is invalid key, It must be \"priceMatrixItem\"");
				return;
			}

			priceMatrixItem = (array.getJSONObject(0).getString("priceMatrixItem")).toString();
			logger.debug("priceMatrixBean: " + priceMatrixItem);
		} catch (JSONException e1) {
			exchange.getOut().setBody("Error Code:" + ErrorCodeConstant.JSON_INVALID_ERROR_CODE + ", "
					+ PropertyFileLoader.getProperties().getProperty(ErrorCodeConstant.JSON_INVALID_ERROR_CODE));
			throw new JSONException(e1.getMessage());
		}

		PriceMatrixService priceMatrixService = new PriceMatrixServiceImpl();
		List<PriceMatrix> priceDetail;
		if (priceMatrixItem.trim().equals("")) {
			try {
				priceDetail = priceMatrixService.getAllPriceMatrix();

				JSONArray priceMatrixJson = new JSONArray();
				for (PriceMatrix priceMatrix : priceDetail) {
					JSONObject priceMatrixJsonObj = new JSONObject(priceMatrix);
					priceMatrixJson.put(priceMatrixJsonObj);
				}

				logger.debug("priceMatrixBean Detail: " + priceMatrixJson);
				exchange.getIn().setBody(priceMatrixJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new PriceMatrixNotFoundException(e.getMessage());
			}
		} else {
			try {
				priceDetail = priceMatrixService.getPriceMatrix(priceMatrixItem);
				JSONArray priceMatrixJson = new JSONArray();
				for (PriceMatrix priceMatrix : priceDetail) {
					JSONObject priceMatrixJsonObj = new JSONObject(priceMatrix);
					priceMatrixJson.put(priceMatrixJsonObj);
				}

				logger.debug("priceMatrixBean Detail: " + priceMatrixJson);
				exchange.getIn().setBody(priceMatrixJson);
			} catch (Exception e) {
				exchange.getOut().setBody(e.getMessage());
				throw new PriceMatrixNotFoundException(e.getMessage());
			}
		}
	}
}
