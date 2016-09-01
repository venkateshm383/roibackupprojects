package com.key2act.sac.servicerequest.impl;

import static com.key2act.sac.servicerequest.ServiceRequestConstant.ATTR_XML_ELEMENT;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.CALL_TYPE_XML_ELEMENT;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.INTERNAL_STATUS_KEY;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.NOTE_NTE_CHANGED_CRITERIA_KEY;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.NOTE_PRIORITY_CHANGED_CRITERIA_KEY;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.NOTE_SCHEDULE_DATE_CHANGED_CRITERIA_KEY;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.NOTE_STATUS_CHANGED_CRITERIA_KEY;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.NOTE_TEXT_SPLIT_CRITERIA_KEY;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.PROBLEM_AREA_DB_COLUMN_KEY;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.PROBLEM_AREA_XML_ELEMENT;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.PROBLEM_DESCRIPTION_DB_COLUMN_KEY;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.PROBLEM_DESCRIPTION_XML_ELEMENT;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.PURCHASEORDER_NUMBER_XML_ELEMENT;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.REQUESTBY_XML_ELEMENT;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.REQUEST_CREATED_DATE_DB_COLUMN_KEY;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.SERVICE_REQUEST_DATE_AMERICAN_FORMAT;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.SERVICE_REQUEST_DATE_FORMAT_IN_DB;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.SERVICE_REQUEST_TABLENAME;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.SERVICE_REQUEST_TIMEZONE_AMERICAN_FORMAT;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.SOURCE_CUST_ID_DB_COLUMN_KEY;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.SOURCE_CUST_LOCATION_DB_COLUMN_KEY;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.SOURCE_REQUEST_ID_DB_COLUMN_KEY;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.SOURCE_STATUS_DB_COLUMN_KEY;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.SOURCE_TYPE;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.TENANTID;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.TRADE;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.TRANSACTION_NUMBER_XML_ELEMENT;
import static com.key2act.sac.servicerequest.ServiceRequestConstant.WONUMBER_XML_ELEMENT;
import static com.key2act.sac.util.SACConstant.CALLER_CHANGED_EVENT_KEY;
import static com.key2act.sac.util.SACConstant.CASSANDRA_TABLE_KEYSPACE;
import static com.key2act.sac.util.SACConstant.NOTE_ADDED_EVENT_KEY;
import static com.key2act.sac.util.SACConstant.NTECHANGED_CHANGED_EVENT_KEY;
import static com.key2act.sac.util.SACConstant.PRIORITY_CHANGED_EVENT_KEY;
import static com.key2act.sac.util.SACConstant.PROBLEM_DESCIPTION_CHANGED_EVENT_KEY;
import static com.key2act.sac.util.SACConstant.PROVIDERNUMBER_CHANGED_EVENT_KEY;
import static com.key2act.sac.util.SACConstant.PURCHASEORDER_NUMBER_CHANGED_EVENT_KEY;
import static com.key2act.sac.util.SACConstant.SCHEDULE_DTM_CHANGED_EVENT_KEY;
import static com.key2act.sac.util.SACConstant.STATUS_CHANGED_EVENT_KEY;
import static com.key2act.sac.util.SACConstant.TRADE_CHANGED_EVENT_KEY;
import static com.key2act.sac.util.SACConstant.WONNUMBER_CHANGED_EVENT_KEY;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.camel.Exchange;
import org.apache.camel.component.restlet.RestletConstants;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.UpdateScript;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.query.FilterItem;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.Table;
import org.apache.metamodel.update.Update;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.datastax.driver.core.Cluster;
import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.eventframework.abstractbean.util.CassandraClusterException;
import com.getusroi.eventframework.abstractbean.util.CassandraConnectionException;
import com.getusroi.mesh.MeshHeader;
import com.key2act.sac.servicerequest.DateFormattingParsingException;
import com.key2act.sac.servicerequest.IServiceRequest;
import com.key2act.sac.servicerequest.InvalidRequestException;
import com.key2act.sac.servicerequest.ServiceRequestDataComparisionException;
import com.key2act.sac.servicerequest.ServiceRequestPermastoreProcessingException;
import com.key2act.sac.servicerequest.UnableToParseRequestJsonException;
import com.key2act.sac.util.ServiceRequestUtil;

