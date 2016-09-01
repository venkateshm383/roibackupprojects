package com.key2act.timetracker.service.helper;

import org.apache.camel.Exchange;

import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;

public class TimeSheetOTCalculationHelper extends AbstractCassandraBean {
	
	
	
	
	/**
	 * #TODO need to write code to get paycode from general setup based on payType 
	 * 
	 * @param tenantId
	 * @param payType
	 * @return
	 */
	public String getPaycodeByPayType(String tenantId, String payType){
		
		
		
		
		
		return null;
	}

	
	/**
	 * #TODO to get OT Rule from EmployeeSetup 
	 * @param tenantId
	 * @param empId
	 * @return
	 */
	private String getOTRuleForEmployee(String tenantId, String empId){
		
		
		return null;
	}
	
	/**
	 * #TODO to get OT Rule from general Setup 
	 * @param tenantId
	 * @param empId
	 * @return
	 */
	private String getOTRuleFromGeneralSetup(String tenantId, String empId){
		
		return null;
	}


	@Override
	protected void processBean(Exchange arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
