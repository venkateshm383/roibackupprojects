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
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.getusroi.wms20.parcel.parcelservice.exception.UpsException;
import com.getusroi.wms20.parcel.parcelservice.ups.constant.UPSConstant;
import com.getusroi.wms20.parcel.parcelservice.ups.vo.ContactAddress;
import com.getusroi.wms20.parcel.parcelservice.ups.vo.Shipper;

public class UPSShipmentServiceBuilder {

	private final String XPATH_EXPRESSION = "ShipmentRequest/Shipment/Shipper/shipper_id/text()";
	private Logger log = Logger.getLogger(UPSShipmentServiceBuilder.class.getName());

	/**
	 * loadProperties file #TODO remove it
	 * 
	 * @return Properties
	 */
	private Properties loadPropertiesFile() {
		Properties prop = new Properties();
		InputStream input = UPSShipmentServiceBuilder.class.getClassLoader()
				.getResourceAsStream(UPSConstant.PROPERTIES);
		try {
			prop.load(input);
		} catch (IOException e) {

		}
		return prop;
	}

	/**
	 * gets the json parsed form the permastore and returns the upsproperties
	 * value
	 * 
	 * @param exchanget
	 * @return jsonObjectUpsProperties
	 */
	private JSONObject getUpsPropertiesfromPermastore(Map<String, Object> permaData) {
		log.debug("Permastore data in the ups: " + permaData);
		Object object = permaData.get(UPSConstant.UPS_PROPERTIES_PERMA_SHIP);
		JSONObject jsonObject = new JSONObject(object.toString());
		JSONObject jsonObjectUpsProperties = new JSONObject(jsonObject.get(UPSConstant.UPS_PROPERTIES_JSON).toString());
		log.debug("Permastore JSON-data in the ups: " + jsonObjectUpsProperties);
		return jsonObjectUpsProperties;
	}// end of method

	private JSONObject getUpsCredentialsfromPermastore(Map<String, Object> permaData) {
		Object object = permaData.get(UPSConstant.UPS_CREDENTIALS_PERMA);
		JSONObject jsonObject = new JSONObject(object.toString());
		JSONObject jsonObjectUpsCredentials = new JSONObject(
				jsonObject.get(UPSConstant.UPS_CREDENTIALS_JSON).toString());
		log.debug("Permastore JSON-creds in the ups: " + jsonObjectUpsCredentials);
		return jsonObjectUpsCredentials;
	}
	
