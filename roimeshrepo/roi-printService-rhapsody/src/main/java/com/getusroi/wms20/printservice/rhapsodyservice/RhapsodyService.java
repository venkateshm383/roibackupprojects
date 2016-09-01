package com.getusroi.wms20.printservice.rhapsodyservice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.soap.MTOMFeature;

import org.apache.camel.Exchange;
import org.apache.commons.codec.binary.Base64;

import com.getusroi.config.RequestContext;
import com.getusroi.dynastore.DynaStoreFactory;
import com.getusroi.dynastore.DynaStoreSession;
import com.getusroi.wms20.printservice.RhapsodyPrintServiceConstant;

/**
 * 
 * @author bizruntime,
 *  Service class to aid the printServices
 *
 */
public class RhapsodyService {

	/**
	 * generateSoapRequest , responsible to build a soap request, in-order to
	 * send it to the RhapsodyServer to submitDocument
	 * 
	 * @param meshHeader
	 * @param exchange
	 * @param batchid
	 * @param printerId
	 * @return SoapMessage
	 * @throws IOException
	 * @throws SOAPException
	 */
	public SOAPMessage generateSoapRequest(RequestContext requestContext, Exchange exchange, String batchid,
			String printerId) throws IOException, SOAPException {
		SOAPMessage soapMessage = null;

		Map<String, String> map = getRhapsodyConnectionProperties();

		MessageFactory messageFactory = MessageFactory.newInstance();
		soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();
		SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
		soapEnvelope.addNamespaceDeclaration(RhapsodyPrintServiceConstant.DOCS_KEY, RhapsodyPrintServiceConstant.DOCS);
		SOAPBody soapBody = soapEnvelope.getBody();
		SOAPElement soapElement = soapBody.addChildElement(RhapsodyPrintServiceConstant.OPERATION, RhapsodyPrintServiceConstant.DOCS_KEY);
		SOAPElement element1 = soapElement.addChildElement(RhapsodyPrintServiceConstant.USER_NAME);
		element1.addTextNode(map.get(RhapsodyPrintServiceConstant.USER_NAME));
		SOAPElement element2 = soapElement.addChildElement(RhapsodyPrintServiceConstant.PASSWORD);
		element2.addTextNode(map.get(RhapsodyPrintServiceConstant.PASSWORD));
		SOAPElement element3 = soapElement.addChildElement(RhapsodyPrintServiceConstant.ALIAS);
		element3.addTextNode(printerId);
		SOAPElement element4 = soapElement.addChildElement(RhapsodyPrintServiceConstant.DOCUMENT);
		String base64 = Base64.encodeBase64String(getContentFromDynastore(requestContext, exchange, batchid));
		element4.addChildElement(RhapsodyPrintServiceConstant.CONTENT).addTextNode(base64);
		soapMessage.setProperty("Enable MTOM", true);
		new MTOMFeature(true);
		soapMessage.saveChanges();
		return soapMessage;
	}//end of method

	/**
	 * getRhapsodyConnectionProperties, loads Rhapsody Connection properties
	 * from ClassPath
	 * 
	 * @return map Object - contains properties
	 * @throws IOException
	 */
	public Map<String, String> getRhapsodyConnectionProperties() throws IOException {

		String propertyFile = RhapsodyPrintServiceConstant.PROPERTIES_FILE;
		InputStream inStream;

		Properties properties = new Properties();
		inStream = getClass().getClassLoader().getResourceAsStream(propertyFile);

		if (inStream != null)
			properties.load(inStream);
		else
			throw new FileNotFoundException("Property file with name " + propertyFile + " not found !");
		String wsdlUrl = properties.getProperty(RhapsodyPrintServiceConstant.WSDL_URL);
		String username = properties.getProperty(RhapsodyPrintServiceConstant.USER_NAME);
		String password = properties.getProperty(RhapsodyPrintServiceConstant.PASSWORD);
		String url = properties.getProperty(RhapsodyPrintServiceConstant.URL);
		String defaultDestination = properties.getProperty(RhapsodyPrintServiceConstant.DEFAULT_PRINTER);
		Map<String, String> map = new HashMap<>();
		map.put(RhapsodyPrintServiceConstant.WSDL_URL, wsdlUrl);
		map.put(RhapsodyPrintServiceConstant.USER_NAME, username);
		map.put(RhapsodyPrintServiceConstant.PASSWORD, password);
		map.put(RhapsodyPrintServiceConstant.DEFAULT_PRINTER, defaultDestination);
		map.put(RhapsodyPrintServiceConstant.URL, url);
		inStream.close();

		return map;
	}//end of method

	/**
	 * getContentFromDynastore, to get the content in byteArray from Dynastore
	 * 
	 * @param meshHeader
	 * @param exchange
	 * @param batchid
	 * @return content from Dynastore in byteArray
	 * @throws UnsupportedEncodingException
	 */
	private byte[] getContentFromDynastore(RequestContext requestContext, Exchange exchange, String batchid)
			throws UnsupportedEncodingException {

		DynaStoreSession cacheStrore = DynaStoreFactory.getDynaStoreSession(requestContext.getTenantId(),
				requestContext.getSiteId(), batchid);
		byte[] completeSubstitutedLabel = null;
		Object labeldata = (Object) cacheStrore.getSessionData(batchid);
		if (labeldata instanceof String) {
			String labelDataString = (String) labeldata;
			String replacedValue = labelDataString.trim().replace("\n", "").replace("\r", "");
			completeSubstitutedLabel = replacedValue.getBytes("UTF-8");
		} else if (labeldata instanceof byte[]) {
			completeSubstitutedLabel = (byte[]) labeldata;
		}

		return completeSubstitutedLabel;
	}//end of method

	/**
	 * getSoapResponse , from SoapMessage object to String transformation
	 * 
	 * @param soapResponse
	 * @return soapMessage in string
	 * @throws SOAPException
	 * @throws TransformerException
	 */
	public String getSoapResponse(SOAPMessage soapResponse) throws SOAPException, TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		Source sourceContent = soapResponse.getSOAPPart().getContent();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(sourceContent, result);
		String strResult = writer.toString();

		return strResult;
	}//end of method
}
