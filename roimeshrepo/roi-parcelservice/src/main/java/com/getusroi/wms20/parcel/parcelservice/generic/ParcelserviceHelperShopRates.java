package com.getusroi.wms20.parcel.parcelservice.generic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

import com.eclipsesource.json.JsonArray;
import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.wms20.parcel.parcelservice.exception.ParcelServiceException;

/**
 * @author bizruntime Class to aid the Parcel Execution route
 */
public class ParcelserviceHelperShopRates extends AbstractROICamelJDBCBean implements AggregationStrategy{
	final static Logger logger = LoggerFactory.getLogger(ParcelserviceHelperShopRates.class);
	private final String XML_KEY_RATEREPLY = "RateReply";
	private final String XML_KEY_RATEREPLYDETAILS = "RateReplyDetails";
	private final String XML_KEY_PACKAGERATEDETAIL = "PackageRateDetail";
	private final String XML_KEY_TOTALCHARGE = "TotalNetChargeWithDutiesAndTaxes";
	private final String RATE_REQUEST_INFO_CARRIER = "RateRequestInfo_Carrier";
	private final String RATE_REQUEST_INFO_RR_CODE = "RateRequestInfo_RateRequestCode";
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
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws XPathExpressionException
	 */
	public void readGenericFile(Exchange exchange)
			throws XPathExpressionException, SAXException, ParserConfigurationException, IOException {
		String bodyIn = exchange.getIn().getBody(String.class);
		JSONObject jsonObjectin = new JSONObject(bodyIn);
		JSONObject jsonObject = new JSONObject(jsonObjectin.toString());
		JSONArray jsonArray = new JSONArray(jsonObject.get("data").toString());
		try {
			initiateTheJsonArrayData(jsonObject);
		} catch (Exception e) {
			jsonArray = new JSONArray(jsonObject.get("data").toString());
		}
		returnTheXmlDatabyValidating(jsonArray, exchange);

		// return xml.toString();
	}// ..end of the method

