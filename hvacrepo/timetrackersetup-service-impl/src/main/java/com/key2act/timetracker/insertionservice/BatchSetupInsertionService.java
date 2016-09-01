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
public class BatchSetupInsertionService extends AbstractCassandraBean {
	Logger logger = LoggerFactory.getLogger(BatchSetupInsertionService.class);
	ServiceRequestBuilder requestbuilder = new ServiceRequestBuilder();
	static Connection connection;

	/**
	 * method to get connection, get table and columns and call
	 * setBatchSetUpData() for insertion of data
	 * 
	 * @throws TimeTrackerSetupInsertionServiceException
	 */
	@Override
	protected void processBean(Exchange exchange)
			throws TimeTrackerSetupInsertionServiceException {
		logger.debug(".processBean method of BatchDataInserter ");
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
		Table table = getTableForDataContext(dataContext, BATCH_SETUP_TABLENAME);
		Column tenantID = table.getColumnByName(TENANTID_COLUMN_KEY);
		Column batchcomment = table.getColumnByName(BATCH_COMMENT);
		Column endofweek = table.getColumnByName(END_OF_WEEK);
		Column ispostbatchesdaily = table
				.getColumnByName(IS_POST_BATCHES_DAILY);
		Column prefixbatchname = table.getColumnByName(PREFIX_BATCH_NAME);
		try {
			JSONObject requestObj = requestbuilder.getRequestData(exchange);
			logger.debug("requestobj : " + requestObj);

			setBatchSetUpData(requestObj, dataContext, table, tenantID,
					batchcomment, endofweek, ispostbatchesdaily,
					prefixbatchname);
		} catch (InvalidRequestDataException e) {
			throw new TimeTrackerSetupInsertionServiceException(
					"Unable to insert Data");
		}
	}

	/**
	 * method to fetch the request data and insert into BatchSetup DB
	 * 
	 * @param requestObj
	 * @param dataContext
	 * @param table
	 * @param tenantID
	 * @param batchcomment
	 * @param endofweek
	 * @param ispostbatchesdaily
	 * @param prefixbatchname
	 * @throws InvalidRequestDataException
	 */
	public void setBatchSetUpData(JSONObject requestObj,
			UpdateableDataContext dataContext, Table table, Column tenantID,
			Column batchcomment, Column endofweek, Column ispostbatchesdaily,
			Column prefixbatchname) throws InvalidRequestDataException {
		logger.debug(".setBatchSetUpData method of BatchDataInserter ");
		String tenant = null;
		String batchComment = null;
		String weekEndDate = null;
		boolean postbatchesdaily = false;
		String batchnamePrefix = null;

		try {
			tenant = requestObj.getString(TENANTID_COLUMN_KEY);
			batchComment = requestObj.getString(BATCH_COMMENT);
			weekEndDate = requestObj.getString(END_OF_WEEK);
			postbatchesdaily = requestObj.getBoolean(IS_POST_BATCHES_DAILY);
			batchnamePrefix = requestObj.getString(PREFIX_BATCH_NAME);
		} catch (JSONException e) {
			throw new InvalidRequestDataException("Request data invalid", e);
		}

		dataContext.executeUpdate((UpdateScript) new InsertInto(table)
				.value(tenantID, tenant).value(batchcomment, batchComment)
				.value(endofweek, weekEndDate)
				.value(ispostbatchesdaily, postbatchesdaily)
				.value(prefixbatchname, batchnamePrefix));

	}

}
