package com.key2act.signatureproxy.util;

public class SignatureGPConstants {
	public static final String TENANT_TO_SIGNATURE_GP_VERSION_AND_ENDPOINT_PERMAKEY="tenant-signatureGPVersion-Mapping";
	public static final String SIGNATURE_GP_VERSION_TO_XSLT_PERMAKEY="signatureGPOperation-xslt-Mapping";
	public static final String SIGNATUREGP_VERSION_KEY = "SignatureGP-Version";
	public static final String SIGNATUREGP_WEBSERVICE_ENDPOINT = "WebServiceEndPoint";
	public static final String SOAP_ACTION_KEY = "SoapAction" ;
	public static final String POST_METHOD = "POST" ;
	public static final String TENANT_NAME = "tenantName" ;
	public static final String OPERATION_BASED_XSL_FILE_KEY="OperationBasedXSLTransformerFile";
	public static final String CREATE_CALL_KEY = "CreateCall" ;
	public static final String SITE_KEY = "site";
	public static final String ORG_KEY = "org";
	public final static String CASSANDRA_TABLE_KEYSPACE = "key2act";
	
	public final static String CUSTOMER_TABLE = "customer";
	public final static String CUSTOMER_USERID_COLUMN_KEY = "USERID";
	public final static String CUSTOMER_CUSTNMBR_COLUMN_KEY = "CUSTNMBR";
	public final static String CUSTOMER_CONTACT_NAME_COLUMN_KEY = "Contact_Name";
	
	public final static String LOCATION_TABLE = "location" ;
	public final static String LOCATION_LOC_NAME_COLUMN_KEY = "LOCATNNM" ;
	public final static String LOCATION_STATE_COLUMN_KEY = "STATE" ;
	public final static String LOCATION_ADDRESS_COLUMN_KEY = "ADDRESS1" ; 
	public final static String LOCATION_ADRSCODE_COLUMN_KEY = "ADRSCODE" ;
	public final static String LOCATION_CUSTOMER_NUMBER_COLUMN_KEY = "CUSTNMBR";
	public final static String LOCATION_BILL_ADDRESS_CODE_COLUMN_KEY = "Bill_Address_Code";
	public final static String LOCATION_BILL_CUSTOMER_NUMBER_COLUMN_KEY = "Bill_Customer_Number";
	
	public final static String EQUIPMENT_TABLENAME = "equipment";
	public final static String EQUIPMENT_CUSTNMBR_COLUMN_KEY = "CUSTNMBR" ;
	public final static String EQUIPMENT_ADRSCODE_COLUMN_KEY = "ADRSCODE" ;
	public final static String EQUIPMENT_CONTRACT_NMBR_COLUMN_KEY = "Contract_Number";
	
	public final static String EQUIPMENT_ID_COLUMN_KEY = "Equipment_ID";
	public final static String EQUIPMENT_TYPE_COLUMN_KEY = "Equipment_Type";
	public final static String EQUIPMENT_DESC2_COLUMN_KEY = "Equipment_Description2";
	
	public final static String TECHNICIAN_TABLE = "technician";
	public final static String TECHNICIAN_TECH_COLUMN_KEY = "Technician";
	public final static String TECHNICIAN_TECHID_COLUMN_KEY= "Technician_ID";
	public final static String TECHNICIAN_TECHLONGNAME_COLUMN_KEY = "Technician_Long_Name" ;
	
	public final static String CALLTYPE_TABLENAME = "calltype";
	public final static String CALLTYPE_TYPE_OF_CALL = "Type_of_Call" ;
	public final static String CALLTYPE_TYPE_OF_CALL_SHORT = "Type_Call_Short";
	
	public final static String DIVISION_TABLENAME = "division" ;
	public final static String DIVISION_DIVISIONS = "Divisions" ;
 }