public class ServiceRequestImpl implements IServiceRequest {
	Logger logger = LoggerFactory.getLogger(ServiceRequestImpl.class);

	/**
	 * This method is used to map service channel XML status value with Service
	 * channel internal status
	 * 
	 * @param wostatus
	 *            : service request status in String
	 * @param wotype
	 *            : service request call type value in String
	 * @param parmaconfigname
	 *            : permastore config name
	 * @param meshHeader
	 *            : meshHeader Object
	 * @return return the primary status
	 * @exception ServiceRequestPermastoreProcessingException
	 */
	public String serviceChanelXMLMapping(String wostatus, String wotype, String parmaConfigname, MeshHeader meshHeader)
			throws ServiceRequestPermastoreProcessingException {
		logger.debug("checkServiceChanelXMLMapping method of ServiceRequestImpl");
		String returnString = null;
		if ((wostatus != null && !(wostatus.isEmpty()) && wostatus.length() > 0)
				&& (wostatus != null && !(wotype.isEmpty()) && wotype.length() > 0)
				&& (parmaConfigname != null && !(parmaConfigname.isEmpty()) && parmaConfigname.length() > 0)) {
			ServiceRequestUtil sacUtil = new ServiceRequestUtil();
			Object object = sacUtil.getPipelineCacheData(wostatus, parmaConfigname, meshHeader);
			if (object != null) {
				JSONObject jsonObject = (JSONObject) object;
				Set<String> allJsonKeySet = jsonObject.keySet();
				for (String key : allJsonKeySet) {
					String jsonKeyValue = (String) jsonObject.get(key);
					if (jsonKeyValue != null && jsonKeyValue.length() > 0 && !(jsonKeyValue.isEmpty())) {
						if (jsonKeyValue.equalsIgnoreCase(wotype)) {
							returnString = (String) jsonObject.get(wotype.trim());
							return returnString;
						} // end of if(jsonKeyValue.equalsIgnoreCase(wotype))
					}
				} // end of for
			} else {
				throw new ServiceRequestPermastoreProcessingException(
						"Cache data for pemastore is null for permastore configname : " + parmaConfigname
								+ " and key : " + wostatus);

			}
		} else {
			throw new ServiceRequestPermastoreProcessingException(
					"Unable to get the pipeline name due to null value of wostatus: " + wostatus + " or wotype : "
							+ wotype + " or parmaconfigname : " + parmaConfigname);
		}
		return returnString;

	}// end of method

