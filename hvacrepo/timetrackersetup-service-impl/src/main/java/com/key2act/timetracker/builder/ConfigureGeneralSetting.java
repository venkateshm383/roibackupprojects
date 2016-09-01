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
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.eventframework.abstractbean.util.CassandraClusterException;
import com.getusroi.permastore.config.IPermaStoreCustomCacheObjectBuilder;
import com.getusroi.permastore.config.jaxb.CustomBuilder;

public class ConfigureGeneralSetting extends AbstractCassandraBean implements IPermaStoreCustomCacheObjectBuilder {
	 Logger logger = LoggerFactory.getLogger(ConfigureGeneralSetting.class);

	/**
	 * Method to load the GeneralSetUp PermaObject into the cache
	 * 
	 * @param configBuilderConfig
	 * @return
	 */
	@Override
	public Serializable loadDataForCache(CustomBuilder configBuilderConfig) {
		Cluster cluster = null;
		JSONObject permaObject=null;
		try {
			cluster = getCassandraCluster();
			String keySpace = CASSANDRA_TABLE_KEYSPACE;
			DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
			Table table = getTableForDataContext(dataContext, GENERAL_SETUP_TABLENAME);
			permaObject = getDataSetForGeneralSetUp(table, dataContext);			 
		} catch (CassandraClusterException e) {
			logger.error("ClusterException, Connection could not be established");
		}
		return (Serializable) permaObject.toString();

		
	}

	/**
	 * Fetching the DataSets by selecting all the Column available in the table list
	 * 
	 * @param table
	 * @param dataContext
	 */
	public JSONObject getDataSetForGeneralSetUp(Table table, DataContext dataContext) {
		List<Column> columnList = getColumnsForDataSet(table);
		DataSet dataset = dataContext.query().from(table)
				.select(columnList.get(0), columnList.get(1), columnList.get(2), columnList.get(3), columnList.get(4),
						columnList.get(5), columnList.get(6), columnList.get(7), columnList.get(8),
						columnList.get(9))
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
	 * @return json object with general settings
	 */
	private JSONObject generatePermaObject(DataSet dataset, List<Column> column) {
		JSONObject generalSetUpObject = new JSONObject();
		while (dataset.next()) {
			Row row = dataset.getRow();
			try {
				JSONObject generalSettingsOfTenant = new JSONObject();
				generalSettingsOfTenant.put(IS_UNBILLED_LABOUR_TRANS_HOLIDAY_COLUMN_KEY,
						row.getValue(column.get(1)));
				generalSettingsOfTenant.put(OVERTIME_CALCULATION_METHOD_COLUMN_KEY, row.getValue(column.get(2)));
				generalSettingsOfTenant.put(PAYCODE_FOR_HOLIDAY_WORKED_COLUMN_KEY, row.getValue(column.get(3)));
				generalSettingsOfTenant.put(PAYCODE_FOR_OVERTIME_COLUMN_KEY, row.getValue(column.get(4)));
				generalSettingsOfTenant.put(PAYCODE_FOR_PREMIUM_COLUMN_KEY, row.getValue(column.get(5)));
				generalSettingsOfTenant.put(PAYCODE_FOR_REGULAR_COLUMN_KEY, row.getValue(column.get(6)));
				generalSettingsOfTenant.put(PAYCODE_FOR_SALARY_EMPLOYEES_COLUMN_KEY,
						row.getValue(column.get(7)));
				generalSettingsOfTenant.put(STANDARD_NUMBER_OF_BILLABLE_HOURS_PER_DAY_COLUMN_KEY,
						row.getValue(column.get(8)));
				generalSettingsOfTenant.put(STANDARD_NUMBER_OF_BILLABLE_HOURS_PER_WEEK_COLUMN_KEY,
						row.getValue(column.get(9)));
				generalSetUpObject.put(row.getValue(column.get(0)).toString(), generalSettingsOfTenant);
			} catch (JSONException e) {
				logger.error("Unable to store PermaStoreCOnfiguration data for General Settings");
			}
		}
		return generalSetUpObject;
	}
	
	/**
	 * Computing the General SETUP Database And passing the columns as List
	 * 
	 * @param table
	 * @return list of columns present in the General SETUP table
	 */
	public List<Column> getColumnsForDataSet(Table table) {
		Column tenantID = table.getColumnByName(TENANTID_COLUMN_KEY);
		Column isUnbilledLaborTransHoliday = table.getColumnByName(IS_UNBILLED_LABOUR_TRANS_HOLIDAY_COLUMN_KEY);
		Column otCalcMethod = table.getColumnByName(OVERTIME_CALCULATION_METHOD_COLUMN_KEY);
		Column paycodeHoliday = table.getColumnByName(PAYCODE_FOR_HOLIDAY_WORKED_COLUMN_KEY);
		Column paycodeOvertime = table.getColumnByName(PAYCODE_FOR_OVERTIME_COLUMN_KEY);
		Column paycodePremium = table.getColumnByName(PAYCODE_FOR_PREMIUM_COLUMN_KEY);
		Column paycodeRegular = table.getColumnByName(PAYCODE_FOR_REGULAR_COLUMN_KEY);
		Column paycodeEmployeeSalary = table.getColumnByName(PAYCODE_FOR_SALARY_EMPLOYEES_COLUMN_KEY);
		Column stdNumofHoursPerDay = table.getColumnByName(STANDARD_NUMBER_OF_BILLABLE_HOURS_PER_DAY_COLUMN_KEY);
		Column stdNumofHoursPerWeek = table.getColumnByName(STANDARD_NUMBER_OF_BILLABLE_HOURS_PER_WEEK_COLUMN_KEY);
		
		List<Column> columnList = new ArrayList<Column>();
		columnList.add(tenantID);
		columnList.add(isUnbilledLaborTransHoliday);
		columnList.add(otCalcMethod);
		columnList.add(paycodeHoliday);
		columnList.add(paycodeOvertime);
		columnList.add(paycodePremium);
		columnList.add(paycodeRegular);
		columnList.add(paycodeEmployeeSalary);
		columnList.add(stdNumofHoursPerDay);
		columnList.add(stdNumofHoursPerWeek);
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