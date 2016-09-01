package com.key2act.timetracker.insertionservice;

import static com.key2act.timetracker.util.TimeTrackerConstants.*;

import java.sql.Connection;

import org.apache.camel.Exchange;
import org.apache.metamodel.UpdateScript;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.insert.InsertInto;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.Table;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.eventframework.abstractbean.util.CassandraConnectionException;
import com.key2act.timetracker.service.InvalidRequestDataException;
import com.key2act.timetracker.service.helper.ServiceRequestBuilder;

/**
 * 
 * @author bizruntime
 *
 */
public class CustomerBiilingSetupInsertionService extends AbstractCassandraBean{
	Logger logger = LoggerFactory.getLogger(EmployeeOTSetupInsertionService.class);
	ServiceRequestBuilder requestbuilder = new ServiceRequestBuilder();
	static Connection connection;
	
	/**
	 * method to get connection, get table and columns and call
	 * setCustomerBillingSetup() for insertion of data
	 * @throws TimeTrackerSetupInsertionServiceException 
	 */
	@Override
	protected void processBean(Exchange exchange) throws TimeTrackerSetupInsertionServiceException {
		logger.debug(".processBean method of CustomerBiilingSetupDataInserter ");
		try {
			connection = getCassandraConnection();
		} catch (CassandraConnectionException e) {
			try {
				throw new CassandraConnectionException(
						"Unable to get the cassandra connection with the configuration provided.");
			} catch (CassandraConnectionException e1) {
				throw new TimeTrackerSetupInsertionServiceException(
						"Unable to insert SETUP Data");
			}
		}
		UpdateableDataContext dataContext = getUpdateableDataContextForCassandra(connection);
		Table table = getTableForDataContext(dataContext, CUSTOMER_BILLING_SETUP_TABLENAME);
		Column tenantID = table.getColumnByName(TENANTID_COLUMN_KEY);
		Column holidaypaywithotbilling = table.getColumnByName(HOLIDAY_PAY_WITH_PREMIUM_TIME_BILLING);
		Column holidaypaywithpremiumtimebilling = table.getColumnByName(HOLIDAY_PAY_WITH_OT_BILLING);
		Column holidaypaywithregtimebilling = table.getColumnByName(HOLIDAY_PAY_WITH_REGULAT_TIME_BILLING);
		Column isratedifferent = table.getColumnByName(IS_RATE_DIFFERENT);
		Column otpaywithholidaybilling = table.getColumnByName(OT_PAY_WITH_HOLIDAY_BILLING);
		Column otpaywithpremiumtimebilling = table.getColumnByName(OT_PAY_WITH_PREMIUM_TIME_BILLING);
		Column otpaywithregtimebilling = table.getColumnByName(OT_PAY_WITH_REGULAR_TIME_BILLING);
		Column premiumpaywithholidaybilling = table.getColumnByName(PREMIUM_PAY_WITH_HOLIDAY_BILING);
		Column premiumpaywithotbilling = table.getColumnByName(PREMIUM_PAY_WITH_OT_BILLING);
		Column premiumpaywithregtimebilling = table.getColumnByName(PREMIUM_PAY_WITH_REGULAR_TIME_BILLING);
		Column regpaywithholidaybilling = table.getColumnByName(REGULAR_PAY_WITH_HOLIDAY_BILLING);
		Column regpaywithotbilling = table.getColumnByName(REGULAR_PAY_WITH_OT_BILLING);
		Column regpaywithpremiumtimebilling = table.getColumnByName(REGULAR_PAY_WITH_PREMIUM_TIME_BILLING);
		
		try{
		JSONObject requestObj = requestbuilder.getRequestData(exchange);
		logger.debug("requestobj : " + requestObj);
		
		setCustomerBillingSetup(requestObj, dataContext, table, tenantID, holidaypaywithotbilling, holidaypaywithpremiumtimebilling,
				holidaypaywithregtimebilling, isratedifferent, otpaywithholidaybilling, otpaywithpremiumtimebilling, otpaywithregtimebilling,
				premiumpaywithholidaybilling, premiumpaywithotbilling, premiumpaywithregtimebilling, regpaywithholidaybilling, 
				regpaywithotbilling, regpaywithpremiumtimebilling);
		}catch(InvalidRequestDataException e){
			throw new TimeTrackerSetupInsertionServiceException(
					"Unable to insert Data");
		}
		}

