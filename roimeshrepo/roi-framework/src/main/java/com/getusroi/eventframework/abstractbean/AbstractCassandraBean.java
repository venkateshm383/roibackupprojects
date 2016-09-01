package com.getusroi.eventframework.abstractbean;

import static com.getusroi.eventframework.abstractbean.util.CassandraUtil.CONFIG_PROPERTY_FILE;
import static com.getusroi.eventframework.abstractbean.util.CassandraUtil.DRIVER_CLASS_KEY;
import static com.getusroi.eventframework.abstractbean.util.CassandraUtil.URL_KEY;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.camel.Exchange;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.DataContextFactory;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.schema.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.getusroi.eventframework.abstractbean.util.CassandraClusterException;
import com.getusroi.eventframework.abstractbean.util.CassandraConnectionException;
import com.getusroi.eventframework.abstractbean.util.CassandraUtil;
import com.getusroi.eventframework.abstractbean.util.ConnectionConfigurationException;

public abstract class AbstractCassandraBean extends AbstractROICamelBean{
	Logger logger=LoggerFactory.getLogger(AbstractCassandraBean.class);

	/**
	 * This method is used to get the cassandra connection
	 * @return
	 * @throws CassandraConnectionException
	 */
	protected Connection getCassandraConnection() throws CassandraConnectionException{
		logger.debug(".getCassandraConnection method of AbstractCassandraBean ");
		Connection con = null;
		Properties prop=null;
		try {
			prop = CassandraUtil.getCassandraConfigProperties();
			try {
				Class.forName(prop.getProperty(DRIVER_CLASS_KEY));
				try {
					con = DriverManager
							.getConnection(prop.getProperty(URL_KEY));
					logger.debug("Connection Object : "+con);
					return con;
				} catch (SQLException e) {
					throw new CassandraConnectionException("unable to get the connection object for cassandra : "+prop.getProperty(URL_KEY),e);
				}
			} catch (ClassNotFoundException e) {
				throw new CassandraConnectionException("unable to load the driver name for cassandra : "+prop.getProperty(DRIVER_CLASS_KEY),e);
			}
		} catch (ConnectionConfigurationException e1) {
			throw new CassandraConnectionException("unable to get the connection object for cassandra : "+prop.getProperty(CONFIG_PROPERTY_FILE),e1);
		}
		
		
	}//end of method
	
	protected Cluster getCassandraCluster() throws CassandraClusterException{
		logger.debug(".getCassandraCluster method of AbstractCassandraBean ");
		Cluster cluster = null;
		Properties prop=null;
		try {
			prop = CassandraUtil.getCassandraConfigProperties();
			String host = prop.getProperty(CassandraUtil.HOST_KEY);
			int port = Integer.parseInt(prop.getProperty(CassandraUtil.PORT_KEY));			
			try{
				cluster = Cluster.builder().addContactPoint(host)
						.withPort(port).build();
				logger.debug("cluster Object : "+cluster);
				return cluster;	
			}
			catch(Exception e){
				throw new CassandraClusterException("unable to connect to the host for cassandra : "+prop.getProperty(CassandraUtil.HOST_KEY),e);
			}
		} catch (ConnectionConfigurationException e1) {
			throw new CassandraClusterException("unable to get the connection object for cassandra : "+prop.getProperty(CONFIG_PROPERTY_FILE),e1);
		}		
			
	}//end of method
	
	protected UpdateableDataContext getUpdateableDataContextForCassandra(Connection connection){
		logger.debug(".getUpdateableDataContextForCassandra method of AbstractCassandraBean");
		UpdateableDataContext dataContext = DataContextFactory
				.createJdbcDataContext(connection);
		return dataContext;
	}
	
	protected DataContext getDataContextForCassandraByCluster(Cluster cluster,String keyspace){
		logger.debug(".getDataContextForCassandraByCluster method of AbstractCassandraBean");
		DataContext dataContext = DataContextFactory
				.createCassandraDataContext(cluster, keyspace);
		return dataContext;
	}
	
	protected Table getTableForDataContext(DataContext datacontext,String tableName){
		logger.debug(".getTableForDataContext method of AbstractCassandraBean");
		Table table=datacontext.getTableByQualifiedLabel(tableName);
		return table;

	}
	@Override
	abstract protected void processBean(Exchange exch) throws Exception;

}
