package com.getusroi.wms20.parcel.parcelservice.generic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.getusroi.wms20.parcel.parcelservice.exception.ParcelServiceException;

/**
 * @author bizruntime Class to aid the Parcel Execution route
 */
public class ParcelserviceHelperRate implements AggregationStrategy {
	final static Logger logger = LoggerFactory.getLogger(ParcelserviceHelperRate.class);
	private final String XML_KEY_RATEREPLY = "RateReply";
	private final String XML_KEY_RATEREPLYDETAILS = "RateReplyDetails";
	private final String XML_KEY_PACKAGERATEDETAIL = "PackageRateDetail";
	private final String XML_KEY_TOTALCHARGE = "TotalNetChargeWithDutiesAndTaxes";
	private final String XPATH_EXPRESSION_CARRIER = "RateRequest/RateRequestInfo/ServiceTypes/Carrier/text()";
	private final String CARRIERS = "Carriers";
	private final String XPATH_EXPRESSION_RATEREQUEST_CODE = "RateRequest/RateRequestInfo/RateRequestType/RateRequestCode/text()";
	private final String RATE_REQUEST_CODE = "RateRequestCode";
	private final String XPATH_EXPRESSION_REQUESTARRIVE_DATE = "RateRequest/RateRequestInfo/RateRequestType/RequestArriveDate/text()";
	private final String REQUEST_ARRIVE_DATE = "RequestArriveDate";
	private final String XPATH_EXPRESSION_CARRIERSERVICE = "RateRequest/RateRequestInfo/ServiceTypes/CarrierServices/text()";
	private final String CARRIERSERVICES = "CarrierServices";
	private final String XPATH_EXPRESSION_WAY = "RateRequest/RateRequestInfo/ServiceTypes/Way/text()";
	private final String WAY = "Way";
	private final String XPATH_EXPRESSION_SERVICETYPE = "RateRequest/RateRequestInfo/ServiceTypes/ServiceType/text()";
	private final String SERVICE_TYPE = "ServiceType";
	private final String All_SERVICES_WAYS = "All";
	private final String SERVICE_KEY = "ServiceType";
	private final String SERVICE_KEY_HEADER = "servicetype";

	/**
	 * reads the xml file from the local system and converts to string, aswell
	 * as sets the RateRequestInfo from xml to Exchange~ headers
	 * 
	 * @param fileName
	 * @param exchange
	 * @throws ParcelServiceException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws XPathExpressionException
	 */
	public void readGenericFile(Exchange exchange) throws ParcelServiceException {
		String bodyIn = exchange.getIn().getBody(String.class);
		JSONObject jsonObjectin = new JSONObject(bodyIn);
		JSONObject jsonObject = new JSONObject(jsonObjectin.toString());
		JSONArray jsonArray = new JSONArray(jsonObject.get("data").toString());
		try {
			jsonArray = returndataforxmlUrl(jsonObject);
		} catch (Exception e) {
			jsonArray = new JSONArray(jsonObject.get("data").toString());
		}
		try {
			checkandReturnTheneededBody(jsonArray, exchange);
		} catch (XPathExpressionException | IOException | ParserConfigurationException e) {
			throw new ParcelServiceException("Unable to build the document: ", e);
		}
	}// ..end of the method

	/**
	 * Setting the body again to reconfirm the json response
	 * 
	 * @param exchange
	 * @return exchange body in String
	 */// #TODO have to remove it one after testing
	public String setResp(Exchange exchange) {

		String body = exchange.getIn().getBody(String.class);

		logger.debug("body s : " + body);

		exchange.getIn().setBody(body);

		return body;
	}// ..end of method

	/**
	 * the second try on the aggregation with a slight logical change
	 */// #TODO add javadocs once tested
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