	/**
	 * This method is used to map service channel XML status value with key2act
	 * status
	 * 
	 * @param wostatus
	 *            : service request status in String
	 * @param wotype
	 *            : service request call type value in String
	 * @param parmaconfigname
	 *            : permastore config name
	 * @param meshHeader
	 *            : meshHeader Object
	 * @return return the internal status
	 * @exception ServiceRequestPermastoreProcessingException
	 */
	public String serviceChanelXMLAndKey2ActStatusMapping(String wostatus, String wotype, String parmaConfigname,
			MeshHeader meshHeader) throws ServiceRequestPermastoreProcessingException {
		logger.debug("checkServiceChanelXMLAndKey2ActStatusMapping method of ServiceRequestImpl");
		String returnString = null;
		if ((wostatus != null && !(wostatus.isEmpty()) && wostatus.length() > 0)
				&& (wostatus != null && !(wotype.isEmpty()) && wotype.length() > 0)
				&& (parmaConfigname != null && !(parmaConfigname.isEmpty()) && parmaConfigname.length() > 0)) {
			ServiceRequestUtil sacUtil = new ServiceRequestUtil();
			Object object = sacUtil.getPipelineCacheData(wostatus.trim(), parmaConfigname.trim(), meshHeader);
			logger.debug("cache object is : " + object);
			if (object != null) {
				if (object instanceof JSONArray) {
					logger.debug("instance of json array");
					JSONArray jsonArr = (JSONArray) object;
					int length = jsonArr.size();
					for (int i = 0; i <= length - 1; i++) {
						JSONObject jsonObject = (JSONObject) jsonArr.get(i);
						String typevalue = (String) jsonObject.get(CALL_TYPE_XML_ELEMENT);
						if (typevalue.equalsIgnoreCase(wotype.trim())) {
							returnString = (String) jsonObject.get(INTERNAL_STATUS_KEY);
						}
					}
				} else {
					logger.debug("not instance of json array");
					JSONObject jsonObject = (JSONObject) object;
					String typevalue = (String) jsonObject.get(CALL_TYPE_XML_ELEMENT);
					if (typevalue.equalsIgnoreCase(wotype.trim())) {
						returnString = (String) jsonObject.get(INTERNAL_STATUS_KEY);
					}
				}
			} else {
				throw new ServiceRequestPermastoreProcessingException(
						"Cache data for pemastore is null for permastore configname : " + parmaConfigname
								+ " and key : " + wostatus);
			}
		} else {
			throw new ServiceRequestPermastoreProcessingException(
					"Unable to get the pipeline name due to null value of wostatus: " + wostatus + " or wotype : "
							+ wotype + " or parmaconfigname : " + parmaConfigname);
		}
		logger.debug("internal status : " + returnString);
		return returnString;
	}// end of method

	/**
	 * This method is used to map internal service status value with pipeline
	 * config to be called
	 * 
	 * @param searchKey
	 *            : search key in String
	 * @param parmaconfigname
	 *            : permastore config name
	 * @param meshHeader
	 *            : meshHeader Object
	 * @return integration pipeline config name
	 * @exception ServiceRequestPermastoreProcessingException
	 */
	public String internalServiceAndPipelineMapping(String searchKey, String parmaConfigname, MeshHeader meshHeader)
			throws ServiceRequestPermastoreProcessingException {
		logger.debug("checkInternalServiceAndPipelineMapping method of ServiceRequestImpl");
		String returnString = null;
		if ((searchKey != null && !(searchKey.isEmpty()) && searchKey.length() > 0)
				&& (parmaConfigname != null && !(parmaConfigname.isEmpty()) && parmaConfigname.length() > 0)) {
			ServiceRequestUtil sacUtil = new ServiceRequestUtil();
			Object object = sacUtil.getPipelineCacheData(searchKey, parmaConfigname, meshHeader);
			if (object != null) {
				returnString = (String) object;
				return returnString;
			} else {
				throw new ServiceRequestPermastoreProcessingException(
						"Cache data for pemastore is null for permastore configname : " + parmaConfigname
								+ " and key : " + searchKey);
			}
		} else {
			throw new ServiceRequestPermastoreProcessingException(
					"Unable to get the pipeline name due to null value of searchKey: " + searchKey
							+ " or  parmaconfigname : " + parmaConfigname);
		}
	}// end of method

	/**
	 * This method is used to map internal service status value with pipeline
	 * config to be called
	 * 
	 * @param searchKey
	 *            : search key in String
	 * @param parmaconfigname
	 *            : permastore config name
	 * @param meshHeader
	 *            : meshHeader Object
	 * @return integration pipeline config name
	 * @exception ServiceRequestPermastoreProcessingException
	 */
	public String getValueFromPermastoreCacheByPassingKey(String searchKey, String parmaConfigname,
			MeshHeader meshHeader) throws ServiceRequestPermastoreProcessingException {
		logger.debug("checkInternalServiceAndPipelineMapping method of ServiceRequestImpl");
		String returnString = null;
		if ((searchKey != null && !(searchKey.isEmpty()) && searchKey.length() > 0)
				&& (parmaConfigname != null && !(parmaConfigname.isEmpty()) && parmaConfigname.length() > 0)) {
			ServiceRequestUtil sacUtil = new ServiceRequestUtil();
			Object object = sacUtil.getPipelineCacheData(searchKey, parmaConfigname, meshHeader);
			if (object != null) {
				returnString = (String) object;
				return returnString;
			} else {
				throw new ServiceRequestPermastoreProcessingException(
						"Cache data for pemastore is null for permastore configname : " + parmaConfigname
								+ " and key : " + searchKey);
			}
		} else {
			throw new ServiceRequestPermastoreProcessingException(
					"Unable to get the pipeline name due to null value of searchKey: " + searchKey
							+ " or  parmaconfigname : " + parmaConfigname);
		}
	}// end of method

