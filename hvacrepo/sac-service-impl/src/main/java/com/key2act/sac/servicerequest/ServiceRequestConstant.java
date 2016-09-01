package com.key2act.sac.servicerequest;

public class ServiceRequestConstant {
	
	
	public static final String PROBLEM_AREA_DB_COLUMN_KEY="problemarea";
	public static final String PROBLEM_DESCRIPTION_DB_COLUMN_KEY="problemdescription";
	public static final String NOTE_TEXT_SPLIT_CRITERIA_KEY="WO Edited:";
	public static final String NOTE_PROVIDER_CHANGED_CRITERIA_KEY="Provider reassigne";
	public static final String NOTE_PRIORITY_CHANGED_CRITERIA_KEY="Priority changed";
	public static final String NOTE_NTE_CHANGED_CRITERIA_KEY="NTE changed";
	public static final String NOTE_STATUS_CHANGED_CRITERIA_KEY="status changed";
	public static final String NOTE_SCHEDULE_DATE_CHANGED_CRITERIA_KEY="Scheduled Date changed";
	public static final String ATTR_XML_ELEMENT="ATTR";
	public static final String TRANSACTION_NUMBER_XML_ELEMENT="TransactionNumber";
	public static final String CALL_TYPE_XML_ELEMENT="Type";
	public static final String CALL_STATUS_XML_ELEMENT="Status";
	public static final String INTERNAL_STATUS_KEY="InternalStatus";
	public static final String WONUMBER_XML_ELEMENT="WONumber";
	public static final String PURCHASEORDER_NUMBER_XML_ELEMENT="WOPO";
	public static final String REQUESTBY_XML_ELEMENT="RequestedBy";
	public static final String PROBLEM_AREA_XML_ELEMENT="ProblemArea";
	public static final String PROBLEM_DESCRIPTION_XML_ELEMENT="ProblemDescription";
	public static final String PIPELINE_LOADED_KEY = "pipelineLoaded";
	public static final String PIPELINE_LOADED_LIST_KEY = "pipelineLoadedList";
	public static final String PIPELINE_LOADED_COUNTER_KEY = "pipelineLoadedCounter";
	public static final String REQUESTDETAIL_TYPE_KEY = "RequestDetail";
	public static final String REQUESTDETAIL_WOSTATUS_TYPE_KEY = "WOStatus";
	public static final String REQUESTDETAIL_STATUS_TYPE_KEY = "Status";
	public final static String SERVICE_REQUEST_TABLENAME = "key2act.servicerequest";
	public final static String TENANTID = "tenantid";
	public final static String SOURCE_TYPE = "sourcetype";
	public final static String TRADE ="trade";
	public final static String SOURCE_REQUEST_ID_DB_COLUMN_KEY = "sourcerequestid";
	public final static String REQUEST_CREATED_DATE_DB_COLUMN_KEY = "requestcreateddtm";
	public final static String SOURCE_CUST_ID_DB_COLUMN_KEY = "sourcecustid";
	public final static String SOURCE_CUST_LOCATION_DB_COLUMN_KEY = "sourcelocation";
	public final static String SOURCE_STATUS_DB_COLUMN_KEY = "sourcestatus";
	public final static String SOURCE_XMLPLAYLOAD_DB_COLUMN_KEY ="xmlpayload";
	public final static String SOURCE_REQUEST_LOG_UID_DB_COLUMN_KEY = "uid";
	
	public final static String SERVICE_REQUEST_STATUS_UPDATED_KEY ="ServiceRequest:UPDATED";
	public final static String SERVICE_REQUEST_NEW_INTERNAL_STATUS_KEY ="newServiceRequestInternalStatus";
	public final static String SERVICE_REQUEST_DATE_FORMAT_IN_DB = "yyyy/MM/ddhh:mm:ss";
	public final static String SERVICE_REQUEST_DATE_AMERICAN_FORMAT = "MM/dd/yyyy";
	public final static String SERVICE_REQUEST_TIMEZONE_AMERICAN_FORMAT = "America/New_York";
	public final static String LIST_NEW_SERVICEREQUEST_DATA_KEY="ListNewServiceRequest";
	public final static String EDIT_NEW_SERVICEREQUEST_DATA_KEY="EditNewServiceRequest";
	public final static String CREATECALL_DATA_KEY="CreateCall";
}
