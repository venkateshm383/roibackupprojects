package com.getusroi.wms20.parcel.parcelservice.stamps.service;

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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
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

public class StampsRateServiceBuilder {

	private Logger log = Logger.getLogger(StampsRateServiceBuilder.class.getName());

	/**
	 * gets the json parsed form the permastore and returns the upsproperties
	 * value
	 * 
	 * @param exchanget
	 * @return jsonObjectUpsProperties
	 */
	private JSONObject getStampsPropertiesfromPermastore(Map<String, Object> permaData) {
		log.debug("Permastore data in the stamps: " + permaData);
		Object object = permaData.get(StampsConstant.STAMPS_PROPERTIES_PERMA);
		JSONObject jsonObject = new JSONObject(object.toString());
		log.debug("The json: " + jsonObject);
		JSONObject jsonObjectStampsProperties = new JSONObject(
				jsonObject.get(StampsConstant.STAMPS_PROPERTIES_JSON).toString());
		log.debug("Permastore JSON-data in the stamps: " + jsonObjectStampsProperties);
		return jsonObjectStampsProperties;
	}// end of method

	private JSONObject getStampsCredentialsfromPermastore(Map<String, Object> permaData) {
		Object object = permaData.get(StampsConstant.STAMPS_CREDENTIALS_PERMA);
		JSONObject jsonObject = new JSONObject(object.toString());
		JSONObject jsonObjectStampsCredentials = new JSONObject(
				jsonObject.get(StampsConstant.STAMPS_CREDENTIALS_JSON).toString());
		log.debug("Permastore JSON-creds in the stamps: " + jsonObjectStampsCredentials);
		return jsonObjectStampsCredentials;
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
	public int getShipperIdfromXml(Object xml) throws ParserConfigurationException, SAXException, IOException {
		log.debug(".get shipperId " + xml);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(((String) xml).getBytes(StandardCharsets.UTF_8));
		Document document = builder.parse(stream);
		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = StampsConstant.XPATH_SHIPPERID;
		// read a string value
		int id = 0;
		try {
			id = Integer.parseInt(xPath.compile(expression).evaluate(document));
		} catch (NumberFormatException | XPathExpressionException e) {

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
		Object object = permaData.get(StampsConstant.PARCEL_SHIPPER_ADDRESS);
		JSONObject jsonObject = new JSONObject(object.toString());
		log.debug("The permastore shipperdata: " + jsonObject);
		// #TODO have to make changes according to data format(Once Array of
		// Data exists) for the multi-shipperId
		// data
		int shipId = (Integer) jsonObject.getInt(StampsConstant.SHIPPER_ID);
		if (shipperIdin == shipId) {
			return setShipperAddress(jsonObject);
		}
		return null;
	}

	/**
	 * 
	 * @param jsonObject
	 * @return
	 */
	private Shipper setShipperAddress(JSONObject jsonObject) {
		JSONObject jsonObjectAddress = new JSONObject(jsonObject.get(StampsConstant.ADDRESS).toString());
		Shipper shipper = new Shipper();
		ContactAddress contactaddress = new ContactAddress();
		contactaddress.setStateOrProvinceCode(jsonObjectAddress.getString(StampsConstant.STATE_CODE));
		contactaddress.setCountryCode(jsonObjectAddress.getString(StampsConstant.COUNTRY_CODE));
		contactaddress.setPostalCode(jsonObjectAddress.getString(StampsConstant.POSTAL_CODE));
		shipper.setContactaddress(contactaddress);
		return shipper;
	}

	/**
	 * 
	 * @param xml
	 * @param ship
	 * @param permaData
	 * @return
	 * @throws TransformerConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public String getTranformedGenericXml(Object xml, Shipper ship, Map<String, Object> permaData)
			throws TransformerConfigurationException, TransformerFactoryConfigurationError,
			ParserConfigurationException, SAXException, IOException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		Document document = null;
		document = appendNodes((String) xml, ship, permaData);
		DOMSource source = new DOMSource(document);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {

		}
		return writer.toString();
	}

	/**
	 * this is to get the currentDate in string format to process the current
	 * date in Stamps append
	 * 
	 * @return string form of date ~(YYYY-MM-DD)
	 */
	private static String getCurrentDateString() {

		org.joda.time.DateTime dateTime_Utc = new DateTime(DateTimeZone.UTC);
		int monthUTC = dateTime_Utc.getMonthOfYear();
		String month;
		switch (monthUTC) {
		case 1:
			month = "01";
			break;
		case 2:
			month = "02";
			break;
		case 3:
			month = "03";
			break;
		case 4:
			month = "04";
			break;
		case 5:
			month = "05";
			break;
		case 6:
			month = "06";
			break;
		case 7:
			month = "07";
			break;
		case 8:
			month = "08";
			break;
		case 9:
			month = "09";
			break;
		default:
			month = Integer.toString(monthUTC);
			break;
		}
		String dateToString = dateTime_Utc.getYear() + "-" + month + "-" + dateTime_Utc.getDayOfMonth();
		return dateToString;
	}// end of the method

	/**
	 * 
	 * @param xml
	 * @param ship
	 * @param permaData
	 * @return
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private Document appendNodes(String xml, Shipper ship, Map<String, Object> permaData)
			throws ParserConfigurationException, SAXException, IOException {

		log.debug("inside append nodes: " + ship.getContactaddress().getPostalCode() + "  the xml : " + xml);
		JSONObject jsonObjectStampsProperties = getStampsPropertiesfromPermastore(permaData);
		JSONObject jsonObjectStampsCredentials = getStampsCredentialsfromPermastore(permaData);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		Document document = documentBuilder.parse(stream);

		NodeList rootList = document.getElementsByTagName(StampsConstant.RATE_REQUEST);
		Node root = rootList.item(0);

		Element stampssecu = document.createElement(StampsConstant.STAMPS_SECURITY);
		root.appendChild(stampssecu);
		Element usertoken = document.createElement(StampsConstant.USER_NAME_TOKEN);
		stampssecu.appendChild(usertoken);
		Element user = document.createElement(StampsConstant.USER_NAME);
		usertoken.appendChild(user);
		Element pass = document.createElement(StampsConstant.PASSWORD);
		usertoken.appendChild(pass);
		Text userval = document.createTextNode(jsonObjectStampsCredentials.getString(StampsConstant.USER_NAME));
		user.appendChild(userval);
		Text passval = document.createTextNode(jsonObjectStampsCredentials.getString(StampsConstant.PASSWORD));
		pass.appendChild(passval);

		Element servicetoken = document.createElement(StampsConstant.SERVICE_ACCESS_TOKEN);
		stampssecu.appendChild(servicetoken);
		Element accesslic = document.createElement(StampsConstant.ACCESS_LICENSE_NUMBER);
		servicetoken.appendChild(accesslic);
		Text accessval = document
				.createTextNode(jsonObjectStampsCredentials.getString(StampsConstant.ACCESS_LICENSE_NUMBER));
		accesslic.appendChild(accessval);

		Element pickuptype = document.createElement(StampsConstant.PICKUP_TYPE);
		root.appendChild(pickuptype);
		Element pickupcode = document.createElement(StampsConstant.PICKUP_TYPE_CODE);
		pickuptype.appendChild(pickupcode);
		Text pickupval = document
				.createTextNode(jsonObjectStampsProperties.getString(StampsConstant.PICKUP_TYPE_CODE_RATE));
		pickupcode.appendChild(pickupval);

		/*Element packingtype = document.createElement(StampsConstant.PACKING_TYPE);
		root.appendChild(packingtype);
		Element packingtypecode = document.createElement(StampsConstant.PACKING_TYPE_CODE);
		packingtype.appendChild(packingtypecode);
		Text packingtypesval = document
				.createTextNode(jsonObjectStampsProperties.getString(StampsConstant.PACKING_TYPE_CODE_RATE));
		packingtypecode.appendChild(packingtypesval);*/

		Element servicetype = document.createElement("ServiceType");
		root.appendChild(servicetype);
		Element servicetypecode = document.createElement("ServiceTypeCode");
		servicetype.appendChild(servicetypecode);
		Text servicetypesval = document.createTextNode(jsonObjectStampsProperties.getString("ServiceType"));
		servicetypecode.appendChild(servicetypesval);

		Element currentDate = document.createElement("CurrentDate");
		root.appendChild(currentDate);
		Element currentDatecode = document.createElement("CurrentDateYYYYMMDD");
		currentDate.appendChild(currentDatecode);
		Text currentDateval = document.createTextNode(getCurrentDateString());
		currentDatecode.appendChild(currentDateval);

		Element nonMachinable = document.createElement("NonMachinable");
		root.appendChild(nonMachinable);
		Element nonMachinablecode = document.createElement("NonMachinableCode");
		nonMachinable.appendChild(nonMachinablecode);
		Text nonMachinablecodesval = document.createTextNode(jsonObjectStampsProperties.getString("NonMachinable"));
		nonMachinablecode.appendChild(nonMachinablecodesval);

		Element rectangularShaped = document.createElement("RectangularShaped");
		root.appendChild(rectangularShaped);
		Element rectangularShapedcode = document.createElement("RectangularShapedCode");
		rectangularShaped.appendChild(rectangularShapedcode);
		Text rectangularShapedval = document.createTextNode(jsonObjectStampsProperties.getString("RectangularShaped"));
		rectangularShapedcode.appendChild(rectangularShapedval);

		Element isIntraBMC = document.createElement("IsIntraBMC");
		root.appendChild(isIntraBMC);
		Element isIntraBMCcode = document.createElement("IsIntraBMCCode");
		isIntraBMC.appendChild(isIntraBMCcode);
		Text isIntraBMCval = document.createTextNode(jsonObjectStampsProperties.getString("IsIntraBMC"));
		isIntraBMCcode.appendChild(isIntraBMCval);

		Element zone = document.createElement("Zone");
		root.appendChild(zone);
		Element zonecode = document.createElement("ZoneCode");
		zone.appendChild(zonecode);
		Text zoneval = document.createTextNode(jsonObjectStampsProperties.getString("Zone"));
		zonecode.appendChild(zoneval);

		Element rateCategory = document.createElement("RateCategory");
		root.appendChild(rateCategory);
		Element rateCategorycode = document.createElement("RateCategoryCode");
		rateCategory.appendChild(rateCategorycode);
		Text rateCategoryval = document.createTextNode(jsonObjectStampsProperties.getString("RateCategory"));
		rateCategorycode.appendChild(rateCategoryval);

		Element cubicPricing = document.createElement("CubicPricing");
		root.appendChild(cubicPricing);
		Element cubicPricingcode = document.createElement("CubicPricingCode");
		cubicPricing.appendChild(cubicPricingcode);
		Text cubicPricingval = document.createTextNode(jsonObjectStampsProperties.getString("CubicPricing"));
		cubicPricingcode.appendChild(cubicPricingval);

		// Shipper details setting
		Element shipperDetails = document.createElement(StampsConstant.SHIPPER_DETAILS);
		root.appendChild(shipperDetails);
		Element contAdd = document.createElement(StampsConstant.CONTACT_ADDRESS);
		shipperDetails.appendChild(contAdd);

		Element stateOrProvinceCode = document.createElement(StampsConstant.STATE);
		contAdd.appendChild(stateOrProvinceCode);
		Element postalCode = document.createElement(StampsConstant.POSTAL_CODE);
		contAdd.appendChild(postalCode);
		Element countryCode = document.createElement(StampsConstant.COUNTRY_CODE);
		contAdd.appendChild(countryCode);

		Text stateVal = document.createTextNode(ship.getContactaddress().getStateOrProvinceCode());
		stateOrProvinceCode.appendChild(stateVal);
		Text post = document.createTextNode(ship.getContactaddress().getPostalCode());
		postalCode.appendChild(post);
		Text cntry = document.createTextNode(ship.getContactaddress().getCountryCode());
		countryCode.appendChild(cntry);

		log.debug("inside append node: " + document);

		return document;
	}
	
	/**
	 * this is to rename the available lists from document-Stamps in to the response
	 * @param jsonArrayIn
	 * @return jsonarray-replaced with usps serviceTypes
	 */
	public JSONArray replaceStampsServiceTypeNames(JSONArray jsonArrayIn) {
		String jsonArrayString = jsonArrayIn.toString();
		JSONArray jsonArray = new JSONArray(jsonArrayString);
		String[] name = { "USPS First-Class Mail", "USPS Media Mail", "USPS Parcel Post", "USPS Priority Mail",
				"USPS Priority Mail Express", "USPS Priority Mail Express International",
				"USPS Priority Mail International", "USPS First Class Mail International", "USPS Parcel Select Ground", "USPS Library Mail" };
		String[] rawtype = { "US-FC", "US-MM", "US-PP", "US-PM", "US-XM", "US-EMI", "US-PMI", "US-FCI", "US-PS",
				"US-LM" };
		JSONArray jsonArray2 = new JSONArray();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			Object serViceType = jsonObject.get("ServiceType");//selects the service type from the input jsonArray
			try {
				String val = serViceType.toString();
					int j;
					for (j = 0; j < rawtype.length; j++) {
						if (val.equals(rawtype[j])) {
							jsonObject.put("ServiceType", name[j]);//compares and if matches replaces the code with the service name
						} 
					}
			} catch (Exception e) {
				jsonObject = (JSONObject) jsonArray.get(i);//if not available sets the same json object indexed with the default available serviceType
			}
			jsonArray2.put(jsonObject);
		}
		return jsonArray2;
	}

}
