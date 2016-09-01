package com.key2act.timetracker.builder;

import static com.key2act.timetracker.util.TimeTrackerConstants.*;

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

public class ConfigureEmployeeOvertimeSetup extends AbstractCassandraBean implements IPermaStoreCustomCacheObjectBuilder {
	static Logger logger = LoggerFactory.getLogger(ConfigureEmployeeOvertimeSetup.class);

	/**
	 * Method to load the EmployeeOverTime PermaObject into the cache
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
		Table table = getTableForDataContext(dataContext, EMPLOYEE_OT_SETUP_TABLENAME);
		JSONObject permaObject = getDataSetForEmployeeOTSetUp(table, dataContext);
				return (Serializable) permaObject.toString();
	}

	/**
	 *Fetching the DataSets by selecting all the Column available in the table list
	 * 
	 * @param table
	 * @param dataContext
	 */
	private JSONObject getDataSetForEmployeeOTSetUp(Table table, DataContext dataContext) {
		List<Column> columnList = getColumnsForDataSet(table);
		DataSet dataset = dataContext.query().from(table)
				.select(columnList.get(0), columnList.get(1), columnList.get(2), columnList.get(3), 
						columnList.get(4), columnList.get(5))
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
	 * @return json object with employee overtime settings
	 */
	private JSONObject generatePermaObject(DataSet dataset, List<Column> column) {
		JSONObject employeeOTSetUpObject = new JSONObject();
		String tenant = null;
		JSONArray jsonArray = new JSONArray();
		JSONObject empOTSettingsOfTenant = new JSONObject();
		while (dataset.next()) {
			Row row = dataset.getRow();
			if (tenant == null) {
				tenant = row.getValue(column.get(0)).toString();
				try {
					empOTSettingsOfTenant = new JSONObject();
					empOTSettingsOfTenant.put(EMP_ID_COLUMN_KEY, row.getValue(column.get(1)));
					empOTSettingsOfTenant.put(EMPLOYEE_NAME_COLUMN_KEY, row.getValue(column.get(2)));
					empOTSettingsOfTenant.put(HOURS_PER_DAY_COLUMN_KEY, row.getValue(column.get(3)));
					empOTSettingsOfTenant.put(HOURS_PER_WEEK_COLUMN_KEY, row.getValue(column.get(4)));
					empOTSettingsOfTenant.put(OVERTIME_CALCULATION_METHOD_COLUMN_KEY, row.getValue(column.get(5)));
					jsonArray.put(empOTSettingsOfTenant);
				} catch (JSONException e) {
					logger.error("Unable to store PermaStoreConfiguration data for Employee OT Settings");
				}
			} else if (tenant != null) {
				try {
					if (employeeOTSetUpObject.has(row.getValue(column.get(0)).toString())) {
						jsonArray = employeeOTSetUpObject.getJSONArray(tenant);
					} else {
						employeeOTSetUpObject.put(tenant, jsonArray);
						tenant = row.getValue(column.get(0)).toString();
						jsonArray = new JSONArray();
					}
					empOTSettingsOfTenant = new JSONObject();
					empOTSettingsOfTenant.put(EMP_ID_COLUMN_KEY, row.getValue(column.get(1)));
					empOTSettingsOfTenant.put(EMPLOYEE_NAME_COLUMN_KEY, row.getValue(column.get(2)));
					empOTSettingsOfTenant.put(HOURS_PER_DAY_COLUMN_KEY, row.getValue(column.get(3)));
					empOTSettingsOfTenant.put(HOURS_PER_WEEK_COLUMN_KEY, row.getValue(column.get(4)));
					empOTSettingsOfTenant.put(OVERTIME_CALCULATION_METHOD_COLUMN_KEY, row.getValue(column.get(5)));
					jsonArray.put(empOTSettingsOfTenant);
				} catch (JSONException e) {
					logger.error("Unable to store PermaStoreCOnfiguration data for Employee OT Settings");
				}
			}
			try {
				employeeOTSetUpObject.put(tenant, jsonArray);
			} catch (JSONException e) {
				logger.error("Unable to store PermaStoreConfiguration data");
			}
		}
		return employeeOTSetUpObject;
	}
	
	/**
	 * Computing the EmployeeOverTime SETUP Database And passing the columns as List
	 * 
	 * @param table
	 * @return list of columns present in the Employee OT SETUP table
	 */
	public List<Column> getColumnsForDataSet(Table table) {
		Column tenantID = table.getColumnByName(TENANTID_COLUMN_KEY);
		Column empId = table.getColumnByName(EMP_ID_COLUMN_KEY);
		Column employeeName = table.getColumnByName(EMPLOYEE_NAME_COLUMN_KEY);
		Column hoursPerDay = table.getColumnByName(HOURS_PER_DAY_COLUMN_KEY);
		Column hoursPerWeek = table.getColumnByName(HOURS_PER_WEEK_COLUMN_KEY);
		Column otCalcSetUp = table.getColumnByName(OVERTIME_CALCULATION_METHOD_COLUMN_KEY);
		
		List<Column> columnList = new ArrayList<Column>();
		columnList.add(tenantID);
		columnList.add(empId);
		columnList.add(employeeName);
		columnList.add(hoursPerDay);
		columnList.add(hoursPerWeek);
		columnList.add(otCalcSetUp);
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