		Object newBody = newExchange.getIn().getBody();
		ArrayList<Object> list = null;
		if (oldExchange == null) {
			list = new ArrayList<Object>();
			list.add(newBody);// ...stores each xml reply to list
			newExchange.getIn().setBody(list);
			return newExchange;
		} else {
			list = oldExchange.getIn().getBody(ArrayList.class);
			list.add(newBody);// ...stores each xml reply to list
			return oldExchange;
		}
	}// ..end of the method

	/**
	 * get the json objects one by one from the exchange
	 * 
	 * @param exchange
	 */
	public void getListofJsonDatafromExchange(Exchange exchange) {

		ArrayList<String> listOfJson = exchange.getIn().getBody(ArrayList.class);
		String jsonFedEX = convertXmlToJson(listOfJson.get(0));
		String jsonUps = convertXmlToJson(listOfJson.get(1));
		String jsonStamps = convertXmlToJson(listOfJson.get(2));

		// convert the jsonString in to json object
		JSONObject job1 = new JSONObject(jsonFedEX);
		JSONObject job2 = new JSONObject(jsonUps);
		JSONObject job3 = new JSONObject(jsonStamps);
		JSONObject value = new JSONObject();
		value.put("TotalNetChargeWithDutiesAndTaxes", 0.0D);

		JSONArray jsonArray1 = returnJsonArrayDataFirst(job1, value);
		JSONArray jsonArray2 = returnJsonArrayDataSecond(job2, value);
		JSONArray jsonArray3 = returnJsonArrayDataThird(job3, value);
		// ...to concatenate the incoming array
		JSONArray arrayResult = concatArray(jsonArray1, jsonArray2, jsonArray3);
		JSONArray ratesArray = sortRateBasedOnTotNetCharge(arrayResult);

		Map<String, Object> getExchangeHeaders = exchange.getIn().getHeaders();
		Map<String, Object> rrInfocarriersInHeader = (Map<String, Object>) getExchangeHeaders
				.get("RateRequestInfo_Carrier");
		ArrayList<String> reqstd_carriers = (ArrayList<String>) rrInfocarriersInHeader.get("Carriers");
		Map<String, String> way = (Map<String, String>) getExchangeHeaders.get(WAY);
		String ways = way.get(WAY);
		String carrier = "";
		Map<String, String> way_serviceType = (Map<String, String>) getExchangeHeaders.get(SERVICE_KEY_HEADER);
		String serviceType = way_serviceType.get(SERVICE_KEY);
		Map<String, String> rrCode_map = (Map<String, String>) getExchangeHeaders
				.get("RateRequestInfo_RateRequestCode");
		String rrCode = rrCode_map.get("RateRequestCode");
		Map<String, String> rrArriveDate_map = (Map<String, String>) getExchangeHeaders.get("RequestArriveDate");
		redirectForRateRequest(rrCode, rrArriveDate_map, ratesArray, ways, serviceType, carrier, exchange);

	}// ...end of the method

	/**
	 * 
	 * @param jsonArray
	 * @param exchange
	 * @throws IOException
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws ParcelServiceException
	 */
	private void checkandReturnTheneededBody(JSONArray jsonArray, Exchange exchange)
			throws IOException, XPathExpressionException, ParserConfigurationException, ParcelServiceException {
		try {
			String xml = XML.toString(jsonArray.get(0));
			logger.debug("The xml before validation: " + xml);
			InputStream streamedXml = new ByteArrayInputStream(xml.getBytes());
			SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source source = new StreamSource(streamedXml);
			Schema schema = schemaFactory.newSchema(new File(
					"/home/vivek/Downloads/ProjectinProgress/roi-parcelservice/src/main/resources/FEDEX_RATE_ENUMERATION.xsd"));
			Validator validator = schema.newValidator();
			validator.validate(source);

			exchange.getIn().setHeader(SERVICE_TYPE, getRateRequestInfoAttributesServiceType(xml.toString()));
			exchange.getIn().setBody(xml.toString());
		} catch (SAXException saxException) {
			exchange.getIn().setBody("{" + "\"ServiceRequest\":\"Fault\"," + "\"Message\":" + saxException + "" + "}");
			throw new ParcelServiceException("The validation of input xml rates gone wrong: ", saxException);
		}

	}

	/**
	 * this is a tempory solution from individual feature to parse the xml which
	 * is requested from the restClient
	 * 
	 * @param jsonObject
	 * @return
	 */
	private JSONArray returndataforxmlUrl(JSONObject jsonObject) {
		JSONArray jsonArraydescfind = (JSONArray) jsonObject.get("data");
		JSONObject jsonObjectdesc = (JSONObject) jsonArraydescfind.get(0);
		JSONArray jsonArray = null;
		JSONObject jsonObject3 = jsonObjectdesc.getJSONObject("RateRequest");
		JSONObject jsonObject4 = jsonObject3.getJSONObject("Shipper");
		JSONObject jsonObject5 = jsonObject3.getJSONObject("ShipToAddress");
		String desc2 = jsonObject4.getString("description");
		String desc3 = jsonObject5.getString("description");
		String desc = jsonObjectdesc.get("description").toString();
		if (desc instanceof String || desc2 instanceof String || desc3 instanceof String) {
			jsonObjectdesc.remove("description");
			jsonObject4.remove("description");
			jsonObject5.remove("description");
			jsonArray = new JSONArray(jsonObject.get("data").toString());
		}
		return jsonArray;
	}

	/**
	 * returns the carriers
	 * 
	 * @param xmlInput
	 * @return carriers from the xml input
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	private Map<String, Object> getRateRequestInfoAttributesCarriers(String xmlInput)
			throws SAXException, ParserConfigurationException, XPathExpressionException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document xmlDocument = null;
		try {
			xmlDocument = builder.parse(new ByteArrayInputStream(xmlInput.getBytes()));
		} catch (IOException e) {
			// #TODO throw exception, since generic exception is not written,
			// available FedEx/Ups Exceptions
			logger.error("Parsing the document executed error, to get the values to set header: " + e);
		}

		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = XPATH_EXPRESSION_CARRIER;
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
		ArrayList<String> carriers = new ArrayList<>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			carriers.add(nodeList.item(i).getTextContent());
		}
		Map<String, Object> attributeMap = new HashMap<>();
		// #TODO if multiple tags, put list into map
		attributeMap.put(CARRIERS, carriers);
		return attributeMap;
	}// end of the method

	/**
	 * to get the rateRequestCodes
	 * 
	 * @param xmlInput
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	private Map<String, Object> getRateRequestInfoAttributesRateRequestCode(String xmlInput)
			throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document xmlDocument = builder.parse(new ByteArrayInputStream(xmlInput.getBytes()));

		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = XPATH_EXPRESSION_RATEREQUEST_CODE;
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
		ArrayList<String> rateRequestcode = new ArrayList<>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			rateRequestcode.add(nodeList.item(i).getTextContent());
		}
		Map<String, Object> attributeMap = new HashMap<>();
		attributeMap.put(RATE_REQUEST_CODE, rateRequestcode.get(0));
		return attributeMap;
	}// end of the method

	/**
	 * gets the RequestArrive-date from the input xml
	 * 
	 * @param xmlInput
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	private Map<String, Object> getRateRequestInfoAttributesRequestArriveDate(String xmlInput)
			throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document xmlDocument = builder.parse(new ByteArrayInputStream(xmlInput.getBytes()));

		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = XPATH_EXPRESSION_REQUESTARRIVE_DATE;
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
		ArrayList<String> requstArriveDate = new ArrayList<>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			requstArriveDate.add(nodeList.item(i).getTextContent());
		}
		Map<String, Object> attributeMap = new HashMap<>();
		// #TODO if multiple tags, put list into map
		attributeMap.put(REQUEST_ARRIVE_DATE, requstArriveDate.get(0));
		return attributeMap;
	}// end of the method

	/**
	 * to get CarrierServices
	 * 
	 * @param xmlInput
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	private Map<String, Object> getRateRequestInfoAttributesCarrierService(String xmlInput)
			throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document xmlDocument = builder.parse(new ByteArrayInputStream(xmlInput.getBytes()));

		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = XPATH_EXPRESSION_CARRIERSERVICE;
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
		ArrayList<String> carrierservices = new ArrayList<>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			carrierservices.add(nodeList.item(i).getTextContent());
		}
		Map<String, Object> attributeMap = new HashMap<>();
		attributeMap.put(CARRIERSERVICES, carrierservices.get(0));
		return attributeMap;
	}// end of the method

	/**
	 * to get the ways
	 * 
	 * @param xmlInput
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	private Map<String, Object> getRateRequestInfoAttributesWay(String xmlInput)
			throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document xmlDocument = builder.parse(new ByteArrayInputStream(xmlInput.getBytes()));

		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = XPATH_EXPRESSION_WAY;
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
		ArrayList<String> way = new ArrayList<>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			way.add(nodeList.item(i).getTextContent());
		}
		Map<String, Object> attributeMap = new HashMap<>();
		attributeMap.put(WAY, way.get(0));
		return attributeMap;
	}// end of the method

	/**
	 * to get the serviceTypes
	 * 
	 * @param xmlInput
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	private Map<String, Object> getRateRequestInfoAttributesServiceType(String xmlInput)
			throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document xmlDocument = builder.parse(new ByteArrayInputStream(xmlInput.getBytes()));

		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = XPATH_EXPRESSION_SERVICETYPE;
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
		ArrayList<String> serviceTYpe = new ArrayList<>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			serviceTYpe.add(nodeList.item(i).getTextContent());
		}
		Map<String, Object> attributeMap = new HashMap<>();
		attributeMap.put(SERVICE_TYPE, serviceTYpe.get(0));
		logger.debug("the map attribute: " + attributeMap);
		return attributeMap;
	}// end of the method

	/**
	 * 
	 * @param job1
	 * @param value
	 * @return
	 */
	private JSONArray returnJsonArrayDataFirst(JSONObject job1, JSONObject value) {
		JSONArray jsonArray1 = null;
		try {
			if (job1.has(XML_KEY_RATEREPLY)) {
				JSONObject jsonObject = (JSONObject) (job1.getJSONObject(XML_KEY_RATEREPLY));
				jsonArray1 = new JSONArray(jsonObject.get(XML_KEY_RATEREPLYDETAILS).toString());
			} else {
				throw new ParcelServiceException("FedEx-Service Unavailable");
			}
		} catch (Exception e) {
			logger.error("FirstError: " + e + " the jsonobj" + job1);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ServiceFault", "FedEX-ServiceNotAvailable-Check with Admin");
			jsonObject.put("PackageRateDetail", value);
			jsonObject.put("ServiceType", "NA");
			jsonArray1 = new JSONArray();
			jsonArray1.put(jsonObject);
		}
		return jsonArray1;
	}

	/**
	 * 
	 * @param job2
	 * @param value
	 * @return
	 */
	private JSONArray returnJsonArrayDataSecond(JSONObject job2, JSONObject value) {
		JSONArray jsonArray2 = null;
		try {
			if (job2.has(XML_KEY_RATEREPLY)) {
				JSONObject jsonObject = (JSONObject) (job2.getJSONObject(XML_KEY_RATEREPLY));
				jsonArray2 = new JSONArray(jsonObject.get(XML_KEY_RATEREPLYDETAILS).toString());
			} else {
				throw new ParcelServiceException("UPS-Service Unavailable");
			}
		} catch (Exception e) {
			logger.error("SecondError: " + e + " the jsonobj" + job2);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ServiceFault", "UPS-ServiceNotAvailable-Check with Admin");
			jsonObject.put("PackageRateDetail", value);
			jsonObject.put("ServiceType", "NA");
			jsonArray2 = new JSONArray();
			jsonArray2.put(jsonObject);
		}
		return jsonArray2;
	}

	/**
	 * 
	 * @param job3
	 * @param value
	 * @return
	 */
	private JSONArray returnJsonArrayDataThird(JSONObject job3, JSONObject value) {
		JSONArray jsonArray3 = null;
		try {
			if (job3.has(XML_KEY_RATEREPLY)) {
				JSONObject jsonObject = (JSONObject) (job3.getJSONObject(XML_KEY_RATEREPLY));
				jsonArray3 = new JSONArray(jsonObject.get(XML_KEY_RATEREPLYDETAILS).toString());
			} else {
				throw new ParcelServiceException("Stamps-Service Unavailable");
			}
		} catch (Exception e) {
			logger.error("ThirdError: " + e + " the jsonobj" + job3);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ServiceFault", "Stamps-ServiceNotAvailable-Check with Admin");
			jsonObject.put("PackageRateDetail", value);
			jsonObject.put("ServiceType", "NA");
			jsonArray3 = new JSONArray();
			jsonArray3.put(jsonObject);
		}
		return jsonArray3;
	}

	/**
	 * the method finally sets the response body according to the requested
	 * RateRequestCode
	 * 
	 * @param rrCode
	 * @param rrArriveDate_map
	 * @param ratesArray
	 * @param ways
	 * @param serviceType
	 * @param carrier
	 * @param exchange
	 */
	private void redirectForRateRequest(String rrCode, Map<String, String> rrArriveDate_map, JSONArray ratesArray,
			String ways, String serviceType, String carrier, Exchange exchange) {
		if (rrCode.equals("Least-Cost-InWindow")) {
			String rrArriveDate = rrArriveDate_map.get("RequestArriveDate");
			JSONArray jsonArray = filterRates(ratesArray, carrier, ways, serviceType, rrArriveDate);
			JSONArray jsonArrayFinal = replaceServiceType(jsonArray);
			JSONArray jsF = replacePounds(jsonArrayFinal);
			String body1 = jsF.toString();
			exchange.getIn().setBody(body1);
		} else if (rrCode.equals("Least-Cost")) {
			JSONArray jsonArray = filterRates(ratesArray, carrier, ways, serviceType, "");
			JSONArray jsonArrayFinal = replaceServiceType(jsonArray);
			JSONArray jsF = replacePounds(jsonArrayFinal);
			String body1 = jsF.toString();
			exchange.getIn().setBody(body1);
		}

	}// ..end of method

	/**
	 * the new concatenation
	 * 
	 * @param arrs
	 * @return json array
	 * @throws JSONException
	 */
	private JSONArray concatArray(JSONArray... arrs) throws JSONException {
		JSONArray result = new JSONArray();
		for (JSONArray arr : arrs) {
			for (int i = 0; i < arr.length(); i++) {
				result.put(arr.get(i));
			}
		}
		return result;
	}

	/**
	 * this is to replace the Units of weight from LB to LBS
	 * 
	 * @param jsonArray
	 * @return jsonArray
	 */
	private JSONArray replacePounds(JSONArray jsonArray) {
		JSONArray array = new JSONArray();
		for (int i = 0; i < jsonArray.length(); i++) {

			JSONObject jsonObject = jsonArray.getJSONObject(i);
			try {
				JSONObject jsonObject2 = jsonObject.getJSONObject("PackageRateDetail");
				JSONObject jsonObject3 = jsonObject2.getJSONObject("TotalBillingWeight");
				JSONObject jsonObject4 = jsonObject.getJSONObject("ShipmentRateDetail");
				JSONObject jsonObject5 = jsonObject4.getJSONObject("TotalBillingWeight");
				if (jsonObject3.get("Units").equals("LB") || jsonObject5.get("Units").equals("LB")) {
					jsonObject5.put("Units", "LBS");
					jsonObject3.put("Units", "LBS");
				}
				array.put(jsonObject);
			} catch (Exception e) {
				logger.error("Attributes missing because of service unavailability: " + e);
				array.put(jsonObject);
			}
		}
		return array;
	}// ..end of the method

	/**
	 * this is to replace the ups service type Values from integer to respective
	 * service names
	 * 
	 * @param jsonArrayIn
	 * @return json array
	 */
	private JSONArray replaceServiceType(JSONArray jsonArrayIn) {
		String jsonArrayString = jsonArrayIn.toString();
		JSONArray jsonArray = new JSONArray(jsonArrayString);

		String[] name = { "UPS Standard", "UPS Ground", "UPS 3 Day Select", "UPS 2nd Day Air", "UPS 2nd Day Air AM",
				"UPS Next Day Air Saver", "UPS Next Day Air", "UPS Next Day Air Early A.M", "UPS Worldwide Express",
				"UPS Worldwide Express Plus", "UPS Worldwide Expedited", "UPS World Wide Saver" };
		int[] inttype = { 11, 3, 12, 2, 59, 13, 1, 14, 7, 54, 8, 65 };
		JSONArray jsonArray2 = new JSONArray();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			Object serViceType = jsonObject.get(SERVICE_TYPE);
			try {
				int val = Integer.parseInt(serViceType.toString());
				/* if (val == (int) val) { */
				int j;
				for (j = 0; j < inttype.length; j++) {
					if (val == inttype[j]) {
						jsonObject.put(SERVICE_TYPE, name[j]);
					} else {
						jsonObject = (JSONObject) jsonArray.get(i);
					}
				}
				// }

			} catch (Exception e) {
				jsonObject = (JSONObject) jsonArray.get(i);
			}
			jsonArray2.put(jsonObject);
		}

		return jsonArray2;
	}

	/**
	 * method to convert xml to string, similar conversion as the Camel xmlJson
	 * dataformat
	 * 
	 * @param xml
	 * @return jsonString
	 */
	private String convertXmlToJson(String xml) {
		final int INDENT_FACTOR = 3;
		JSONObject xmlJSONObj = XML.toJSONObject(xml);
		String jsonPrettyPrintString = xmlJSONObj.toString(INDENT_FACTOR);
		return jsonPrettyPrintString;
	}// ..end of the method

	/**
	 * this is to sort the array of json converted from the responses
	 * 
	 * @param unsortedJsonArray
	 * @return sortedJsonArray
	 */
	private JSONArray sortRateBasedOnTotNetCharge(JSONArray unsortedJsonArray) {
		JSONArray sortedJsonArray = new JSONArray();
		List<JSONObject> unsortedJsonInList = new ArrayList<JSONObject>();
		for (int i = 0; i < unsortedJsonArray.length(); i++) {
			unsortedJsonInList.add(unsortedJsonArray.getJSONObject(i));
		}
		if (unsortedJsonInList != null || !(unsortedJsonInList.isEmpty())) {
			// logic to sort json list based on total_rate with taxndDuties
			Collections.sort(unsortedJsonInList, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject oldobj, JSONObject newobj) {
					Double valoldobj = null;
					Double valnewobj = null;
					valoldobj = (Double) oldobj.getJSONObject(XML_KEY_PACKAGERATEDETAIL).get(XML_KEY_TOTALCHARGE);
					valnewobj = (Double) newobj.getJSONObject(XML_KEY_PACKAGERATEDETAIL).get(XML_KEY_TOTALCHARGE);
					return valoldobj.compareTo(valnewobj);
				}
			});
			// storing - sorted list into sorted JSON array
			for (int i = 0; i < unsortedJsonArray.length(); i++) {
				sortedJsonArray.put(unsortedJsonInList.get(i));
			}
		} // end of if
		return sortedJsonArray;
	}// end of method

	/**
	 * this works for the combination (Way,ServiceType)~ (All,All),
	 * (All,2_DAY/3_DAY), (Ground/Freight/Air, All)
	 * 
	 * @param jsonArray
	 * @param carrier
	 * @param ways
	 * @param serviceType
	 * @return jsonArray according to the request
	 */
	private JSONArray filterRates(JSONArray jsonArray, String carrier, String ways, String serviceType,
			String requestArriveDate) {

		if ((serviceType instanceof String && serviceType != null && !serviceType.isEmpty() && !serviceType.equals(""))
				&& (serviceType instanceof String && serviceType != All_SERVICES_WAYS
						&& !serviceType.equals(All_SERVICES_WAYS))
				&& (ways instanceof String && ways != null && !ways.isEmpty() && !ways.equals(""))
				&& (ways instanceof String && ways != All_SERVICES_WAYS || ways.equals(All_SERVICES_WAYS))) {
			String[] service = null;
			switch (serviceType) {
			case "2_DAY":
				service = new String[] { "FEDEX_2_DAY", "FEDEX_2_DAY_AM", "FEDEX_2_DAY_FREIGHT", "02", "59", "NA" };
				break;

			case "3_DAY":
				service = new String[] { "FEDEX_3_DAY_FREIGHT", "12", "NA" };
				break;

			default:
				break;
			}

			return processFilter(service, jsonArray, requestArriveDate);
		} else if ((serviceType instanceof String && serviceType != null && !serviceType.isEmpty()
				&& !serviceType.equals("") && serviceType != All_SERVICES_WAYS
				&& !serviceType.equals(All_SERVICES_WAYS)) && (ways == null || ways.isEmpty() || ways.equals(""))) {
			String[] service = null;
			switch (serviceType) {
			case "2_DAY":
				service = new String[] { "FEDEX_2_DAY", "FEDEX_2_DAY_AM", "FEDEX_2_DAY_FREIGHT", "02", "59", "NA" };
				break;

			case "3_DAY":
				service = new String[] { "FEDEX_3_DAY_FREIGHT", "12", "NA" };
				break;

			default:
				break;
			}

			return processFilter(service, jsonArray, requestArriveDate);
		} else if (ways instanceof String && ways != null && !ways.isEmpty() && !ways.equals("")
				&& ways instanceof String && ways != All_SERVICES_WAYS && !ways.equals(All_SERVICES_WAYS)) {
			String[] service = null;
			switch (ways) {
			case "Ground":
				return jsonArrayDataWhenGround(service, jsonArray, requestArriveDate);
			case "Air":
				return jsonArrayDataWhenAir(service, jsonArray, requestArriveDate);
			case "Freight":
				return jsonArrayDataWhenFreight(service, jsonArray, requestArriveDate);
			default:
				break;
			}

		} else if (((carrier.equals("") || carrier.isEmpty() || carrier == null)
				&& (ways.equals(All_SERVICES_WAYS) || ways == All_SERVICES_WAYS)
				&& (serviceType == All_SERVICES_WAYS || serviceType.equals(All_SERVICES_WAYS)))) {

			return jsonArrayDataWhenWaysIsAll(jsonArray, requestArriveDate);
		}
		return null;
	}// end of the method

	/**
	 * 
	 * @param service
	 * @param jsonArray
	 * @param requestArriveDate
	 * @return
	 */
	private JSONArray jsonArrayDataWhenGround(String[] service, JSONArray jsonArray, String requestArriveDate) {

		service = new String[] { "FEDEX_GROUND", "03", "GROUND_HOME_DELIVERY", "NA" };
		JSONArray resultArray = processFilter(service, jsonArray, requestArriveDate);
		return !resultArray.isNull(0) ? resultArray : resultArray.put("For the request - Service Unavailable");
	}

	/**
	 * 
	 * @param service
	 * @param jsonArray
	 * @param requestArriveDate
	 * @return
	 */
	private JSONArray jsonArrayDataWhenAir(String[] service, JSONArray jsonArray, String requestArriveDate) {
		service = new String[] { "FEDEX_EXPRESS_SAVER", "PRIORITY_OVERNIGHT", "13", "59", "02", "01", "14", "85", "86",
				"07", "54", "08", "INTERNATIONAL_ECONOMY", "INTERNATIONAL_ECONOMY_DISTRIBUTION", "INTERNATIONAL_FIRST",
				"INTERNATIONAL_PRIORITY", "INTERNATIONAL_PRIORITY_DISTRIBUTION", "NA" };
		JSONArray resultArray1 = processFilter(service, jsonArray, requestArriveDate);
		return !resultArray1.isNull(0) ? resultArray1 : resultArray1.put("For the request - Service Unavailable");

	}

	/**
	 * 
	 * @param service
	 * @param jsonArray
	 * @param requestArriveDate
	 * @return
	 */
	private JSONArray jsonArrayDataWhenFreight(String[] service, JSONArray jsonArray, String requestArriveDate) {
		service = new String[] { "FEDEX_EXPRESS_SAVER", "FEDEX_1_DAY_FREIGHT", "12", "01", "14", "65", "59", "85", "86",
				"FEDEX_FIRST_FREIGHT", "INTERNATIONAL_DISTRIBUTION_FREIGHT", "INTERNATIONAL_ECONOMY_FREIGHT",
				"INTERNATIONAL_PRIORITY_FREIGHT", "NA" };
		JSONArray resultArray2 = processFilter(service, jsonArray, requestArriveDate);
		return !resultArray2.isNull(0) ? resultArray2 : resultArray2.put("For the request - Service Unavailable");

	}

	/**
	 * 
	 * @param jsonArray
	 * @param requestArriveDate
	 * @return
	 */
	private JSONArray jsonArrayDataWhenWaysIsAll(JSONArray jsonArray, String requestArriveDate) {
		String[] service;
		service = new String[] { "FEDEX_GROUND", "03", "FEDEX_EXPRESS_SAVER", "FEDEX_2_DAY", "12", "02", "13", "01",
				"FIRST_OVERNIGHT", "14", "11", "59", "7", "8", "54", "65", "EUROPE_FIRST_INTERNATIONAL_PRIORITY",
				"FEDEX_1_DAY_FREIGHT", "FEDEX_2_DAY_AM", "FEDEX_2_DAY_FREIGHT", "FEDEX_3_DAY_FREIGHT",
				"FEDEX_FIRST_FREIGHT", "GROUND_HOME_DELIVERY", "INTERNATIONAL_DISTRIBUTION_FREIGHT",
				"INTERNATIONAL_ECONOMY", "INTERNATIONAL_ECONOMY_DISTRIBUTION", "INTERNATIONAL_ECONOMY_FREIGHT",
				"INTERNATIONAL_FIRST", "INTERNATIONAL_PRIORITY", "INTERNATIONAL_PRIORITY_DISTRIBUTION",
				"INTERNATIONAL_PRIORITY_FREIGHT", "PRIORITY_OVERNIGHT", "STANDARD_OVERNIGHT", "85", "86", "NA" };
		JSONArray resultArray = processFilter(service, jsonArray, requestArriveDate);
		return !resultArray.isNull(0) ? resultArray : resultArray.put("For the request - Service Unavailable");
	}

	/**
	 * to process the filtering from the aggregatedJsonArray
	 * 
	 * @param service
	 * @param jsonArray
	 * @return jsonArray Filtered
	 */
	private JSONArray processFilter(String[] service, JSONArray jsonArray, String rateRequestDate)
			throws JSONException {

		if (rateRequestDate != null && !rateRequestDate.isEmpty() && !rateRequestDate.equals("")) {
			JSONArray jsonArray2 = new JSONArray();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String serviceTypeFromJsonArray = jsonObject.get(SERVICE_KEY).toString();
				Object commitTimeStamoFromJsonArray = jsonObject.get("CommitTimestamp");

				if (commitTimeStamoFromJsonArray instanceof Integer) {
					org.joda.time.DateTime dateTime_Utcups = new DateTime(DateTimeZone.UTC)
							.plusDays(((Integer) commitTimeStamoFromJsonArray).intValue());
					commitTimeStamoFromJsonArray = dateTime_Utcups.toString();
				} else {
					commitTimeStamoFromJsonArray = commitTimeStamoFromJsonArray.toString();
				}
				String dateToCompare = returnDateToCompare(commitTimeStamoFromJsonArray);
				for (int j = 0; j < service.length; j++) {
					if (serviceTypeFromJsonArray.equals(service[j]) && dateToCompare.equals(rateRequestDate))
						jsonArray2.put(jsonObject);
				}
			}
			return jsonArray2;
		} else if (rateRequestDate == null || rateRequestDate.equals("") || rateRequestDate.isEmpty()) {
			// returns a jsonArray when requestedDate is null
			return returnJsonArraywhenRequestDateIsNull(jsonArray, service);
		}
		return jsonArray;

	}// end of the method

	/**
	 * 
	 * @param commitTimeStamoFromJsonArray
	 * @return
	 */
	private String returnDateToCompare(Object commitTimeStamoFromJsonArray) {

		DateTime date = null;
		String dateToCompare = null;
		try {
			org.joda.time.DateTime dateTime_Utc = new DateTime(commitTimeStamoFromJsonArray, DateTimeZone.UTC);
			date = dateTime_Utc.toDateTime(DateTimeZone.UTC);
			dateToCompare = date.getDayOfMonth() + "/" + date.getMonthOfYear() + "/" + date.getYear();
		} catch (Exception e) {
			date = new DateTime(DateTimeZone.UTC);
			dateToCompare = date.getDayOfMonth() + 1 + "/" + date.getMonthOfYear() + "/" + date.getYear();
		}
		return dateToCompare;
	}

	/**
	 * just an ait when requestDate is null, null is returned when requested for
	 * the Least_Cost
	 * 
	 * @param jsonArray
	 * @param service
	 * @return
	 */
	private JSONArray returnJsonArraywhenRequestDateIsNull(JSONArray jsonArray, String[] service) {

		JSONArray jsonArray2 = new JSONArray();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String serviceTypeFromJsonArray = jsonObject.get(SERVICE_KEY).toString();

			for (int j = 0; j < service.length; j++) {
				if (serviceTypeFromJsonArray.equals(service[j]))
					jsonArray2.put(jsonObject);
			}
		}
		return jsonArray2;
	}// ..end of the method
}
