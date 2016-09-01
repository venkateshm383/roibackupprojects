package com.key2act.sac.notification.bean;


import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.xml.transform.TransformerException;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.key2act.sac.notification.service.SCNotifyRequestProcessingException;
import com.key2act.sac.notification.service.SCNotifySendingResponseException;
import com.key2act.sac.notification.util.NotificationMasterConstants;
import com.key2act.sac.notification.util.NotificationUtil;

public class ServiceChannelNotificationProcessBean {
	final Logger logger = LoggerFactory.getLogger(ServiceChannelNotificationProcessBean.class);


	/**
	 * method to process the exchange, create the request to be sent, and send
	 * to the ServiceChannel Endpoint.
	 * 
	 * @param exchange
	 * @throws SCNotifyRequestProcessingException
	 * @throws SCNotifySendingResponseException
	 * @throws TransformerException 
	 */
	public void processorBean(Exchange exchange)
			throws SCNotifyRequestProcessingException, SCNotifySendingResponseException, TransformerException {
		logger.debug("(.) Exchange in processor Bean in ServiceChannelNotificationProcessBean class  ");
		NotificationUtil notificationUtil = new NotificationUtil();
		MeshHeader meshHeader=(MeshHeader)exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);

		Document document = notificationUtil.generateDocumentFromString(exchange.getIn().getBody(String.class));
		NotificationUtil notificationUti=new NotificationUtil();
		//To Add  PIN
		notificationUti.addAttributeToXmlDocument(document, NotificationMasterConstants.PIN, notificationUti.getDataFromCacheData(meshHeader.getTenant(), NotificationMasterConstants.PIN, NotificationMasterConstants.PROVIDER_DETAILS_PERMASTORE_KEY, meshHeader), NotificationMasterConstants.PIN_XPATH);
		
		// creating the request
		String request = NotificationMasterConstants.STORE_ID_ENTITY + "="
				+notificationUti.getDataFromCacheData(meshHeader.getTenant(), NotificationMasterConstants.STORE_ID_KEY, NotificationMasterConstants.PROVIDER_DETAILS_PERMASTORE_KEY, meshHeader) + "&";
		logger.debug("  Store ID: " + request.trim());
		request = request.trim() + NotificationMasterConstants.CUSTOMER_ID_ENTITY.trim() + "="
				+ notificationUti.getDataFromCacheData(meshHeader.getTenant(), NotificationMasterConstants.CUSTOMER_ID_KEY, NotificationMasterConstants.PROVIDER_DETAILS_PERMASTORE_KEY, meshHeader)+ "&".trim();
		logger.debug("  Store ID And Customer Id : " + request.trim());
		request = request.trim() + NotificationMasterConstants.XML_STRING_ENTITY.trim() + "="
				+notificationUti.encodeFromXpath(NotificationMasterConstants.XMLREQUEST_XPATH, document);
		logger.debug("    created request before  trim : " + request.toString());
		request = request.toString().replaceAll(NotificationMasterConstants.ZERO_WIDTH_WHITE_SPACE_CHAR, "");
		logger.debug("    created request after trim : " + request);
		
		exchange.getIn().setBody(request);
		String scUrl=notificationUti.getDataFromCacheData(meshHeader.getTenant(), NotificationMasterConstants.URL_SC, NotificationMasterConstants.PROVIDER_DETAILS_PERMASTORE_KEY, meshHeader);
		
		exchange.getIn().setHeader("headerData", 		exchange.getIn().getHeaders());
		//to remove base URL 
		exchange.getIn().setHeader("CamelHttpUri", scUrl);		
		exchange.getIn().setHeader(NotificationMasterConstants.URL_SC,scUrl);
		//storing request to header to fetch in other activity 
		exchange.getIn().setHeader(NotificationMasterConstants.REQUEST_DATA_KEY, notificationUti.documentToString(document));
		exchange.getIn().setHeader("Accept-Language", "en-US,en;q=0.5");
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
		
		
	}// ..end of method
	
	/**
	 * To get response from Service channel set In Header sucess or failur status 
	 * @param exchange
	 * @throws SCNotifyRequestProcessingException
	 * @throws TransformerException
	 */
	public void checkResponseFromSC(Exchange exchange) throws SCNotifyRequestProcessingException, TransformerException{
		NotificationUtil notificationUtil = new NotificationUtil();
		MeshHeader meshHeader=(MeshHeader)exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		int responsecode=exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
		logger.debug(" Service Channel  Response Code : "+responsecode);
		String bodyData=exchange.getIn().getBody(String.class);
		logger.debug(" Response From Service Channel :  "+ bodyData);
		Document document = notificationUtil.generateDocumentFromString(exchange.getIn().getHeader(NotificationMasterConstants.REQUEST_DATA_KEY).toString());

		//to remove header 
		exchange.getIn().removeHeader(NotificationMasterConstants.REQUEST_DATA_KEY);

		if(responsecode==200){
			if(bodyData.contains("OK")){
				notificationUtil.addElmentToXmlDocument(document, NotificationMasterConstants.SUCESS_STATUS_NODE, NotificationMasterConstants.SUCCESS,NotificationMasterConstants.XPATH_SC_INTERNAL_STATUS);
				notificationUtil.getStatusNameFromXmlData(document,meshHeader,exchange);
				logger.debug("if  status upateding sucess to SERVICE CHANNEL ---------------------");
			}else{
				logger.debug("if  status Updating failed or status Not exist  to SERVICE CHANNEL ---------------------");
				notificationUtil.addElmentToXmlDocument(document, NotificationMasterConstants.SUCESS_STATUS_NODE, NotificationMasterConstants.FAILED,NotificationMasterConstants.XPATH_SC_INTERNAL_STATUS);
			
			}
		}
		String xmlData=notificationUtil.documentToString(document);
		//To stop restlet hanging for formation for response
		//setting xml data to body which need for further execution pipe activity's 
		exchange.getIn().setHeaders((Map<String, Object>)(exchange.getIn().getHeader("headerData")));
		exchange.getIn().removeHeader("headerData");
		exchange.getIn().setBody(xmlData);
	
		
	}
	
	



	

	
}