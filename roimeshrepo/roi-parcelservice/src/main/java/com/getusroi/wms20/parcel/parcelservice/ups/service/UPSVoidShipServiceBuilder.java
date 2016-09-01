package com.getusroi.wms20.parcel.parcelservice.ups.service;

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

import com.getusroi.wms20.parcel.parcelservice.ups.constant.UPSConstant;

public class UPSVoidShipServiceBuilder {

	private Logger log = Logger.getLogger(UPSVoidShipServiceBuilder.class.getName());

	/**
	 * @method loadProperties file
	 * @return Properties
	 */
	private Properties loadPropertiesFile() {
		Properties prop = new Properties();
		InputStream input = UPSVoidShipServiceBuilder.class.getClassLoader()
				.getResourceAsStream(UPSConstant.PROPERTIES);
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

		log.debug("Permastore data in Ups: " + permaData);
		Object object = permaData.get(UPSConstant.VOID_PROPERTIES_JSON);
		log.debug("Permastore dataObject in Ups: " + object);
		JSONObject jsonObject = new JSONObject(object.toString());
		JSONObject jsonObjectProperties = new JSONObject(jsonObject.get("upsproperties").toString());
		log.debug("Permastore JSON-data in Ups: " + jsonObjectProperties);
		return jsonObjectProperties;
	}

	private JSONObject getUpsCredentialsfromPermastore(Map<String, Object> permaData) {
		Object object = permaData.get(UPSConstant.UPS_CREDENTIALS_PERMA);
		JSONObject jsonObject = new JSONObject(object.toString());
		JSONObject jsonObjectUpsCredentials = new JSONObject(
				jsonObject.get(UPSConstant.UPS_CREDENTIALS_JSON).toString());
		log.debug("Permastore JSON-creds in the ups: " + jsonObjectUpsCredentials);
		return jsonObjectUpsCredentials;
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
		JSONObject jsonObjectcreds = getUpsCredentialsfromPermastore(permaData);

		log.debug("The json object inside the document builder: " + jsonObject);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		Document document = documentBuilder.parse(stream);

		NodeList rootList = document.getElementsByTagName(UPSConstant.VOID);
		Node root = rootList.item(0);

		Element upssecu = document.createElement(UPSConstant.UPS_SECURITY);
		root.appendChild(upssecu);
		Element usertoken = document.createElement(UPSConstant.USER_NAME_TOKEN);
		upssecu.appendChild(usertoken);
		Element user = document.createElement(UPSConstant.USER_NAME);
		usertoken.appendChild(user);
		Element pass = document.createElement(UPSConstant.PASSWORD);
		usertoken.appendChild(pass);
		Text userval = document.createTextNode(jsonObjectcreds.getString(UPSConstant.USER_NAME));
		user.appendChild(userval);
		Text passval = document.createTextNode(jsonObjectcreds.getString(UPSConstant.PASSWORD));
		pass.appendChild(passval);

		Element servicetoken = document.createElement(UPSConstant.SERVICE_ACCESS_TOKEN);
		upssecu.appendChild(servicetoken);
		Element accesslic = document.createElement(UPSConstant.ACCESS_LICENSE_NUMBER);
		servicetoken.appendChild(accesslic);
		Text accessval = document.createTextNode(jsonObjectcreds.getString(UPSConstant.ACCESS_LICENSE_NUMBER));
		accesslic.appendChild(accessval);

		Element voidshipment = document.createElement(UPSConstant.VOID_SHIPMENT);
		root.appendChild(voidshipment);
		/*Element shipmentnumber = document.createElement(UPSConstant.SHIPMENT_IDNUMBER);
		voidshipment.appendChild(shipmentnumber);
		Text shipidval = document.createTextNode(jsonObject.getString(UPSConstant.SHIPMENT_IDNUMBER));
		shipmentnumber.appendChild(shipidval);*/
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
