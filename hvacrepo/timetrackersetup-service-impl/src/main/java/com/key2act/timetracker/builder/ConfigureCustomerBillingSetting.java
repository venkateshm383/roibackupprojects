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

/**
 * 
 * @author bizruntime
 *
 */
public class ConfigureCustomerBillingSetting extends AbstractCassandraBean
		implements IPermaStoreCustomCacheObjectBuilder {
	Logger logger = LoggerFactory.getLogger(ConfigureCustomerBillingSetting.class);

	/**
	 * Method to load the CustomerBillingSetUp PermaObject into the cache
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
		Table table = getTableForDataContext(dataContext, CUSTOMER_BILLING_SETUP_TABLENAME);
		JSONObject permaObject = getDataSetForCustomerBillingSetUp(table, dataContext);
		
		return (Serializable) permaObject.toString();
	}

	/**
	 * Method which will use the dataset Provided and the name of the columns
	 * available in the table, which would create a new jsonobject based on the
	 * tenant name to act as permaCacheObject
	 * 
	 * @param dataset
	 * @param column
	 * @return json object with customerbilling settings
	 */
	private JSONObject generatePermaObject(DataSet dataset, List<Column> column) {
		org.json.JSONObject customerBillingSetUpObject = new org.json.JSONObject();
		while (dataset.next()) {
			Row row = dataset.getRow();
			try {
				JSONObject customerBillingSettingsOfTenant = new JSONObject();

				customerBillingSettingsOfTenant.put(HOLIDAY_PAY_WITH_PREMIUM_TIME_BILLING,
						row.getValue(column.get(2)));
				customerBillingSettingsOfTenant.put(HOLIDAY_PAY_WITH_OT_BILLING, row.getValue(column.get(1)));
				customerBillingSettingsOfTenant.put(HOLIDAY_PAY_WITH_REGULAT_TIME_BILLING,
						row.getValue(column.get(3)));
				customerBillingSettingsOfTenant.put(IS_RATE_DIFFERENT, row.getValue(column.get(4)));
				customerBillingSettingsOfTenant.put(OT_PAY_WITH_HOLIDAY_BILLING, row.getValue(column.get(5)));
				customerBillingSettingsOfTenant.put(OT_PAY_WITH_PREMIUM_TIME_BILLING,
						row.getValue(column.get(6)));
				customerBillingSettingsOfTenant.put(OT_PAY_WITH_REGULAR_TIME_BILLING,
						row.getValue(column.get(7)));
				customerBillingSettingsOfTenant.put(PREMIUM_PAY_WITH_HOLIDAY_BILING,
						row.getValue(column.get(8)));
				customerBillingSettingsOfTenant.put(PREMIUM_PAY_WITH_OT_BILLING, row.getValue(column.get(9)));
				customerBillingSettingsOfTenant.put(PREMIUM_PAY_WITH_REGULAR_TIME_BILLING,
						row.getValue(column.get(10)));
				customerBillingSettingsOfTenant.put(REGULAR_PAY_WITH_HOLIDAY_BILLING,
						row.getValue(column.get(11)));
				customerBillingSettingsOfTenant.put(REGULAR_PAY_WITH_OT_BILLING, row.getValue(column.get(12)));
				customerBillingSettingsOfTenant.put(REGULAR_PAY_WITH_PREMIUM_TIME_BILLING,
						row.getValue(column.get(13)));
				customerBillingSetUpObject.put(row.getValue(column.get(0)).toString(),
						customerBillingSettingsOfTenant);
			} catch (JSONException e) {
				logger.error("Unable to store PermaStoreCOnfiguration data for Customer Billing Setting");
			}
		}
		return customerBillingSetUpObject;
	}

	/**
	 * Fetching the DataSets by selecting all the Column available in the table list
	 * 
	 * @param table
	 * @param dataContext
	 */
	public JSONObject getDataSetForCustomerBillingSetUp(Table table, DataContext dataContext) {

		List<Column> columnList = getColumnsForDataSet(table);
		DataSet dataset = dataContext.query().from(table)
				.select(columnList.get(0), columnList.get(1), columnList.get(2),columnList.get(3), 
						columnList.get(4), columnList.get(5), columnList.get(6), columnList.get(7), 
						columnList.get(8), columnList.get(9), columnList.get(10), columnList.get(11),
						columnList.get(12), columnList.get(13))
				.execute();
		JSONObject permaObject = generatePermaObject(dataset, columnList);
		

		return permaObject;
	}
	
	/**
	 * Computing the CustomerBilling SETUP Database And passing the columns as List
	 * 
	 * @param table
	 * @return list of columns present in the CustomerBilling SETUP table
	 */
	public List<Column> getColumnsForDataSet(Table table) {
		Column tenantID = table.getColumnByName(TENANTID_COLUMN_KEY);
		Column holidayPayWithOTBilling = table.getColumnByName(HOLIDAY_PAY_WITH_OT_BILLING);
		Column holidayPayWithPremiumTimeBilling = table.getColumnByName(HOLIDAY_PAY_WITH_PREMIUM_TIME_BILLING);
		Column holidayPayWithRegTimeBilling = table.getColumnByName(HOLIDAY_PAY_WITH_REGULAT_TIME_BILLING);
		Column isRateDifferent = table.getColumnByName(IS_RATE_DIFFERENT);
		Column otPayWithHolidayBilling = table.getColumnByName(OT_PAY_WITH_HOLIDAY_BILLING);
		Column otPayWithPremiumTimeBilling = table.getColumnByName(OT_PAY_WITH_PREMIUM_TIME_BILLING);
		Column otPayWithRegTimeBilling = table.getColumnByName(OT_PAY_WITH_REGULAR_TIME_BILLING);
		Column premiumPayWithHolidayBilling = table.getColumnByName(PREMIUM_PAY_WITH_HOLIDAY_BILING);
		Column premiumPayWithOTBilling = table.getColumnByName(PREMIUM_PAY_WITH_OT_BILLING);
		Column premiumPayWithRegTimeBilling = table.getColumnByName(PREMIUM_PAY_WITH_REGULAR_TIME_BILLING);
		Column regPayWithHolidayBilling = table.getColumnByName(REGULAR_PAY_WITH_HOLIDAY_BILLING);
		Column regPayWithOTBilling = table.getColumnByName(REGULAR_PAY_WITH_OT_BILLING);
		Column regPayWithPremiumTimeBilling = table.getColumnByName(REGULAR_PAY_WITH_PREMIUM_TIME_BILLING);
		
		List<Column> columnList = new ArrayList<Column>();
		columnList.add(tenantID);
		columnList.add(holidayPayWithOTBilling);
		columnList.add(holidayPayWithPremiumTimeBilling);
		columnList.add(holidayPayWithRegTimeBilling);
		columnList.add(isRateDifferent);
		columnList.add(otPayWithHolidayBilling);
		columnList.add(otPayWithPremiumTimeBilling);
		columnList.add(otPayWithRegTimeBilling);
		columnList.add(premiumPayWithHolidayBilling);
		columnList.add(premiumPayWithOTBilling);
		columnList.add(premiumPayWithRegTimeBilling);
		columnList.add(regPayWithHolidayBilling);
		columnList.add(regPayWithOTBilling);
		columnList.add(regPayWithPremiumTimeBilling);
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