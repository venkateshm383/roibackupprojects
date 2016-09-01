package com.getusroi.wms20.label;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoadingLabelAttribute {
	
	final static Logger logger = LoggerFactory.getLogger(LoadingLabelAttribute.class);	
	
	/**
	 * This method is used to load the label attribute for label template
	 * @return JSONArray object
	 * @throws LabelAttributeLoadingException 
	 */
	 public  JSONArray  getJSONArrayLabel() throws LabelAttributeLoadingException{	    
			logger.debug(".getJSONArrayLabel method of LoadingLabelAttribute ");	
			String labelattributetilda =  "{\"jarraylabelattribute\":[{\"sfr_address1\":\"bangalore1\",\"rt_address2\":\"belandur1\",\"rt_address1\":\"total\",\"from_post\":\"wiprogate\",\"rt_address3\":\"batashoroom\",\"sfr_address2\":\"kasavanhalli\",\"sfr_address3\":\"kannali\",\"sscc_no\":\"1982309232\",\"process_dt\":\"09-92-20\",\"shipto_post\":\"GLB\",\"SUBUCC\":\"sub345\"},{\"sfr_address1\":\"bangalore\",\"rt_address2\":\"belandur\",\"rt_address1\":\"total\",\"from_post\":\"wiprogate\",\"rt_address3\":\"batashoroom\",\"sfr_address2\":\"kasavanhalli\",\"sfr_address3\":\"kannali\",\"sscc_no\":\"1982309232\",\"process_dt\":\"09-92-20\",\"shipto_post\":\"GLB\",\"SUBUCC\":\"sub345\"}]}";	    
	    	String labelattributeFDFS = "{\"jarraylabelattribute\":[{\"sfr_address2\":\"kannali\",\"rt_address2\":\"bizruntime1\",\"rt_address3\":\"hsr\",\"st_cstnam\":\"sanat\",\"rt_address1\":\"city bank\",\"sfr_address3\":\"forum\",\"sfr_address1\":\"roi\",\"po_number\":\"5467\",\"edi_fld\":\"90IJ\",\"ucc128_display\":\"SBC\",\"ucc128_code\":\"ER78\","+"\"process_dt\":\"20-10-13\",\"item_number\":\"9089\",\"location\":\"gulbarga\",\"order_number\":\"890\",\"shipment_id\":\"OPI54\"},{\"sfr_address2\":\"hsr\",\"rt_address2\":\"roi\",\"rt_address3\":\"begur\",\"st_cstnam\":\"vikash\",\"rt_address1\":\"sbh bank\",\"sfr_address3\":\"phonix\",\"sfr_address1\":\"bizruntime\",\"po_number\":\"3456743\",\"edi_fld\":\"72BN\",\"ucc128_display\":\"RCR\",\"ucc128_code\":\"CW1234\","+"\"process_dt\":\"12-01-12\",\"item_number\":\"56784\",\"location\":\"mysore\",\"order_number\":\"345\",\"shipment_id\":\"GFF54\"}]}";
	    	JSONObject jsonObject=null;
	    	JSONArray jsonArray=null;
			try {
				jsonObject = new JSONObject(labelattributetilda);
				jsonArray = jsonObject.getJSONArray(LabelPropertyConstant.JSONARRAY_LABEL_ATTRIBUTE);
				logger.debug("getJSONArrayLabel   jsonArray.length() "+jsonArray.length());
			} catch (JSONException e) {
				throw new LabelAttributeLoadingException("Unable to load the label attribute for the label template ",e);
			}
			logger.debug("exiting the getJSONArrayLabel method of LoadingLabelAttribute ");
	    	return jsonArray;
	    }//end of method
	 
	 
} 
	
	
	
