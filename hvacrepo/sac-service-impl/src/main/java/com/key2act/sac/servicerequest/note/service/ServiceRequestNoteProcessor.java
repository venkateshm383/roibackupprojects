package com.key2act.sac.servicerequest.note.service;

import org.apache.camel.Exchange;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.getusroi.integrationfwk.activities.bean.ActivityEnricherException;
import com.key2act.sac.util.ServiceRequestUtil;
import com.key2act.sac.util.UnableToConvertJsonServiceRequestToXML;
import com.key2act.sac.util.UnableToParseServiceRequestException;

public class ServiceRequestNoteProcessor {
	Logger logger=LoggerFactory.getLogger(ServiceRequestNoteProcessor.class);
	
	/**
	 * This method is used to check the incoming exchange body format
	 * @param exchange : CamelExchange Object
	 * @throws UnableToConvertJsonServiceRequestToXML 
	 * @throws JSONException 
	 */
	public void checkIncomingExchangeDataFormat(Exchange exchange) throws UnableToConvertJsonServiceRequestToXML {
		logger.debug(".checkIncomingExchangeDataFormat method of WorkOrderNoteProcessor");
		String inExchangeBody=exchange.getIn().getBody(String.class);
		ServiceRequestUtil serviceRequestUtil=new ServiceRequestUtil();
		//if exchange body starts with '{' its a json data therefore we need to convert it into xml first else, we expect it to be xml data
		if(inExchangeBody.trim().startsWith("{")){
			logger.debug("incomming data is json data, therefore convert it into json before forwding to pipeline");			
			serviceRequestUtil.checkAndConvertRequestToXML(exchange);
		}else{
			logger.debug("incomming data is xml data,forward the data to pipeline");
			exchange.getIn().setBody(inExchangeBody);
		}
	}//end of method checkIncomingExchangeDataFormat
	
	/**
	 * This method is used to check if 'note' is available in incoming request data or not.
	 * @param exchange : Camel Exchange Object
	 * @throws UnableToParseServiceRequestException 
	 * @throws ActivityEnricherException
	 */
	public void checkIfNoteExistInRequest(Exchange exchange) throws UnableToParseServiceRequestException {
		logger.debug(".checkIfNoteExistInRequest method of WorkOrderNoteProcessor");
		String inExchnageBody=exchange.getIn().getBody(String.class);
		ServiceRequestUtil serviceRequestUtil=new ServiceRequestUtil();
		//get the document object of the input xml
		Document document=serviceRequestUtil.generateDocumentFromString(inExchnageBody);
		NodeList nodeList=document.getElementsByTagName("NOTE");
		if(nodeList !=null && nodeList.getLength()>0){
			
		}
	}//end of method checkIfNoteExistInRequest
	
	
}
