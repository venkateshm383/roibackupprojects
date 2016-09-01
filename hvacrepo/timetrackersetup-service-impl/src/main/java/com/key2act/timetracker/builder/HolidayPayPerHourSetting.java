package com.key2act.timetracker.builder;

import static com.key2act.timetracker.util.TimeTrackerConstants.CASSANDRA_TABLE_KEYSPACE;
import static com.key2act.timetracker.util.TimeTrackerConstants.HOLIDAY_DATE_COLUMN_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.HOLIDAY_NAME;
import static com.key2act.timetracker.util.TimeTrackerConstants.HOLIDAY_PAY_PER_HOUR;
import static com.key2act.timetracker.util.TimeTrackerConstants.HOLIDAY_PAY_PER_HOUR_SETUP_TABLENAME;
import static com.key2act.timetracker.util.TimeTrackerConstants.IS_UNBILLED_LABOUR_TRANS_HOLIDAY_COLUMN_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.OVERTIME_CALCULATION_METHOD_COLUMN_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.PAYCODE_FOR_HOLIDAY_WORKED_COLUMN_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.PAYCODE_FOR_OVERTIME_COLUMN_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.PAYCODE_FOR_PREMIUM_COLUMN_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.PAYCODE_FOR_REGULAR_COLUMN_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.PAYCODE_FOR_SALARY_EMPLOYEES_COLUMN_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.STANDARD_NUMBER_OF_BILLABLE_HOURS_PER_DAY_COLUMN_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.STANDARD_NUMBER_OF_BILLABLE_HOURS_PER_WEEK_COLUMN_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.TENANTID_COLUMN_KEY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.Table;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.eventframework.abstractbean.util.CassandraClusterException;
import com.getusroi.permastore.config.IPermaStoreCustomCacheObjectBuilder;
import com.getusroi.permastore.config.jaxb.CustomBuilder;

public class HolidayPayPerHourSetting extends AbstractCassandraBean implements IPermaStoreCustomCacheObjectBuilder {
	static Logger logger = LoggerFactory.getLogger(HolidayPayPerHourSetting.class);

	/**
	 * Method to load the GeneralSetUp PermaObject into the cache
	 * 
	 * @param configBuilderConfig
	 * @return
	 */
	@Override
	public Serializable loadDataForCache(CustomBuilder configBuilderConfig) {
		Cluster cluster = null;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			logger.error("ClusterException, Connection could not be established");
		}

		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, HOLIDAY_PAY_PER_HOUR_SETUP_TABLENAME);
		JSONObject permaObject = getDataSetForEmployeeOTSetUp(table, dataContext);
		return (Serializable) permaObject.toString();
	}

	/**
	 * Fetching the DataSets by selecting all the Column available in the table list
	 * 
	 * @param table
	 * @param dataContext
	 */
	private JSONObject getDataSetForEmployeeOTSetUp(Table table, DataContext dataContext) {
		List<Column> columnList = getColumnsForDataSet(table);
		DataSet dataset = dataContext.query().from(table).select(columnList.get(0), columnList.get(1), 
				columnList.get(2), columnList.get(3))
				.execute();
		JSONObject permaObject = generatePermaObject(dataset, columnList);
		return permaObject;
	}

	/**
	 * Method which will use the dataset Provided and the name of the columns
	 * available in the table, which would create a new jsonobject based on the
	 * tenant name to act as permaCacheObject
	 * 
	 * @param dataset
	 * @param column
	 * @return json object with holidaypayperhour settings
	 */
	private JSONObject generatePermaObject(DataSet dataset, List<Column> column) {
		JSONObject holidayPayPerHourSetUpObject = new JSONObject();
		String tenant = null;
		JSONArray jsonArray = new JSONArray();
		JSONObject holidayPayPerHourSettingsOfTenant = new JSONObject();
		while (dataset.next()) {
			Row row = dataset.getRow();
			if (tenant == null) {
				tenant = row.getValue(column.get(0)).toString();
				try {
					holidayPayPerHourSettingsOfTenant = new JSONObject();
					holidayPayPerHourSettingsOfTenant.put(HOLIDAY_DATE_COLUMN_KEY, row.getValue(column.get(1)));
					holidayPayPerHourSettingsOfTenant.put(HOLIDAY_PAY_PER_HOUR, row.getValue(column.get(2)));
					holidayPayPerHourSettingsOfTenant.put(HOLIDAY_NAME, row.getValue(column.get(3)));
					jsonArray.put(holidayPayPerHourSettingsOfTenant);
				} catch (JSONException e) {
					logger.error("Unable to store PermaStoreConfiguration data for Holiday Payment hours Setting");
				}
			} else if(tenant!=null) {
				try {
					if (holidayPayPerHourSetUpObject.has(row.getValue(column.get(0)).toString())) {
						jsonArray = holidayPayPerHourSetUpObject.getJSONArray(tenant);
					} else {
						holidayPayPerHourSetUpObject.put(tenant, jsonArray);
						tenant = row.getValue(column.get(0)).toString();
						jsonArray = new JSONArray();
					}
					holidayPayPerHourSettingsOfTenant = new JSONObject();
					holidayPayPerHourSettingsOfTenant.put(HOLIDAY_DATE_COLUMN_KEY, row.getValue(column.get(1)));
					holidayPayPerHourSettingsOfTenant.put(HOLIDAY_PAY_PER_HOUR, row.getValue(column.get(2)));
					holidayPayPerHourSettingsOfTenant.put(HOLIDAY_NAME, row.getValue(column.get(3)));
					jsonArray.put(holidayPayPerHourSettingsOfTenant);
				} catch (JSONException e) {
					logger.error("Unable to store PermaStoreCOnfiguration data for Holiday Payment hours Setting");
				}
			}
			try {
				holidayPayPerHourSetUpObject.put(tenant, jsonArray);
			} catch (JSONException e) {
				logger.error("Unable to store PermaStoreCOnfiguration data");
			}
		}
		return holidayPayPerHourSetUpObject;
	}
	
	/**
	 * Computing the HolidayPayPerHour SETUP Database And passing the columns as List
	 * 
	 * @param table
	 * @return list of columns present in the Holiday Pay Per Hour SETUP table
	 */
	public List<Column> getColumnsForDataSet(Table table) {
		Column tenantID = table.getColumnByName(TENANTID_COLUMN_KEY);
		Column holidayDate = table.getColumnByName(HOLIDAY_DATE_COLUMN_KEY);
		Column holidayPayPerHour = table.getColumnByName(HOLIDAY_PAY_PER_HOUR);
		Column holidayName = table.getColumnByName(HOLIDAY_NAME);
		
		List<Column> columnList = new ArrayList<Column>();
		columnList.add(tenantID);
		columnList.add(holidayDate);
		columnList.add(holidayPayPerHour);
		columnList.add(holidayName);
		
		return columnList;
	}

	/**
	 * #TODO to check whether the processBean implementation needs to be changed
	 * or not after having it reviewed
	 * 
	 * @param exchange
	 * @throws Exception
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception {
		// Since it is a void method, its not used to do any processing
	}

}