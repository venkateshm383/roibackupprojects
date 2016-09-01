package com.key2act.timetracker.insertionservice;

import static com.key2act.timetracker.util.TimeTrackerConstants.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

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

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.eventframework.abstractbean.util.CassandraClusterException;
import com.getusroi.eventframework.abstractbean.util.CassandraConnectionException;
import com.key2act.timetracker.service.InvalidRequestDataException;
import com.key2act.timetracker.service.helper.ServiceRequestBuilder;

/**
 * 
 * @author bizruntime
 * 
 */
public class CrewSetupInsertionService extends AbstractCassandraBean {
	Logger logger = LoggerFactory.getLogger(CrewSetupInsertionService.class);
	ServiceRequestBuilder requestbuilder = new ServiceRequestBuilder();
	static Connection connection;

	/**
	 * method to get connection, get table and columns and call
	 * setCrewSetUpData() for insertion of data
	 * @throws TimeTrackerSetupInsertionServiceException 
	 * 
	 * @throws CassandraConnectionException
	 * @throws InvalidRequestDataException
	 * @throws CassandraClusterException
	 * @throws JSONException
	 * 
	 */
	@Override
	protected void processBean(Exchange exchange) throws TimeTrackerSetupInsertionServiceException
			  {
		logger.debug(".processBean method of CrewDataInserter ");
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
		Table table = getTableForDataContext(dataContext, CREW_SETUP_TABLENAME);
		Column tenantID = table.getColumnByName(TENANTID_COLUMN_KEY);
		Column empId = table.getColumnByName(EMP_ID_COLUMN_KEY);
		Column employeeName = table.getColumnByName(EMPLOYEE_NAME_COLUMN_KEY);
		try{
		JSONObject requestObj = requestbuilder.getRequestData(exchange);
		logger.debug("requestobj : " + requestObj);

		setCrewSetUpData(requestObj, dataContext, table, tenantID, empId,
				employeeName);
		}catch(InvalidRequestDataException e){
			throw new TimeTrackerSetupInsertionServiceException(
					"Unable to insert Data");
		}
	}

	/**
	 * method to fetch the request data and insert into CrewSetup DB
	 * 
	 * @param requestObj
	 * @param dataContext
	 * @param table
	 * @param tenantID
	 * @param empId
	 * @param employeeName
	 * @param isEmpPermissionLog
	 * @throws InvalidRequestDataException
	 */
	public void setCrewSetUpData(JSONObject requestObj,
			UpdateableDataContext dataContext, Table table, Column tenantID,
			Column empId, Column employeeName)
			throws InvalidRequestDataException {
		logger.debug(".setCrewSetUpData method of CrewDataInserter ");
		String tenant = null;
		String empid = null;
		String empname = null;
		try {
			tenant = requestObj.getString(TENANTID_COLUMN_KEY	);
			empid = requestObj.getString(EMP_ID_COLUMN_KEY);
			empname = requestObj.getString(EMPLOYEE_NAME_COLUMN_KEY);
		} catch (JSONException e) {
			throw new InvalidRequestDataException("Request data invalid", e);
		}
		dataContext.executeUpdate((UpdateScript) new InsertInto(table)
				.value(tenantID, tenant).value(empId, empid)
				.value(employeeName, empname));
	}

}