	/**
	 * this method, appends the unavailable tags to the input xml
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
	private Document appendNodes(String xml, Shipper ship, Map<String, Object> permaData)
			throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError,
			TransformerException {
		log.info("inside append nodes: " + ship.getContactaddress().getCity() + "  the xml : " + xml);
		// Properties prop = loadPropertiesFile();
		JSONObject jsonObjectUpsProperties = getUpsPropertiesfromPermastore(permaData);
		JSONObject jsonObjectUpsCreds = getUpsCredentialsfromPermastore(permaData);

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		Document document = documentBuilder.parse(stream);

		NodeList rootList = document.getElementsByTagName(UPSConstant.SHIPMENT);
		Node root = rootList.item(0);

		Element upssecu = document.createElement(UPSConstant.UPS_SECURITY);
		root.appendChild(upssecu);
		Element usertoken = document.createElement(UPSConstant.USER_NAME_TOKEN);
		upssecu.appendChild(usertoken);
		Element user = document.createElement(UPSConstant.USER_NAME);
		usertoken.appendChild(user);
		Element pass = document.createElement(UPSConstant.PASSWORD);
		usertoken.appendChild(pass);
		Text userval = document.createTextNode(jsonObjectUpsCreds.getString(UPSConstant.USER_NAME));
		user.appendChild(userval);
		Text passval = document.createTextNode(jsonObjectUpsCreds.getString(UPSConstant.PASSWORD));
		pass.appendChild(passval);

		Element servicetoken = document.createElement(UPSConstant.SERVICE_ACCESS_TOKEN);
		upssecu.appendChild(servicetoken);
		Element accesslic = document.createElement(UPSConstant.ACCESS_LICENSE_NUMBER);
		servicetoken.appendChild(accesslic);
		Text accessval = document.createTextNode(jsonObjectUpsCreds.getString(UPSConstant.ACCESS_LICENSE_NUMBER));
		accesslic.appendChild(accessval);

		Element req = document.createElement(UPSConstant.REQUEST);
		root.appendChild(req);
		Element reqOption = document.createElement(UPSConstant.REQUST_OPTION);
		req.appendChild(reqOption);
		Text shop = document.createTextNode(jsonObjectUpsProperties.getString(UPSConstant.REQUST_OPTION_SHIP));
		reqOption.appendChild(shop);

		Element subversion = document.createElement(UPSConstant.SUB_VERSION);
		req.appendChild(subversion);

		Element tranrefer = document.createElement(UPSConstant.TRANSACTION_REFERENCE);
		req.appendChild(tranrefer);
	/*	Element coustomercont = document.createElement(UPSConstant.CUSTOMER_CONTEXT);
		tranrefer.appendChild(coustomercont);*/
		Element xpciver = document.createElement(UPSConstant.XPCI_VERSION);
		tranrefer.appendChild(xpciver);
		/*Text customercontval = document.createTextNode(jsonObjectUpsProperties.getString(UPSConstant.CUSTOMER_CONTEXT_SHIP));
		coustomercont.appendChild(customercontval);*/
		Text xpcivertval = document.createTextNode(jsonObjectUpsProperties.getString(UPSConstant.XPCI_VERSION_SHIP));
		xpciver.appendChild(xpcivertval);
		Element tranid = document.createElement(UPSConstant.TRANSACTION_IDENTIFIER);
		tranrefer.appendChild(tranid);

		Element labelStockSize = document.createElement(UPSConstant.LABEL_STOCK_SIZE);
		root.appendChild(labelStockSize);
		Element labelStockSizeHeight = document.createElement(UPSConstant.LABEL_STOCK_SIZE_HEIGHT);
		labelStockSize.appendChild(labelStockSizeHeight);
		Element labelStockSizeWidth = document.createElement(UPSConstant.LABEL_STOCK_SIZE_WIDTH);
		labelStockSize.appendChild(labelStockSizeWidth);
		Text labelStockSizeHeightval = document
				.createTextNode(jsonObjectUpsProperties.getString(UPSConstant.LABEL_STOCK_SIZE_HEIGHT_SHIP));
		labelStockSizeHeight.appendChild(labelStockSizeHeightval);
		Text labelStockSizeWidthval = document
				.createTextNode(jsonObjectUpsProperties.getString(UPSConstant.LABEL_STOCK_SIZE_WIDTH_SHIP));
		labelStockSizeWidth.appendChild(labelStockSizeWidthval);

		// Shipper details setting
		Element shipperDetails = document.createElement(UPSConstant.SHIPPER_DETAILS);
		root.appendChild(shipperDetails);
		Element contAdd = document.createElement(UPSConstant.CONTACT_ADDRESS);
		shipperDetails.appendChild(contAdd);
		Element name = document.createElement(UPSConstant.N_NAME);
		contAdd.appendChild(name);
		Element addressLine = document.createElement(UPSConstant.ADDRESS_LINE_KEY);
		contAdd.appendChild(addressLine);
		Element city = document.createElement(UPSConstant.CITY);
		contAdd.appendChild(city);
		Element stateOrProvinceCode = document.createElement(UPSConstant.STATE);
		contAdd.appendChild(stateOrProvinceCode);
		Element postalCode = document.createElement(UPSConstant.POSTAL_CODE);
		contAdd.appendChild(postalCode);
		Element countryCode = document.createElement(UPSConstant.COUNTRY_CODE);
		contAdd.appendChild(countryCode);