	@Override
	public List<String> getServiceRequestDataChanged(Document document, DataContext dataContext, Table table,
			String tenant) throws ServiceRequestDataComparisionException {
		logger.debug(".getServiceRequestDataChanged method of ServiceRequestImpl");
		List<String> serviceRequestDataChangedFromDBList = new ArrayList<>();
		List<String> serviceRequestDataChangedFromNoteList = new ArrayList<>();
		NodeList nodeList = document.getElementsByTagName(TRANSACTION_NUMBER_XML_ELEMENT);
		String sourceRequestId = null;
		if (nodeList != null && nodeList.getLength() > 0) {
			Node node = nodeList.item(0);
			sourceRequestId = node.getTextContent();
			logger.debug("textcontext sourceRequestId node : " + sourceRequestId);
			serviceRequestDataChangedFromDBList = compareServiceRequestChangedFromDB(document, dataContext, table,
					tenant, sourceRequestId);
			serviceRequestDataChangedFromNoteList = compareServiceRequestChangedFromNote(document);
			serviceRequestDataChangedFromNoteList.addAll(serviceRequestDataChangedFromDBList);
		}
		logger.debug("complete list of data changed in request : " + serviceRequestDataChangedFromNoteList);
		return serviceRequestDataChangedFromNoteList;
	}

	/**
	 * This method is used to compare the value changed for service request by
	 * comparing with previously store db value. Note: {Problem Description and
	 * trade implementation not yet provided, as no column available for them
	 * 
	 * @param document
	 *            : XML document Object
	 * @param dataContext
	 *            : Apache metamode Data context object
	 * @param table
	 *            : Table Object
	 * @param tenant
	 *            : tenant id in String
	 * @param sourcerequestid
	 *            : sourcerequestid(TR_NUM) in String
	 * @return List<String> : List of events specify which data is changed
	 * @throws ServiceRequestDataComparisionException
	 */
	private List<String> compareServiceRequestChangedFromDB(Document document, DataContext dataContext, Table table,
			String tenant, String sourcerequestid) throws ServiceRequestDataComparisionException {
		logger.debug(".compareServiceRequestChangedFromDB method of ServiceRequestImpl");
		List<String> listOfValueChanged = new ArrayList<>();
		DataSet dataset = dataContext.query().from(table).selectAll().where(TENANTID).eq(tenant)
				.and(SOURCE_REQUEST_ID_DB_COLUMN_KEY).eq(sourcerequestid).execute();
		// check if wo and provider number is same or not
		logger.debug("dataset : " + dataset);
		for (final Row row : dataset) {
			String workorderNumber = (String) row.getValue(16);
			String purchaseorderNumber = (String) row.getValue(8);
			String caller = (String) row.getValue(10);
			String trade = (String) row.getValue(5);
			String problemDescriptiondb = (String) row.getValue(6);
			logger.debug("work order no. : " + workorderNumber + ", purchase order : " + purchaseorderNumber
					+ ", caller : " + caller + ",trade : " + trade + ", problem description : " + problemDescriptiondb);
			// check if wo number is changed
			boolean woNumflag = checkValuesISSameWithDB(WONUMBER_XML_ELEMENT, workorderNumber.trim(), document);
			if (!woNumflag) {
				listOfValueChanged.add(WONNUMBER_CHANGED_EVENT_KEY);
			}
			// check if purchase order number is changed
			boolean poNumflag = checkValuesISSameWithDB(PURCHASEORDER_NUMBER_XML_ELEMENT, purchaseorderNumber.trim(),
					document);
			if (!poNumflag) {
				listOfValueChanged.add(PURCHASEORDER_NUMBER_CHANGED_EVENT_KEY);
			}
			// check if purchase order number is changed
			boolean callerflag = checkValuesISSameWithDB(REQUESTBY_XML_ELEMENT, caller.trim(), document);
			if (!callerflag) {
				listOfValueChanged.add(CALLER_CHANGED_EVENT_KEY);
			}

			// check if trade is changed
			boolean problemArea = checkValuesISSameWithDB(PROBLEM_AREA_XML_ELEMENT, trade.trim(), document);
			if (!problemArea) {
				listOfValueChanged.add(TRADE_CHANGED_EVENT_KEY);
			}

			// check if problem description is changed
			boolean problemDescription = checkValuesISSameWithDB(PROBLEM_DESCRIPTION_XML_ELEMENT,
					problemDescriptiondb.trim(), document);
			if (!problemDescription) {
				listOfValueChanged.add(PROBLEM_DESCIPTION_CHANGED_EVENT_KEY);
			}

		}
		logger.debug("listOfValueChanged from db : " + listOfValueChanged);
		return listOfValueChanged;
	}// end of method compareServiceRequestChangedFromDB

