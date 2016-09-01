package com.key2act.timetracker.builder;

import static com.key2act.timetracker.util.TimeTrackerConstants.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.query.SelectItem;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.Table;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.eventframework.abstractbean.util.CassandraClusterException;
import com.getusroi.permastore.config.IPermaStoreCustomCacheObjectBuilder;
import com.getusroi.permastore.config.jaxb.CustomBuilder;

public class ConfigureBatchSetting extends AbstractCassandraBean implements
		IPermaStoreCustomCacheObjectBuilder {
	static Logger logger = LoggerFactory.getLogger(ConfigureBatchSetting.class);

	/**
	 * Method to load the BatchSetting PermaObject into the cache
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
		DataContext dataContext = getDataContextForCassandraByCluster(cluster,
				keySpace);
		Table table = getTableForDataContext(dataContext, BATCH_SETUP_TABLENAME);
		JSONObject permaObject = getDataSetForBatchSetUp(table,
				dataContext);
		return (Serializable) permaObject.toString();
	}

	/**
	 * Fetching the DataSets by selecting all the Column available in the table list
	 * 
	 * @param table
	 * @param dataContext
	 */
	private JSONObject getDataSetForBatchSetUp(Table table,
			DataContext dataContext) {
		List<Column> columnList = getColumnsForDataSet(table);
		DataSet dataset = dataContext
				.query()
				.from(table)
				.select(columnList.get(0), columnList.get(1), columnList.get(2), columnList.get(3),
						columnList.get(4)).execute();
		JSONObject permaObject = generatePermaObject(dataset, columnList);
		return permaObject;
	}

	/**
	 * Computing the Batch SETUP Database And passing the columns as List
	 * 
	 * @param table
	 * @return list of columns present in the Batch SETUP table
	 */
	public List<Column> getColumnsForDataSet(Table table) {
		Column tenantID = table.getColumnByName(TENANTID_COLUMN_KEY);
		Column batchcomment = table.getColumnByName(BATCH_COMMENT);
		Column endofweek = table.getColumnByName(END_OF_WEEK);
		Column ispostbatchesdaily = table.getColumnByName(IS_POST_BATCHES_DAILY);
		Column prefixbatchname = table.getColumnByName(PREFIX_BATCH_NAME);
		List<Column> columnList = new ArrayList<Column>();
		columnList.add(tenantID);
		columnList.add(batchcomment);
		columnList.add(endofweek);
		columnList.add(ispostbatchesdaily);
		columnList.add(prefixbatchname);
		return columnList;
	}

	/**
	 * Method which will use the dataset Provided and the List of the columns
	 * available in the table, which would create a new jsonobject based on the
	 * tenant name to act as permaCacheObject
	 * 
	 * @param dataset
	 * @param column
	 * @return json object with batch settings
	 */
	private JSONObject generatePermaObject(DataSet dataset, List<Column> column) {
		org.json.JSONObject batchSetUpObject = new org.json.JSONObject();
		while (dataset.next()) {
			Row row = dataset.getRow();
			try {
				JSONObject batchSettingsOfTenant = new JSONObject();
				batchSettingsOfTenant.put(BATCH_COMMENT, row.getValue(column.get(1)));
				batchSettingsOfTenant.put(END_OF_WEEK, row.getValue(column.get(2)));
				batchSettingsOfTenant.put(IS_POST_BATCHES_DAILY, row.getValue(column.get(3)));
				batchSettingsOfTenant.put(PREFIX_BATCH_NAME, row.getValue(column.get(4)));
				batchSetUpObject.put(row.getValue(column.get(0)).toString(), batchSettingsOfTenant);
			} catch (JSONException e) {
				logger.error("Unable to store PermaStoreConfiguration data for Batch Setting");
			}
		}
		return batchSetUpObject;
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
		// Since it is a void method, its not used to do any processing for Configuring Batch Settings
	}

}