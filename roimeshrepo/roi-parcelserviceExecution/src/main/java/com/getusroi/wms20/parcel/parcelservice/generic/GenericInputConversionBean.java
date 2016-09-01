package com.getusroi.wms20.parcel.parcelservice.generic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;

public class GenericInputConversionBean extends AbstractROICamelJDBCBean {

	final static Logger logger = LoggerFactory.getLogger(GenericInputConversionBean.class);
	private final String RATE_REQUEST_INFO_CARRIER = "RateRequestInfo_Carrier";
	private final String RATE_REQUEST_INFO_RR_CODE = "RateRequestInfo_RateRequestCode";
	private final String XPATH_EXPRESSION_CARRIER = "RateRequest/RateRequestInfo/ServiceTypes/Carrier/text()";
	private final String CARRIERS = "Carriers";
	private final String XPATH_EXPRESSION_RATEREQUEST_CODE = "RateRequest/RateRequestInfo/RateRequestType/RateRequestCode/text()";
	private final String RATE_REQUEST_CODE = "RateRequestCode";
	private final String XPATH_EXPRESSION_REQUESTARRIVE_DATE = "RateRequest/RateRequestInfo/RateRequestType/RequestArriveDate/text()";
	private final String REQUEST_ARRIVE_DATE = "RequestArriveDate";
	private final String XPATH_EXPRESSION_WAY = "RateRequest/RateRequestInfo/ServiceTypes/Way/text()";
	private final String WAY = "Way";
	private final String XPATH_EXPRESSION_SERVICETYPE = "RateRequest/RateRequestInfo/ServiceTypes/ServiceType/text()";
	private final String SERVICE_TYPE = "ServiceType";

	@Override
	protected void processBean(Exchange exchange) throws Exception {
		String bodyIn = exchange.getIn().getBody(String.class);
		logger.debug("inside processBean the body: " + bodyIn);
		JSONObject jsonObjectin = new JSONObject(bodyIn);
		JSONObject jsonObject = new JSONObject(jsonObjectin.toString());
		JSONArray jsonArray = new JSONArray(jsonObject.get("data").toString());
		String validationChooser = (String) exchange.getIn().getHeader("exeroute");
		if ("parcelservice-shop-ER".equals(validationChooser)) {
			try {
				initiateTheJsonArrayData(jsonObject);
			} catch (Exception e) {
				jsonArray = new JSONArray(jsonObject.get("data").toString());
			}
			returnTheXmlDatabyValidating(jsonArray, exchange);
		} else if ("parcelservice-rates-ER".equals(validationChooser)) {
			try {
				jsonArray = returndataforxmlUrlRateService(jsonObject);
			} catch (Exception e) {
				jsonArray = new JSONArray(jsonObject.get("data").toString());
			}
			try {
				checkandReturnTheneededBodyRateService(jsonArray, exchange);
			} catch (XPathExpressionException | IOException | ParserConfigurationException e) {
				//throw new ParcelServiceException("Unable to build the document: ", e);
			}
		} else if ("parcelservice-createship-ER".equals(validationChooser)) {
			convertjsontoXmlForShipment(exchange);
		} else if ("parcelservice-voidship-ER".equals(validationChooser)) {
			convertJsontoXmlForVoid(exchange);
		}

	}// ..end of the method..

	/**
	 * 
	 * @param exchange
	 * @return
	 * @throws IOException
	 */
	private void convertJsontoXmlForVoid(Exchange exchange) throws IOException {
		
		String bodyIn = exchange.getIn().getBody(String.class);
		JSONObject jsonObjectin = new JSONObject(bodyIn);
		JSONObject jsonObject = new JSONObject(jsonObjectin.toString());
		JSONArray jsonArray = new JSONArray(jsonObject.get("data").toString());
		String xml = XML.toString(jsonArray.get(0));
		logger.debug("inside void: "+xml);
		exchange.getIn().setBody(xml.toString());
	}// ..end of the method

	/**
	 * 
	 * @param exchange
	 * @return
	 * @throws IOException
	 */
	private void convertjsontoXmlForShipment(Exchange exchange) throws IOException {
		String bodyIn = exchange.getIn().getBody(String.class);
		JSONObject jsonObjectin = new JSONObject(bodyIn);
		JSONObject jsonObject = new JSONObject(jsonObjectin.toString());
		JSONArray jsonArray = new JSONArray(jsonObject.get("data").toString());
		String xml = XML.toString(jsonArray.get(0));
		logger.debug("inside shipment: "+xml);
		exchange.getIn().setBody(xml.toString());
	}// end of method

	/**
	 * 
	 * @param jsonArray
	 * @param exchange
	 * @throws IOException
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws ParcelServiceException
	 */
	private void checkandReturnTheneededBodyRateService(JSONArray jsonArray, Exchange exchange)
			throws IOException, XPathExpressionException, ParserConfigurationException {
		try {
			String xml = XML.toString(jsonArray.get(0));
			logger.debug("The xml before validation: " + xml);
			InputStream streamedXml = new ByteArrayInputStream(xml.getBytes());
			SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source source = new StreamSource(streamedXml);
			URL url = GenericInputConversionBean.class.getResource("/FEDEX_RATE_ENUMERATION.xsd");
			Schema schema = schemaFactory.newSchema(url);
			Validator validator = schema.newValidator();
			validator.validate(source);

			exchange.getIn().setHeader(SERVICE_TYPE, getRateRequestInfoAttributesServiceType(xml.toString()));
			exchange.getIn().setBody(xml.toString());
		} catch (SAXException saxException) {
			exchange.getIn().setBody("{" + "\"ServiceRequest\":\"Fault\"," + "\"Message\":" + saxException + "" + "}");
			//throw new ParcelServiceException("The validation of input xml rates gone wrong: ", saxException);
		}

	}// ..end of the method

	/**
	 * this is a tempory solution from individual feature to parse the xml which
	 * is requested from the restClient
	 * 
	 * @param jsonObject
	 * @return
	 */
	private JSONArray returndataforxmlUrlRateService(JSONObject jsonObject) {
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
	}// ..end of the method

	/**
	 * reads jsonInput and converts it to xml for processing, aswell as sets the
	 * RateRequestInfo from xml to Exchange~ headers
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
	}// ..end of the method

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
		logger.debug(".inside the xml initializer: " + xml);
		InputStream streamedXml = new ByteArrayInputStream(xml.getBytes());
		SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Source source = new StreamSource(streamedXml);
		Schema schema = null;
		try {
			logger.debug("inside-returnTheXmlDatabyValidating: " + xml);
			// #TODO since the project is loaded from the FeatureInstaller, it
			// searches the xsd in its classpath, hence given the local path,
			// have to change.
			URL url = GenericInputConversionBean.class.getResource("/RawFedExRate.xsd");
			logger.debug("the url generated is: " + url);
			schema = schemaFactory.newSchema(url);
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
	}// ..end of the method

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

}
