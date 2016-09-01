package com.getusroi.wms20.parcel.parcelservice.fedEx.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.getusroi.wms20.parcel.parcelservice.fedEx.constant.FedexConstant;

/**
 * To aid the process bean called from the impl route
 * 
 * @author BizRuntimeIT Services Pvt-Ltd
 */
public class FedexVoidShipServiceBuilder {

	private Logger log = Logger.getLogger(FedexVoidShipServiceBuilder.class.getName());

	/**
	 * to transform the generic input xml
	 * 
	 * @param xml
	 * @return string in xml format
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public String getTranformedGenericXml(Object xml, Map<String, Object> permaData)
			throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError,
			TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		Document document = appendNodes((String) xml, permaData);
		DOMSource source = new DOMSource(document);
		transformer.transform(source, result);
		return writer.toString();
	}// ..end of the method

	/**
	 * to get the permastore data
	 * 
	 * @param exchange
	 * @return properties, from the permastore as json
	 */
	private JSONObject getPropertiesfromPermastore(Map<String, Object> permaData) {
		log.debug("The permaData in FedEx: " + permaData);
		Object object = permaData.get("FedExProperties");
		JSONObject jsonObject = new JSONObject(object.toString());
		JSONObject jsonObjectProperties = new JSONObject(jsonObject.get("fedexproperties").toString());
		log.debug("Permastore JSON-data in fedEx: " + jsonObjectProperties);
		return jsonObjectProperties;
	}

	/**
	 * gets the json parsed from the permastore for the credentials of FedEx
	 * 
	 * @param permaData
	 * @return jsonObjectCredentials
	 */
	private JSONObject getFedExCredsfromPermastore(Map<String, Object> permaData) {
		Object object = permaData.get(FedexConstant.FEDEX_CREDS_PERMA);
		JSONObject jsonObject = new JSONObject(object.toString());
		JSONObject jsonObjectCredentials = new JSONObject(
				jsonObject.get(FedexConstant.FEDEX_CREDENTIALS_JSON).toString());
		log.debug("Permastore JSON-creds in fedEx: " + jsonObjectCredentials);
		return jsonObjectCredentials;
	}

	/**
	 * this method is used to append the extra nodes to the generic xml input
	 * 
	 * @param xml
	 * @return transformed xml-in-String
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	// #TODO Either in java object, or other way to feed cxf
	private Document appendNodes(String xml, Map<String, Object> permaData) throws ParserConfigurationException,
			SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {

		JSONObject properyObj = getPropertiesfromPermastore(permaData);
		JSONObject fedExCreds = getFedExCredsfromPermastore(permaData);

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		Document document = documentBuilder.parse(stream);

		NodeList rootList = document.getElementsByTagName(FedexConstant.VOID);
		Node root = rootList.item(0);

		Element deleteShipent = document.createElement(FedexConstant.DELETE_SHIPMENT_REQUEST);
		root.appendChild(deleteShipent);

		Element shipTimeStamp = document.createElement(FedexConstant.SHIP_TIME_STAMP);
		root.appendChild(shipTimeStamp);
		Text shipTimeStampVal = document.createTextNode(properyObj.getString(FedexConstant.SHIP_TIME_STAMP_VOID));
		shipTimeStamp.appendChild(shipTimeStampVal);

		Element trackingIdType = document.createElement(FedexConstant.TRACKING_ID_TYPE);
		root.appendChild(trackingIdType);
		Text trackingIdTypeVal = document.createTextNode(properyObj.getString(FedexConstant.TRACKING_ID_TYPE_VOID));
		trackingIdType.appendChild(trackingIdTypeVal);

		Element deletionControl = document.createElement(FedexConstant.DELETION_CONTROL);
		root.appendChild(deletionControl);
		Text deletionControlVal = document.createTextNode(properyObj.getString(FedexConstant.DELETION_CONTROL_VOID));
		deletionControl.appendChild(deletionControlVal);

		Element wsauth = document.createElement(FedexConstant.AUTH_DETAIL);
		deleteShipent.appendChild(wsauth);
		Element usercred = document.createElement(FedexConstant.USER_CRED);
		wsauth.appendChild(usercred);

		Element clientDetail = document.createElement(FedexConstant.CLIENT_DETAIL);
		deleteShipent.appendChild(clientDetail);
		Element accountNumber = document.createElement(FedexConstant.ACCOUNT_NUMBER);
		clientDetail.appendChild(accountNumber);
		Element meterNumber = document.createElement(FedexConstant.METER_NUMBER);
		clientDetail.appendChild(meterNumber);
		Text accountNumberVal = document.createTextNode(fedExCreds.getString(FedexConstant.ACCOUNT_NUMBER));
		accountNumber.appendChild(accountNumberVal);
		Text meterNumberVal = document.createTextNode(fedExCreds.getString(FedexConstant.METER_NUMBER));
		meterNumber.appendChild(meterNumberVal);

		Element version = document.createElement(FedexConstant.VERSION);
		deleteShipent.appendChild(version);
		Element serviceId = document.createElement(FedexConstant.SERVICE_ID);
		version.appendChild(serviceId);
		Element major = document.createElement(FedexConstant.MAJOR);
		version.appendChild(major);
		Text serviceIdVal = document.createTextNode(properyObj.getString(FedexConstant.SERVICE_ID_VOID));
		serviceId.appendChild(serviceIdVal);
		Text majorVal = document.createTextNode(properyObj.getString(FedexConstant.MAJOR_VOID));
		major.appendChild(majorVal);
		Element intermediate = document.createElement(FedexConstant.INTERMEDIATE);
		version.appendChild(intermediate);
		Element minor = document.createElement(FedexConstant.MINOR);
		version.appendChild(minor);
		Text intermediateVal = document.createTextNode(properyObj.getString(FedexConstant.INTERMEDIATE_VOID));
		intermediate.appendChild(intermediateVal);
		Text minorVal = document.createTextNode(properyObj.getString(FedexConstant.MINOR_VOID));
		minor.appendChild(minorVal);

		Element key = document.createElement(FedexConstant.KEY);
		Element password = document.createElement(FedexConstant.PASSWORD);
		usercred.appendChild(key);
		usercred.appendChild(password);
		Text keyVal = document.createTextNode(fedExCreds.getString(FedexConstant.KEY));
		key.appendChild(keyVal);
		Text passVal = document.createTextNode(fedExCreds.getString(FedexConstant.PASSWORD));
		password.appendChild(passVal);

		return document;

	}// ..end of the method
}