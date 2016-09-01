package com.key2act.timetracker.util;

public class TimeTrackerConstants {
	
	public final static String CASSANDRA_TABLE_KEYSPACE = "key2act";
	public final static String TENANTID_COLUMN_KEY="tenantId";
	public final static String TENANT_ID_FROM_REQUEST_DATA = "tenantID";
	public final static String TRANSACTION_TYPE_COLUMN_KEY = "transactionTypes";
	public final static String BATCH_SETUP_TABLENAME = "batchsetup";
	public final static String IS_EMPLOYEE_PERMISSION_LOG_CREW_COLUMN_KEY="isemppermissionlogcrew";
	public final static String BATCH_COMMENT = "batchcomment";
	public final static String END_OF_WEEK = "endofweek";
	public final static String IS_POST_BATCHES_DAILY = "ispostbatchesdaily";
	public final static String PREFIX_BATCH_NAME = "prefixbatchname";
	
	public final static String SELECT_QUERY_GENERAL_SETUP ="SELECT * FROM GeneralSetup";
	public final static String GENERAL_SETUP_TABLENAME = "GeneralSetup";
	public final static String CREW_SETUP_TABLENAME = "CrewSetup" ;
	public final static String CREW_POSITIONS_TO_LOG_TABLENAME = "CrewPositionsToLog";
	public final static String EMPLOYEE_OT_SETUP_TABLENAME = "EmployeeOTSetup" ;
	public final static String CUSTOMER_BILLING_SETUP_TABLENAME = "CustomerBillingSetup" ;
	public final static String HOLIDAY_PAY_PER_HOUR_SETUP_TABLENAME="HolidayPayPerHourSetup";
	public final static String TRANSACTION_TYPE_SETUP_TABLENAME = "TransactionType";
	
	public final static String IS_UNBILLED_LABOUR_TRANS_HOLIDAY_COLUMN_KEY="isUnbilledLaborTransHoliday";
	public final static String OVERTIME_CALCULATION_METHOD_COLUMN_KEY="otCalcMethod";
	public final static String PAYCODE_FOR_HOLIDAY_WORKED_COLUMN_KEY="paycodeHolidayWorked";
	public final static String PAYCODE_FOR_OVERTIME_COLUMN_KEY="paycodeOverTime";
	public final static String PAYCODE_FOR_PREMIUM_COLUMN_KEY="paycodePremium";
	public final static String PAYCODE_FOR_REGULAR_COLUMN_KEY="paycodeRegular";
	public final static String PAYCODE_FOR_SALARY_EMPLOYEES_COLUMN_KEY="paycodeSalaryEmployees";
	public final static String STANDARD_NUMBER_OF_BILLABLE_HOURS_PER_DAY_COLUMN_KEY="stdNumOfHoursDay";
	public final static String STANDARD_NUMBER_OF_BILLABLE_HOURS_PER_WEEK_COLUMN_KEY="stdNumOfHoursWeek";
	
	public final static String DESCRIPTION_KEY_COLUMN_KEY="description";
	public final static String IS_EMPLOYEE_PERMISSION_LOG_COLUMN_KEY="isEmpPermissionLog";
	public final static String EMP_ID_COLUMN_KEY="empId";
	public final static String EMPLOYEE_NAME_COLUMN_KEY="employeeName";
	public final static String POSITION_CODE_COLUMN_KEY="positionCode";
	
	public final static String HOURS_PER_DAY_COLUMN_KEY ="hoursPerDay";
	public final static String HOURS_PER_WEEK_COLUMN_KEY="hoursPerWeek";
	public final static String OT_CALC_METHOD_COLOUM_KEY="otCalcMethod";
	
