package com.key2act.sac.servicerequest.bean;

import static com.key2act.sac.servicerequest.ServiceRequestConstant.*;
import static com.key2act.sac.util.SACConstant.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.camel.Exchange;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.schema.Table;
import org.json.JSONException;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.datastax.driver.core.Cluster;
import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.eventframework.abstractbean.util.CassandraClusterException;
import com.getusroi.eventframework.abstractbean.util.CassandraConnectionException;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.mesh.generic.UnableToLoadPropertiesException;
import com.key2act.sac.notification.service.SCNotifySendingResponseException;
import com.key2act.sac.notification.util.NotificationMasterConstants;
import com.key2act.sac.servicerequest.DateFormattingParsingException;
import com.key2act.sac.servicerequest.IServiceRequest;
import com.key2act.sac.servicerequest.InvalidRequestException;
import com.key2act.sac.servicerequest.SendDataToServiceChannelException;
import com.key2act.sac.servicerequest.ServiceRequestDataComparisionException;
import com.key2act.sac.servicerequest.ServiceRequestPermastoreProcessingException;
import com.key2act.sac.servicerequest.UnableToParseRequestJsonException;
import com.key2act.sac.servicerequest.impl.ServiceRequestImpl;
import com.key2act.sac.util.ServiceRequestUtil;
import com.key2act.sac.util.UnableToConvertJsonServiceRequestToXML;
import com.key2act.sac.util.UnableToParseServiceRequestException;

public class ServiceRequestProcessBean extends AbstractCassandraBean {
	final Logger logger = LoggerFactory.getLogger(ServiceRequestProcessBean.class);

	/**
	 * This method is used to check the format in which data coming convert into
	 * xml format
	 * 
	 * @param exchange
	 *            : Camel Exchange object
	 * @throws UnableToConvertJsonServiceRequestToXML
	 * @throws JSONException
	 */
	public void convertServiceRequestDataInXMLFormate(Exchange exchange) throws UnableToConvertJsonServiceRequestToXML{
		logger.debug(".convertServiceRequestDataInXMLFormate method of ServiceRequestProcessBean");
		ServiceRequestUtil serviceRequestUtil = new ServiceRequestUtil();		
			serviceRequestUtil.checkAndConvertRequestToXML(exchange);		
	}// end of method checkIncomingDataFormate

	/**
	 * This method is used to identify request came from which source and decide
	 * which pipeline to be called for converting request to internal message
	 * 
	 * @param exchange
	 *            : Camel Exchange object
	 * @throws ServiceRequestProcessBeanException
	 */
	public void identifySourceAndProcess(Exchange exchange) throws ServiceRequestProcessBeanException {
		logger.debug(".identifySourceAndProcess method of ServiceRequestProcessBean");
		String bodyIn = exchange.getIn().getBody(String.class);
		ServiceRequestUtil serviceRequestUtil = new ServiceRequestUtil();
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		IServiceRequest serviceRequest = new ServiceRequestImpl();
		try {
			Document document = serviceRequestUtil.generateDocumentFromString(bodyIn);
			if (document != null) {
				// get the root element from document object
				Element root = document.getDocumentElement();
				logger.debug("root element name : " + root.getNodeName());
				Properties properties = ServiceRequestUtil.getPropertyObject();
				if (properties != null) {
					// get the sourcetype from property file by passing the root
					// elemnt
					String source = properties.getProperty(root.getNodeName().trim());
					logger.debug("source type : " + source);
					if (source != null && source.length() > 0 && !(source.isEmpty())) {
						try {
							// get the name of pipeline config name to be loaded
							// by passing sorce type as key from permastore
							String integrationPipelineName = serviceRequest.getValueFromPermastoreCacheByPassingKey(
									source.trim(), SOURCETYPE_PIPELINECONFIG_NAME_MAPPING_KEY, meshHeader);
							exchange.getIn().setHeader(PIPELINE_LOADED_KEY, integrationPipelineName.trim());
						} catch (ServiceRequestPermastoreProcessingException e) {
							throw new ServiceRequestProcessBeanException("error message : " + e.getMessage());
						}
					}
				} else {
					throw new ServiceRequestProcessBeanException("Unable to load propertyfile from class path - "
							+ SOURCE_IDENTIFY_FILE + " therefore the value is null");
				}
			} else {
				throw new ServiceRequestProcessBeanException("Document object is null for the xml : " + bodyIn);
			}
		} catch (UnableToParseServiceRequestException e) {
			throw new ServiceRequestProcessBeanException("error message : " + e.getMessage());
		}

	}// end of method identifySourceAndProcess