		Text nameVal = document.createTextNode(ship.getContactaddress().getName());
		name.appendChild(nameVal);
		Text addressval = document.createTextNode(ship.getContactaddress().getAddressLine());
		addressLine.appendChild(addressval);
		Text cityVal = document.createTextNode(ship.getContactaddress().getCity());
		city.appendChild(cityVal);
		Text stateVal = document.createTextNode(ship.getContactaddress().getStateOrProvinceCode());
		stateOrProvinceCode.appendChild(stateVal);
		Text post = document.createTextNode(ship.getContactaddress().getPostalCode());
		postalCode.appendChild(post);
		Text cntry = document.createTextNode(ship.getContactaddress().getCountryCode());
		countryCode.appendChild(cntry);

		return document;

	}

	/**
	 * this is to commit the transformation of the xml
	 * 
	 * @param object
	 * @return string of xml after transformation
	 * @throws TransformerConfigurationException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public String getTranformedGenericXml(Object xml, Shipper ship, Map<String, Object> permaData)
			throws UpsException, TransformerConfigurationException, TransformerFactoryConfigurationError {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		Document document;
		try {
			document = appendNodes((String) xml, ship, permaData);
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			throw new UpsException("Unable to append the new elements to the generic xml input: ", e);
		}
		DOMSource source = new DOMSource(document);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new UpsException("Unable to commit the modifications to the Generic input xml: ", e);
		}
		return writer.toString();
	}

	/**
	 * this will get the shipperId form the input xml
	 * 
	 * @param xml
	 * @return integer shipperID
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public int getShipperIdfromXml(Object xml) throws UpsException, ParserConfigurationException {
		log.info("inside the get shipperId " + xml);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(((String) xml).getBytes(StandardCharsets.UTF_8));
		Document document;
		try {
			document = builder.parse(stream);
		} catch (SAXException | IOException e) {
			throw new UpsException("Unable to parse the Xml input string: ", e);
		}
		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = XPATH_EXPRESSION;

		// read a string value
		int id;
		try {
			id = Integer.parseInt(xPath.compile(expression).evaluate(document));
		} catch (NumberFormatException | XPathExpressionException e) {
			throw new UpsException("Unable to compile the Xpath Expression: ", e);
		}
		log.info("the ship id is: " + id);
		return id;
	}

	/**
	 * Sets the shipperDetails
	 * 
	 * @param mapStore
	 * @param permaData
	 * @return shipperObject
	 */
	public Shipper setShipperDetails(Map<String, Object> mapStore, Map<String, Object> permaData) {

		int shipperId = (Integer) mapStore.get(UPSConstant.SHIPPER_ID);

		Object object = permaData.get(UPSConstant.SHIPPER_ADDRESS);
		JSONObject jsonObject = new JSONObject(object.toString());
		int shipId = (Integer) jsonObject.getInt(UPSConstant.SHIPPER_ID);
		if (shipperId == shipId) {
			log.info("inside set shipper details: " + shipId);
			return serShipperAddress(jsonObject);
		}
		return null;
	}

	/**
	 * sets the shipper address
	 * 
	 * @param jsonObject
	 * @return shipper object
	 */
	private Shipper serShipperAddress(JSONObject jsonObject) {

		JSONObject jsonObjectAddress = new JSONObject(jsonObject.get(UPSConstant.ADDRESS).toString());
		Shipper shipper = new Shipper();
		ContactAddress contactaddress = new ContactAddress();
		contactaddress.setName(jsonObjectAddress.getString(UPSConstant.NAME));
		contactaddress.setAddressLine(jsonObjectAddress.getString(UPSConstant.ADDRESS_LINE));
		contactaddress.setCity(jsonObjectAddress.getString(UPSConstant.CITY));
		contactaddress.setStateOrProvinceCode(jsonObjectAddress.getString(UPSConstant.STATE));
		contactaddress.setCountryCode(jsonObjectAddress.getString(UPSConstant.COUNTRY_CODE));
		contactaddress.setPostalCode(jsonObjectAddress.getString(UPSConstant.POSTAL_CODE));
		shipper.setContactaddress(contactaddress);

		return shipper;
	}

}
