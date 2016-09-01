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

import com.getusroi.wms20.parcel.parcelservice.exception.ParcelServiceException;
import com.getusroi.wms20.parcel.parcelservice.fedEx.constant.FedexConstant;
import com.getusroi.wms20.parcel.parcelservice.fedEx.vo.ContactAddress;
import com.getusroi.wms20.parcel.parcelservice.fedEx.vo.Shipper;

/**
 * 
 * @author BizRuntimeIT Services Pvt-Ltd
 */
public class FedexShipmentServiceBuilder {

	private Logger log = Logger.getLogger(FedexShipmentServiceBuilder.class.getName());
	private final String XPATH_EXPRESSION = "Shipment/Shipper/shipper_id/text()";

	/**
	 * to save changes the xml which has to be transformed
	 * 
	 * @param xml
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public String getTranformedGenericXml(Object xml, Shipper ship, Map<String, Object> permaData)
			throws TransformerConfigurationException, TransformerFactoryConfigurationError, ParcelServiceException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		Document document;
		try {
			document = appendNodes((String) xml, ship, permaData);
		} catch (ParserConfigurationException e) {
			throw new ParcelServiceException("Unable to append/modify the xml string: ", e);
		}
		DOMSource source = new DOMSource(document);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new ParcelServiceException("Unable to transform the appended xml: ", e);
		}
		return writer.toString();
	}// end of the method

	/**
	 * to parse the xml input to get the shipperId
	 * 
	 * @param xml
	 * @return shipperId in integer format
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public int getShipperIdfromXml(Object xml) throws ParcelServiceException, ParserConfigurationException {
		log.info("inside the get shipperId " + xml);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(((String) xml).getBytes(StandardCharsets.UTF_8));
		Document document;
		try {
			document = builder.parse(stream);
		} catch (SAXException | IOException e) {
			throw new ParcelServiceException("Unable to generate the document: ", e);
		}
		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = XPATH_EXPRESSION;
		// read a string value
		int id = 0;
		try {
			id = Integer.parseInt(xPath.compile(expression).evaluate(document));
		} catch (NumberFormatException | XPathExpressionException e) {
			throw new ParcelServiceException("Unable to get the shipperID from the document: ", e);
		}
		log.info("the ship id is: " + id);
		return id;
	}// end of the method

	/**
	 * sets the extra values
	 * 
	 * @param mapobj
	 * @return ShipperObject
	 */
	public Shipper setShipperDetails(int shipperIdin, Map<String, Object> permaData) {

		Object object = permaData.get(FedexConstant.PARCEL_SHIPPER_ADDRESS);
		JSONObject jsonObject = new JSONObject(object.toString());

		int shipId = (Integer) jsonObject.getInt(FedexConstant.SHIPPER_ID);

		if (shipperIdin == shipId) {
			log.info("inside set shipper id: " + shipId);
			return setShipperAddress(jsonObject);

		}

		return null;

	}// end of the method
	
