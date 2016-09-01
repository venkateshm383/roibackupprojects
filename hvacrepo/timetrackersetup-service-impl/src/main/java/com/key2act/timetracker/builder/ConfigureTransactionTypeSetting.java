package com.key2act.timetracker.builder;

import static com.key2act.timetracker.util.TimeTrackerConstants.*;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.eventframework.abstractbean.util.CassandraClusterException;
import com.getusroi.permastore.config.IPermaStoreCustomCacheObjectBuilder;
import com.getusroi.permastore.config.jaxb.CustomBuilder;

/**
 * 
 * @author bizruntime
 * 
 */
public class ConfigureTransactionTypeSetting extends AbstractCassandraBean
		implements IPermaStoreCustomCacheObjectBuilder {
	Logger logger = LoggerFactory
			.getLogger(ConfigureTransactionTypeSetting.class);

	/**
	 * Method to load the CustomerBillingSetUp PermaObject into the cache
	 * 
	 * @param configBuilderConfig
	 * @return
	 */
	@Override
	public Serializable loadDataForCache(CustomBuilder configBuilderConfig) {
		Cluster cluster = null;
		Session session = null;
		try {
			cluster = getCassandraCluster();
			session = cluster.connect(CASSANDRA_TABLE_KEYSPACE);
		} catch (CassandraClusterException e) {
			logger.error("ClusterException, Connection could not be established");
		}

		JSONObject permaObject = generatePermaObject(session);
		return (Serializable) permaObject.toString();
	}

	/**
	 * Method which will use the resultset provided and a simpleStatement to execute and fetch the transaction types,
	 * which would create a new jsonobject based on the tenant name to act as permaCacheObject
	 * 
	 * @param dataset
	 * @param column
	 * @return json object with transactiontype settings
	 */
	private JSONObject generatePermaObject(Session session) {
		org.json.JSONObject transactionTypeSetUpObject = new org.json.JSONObject();
		Statement stmt = new SimpleStatement(
				"SELECT * FROM key2act.transactionType ");
		// stmt.setFetchSize(100);
		ResultSet result = session.execute(stmt);

		Iterator<com.datastax.driver.core.Row> iterator = result.iterator();
		while (iterator.hasNext()) {
			Row row = (Row) iterator.next();
			String tenantid = row.getString(0);
			List<String> listed = row.getList(1, String.class);
			logger.debug("tenantID :" + tenantid + " " + "transactiontypes :"
					+ listed);
			JSONObject transactionTypeSettingsOfTenant = new JSONObject();
			try {
				transactionTypeSettingsOfTenant.put(TRANSACTION_TYPE_COLUMN_KEY,row.getList(1, String.class));
				transactionTypeSetUpObject.put(row.getString(0).toString(),
						transactionTypeSettingsOfTenant);
			} catch (JSONException e) {
				logger.error("Unable to store PermaStoreCOnfiguration data for Transaction Type Setting");
			} finally {
				session.close();
			}
		}
		return transactionTypeSetUpObject;
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
