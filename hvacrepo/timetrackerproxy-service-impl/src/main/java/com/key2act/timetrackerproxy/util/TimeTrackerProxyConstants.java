package com.key2act.timetrackerproxy.util;

public class TimeTrackerProxyConstants {
	public final static String TENANT_ID_REQUEST_KEY = "tenantId";
	
	public final static String TENANT_TO_VERSION_WSURL_MAPPING_PERMA_CONFIG_KEY="tenant-version-WebServiceMappingEndpoint-connectionString-mapping";
	public final static String VERSION_TO_XSL_CONNECTION_STRING="version-requestXSL-responseXSL-mapping";
	
	public final static String VERSION_PERMA_KEY = "version" ;
	public final static String CONNECTION_STRING_PERMA_KEY = "connectionString";
	
	public final static String WEBSERVICE_ENDPOINT_PERMA_KEY = "webServiceURL";
	public final static String WS_URL_PERMA_KEY = "webServiceURL";
	public final static String CONN_STRING_PERMA_KEY = "connectionString";
	
	public final static String REQUEST_TRANSFORMER_XSL_FILE_PERMA_KEY="RequestTransformerFile";
	public final static String RESPONSE_TRANSFORMER_XSL_FILE_PERMA_KEY = "ResponseTransformerFile";
	
	public final static String SERVICE_DATA_KEY = "ServiceData";
	public final static String REQUEST_XSLT_FILE_KEY = "RequestTransformerFile";
	public final static String RESPONSE_XSLT_FILE_KEY = "ResponseTransformerFile";
	public final static String SERVICE_OPERATION_NAME_KEY = "OperationName";
	public final static String SERVICE_DATA = "ServiceData";
	public final static String CASSANDRA_TABLE_KEYSPACE = "key2act";

	public final static String TENANT_ID_COLUMN_KEY = "tenantid";
	
	public final static String UNION_TABLENAME = "union" ;
	public final static String UNIONCODE_COLUMN_KEY = "UNIONCD" ;
	public final static String UNION_DESCRIPTION_COLUMN_KEY = "Union_Description" ;

	public final static String RATECLASS_TABLENAME = "rateclass" ;
	public final static String RATECLASS_COLUMN_KEY = "RATECLSS";
	public final static String RATEDESCRIPTION_COLUMN_KEY = "RATEDSCR";
	
	public final static String FEDCLASS_TABLENAME = "fedclass";
	public final static String FEDCLASS_CODE_COLUMN_KEY = "FEDCLSSCD";
	public final static String FEDCLASS_DESC_COLUMN_KEY = "Federal_Class_Descript";
	
	public final static String DEPARTMENT_TABLENAME = "department";
	public final static String DEPARTMENT_ID_COLUMN_KEY = "DEPRTMNT";
	public final static String DEPARTMENT_DESC_COLUMN_KEY = "DSCRIPTN";
	
	public final static String POSITION_TABLENAME = "position";
	public final static String POSITION_TITLE_COLUMN_KEY = "JOBTITLE";
	public final static String POSITION_DESC_COLUMN_KEY = "DSCRIPTN";
	
	public final static String PAYROLL_STATE_TABLENAME = "payrollstate";
	public final static String PAYROLL_STATE_CODE_COLUMN_KEY = "STATECD";
	public final static String PAYROLL_STATE_NAME_COLUMN_KEY = "STATENAM";
	
	public final static String EMPLOYEE_TABLENAME ="employee" ;
	public final static String EMPLOYEE_SUTASTAT_COLUMN_KEY = "SUTASTAT" ;
	
	public final static String WORKERS_COMP_TABLENAME = "workerscomp";
	public final static String WORKERS_COMP_NUMBER_COLUMN_KEY = "WRKRCOMP";
	public final static String WORKERS_COMP_DESC_COLUMN_KEY = "DSCRIPTN";
	
	public final static String EMPLOYEE_ID_COLUMN_KEY = "EMPLOYID";
	public final static String EMPLOYEE_FIRSTNAME_COLUMN_KEY = "FRSTNAME";
	public final static String EMPLOYEE_LASTNAME_COLUMN_KEY = "LASTNAME";
	
	public final static String EQUIPMENT_TABLENAME = "equipment";
	public final static String EQUIPMENT_ID_COLUMN_KEY = "Equipment_ID";
	public final static String EQUIPMENT_WENNSOFT_MASTER_ID_COLUMN_KEY = "Wennsoft_Master_Equip_ID";
	public final static String EQUIPMENT_TYPE = "Equipment_Type";
	public final static String EQUIPMENT_DESCRIPTION = "Equipment_Description2";
}
