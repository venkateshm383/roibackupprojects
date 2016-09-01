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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.getusroi.wms20.parcel.parcelservice.ups.constant.UPSConstant;
import com.getusroi.wms20.parcel.parcelservice.ups.vo.ContactAddress;
import com.getusroi.wms20.parcel.parcelservice.ups.vo.Shipper;

/**
 * Bean called form the camel context of ups-impl
 * 
 * @author bizruntime
 *
 */
public class UPSRATE_ServiceBuilder {

	private Logger log = Logger.getLogger(UPSRATE_ServiceBuilder.class.getName());

	/**
	 * loadProperties file
	 * 
	 * @return Properties
	 */ // NOt used #TODO remove it
	private Properties loadPropertiesFile() {
		Properties prop = new Properties();
		InputStream input = UPSRATE_ServiceBuilder.class.getClassLoader().getResourceAsStream(UPSConstant.PROPERTIES);
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
		Object object = permaData.get(UPSConstant.UPS_PROPERTIES_PERMA);
		JSONObject jsonObject = new JSONObject(object.toString());
		log.debug("The json: " + jsonObject);
		JSONObject jsonObjectUpsProperties = new JSONObject(jsonObject.get(UPSConstant.UPS_PROPERTIES_JSON).toString());
		log.debug("Permastore JSON-data in the ups: " + jsonObjectUpsProperties);
		return jsonObjectUpsProperties;
	}// end of method

	/**
	 * the method will fetch the corresponding credential data from the permastore
	 * @param permaData
	 * @return
	 */
	private JSONObject getUpsCredentialsfromPermastore(Map<String, Object> permaData) {
		Object object = permaData.get(UPSConstant.UPS_CREDENTIALS_PERMA);
		JSONObject jsonObject = new JSONObject(object.toString());
		JSONObject jsonObjectUpsCredentials = new JSONObject(
				jsonObject.get(UPSConstant.UPS_CREDENTIALS_JSON).toString());
		log.debug("Permastore JSON-creds in the ups: " + jsonObjectUpsCredentials);
		return jsonObjectUpsCredentials;
	}

	/**
	 * method to append the unavailable nodes
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
		log.debug("inside append nodes: " + ship.getContactaddress().getCity() + "  the xml : " + xml);
		JSONObject jsonObjectUpsProperties = getUpsPropertiesfromPermastore(permaData);
		JSONObject jsonObjectUpsCredentials = getUpsCredentialsfromPermastore(permaData);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		Document document = documentBuilder.parse(stream);

		NodeList rootList = document.getElementsByTagName(UPSConstant.RATE_REQUEST);
		Node root = rootList.item(0);

		Element upssecu = document.createElement(UPSConstant.UPS_SECURITY);
		root.appendChild(upssecu);
		Element usertoken = document.createElement(UPSConstant.USER_NAME_TOKEN);
		upssecu.appendChild(usertoken);
		Element user = document.createElement(UPSConstant.USER_NAME);
		usertoken.appendChild(user);
		Element pass = document.createElement(UPSConstant.PASSWORD);
		usertoken.appendChild(pass);
		Text userval = document.createTextNode(jsonObjectUpsCredentials.getString(UPSConstant.USER_NAME));
		user.appendChild(userval);
		Text passval = document.createTextNode(jsonObjectUpsCredentials.getString(UPSConstant.PASSWORD));
		pass.appendChild(passval);

		Element servicetoken = document.createElement(UPSConstant.SERVICE_ACCESS_TOKEN);
		upssecu.appendChild(servicetoken);
		Element accesslic = document.createElement(UPSConstant.ACCESS_LICENSE_NUMBER);
		servicetoken.appendChild(accesslic);
		Text accessval = document.createTextNode(jsonObjectUpsCredentials.getString(UPSConstant.ACCESS_LICENSE_NUMBER));
		accesslic.appendChild(accessval);

		Element req = document.createElement(UPSConstant.REQUEST);
		root.appendChild(req);
		Element reqOption = document.createElement(UPSConstant.REQUST_OPTION);
		req.appendChild(reqOption);
		Text rate = document.createTextNode("Rate");
		reqOption.appendChild(rate);

		Element subversion = document.createElement(UPSConstant.SUB_VERSION);
		req.appendChild(subversion);

		Element tranrefer = document.createElement(UPSConstant.TRANSACTION_REFERENCE);
		req.appendChild(tranrefer);
		
		Element xpciver = document.createElement(UPSConstant.XPCI_VERSION);
		tranrefer.appendChild(xpciver);
		
		Text xpcivertval = document.createTextNode(jsonObjectUpsProperties.getString(UPSConstant.XPCI_VERSION_RATE));
		xpciver.appendChild(xpcivertval);
		Element tranid = document.createElement(UPSConstant.TRANSACTION_IDENTIFIER);
		tranrefer.appendChild(tranid);

		Element pickuptype = document.createElement(UPSConstant.PICKUP_TYPE);
		root.appendChild(pickuptype);
		Element pickupcode = document.createElement(UPSConstant.PICKUP_TYPE_CODE);
		pickuptype.appendChild(pickupcode);
		Text pickupval = document.createTextNode(jsonObjectUpsProperties.getString(UPSConstant.PICKUP_TYPE_CODE_RATE));
		pickupcode.appendChild(pickupval);

		Element customerclass = document.createElement(UPSConstant.CUSTOMER_CLASSIFICATION);
		root.appendChild(customerclass);
		Element customerclasscode = document.createElement(UPSConstant.CUSTOMER_CLASSIFICATION_CODE);
		customerclass.appendChild(customerclasscode);
		Text customerclassval = document
				.createTextNode(jsonObjectUpsProperties.getString(UPSConstant.CUSTOMER_CLASSIFICATION_CODE_RATE));
		customerclasscode.appendChild(customerclassval);

		// Note: We are considering the package code from the input itself
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
	}// end of method

	/**
	 * to tranform the new document , wtih appended values
	 * 
	 * @param object
	 * @return string of xml
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public String getTranformedGenericXml(Object xml, Shipper ship, Map<String, Object> permaData)
			throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError,
			TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		Document document = appendNodes((String) xml, ship, permaData);
		DOMSource source = new DOMSource(document);
		transformer.transform(source, result);
		return writer.toString();
	}// end of method

	/**
	 * get the shipperID from the generic xml and verify against the permaStore
	 * shipperID
	 * 
	 * @param xml
	 * @return id in integer
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public int getShipperIdfromXml(Object xml)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		log.debug("inside the get shipperId " + xml);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(((String) xml).getBytes(StandardCharsets.UTF_8));
		Document document = builder.parse(stream);
		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = UPSConstant.XPATH_SHIPPERID;

		// read a string value
		int id = Integer.parseInt(xPath.compile(expression).evaluate(document));
		log.debug("the ship id is: " + id);
		return id;
	}// end of method

	/**
	 * sets the shipper details, from perMastore
	 * 
	 * @param mapStore
	 * @param exchange
	 * @return
	 */
	public Shipper setShipperDetails(Map<String, Object> mapStore, Map<String, Object> permaData) {

		log.debug("The permaData, in the setShipper: " + permaData);
		Object object = permaData.get(UPSConstant.SHIPPER_ADDRESS);
		JSONObject jsonObject = new JSONObject(object.toString());
		int shipperId = (Integer) mapStore.get(UPSConstant.SHIPPER_ID);
		// #TODO have to make changes in the logic according to data
		int shipId = (Integer) jsonObject.getInt(UPSConstant.SHIPPER_ID);
		if (shipperId == shipId) {
			return serShipperAddress(jsonObject);
		}
		return null;
	}// end of method