	/**
	 * This method is used to check if value comming in xml is same with value
	 * in db
	 * 
	 * @param elementName
	 *            : xml element name whose value need to be compared with db
	 *            value
	 * @param valueToBeCompared
	 *            : DB value whose value need to be compared with XML element
	 *            value
	 * @param document
	 *            : XML document Object
	 * @return boolean : true if same value,else false
	 * @throws ServiceRequestDataComparisionException
	 */
	private boolean checkValuesISSameWithDB(String elementName, String valueToBeCompared, Document document)
			throws ServiceRequestDataComparisionException {
		logger.debug(".checkValuesISSameWithDB method of ServiceRequestImpl");
		if ((elementName != null && elementName.length() > 0 && !(elementName.isEmpty())) && valueToBeCompared != null
				&& valueToBeCompared.length() > 0 && !(valueToBeCompared.isEmpty())) {
			NodeList nodeList = document.getElementsByTagName(elementName);
			if ((nodeList != null && nodeList.getLength() > 0)) {
				Node node = nodeList.item(0);
				String value = node.getTextContent();
				logger.debug("value :  " + value + " for element: " + elementName);
				if ((value.trim().equalsIgnoreCase(valueToBeCompared))) {
					return true;
				} else {
					return false;
				}
			} else {
				throw new ServiceRequestDataComparisionException(
						"no element is availabe in xml with name : " + elementName);
			}
		} else {
			throw new ServiceRequestDataComparisionException(
					"XML element to be compared with DB value is null element name : " + elementName
							+ ", db value to be compared : " + valueToBeCompared);
		}
	}// end of method checkValuesISSameWithDB

	/**
	 * This method is used to findout the change in service request data from
	 * note
	 * 
	 * @param document
	 *            : Document Object
	 * @return List<String> : list of data changed event
	 */
	private List<String> compareServiceRequestChangedFromNote(Document document) {
		logger.debug(".compareServiceRequestChangedFromDB method of ServiceRequestImpl");
		List<String> listOfValueChanged = new ArrayList<>();
		NodeList attrNodeList = document.getElementsByTagName(ATTR_XML_ELEMENT);
		if (attrNodeList != null && attrNodeList.getLength() > 0) {
			// check if new schedule date provided
			Node attrNode = attrNodeList.item(0);
			String attrValue = attrNode.getTextContent();
			logger.debug("attr element text value : " + attrValue);
			String[] arrayOfAttrValue = attrValue.split(NOTE_TEXT_SPLIT_CRITERIA_KEY);
			logger.debug("array Of Attribute Value split based on WO Edited :" + arrayOfAttrValue + ", size : "
					+ arrayOfAttrValue.length);
			if (arrayOfAttrValue != null && arrayOfAttrValue.length > 0) {
				listOfValueChanged = identifyDataChangedFromNote(arrayOfAttrValue);
				if (attrValue.contains("Provider reassigne")) {
					listOfValueChanged.add(PROVIDERNUMBER_CHANGED_EVENT_KEY);
				} else {
					logger.debug("Nothing is changed just a note is added");
					listOfValueChanged.add(NOTE_ADDED_EVENT_KEY);
				}
			} // end of if(attrValue.contains("Provider reassigne"))
		} // end of if(arrayOfAttrValue!=null && arrayOfAttrValue.length>0)
		logger.debug("listOfValueChanged from db : " + listOfValueChanged);
		return listOfValueChanged;
	}// end of method compareServiceRequestChangedFromNote