	public final static String HOLIDAY_PAY_WITH_PREMIUM_TIME_BILLING="holidayPayWithPremiumTimeBilling";
	public final static String HOLIDAY_PAY_WITH_OT_BILLING="holidayPayWithOTBilling";
	public final static String HOLIDAY_PAY_WITH_REGULAT_TIME_BILLING="holidayPayWithRegTimeBilling";
	public final static String IS_RATE_DIFFERENT="isRateDifferent";
	public final static String OT_PAY_WITH_HOLIDAY_BILLING="otPayWithHolidayBilling";
	public final static String OT_PAY_WITH_PREMIUM_TIME_BILLING="otPayWithPremiumTimeBilling";
	public final static String OT_PAY_WITH_REGULAR_TIME_BILLING="otPayWithRegTimeBilling";
	public final static String PREMIUM_PAY_WITH_HOLIDAY_BILING="premiumPayWithHolidayBilling";
	public final static String PREMIUM_PAY_WITH_OT_BILLING="premiumPayWithOTBilling";
	public final static String PREMIUM_PAY_WITH_REGULAR_TIME_BILLING="premiumPayWithRegTimeBilling";
	public final static String REGULAR_PAY_WITH_HOLIDAY_BILLING="regPayWithHolidayBilling";
	public final static String REGULAR_PAY_WITH_OT_BILLING="regPayWithOTBilling";
	public final static String REGULAR_PAY_WITH_PREMIUM_TIME_BILLING="regPayWithPremiumTimeBilling";
	
	public final static String HOLIDAY_DATE_COLUMN_KEY="holidayDate";
	public final static String HOLIDAY_PAY_PER_HOUR="holidayPayPerHour";
	public final static String HOLIDAY_NAME="holidayName";
	
	public final static String CREW_SETUP_PERMA_CONFIG_KEY="CrewSettingsConfiguration";
	public final static String GENERAL_SETUP_PERMA_CONFIG_KEY="GeneralSettingsConfiguration";
	public final static String HOLIDAY_PAY_PER_HOUR_PERMA_CONFIG_KEY="HolidayPayPerHourSettingsConfiguration";
	public final static String CREW_POSITIONS_TO_LOG_SETTINGS_CONFIGURATION_PERMA_CONFIG_KEY = "CrewPositionsToLogSettingsConfiguration";
	public final static String EMPLOYEE_OT_SETTINGS_PERMA_CONFIG_KEY = "EmployeeOTSettingsConfiguration";
	public final static String CUSTOMER_BILLING_PERMA_CONFIG_KEY= "CustomerBillingSettingsConfiguration";
	public final static String BATCH_SETTINGS_PERMA_CONFIG_KEY = "BatchSettingsConfiguration";
	public final static String TRANSACTION_SETTINGS_CONFIG_KEY = "TransactionTypeSettingsConfiguration";
	
