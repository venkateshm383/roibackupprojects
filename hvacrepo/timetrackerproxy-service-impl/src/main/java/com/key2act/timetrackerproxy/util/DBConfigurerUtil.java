package com.key2act.timetrackerproxy.util;

import static com.key2act.timetrackerproxy.util.TimeTrackerProxyConstants.*;

import org.apache.camel.Exchange;
import org.apache.metamodel.DataContext;
import org.json.JSONException;
import org.json.JSONObject;
import com.datastax.driver.core.Cluster;
import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.eventframework.abstractbean.util.CassandraClusterException;
import com.getusroi.mesh.MeshHeaderConstant;
import com.key2act.timetrackerproxy.service.TimeTrackLookUpServiceException;

public class DBConfigurerUtil extends AbstractCassandraBean {
	
	@Override
	protected void processBean(Exchange exchange) throws Exception {
		String requestString = exchange.getIn().getBody(String.class);
		try {
			JSONObject requestJsonObject = new JSONObject(requestString);
			requestJsonObject = (JSONObject) requestJsonObject.getJSONArray(MeshHeaderConstant.DATA_KEY)
					.getJSONObject(0);
			String tenantID = requestJsonObject.getString(TENANT_ID_REQUEST_KEY);
			Cluster cluster;
			try {
				cluster = getCassandraCluster();
			} catch (CassandraClusterException e) {
				throw new TimeTrackLookUpServiceException("Cannot fetch the Cassandra Cluster from the configuration");
			}

			String keySpace = CASSANDRA_TABLE_KEYSPACE;
			DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		} catch (JSONException e) {
			throw new TimeTrackLookUpServiceException();
		}
	}
}