	/**
	 * This method is used to identify the work order call type (WONEW|WOUPDATE)
	 * and call its respective routes
	 * 
	 * @param exchange
	 *            : Camel Exchange
	 * @throws ServiceRequestProcessBeanException
	 */
	public void identifyWorkOrderType(Exchange exchange) throws ServiceRequestProcessBeanException {
		logger.debug(".identifyWorkOrderType method of ServiceRequestProcessBean");
		String bodyIn = exchange.getIn().getBody(String.class);
		ServiceRequestUtil serviceRequestUtil = new ServiceRequestUtil();
		String workOrderTypeValue;
		try {
			Document document = serviceRequestUtil.generateDocumentFromString(bodyIn);
			NodeList nodeTypeList = document.getElementsByTagName(REQUESTDETAIL_TYPE_KEY);
			// checking if work order status is available or not
			if (nodeTypeList != null && nodeTypeList.getLength() > 0) {
				Element element = (Element) nodeTypeList.item(0);
				workOrderTypeValue = element.getAttribute(CALL_STATUS_XML_ELEMENT);
				// workOrderTypeValue=node.getTextContent();
				logger.debug("textcontext type node : " + workOrderTypeValue);
				if (workOrderTypeValue.trim().equalsIgnoreCase(WONEW_CALL_TYPE_KEY))
					exchange.getIn().setHeader(PIPELINE_LOADED_KEY, WONEW_ROUTE_KEY);
				else if (workOrderTypeValue.trim().equalsIgnoreCase(WOUPDATE_CALL_TYPE_KEY))
					exchange.getIn().setHeader(PIPELINE_LOADED_KEY, WOUPDATE_ROUTE_KEY);
				else
					throw new ServiceRequestProcessBeanException("Unrecognizable work order call type : "
							+ workOrderTypeValue + " only supported work order type is WONEW|WOUPDATE");
			}
		} catch (UnableToParseServiceRequestException e) {
			throw new ServiceRequestProcessBeanException("error message : " + e.getMessage());
		}

	}// end of method identifyWorkOrderType

	/**
	 * This method is used to process the work order new sevice request
	 * 
	 * @param exchange
	 *            : Camel Exchange Object
	 * @throws ServiceRequestPermastoreProcessingException
	 * @throws ServiceRequestProcessBeanException
	 */
	public void processNewWorkOrder(Exchange exchange)
			throws ServiceRequestPermastoreProcessingException, ServiceRequestProcessBeanException {
		logger.debug(".processNewWorkOrder method of ServiceRequestProcessBean");
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		String bodyIn = exchange.getIn().getBody(String.class);
		IServiceRequest sacService = new ServiceRequestImpl();
		ServiceRequestUtil serviceRequestUtil = new ServiceRequestUtil();
		Document document;
		try {
			document = serviceRequestUtil.generateDocumentFromString(bodyIn);
			NodeList nodeStatusList = document.getElementsByTagName(REQUESTDETAIL_WOSTATUS_TYPE_KEY);
			String workOrderStatusValue = null;
			// checking if work order status is available or not
			if (nodeStatusList != null && nodeStatusList.getLength() > 0) {
				Node node = nodeStatusList.item(0);
				workOrderStatusValue = node.getTextContent();
				logger.debug("textcontext status node : " + workOrderStatusValue);
			}
			String value = sacService.serviceChanelXMLAndKey2ActStatusMapping(workOrderStatusValue.trim(),
					WONEW_CALL_TYPE_KEY, SERVICECHANEL_KEY2ACT_STATUS_MAPPING_KEY, meshHeader);
			Map<String, Object> serviceRequestDataValue = meshHeader.getServiceRequestData();
			serviceRequestDataValue.put(SERVICE_REQUEST_NEW_INTERNAL_STATUS_KEY, value.trim());
			value = sacService.internalServiceAndPipelineMapping(value, INTERNALEVENT_PIPELINEHANDLER_MAPPING_KEY,
					meshHeader);
			List<String> pipelineNameToBeLoaded = new ArrayList<>();
			pipelineNameToBeLoaded.add(value);
			exchange.getIn().setHeader(PIPELINE_LOADED_LIST_KEY, pipelineNameToBeLoaded);
			exchange.getIn().setHeader(PIPELINE_LOADED_COUNTER_KEY, pipelineNameToBeLoaded.size());
			logger.debug("integration pipeline to be called for new work order : " + value
					+ ", no. of pipeline to be loaded : " + pipelineNameToBeLoaded.size());
		} catch (UnableToParseServiceRequestException e) {
			throw new ServiceRequestProcessBeanException("error message : " + e.getMessage());
		}

	}// end of method processNewWorkOrder