	/**
	 * to get the permastore data
	 * 
	 * @param exchange
	 * @return properties, from the permastore as json
	 */
	private JSONObject getPropertiesfromPermastore(Map<String, Object> permaData) {
		log.debug("The permaData in FedEx: " + permaData);
		Object object = permaData.get(FedexConstant.FEDEX_PROPERTIES_PERMA);
		JSONObject jsonObject = new JSONObject(object.toString());
		JSONObject jsonObjectProperties = new JSONObject(
				jsonObject.get(FedexConstant.FEDEX_PROPERTIES_JSON).toString());
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
	 * this is to append elements missing and needful for th request generation
	 * of the FedEx
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
			throws ParcelServiceException, ParserConfigurationException {

		JSONObject properyObj = getPropertiesfromPermastore(permaData);
		JSONObject fedExCreds = getFedExCredsfromPermastore(permaData);

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		Document document;
		try {
			document = documentBuilder.parse(stream);
		} catch (SAXException | IOException e) {
			throw new ParcelServiceException("Unable to parse the xml document in createShipment : ", e);
		}

		NodeList rootList = document.getElementsByTagName(FedexConstant.SHIPMENT);
		Node root = rootList.item(0);

		Element wsauth = document.createElement(FedexConstant.AUTH_DETAIL);
		root.appendChild(wsauth); // append as before
		Element usercred = document.createElement(FedexConstant.USER_CRED);
		wsauth.appendChild(usercred);

		Element clientDetail = document.createElement(FedexConstant.CLIENT_DETAIL);
		root.appendChild(clientDetail); // append as before
		Element accountNumber = document.createElement(FedexConstant.ACCOUNT_NUMBER);
		clientDetail.appendChild(accountNumber);
		Element meterNumber = document.createElement(FedexConstant.METER_NUMBER);
		clientDetail.appendChild(meterNumber);
		Text accountNumberVal = document.createTextNode(fedExCreds.getString(FedexConstant.ACCOUNT_NUMBER));
		accountNumber.appendChild(accountNumberVal);
		Text meterNumberVal = document.createTextNode(fedExCreds.getString(FedexConstant.METER_NUMBER));
		meterNumber.appendChild(meterNumberVal);

		Element version = document.createElement(FedexConstant.VERSION);
		root.appendChild(version);
		Element serviceId = document.createElement(FedexConstant.SERVICE_ID);
		version.appendChild(serviceId);
		Element major = document.createElement(FedexConstant.MAJOR);
		version.appendChild(major);
		Text serviceIdVal = document.createTextNode(properyObj.getString(FedexConstant.SERVICE_ID_SHIP));
		serviceId.appendChild(serviceIdVal);
		Text majorVal = document.createTextNode(properyObj.getString(FedexConstant.MAJOR_SHIP));
		major.appendChild(majorVal);
		Element intermediate = document.createElement(FedexConstant.INTERMEDIATE);
		version.appendChild(intermediate);
		Element minor = document.createElement(FedexConstant.MINOR);
		version.appendChild(minor);
		Text intermediateVal = document.createTextNode(properyObj.getString(FedexConstant.INTERMEDIATE_SHIP));
		intermediate.appendChild(intermediateVal);
		Text minorVal = document.createTextNode(properyObj.getString(FedexConstant.MINOR_SHIP));
		minor.appendChild(minorVal);

		Element key = document.createElement(FedexConstant.KEY);
		Element password = document.createElement(FedexConstant.PASSWORD);
		usercred.appendChild(key);
		usercred.appendChild(password);
		Text keyVal = document.createTextNode(fedExCreds.getString(FedexConstant.KEY));
		key.appendChild(keyVal);
		Text passVal = document.createTextNode(fedExCreds.getString(FedexConstant.PASSWORD));
		password.appendChild(passVal);

		Element requestedShipment = document.createElement(FedexConstant.REQUEST_SHIPMENT);
		root.appendChild(requestedShipment);
		Element shipTimestamp = document.createElement(FedexConstant.SHIP_TIME_STAMP);
		requestedShipment.appendChild(shipTimestamp);
		Element dropoffType = document.createElement(FedexConstant.DROP_OFF_TYPE);
		requestedShipment.appendChild(dropoffType);
		Element packagingType = document.createElement(FedexConstant.PACKAGING_TYPE);
		requestedShipment.appendChild(packagingType);

		Text shipTimestampVal = document.createTextNode(properyObj.getString(FedexConstant.SHIP_TIME_STAMP_SHIP));
		shipTimestamp.appendChild(shipTimestampVal);
		Text dropoffTypeVal = document.createTextNode(properyObj.getString(FedexConstant.DROP_OFF_TYPE_SHIP));
		dropoffType.appendChild(dropoffTypeVal);
		Text packagingTypeVal = document.createTextNode(properyObj.getString(FedexConstant.PACKAGING_TYPE_SHIP));
		packagingType.appendChild(packagingTypeVal);

		Element labelSpecification = document.createElement(FedexConstant.LABEL_SPECIFICATION);
		root.appendChild(labelSpecification);
		Element labelFormatType = document.createElement(FedexConstant.LABEL_FORMAT_TYPE);
		labelSpecification.appendChild(labelFormatType);
		Text labelFormatTypeVal = document.createTextNode(properyObj.getString(FedexConstant.LABEL_FORMAT_TYPE_SHIP));
		labelFormatType.appendChild(labelFormatTypeVal);

		Element shippingChargesPayment = document.createElement(FedexConstant.SHIPPING_CHARGES_PAYMENT);
		root.appendChild(shippingChargesPayment);
		Element paymentType = document.createElement(FedexConstant.PAYMENT_TYPE);
		shippingChargesPayment.appendChild(paymentType);
		Text paymentTypeVal = document.createTextNode(properyObj.getString(FedexConstant.PAYMENT_TYPE_SHIP));
		paymentType.appendChild(paymentTypeVal);

		// Shipper details setting
		Element shipperDetails = document.createElement(FedexConstant.SHIPPER_DETAILS);
		root.appendChild(shipperDetails);

		Element contAdd = document.createElement(FedexConstant.CONTACT_ADDRESS);
		shipperDetails.appendChild(contAdd);
		Element companyName = document.createElement(FedexConstant.COMPANY_NAME);
		contAdd.appendChild(companyName);
		Element phoneNumber = document.createElement(FedexConstant.PH_NUM);
		contAdd.appendChild(phoneNumber);
		Element streetLine1 = document.createElement(FedexConstant.STREET_LINE1);
		contAdd.appendChild(streetLine1);
		Element streetLine2 = document.createElement(FedexConstant.STREET_LINE2);
		contAdd.appendChild(streetLine2);
		Element city = document.createElement(FedexConstant.CITY);
		contAdd.appendChild(city);
		Element stateOrProvinceCode = document.createElement(FedexConstant.STATE_CODE);
		contAdd.appendChild(stateOrProvinceCode);
		Element postalCode = document.createElement(FedexConstant.POSTAL_CODE);
		contAdd.appendChild(postalCode);
		Element countryCode = document.createElement(FedexConstant.COUNTRY_CODE);
		contAdd.appendChild(countryCode);

		Text companyNameVal = document.createTextNode(ship.getContactaddress().getCompanyName());
		companyName.appendChild(companyNameVal);
		Text phVal = document.createTextNode(ship.getContactaddress().getPhoneNumber());
		phoneNumber.appendChild(phVal);
		Text stln1 = document.createTextNode(ship.getContactaddress().getStreetLine1());
		streetLine1.appendChild(stln1);
		Text stln2 = document.createTextNode(ship.getContactaddress().getStreetLine2());
		streetLine2.appendChild(stln2);
		Text cityVal = document.createTextNode(ship.getContactaddress().getCity());
		city.appendChild(cityVal);
		Text stateVal = document.createTextNode(ship.getContactaddress().getStateOrProvinceCode());
		stateOrProvinceCode.appendChild(stateVal);
		Text post = document.createTextNode(ship.getContactaddress().getPostalCode());
		postalCode.appendChild(post);
		Text cntry = document.createTextNode(ship.getContactaddress().getCountryCode());
		countryCode.appendChild(cntry);

		return document;

	}// end of the method

	/**
	 * 
	 * @param jsonObject
	 * @return Shipper object
	 */
	private Shipper setShipperAddress(JSONObject jsonObject) {

		JSONObject jsonObjectAddress = new JSONObject(jsonObject.get(FedexConstant.ADDRESS).toString());
		Shipper shipper = new Shipper();
		ContactAddress contactaddress = new ContactAddress();
		contactaddress.setCompanyName(jsonObjectAddress.getString(FedexConstant.COMPANY_NAME));
		contactaddress.setPhoneNumber(jsonObjectAddress.getString(FedexConstant.PH_NUM));
		contactaddress.setStreetLine1(jsonObjectAddress.getString(FedexConstant.STREET_LINE1));
		contactaddress.setStreetLine2(jsonObjectAddress.getString(FedexConstant.STREET_LINE2));
		contactaddress.setCity(jsonObjectAddress.getString(FedexConstant.CITY));
		contactaddress.setStateOrProvinceCode(jsonObjectAddress.getString(FedexConstant.STATE_CODE));
		contactaddress.setCountryCode(jsonObjectAddress.getString(FedexConstant.COUNTRY_CODE));
		contactaddress.setPostalCode(jsonObjectAddress.getString(FedexConstant.POSTAL_CODE));
		shipper.setContactaddress(contactaddress);

		return shipper;
	}// end of the method

}