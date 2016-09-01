package com.key2act.timetracker.insertionservice;

import static com.key2act.timetracker.util.TimeTrackerConstants.*;
import static com.key2act.timetracker.util.TimeTrackerConstants.IS_EMPLOYEE_PERMISSION_LOG_COLUMN_KEY;

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
public class GeneralSetupInsertionService extends AbstractCassandraBean {
	Logger logger = LoggerFactory.getLogger(GeneralSetupInsertionService.class);
	ServiceRequestBuilder requestbuilder = new ServiceRequestBuilder();
	static Connection connection;

	/**
	 * method to get connection, get table and columns and call
	 * setGeneralSetUpTableColumn() and setHolidayPayPerHourSetupTableColumn()
	 * for fetching respective columns of DB tables
	 * 
	 * @throws TimeTrackerSetupInsertionServiceException
	 * 
	 */
	@Override
	protected void processBean(Exchange exchange)
			throws TimeTrackerSetupInsertionServiceException {
		logger.debug(".processBean method of GeneralSetupDataInserter ");
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
		Table table = getTableForDataContext(dataContext,
				GENERAL_SETUP_TABLENAME);
		Table table_holiday = getTableForDataContext(dataContext,
				HOLIDAY_PAY_PER_HOUR_SETUP_TABLENAME);

		try {
			JSONObject requestObj = requestbuilder.getRequestData(exchange);
			logger.debug("requestobj : " + requestObj);

			setHolidayPayPerHourSetupTableColumn(requestObj, dataContext,
					table_holiday);
			setGeneralSetUpTableColumn(requestObj, dataContext, table);
		} catch (InvalidRequestDataException e) {
			throw new TimeTrackerSetupInsertionServiceException(
					"Unable to insert Data");
		}
	}

	/**
	 * method to fetch the column names and call setGeneralSetUpData() for
	 * inserting data
	 * 
	 * @param requestObj
	 * @param dataContext
	 * @param table
	 * @throws TimeTrackerSetupInsertionServiceException
	 */
	public void setGeneralSetUpTableColumn(JSONObject requestObj,
			UpdateableDataContext dataContext, Table table)
			throws TimeTrackerSetupInsertionServiceException {
		logger.debug(".setGeneralSetUpTableColumn method of GeneralSetupDataInserter ");
		Column tenantID = table.getColumnByName(TENANTID_COLUMN_KEY);
		Column isemppermissionlogcrew = table
				.getColumnByName(IS_EMPLOYEE_PERMISSION_LOG_CREW_COLUMN_KEY);
		Column isunbilledlabortransholiday = table
				.getColumnByName(IS_UNBILLED_LABOUR_TRANS_HOLIDAY_COLUMN_KEY);
		Column otcalcmethod = table
				.getColumnByName(OVERTIME_CALCULATION_METHOD_COLUMN_KEY);
		Column paycodeholidayworked = table
				.getColumnByName(PAYCODE_FOR_HOLIDAY_WORKED_COLUMN_KEY);
		Column paycodeovertime = table
				.getColumnByName(PAYCODE_FOR_OVERTIME_COLUMN_KEY);
		Column paycodepremium = table
				.getColumnByName(PAYCODE_FOR_PREMIUM_COLUMN_KEY);
		Column paycoderegular = table
				.getColumnByName(PAYCODE_FOR_REGULAR_COLUMN_KEY);
		Column paycodesalaryemployees = table
				.getColumnByName(PAYCODE_FOR_SALARY_EMPLOYEES_COLUMN_KEY);
		Column stdnumofhoursday = table
				.getColumnByName(STANDARD_NUMBER_OF_BILLABLE_HOURS_PER_DAY_COLUMN_KEY);
		Column stdnumofhoursweek = table
				.getColumnByName(STANDARD_NUMBER_OF_BILLABLE_HOURS_PER_WEEK_COLUMN_KEY);

		try {
			setGeneralSetUpData(requestObj, dataContext, table, tenantID,
					isemppermissionlogcrew, isunbilledlabortransholiday,
					otcalcmethod, paycodeholidayworked, paycodeovertime,
					paycodepremium, paycoderegular, paycodesalaryemployees,
					stdnumofhoursday, stdnumofhoursweek);
		} catch (InvalidRequestDataException e) {
			throw new TimeTrackerSetupInsertionServiceException(
					"Unable to insert Data");
		}

	}