	/**
	 * This method is used to process the work order update service request
	 * 
	 * @param exchange
	 *            : Camel Exchange Object
	 * @throws ServiceRequestProcessBeanException
	 */
	public void processUpdatedWorkOrder(Exchange exchange) throws ServiceRequestProcessBeanException {
		logger.debug(".processUpdatedWorkOrder method of ServiceRequestProcessBean");
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		ServiceRequestUtil serviceRequestUtil = new ServiceRequestUtil();
		String bodyIn = exchange.getIn().getBody(String.class);
		Document document;
		try {
			document = serviceRequestUtil.generateDocumentFromString(bodyIn);
			IServiceRequest serviceRequestImpl = new ServiceRequestImpl();
			List<String> updatePipelineList = new ArrayList<>();
			List<String> eventsForLoadingUpdatePipeline = CompareServiceRequestDataChanged(document, exchange);
			String serviceRequestInternalEventKey = null;
			try {
				for (String internaleventskey : eventsForLoadingUpdatePipeline) {
					if (internaleventskey.contains("ServiceRequest:")) {
						serviceRequestInternalEventKey = internaleventskey;
					} else {
						serviceRequestInternalEventKey = "ServiceRequest:" + internaleventskey.trim();
					}
					String pipelineLoadingName = serviceRequestImpl.internalServiceAndPipelineMapping(
							serviceRequestInternalEventKey, INTERNALEVENT_PIPELINEHANDLER_MAPPING_KEY, meshHeader);
					updatePipelineList.add(pipelineLoadingName);
				}
				logger.debug("list of pipeline to be called : " + updatePipelineList);
				exchange.getIn().setHeader(PIPELINE_LOADED_LIST_KEY, updatePipelineList);
				exchange.getIn().setHeader(PIPELINE_LOADED_COUNTER_KEY, updatePipelineList.size());
			} catch (ServiceRequestPermastoreProcessingException e) {
				throw new ServiceRequestProcessBeanException("error message : " + e.getMessage());

			}

		} catch (UnableToParseServiceRequestException e1) {
			throw new ServiceRequestProcessBeanException("error message : " + e1.getMessage());
		}

	}// end of method processUpdatedWorkOrder

