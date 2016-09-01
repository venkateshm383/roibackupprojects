package com.key2act.timetracker.bean;

import org.apache.camel.Exchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.key2act.timetracker.service.InvalidRequestDataException;
import com.key2act.timetracker.service.helper.ServiceRequestBuilder;
import com.key2act.timetracker.service.helper.TimeSheetDetailsGetHelper;
import com.key2act.timetracker.service.helper.TimeSheetEntryHelper;
import com.key2act.timetracker.timesheet.TimeSheetTrackerProcessingException;

public class TimeSheetEntryBean  extends AbstractCassandraBean {

	
	Logger logger=LoggerFactory.getLogger(TimeSheetEntryBean.class);
	@Override
	protected void processBean(Exchange exhExchange) throws Exception {
		
		logger.debug(" (.) insidee processBean of TimeSheetEntryBean  ");
		ServiceRequestBuilder requestBuilder=new ServiceRequestBuilder();
		
		JSONObject requestObj = requestBuilder.getRequestData(exhExchange);
		addTimeSheetEntryDetails(requestObj);
		
	}
	
	
	public void addTimeSheetEntryDetails(JSONObject jsonObject) throws TimeSheetTrackerProcessingException{
	
		
		TimeSheetEntryHelper tiEntryHelper=new TimeSheetEntryHelper();
		
		tiEntryHelper.addTimeTrackerDetails(jsonObject);
		
	}

	
	
	public void getTimeSheetDetails(Exchange exhExchange) throws TimeSheetTrackerProcessingException, InvalidRequestDataException, JSONException{
	
		
		ServiceRequestBuilder requestBuilder=new ServiceRequestBuilder();
		
		JSONObject requestObj = requestBuilder.getRequestData(exhExchange);

			TimeSheetDetailsGetHelper thDetailsGetHelper=new TimeSheetDetailsGetHelper();
		exhExchange.getIn().setBody(	thDetailsGetHelper.getTimeSheetDetails(requestObj));
	}
	
	
}