	public final static String BATCHSETTINGS_RESPONSE_KEY = "BatchSettings";
	public final static String CREWSETTINGS_EMPLOYEE_RESPONSE_KEY = "EmployeeWhoCanLog";
	public final static String CREWSETTINGS_EMPLOYEE_POSITION_RESPONSE_KEY = "CrewPositionWhoCanBeLogged";
	public final static String CUSTOMER_BILLING_RESPONSE_KEY = "CustomerBilling";
	public final static String EMPLOYEE_OT_CALCULATION_METHOD_RESPONSE_KEY = "OverTimeCalculationData";
	public final static String GENERALSETTINGS_RESPONSE_KEY = "GeneralSettings";
	public final static String HOLIDAYSETTINGS_RESPONSE_KEY = "holidaySettings";
	public final static String TRANSACTION_TYPE_RESPONSE_KEY = "TransactionTypeSettings";
	
	
	//Time Sheet Tables Names 
	public final static String TIME_SHEET_ENTRY_TABLE="timesheetentrydata";
	public final static String TIME_SHEET_PAYROL_ENTRY_TABLE="timesheetpayrollentrydata";
	public final static String TIME_SHEET_CUSTOMER_BILL_ENTRY_TABLE="timesheetcustomerbillingentrydata";
	public final static String TIME_SHEET_NOTES_ENTRY_TABLE="timesheetnotes";
	public final static String TIME_SHEET_ATTACHMENTS_ENTRY_TABLE="timesheetattachments";
	public final static String TIME_SHEET_EQUIPMENTS_ENTRY_TABLE="timesheetequipment";

	
	//TimeSheetEntryTable columns		
	public final static String TIME_SHEET_CLMN_TENANTID="tenantid";
	public final static String TIME_SHEET_CLMN_EMP_ID="empid";
	public final static String TIME_SHEET_CLMN_TIME_ENTRY_DTM="timeentrydate";
	public final static String TIME_SHEET_CLMN_ITEM_NO="lineitem";
	public final static String TIME_SHEET_CLMN_GL_ACCNT_CODE="glaccountcode";
	public final static String TIME_SHEET_CLMN_JOB_NO="jobnumber";
	public final static String TIME_SHEET_CLMN_JOB_COST_CODE="jobcostcode";
	public final static String TIME_SHEET_CLMN_SERVICE_CALL_COST_CODE="servicecallcostcode";
	public final static String TIME_SHEET_CLMN_TIME_ENTRY_TYPE="timeentrytype";
	public final static String TIME_SHEET_CLMN_SERVICE_CALL_NO="servicecallnumber";
	public final static String TIME_SHEET_CLMN_STATUS="status";
	public final static String TIME_SHEET_CLMN_HAS_ATTACHMENT="hasAttachments";
	public final static String TIME_SHEET_CLMN_HAS_NOTES="hasNotes";
	public final static String TIME_SHEET_CLMN_EQUIPMENT="equipment";
	public final static String TIME_SHEET_CLMN_TOTAL_HRS="totalhrs";

	
	//TimeSheetPayroll table column
	public final static String TIME_SHEET_PAY_ROLL_TENANTID="tenantid";
	public final static String TIME_SHEET_PAY_ROLL_EMP_ID="empid";
	public final static String TIME_SHEET_PAY_ROLL_TIME_ENTRY_DTM="timeentrydate";
	public final static String TIME_SHEET_PAY_ROLL_ITEM_NO="lineitem";
	public final static String TIME_SHEET_PAY_ROLL_DEPART="department";
	public final static String TIME_SHEET_PAY_ROLL_FED_CLASS_CODE="fedclasscode";
	public final static String TIME_SHEET_PAY_ROLL_POSITION="position";
	public final static String TIME_SHEET_PAY_ROLL_RATE_CLASS="rateclass";
	public final static String TIME_SHEET_PAY_ROLL_STATE="state";
	public final static String TIME_SHEET_PAY_ROLL_SUTATE="sutastate";
	public final static String TIME_SHEET_PAY_ROLL_UNOIN="union";
	public final static String TIME_SHEET_PAY_ROLL_WORK_COMP="workercomp";

	

//Time Sheet CustomerBilling Table columns
	public final static String TIME_SHEET_CUSTOMER_BILL_TENANTID="tenantid";
	public final static String TIME_SHEET_CUSTOMER_BILL_EMP_ID="empid";
	public final static String TIME_SHEET_CUSTOMER_BILL_TIME_ENTRY_DTM="timeentrydate";
	public final static String TIME_SHEET_CUSTOMER_BILL_ITEM_NO="lineitem";
	public final static String TIME_SHEET_CUSTOMER_BILL_REGULAR_HRS="billoverregularhrs";
	public final static String TIME_SHEET_CUSTOMER_BILL_OVER_TIME_HRS="billoverovertimehrs";
	public final static String TIME_SHEET_CUSTOMER_BILL_PREMIUM_HRS="billoverpremiumhrs";
	public final static String TIME_SHEET_CUSTOMER_BILL_HOLIDAY_HRS="billoverholidayhrs";
	public final static String TIME_SHEET_CUSTOMER_BILL_BILLLED_HRS="billedhrs";
	
	
	public final static String OT_RULE_WEEK_BASIS="Per Week Basis";
	public final static String OT_RULE_DAY_BASIS="Day Basis";
	public final static String OT_RULE_COMBINATION_BASIS="Combination Basis";

	
	//Timesheetnotes table column
	public final static String TIME_SHEET_TRANSCTION_CLMN_KEY="transctiontype";
	public final static String TIME_SHEET_NOTES_CLMN_KEY="notes";
	public final static String TIME_SHEET_EQUIPMENT_CLMN_KEY="equipment";
	public final static String TIME_SHEET_ATTACHMENT_CLMN_KEY="attachments";

	public final static String TIME_SHEET_NOTE_ID_CLMN_KEY="noteid";

	public final static String TIME_SHEET_EQUIPMENT_ID_CLMN_KEY="equipmentid";

	public static final String TIME_SHEET_ENTRY_JSON_WEEK_END_DATE_KEY="weekendDate";


}
