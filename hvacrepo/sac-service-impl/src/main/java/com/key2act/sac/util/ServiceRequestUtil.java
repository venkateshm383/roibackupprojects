package com.key2act.sac.util;

import static com.key2act.sac.util.SACConstant.SOURCE_IDENTIFY_FILE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.mesh.feature.FeatureErrorPropertyConstant;
import com.getusroi.mesh.generic.UnableToLoadPropertiesException;

public class ServiceRequestUtil {
	private static final Logger logger=LoggerFactory.getLogger(ServiceRequestUtil.class);
	private static Properties properties=null;
	
	static{
		try {
			properties=loadingPropertiesFile(SOURCE_IDENTIFY_FILE);
			
		} catch (UnableToLoadPropertiesException e) {
			logger.error("Uable to read error code and error message property file");
		}
	}
	
	/**
	 * to generate the document object from the xml input which is 
	 * of String
	 * 
	 * @param xmlInput : xml input in string format
	 * @return Document : converted xml string into DOcument object 
	 * @throws UnableToParseServiceRequestException : Exception in converting xml string into document object
	 */
	public Document generateDocumentFromString(String xmlInput) throws UnableToParseServiceRequestException {
		logger.debug(".generateDocumentFromString method of ServiceRequestUtil");
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document xmlDocument;
		try {
			builder = builderFactory.newDocumentBuilder();
			try {
				xmlDocument = builder.parse(new ByteArrayInputStream(xmlInput.getBytes("UTF-16")));
			} catch (SAXException | IOException e) {
				throw new UnableToParseServiceRequestException("Unable to parse the xmlString into document..", e);
			}
		} catch (ParserConfigurationException e) {
			throw new UnableToParseServiceRequestException("Unable to initiate the document builder..", e);
		}		
		return xmlDocument;
	}// ..end of method generateDocumentFromString
	
	public static Properties getPropertyObject(){
		return properties;
	}

	/**
	 * Custom bean to marshal the XML string to org.json.Json Object
	 * 
	 * @param exchange : Camel Exchange Object
	 * @throws UnableToConvertJsonServiceRequestToXML 
	 * @throws JSONException
	 */
	public void checkAndConvertRequestToXML(Exchange exchange) throws UnableToConvertJsonServiceRequestToXML  {
		logger.debug(".checkAndConvertRequestToXML method of ServiceRequestUtil ");
		String bodyIn = exchange.getIn().getBody(String.class);
		if (bodyIn.startsWith("{")) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(bodyIn);
				JSONArray jsonArray = (JSONArray) jsonObject.get(MeshHeaderConstant.DATA_KEY);
				JSONObject jobj = (JSONObject) jsonArray.get(0);
				String xmlString = XML.toString(jobj);
				logger.debug("unmarshalled - jsonObject to xml: " + xmlString);
				exchange.getIn().setBody(xmlString);
			} catch (JSONException e) {
				throw new UnableToConvertJsonServiceRequestToXML("Unable to convert Json format service request to xml format : "+bodyIn);
			}			
		}else{			 
			exchange.getIn().setBody(bodyIn);
		}
	}//end of method checkAndConvertRequestToXML

	
	/**
	 * This is to get parmastore cache data from mesh header
	 * @param searchString : cache key
	 * @param parmaConfigname : parmastore config key
	 * @param meshHeader : MeshHeader Object
	 * @return Object
	 */
	public Object getPipelineCacheData(String searchString,String parmaConfigname,MeshHeader meshHeader){
		logger.debug(".getPipelineCacheData method of ServiceRequestUtil");
		Map<String,Object> permaCacheObjectInMap=meshHeader.getPermadata();
		Map<String,Object> permaCacheObject=(Map<String, Object>)permaCacheObjectInMap.get(parmaConfigname.trim());	
		if(permaCacheObject !=null && !(permaCacheObject.isEmpty())){
			logger.debug("search key : "+searchString);
		Object object=permaCacheObject.get(searchString.trim());
		return object;
		}else{
			logger.debug("No permastore is mapped in mesh header  with name : "+parmaConfigname);
		return null;	
		}		
	}//end of method of getPipelineCacheData
	
	/**
	 * This method is used to get unique id
	 * @return unique id in String
	 */
	public String generateUID(){
		logger.debug(".generateUID method in ServiceRequestUtil");
		UUID uuid=UUID.randomUUID();
		return uuid.toString();
	}//end of method generateUID
	
	/**
	 * This method is used to load property file from classpath
	 * @param fileToLoad : file name to be loaded from classpath
	 * @return Properties Object
	 * @throws UnableToLoadPropertiesException
	 */
	private static Properties loadingPropertiesFile(String fileToLoad) throws UnableToLoadPropertiesException {
		logger.debug("inside loadingPropertiesFile method of ServiceRequestUtil");
		Properties prop = new Properties();
		InputStream input1 = ServiceRequestUtil.class.getClassLoader().getResourceAsStream(fileToLoad);
		try {
			prop.load(input1);
		} catch (IOException e) {
			throw new UnableToLoadPropertiesException(
					"unable to load property file = " + FeatureErrorPropertyConstant.ERROR_CODE_FILE, e);
		}
		return prop;
	}
	
	/**
	 * This method is used to set the response message and response code 
	 * @param key : key in String 
	 * @param exchange : Camel Exchange
	 * @throws UnableToLoadPropertiesException
	 */
	public void setResponseCode(String key,Exchange exchange) throws UnableToLoadPropertiesException{
		logger.debug(".getResponseCode method in ServiceRequestUtil");
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		exchange.getOut().setHeader(MeshHeaderConstant.MESH_HEADER_KEY,meshHeader);
		Exception exception=exchange.getException();
		if(exception == null){
			Properties prop=loadingPropertiesFile(SACConstant.HTTP_RESPONSE_MESSAGE_FILE_KEY);
			String responsemessage=prop.getProperty(key.trim());
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE,200);
			exchange.getOut().setBody("http response code : "+exchange.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE)+", response message : "+responsemessage);
		}else{
			logger.debug("error occured : "+exception.getMessage());
		}
	}//end of method getResponseCode

}
