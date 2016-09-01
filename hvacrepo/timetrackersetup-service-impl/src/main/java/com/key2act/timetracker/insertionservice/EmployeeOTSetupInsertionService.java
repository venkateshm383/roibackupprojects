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
public class EmployeeOTSetupInsertionService extends AbstractCassandraBean {
	Logger logger = LoggerFactory
			.getLogger(EmployeeOTSetupInsertionService.class);
	ServiceRequestBuilder requestbuilder = new ServiceRequestBuilder();
	static Connection connection;

	/**
	 * method to get connection, get table and columns and call
	 * setEmployeeOTSetUpData() for insertion of data
	 * 
	 * @throws TimeTrackerSetupInsertionServiceException
	 */
	@Override
	protected void processBean(Exchange exchange)
			throws TimeTrackerSetupInsertionServiceException {
		logger.debug(".processBean method of EmployeeOTSetupDataInserter ");
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
				EMPLOYEE_OT_SETUP_TABLENAME);
		Column tenantID = table.getColumnByName(TENANTID_COLUMN_KEY);
		Column empid = table.getColumnByName(EMP_ID_COLUMN_KEY);
		Column empname = table.getColumnByName(EMPLOYEE_NAME_COLUMN_KEY);
		Column hoursperday = table.getColumnByName(HOURS_PER_DAY_COLUMN_KEY);
		Column hoursperweek = table.getColumnByName(HOURS_PER_WEEK_COLUMN_KEY);
		Column otcalcmethod = table.getColumnByName(OT_CALC_METHOD_COLOUM_KEY);

		try {
			JSONObject requestObj = requestbuilder.getRequestData(exchange);
			logger.debug("requestobj : " + requestObj);

			setEmployeeOTSetUpData(requestObj, dataContext, table, tenantID,
					empid, empname, hoursperday, hoursperweek, otcalcmethod);
		} catch (InvalidRequestDataException e) {
			throw new TimeTrackerSetupInsertionServiceException(
					"Unable to insert Data");
		}
	}

	/**
	 * method to fetch the request data and insert into EmployeeOverTime DB
	 * 
	 * @param requestObj
	 * @param dataContext
	 * @param table
	 * @param tenantID
	 * @param empid
	 * @param empname
	 * @param hoursperday
	 * @param hoursperweek
	 * @param otcalcmethod
	 * @throws InvalidRequestDataException
	 */
	public void setEmployeeOTSetUpData(JSONObject requestObj,
			UpdateableDataContext dataContext, Table table, Column tenantID,
			Column empid, Column empname, Column hoursperday,
			Column hoursperweek, Column otcalcmethod)
			throws InvalidRequestDataException {
		logger.debug(".setEmployeeOTSetUpData method of EmployeeOTSetupDataInserter ");
		String tenant = null;
		String empId = null;
		String empName = null;
		String hoursPerDay = null;
		String hoursPerWeek = null;
		String otCalcMethod = null;

		try {
			tenant = requestObj.getString(TENANTID_COLUMN_KEY);
			empId = requestObj.getString(EMP_ID_COLUMN_KEY);
			empName = requestObj.getString(EMPLOYEE_NAME_COLUMN_KEY);
			hoursPerDay = requestObj.getString(HOURS_PER_DAY_COLUMN_KEY);
			hoursPerWeek = requestObj.getString(HOURS_PER_WEEK_COLUMN_KEY);
			otCalcMethod = requestObj.getString(OT_CALC_METHOD_COLOUM_KEY);
		} catch (JSONException e) {
			throw new InvalidRequestDataException("Request data invalid", e);
		}

		dataContext.executeUpdate((UpdateScript) new InsertInto(table)
				.value(tenantID, tenant).value(empid, empId)
				.value(empname, empName).value(hoursperday, hoursPerDay)
				.value(hoursperweek, hoursPerWeek)
				.value(otcalcmethod, otCalcMethod));
	}

}
