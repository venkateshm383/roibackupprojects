package com.key2act.timetracker;

import static com.key2act.timetracker.util.TimeTrackerConstants.*;

import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.query.SelectItem;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.Table;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.eventframework.abstractbean.util.CassandraClusterException;
import com.google.common.reflect.TypeToken;

public class Test extends AbstractCassandraBean {
	Logger logger = LoggerFactory.getLogger(Test.class);

	@Override
	protected void processBean(Exchange exchange) throws Exception {

	}

	public static void main(String[] args) {
		Test test = new Test();
		test.process();
	}

	@SuppressWarnings("unchecked")
	private void process() {
		Cluster cluster = null;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			logger.error("ClusterException, Connection could not be established");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster,
				keySpace);
		Table table = getTableForDataContext(dataContext,
				TRANSACTION_TYPE_SETUP_TABLENAME);
		Column tenantID = table.getColumnByName(TENANTID_COLUMN_KEY);
		Column trnxtype = table.getColumnByName(TRANSACTION_TYPE_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table)
				.select(tenantID, trnxtype).execute();
		logger.debug("dataset value : "+dataset);
		
		Statement stmt = new SimpleStatement("SELECT * FROM key2act.transactionType ");
		stmt.setFetchSize(100);
		ResultSet result = cluster.newSession().execute(stmt);
		
		Iterator<com.datastax.driver.core.Row> it = result.iterator();
        while(it.hasNext()){
        	logger.debug("inside while");
            Row row = (Row) it.next();
            logger.debug("data : " + row);
            String tenant = row.getString("tenantId");
            logger.debug("tenant : " + tenant);
            String tenantid = row.getString(0);
            logger.debug("tenant : " + tenantid);
            List<String> listed = row.get(1, List.class);
            logger.debug("listed : " + tenantid);
         	List<List> list = row.getList("transactionTypes", List.class);
            logger.debug("data : " + tenant + " " + list);
            }
        }
		
		/*while (dataset.next()) {
			logger.debug("inside while");
			Row row = dataset.getRow();
			logger.debug("data : " + row);
			String tenant = (String) row.getValue(tenantID);
			java.util.List<String> type = (List<String>) row.getValue(trnxtype);
			logger.debug("data : " + tenant + " " + type);
		}*/

	}