	/**
	 * This method is used to compare the data changed in the service request by
	 * comparing from both db and note coming in request
	 * 
	 * @param document
	 *            : Document Object
	 * @param exchange
	 *            : Camel Exchange Object
	 * @return List<String> : List of data changed in the service request
	 * @throws ServiceRequestProcessBeanException
	 */
	private List<String> CompareServiceRequestDataChanged(Document document, Exchange exchange)
			throws ServiceRequestProcessBeanException {
		logger.debug(".CompareRequestDataChangesFromDB method of ServiceRequestImpl");
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		List<String> eventsForLoadingUpdatePipeline = null;
		IServiceRequest serviceRequestImpl = new ServiceRequestImpl();
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
			DataContext dataContext = getDataContextForCassandraByCluster(cluster, CASSANDRA_TABLE_KEYSPACE);
			Table table = getTableForDataContext(dataContext, "servicerequest");
			try {
				eventsForLoadingUpdatePipeline = serviceRequestImpl.getServiceRequestDataChanged(document, dataContext,
						table, "gap");
				Map<String, Object> serviceRequestDataValue = meshHeader.getServiceRequestData();
				serviceRequestDataValue.put(SERVICE_REQUEST_NEW_INTERNAL_STATUS_KEY,
						SERVICE_REQUEST_STATUS_UPDATED_KEY);
				if (eventsForLoadingUpdatePipeline.contains(STATUS_CHANGED_EVENT_KEY)) {
					getStatusChangedEvent(document, eventsForLoadingUpdatePipeline, WOUPDATE_CALL_TYPE_KEY, meshHeader);
				}
			} catch (ServiceRequestDataComparisionException e) {
				throw new ServiceRequestProcessBeanException("error message : " + e.getMessage());
			}
		} catch (CassandraClusterException e) {
			throw new ServiceRequestProcessBeanException("error message : " + e.getMessage());
		}

