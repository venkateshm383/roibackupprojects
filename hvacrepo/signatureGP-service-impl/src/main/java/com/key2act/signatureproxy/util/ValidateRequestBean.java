package com.key2act.signatureproxy.util;

import org.json.JSONObject;

public class ValidateRequestBean {

	/**
	 * validating the incoming Request for Create Call Service
	 * 
	 * @param requestObject
	 */
	public boolean validateRequestForCreateCall(JSONObject requestObject) {
		if (requestObject.has("customernumber") && requestObject.has("addresscode") && requestObject.has("technician")
				&& requestObject.has("dateofservicecall") && requestObject.has("equipmentid")
				&& requestObject.has("typecallshort") && requestObject.has("division")
				&& requestObject.has("calluserid") && requestObject.has("callsource")
				&& requestObject.has("aggregator")) {
			return true;
		} else
			return false;
	}
}
