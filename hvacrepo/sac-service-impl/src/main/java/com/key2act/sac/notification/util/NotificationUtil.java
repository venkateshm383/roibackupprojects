package com.key2act.sac.notification.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.getusroi.integrationfwk.activities.bean.ActivityEnricherException;
import com.getusroi.integrationfwk.jdbcIntactivity.config.helper.JdbcIntActivityConfigurationException;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.key2act.sac.notification.service.SCNotifyRequestProcessingException;
import com.key2act.sac.util.SACConstant;
import com.key2act.sac.util.UnableToParseServiceRequestException;

public class NotificationUtil {
	Logger logger=LoggerFactory.getLogger(NotificationUtil.class);
	/**
	 * unmarshalling json to xml of Service Call
	 * 
	 * 
	 * @param exchange
	 * @throws JSONException
	 */
	public void serviceCallUnmarshall(Exchange exchange) throws JSONException{
		String jsonInput=exchange.getIn().getBody(String.class);
		//unmarshalls only if the exchange contains json data
		if(jsonInput.startsWith("{")){
			JSONObject jsonObj=new JSONObject(jsonInput);
			JSONArray jsonarray = (JSONArray) jsonObj.get("data");
			String xml= XML.toString(jsonarray.get(0));
			exchange.getIn().setBody(xml);			
		}
		else{
			exchange.getIn().setBody(jsonInput);
		}
	}//..end of method
	
