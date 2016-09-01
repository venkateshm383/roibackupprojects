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

public class ConfigureCrewSetting extends AbstractCassandraBean implements IPermaStoreCustomCacheObjectBuilder {
	static Logger logger = LoggerFactory.getLogger(ConfigureCrewSetting.class);

	/**
	 * Method to load the Crew Setup PermaObject into the cache
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
		Table table = getTableForDataContext(dataContext, CREW_SETUP_TABLENAME);
		JSONObject permaObject = getDataSetForCrewSetUp(table, dataContext);
		return (Serializable) permaObject.toString();
	}

	/**
	 * Method which will use the dataset Provided and the name of the columns
	 * available in the table, which would create a new jsonobject based on the
	 * tenant name to act as permaCacheObject
	 * 
	 * @param dataset
	 * @param column
	 * @return json object with crew settings
	 */
	private JSONObject generatePermaObject(DataSet dataset, List<Column> column) {
		JSONObject crewSetupObject = new JSONObject();
		String tenant = null;
		JSONArray jsonArray = new JSONArray();
		JSONObject crewSettingsOfTenant = new JSONObject();
		while (dataset.next()) {
			Row row = dataset.getRow();
			if (tenant == null) {
				tenant = row.getValue(column.get(0)).toString();
				try {
					crewSettingsOfTenant = new JSONObject();
					crewSettingsOfTenant.put(EMP_ID_COLUMN_KEY, row.getValue(column.get(1)));
					crewSettingsOfTenant.put(EMPLOYEE_NAME_COLUMN_KEY, row.getValue(column.get(2)));
//					crewSettingsOfTenant.put(IS_EMPLOYEE_PERMISSION_LOG_COLUMN_KEY, row.getValue(isEmpPermissionLog));
					jsonArray.put(crewSettingsOfTenant);
				} catch (JSONException e) {
					logger.error("Unable to store PermaStoreConfiguration data for Crew Setting");
				}
			} else if (tenant != null) {
				try {
					if (crewSetupObject.has(row.getValue(column.get(0)).toString())) {
						jsonArray = crewSetupObject.getJSONArray(tenant);
					} else {
						crewSetupObject.put(tenant, jsonArray);
						tenant = row.getValue(column.get(0)).toString();
						jsonArray = new JSONArray();
					}
					crewSettingsOfTenant = new JSONObject();
					crewSettingsOfTenant.put(EMP_ID_COLUMN_KEY, row.getValue(column.get(1)));
					crewSettingsOfTenant.put(EMPLOYEE_NAME_COLUMN_KEY, row.getValue(column.get(2)));
//					crewSettingsOfTenant.put(IS_EMPLOYEE_PERMISSION_LOG_COLUMN_KEY, row.getValue(isEmpPermissionLog));
					jsonArray.put(crewSettingsOfTenant);
				} catch (JSONException e) {
					logger.error("Unable to store PermaStoreCOnfiguration data for Crew Setting");
				}
			}
			try {
				crewSetupObject.put(tenant, jsonArray);
			} catch (JSONException e) {
				logger.error("Error Occured");
			}
		}
		return crewSetupObject;
	}

	/**
	 *  Fetching the DataSets by selecting all the Column available in the table list
	 * 
	 * @param table
	 * @param dataContext
	 */
	public JSONObject getDataSetForCrewSetUp(Table table, DataContext dataContext) {

		List<Column> columnList = getColumnsForDataSet(table);
		DataSet dataset = dataContext.query().from(table).select(columnList.get(0), columnList.get(1), columnList.get(2))
				.execute();
		JSONObject permaObject = generatePermaObject(dataset, columnList);
		return permaObject;
	}
	
	/**
	 * Computing the Crew SETUP Database And passing the columns as List
	 * 
	 * @param table
	 * @return list of columns present in the Crew SETUP table
	 */
	public List<Column> getColumnsForDataSet(Table table) {
		Column tenantID = table.getColumnByName(TENANTID_COLUMN_KEY);
//		Column isEmpPermissionLog = table.getColumnByName(IS_EMPLOYEE_PERMISSION_LOG_CREW_COLUMN_KEY);
		Column empId = table.getColumnByName(EMP_ID_COLUMN_KEY);
		Column employeeName = table.getColumnByName(EMPLOYEE_NAME_COLUMN_KEY);
		List<Column> columnList = new ArrayList<Column>();
		columnList.add(tenantID);
//		columnList.add(isEmpPermissionLog);
		columnList.add(empId);
		columnList.add(employeeName);
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