	/**
	 * This method is used to findout the change in service request data from
	 * note in "WO EDITED:" format
	 * 
	 * @param arrayOfAttrValue
	 *            : array of string split based on "WO EDITED:" contains changed
	 *            information
	 * @return List<String> : list of data changed event
	 */
	private List<String> identifyDataChangedFromNote(String[] arrayOfAttrValue) {
		logger.debug(".identifyDataChangedFromNote method of ServiceRequestImpl ");
		List<String> listOfValueChanged = new ArrayList<>();
		for (String str : arrayOfAttrValue) {
			if (str.trim().contains(NOTE_PRIORITY_CHANGED_CRITERIA_KEY)) {
				listOfValueChanged.add(PRIORITY_CHANGED_EVENT_KEY);
			} else if (str.trim().contains(NOTE_SCHEDULE_DATE_CHANGED_CRITERIA_KEY)) {
				listOfValueChanged.add(SCHEDULE_DTM_CHANGED_EVENT_KEY);
			} else if (str.trim().contains(NOTE_NTE_CHANGED_CRITERIA_KEY)) {
				listOfValueChanged.add(NTECHANGED_CHANGED_EVENT_KEY);
			} else if (str.trim().contains(NOTE_STATUS_CHANGED_CRITERIA_KEY)) {
				listOfValueChanged.add(STATUS_CHANGED_EVENT_KEY);
			}
		}
		return listOfValueChanged;
	}// end of method identifyDataChangedFromNote

	/**
	 * method to get the database entities like column, cluster etc. build the
	 * query and pass it to listNewServiceRequestAsArrayInExchange
	 * 
	 * @param exchange
	 * @throws UnableToParseRequestJsonException
	 * @throws CassandraClusterException
	 * @throws DateFormattingParsingException
	 * @throws InvalidRequestException
	 * @throws ParseException
	 */
	public void getDBEntitiesAndlistNewServices(Exchange exchange, org.json.JSONObject listNewServiceRequestObj, Cluster cluster, Table table, DataContext dataContext)
			throws DateFormattingParsingException, UnableToParseRequestJsonException, CassandraClusterException,
			InvalidRequestException {
		
		
		Column tenantID = table.getColumnByName(TENANTID);
		Column source = table.getColumnByName(SOURCE_TYPE);
		Column control_Number = table.getColumnByName(SOURCE_REQUEST_ID_DB_COLUMN_KEY);
		Column requestDate = table.getColumnByName(REQUEST_CREATED_DATE_DB_COLUMN_KEY);
		Column trade = table.getColumnByName(PROBLEM_AREA_DB_COLUMN_KEY);
		Column customer = table.getColumnByName(SOURCE_CUST_ID_DB_COLUMN_KEY);
		Column location = table.getColumnByName(SOURCE_CUST_LOCATION_DB_COLUMN_KEY);
		Column problemdescription = table.getColumnByName(PROBLEM_DESCRIPTION_DB_COLUMN_KEY);
		Column sourceStatus = table.getColumnByName(SOURCE_STATUS_DB_COLUMN_KEY);
		DataSet dataset = dataSetQueryDeciderOnRequestType(exchange, listNewServiceRequestObj, dataContext, table,
				tenantID, source, control_Number, requestDate, trade, problemdescription, customer, location,
				sourceStatus);
		listNewServiceRequestAsArrayInExchange(dataset, exchange, tenantID, source, control_Number, requestDate, trade,
				problemdescription, customer, location, sourceStatus);
	}// ..end of method getDBEntitiesAndlistNewServices

