package com.getusroi.wms20.printservice.bean;

import java.io.IOException;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.wms20.printservice.RhapsodyPrintServiceConstant;
import com.getusroi.wms20.printservice.exception.CustomRhapsodyException;
import com.getusroi.wms20.printservice.rhapsodyservice.RhapsodyService;

/**
 * 
 * @author bizruntime
 *
 */
public class PrintStatus extends AbstractROICamelJDBCBean {

	Logger logger = LoggerFactory.getLogger(PrintStatus.class);

	/**
	 *  is the method to get the status in json format
	 */
	@Override
	protected void processBean(Exchange exchange) throws CustomRhapsodyException {
		logger.debug(".processBean()...GetStatus");
		Message message = exchange.getIn();
		String body = message.getBody(String.class);
		String soapEnvlope[] = body.split("\\r?\\n");
		try {
			JSONObject jsonObject = XML.toJSONObject(soapEnvlope[5]);
			message.setBody(jsonObject);
		} catch (JSONException e) {
			throw new CustomRhapsodyException("Error converting SoapEnv to Json: " + e);
			
		}
	}//end of method

	/**
	 * this method gets the authentication, to make a session with the rhapsodyServer
	 * @param exchange
	 * @throws CustomRhapsodyException
	 */
	public void getAuthentication(Exchange exchange) throws CustomRhapsodyException {
		logger.debug(".getAuthentication()...GetStatus");
		RhapsodyService rhapsodyService = new RhapsodyService();
		Message message = exchange.getIn();
		Float body = message.getBody(Float.class);
		try {
			Map<String, String> map = rhapsodyService.getRhapsodyConnectionProperties();
			String username = map.get(RhapsodyPrintServiceConstant.USER_NAME);
			String password = map.get(RhapsodyPrintServiceConstant.PASSWORD);
			String url = map.get(RhapsodyPrintServiceConstant.URL);
			message.setHeader(RhapsodyPrintServiceConstant.USER_NAME, username);
			message.setHeader(RhapsodyPrintServiceConstant.PASSWORD, password);
			message.setHeader(RhapsodyPrintServiceConstant.PRINT_JOB_ID, body);
			message.setHeader(RhapsodyPrintServiceConstant.URL, url);
		} catch (IOException e) {
			throw new CustomRhapsodyException("Loading property file encountered error .PrintStatus", e);

		}

	}//end of method

}
