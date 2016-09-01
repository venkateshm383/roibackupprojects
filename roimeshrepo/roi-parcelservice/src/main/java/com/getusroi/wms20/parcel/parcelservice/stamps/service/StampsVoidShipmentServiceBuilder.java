package com.getusroi.wms20.parcel.parcelservice.stamps.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

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

import com.getusroi.wms20.parcel.parcelservice.stamps.constant.StampsConstant;

public class StampsVoidShipmentServiceBuilder {
	private Logger log = Logger.getLogger(StampsVoidShipmentServiceBuilder.class.getName());

	/**
	 * @method loadProperties file
	 * @return Properties
	 */
	private Properties loadPropertiesFile() {
		Properties prop = new Properties();
		InputStream input = StampsVoidShipmentServiceBuilder.class.getClassLoader().getResourceAsStream("");
		try {
			prop.load(input);
		} catch (IOException e) {

		}
		return prop;
	}

	/**
	 * to get the permastore data
	 * 
	 * @param exchange
	 * @return properties, from the permastore as json
	 */
	private JSONObject getPropertiesfromPermastore(Map<String, Object> permaData) {

		log.debug("Permastore data in Stamps: " + permaData);
		Object object = permaData.get(StampsConstant.VOID_PROPERTIES_JSON);
		log.debug("Permastore dataObject in Stamps: " + object);
		JSONObject jsonObject = new JSONObject(object.toString());
		JSONObject jsonObjectProperties = new JSONObject(jsonObject.get("stampsproperties").toString());
		log.debug("Permastore JSON-data in stamps: " + jsonObjectProperties);
		return jsonObjectProperties;
	}

	private JSONObject getStampsCredentialsfromPermastore(Map<String, Object> permaData) {
		Object object = permaData.get(StampsConstant.STAMPS_CREDENTIALS_PERMA);
		JSONObject jsonObject = new JSONObject(object.toString());
		JSONObject jsonObjectStampsCredentials = new JSONObject(
				jsonObject.get(StampsConstant.STAMPS_CREDENTIALS_JSON).toString());
		log.debug("Permastore JSON-creds in the stamps: " + jsonObjectStampsCredentials);
		return jsonObjectStampsCredentials;
	}

	/**
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
		// Properties prop = loadPropertiesFile();
		log.debug("The xml inside the document builder: " + xml);
		JSONObject jsonObject = getPropertiesfromPermastore(permaData);
		JSONObject jsonObjectcreds = getStampsCredentialsfromPermastore(permaData);

		log.debug("The json object inside the document builder: " + jsonObject);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		Document document = documentBuilder.parse(stream);

		NodeList rootList = document.getElementsByTagName(StampsConstant.VOID);
		Node root = rootList.item(0);

		Element stampssecu = document.createElement(StampsConstant.STAMPS_SECURITY);
		root.appendChild(stampssecu);
		Element usertoken = document.createElement(StampsConstant.USER_NAME_TOKEN);
		stampssecu.appendChild(usertoken);
		Element user = document.createElement(StampsConstant.USER_NAME);
		usertoken.appendChild(user);
		Element pass = document.createElement(StampsConstant.PASSWORD);
		usertoken.appendChild(pass);
		Text userval = document.createTextNode(jsonObjectcreds.getString(StampsConstant.USER_NAME));
		user.appendChild(userval);
		Text passval = document.createTextNode(jsonObjectcreds.getString(StampsConstant.PASSWORD));
		pass.appendChild(passval);

		Element servicetoken = document.createElement(StampsConstant.SERVICE_ACCESS_TOKEN);
		stampssecu.appendChild(servicetoken);
		Element accesslic = document.createElement(StampsConstant.ACCESS_LICENSE_NUMBER);
		servicetoken.appendChild(accesslic);
		Text accessval = document.createTextNode(jsonObjectcreds.getString(StampsConstant.ACCESS_LICENSE_NUMBER));
		accesslic.appendChild(accessval);

		Element voidshipment = document.createElement(StampsConstant.VOID_SHIPMENT);
		root.appendChild(voidshipment);
		/*
		 * Element shipmentnumber =
		 * document.createElement(stampsConstant.SHIPMENT_IDNUMBER);
		 * voidshipment.appendChild(shipmentnumber); Text shipidval =
		 * document.createTextNode(jsonObject.getString(stampsConstant.
		 * SHIPMENT_IDNUMBER)); shipmentnumber.appendChild(shipidval);
		 */
		log.debug("The document inside the document builder: " + document);
		return document;

	}

	/**
	 * 
	 * @param object
	 * @return
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
		log.debug("The xml inside the transformer: " + xml.toString());
		Document document = appendNodes((String) xml, permaData);
		DOMSource source = new DOMSource(document);
		transformer.transform(source, result);
		return writer.toString();
	}

}