	/**
	 * method to fetch the request data and insert into GeneralSetup DB
	 * 
	 * @param requestObj
	 * @param dataContext
	 * @param table
	 * @param tenantID
	 * @param isemppermissionlogcrew
	 * @param isunbilledlabortransholiday
	 * @param otcalcmethod
	 * @param paycodeholidayworked
	 * @param paycodeovertime
	 * @param paycodepremium
	 * @param paycoderegular
	 * @param paycodesalaryemployees
	 * @param stdnumofhoursday
	 * @param stdnumofhoursweek
	 * @throws InvalidRequestDataException
	 */
	public void setGeneralSetUpData(JSONObject requestObj,
			UpdateableDataContext dataContext, Table table, Column tenantID,
			Column isemppermissionlogcrew, Column isunbilledlabortransholiday,
			Column otcalcmethod, Column paycodeholidayworked,
			Column paycodeovertime, Column paycodepremium,
			Column paycoderegular, Column paycodesalaryemployees,
			Column stdnumofhoursday, Column stdnumofhoursweek)
			throws InvalidRequestDataException {
		logger.debug(".setBatchSetUpData method of BatchSetupDataInserter ");
		String tenant = null;
		boolean ispermissiontologcrew = false;
		boolean islabortransholidayunbilled = false;
		String otcalc = null;
		String paycodeforholidayworked = null;
		String paycodeforovertime = null;
		String paycodeforpremium = null;
		String paycodeforregular = null;
		String paycodeforsalaryemployees = null;
		String stdnumofhoursperday = null;
		String stdnumofhoursperweek = null;
		try {
			tenant = requestObj.getString(TENANTID_COLUMN_KEY);
			ispermissiontologcrew = requestObj
					.getBoolean(IS_EMPLOYEE_PERMISSION_LOG_CREW_COLUMN_KEY);
			islabortransholidayunbilled = requestObj
					.getBoolean(IS_UNBILLED_LABOUR_TRANS_HOLIDAY_COLUMN_KEY);
			otcalc = requestObj
					.getString(OVERTIME_CALCULATION_METHOD_COLUMN_KEY);
			paycodeforholidayworked = requestObj
					.getString(PAYCODE_FOR_HOLIDAY_WORKED_COLUMN_KEY);
			paycodeforovertime = requestObj
					.getString(PAYCODE_FOR_OVERTIME_COLUMN_KEY);
			paycodeforpremium = requestObj
					.getString(PAYCODE_FOR_PREMIUM_COLUMN_KEY);
			paycodeforregular = requestObj
					.getString(PAYCODE_FOR_REGULAR_COLUMN_KEY);
			paycodeforsalaryemployees = requestObj
					.getString(PAYCODE_FOR_SALARY_EMPLOYEES_COLUMN_KEY);
			stdnumofhoursperday = requestObj
					.getString(STANDARD_NUMBER_OF_BILLABLE_HOURS_PER_DAY_COLUMN_KEY);
			stdnumofhoursperweek = requestObj
					.getString(STANDARD_NUMBER_OF_BILLABLE_HOURS_PER_WEEK_COLUMN_KEY);
		} catch (JSONException e) {
			throw new InvalidRequestDataException("Request data invalid", e);
		}

		dataContext
				.executeUpdate((UpdateScript) new InsertInto(table)
						.value(tenantID, tenant)
						.value(isemppermissionlogcrew, ispermissiontologcrew)
						.value(isunbilledlabortransholiday,
								islabortransholidayunbilled)
						.value(otcalcmethod, otcalc)
						.value(paycodeholidayworked, paycodeforholidayworked)
						.value(paycodeovertime, paycodeforovertime)
						.value(paycodepremium, paycodeforpremium)
						.value(paycoderegular, paycodeforregular)
						.value(paycodesalaryemployees,
								paycodeforsalaryemployees)
						.value(stdnumofhoursday, stdnumofhoursperday)
						.value(stdnumofhoursweek, stdnumofhoursperweek));
	}

	/**
	 * method to fetch the column names and call setHolidayPayPerHourSetupData()
	 * for inserting data
	 * 
	 * @param requestObj
	 * @param dataContext
	 * @param table_holiday
	 * @throws TimeTrackerSetupInsertionServiceException
	 */
	public void setHolidayPayPerHourSetupTableColumn(JSONObject requestObj,
			UpdateableDataContext dataContext, Table table_holiday)
			throws TimeTrackerSetupInsertionServiceException {
		logger.debug(".setHolidayPayPerHourSetupTableColumn method of GeneralSetupDataInserter ");
		Column tenantID = table_holiday.getColumnByName(TENANTID_COLUMN_KEY);
		Column holidaydate = table_holiday
				.getColumnByName(HOLIDAY_DATE_COLUMN_KEY);
		Column holidayname = table_holiday.getColumnByName(HOLIDAY_NAME);
		Column holidaypayperhour = table_holiday
				.getColumnByName(HOLIDAY_PAY_PER_HOUR);

		try {
			setHolidayPayPerHourSetupData(requestObj, dataContext,
					table_holiday, tenantID, holidaydate, holidayname,
					holidaypayperhour);
		} catch (InvalidRequestDataException e) {
			throw new TimeTrackerSetupInsertionServiceException(
					"Unable to insert Data");
		}
	}

	/**
	 * method to fetch the request data and insert into HolidayPayPerHourSetup
	 * DB
	 * 
	 * @param requestObj
	 * @param dataContext
	 * @param table_holiday
	 * @param tenantID
	 * @param holidaydate
	 * @param holidayname
	 * @param holidaypayperhour
	 * @throws InvalidRequestDataException
	 */
	public void setHolidayPayPerHourSetupData(JSONObject requestObj,
			UpdateableDataContext dataContext, Table table_holiday,
			Column tenantID, Column holidaydate, Column holidayname,
			Column holidaypayperhour) throws InvalidRequestDataException {
		logger.debug(".setHolidayPayPerHourSetupData method of GeneralSetupDataInserter ");
		String tenant = null;
		String holidayDate = null;
		String holidayName = null;
		String holidayPayPerHour = null;
		try {
			tenant = requestObj.getString(TENANTID_COLUMN_KEY);
			holidayDate = requestObj.getString(HOLIDAY_DATE_COLUMN_KEY);
			holidayName = requestObj.getString(HOLIDAY_NAME);
			holidayPayPerHour = requestObj.getString(HOLIDAY_PAY_PER_HOUR);
		} catch (JSONException e) {
			throw new InvalidRequestDataException("Request data invalid", e);
		}

		dataContext.executeUpdate((UpdateScript) new InsertInto(table_holiday)
				.value(tenantID, tenant).value(holidaydate, holidayDate)
				.value(holidayname, holidayName)
				.value(holidaypayperhour, holidayPayPerHour));

	}
}
