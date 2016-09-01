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
 * Aid to the process bean
 * 
 * @author BizRuntimeIT Services Pvt-Ltd
 */
public class FedexRateServiceBuilder {
	private Logger log = Logger.getLogger(FedexRateServiceBuilder.class.getName());
	

	/**
	 * to confirm the changes made for the xml, will save the changes as new
	 * document
	 * 
	 * @param xml
	 * @return xml in string
	 * @throws TransformerConfigurationException
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
		Document document = null;
		try {
			document = appendNodes((String) xml, ship, permaData);
		} catch (ParcelServiceException | ParserConfigurationException e) {
			log.error("Unable to append the nodes, elements malformed: " + e);
		}
		DOMSource source = new DOMSource(document);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new ParcelServiceException("Unable to update the nodes of xmlString: ", e);
		}
		return writer.toString();
	}

	/**
	 * method to get the shipper ID
	 * 
	 * @param xml
	 * @return id in integer
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public int getShipperIdfromXml(Object xml)
			throws ParcelServiceException, SAXException, IOException, ParserConfigurationException {
		log.debug(".get shipperId " + xml);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(((String) xml).getBytes(StandardCharsets.UTF_8));
		Document document = builder.parse(stream);
		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = FedexConstant.XPATH_SHIPPERID;
		// read a string value
		int id = 0;
		try {
			id = Integer.parseInt(xPath.compile(expression).evaluate(document));
		} catch (NumberFormatException | XPathExpressionException e) {
			throw new ParcelServiceException("Unable to parse and get the ShipperID: ", e);
		}
		log.debug("the ship id is: " + id);
		return id;
	}// end of the method

	/**
	 * sets the values for the respective shipperId, gets values from the
	 * permastore
	 * 
	 * @param mapobj
	 * @param exchange
	 * @return shipperObject
	 */ // #TODO have to pass the shipperId dynamically and little logical
		// changes to verify the id
	public Shipper setShipperDetails(int shipperIdin, Map<String, Object> permaData) {

		log.debug("The permastore data: " + permaData);
		Object object = permaData.get(FedexConstant.PARCEL_SHIPPER_ADDRESS);
		JSONObject jsonObject = new JSONObject(object.toString());
		log.debug("The permastore shipperdata: " + jsonObject);
		// #TODO have to make changes according to data format(Once Array of Data exists) for the multi-shipperId
		// data
		int shipId = (Integer) jsonObject.getInt(FedexConstant.SHIPPER_ID);
		if (shipperIdin == shipId) {
			return setShipperAddress(jsonObject);
		}
		return null;
	}// end of the method

	/**
	 * gets the json parsed form the permastore and returns the properties value
	 * 
	 * @param exchanget
	 * @return jsonObjectProperties
	 */
	private JSONObject getPropertiesfromPermastore(Map<String, Object> permaData) {
		Object object = permaData.get(FedexConstant.FEDEX_PROPERTIES_PERMA);
		JSONObject jsonObject = new JSONObject(object.toString());
		JSONObject jsonObjectProperties = new JSONObject(jsonObject.get(FedexConstant.FEDEX_PROPERTIES_JSON).toString());
		log.debug("Permastore JSON-data in fedEx: " + jsonObjectProperties);
		return jsonObjectProperties;
	}
	
	/**
	 * gets the json parsed from the permastore for the credentials of FedEx
	 * @param permaData
	 * @return jsonObjectCredentials
	 */
	private JSONObject getFedExCredsfromPermastore(Map<String, Object> permaData) {
		Object object = permaData.get(FedexConstant.FEDEX_CREDS_PERMA);
		JSONObject jsonObject = new JSONObject(object.toString());
		JSONObject jsonObjectCredentials = new JSONObject(jsonObject.get(FedexConstant.FEDEX_CREDENTIALS_JSON).toString());
		log.debug("Permastore JSON-creds in fedEx: " + jsonObjectCredentials);
		return jsonObjectCredentials;
	}

	/**
	 * method appends the nodes which is not in the xml
	 * 
	 * @param xml
	 * @return transformed xml-in-String
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	// #TODO Either in java object, or other way to feed cxf / shorten the
	// method
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
			throw new ParcelServiceException("ParseException when appending nodes: ", e);
		}

		NodeList rootList = document.getElementsByTagName(FedexConstant.RATE_REQUEST);
		Node root = rootList.item(0);

		Element wsauth = document.createElement(FedexConstant.AUTH_DETAIL);
		root.appendChild(wsauth); // append as before
		Element usercred = document.createElement(FedexConstant.USER_CRED);
		wsauth.appendChild(usercred);

		Element transactionDetail = document.createElement(FedexConstant.TRANSACTION_DETAIL);
		root.appendChild(transactionDetail); // append as before
		
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
		Text serviceIdVal = document.createTextNode(properyObj.getString(FedexConstant.SERVICE_ID_RATE));
		serviceId.appendChild(serviceIdVal);
		Text majorVal = document.createTextNode(properyObj.getString(FedexConstant.MAJOR_RATE));
		major.appendChild(majorVal);
		Element intermediate = document.createElement(FedexConstant.INTERMEDIATE);
		version.appendChild(intermediate);
		Element minor = document.createElement(FedexConstant.MINOR);
		version.appendChild(minor);
		Text intermediateVal = document.createTextNode(properyObj.getString(FedexConstant.INTERMEDIATE_RATE));
		intermediate.appendChild(intermediateVal);
		Text minorVal = document.createTextNode(properyObj.getString(FedexConstant.MINOR_RATE));
		minor.appendChild(minorVal);

		Element key = document.createElement(FedexConstant.KEY);
		Element password = document.createElement(FedexConstant.PASSWORD);
		usercred.appendChild(key);
		usercred.appendChild(password);
		Text keyVal = document.createTextNode(fedExCreds.getString(FedexConstant.KEY));
		key.appendChild(keyVal);
		Text passVal = document.createTextNode(fedExCreds.getString(FedexConstant.PASSWORD));
		password.appendChild(passVal);

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

	}

	/**
	 * to set the shipper data, comparing with the permadata
	 * 
	 * @param jsonObject
	 * @return shipper object
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