	/**
	 * the second try on the aggregation with a slight logical change
	 */
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
			logger.debug("in the aggreagatino: " + list);
			return oldExchange;
		}
	}// ..end of the method

	/**
	 * 
	 * @param jsonObject
	 * @return
	 */
	private JSONArray initiateTheJsonArrayData(JSONObject jsonObject) {
		JSONArray jsonArray = null;
		JSONArray jsonArraydescfind = (JSONArray) jsonObject.get("data");
		JSONObject jsonObjectdesc = (JSONObject) jsonArraydescfind.get(0);
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
	 * 
	 * @param jsonArray
	 * @param exchange
	 * @throws IOException
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 */
	private void returnTheXmlDatabyValidating(JSONArray jsonArray, Exchange exchange)
			throws IOException, XPathExpressionException, ParserConfigurationException {
		String xml = XML.toString(jsonArray.get(0));
		InputStream streamedXml = new ByteArrayInputStream(xml.getBytes());
		SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Source source = new StreamSource(streamedXml);
		Schema schema = null;
		try {
			// #TODO since the project is loaded from the FeatureInstaller, it
			// searches the xsd in its classpath, hence given the local path,
			// have to change.
			schema = schemaFactory.newSchema(new File(
					"/home/vivek/Downloads/ProjectinProgress/roi-parcelservice/src/main/resources/RawFedExRate.xsd"));
			Validator validator = schema.newValidator();
			validator.validate(source);
			// set header with RateRequestInfo values
			exchange.getIn().setHeader(RATE_REQUEST_INFO_CARRIER, getRateRequestInfoAttributesCarriers(xml.toString()));
			exchange.getIn().setHeader(RATE_REQUEST_INFO_RR_CODE,
					getRateRequestInfoAttributesRateRequestCode(xml.toString()));
			exchange.getIn().setHeader(WAY, getRateRequestInfoAttributesWay(xml.toString()));
			exchange.getIn().setHeader(SERVICE_TYPE, getRateRequestInfoAttributesServiceType(xml.toString()));
			exchange.getIn().setHeader(REQUEST_ARRIVE_DATE,
					getRateRequestInfoAttributesRequestArriveDate(xml.toString()));
			exchange.getIn().setBody(xml.toString());
		} catch (SAXException e) {
			logger.error("Xml Validation Failed: " + e);
			JSONObject error_jsonObject = new JSONObject();
			error_jsonObject.put("ServiceRequest", "Fault");
			error_jsonObject.put("Message", "Invalid request for the ParcelService");
			error_jsonObject.put("Error_message_detail", e);
			exchange.getIn().setBody(error_jsonObject);
		}
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
		return attributeMap;
	}// end of the method

	/**
	 * get the json objects one by one from the exchange
	 * 
	 * @param exchange
	 */
	public void getListofExchangeBody(Exchange exchange) {

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
		JSONArray jsonArray1 = gettheneededJsonData1(job1, value);
		JSONArray jsonArray2 = gettheneededJsonData2(job2, value);
		JSONArray jsonArray3 = gettheneededJsonData3(job3, value);
		JSONArray arrayResult = concatArray(jsonArray1, jsonArray2, jsonArray3);
		JSONArray ratesArray = sortRateBasedOnTotNetCharge(arrayResult);
		logger.debug("The sortRateBasedOnTotNetCharge array: " + ratesArray);
		Map<String, Object> getHeaderData = (Map<String, Object>) exchange.getIn().getHeaders();
		logger.debug("in the headers: " + getHeaderData);
		Map<String, Object> rrInfocarriersInHeader = (Map<String, Object>) getHeaderData.get("RateRequestInfo_Carrier");
		ArrayList<String> reqstd_carriers = (ArrayList<String>) rrInfocarriersInHeader.get("Carriers");
		Map<String, String> way = (Map<String, String>) getHeaderData.get(WAY);
		String ways = way.get(WAY);
		String carrier = "";
		Map<String, String> way_serviceType = (Map<String, String>) getHeaderData.get(SERVICE_KEY_HEADER);
		String serviceType = way_serviceType.get(SERVICE_KEY);
		Map<String, String> rrCode_map = (Map<String, String>) getHeaderData.get("RateRequestInfo_RateRequestCode");
		String rrCode = rrCode_map.get("RateRequestCode");
		Map<String, String> rrArriveDate_map = (Map<String, String>) getHeaderData.get("RequestArriveDate");

		routeFortheCondition(rrCode, rrArriveDate_map, ways, carrier, serviceType, ratesArray, exchange);

	}// ...end of the method

	/**
	 * 
	 * @param rrCode
	 * @param rrArriveDate_map
	 * @param ways
	 * @param carrier
	 * @param serviceType
	 * @param ratesArray
	 * @param exchange
	 */
	private void routeFortheCondition(String rrCode, Map<String, String> rrArriveDate_map, String ways, String carrier,
			String serviceType, JSONArray ratesArray, Exchange exchange) {
		if (rrCode.equals("Least-Cost-InWindow")) {
			String rrArriveDate = rrArriveDate_map.get("RequestArriveDate");
			JSONArray jsonArray = filterRates(ratesArray, carrier, ways, serviceType, rrArriveDate);
			logger.debug("The jsonArray Finally built in the leaseCostInwindow: " + jsonArray);
			try {
				JSONArray jsonArrayFinal = replaceUpsServiceTypeNames(jsonArray);
				JSONArray stampsSTreplaced = replaceStampsServiceTypeNames(jsonArrayFinal);
				JSONArray jsF = replacePounds(stampsSTreplaced);
				String body1 = jsF.toString();
				exchange.getIn().setBody(body1);
			} catch (JSONException jsonException) {
				logger.error("The is some exception in getting some data: " + jsonException);
				exchange.getIn().setBody(jsonArray);
			}
		} else if (rrCode.equals("Least-Cost")) {
			JSONArray jsonArray = filterRates(ratesArray, carrier, ways, serviceType, "");
			try {
				JSONArray jsonArrayFinal = replaceUpsServiceTypeNames(jsonArray);
				JSONArray stampsSTreplaced = replaceStampsServiceTypeNames(jsonArrayFinal);
				JSONArray jsF = replacePounds(stampsSTreplaced);
				String body1 = jsF.toString();
				exchange.getIn().setBody(body1);
			} catch (JSONException jsonException) {
				logger.error("The is some exception in getting some data: " + jsonException);
				exchange.getIn().setBody(jsonArray);
			}
		}

	}// ..end of the method

	/**
	 * 
	 * @param job3
	 * @param value
	 * @return
	 */
	private JSONArray gettheneededJsonData3(JSONObject job3, JSONObject value) {
		JSONArray jsonArray3 = new JSONArray();
		try {
			if (job3.has(XML_KEY_RATEREPLY)) {
				JSONObject jsonObject = (JSONObject) (job3.getJSONObject(XML_KEY_RATEREPLY));
				jsonArray3.put(jsonObject.get(XML_KEY_RATEREPLYDETAILS));
				// jsonArray3 = new
				// JSONArray(jsonObject.get(XML_KEY_RATEREPLYDETAILS).toString());
			} else {
				throw new ParcelServiceException("Stamps-Service Unavailable");
			}
		} catch (Exception e) {
			logger.error("No response from the Stamps service: : " + e + " the jsonobj" + job3);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ServiceFault", "Stamps-ServiceNotAvailable-Check with Admin");
			jsonObject.put("PackageRateDetail", value);
			jsonObject.put("ServiceType", "NA");
			jsonArray3 = new JSONArray();
			jsonArray3.put(jsonObject);
		}
		return jsonArray3;
	}// ..end of the method

	/**
	 * 
	 * @param job2
	 * @param value
	 * @return
	 */
	private JSONArray gettheneededJsonData2(JSONObject job2, JSONObject value) {
		JSONArray jsonArray2 = new JSONArray();
		try {
			if (job2.has(XML_KEY_RATEREPLY)) {
				JSONObject jsonObject = (JSONObject) (job2.getJSONObject(XML_KEY_RATEREPLY));
				// jsonArray2 = new
				// JSONArray(jsonObject.get(XML_KEY_RATEREPLYDETAILS).toString());
				jsonArray2.put(jsonObject.get(XML_KEY_RATEREPLYDETAILS));
			} else {
				throw new ParcelServiceException("UPS-Service Unavailable");
			}
		} catch (Exception e) {
			logger.error("No Response from the UPS service: " + e + " the jsonobj" + job2);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ServiceFault", "UPS-ServiceNotAvailable-Check with Admin");
			jsonObject.put("PackageRateDetail", value);
			jsonObject.put("ServiceType", "NA");
			jsonArray2 = new JSONArray();
			jsonArray2.put(jsonObject);

		}
		return jsonArray2;
	}// ..end of the method

	/**
	 * 
	 * @param job1
	 * @param value
	 * @return
	 */
	private JSONArray gettheneededJsonData1(JSONObject job1, JSONObject value) {
		JSONArray jsonArray1 = new JSONArray();
		try {

			if (job1.has(XML_KEY_RATEREPLY)) {
				JSONObject jsonObject = (JSONObject) (job1.getJSONObject(XML_KEY_RATEREPLY));
				// jsonArray1 = new
				// JSONArray(jsonObject.get(XML_KEY_RATEREPLYDETAILS).toString());
				jsonArray1.put(jsonObject.get(XML_KEY_RATEREPLYDETAILS));
			} else {
				throw new ParcelServiceException("FedEx-Service Unavailable");
			}
		} catch (Exception e) {
			logger.error("No Response from the FedEx Service: " + e + " the jsonobj" + job1);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ServiceFault", "FedEX-ServiceNotAvailable-Check with Admin");
			jsonObject.put("PackageRateDetail", value);
			jsonObject.put("ServiceType", "NA");
			jsonArray1 = new JSONArray();
			jsonArray1.put(jsonObject);
		}
		return jsonArray1;
	}// ..end of the method

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
	}// ..end of the method

	/**
	 * the three jsonArray been parsed and concatenated here
	 * 
	 * @param arr1
	 * @param arr2
	 * @param arr3
	 * @return concatenated jsonArray
	 * @throws JSONException
	 */
	private JSONArray concatArray(JSONArray arr1, JSONArray arr2, JSONArray arr3) throws JSONException {
		JSONArray result = new JSONArray();
		for (int i = 0; i < arr1.getJSONArray(0).length(); i++) {
			result.put(arr1.getJSONArray(0).get(i));
		}
		for (int i = 0; i < arr2.getJSONArray(0).length(); i++) {
			result.put(arr2.getJSONArray(0).get(i));
		}
		for (int i = 0; i < arr3.getJSONArray(0).length(); i++) {
			result.put(arr3.getJSONArray(0).get(i));
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
	private JSONArray replaceUpsServiceTypeNames(JSONArray jsonArrayIn) {

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

	}// ..end of the method

	/**
	 * this is to rename the available lists from document-Stamps in to the
	 * response
	 * 
	 * @param jsonArrayIn
	 * @return jsonarray-replaced with usps serviceTypes
	 */
	private JSONArray replaceStampsServiceTypeNames(JSONArray jsonArrayIn) {
		String jsonArrayString = jsonArrayIn.toString();
		JSONArray jsonArray = new JSONArray(jsonArrayString);
		String[] name = { "USPS First-Class Mail", "USPS Media Mail", "USPS Parcel Post", "USPS Priority Mail",
				"USPS Priority Mail Express", "USPS Priority Mail Express International",
				"USPS Priority Mail International", "USPS First Class Mail International", "USPS Parcel Select Ground",
				"USPS Library Mail" };
		String[] rawtype = { "US-FC", "US-MM", "US-PP", "US-PM", "US-XM", "US-EMI", "US-PMI", "US-FCI", "US-PS",
				"US-LM" };
		JSONArray jsonArray2 = new JSONArray();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			Object serViceType = jsonObject.get(SERVICE_TYPE);
			try {
				String val = serViceType.toString();
				int j;
				for (j = 0; j < rawtype.length; j++) {
					if (val.equals(rawtype[j])) {
						jsonObject.put(SERVICE_TYPE, name[j]);
					}
				}
			} catch (Exception e) {
				jsonObject = (JSONObject) jsonArray.get(i);
			}
			jsonArray2.put(jsonObject);
		}
		return jsonArray2;
	}// ..endo of the method

	/**
	 * Setting the body again to reconfirm the json response
	 * 
	 * @param exchange
	 * @return exchange body in String
	 */// #TODO have to remove it after testing all and see the UI Response
	public String setResp(Exchange exchange) {

		String body = exchange.getIn().getBody(String.class);

		logger.debug("body s : " + body);

		exchange.getIn().setBody(body);

		return body;
	}// ..end of the method

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
			String[] service;
			switch (ways) {
			case "Ground":
				return returnifGround(jsonArray, requestArriveDate);
			// break;

			case "Air":
				return returnifAir(jsonArray, requestArriveDate);
			// break;

			case "Freight":
				return returnifFrieght(jsonArray, requestArriveDate);
			default:
				break;
			}

		} else if (((carrier.equals("") || carrier.isEmpty() || carrier == null)
				&& (ways.equals(All_SERVICES_WAYS) || ways == All_SERVICES_WAYS)
				&& (serviceType == All_SERVICES_WAYS || serviceType.equals(All_SERVICES_WAYS)))) {

			return returnifAllofTheWays(jsonArray, requestArriveDate);
		}
		return null;
	}// end of the method

	/**
	 * 
	 * @param jsonArray
	 * @param requestArriveDate
	 * @return
	 */
	private JSONArray returnifGround(JSONArray jsonArray, String requestArriveDate) {
		String[] service;
		service = new String[] { "FEDEX_GROUND", "03", "GROUND_HOME_DELIVERY", "NA", "US-PS" };
		JSONArray resultArray = processFilter(service, jsonArray, requestArriveDate);
		JSONObject errorNAobj = new JSONObject();
		errorNAobj.put("ServiceNA", "For the Request Service not available");
		return !resultArray.isNull(0) ? resultArray : resultArray.put(errorNAobj);

	}// ..end of the method

	/**
	 * 
	 * @param jsonArray
	 * @param requestArriveDate
	 * @return
	 */
	private JSONArray returnifAir(JSONArray jsonArray, String requestArriveDate) {
		String[] service;
		service = new String[] { "FEDEX_EXPRESS_SAVER", "PRIORITY_OVERNIGHT", "13", "59", "02", "01", "14", "85", "86",
				"07", "54", "08", "INTERNATIONAL_ECONOMY", "INTERNATIONAL_ECONOMY_DISTRIBUTION", "INTERNATIONAL_FIRST",
				"INTERNATIONAL_PRIORITY", "INTERNATIONAL_PRIORITY_DISTRIBUTION", "NA", "US-FCI", "US-XM", "US-PM",
				"US-FC" };
		JSONArray resultArray1 = processFilter(service, jsonArray, requestArriveDate);
		JSONObject errorNAobj = new JSONObject();
		errorNAobj.put("ServiceNA", "For the Request Service not available");
		return !resultArray1.isNull(0) ? resultArray1 : resultArray1.put(errorNAobj);
	}// ..end of the method

	/**
	 * 
	 * @param jsonArray
	 * @param requestArriveDate
	 * @return
	 */
	private JSONArray returnifFrieght(JSONArray jsonArray, String requestArriveDate) {
		String[] service;
		service = new String[] { "FEDEX_EXPRESS_SAVER", "FEDEX_1_DAY_FREIGHT", "12", "01", "14", "65", "59", "85", "86",
				"FEDEX_FIRST_FREIGHT", "INTERNATIONAL_DISTRIBUTION_FREIGHT", "INTERNATIONAL_ECONOMY_FREIGHT",
				"INTERNATIONAL_PRIORITY_FREIGHT", "NA" };
		JSONArray resultArray2 = processFilter(service, jsonArray, requestArriveDate);
		JSONObject errorNAobj = new JSONObject();
		errorNAobj.put("ServiceNA", "For the Request Service not available");
		return !resultArray2.isNull(0) ? resultArray2 : resultArray2.put(errorNAobj);
	}// ..end of the method

	/**
	 * 
	 * @param jsonArray
	 * @param requestArriveDate
	 * @return
	 */
	private JSONArray returnifAllofTheWays(JSONArray jsonArray, String requestArriveDate) {
		String[] service;
		service = new String[] { "FEDEX_GROUND", "03", "FEDEX_EXPRESS_SAVER", "FEDEX_2_DAY", "12", "02", "13", "01",
				"FIRST_OVERNIGHT", "14", "11", "59", "7", "8", "54", "65", "EUROPE_FIRST_INTERNATIONAL_PRIORITY",
				"FEDEX_1_DAY_FREIGHT", "FEDEX_2_DAY_AM", "FEDEX_2_DAY_FREIGHT", "FEDEX_3_DAY_FREIGHT",
				"FEDEX_FIRST_FREIGHT", "GROUND_HOME_DELIVERY", "INTERNATIONAL_DISTRIBUTION_FREIGHT",
				"INTERNATIONAL_ECONOMY", "INTERNATIONAL_ECONOMY_DISTRIBUTION", "INTERNATIONAL_ECONOMY_FREIGHT",
				"INTERNATIONAL_FIRST", "INTERNATIONAL_PRIORITY", "INTERNATIONAL_PRIORITY_DISTRIBUTION",
				"INTERNATIONAL_PRIORITY_FREIGHT", "PRIORITY_OVERNIGHT", "STANDARD_OVERNIGHT", "85", "86", "NA", "US-FC",
				"US-MM", "US-PP", "US-PM", "US-XM", "US-EMI", "US-PMI", "US-FCI", "US-PS", "US-LM", "DHL-PE", "DHL-PG",
				"DHL-PPE", "DHL-PPG", "DHL-BPME", "DHL-BPMG", "DHL-MPE", "DHL-MPG", "AS-IPA", "AS-ISAL", "AS-EPKT",
				"DHL-PIPA", "DHL-PISAL", "GG-IPA", "GG-ISAL", "GG-EPKT", "IBC-IPA", "IBC-ISAL", "IBC-EPKT", "RRD-IPA",
				"RRD-ISAL", "RRD-EPKT", "AS-GNRC", "GG-GNRC", "RRD-GNRC" };
		JSONArray resultArray = processFilter(service, jsonArray, requestArriveDate);
		logger.debug("The resultArray for least with date: " + resultArray);
		JSONObject errorNAobj = new JSONObject();
		errorNAobj.put("ServiceNA", "For the Request Service not available");
		return !resultArray.isNull(0) ? resultArray : resultArray.put(errorNAobj);
	}// ..end of the method

	/**
	 * to process the filtering from the aggregatedJsonArray
	 * 
	 * @param service
	 * @param jsonArray
	 * @return jsonArray Filtered
	 */
	public JSONArray processFilter(String[] service, JSONArray jsonArray, String rateRequestDate)
			throws JSONException {
		logger.debug("The jsonArray incoming---" + jsonArray);
		if (rateRequestDate != null && !rateRequestDate.isEmpty() && !rateRequestDate.equals("")) {
			JSONArray jsonArray2 = new JSONArray();
			logger.debug("The jsonArrayLength: " + jsonArray.length());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				logger.debug("EachJsonObject: " + jsonObject);
				String serviceTypeFromJsonArray = jsonObject.get(SERVICE_KEY).toString();
				Object commitTimeStamoFromJsonArray = jsonObject.get("CommitTimestamp");
				System.out.println("EachValue FromJsonArray: "+commitTimeStamoFromJsonArray);
				String dateToCompare = returnDateToCompare(commitTimeStamoFromJsonArray);
				logger.debug("The service length: " + service.length);
				for (int j = 0; j < service.length; j++) {
					logger.debug("serviceTypeFromJsonArray: "+serviceTypeFromJsonArray+" service[j]: "+service[j]+" dateToCompare: "+dateToCompare+" rateRequestDate: "+rateRequestDate);
					if (serviceTypeFromJsonArray.equals(service[j]) && dateToCompare.equals(rateRequestDate))
						jsonArray2.put(jsonObject);
				}
			}
			logger.debug("The outComing jsonArray: " + jsonArray2);
			return jsonArray2;
		} else if (rateRequestDate == null || rateRequestDate.equals("") || rateRequestDate.isEmpty()) {
			return returnwhenrateRequestDateisNull(jsonArray, service);
		}
		return jsonArray;

	}// end of the method

	/**
	 * 
	 * @param jsonArray
	 * @param service
	 * @return
	 */
	private JSONArray returnwhenrateRequestDateisNull(JSONArray jsonArray, String[] service) {
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

	/**
	 * 
	 * @param commitTimeStamoFromJsonArray
	 * @return
	 */
	public String returnDateToCompare(Object commitTimeStamoFromJsonArray) {

		if (commitTimeStamoFromJsonArray instanceof Integer) {
			System.out.println("commitFrom jArray: "+commitTimeStamoFromJsonArray);
			org.joda.time.DateTime dateTime_Utcups = new DateTime(DateTimeZone.UTC)
					.plusDays(((Integer) commitTimeStamoFromJsonArray).intValue());
			commitTimeStamoFromJsonArray = dateTime_Utcups.toString();
			
		} else if(commitTimeStamoFromJsonArray instanceof Date){
			commitTimeStamoFromJsonArray = commitTimeStamoFromJsonArray.toString();
			
		}else{
			commitTimeStamoFromJsonArray = commitTimeStamoFromJsonArray.toString();
		}
		DateTime date = null;
		String dateToCompare = null;
	//	if(commitTimeStamoFromJsonArray instanceof String){
		try {
			org.joda.time.DateTime dateTime_Utc = new DateTime(commitTimeStamoFromJsonArray, DateTimeZone.UTC);
			date = dateTime_Utc.toDateTime(DateTimeZone.UTC);
			System.out.println("DateTime: "+date);
			dateToCompare = date.getDayOfMonth() + "/" + date.getMonthOfYear() + "/" + date.getYear();
		} catch (Exception e) {
			date = new DateTime(DateTimeZone.UTC);
			dateToCompare = date.getDayOfMonth() + 1 + "/" + date.getMonthOfYear() + "/" + date.getYear();
		}//}
		logger.debug("The date to be compared: " + dateToCompare);
		return dateToCompare;
	}// ..end of the method
	
	public static void main(String[] a){
		
		ParcelserviceHelperShopRates rates = new ParcelserviceHelperShopRates();
		JSONArray array = new JSONArray(rates.JsonAr());
		String[] service;
		service = new String[] { "FEDEX_GROUND", "03", "FEDEX_EXPRESS_SAVER", "FEDEX_2_DAY", "12", "02", "13", "01",
				"FIRST_OVERNIGHT", "14", "11", "59", "7", "8", "54", "65", "EUROPE_FIRST_INTERNATIONAL_PRIORITY",
				"FEDEX_1_DAY_FREIGHT", "FEDEX_2_DAY_AM", "FEDEX_2_DAY_FREIGHT", "FEDEX_3_DAY_FREIGHT",
				"FEDEX_FIRST_FREIGHT", "GROUND_HOME_DELIVERY", "INTERNATIONAL_DISTRIBUTION_FREIGHT",
				"INTERNATIONAL_ECONOMY", "INTERNATIONAL_ECONOMY_DISTRIBUTION", "INTERNATIONAL_ECONOMY_FREIGHT",
				"INTERNATIONAL_FIRST", "INTERNATIONAL_PRIORITY", "INTERNATIONAL_PRIORITY_DISTRIBUTION",
				"INTERNATIONAL_PRIORITY_FREIGHT", "PRIORITY_OVERNIGHT", "STANDARD_OVERNIGHT", "85", "86", "NA", "US-FC",
				"US-MM", "US-PP", "US-PM", "US-XM", "US-EMI", "US-PMI", "US-FCI", "US-PS", "US-LM", "DHL-PE", "DHL-PG",
				"DHL-PPE", "DHL-PPG", "DHL-BPME", "DHL-BPMG", "DHL-MPE", "DHL-MPG", "AS-IPA", "AS-ISAL", "AS-EPKT",
				"DHL-PIPA", "DHL-PISAL", "GG-IPA", "GG-ISAL", "GG-EPKT", "IBC-IPA", "IBC-ISAL", "IBC-EPKT", "RRD-IPA",
				"RRD-ISAL", "RRD-EPKT", "AS-GNRC", "GG-GNRC", "RRD-GNRC" };
		System.out.println("******"+rates.processFilter(service, array, "29/5/2016"));
		
	}
	
	public String JsonAr(){
		

String myvar = "[{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":0.34,\"TotalTaxes\":0,\"TotalBaseCharge\":0.34,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":3,\"PackageType\":\"Postcard\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-FC\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":0.34,\"TotalTaxes\":0,\"TotalBaseCharge\":0.34,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-05-28\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":2.48,\"TotalTaxes\":0,\"TotalBaseCharge\":2.48,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":\"2-8\",\"PackageType\":\"Large Envelope or Flat\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-LM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":2.48,\"TotalTaxes\":0,\"TotalBaseCharge\":2.48,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":2.48,\"TotalTaxes\":0,\"TotalBaseCharge\":2.48,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":\"2-8\",\"PackageType\":\"Thick Envelope\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-LM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":2.48,\"TotalTaxes\":0,\"TotalBaseCharge\":2.48,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":2.48,\"TotalTaxes\":0,\"TotalBaseCharge\":2.48,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":\"2-8\",\"PackageType\":\"Package\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-LM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":2.48,\"TotalTaxes\":0,\"TotalBaseCharge\":2.48,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":2.61,\"TotalTaxes\":0,\"TotalBaseCharge\":2.61,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":5,\"PackageType\":\"Large Envelope or Flat\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-MM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":2.61,\"TotalTaxes\":0,\"TotalBaseCharge\":2.61,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-06-01\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":2.61,\"TotalTaxes\":0,\"TotalBaseCharge\":2.61,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":5,\"PackageType\":\"Thick Envelope\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-MM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":2.61,\"TotalTaxes\":0,\"TotalBaseCharge\":2.61,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-06-01\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":2.61,\"TotalTaxes\":0,\"TotalBaseCharge\":2.61,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":5,\"PackageType\":\"Package\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-MM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":2.61,\"TotalTaxes\":0,\"TotalBaseCharge\":2.61,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-06-01\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":2.61,\"TotalTaxes\":0,\"TotalBaseCharge\":2.61,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":5,\"PackageType\":\"Large Package\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-MM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":2.61,\"TotalTaxes\":0,\"TotalBaseCharge\":2.61,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-06-01\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":5.75,\"TotalTaxes\":0,\"TotalBaseCharge\":5.75,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":2,\"PackageType\":\"Flat Rate Envelope\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":5.75,\"TotalTaxes\":0,\"TotalBaseCharge\":5.75,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-05-27\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":5.75,\"TotalTaxes\":0,\"TotalBaseCharge\":5.75,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":2,\"PackageType\":\"Legal Flat Rate Envelope\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":5.75,\"TotalTaxes\":0,\"TotalBaseCharge\":5.75,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-05-27\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.05,\"TotalTaxes\":0,\"TotalBaseCharge\":6.05,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":2,\"PackageType\":\"Letter\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.05,\"TotalTaxes\":0,\"TotalBaseCharge\":6.05,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-05-27\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.05,\"TotalTaxes\":0,\"TotalBaseCharge\":6.05,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":2,\"PackageType\":\"Large Envelope or Flat\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.05,\"TotalTaxes\":0,\"TotalBaseCharge\":6.05,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-05-27\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.05,\"TotalTaxes\":0,\"TotalBaseCharge\":6.05,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":2,\"PackageType\":\"Thick Envelope\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.05,\"TotalTaxes\":0,\"TotalBaseCharge\":6.05,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-05-27\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.05,\"TotalTaxes\":0,\"TotalBaseCharge\":6.05,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":2,\"PackageType\":\"Package\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.05,\"TotalTaxes\":0,\"TotalBaseCharge\":6.05,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-05-27\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.1,\"TotalTaxes\":0,\"TotalBaseCharge\":6.1,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":2,\"PackageType\":\"Small Flat Rate Box\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.1,\"TotalTaxes\":0,\"TotalBaseCharge\":6.1,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-05-27\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.1,\"TotalTaxes\":0,\"TotalBaseCharge\":6.1,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":2,\"PackageType\":\"Flat Rate Padded Envelope\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.1,\"TotalTaxes\":0,\"TotalBaseCharge\":6.1,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-05-27\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.15,\"TotalTaxes\":0,\"TotalBaseCharge\":6.15,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":5,\"PackageType\":\"Thick Envelope\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PS\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.15,\"TotalTaxes\":0,\"TotalBaseCharge\":6.15,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-06-01\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.15,\"TotalTaxes\":0,\"TotalBaseCharge\":6.15,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":5,\"PackageType\":\"Package\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PS\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.15,\"TotalTaxes\":0,\"TotalBaseCharge\":6.15,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-06-01\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.6,\"TotalTaxes\":0,\"TotalBaseCharge\":6.6,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":2,\"PackageType\":\"Regional Rate Box A\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":6.6,\"TotalTaxes\":0,\"TotalBaseCharge\":6.6,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-05-27\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":8.75,\"TotalTaxes\":0,\"TotalBaseCharge\":8.75,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":2,\"PackageType\":\"Regional Rate Box B\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":8.75,\"TotalTaxes\":0,\"TotalBaseCharge\":8.75,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-05-27\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":11,\"Units\":\"LB\"},\"TotalNetChargeWithDutiesAndTaxes\":11.53,\"TotalTaxes\":0,\"TotalBaseCharge\":11.11,\"TotalNetCharge\":11.53,\"TotalDiscounts\":0,\"TotalSurcharges\":0.42},\"GroupNumber\":0,\"CommitTimestamp\":{},\"Carrier\":\"FEDEX\",\"ServiceType\":\"FEDEX_GROUND\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":11,\"Units\":\"LB\"},\"TotalNetChargeWithDutiesAndTaxes\":11.53,\"TotalTaxes\":0,\"TotalBaseCharge\":11.11,\"TotalNetCharge\":11.53,\"TotalDiscounts\":0,\"TotalSurcharges\":0.42},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":11.95,\"TotalTaxes\":0,\"TotalBaseCharge\":11.95,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":2,\"PackageType\":\"Flat Rate Box\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":11.95,\"TotalTaxes\":0,\"TotalBaseCharge\":11.95,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-05-27\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":12.41,\"TotalTaxes\":0,\"TotalBaseCharge\":12.41,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":2,\"PackageType\":\"Large Package\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":12.41,\"TotalTaxes\":0,\"TotalBaseCharge\":12.41,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-05-27\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":12.41,\"TotalTaxes\":0,\"TotalBaseCharge\":12.41,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":5,\"PackageType\":\"Large Package\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PS\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":12.41,\"TotalTaxes\":0,\"TotalBaseCharge\":12.41,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-06-01\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":16.35,\"TotalTaxes\":0,\"TotalBaseCharge\":16.35,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":2,\"PackageType\":\"Large Flat Rate Box\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":16.35,\"TotalTaxes\":0,\"TotalBaseCharge\":16.35,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-05-27\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":11,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":18.57,\"TotalTaxes\":0,\"TotalBaseCharge\":18.57,\"TotalNetCharge\":18.57,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"CommitTimestamp\":{},\"Carrier\":\"UPS\",\"ServiceType\":3,\"DeliveryByTime\":{},\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":11,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":18.57,\"TotalTaxes\":0,\"TotalBaseCharge\":18.57,\"TotalNetCharge\":18.57,\"TotalDiscounts\":0,\"TotalSurcharges\":0}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":20.66,\"TotalTaxes\":0,\"TotalBaseCharge\":20.66,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":\"1-2\",\"PackageType\":\"Flat Rate Envelope\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-XM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":20.66,\"TotalTaxes\":0,\"TotalBaseCharge\":20.66,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":20.66,\"TotalTaxes\":0,\"TotalBaseCharge\":20.66,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":\"1-2\",\"PackageType\":\"Flat Rate Padded Envelope\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-XM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":20.66,\"TotalTaxes\":0,\"TotalBaseCharge\":20.66,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":20.66,\"TotalTaxes\":0,\"TotalBaseCharge\":20.66,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":\"1-2\",\"PackageType\":\"Legal Flat Rate Envelope\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-XM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":20.66,\"TotalTaxes\":0,\"TotalBaseCharge\":20.66,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":25.97,\"TotalTaxes\":0,\"TotalBaseCharge\":25.97,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":\"1-2\",\"PackageType\":\"Letter\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-XM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":25.97,\"TotalTaxes\":0,\"TotalBaseCharge\":25.97,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":25.97,\"TotalTaxes\":0,\"TotalBaseCharge\":25.97,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":\"1-2\",\"PackageType\":\"Large Envelope or Flat\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-XM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":25.97,\"TotalTaxes\":0,\"TotalBaseCharge\":25.97,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":25.97,\"TotalTaxes\":0,\"TotalBaseCharge\":25.97,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":\"1-2\",\"PackageType\":\"Thick Envelope\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-XM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":25.97,\"TotalTaxes\":0,\"TotalBaseCharge\":25.97,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":25.97,\"TotalTaxes\":0,\"TotalBaseCharge\":25.97,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":\"1-2\",\"PackageType\":\"Package\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-XM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":25.97,\"TotalTaxes\":0,\"TotalBaseCharge\":25.97,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":25.97,\"TotalTaxes\":0,\"TotalBaseCharge\":25.97,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":\"1-2\",\"PackageType\":\"Large Package\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-XM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":25.97,\"TotalTaxes\":0,\"TotalBaseCharge\":25.97,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":9,\"Units\":\"LB\"},\"TotalNetChargeWithDutiesAndTaxes\":27.67,\"TotalTaxes\":0,\"TotalBaseCharge\":27.46,\"TotalNetCharge\":27.67,\"TotalDiscounts\":0,\"TotalSurcharges\":0.21},\"GroupNumber\":0,\"CommitTimestamp\":{},\"Carrier\":\"FEDEX\",\"ServiceType\":\"FEDEX_EXPRESS_SAVER\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":9,\"Units\":\"LB\"},\"TotalNetChargeWithDutiesAndTaxes\":27.67,\"TotalTaxes\":0,\"TotalBaseCharge\":27.46,\"TotalNetCharge\":27.67,\"TotalDiscounts\":0,\"TotalSurcharges\":0.21},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":9,\"Units\":\"LB\"},\"TotalNetChargeWithDutiesAndTaxes\":29.78,\"TotalTaxes\":0,\"TotalBaseCharge\":29.56,\"TotalNetCharge\":29.78,\"TotalDiscounts\":0,\"TotalSurcharges\":0.22},\"GroupNumber\":0,\"CommitTimestamp\":{},\"Carrier\":\"FEDEX\",\"ServiceType\":\"FEDEX_2_DAY\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":9,\"Units\":\"LB\"},\"TotalNetChargeWithDutiesAndTaxes\":29.78,\"TotalTaxes\":0,\"TotalBaseCharge\":29.56,\"TotalNetCharge\":29.78,\"TotalDiscounts\":0,\"TotalSurcharges\":0.22},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":11,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":31.06,\"TotalTaxes\":0,\"TotalBaseCharge\":31.06,\"TotalNetCharge\":31.06,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"CommitTimestamp\":3,\"Carrier\":\"UPS\",\"ServiceType\":12,\"DeliveryByTime\":{},\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":11,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":31.06,\"TotalTaxes\":0,\"TotalBaseCharge\":31.06,\"TotalNetCharge\":31.06,\"TotalDiscounts\":0,\"TotalSurcharges\":0}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":9,\"Units\":\"LB\"},\"TotalNetChargeWithDutiesAndTaxes\":34.24,\"TotalTaxes\":0,\"TotalBaseCharge\":33.99,\"TotalNetCharge\":34.24,\"TotalDiscounts\":0,\"TotalSurcharges\":0.25},\"GroupNumber\":0,\"CommitTimestamp\":{},\"Carrier\":\"FEDEX\",\"ServiceType\":\"FEDEX_2_DAY_AM\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":9,\"Units\":\"LB\"},\"TotalNetChargeWithDutiesAndTaxes\":34.24,\"TotalTaxes\":0,\"TotalBaseCharge\":33.99,\"TotalNetCharge\":34.24,\"TotalDiscounts\":0,\"TotalSurcharges\":0.25},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":11,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":38.63,\"TotalTaxes\":0,\"TotalBaseCharge\":38.63,\"TotalNetCharge\":38.63,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"CommitTimestamp\":2,\"Carrier\":\"UPS\",\"ServiceType\":2,\"DeliveryByTime\":{},\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":11,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":38.63,\"TotalTaxes\":0,\"TotalBaseCharge\":38.63,\"TotalNetCharge\":38.63,\"TotalDiscounts\":0,\"TotalSurcharges\":0}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":9,\"Units\":\"LB\"},\"TotalNetChargeWithDutiesAndTaxes\":45.84,\"TotalTaxes\":0,\"TotalBaseCharge\":45.5,\"TotalNetCharge\":45.84,\"TotalDiscounts\":0,\"TotalSurcharges\":0.34},\"GroupNumber\":0,\"CommitTimestamp\":{},\"Carrier\":\"FEDEX\",\"ServiceType\":\"PRIORITY_OVERNIGHT\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":9,\"Units\":\"LB\"},\"TotalNetChargeWithDutiesAndTaxes\":45.84,\"TotalTaxes\":0,\"TotalBaseCharge\":45.5,\"TotalNetCharge\":45.84,\"TotalDiscounts\":0,\"TotalSurcharges\":0.34},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":86.89,\"TotalTaxes\":0,\"TotalBaseCharge\":86.89,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"GroupNumber\":4,\"CommitTimestamp\":5,\"PackageType\":\"Oversized Package\",\"Carrier\":\"STAMPS\",\"ServiceType\":\"US-PS\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":1,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":86.89,\"TotalTaxes\":0,\"TotalBaseCharge\":86.89,\"TotalNetCharge\":0,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"DeliveryTimestamp\":\"2016-06-01\"},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":9,\"Units\":\"LB\"},\"TotalNetChargeWithDutiesAndTaxes\":87.06,\"TotalTaxes\":0,\"TotalBaseCharge\":86.41,\"TotalNetCharge\":87.06,\"TotalDiscounts\":0,\"TotalSurcharges\":0.65},\"GroupNumber\":0,\"CommitTimestamp\":{},\"Carrier\":\"FEDEX\",\"ServiceType\":\"STANDARD_OVERNIGHT\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":9,\"Units\":\"LB\"},\"TotalNetChargeWithDutiesAndTaxes\":87.06,\"TotalTaxes\":0,\"TotalBaseCharge\":86.41,\"TotalNetCharge\":87.06,\"TotalDiscounts\":0,\"TotalSurcharges\":0.65},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":11,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":103.37,\"TotalTaxes\":0,\"TotalBaseCharge\":103.37,\"TotalNetCharge\":103.37,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"CommitTimestamp\":1,\"Carrier\":\"UPS\",\"ServiceType\":13,\"DeliveryByTime\":{},\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":11,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":103.37,\"TotalTaxes\":0,\"TotalBaseCharge\":103.37,\"TotalNetCharge\":103.37,\"TotalDiscounts\":0,\"TotalSurcharges\":0}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":11,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":112.31,\"TotalTaxes\":0,\"TotalBaseCharge\":112.31,\"TotalNetCharge\":112.31,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"CommitTimestamp\":1,\"Carrier\":\"UPS\",\"ServiceType\":1,\"DeliveryByTime\":\"10:30 A.M.\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":11,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":112.31,\"TotalTaxes\":0,\"TotalBaseCharge\":112.31,\"TotalNetCharge\":112.31,\"TotalDiscounts\":0,\"TotalSurcharges\":0}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":9,\"Units\":\"LB\"},\"TotalNetChargeWithDutiesAndTaxes\":117.45,\"TotalTaxes\":0,\"TotalBaseCharge\":116.58,\"TotalNetCharge\":117.45,\"TotalDiscounts\":0,\"TotalSurcharges\":0.87},\"GroupNumber\":0,\"CommitTimestamp\":{},\"Carrier\":\"FEDEX\",\"ServiceType\":\"FIRST_OVERNIGHT\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":9,\"Units\":\"LB\"},\"TotalNetChargeWithDutiesAndTaxes\":117.45,\"TotalTaxes\":0,\"TotalBaseCharge\":116.58,\"TotalNetCharge\":117.45,\"TotalDiscounts\":0,\"TotalSurcharges\":0.87},\"DeliveryTimestamp\":{}},{\"PackageRateDetail\":{\"TotalBillingWeight\":{\"Value\":11,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":143.13,\"TotalTaxes\":0,\"TotalBaseCharge\":143.13,\"TotalNetCharge\":143.13,\"TotalDiscounts\":0,\"TotalSurcharges\":0},\"CommitTimestamp\":1,\"Carrier\":\"UPS\",\"ServiceType\":14,\"DeliveryByTime\":\"8:00 A.M.\",\"ShipmentRateDetail\":{\"TotalBillingWeight\":{\"Value\":11,\"Units\":\"LBS\"},\"TotalNetChargeWithDutiesAndTaxes\":143.13,\"TotalTaxes\":0,\"TotalBaseCharge\":143.13,\"TotalNetCharge\":143.13,\"TotalDiscounts\":0,\"TotalSurcharges\":0}}]";
	//2-3
//3, 5
		return myvar;
	}

	@Override
	protected void processBean(Exchange exchange) throws Exception {
		String body = exchange.getIn().getBody(String.class);

		logger.debug("body s : " + body);

		exchange.getIn().setBody(body);
	}

}
