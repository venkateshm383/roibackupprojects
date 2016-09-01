package com.getusroi.wms20.printservice.cups.helper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.cups4j.CupsClient;

import com.getusroi.config.persistence.dao.DataBaseUtil;
import com.getusroi.wms20.printservice.cups.PrintServiceCupsConstant;
import java.sql.PreparedStatement;
/**
 * This class is used for cups related client and db configuration
 * @author bizruntime
 *
 */
public class CupsPrinterConfiguration {
	
	private static String URL = null;
	private static String DRIVER_CLASS = null;
	private static String USER = null;
	private static String PASSWORD = null;

	
	/**
	 * 
	 * @return JDBCconnection
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static Connection getDbConnection() throws ClassNotFoundException, SQLException, IOException{
		if (URL == null) {
			loadConfigrationDbPropertyFile();
		}
		Class.forName(DRIVER_CLASS);
		Connection connection = (Connection) DriverManager.getConnection(URL, USER, PASSWORD);
		return connection;
	}
	
	/**
	 * This method is used to get cups client 
	 * @return CupsClient Object
	 * @throws Exception
	 */
	public static CupsClient configureClient() throws Exception {
		return new CupsClient();
	}

	/**
	 * This method is used to close the db connection
	 * @param con : SQL Connection Object
	 * @param ptst : PreparedStatement Object
	 * @param rs : ResultSet Object
	 */
	public static void dbCleanup(Connection con, PreparedStatement ptst, ResultSet rs) {
		close(con);
		close(ptst);
		close(rs);
	}
	
	/**
	 * This method is used to close db connection
	 * @param conn : SQL connection Object
	 * @param ps : SQL prepared statment Object
	 */
	public static void dbCleanUp(Connection conn, PreparedStatement ps) {
		close(conn);
		close(ps);
	}

	/**
	 * This method is used to close the db connection
	 * @param connection : SQL Connection Object
	 */
	public static void close(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException sqlexp) {
				sqlexp.printStackTrace();
			}
		}
	}

	/**
	 * This method is used to close Prepared statement
	 * @param pStatement : SQL prepared statement
	 */
	public static void close(PreparedStatement pStatement) {
		if (pStatement != null) {
			try {
				pStatement.close();
			} catch (SQLException sqlexp) {
				sqlexp.printStackTrace();
			}
		}
	}

	/**
	 * This method is used to close the ResultSet 
	 * @param resultSet : Resultset Object
	 */
	public static void close(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException sqlexp) {
				sqlexp.printStackTrace();
			}
		}
	}


	/**
	 * This method is used to load the db configuation
	 * @throws IOException
	 */
	private synchronized static void loadConfigrationDbPropertyFile() throws IOException {
		Properties properties = new Properties();
		properties.load(DataBaseUtil.class.getClassLoader().getResourceAsStream(PrintServiceCupsConstant.CUPS_PROPERTY_FILE));
		URL = properties.getProperty(PrintServiceCupsConstant.URL);
		DRIVER_CLASS = properties.getProperty(PrintServiceCupsConstant.DRIVER_CLASS);
		USER = properties.getProperty(PrintServiceCupsConstant.USER);
		PASSWORD = properties.getProperty(PrintServiceCupsConstant.PASSWORD);

	}

}