		return eventsForLoadingUpdatePipeline;
	}// end of method

	@Override
	protected void processBean(Exchange exchange) throws Exception {
		int index = Integer.valueOf(exchange.getProperty(Exchange.LOOP_INDEX).toString());
		logger.debug("index value : " + index);
		List<String> pipelineConfinameList = (List<String>) exchange.getIn().getHeader(PIPELINE_LOADED_LIST_KEY);
		String pipelineConfiname = pipelineConfinameList.get(index);
		logger.debug("pipeline config name : " + pipelineConfiname);
		exchange.getIn().setHeader(PIPELINE_LOADED_KEY, pipelineConfiname);

	}

	/**
	 * This method is used to find out if status is changed then what is the new
	 * changed status and accordingly changed the event name for it
	 * 
	 * @param document
	 *            : Documnt Object
	 * @param eventsForLoadingUpdatePipeline
	 *            : list of all the events
	 * @param workorderType
	 *            : work order type in STring
	 * @param meshHeader
	 *            : Mesh Header Object
	 * @throws ServiceRequestProcessBeanException
	 */
	private void getStatusChangedEvent(Document document, List<String> eventsForLoadingUpdatePipeline,
			String workorderType, MeshHeader meshHeader) throws ServiceRequestProcessBeanException {
		logger.debug(".getStatusChangedEvent method of ServiceRequestImpl");
		NodeList nodeList = document.getElementsByTagName(REQUESTDETAIL_WOSTATUS_TYPE_KEY);
		IServiceRequest serviceRequest = new ServiceRequestImpl();
		if (nodeList != null && nodeList.getLength() > 0) {
			Node node = nodeList.item(0);
			String workOrderStatusValue = node.getTextContent();
			logger.debug("textcontext status node : " + workOrderStatusValue);
			try {
				logger.debug("status is : " + workOrderStatusValue + ", call type : " + workorderType
						+ ", pipeline to be called + " + SERVICECHANEL_KEY2ACT_STATUS_MAPPING_KEY);
				String internalevent = serviceRequest.serviceChanelXMLAndKey2ActStatusMapping(workOrderStatusValue,
						workorderType, SERVICECHANEL_KEY2ACT_STATUS_MAPPING_KEY, meshHeader);
				eventsForLoadingUpdatePipeline.remove(STATUS_CHANGED_EVENT_KEY);
				eventsForLoadingUpdatePipeline.add(internalevent);
				Map<String, Object> serviceRequestDataValue = meshHeader.getServiceRequestData();
				serviceRequestDataValue.put(SERVICE_REQUEST_NEW_INTERNAL_STATUS_KEY, internalevent.trim());
			} catch (ServiceRequestPermastoreProcessingException e) {
				throw new ServiceRequestProcessBeanException("error message : " + e.getMessage());
			}
		}
	}// end of method getStatusChangedEvent

	/**
	 * Method for creating call of all the open status ServiceRequest from the
	 * servicerequest DB
	 * 
	 * @throws UnableToParseRequestJsonException
	 * @throws SendDataToServiceChannelException 
	 * 
	 */
	public void createCallFromOpenServiceRequest(Exchange exchange) throws UnableToParseRequestJsonException, SendDataToServiceChannelException {
		String requestInput = exchange.getIn().getBody(String.class);
		org.json.JSONArray requestJsonArray;
		org.json.JSONObject requestJsonObj;

		try {
			requestJsonObj = new org.json.JSONObject(requestInput);
			org.json.JSONObject createCallJsonObj = new org.json.JSONObject();

			if (requestJsonObj.has(MeshHeaderConstant.DATA_KEY)) {
				requestJsonArray = requestJsonObj.getJSONArray(MeshHeaderConstant.DATA_KEY);
				createCallJsonObj = (org.json.JSONObject) requestJsonArray.get(0);
			} else {
				createCallJsonObj = (org.json.JSONObject) requestJsonObj.get(CREATECALL_DATA_KEY);
			}
			exchange.getIn().setBody("CREATECALL : " + createCallJsonObj.toString());
			String jsonToXML = XML.toString(createCallJsonObj);
			sendRequestToCreateCall(jsonToXML);
		} catch (JSONException e) {
			throw new UnableToParseRequestJsonException("The request which has been passed is not valid");
		}
	}

	/**
	 * method to fetch the request for create call from serviceRequests which
	 * are NEW, depending on whether xml or json request is passed, then it is
	 * passing the request as json object and passing it into
	 * getDBEntitiesAndlistNewServices method.
	 * 
	 * @throws UnableToParseRequestJsonException
	 * @throws InvalidRequestException
	 * @throws CassandraClusterException
	 * @throws DateFormattingParsingException
	 */
	public void fetchJsonRequestForListingNewServiceRequest(Exchange exchange) throws UnableToParseRequestJsonException,
			DateFormattingParsingException, CassandraClusterException, InvalidRequestException {
		String requestInput = exchange.getIn().getBody(String.class);
		org.json.JSONArray requestJsonArray;
		org.json.JSONObject requestJsonObj;
		try {
			requestJsonObj = new org.json.JSONObject(requestInput);
			org.json.JSONObject listNewServiceRequestObj = new org.json.JSONObject();
			if (requestJsonObj.has(MeshHeaderConstant.DATA_KEY)) {
				requestJsonArray = requestJsonObj.getJSONArray(MeshHeaderConstant.DATA_KEY);
				requestJsonObj = (org.json.JSONObject) requestJsonArray.get(0);
				listNewServiceRequestObj = (org.json.JSONObject) requestJsonObj.get(LIST_NEW_SERVICEREQUEST_DATA_KEY);
			} else {
				listNewServiceRequestObj = (org.json.JSONObject) requestJsonObj.get(LIST_NEW_SERVICEREQUEST_DATA_KEY);
			}
			ServiceRequestImpl serviceRequestImpl = new ServiceRequestImpl();
			Cluster cluster = getCassandraCluster();
			String keySpace = CASSANDRA_TABLE_KEYSPACE;
			DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
			Table table = getTableForDataContext(dataContext, SERVICE_REQUEST_TABLENAME);
			serviceRequestImpl.getDBEntitiesAndlistNewServices(exchange, listNewServiceRequestObj,cluster,table, dataContext);
		} catch (JSONException e) {
			throw new UnableToParseRequestJsonException("The request which has been passed is not valid");
		}
	}

	/**
	 * #TODO Temporary method to send the string to dummy webService
	 * method to use the generated string as url parameters for HTTP POST METHOD
	 * to the service channel
	 * 
	 * @throws SCNotifyResponsePostException
	 * 
	 */
	public void sendRequestToCreateCall(String request) throws SendDataToServiceChannelException {
		logger.debug("(.) inside sendRequestToCreateCall ");
		BufferedReader in = null;
		DataOutputStream wr = null;
		try {
			URL obj = new URL("http://localhost:8080/SacWebService/sac/response");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// add reuqest header
			con.setRequestMethod(NotificationMasterConstants.POST_METHOD);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			String urlParameters = request;
			// Send post request
			con.setDoOutput(true);
			wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			int responseCode = con.getResponseCode();
			logger.debug("responseCode : " + responseCode);
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			logger.debug("\nResponse : " + response.toString());
		} catch (IOException e) {
			throw new SendDataToServiceChannelException("Unable to send  Reponse to   Service Channel   web service ",
					e);
		} finally {
			try {
				wr.flush();
				wr.close();
				in.close();
			} catch (IOException e) {
				throw new SendDataToServiceChannelException(
						"Unable to close InputStream connection of of Service Chanel web service");

			} // end of try

		} // end of finally
	}

	/**
	 * method to fetch the request for edit serviceRequests which are NEW/OPEN,
	 * depending on the request format passed, then the jsondata is passed to
	 * getDBEntitiesAndUpdate method.
	 * 
	 * @throws UnableToParseRequestJsonException
	 * @throws CassandraConnectionException
	 * @throws InvalidRequestException
	 */
	public void fetchJsonRequestForEditServiceRequest(Exchange exchange)
			throws UnableToParseRequestJsonException, InvalidRequestException, CassandraConnectionException {
		String requestInput = exchange.getIn().getBody(String.class);
		org.json.JSONArray requestJsonArray;
		org.json.JSONObject requestJsonObj;
		try {
			requestJsonObj = new org.json.JSONObject(requestInput);
			org.json.JSONObject editNewServiceRequestObj = new org.json.JSONObject();

			if (requestJsonObj.has(MeshHeaderConstant.DATA_KEY)) {
				requestJsonArray = requestJsonObj.getJSONArray(MeshHeaderConstant.DATA_KEY);
				requestJsonObj = (org.json.JSONObject) requestJsonArray.get(0);
			}
			Connection connection = getCassandraConnection();
			UpdateableDataContext dataContext = getUpdateableDataContextForCassandra(connection);
			Table table = getTableForDataContext(dataContext, SERVICE_REQUEST_TABLENAME);
			ServiceRequestImpl serviceRequestImpl = new ServiceRequestImpl();
			serviceRequestImpl.getDBEntitiesAndUpdate(exchange, requestJsonObj,table, dataContext);
			
		} catch (JSONException e) {
			throw new UnableToParseRequestJsonException("The request which has been passed is not valid");
		}
	}
	
	/**
	 * This method is used to send the response code and message
	 * @param exchange : Camel Exchange Object
	 * @throws ServiceRequestProcessBeanException 
	 */
	public void sendResponseCode(Exchange exchange) throws ServiceRequestProcessBeanException{
		logger.debug(".sendResponseCode method in ServiceRequestProcessBean");
		ServiceRequestUtil serviceRequestUtil=new ServiceRequestUtil();
		String bodyIn = exchange.getIn().getBody(String.class);
		try {
			Document document = serviceRequestUtil.generateDocumentFromString(bodyIn);
			NodeList nodeList=document.getElementsByTagName(REQUESTDETAIL_TYPE_KEY);
			// checking if work order type is available or not
			if (nodeList != null && nodeList.getLength() > 0) {
				Node node = nodeList.item(0);
				NamedNodeMap attributeNodeMap = node.getAttributes();
				Node workorderTypeNode=attributeNodeMap.getNamedItem(REQUESTDETAIL_STATUS_TYPE_KEY);
				logger.debug("Work Order Type (NEW|UPDATE) " + workorderTypeNode.getNodeValue());
				try {
					serviceRequestUtil.setResponseCode(workorderTypeNode.getNodeValue().trim(),exchange);
				} catch (DOMException | UnableToLoadPropertiesException e) {
					throw new ServiceRequestProcessBeanException("Unable to set the response code and message",e);
				}
			}			
		} catch (UnableToParseServiceRequestException e) {
			throw new ServiceRequestProcessBeanException(e.getMessage());
		}		
	}//end of method sendResponseCode
}