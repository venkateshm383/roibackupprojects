package com.getusroi.wms20.parcel.parcelservice.ups.beans;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.parcel.parcelservice.exception.ParcelServiceErrorCodePropertiesReaderException;
import com.getusroi.wms20.parcel.parcelservice.exception.UpsException;
import com.getusroi.wms20.parcel.parcelservice.generic.ParcelServiceErrorCodeHandler;
import com.getusroi.wms20.parcel.parcelservice.ups.constant.UPSConstant;
import com.getusroi.wms20.parcel.parcelservice.ups.service.UPSRATE_ServiceBuilder;
import com.getusroi.wms20.parcel.parcelservice.ups.vo.Shipper;

public class UPSRATERequestBuilder extends AbstractROICamelJDBCBean {

	private final String PROPERTIES_UPS_ERRORCODE = "UPS_ERROR_CODES.properties";
	private final String SERVICE_TYPE = "ServiceType";
	private Logger logger = LoggerFactory.getLogger(UPSRATERequestBuilder.class);

	@Override
	protected void processBean(Exchange exchange) throws UpsException {

		String transOne = exchange.getIn().getBody(String.class);
		UPSRATE_ServiceBuilder upsrate_ServiceBuilder = new UPSRATE_ServiceBuilder();
		Map<String, Object> mapStore = new HashMap<String, Object>();
		mapStore.put(UPSConstant.XML, transOne);
		int shipId = 0;
		try {
			shipId = upsrate_ServiceBuilder.getShipperIdfromXml(mapStore.get(UPSConstant.XML));
		} catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
			throw new UpsException("unable to get the shipperId from the xml: ", e);
		}
		mapStore.put(UPSConstant.SHIPPER_ID, shipId);
		Map<String, Object> permaData = getPermastoreData(exchange);
		logger.debug("THe permadata in the upsRateBuilder: " + permaData);
		Shipper ship = upsrate_ServiceBuilder.setShipperDetails(mapStore, permaData);
		String xmlOneTransformed = null;
		try {
			xmlOneTransformed = upsrate_ServiceBuilder.getTranformedGenericXml(mapStore.get(UPSConstant.XML), ship,
					permaData);
		} catch (ParserConfigurationException | SAXException | IOException | TransformerFactoryConfigurationError
				| TransformerException e) {
			throw new UpsException("unable to transform/append the xml in ups: ", e);
		}
		exchange.getIn().setBody(xmlOneTransformed);

	}// end of the method

	/**
	 * to get the permastoredata from the meshheader initialized in meshcore
	 * 
	 * @param exchange
	 * @return permastore object
	 */
	private Map<String, Object> getPermastoreData(Exchange exchange) {

		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> permaData = meshHeader.getPermadata();

		return permaData;
	}// ..end of the method

	/**
	 * this is to replace service name when success response, and to set custom error codes when soap fault occurs
	 * @param exchange
	 * @throws Exception
	 */
	public void processBean1(Exchange exchange) throws Exception {

		String bodyIn = exchange.getIn().getBody(String.class);
		Map<String, Object> serviceType = (Map<String, Object>) exchange.getIn().getHeader(SERVICE_TYPE);
		JSONObject jsonObject = XML.toJSONObject(bodyIn);
		if (jsonObject.getJSONObject("RateReply").has("RateReplyDetails")) {
			JSONObject jsonObject2 = jsonObject.getJSONObject("RateReply").getJSONObject("RateReplyDetails");
			JSONArray jnewArray = new JSONArray();
			jnewArray.put(jsonObject2);
			UPSRATE_ServiceBuilder upsRateServiceBuilder = new UPSRATE_ServiceBuilder();
			JSONArray resultingUpsReplacedServiceType = upsRateServiceBuilder.replaceUpsServiceTypeNames(jnewArray,
					exchange);
			exchange.getIn().setBody(resultingUpsReplacedServiceType.toString());
		} else {
			JSONObject errorJsonObj = jsonObject.getJSONObject("RateReply");
			ParcelServiceErrorCodeHandler errorCodeHandler = new ParcelServiceErrorCodeHandler();
			Properties errorProp;
			Map<String, String> maperrorProp;
			try {
				int code = (int) errorJsonObj.get("Code");
				errorProp = loadUpsErrorCodePropertiesFile();
				maperrorProp = errorCodeHandler.errorCodeSetter(errorProp.getProperty(String.valueOf(code)),
						errorJsonObj.getString("Message"));
			} catch (IOException e) {
				//thrown when property file is missing 
				throw new ParcelServiceErrorCodePropertiesReaderException(
						"Unable to get the UPS ErrorCodes properties file: ", e);
			}
			Message out = exchange.getOut();
			out.setHeader(org.apache.cxf.message.Message.RESPONSE_CODE,
					Integer.parseInt(maperrorProp.get("ParcelServiceErrorCode")));
			JSONObject object = new JSONObject(maperrorProp);
			// ...sets the new body with the generic error code and message
			out.setBody(object);

		}
	}// ..end of the method

	/**
	 * to load - PARCEL_SERVICE_GENERIC_ERROR_CODES.properties - Properties file
	 * 
	 * @return Properties
	 * @throws ParcelServiceErrorCodePropertiesReaderException
	 * @throws IOException
	 */
	private Properties loadUpsErrorCodePropertiesFile() throws IOException {
		Properties prop = new Properties();
		InputStream input = UPSRATERequestBuilder.class.getClassLoader().getResourceAsStream(PROPERTIES_UPS_ERRORCODE);
		prop.load(input);
		return prop;
	}// ..end of the method
}
