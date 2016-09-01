package com.getusroi.wms20.parcel.parcelservice.stamps.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

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

import com.getusroi.wms20.parcel.parcelservice.stamps.constant.StampsConstant;
import com.getusroi.wms20.parcel.parcelservice.stamps.vo.ContactAddress;
import com.getusroi.wms20.parcel.parcelservice.stamps.vo.Shipper;

public class StampsShipmentServiceBuilder {

	private final String XPATH_EXPRESSION = "ShipmentRequest/Shipment/Shipper/shipper_id/text()";
	private Logger log = Logger.getLogger(StampsShipmentServiceBuilder.class.getName());

	/**
	 * loadProperties file #TODO remove it
	 * 
	 * @return Properties
	 */
	private Properties loadPropertiesFile() {
		Properties prop = new Properties();
		InputStream input = StampsShipmentServiceBuilder.class.getClassLoader().getResourceAsStream("");
		try {
			prop.load(input);
		} catch (IOException e) {

		}
		return prop;
	}

	/**
	 * gets the json parsed form the permastore and returns the stampsproperties
	 * value
	 * 
	 * @param exchanget
	 * @return jsonObjectstampsProperties
	 */
	private JSONObject getStampsPropertiesfromPermastore(Map<String, Object> permaData) {
		log.debug("Permastore data in the stamps: " + permaData);
		Object object = permaData.get("StampsProperties");
		JSONObject jsonObject = new JSONObject(object.toString());
		JSONObject jsonObjectStampsProperties = new JSONObject(jsonObject.get("stampsproperties").toString());
		log.debug("Permastore JSON-data in the stamps: " + jsonObjectStampsProperties);
		return jsonObjectStampsProperties;
	}// end of method

	/**
	 * 
	 * @param permaData
	 * @return json object of the stamps credentials
	 */
	private JSONObject getStampsCredentialsfromPermastore(Map<String, Object> permaData) {
		Object object = permaData.get("StampsCredentials");
		JSONObject jsonObject = new JSONObject(object.toString());
		JSONObject jsonObjectStampsCredentials = new JSONObject(jsonObject.get("stampscreds").toString());
		log.debug("Permastore JSON-creds in the stamps: " + jsonObjectStampsCredentials);
		return jsonObjectStampsCredentials;
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
		JSONObject jsonObjectStampsProperties = getStampsPropertiesfromPermastore(permaData);
		JSONObject jsonObjectStampsCreds = getStampsCredentialsfromPermastore(permaData);

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		Document document = documentBuilder.parse(stream);

		NodeList rootList = document.getElementsByTagName(StampsConstant.SHIPMENT);
		Node root = rootList.item(0);

		Element stampssecu = document.createElement(StampsConstant.STAMPS_SECURITY);
		root.appendChild(stampssecu);
		Element usertoken = document.createElement(StampsConstant.USER_NAME_TOKEN);
		stampssecu.appendChild(usertoken);
		Element user = document.createElement(StampsConstant.USER_NAME);
		usertoken.appendChild(user);
		Element pass = document.createElement(StampsConstant.PASSWORD);
		usertoken.appendChild(pass);
		Text userval = document.createTextNode(jsonObjectStampsCreds.getString(StampsConstant.USER_NAME));
		user.appendChild(userval);
		Text passval = document.createTextNode(jsonObjectStampsCreds.getString(StampsConstant.PASSWORD));
		pass.appendChild(passval);

		Element servicetoken = document.createElement(StampsConstant.SERVICE_ACCESS_TOKEN);
		stampssecu.appendChild(servicetoken);
		Element accesslic = document.createElement(StampsConstant.ACCESS_LICENSE_NUMBER);
		servicetoken.appendChild(accesslic);
		Text accessval = document.createTextNode(jsonObjectStampsCreds.getString(StampsConstant.ACCESS_LICENSE_NUMBER));
		accesslic.appendChild(accessval);

		// this below block is to set Guid each time

		Element accesslicIntegTXGuid = document.createElement("IntegratorTxID");
		servicetoken.appendChild(accesslicIntegTXGuid);
		Text accesslicIntegTXGuidval = document
				.createTextNode(getGuid(jsonObjectStampsCreds.getString(StampsConstant.USER_NAME)));
		accesslicIntegTXGuid.appendChild(accesslicIntegTXGuidval);

		// Shipper details setting
		Element shipperDetails = document.createElement(StampsConstant.SHIPPER_DETAILS);
		root.appendChild(shipperDetails);
		Element contAdd = document.createElement(StampsConstant.CONTACT_ADDRESS);
		shipperDetails.appendChild(contAdd);
		Element name = document.createElement(StampsConstant.N_NAME);
		contAdd.appendChild(name);
		Element addressLine = document.createElement(StampsConstant.ADDRESS_LINE_KEY);
		contAdd.appendChild(addressLine);
		Element city = document.createElement(StampsConstant.CITY);
		contAdd.appendChild(city);
		Element stateOrProvinceCode = document.createElement(StampsConstant.STATE);
		contAdd.appendChild(stateOrProvinceCode);
		Element postalCode = document.createElement(StampsConstant.POSTAL_CODE);
		contAdd.appendChild(postalCode);
		Element countryCode = document.createElement(StampsConstant.COUNTRY_CODE);
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

		log.debug("THe docuent in append nodes Stamps: " + document);
		return document;
	}// ..end of the method append and generates the new document

	/**
	 * this is to generate a random uuid and append the Username along with it
	 * (for unique identification)
	 * 
	 * @param userName
	 * @return unique random string
	 */
	private String getGuid(String userName) {
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		return randomUUIDString + "-" + userName;
	}// ..end of the method

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
			throws TransformerConfigurationException, TransformerFactoryConfigurationError {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		Document document = null;
		try {
			document = appendNodes((String) xml, ship, permaData);
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
		}
		DOMSource source = new DOMSource(document);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
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
	public int getShipperIdfromXml(Object xml) throws ParserConfigurationException {
		log.info("inside the get shipperId " + xml);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(((String) xml).getBytes(StandardCharsets.UTF_8));
		Document document = null;
		try {
			document = builder.parse(stream);
		} catch (SAXException | IOException e) {
		}
		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = XPATH_EXPRESSION;

		// read a string value
		int id = 0;
		try {
			id = Integer.parseInt(xPath.compile(expression).evaluate(document));
		} catch (NumberFormatException | XPathExpressionException e) {

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

		int shipperId = (Integer) mapStore.get(StampsConstant.SHIPPER_ID);

		Object object = permaData.get(StampsConstant.SHIPPER_ADDRESS);
		JSONObject jsonObject = new JSONObject(object.toString());
		int shipId = (Integer) jsonObject.getInt(StampsConstant.SHIPPER_ID);
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

		JSONObject jsonObjectAddress = new JSONObject(jsonObject.get(StampsConstant.ADDRESS).toString());
		Shipper shipper = new Shipper();
		ContactAddress contactaddress = new ContactAddress();
		contactaddress.setName(jsonObjectAddress.getString(StampsConstant.NAME));
		contactaddress.setAddressLine(jsonObjectAddress.getString(StampsConstant.ADDRESS_LINE));
		contactaddress.setCity(jsonObjectAddress.getString(StampsConstant.CITY));
		contactaddress.setStateOrProvinceCode(jsonObjectAddress.getString(StampsConstant.STATE));
		contactaddress.setCountryCode(jsonObjectAddress.getString(StampsConstant.COUNTRY_CODE));
		contactaddress.setPostalCode(jsonObjectAddress.getString(StampsConstant.POSTAL_CODE));
		shipper.setContactaddress(contactaddress);

		return shipper;
	}

}
