package com.key2act.sac.notification.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.camel.Exchange;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.key2act.sac.notification.util.NotificationMasterConstants;
import com.key2act.sac.util.UnableToParseServiceRequestException;

public class FetchPipelineName extends AbstractCassandraBean {
	
	
	Logger logger=LoggerFactory.getLogger(FetchPipelineName.class);

	private final String  PIPELINE_LIST="pipelineList";
	private final String  RECIPIENT_LIST="pipelineRecipientList";
	private final String  ROUTE="direct:pipeline";
	private final String PIPELINE_NAME="pipelineName";
	private final String EVENT_ID = "eventid";
	private final String SC_NOTIFCATION_PERMASTORECONFIG_KEY="ServiceChannel-Key2Act-Notification-Mapping";
	private final String NOTIFICATION_KEY="Notification";
	private final String XML_EVENT_ID_TAG = "EventId";

	
	/**
	 * method to fetch the pipeline names  from permastore and route to pipe lines 
	 * 	* @param exchange
	 * @return
	 * @throws SCNotifyRequestProcessingException 
	 */

	public void processorBean(Exchange exchange) throws SCNotifyRequestProcessingException{

		List<String> pipelineConfigNameList = new ArrayList<String>();
		List<String> pipelineRecipienttList = new ArrayList<String>();

		String bodyIn=exchange.getIn().getBody(String.class);
		MeshHeader meshHeader=(MeshHeader)exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);

		String eventValue=getEventNameFromXmlData(bodyIn);
		if(eventValue!=null&&eventValue.isEmpty())
			throw new SCNotifyRequestProcessingException("event Id not found in Event Notification = "+bodyIn);
		
		Object object=getPipelineNamesFromCacheData(NOTIFICATION_KEY, SC_NOTIFCATION_PERMASTORECONFIG_KEY, meshHeader);	
		logger.debug("permastore cache  Object "+object);
		if(object==null)
			throw new SCNotifyRequestProcessingException("Notification Pipeline configuration not found in permastore Cache for ConfigName  = "+SC_NOTIFCATION_PERMASTORECONFIG_KEY);

		if(!(object instanceof JSONArray))
			throw new SCNotifyRequestProcessingException("pipeline names not found in permstore for Notification ");
	
		JSONArray jsonArr=(JSONArray)object;
		JSONObject jsonObject=(JSONObject)jsonArr.get(0);
		JSONArray  jsonArray=(JSONArray)jsonObject.get(eventValue);
		logger.debug("pipelines Names From Permastore Cache :  "+jsonArray);
		
		int length=jsonArray.size();
		if(length==0)
			throw new SCNotifyRequestProcessingException("pipeline names not found in permstore for given Event ID= "+EVENT_ID);

		for(int i=0;i<length;i++){		
	
				pipelineConfigNameList.add(jsonArray.get(i).toString());
				pipelineRecipienttList.add(ROUTE);
		}				
		logger.debug("Pipe Line List  "+pipelineConfigNameList);
		exchange.getIn().setHeader(RECIPIENT_LIST, pipelineRecipienttList);
		exchange.getIn().setHeader(PIPELINE_LIST, pipelineConfigNameList);

		
	
	}
	
	
	/**
	 * to get PipeLIne names from messhHeader and set to header based on multi cast Index
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception {
		int index = Integer.valueOf(exchange.getProperty(Exchange.MULTICAST_INDEX).toString());
		logger.debug("index value : " + index);
		@SuppressWarnings("unchecked")
		List<String> pipelineConfinameList = (List<String>) exchange.getIn().getHeader(PIPELINE_LIST);
		String pipelineConfiname = pipelineConfinameList.get(index);
		logger.debug("pipeline config name : " + pipelineConfiname +" with Index : "+index);
		exchange.getIn().setHeader(PIPELINE_NAME, pipelineConfiname);

	
	}
	
	
	/**
	 * to get getEventID Id From given notification Xml Data 
	 * @param bodyIn
	 * @return
	 * @throws SCNotifyRequestProcessingException 
	 * @throws UnableToParseServiceRequestException
	 */
	private String getEventNameFromXmlData(String bodyIn) throws SCNotifyRequestProcessingException {
		logger.debug("(.) inside getEventNameFromXmlData method of FetchPipelineName ");
		String eventID="";
		if(bodyIn !=null){
			Document document=generateDocumentFromString(bodyIn);
			NodeList nodeTypeList=document.getElementsByTagName(XML_EVENT_ID_TAG);
			//checking if 
			if(nodeTypeList !=null && nodeTypeList.getLength()>0){
				Node node =nodeTypeList.item(0);								
				eventID=node.getTextContent();
				logger.debug(" EventName === "+eventID);
			}
		}
		return eventID;
	}
	
	/**
	 * to generate the document object from the xml input which is 
	 * of String
	 * 
	 * @param xmlInput : xml input in string format
	 * @return Document : converted xml string into DOcument object 
	 * @throws SCNotifyRequestProcessingException : Exception in converting xml string into document object
	 */
	private Document generateDocumentFromString(String xmlInput) throws SCNotifyRequestProcessingException  {
		logger.debug(".generateDocumentFromString method of FetchPipelineName");
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document xmlDocument;
		try {
			builder = builderFactory.newDocumentBuilder();
			try {
				xmlDocument = builder.parse(new ByteArrayInputStream(xmlInput.getBytes(NotificationMasterConstants.URL_ENCODE_TYPE)));
			} catch (SAXException | IOException e) {
				throw new SCNotifyRequestProcessingException("Unable to parse the xmlString into document..", e);
			}
		} catch (ParserConfigurationException e) {
			throw new SCNotifyRequestProcessingException("Unable to initiate the document builder..", e);
		}		
		return xmlDocument;
	}// ..end of method generateDocumentFromString
	
	
	/**
	 * This is to get parmastore cache data from mesh header
	 * @param searchString : cache key
	 * @param parmaConfigname : parmastore config key
	 * @param meshHeader : MeshHeader Object
	 * @return Object
	 */
	public Object getPipelineNamesFromCacheData(String searchString,String parmaConfigname,MeshHeader meshHeader){
		logger.debug(".getPipelineCacheData method of FetchPipelineName Class");
		Map<String,Object> permaCacheObjectInMap=meshHeader.getPermadata();
		logger.debug("permaCacheObjectInMap : "+permaCacheObjectInMap);
		Map<String,Object> permaCacheObject=(Map<String, Object>)permaCacheObjectInMap.get(parmaConfigname.trim());	
		logger.debug("permaCacheObject : "+permaCacheObject);
		Object object=permaCacheObject.get(searchString.trim());
		logger.debug("object : "+object);
		return object;
	}
	
}