	/**
	 * sets the shipper address
	 * 
	 * @param jsonObject
	 * @return shipper object
	 */
	private Shipper serShipperAddress(JSONObject jsonObject) {
		log.debug(".in serShipperAddress().. ");
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
	}// end of the method

	/**
	 * this is to replace the ups service type Values from integer to respective
	 * service names
	 * 
	 * @param jsonArrayIn
	 * @return json array
	 */
	public JSONArray replaceUpsServiceTypeNames(JSONArray jsonArrayIn, Exchange exchange)  {
		log.debug(".in replaceUpsServiceTypeNames().. ");
		String jsonArrayString = jsonArrayIn.toString();
		JSONArray jsonArray = new JSONArray(jsonArrayString);
		String[] name = { "UPS Standard", "UPS Ground", "UPS 3 Day Select", "UPS 2nd Day Air", "UPS 2nd Day Air AM",
				"UPS Next Day Air Saver", "UPS Next Day Air", "UPS Next Day Air Early A.M", "UPS Worldwide Express",
				"UPS Worldwide Express Plus", "UPS Worldwide Expedited", "UPS World Wide Saver" };
		int[] inttype = { 11, 3, 12, 2, 59, 13, 1, 14, 7, 54, 8, 65 };
		JSONArray jsonArray2 = new JSONArray();
		JSONObject objectRef = null;
		try {
			objectRef = jsonArray.getJSONObject(0);
			if (objectRef.has("PackageRateDetail")) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					Object serViceType = jsonObject.get("ServiceType");
					try {
						int val = Integer.parseInt(serViceType.toString());
					/*	if (val == (int) val) {*/
							int j;
							for (j = 0; j < inttype.length; j++) {
								if (val == inttype[j]) {
									jsonObject.put("ServiceType", name[j]);
								} else {
									jsonObject = (JSONObject) jsonArray.get(i);
								}
							}
						//}
					} catch (Exception e) {
						jsonObject = (JSONObject) jsonArray.get(i);
						log.debug("inside catch block: " + jsonObject);
					}
					jsonArray2.put(jsonObject);
				}
				return jsonArray2;
			}//..end of if-replaceServiceType
		} catch (Exception e) {
			log.error("exception in getting PackageRateDetails: " + e);
		}
		JSONObject value = new JSONObject();
		JSONObject errorJosn = jsonArray.getJSONObject(0);
		log.debug("The fault json string: "+errorJosn);
		Message out = exchange.getOut();
		out.setHeader(Exchange.HTTP_RESPONSE_CODE, new Integer(404));
		out.setHeader(org.apache.cxf.message.Message.RESPONSE_CODE, new Integer(404));
		JSONArray r = jsonArray2.put(value.put("ServiceUnavailability", "The requested service not available"));
		out.setBody(r);
		return r;
	}//..end of the method
}