	/**
	 * method to check which query to build based on the input request
	 * 
	 * @throws InvalidRequestException
	 * @throws UnableToParseRequestJsonException
	 */
	public DataSet dataSetQueryDeciderOnRequestType(Exchange exchange, org.json.JSONObject listNewServiceRequestObj,
			DataContext dataContext, Table table, Column tenantID, Column source, Column control_Number,
			Column requestDate, Column trade, Column problemdescription, Column customer, Column location,
			Column sourceStatus) throws InvalidRequestException, UnableToParseRequestJsonException {
		DataSet dataset = null;
		try {
			if (listNewServiceRequestObj.has(SOURCE_TYPE) && listNewServiceRequestObj.has(TENANTID)) {
				String tenant = (String) listNewServiceRequestObj.get(TENANTID);
				String sourceType = (String) listNewServiceRequestObj.get(SOURCE_TYPE);
				logger.debug(".sourcetype and tenantid is passed with the parameters");
				dataset = dataContext.query().from(table)
						.select(tenantID, source, control_Number, requestDate, trade, problemdescription, customer,
								location)
						.where(sourceStatus).eq("OPEN").and(tenantID).eq(tenant).and(source).eq(sourceType).execute();
				logger.debug(".dataset done");
			} else if (listNewServiceRequestObj.has(TENANTID)) {
				String tenant = (String) listNewServiceRequestObj.get(TENANTID);
				logger.debug("Only tenantid is passed with the parameters");
				dataset = dataContext
						.query().from(table).select(tenantID, source, control_Number, requestDate, trade,
								problemdescription, customer, location)
						.where(sourceStatus).eq("OPEN").and(tenantID).eq(tenant).execute();
			} else {
				Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);
				response.setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
				exchange.getIn()
						.setBody("The request passed is invalid, Tenantid is Must for the request to be processed.");
				throw new InvalidRequestException("The request passed is invalid, Tenantid is Must.");
			}
		} catch (JSONException e) {
			throw new UnableToParseRequestJsonException("The requested data is not valid");
		}
		return dataset;
	}// ..end of method dataSetQueryDeciderOnRequestType

	/**
	 * method to take the dataset and return a jsonArray computed with all the
	 * rows whose status is OPEN
	 * 
	 * @throws DateFormattingParsingException
	 * @throws UnableToParseRequestJsonException
	 */
	public void listNewServiceRequestAsArrayInExchange(DataSet dataset, Exchange exchange, Column tenantID,
			Column source, Column control_Number, Column requestDate, Column trade, Column problemdescription,
			Column customer, Column location, Column sourceStatus)
			throws DateFormattingParsingException, UnableToParseRequestJsonException {
		Row row = null;
		String sDateInAmerica = null;
		org.json.JSONArray jsonArray = new org.json.JSONArray();
		while (dataset.next()) {
			org.json.JSONObject serviceRequest = new org.json.JSONObject();
			row = dataset.getRow();
			SimpleDateFormat formatter = new SimpleDateFormat(SERVICE_REQUEST_DATE_FORMAT_IN_DB);
			String dealDateString = (String) row.getValue(requestDate);
			Date date;
			try {
				date = formatter.parse(dealDateString);
			} catch (ParseException e1) {
				throw new DateFormattingParsingException(
						"DateTime Format is not in this pattern of : " + SERVICE_REQUEST_DATE_FORMAT_IN_DB);
			}
			try {
				// converting date format for US
				SimpleDateFormat sdfAmerica = new SimpleDateFormat(SERVICE_REQUEST_DATE_AMERICAN_FORMAT);
				TimeZone tzInAmerica = TimeZone.getTimeZone(SERVICE_REQUEST_TIMEZONE_AMERICAN_FORMAT);
				sdfAmerica.setTimeZone(tzInAmerica);
				sDateInAmerica = sdfAmerica.format(date);
			} catch (Exception bug) {
				throw new DateFormattingParsingException("Unable to convert the date into US ST");
			}
			try {
				serviceRequest.put(TENANTID, row.getValue(tenantID));
				serviceRequest.put(SOURCE_TYPE, row.getValue(source));
				serviceRequest.put(SOURCE_REQUEST_ID_DB_COLUMN_KEY, row.getValue(control_Number));
				serviceRequest.put(REQUEST_CREATED_DATE_DB_COLUMN_KEY, sDateInAmerica);
				serviceRequest.put(TRADE, row.getValue(trade));
				serviceRequest.put(PROBLEM_DESCRIPTION_DB_COLUMN_KEY, row.getValue(problemdescription));
				serviceRequest.put(SOURCE_CUST_ID_DB_COLUMN_KEY, row.getValue(customer));
				serviceRequest.put(SOURCE_CUST_LOCATION_DB_COLUMN_KEY, row.getValue(location));
				logger.debug("serviceRequest : " + serviceRequest.toString());
			} catch (JSONException e) {
				throw new UnableToParseRequestJsonException("Unable to fetch the data from Row : " + e);
			}
			jsonArray.put(serviceRequest);
		}
		exchange.getIn().setBody(jsonArray.toString());
	}// ..end of method listNewServiceRequestAsArrayInExchange

	/**
	 * method to take In request and perform Update Query on DB
	 * 
	 * @throws InvalidRequestException
	 * @throws CassandraConnectionException
	 * @throws UnableToParseRequestJsonException
	 */
	public void getDBEntitiesAndUpdate(Exchange exchange, org.json.JSONObject editNewServiceRequestObj,Table table, UpdateableDataContext dataContext)
			throws InvalidRequestException, CassandraConnectionException, UnableToParseRequestJsonException {
		Column customer = table.getColumnByName(SOURCE_CUST_ID_DB_COLUMN_KEY);
		Column location = table.getColumnByName(SOURCE_CUST_LOCATION_DB_COLUMN_KEY);
		Column problemdescription = table.getColumnByName(PROBLEM_DESCRIPTION_DB_COLUMN_KEY);
		Column sourceStatus = table.getColumnByName(SOURCE_STATUS_DB_COLUMN_KEY);
		if (editNewServiceRequestObj.has("sourcerequestid") && editNewServiceRequestObj.has("tenantid")) {
			try {
				String tenant = (String) editNewServiceRequestObj.get(TENANTID);
				String servicerequestid = editNewServiceRequestObj.getString(SOURCE_REQUEST_ID_DB_COLUMN_KEY);
				String status = (String) editNewServiceRequestObj.get(SOURCE_STATUS_DB_COLUMN_KEY);
				String description = (String) editNewServiceRequestObj.get(PROBLEM_DESCRIPTION_DB_COLUMN_KEY);
				String customerid = (String) editNewServiceRequestObj.get(SOURCE_CUST_ID_DB_COLUMN_KEY);
				String loc = (String) editNewServiceRequestObj.get(SOURCE_CUST_LOCATION_DB_COLUMN_KEY);
				FilterItem fitem = new FilterItem(
						"tenantid='" + tenant + "'" + " and sourcerequestid='" + servicerequestid + "'");

				dataContext.executeUpdate((UpdateScript) new Update(table).value(sourceStatus, status)
						.value(problemdescription, description).value(customer, customerid).value(location, loc)
						.where(fitem));
			} catch (JSONException e) {
				throw new UnableToParseRequestJsonException("The request Data is invalid");
			}
		} else {
			Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);
			response.setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
			response.setEntity("<response>Invalid Request</response>", MediaType.TEXT_XML);
			exchange.getIn().setBody(response);
			throw new InvalidRequestException("The request passed is invalid");
		}
	}// ..end of method getDBEntitiesAndUpdate

}