	/**
	 * method to fetch the request data and insert into CustomerBilling DB
	 * 
	 * @param requestObj
	 * @param dataContext
	 * @param table
	 * @param tenantID
	 * @param holidaypaywithotbilling
	 * @param holidaypaywithpremiumtimebilling
	 * @param holidaypaywithregtimebilling
	 * @param isratedifferent
	 * @param otpaywithholidaybilling
	 * @param otpaywithpremiumtimebilling
	 * @param otpaywithregtimebilling
	 * @param premiumpaywithholidaybilling
	 * @param premiumpaywithotbilling
	 * @param premiumpaywithregtimebilling
	 * @param regpaywithholidaybilling
	 * @param regpaywithotbilling
	 * @param regpaywithpremiumtimebilling
	 * @throws InvalidRequestDataException
	 */
	public void setCustomerBillingSetup(JSONObject requestObj,
			UpdateableDataContext dataContext, Table table, Column tenantID, Column holidaypaywithotbilling,
			Column holidaypaywithpremiumtimebilling, Column holidaypaywithregtimebilling, Column isratedifferent,
			Column otpaywithholidaybilling, Column otpaywithpremiumtimebilling,	Column otpaywithregtimebilling,
			Column premiumpaywithholidaybilling, Column premiumpaywithotbilling, Column premiumpaywithregtimebilling,
			Column regpaywithholidaybilling, Column regpaywithotbilling, Column regpaywithpremiumtimebilling) throws InvalidRequestDataException {
		logger.debug(".setCustomerBillingSetup method of CustomerBiilingSetupDataInserter ");
		String tenant = null;
		String holidayPayWithPremiumTimeBilling = null;
		String holidayPayWithOTBilling = null;
		String holidayPayWithRegTimeBilling = null;
		boolean isRateDifferent = false;
		String otPayWithHolidayBilling = null;
		String otPayWithPremiumTimeBilling = null;
		String otPayWithRegTimeBilling = null;
		String premiumPayWithHolidayBilling = null;
		String premiumPayWithOTBilling = null;
		String premiumPayWithRegTimeBilling = null;
		String regPayWithHolidayBilling = null;
		String regPayWithOTBilling = null;
		String regPayWithPremiumTimeBilling = null;
		
		try{
			tenant = requestObj.getString(TENANTID_COLUMN_KEY);
			holidayPayWithPremiumTimeBilling = requestObj.getString(HOLIDAY_PAY_WITH_PREMIUM_TIME_BILLING);
			holidayPayWithOTBilling = requestObj.getString(HOLIDAY_PAY_WITH_OT_BILLING);
			holidayPayWithRegTimeBilling = requestObj.getString(HOLIDAY_PAY_WITH_REGULAT_TIME_BILLING);
			isRateDifferent = requestObj.getBoolean(IS_RATE_DIFFERENT);
			otPayWithHolidayBilling = requestObj.getString(OT_PAY_WITH_HOLIDAY_BILLING);
			otPayWithPremiumTimeBilling = requestObj.getString(OT_PAY_WITH_PREMIUM_TIME_BILLING);
			otPayWithRegTimeBilling = requestObj.getString(OT_PAY_WITH_REGULAR_TIME_BILLING);
			premiumPayWithHolidayBilling = requestObj.getString(PREMIUM_PAY_WITH_HOLIDAY_BILING);
			premiumPayWithOTBilling = requestObj.getString(PREMIUM_PAY_WITH_OT_BILLING);
			premiumPayWithRegTimeBilling = requestObj.getString(PREMIUM_PAY_WITH_REGULAR_TIME_BILLING);
			regPayWithHolidayBilling = requestObj.getString(REGULAR_PAY_WITH_HOLIDAY_BILLING);
			regPayWithOTBilling = requestObj.getString(REGULAR_PAY_WITH_OT_BILLING);
			regPayWithPremiumTimeBilling = requestObj.getString(REGULAR_PAY_WITH_PREMIUM_TIME_BILLING);
		}catch(JSONException e){
			throw new InvalidRequestDataException("Request data invalid", e);
		}
		
		dataContext.executeUpdate((UpdateScript) new InsertInto(table)
		.value(tenantID, tenant).value(holidaypaywithotbilling, holidayPayWithPremiumTimeBilling)
		.value(holidaypaywithpremiumtimebilling, holidayPayWithOTBilling)
		.value(holidaypaywithregtimebilling, holidayPayWithRegTimeBilling).value(isratedifferent, isRateDifferent).value(otpaywithholidaybilling, otPayWithHolidayBilling)
		.value(otpaywithpremiumtimebilling, otPayWithPremiumTimeBilling).value(otpaywithregtimebilling, otPayWithRegTimeBilling).value(premiumpaywithholidaybilling, premiumPayWithHolidayBilling)
		.value(premiumpaywithotbilling, premiumPayWithOTBilling).value(premiumpaywithregtimebilling, premiumPayWithRegTimeBilling).value(regpaywithholidaybilling, regPayWithHolidayBilling)
		.value(regpaywithotbilling, regPayWithOTBilling).value(regpaywithpremiumtimebilling, regPayWithPremiumTimeBilling));
		
	}

}