	/**
	 * to generate the document object once and all from the xml input which is
	 * of String
	 * 
	 * @param xmlInput
	 * @return documentObject
	 * @throws ActivityEnricherException
	 */
	public Document generateDocumentFromString(String xmlInput) throws SCNotifyRequestProcessingException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document xmlDocument;
		logger.debug(".xmlInput : "+xmlInput);
		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new SCNotifyRequestProcessingException("Unable to initiate the document builder..", e);
		}
		try {
			xmlDocument = builder.parse(new ByteArrayInputStream(xmlInput.getBytes("UTF-16")));
		} catch (SAXException | IOException e) {
			throw new SCNotifyRequestProcessingException("Unable to parse the xmlString into document..", e);
		}
		return xmlDocument;
	}// ..end of method
	/**
	 * This method is used for node to String format
	 * 
	 * @param givenNode
	 *            : Node Object
	 * @return
	 * @throws SCNotifyRequestProcessingException
	 * @throws JdbcIntActivityConfigurationException
	 */
	public String nodeToString(Node givenNode) throws SCNotifyRequestProcessingException {
		StringWriter sw = new StringWriter();
		String str;
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
			t.transform(new DOMSource(givenNode), new StreamResult(sw));
			str = sw.toString();
		} catch (TransformerException te) {
			throw new SCNotifyRequestProcessingException();
		}
		return str;
	}//..end of method
	
	
	

	
	/**
	 * to get StatusID Id From given notification Xml Data 
	 * @param bodyIn
	 * @return
	 * @throws SCNotifyRequestProcessingException 
	 * @throws TransformerException 
	 * @throws UnableToParseServiceRequestException
	 */
	public boolean getStatusNameFromXmlData(Document document,MeshHeader meshHeader,Exchange exhExchange) throws SCNotifyRequestProcessingException {
		logger.debug("(.)  getStatusNameFromXmlData method of ServiceChannelNotificationProcessBean class ");
		String status="";
		String internalStatus="";
		boolean statusExist=false;
		if(document !=null){
			
			XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodeTypeList;
		try {
			nodeTypeList = (NodeList) xPath.compile((String)NotificationMasterConstants.XPATH_SC_STATUS).evaluate(document,
								XPathConstants.NODESET);
			//checking if 
			if(nodeTypeList !=null && nodeTypeList.getLength()>0){
				Node node =nodeTypeList.item(0);								
				status=node.getTextContent();
				logger.debug(" Status Value === "+status);
				statusExist=true;
				internalStatus=	getInternalStatusFromCacheData(status.trim(), SACConstant.SERVICECHANEL_KEY2ACT_STATUS_MAPPING_KEY, meshHeader);
				
				addElmentToXmlDocument(document, NotificationMasterConstants.INTERNAL_STATUS_NODE,internalStatus,NotificationMasterConstants.XPATH_SC_INTERNAL_STATUS);

			}
			exhExchange.getIn().setBody(documentToString(document));

		} catch (XPathExpressionException | TransformerException e) {
			logger.error("Error in Evavluating  Xpath Expression to get status value  ",e);
		}}
		return statusExist;
	}// .. End of Method getStatusNameFromXmlData
	

	
	
	
	/**
	 * to add new element to  xml data
	 * @param bodyIn
	 * @param elementToAdd
	 * @param xpath
	 * @throws SCNotifyRequestProcessingException
	 */
	public void addElmentToXmlDocument(Document document,String elementToAdd,String elementContent,String xpath) {
		logger.debug("(.)  addElmentToXmlDocument  ");
		if(document !=null){
			XPath xPath = XPathFactory.newInstance().newXPath();
		
		NodeList nodeTypeList;
		try {
			nodeTypeList = (NodeList) xPath.compile((String)xpath).evaluate(document,
								XPathConstants.NODESET);
			//checking if 
			if(nodeTypeList !=null && nodeTypeList.getLength()>0){
				Element node = document.createElement(elementToAdd);
				node.setTextContent(elementContent);
				// appended the element as a child to the node fetched from the
				// xpath
				nodeTypeList.item(0).appendChild(node);			
				logger.debug(" element added to xml  ");
			}
		} catch (XPathExpressionException e) {
			logger.error("Error in Evavluating  Xpath Expression  :  "+xpath ,e);
		}}
	}// .. End of Method addElmentToXmlDocument
	

	
	/**
	 * to add Attriubute to  xml data
	 * @param bodyIn
	 * @param elementToAdd
	 * @param xpath
	 * @throws SCNotifyRequestProcessingException
	 */
	public void addAttributeToXmlDocument(Document document,String attributeName,String attributeContent,String xpath) {
		logger.debug("(.) addAttributeToXmlDocument  ");
		if(document !=null){
			XPath xPath = XPathFactory.newInstance().newXPath();
		
		NodeList nodeTypeList;
		try {
			nodeTypeList = (NodeList) xPath.compile((String)xpath).evaluate(document,
								XPathConstants.NODESET);
			logger.debug("Node Type LIST : "+nodeTypeList.getLength());
			//checking if 
			if(nodeTypeList !=null && nodeTypeList.getLength()>0){
				Element node =(Element) nodeTypeList.item(0);
				node.setAttribute(attributeName, attributeContent);
				logger.debug(" element added to xml  ");
			}
		} catch (XPathExpressionException e) {
			logger.error("Error in Evavluating  Xpath Expression  :  "+xpath ,e);
		}}
	}// .. End of Method addElmentToXmlDocument
	
	
	
	/**
	 * method taking XPAth as input and returning the data processed with the
	 * exchange after encoding it.
	 * 
	 * @throws SCNotifyRequestProcessingException
	 */
	public String encodeFromXpath(String expression, Document xmlDocument) throws SCNotifyRequestProcessingException {
		logger.debug("(.) inside encodeFromXpath method of ServiceChannelNotificationProcessBean Class ");
		NotificationUtil notificationUtil = new NotificationUtil();
		String fieldVal = null;
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = null;
		try {
			nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				if (nodeList.item(0).getNodeType() != Node.ELEMENT_NODE) {
					fieldVal = nodeList.item(0).getTextContent().trim();
					logger.debug(".encodedData (StoreID OR CustomerID) : " + fieldVal);
				} else {
					Node givenNode = nodeList.item(0);
					fieldVal = notificationUtil.nodeToString(givenNode.getFirstChild()).trim();
					logger.debug(".xmlData : " + fieldVal);
				}
			}
			return fieldVal;
		} catch (XPathExpressionException e) {
			throw new SCNotifyRequestProcessingException("Unable to compile the xpath expression at index - "
					+ " when evaluating document - " + xmlDocument.getFirstChild().getNodeName() + "..", e);
		} 
	}// ..end of method encodeFromXpath
	
	/**
	 * This is to get permastore cache data from mesh header
	 * @param searchString : cache key
	 * @param parmaConfigname : permastore config key
	 * @param meshHeader : MeshHeader Object
	 * @return String
	 * @throws SCNotifyRequestProcessingException 
	 */
	private String getInternalStatusFromCacheData(String searchString,String parmaConfigname,MeshHeader meshHeader) throws SCNotifyRequestProcessingException{
		logger.debug("(.) getInternalStatusFromCacheData method of ServiceChannelNotificationProcessBean Class");
		String internalStatus="";
		Map<String,Object> permaCacheObjectInMap=meshHeader.getPermadata();
		Map<String,Object> permaCacheObject=(Map<String, Object>)permaCacheObjectInMap.get(parmaConfigname.trim());	
		Object object=permaCacheObject.get(searchString.trim());
		logger.debug("object : "+object);

		if(object==null)
			throw new SCNotifyRequestProcessingException("Error not found permstore Cache Data for key == "+searchString);
			
		if(object instanceof 	org.json.simple.JSONArray){
			org.json.simple.JSONArray jsonArray=(	org.json.simple.JSONArray)object;	
			if(jsonArray.size()==2){
				org.json.simple.JSONObject json=(org.json.simple.JSONObject)jsonArray.get(0);
			internalStatus=json.get(NotificationMasterConstants.INTERNAL_STATUS_KEY).toString();
			}
		}else{
			org.json.simple.JSONObject json=(org.json.simple.JSONObject)object;
			internalStatus=json.get(NotificationMasterConstants.INTERNAL_STATUS_KEY).toString();
		}
		logger.debug("  internal status value = "+ internalStatus);
		return internalStatus;
	}//..end of method getInternalStatusFromCacheData
	
	
	/**
	 * This is to get permastore cache data from mesh header
	 * @param searchString : cache key
	 * @param parmaConfigname : permastore config key
	 * @param meshHeader : MeshHeader Object
	 * @return String
	 * @throws SCNotifyRequestProcessingException 
	 */
	public  String getDataFromCacheData(String providerkey,String searchKey,String parmaConfigname,MeshHeader meshHeader) throws SCNotifyRequestProcessingException{
		logger.debug("(.) getDataFromCacheData method of ServiceChannelNotificationProcessBean Class");
		String data="";
		Map<String,Object> permaCacheObjectInMap=meshHeader.getPermadata();
		logger.debug("providerkey : "+providerkey  +" searchKey : "+ searchKey +"  Permastore Data : "+parmaConfigname );
		Map<String,Object> permaCacheObject=(Map<String, Object>)permaCacheObjectInMap.get(parmaConfigname.trim());	
		logger.debug("Permastore Data : "+permaCacheObject );

		Object object=permaCacheObject.get(providerkey.trim());
		logger.debug("object : "+object);

		if(object==null)
			throw new SCNotifyRequestProcessingException("Error not found permstore Cache Data for key == "+providerkey);
			
		if(!(object instanceof org.json.simple.JSONObject))
			throw new SCNotifyRequestProcessingException("Error not found permstore Cache Data for key == "+providerkey);

			org.json.simple.JSONObject json=(org.json.simple.JSONObject)object;
			logger.debug("json : "+json);

			org.json.simple.JSONObject scConfig=(org.json.simple.JSONObject)json.get(NotificationMasterConstants.SC_CONFIG_KEY);
			data=scConfig.get(searchKey).toString();
	
		logger.debug("  value From permastore SC-CONFIG  = "+ data);
		return data;
	}//..end of method getDataFromCacheData
	

	
	/**
	 * encode xml document with UTF-16 and convert string format
	 * @param xmlDocument
	 * @return encoded String
	 * @throws TransformerException
	 */
	public String documentToString(Document xmlDocument) throws TransformerException {
		logger.debug("(.) documentToString method of ServiceChannelNotificationProcessBean class ");
		DOMSource domSource = new DOMSource(xmlDocument);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-16");
		transformer.transform(domSource, result);
		logger.debug(" DOcument generated string : "+writer.toString());
		return writer.toString();
	}// ..end of method documentToString
}
