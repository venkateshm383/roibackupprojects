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

public class ConfigureCrewPositionsToLogSetting extends AbstractCassandraBean implements IPermaStoreCustomCacheObjectBuilder {
	static Logger logger = LoggerFactory.getLogger(ConfigureCrewPositionsToLogSetting.class);

	/**
	 * Method to load the CrewPositionToLog Settings PermaObject into the cache
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
		Table table = getTableForDataContext(dataContext, CREW_POSITIONS_TO_LOG_TABLENAME);
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
		org.json.JSONObject crewPositionsToLogSetUpObject = new org.json.JSONObject();
		String tenant = null;
		JSONArray jsonArray = new JSONArray();
		JSONObject crewPositionsToLogSettingsOfTenant = new JSONObject();
		while (dataset.next()) {
			Row row = dataset.getRow();
			if (tenant == null) {
				tenant = row.getValue(column.get(0)).toString();
				try {
					crewPositionsToLogSettingsOfTenant = new JSONObject();
					crewPositionsToLogSettingsOfTenant.put(POSITION_CODE_COLUMN_KEY, row.getValue(column.get(1)));
					crewPositionsToLogSettingsOfTenant.put(DESCRIPTION_KEY_COLUMN_KEY, row.getValue(column.get(2)));
					jsonArray.put(crewPositionsToLogSettingsOfTenant);
				} catch (JSONException e) {
					logger.error("Unable to store PermaStoreConfiguration data for Crew Positions to be logged");
				}
			} else if(tenant!=null) {
				try {
					if (crewPositionsToLogSetUpObject.has(row.getValue(column.get(0)).toString())) {
						jsonArray = crewPositionsToLogSetUpObject.getJSONArray(tenant);
					} else {
						crewPositionsToLogSetUpObject.put(tenant, jsonArray);
						tenant = row.getValue(column.get(0)).toString();
						jsonArray = new JSONArray();
					}
					crewPositionsToLogSettingsOfTenant = new JSONObject();
					crewPositionsToLogSettingsOfTenant.put(POSITION_CODE_COLUMN_KEY, row.getValue(column.get(1)));
					crewPositionsToLogSettingsOfTenant.put(DESCRIPTION_KEY_COLUMN_KEY, row.getValue(column.get(2)));
					jsonArray.put(crewPositionsToLogSettingsOfTenant);
				} catch (JSONException e) {
					logger.error("Unable to store PermaStoreConfiguration data for Crew Positions to be logged");
				}
			}
			try {
				crewPositionsToLogSetUpObject.put(tenant, jsonArray);
			} catch (JSONException e) {
				logger.error("Unable to store PermaStoreConfiguration data");
			}
		}
		return crewPositionsToLogSetUpObject;
	}

	/**
	 * Fetching the DataSets by selecting all the Column available in the table list
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
	 * Computing the CrewPositionToLog SETUP Database And passing the columns as List
	 * 
	 * @param table
	 * @return list of columns present in the CrewPositionToLog SETUP table
	 */
	public List<Column> getColumnsForDataSet(Table table) {
		Column tenantID = table.getColumnByName(TENANTID_COLUMN_KEY);
		Column positionCode = table.getColumnByName(POSITION_CODE_COLUMN_KEY);
		Column description = table.getColumnByName(DESCRIPTION_KEY_COLUMN_KEY);
		List<Column> columnList = new ArrayList<Column>();
		columnList.add(tenantID);
		columnList.add(positionCode);
		columnList.add(description);
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