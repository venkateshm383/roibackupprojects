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
public class CrewPositionInsertionService extends AbstractCassandraBean {
	Logger logger = LoggerFactory.getLogger(CrewSetupInsertionService.class);
	ServiceRequestBuilder requestbuilder = new ServiceRequestBuilder();
	static Connection connection;

	/**
	 * method to get connection, get table and columns and call
	 * setCrewPositionToLogData() for insertion of data
	 * 
	 * @throws CassandraConnectionException
	 * @throws InvalidRequestDataException
	 * 
	 */
	@Override
	protected void processBean(Exchange exchange)
			throws TimeTrackerSetupInsertionServiceException {
		logger.debug(".processBean method of CrewInsertData ");
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
				CREW_POSITIONS_TO_LOG_TABLENAME);
		Column tenantID = table.getColumnByName(TENANTID_COLUMN_KEY);
		Column position = table.getColumnByName(POSITION_CODE_COLUMN_KEY);
		Column desription = table.getColumnByName(DESCRIPTION_KEY_COLUMN_KEY);
		try {
			JSONObject requestObj = requestbuilder.getRequestData(exchange);
			logger.debug("requestobj : " + requestObj);

			setCrewPositionToLogData(requestObj, dataContext, table, tenantID,
					position, desription);
		} catch (InvalidRequestDataException e) {
			throw new TimeTrackerSetupInsertionServiceException(
					"Unable to insert Data");
		}
	}

	/**
	 * method to fetch the request data and insert into CrewPositionsToLog DB
	 * 
	 * @param requestObj
	 * @param dataContext
	 * @param table
	 * @param tenantID
	 * @param position
	 * @param desription
	 * @throws InvalidRequestDataException
	 */
	public void setCrewPositionToLogData(JSONObject requestObj,
			UpdateableDataContext dataContext, Table table, Column tenantID,
			Column position, Column desription)
			throws InvalidRequestDataException {
		String tenant = null;
		String positionCode = null;
		String description = null;
		try {
			tenant = requestObj.getString(TENANTID_COLUMN_KEY);
			positionCode = requestObj.getString(POSITION_CODE_COLUMN_KEY);
			description = requestObj.getString(DESCRIPTION_KEY_COLUMN_KEY);
		} catch (JSONException e) {
			throw new InvalidRequestDataException(
					"Request data invalid", e);
		}
		dataContext.executeUpdate((UpdateScript) new InsertInto(table)
				.value(tenantID, tenant).value(position, positionCode)
				.value(desription, description));

	}

}
