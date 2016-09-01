package com.getusroi.wms20.parcel.parcelservice.fedEx.constant;

/**
 * Class defines FedEx related constants
 * 
 * @author BizRuntimeIT Services Pvt-Ltd
 */
public class FedexConstant {

	public static final String XML = "xml";
	public static final String SHIPPER_ID = "shipperId";
	public static final String KEY = "Key";
	public static final String PASSWORD = "Password";
	public static final String ACCOUNT_NUMBER = "AccountNumber";
	public static final String METER_NUMBER = "MeterNumber";
	public static final String SERVICE_ID = "ServiceId";
	public static final String SERVICE_ID_SHIP = "ServiceId_ship";

	public static final String SERVICE_ID_VOID = "ServiceId_Void";

	public static final String SERVICE_ID_RATE = "ServiceId_Rate";
	
	public static final String MAJOR = "Major";
	public static final String MAJOR_SHIP = "Major_ship";

	public static final String MAJOR_VOID = "Major_Void";

	
	public static final String MAJOR_RATE = "Major_Rate";
	public static final String MINOR = "Minor";
	public static final String MINOR_SHIP = "Minor_ship";

	public static final String MINOR_VOID = "Minor_Void";

	public static final String MINOR_RATE = "Minor_Rate";

	public static final String INTERMEDIATE = "Intermediate";
	public static final String INTERMEDIATE_SHIP = "Intermediate_ship";

	public static final String INTERMEDIATE_VOID = "Intermediate_Void";

	public static final String INTERMEDIATE_RATE = "Intermediate_Rate";

	public static final String RATE_REQUEST = "RateRequest";
	public static final String AUTH_DETAIL = "WebAuthenticationDetail";
	public static final String USER_CRED = "UserCredential";
	public static final String TRANSACTION_DETAIL = "TransactionDetail";
	public static final String VERSION = "Version";
	public static final String CLIENT_DETAIL = "ClientDetail";
	public static final String SHIPPER_DETAILS = "ShipperDetails";
	public static final String CONTACT_ADDRESS = "ContactAddress";
	public static final String COMPANY_NAME = "CompanyName";
	public static final String PH_NUM = "PhoneNumber";
	public static final String STREET_LINE1 = "StreetLine1";
	public static final String STREET_LINE2 = "StreetLine2";
	public static final String STATE_CODE = "StateOrProvinceCode";
	public static final String POSTAL_CODE = "PostalCode";
	public static final String COUNTRY_CODE = "CountryCode";
	public static final String CITY = "City";
	public static final String ADDRESS = "address";
	public static final String PARCEL_SHIPPER_ADDRESS = "ParcelShipperAddress";
	public static final String FEDEX_PROPERTIES_JSON = "fedexproperties";
	public static final String FEDEX_CREDENTIALS_JSON = "fedexcreds";
	public static final String FEDEX_PROPERTIES_PERMA = "FedExProperties";
	public static final String FEDEX_CREDS_PERMA = "FedExCredentials";
	public static final String XPATH_SHIPPERID = "RateRequest/RequestedShipment/Shipper/shipper_id/text()";
	
	//void properties

	public static final String VOID = "Void";
	
	public static final String SHIP_TIME_STAMP = "ShipTimestamp";
	public static final String SHIP_TIME_STAMP_SHIP = "ShipTimestamp_ship";

	public static final String SHIP_TIME_STAMP_VOID = "ShipTimestamp_Void";
	
	public static final String DELETION_CONTROL = "DeletionControl";
	public static final String DELETION_CONTROL_VOID = "DeletionControl_Void";
	public static final String TRACKING_ID_TYPE = "TrackingIdType";
	public static final String TRACKING_ID_TYPE_VOID = "TrackingIdType_Void";

	public static final String DELETE_SHIPMENT_REQUEST = "DeleteShipmentRequest";

	//ship constants
	public static final String SHIPMENT = "Shipment";
	public static final String SHIPPING_CHARGES_PAYMENT = "ShippingChargesPayment";
	public static final String PAYMENT_TYPE = "PaymentType";
	public static final String PAYMENT_TYPE_SHIP = "PaymentType_ship";

	
	public static final String REQUEST_SHIPMENT = "RequestedShipment";
	public static final String DROP_OFF_TYPE = "DropoffType";
	public static final String DROP_OFF_TYPE_SHIP = "DropoffType_ship";

	public static final String PACKAGING_TYPE = "PackagingType";
	public static final String PACKAGING_TYPE_SHIP = "PackagingType_ship";


	public static final String LABEL_SPECIFICATION = "LabelSpecification";
	public static final String LABEL_FORMAT_TYPE = "LabelFormatType";
	public static final String LABEL_FORMAT_TYPE_SHIP = "LabelFormatType_ship";

	public static final String RATE_REPLYDETAILS = "RateReplyDetails";
	public static final String RATE_REPLY = "RateReply";
	public static final String CODE = "Code";
	public static final String MESSAGE = "Message";
	public static final String PARCEL_SERVICE_ERROR_CODE = "ParcelServiceErrorCode";
}
