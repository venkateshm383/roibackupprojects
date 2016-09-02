package com.key2act.signatureproxy.service;

import static com.key2act.signatureproxy.util.SignatureGPConstants.*;
import org.apache.camel.Exchange;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.Table;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.datastax.driver.core.Cluster;
import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.eventframework.abstractbean.util.CassandraClusterException;
import com.key2act.signatureproxy.InvalidRequestExeption;

public class SignatureGPCreateCallLookUpServiceBean extends AbstractCassandraBean{
	
	/**
	 * method to load site And org from the request JsonObject
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception {
		String request = exchange.getIn().getBody(String.class);
		try {
			JSONObject reqJsonObject = new JSONObject(request);
			String site = reqJsonObject.getString(SITE_KEY);
			String org = reqJsonObject.getString(ORG_KEY);
			exchange.getIn().setHeader(SITE_KEY, site);
			exchange.getIn().setHeader(ORG_KEY, org);
		} catch (JSONException e) {
			throw new InvalidRequestExeption("The Json Data passed is not valid, must contain site and org as keys");
		}
	}
	
	/**
	 * method to get the LookUp Data for Customer Data
	 * load the site and org from the request and fire query to get specific data for createCall Service
	 * @throws SignatureGPLookUpException 
	 * @throws InvalidRequestExeption 
	 * 
	 */
	public void getSiteAndOrgtoLoadCustomerDataForCreateCall(Exchange exchange) throws SignatureGPLookUpException{
		String site = exchange.getIn().getHeader(SITE_KEY).toString();
		String org = exchange.getIn().getHeader(ORG_KEY).toString();
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new SignatureGPLookUpException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, CUSTOMER_TABLE);
		Column orgCol = table.getColumnByName(ORG_KEY);
		Column siteCol = table.getColumnByName(SITE_KEY);
		Column custUserIDCol = table.getColumnByName(CUSTOMER_USERID_COLUMN_KEY);
		Column custNumberCol = table.getColumnByName(CUSTOMER_CUSTNMBR_COLUMN_KEY);
		Column custCnctNmCol = table.getColumnByName(CUSTOMER_CONTACT_NAME_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table).select(custUserIDCol, custNumberCol, custCnctNmCol).where(siteCol).eq(site).and(orgCol).eq(org)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray custDataJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject rateClassJsonObject = new JSONObject();
				Row row = dataset.getRow();
				rateClassJsonObject.put(CUSTOMER_USERID_COLUMN_KEY, row.getValue(custUserIDCol));
				rateClassJsonObject.put(CUSTOMER_CUSTNMBR_COLUMN_KEY, row.getValue(custNumberCol));
				rateClassJsonObject.put(CUSTOMER_CONTACT_NAME_COLUMN_KEY, row.getValue(custCnctNmCol));
				custDataJsonArray.put(rateClassJsonObject);
			}
			responseObject.put(CUSTOMER_TABLE, custDataJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException | NullPointerException e) {
			throw new SignatureGPLookUpException("The JSONObject could not be constructed, either There is no such data Available or the column Name are not found");
		}
	}
	
	/**
	 * method to get the LookUp Data for Location Data
	 * load the site and org from the request and fire query to get specific data for createCall Service
	 * 
	 * @throws SignatureGPLookUpException 
	 * @throws InvalidRequestExeption 
	 */
	public void getSiteAndOrgtoLoadLocationForCreateCall(Exchange exchange) throws SignatureGPLookUpException{
		String site = exchange.getIn().getHeader(SITE_KEY).toString();
		String org = exchange.getIn().getHeader(ORG_KEY).toString();
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new SignatureGPLookUpException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, LOCATION_TABLE);
		Column orgCol = table.getColumnByName(ORG_KEY);
		Column siteCol = table.getColumnByName(SITE_KEY);
		Column locNamCol = table.getColumnByName(LOCATION_LOC_NAME_COLUMN_KEY);
		Column locAddrCol = table.getColumnByName(LOCATION_ADDRESS_COLUMN_KEY);
		Column locAdrsCol = table.getColumnByName(LOCATION_ADRSCODE_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table).select(locNamCol, locAddrCol, locAdrsCol).where(siteCol).eq(site).and(orgCol).eq(org)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray locDataJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject rateClassJsonObject = new JSONObject();
				Row row = dataset.getRow();
				rateClassJsonObject.put(LOCATION_LOC_NAME_COLUMN_KEY, row.getValue(locNamCol));
				rateClassJsonObject.put(LOCATION_ADDRESS_COLUMN_KEY, row.getValue(locAddrCol));
				rateClassJsonObject.put(LOCATION_ADRSCODE_COLUMN_KEY, row.getValue(locAdrsCol));
				locDataJsonArray.put(rateClassJsonObject);
			}
			responseObject.put(LOCATION_TABLE, locDataJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException | NullPointerException e) {
			throw new SignatureGPLookUpException("The JSONObject could not be constructed, either There is no such data Available or the column Name are not found");
		}
	}
	
	/**
	 * method to get the LookUp Data for BillAddress Data
	 * load the site and org from the request and fire query to get specific data for createCall Service
	 * 
	 * @throws SignatureGPLookUpException 
	 * @throws InvalidRequestExeption 
	 */
	public void getSiteAndOrgtoLoadBillAddrForCreateCall(Exchange exchange) throws SignatureGPLookUpException{
		//loading CustNmbr and adrsCode from the exchange Object
		String adrs = null;
		String custnmbr = null;
		try {
			JSONObject jsonObject = new JSONObject(exchange.getIn().getBody(String.class));
			adrs = jsonObject.getString(LOCATION_ADRSCODE_COLUMN_KEY);
			custnmbr = jsonObject.getString(LOCATION_CUSTOMER_NUMBER_COLUMN_KEY);
		} catch (JSONException e1) {
			try {
				throw new InvalidRequestExeption("The Request Object Passed is not a valid JsonObject");
			} catch (InvalidRequestExeption e) {
				throw new SignatureGPLookUpException("The Request Object Passed is not a valid JsonObject, It must contain AdrsCode and CustNmbr for BillAddress");
			}
		}
		
		String site = exchange.getIn().getHeader(SITE_KEY).toString();
		String org = exchange.getIn().getHeader(ORG_KEY).toString();
		
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new SignatureGPLookUpException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, LOCATION_TABLE);
		Column orgCol = table.getColumnByName(ORG_KEY);
		Column siteCol = table.getColumnByName(SITE_KEY);
		Column locCustNumCol = table.getColumnByName(LOCATION_CUSTOMER_NUMBER_COLUMN_KEY);
		Column locAdrsCol = table.getColumnByName(LOCATION_ADRSCODE_COLUMN_KEY);
		Column BillAdressCodeCol = table.getColumnByName(LOCATION_BILL_ADDRESS_CODE_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table).select(BillAdressCodeCol).where(siteCol).eq(site).and(orgCol).eq(org).and(locAdrsCol).eq(adrs).and(locCustNumCol).eq(custnmbr)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray locDataJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject billAddressCodeJsonObject = new JSONObject();
				Row row = dataset.getRow();
				billAddressCodeJsonObject.put(LOCATION_BILL_ADDRESS_CODE_COLUMN_KEY, row.getValue(BillAdressCodeCol));
				locDataJsonArray.put(billAddressCodeJsonObject);
			}
			responseObject.put(LOCATION_TABLE, locDataJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException | NullPointerException e) {
			throw new SignatureGPLookUpException("The JSONObject could not be constructed, either There is no such data Available or the column Name are not found");
		}
	}
	
	/**
	 * method to get the LookUp Data for BillCust Data
	 * load the site and org from the request and fire query to get specific data for createCall Service
	 * 
	 * @throws SignatureGPLookUpException 
	 * @throws InvalidRequestExeption 
	 */
	public void getSiteAndOrgtoLoadBillCustomerForCreateCall(Exchange exchange) throws SignatureGPLookUpException{
		//loading CustNmbr and adrsCode from the exchange Object
		String adrs = null;
		String custnmbr = null;
		try {
			JSONObject jsonObject = new JSONObject(exchange.getIn().getBody(String.class));
			adrs = jsonObject.getString(LOCATION_ADRSCODE_COLUMN_KEY);
			custnmbr = jsonObject.getString(LOCATION_CUSTOMER_NUMBER_COLUMN_KEY);
		} catch (JSONException e1) {
			try {
				throw new InvalidRequestExeption("The Request Object Passed is not a valid JsonObject");
			} catch (InvalidRequestExeption e) {
				throw new SignatureGPLookUpException("The Request Object Passed is not a valid JsonObject, It must contain AdrsCode and CustNmbr for BillCustomer");
			}
		}
		String site = exchange.getIn().getHeader(SITE_KEY).toString();
		String org = exchange.getIn().getHeader(ORG_KEY).toString();
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new SignatureGPLookUpException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, LOCATION_TABLE);
		Column orgCol = table.getColumnByName(ORG_KEY);
		Column siteCol = table.getColumnByName(SITE_KEY);
		Column locCustNumCol = table.getColumnByName(LOCATION_CUSTOMER_NUMBER_COLUMN_KEY);
		Column locAdrsCol = table.getColumnByName(LOCATION_ADRSCODE_COLUMN_KEY);
		Column billCustomerCol = table.getColumnByName(LOCATION_BILL_CUSTOMER_NUMBER_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table).select(billCustomerCol).where(siteCol).eq(site).and(orgCol).eq(org).and(locAdrsCol).eq(adrs).and(locCustNumCol).eq(custnmbr)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray locDataJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject billCustCodeJsonObject = new JSONObject();
				Row row = dataset.getRow();
				billCustCodeJsonObject.put(LOCATION_BILL_CUSTOMER_NUMBER_COLUMN_KEY, row.getValue(billCustomerCol));
				locDataJsonArray.put(billCustCodeJsonObject);
			}
			responseObject.put(LOCATION_TABLE, locDataJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException | NullPointerException e) {
			throw new SignatureGPLookUpException("The JSONObject could not be constructed, either There is no such data Available or the column Name are not found");
		}
	}
	
	/**
	 * method to get the LookUp Data for Contract Number
	 * load the site and org from the request and fire query to get specific data for createCall Service
	 * 
	 * @throws SignatureGPLookUpException 
	 * @throws InvalidRequestExeption 
	 */
	public void getSiteAndOrgtoLoadContractNumberForCreateCall(Exchange exchange) throws SignatureGPLookUpException{
		//loading CustNmbr and adrsCode from the exchange Object
		String adrs = null;
		String custnmbr = null;
		try {
			JSONObject jsonObject = new JSONObject(exchange.getIn().getBody(String.class));
			adrs = jsonObject.getString(EQUIPMENT_ADRSCODE_COLUMN_KEY);
			custnmbr = jsonObject.getString(EQUIPMENT_CUSTNMBR_COLUMN_KEY);
		} catch (JSONException e1) {
			try {
				throw new InvalidRequestExeption("The Request Object Passed is not a valid JsonObject");
			} catch (InvalidRequestExeption e) {
				throw new SignatureGPLookUpException("The Request Object Passed is not a valid JsonObject, It must contain AdrsCode and CustNmbr for BillCustomer");
			}
		}
		String site = exchange.getIn().getHeader(SITE_KEY).toString();
		String org = exchange.getIn().getHeader(ORG_KEY).toString();
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new SignatureGPLookUpException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, EQUIPMENT_TABLENAME);
		Column orgCol = table.getColumnByName(ORG_KEY);
		Column siteCol = table.getColumnByName(SITE_KEY);
		Column eqpmntCustNumCol = table.getColumnByName(EQUIPMENT_CUSTNMBR_COLUMN_KEY);
		Column eqpmntAdrsCol = table.getColumnByName(EQUIPMENT_ADRSCODE_COLUMN_KEY);
		Column eqpmntContractNumber = table.getColumnByName(EQUIPMENT_CONTRACT_NMBR_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table).select(eqpmntContractNumber).where(siteCol).eq(site).and(orgCol).eq(org).and(eqpmntAdrsCol).eq(adrs).and(eqpmntCustNumCol).eq(custnmbr)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray locDataJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject contractNumberJsonObject = new JSONObject();
				Row row = dataset.getRow();
				contractNumberJsonObject.put(EQUIPMENT_CONTRACT_NMBR_COLUMN_KEY, row.getValue(eqpmntContractNumber));
				locDataJsonArray.put(contractNumberJsonObject);
			}
			responseObject.put(EQUIPMENT_TABLENAME, locDataJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException | NullPointerException e) {
			throw new SignatureGPLookUpException("The JSONObject could not be constructed, either There is no such data Available or the column Name are not found");
		}
	}
	
	
	/**
	 * method to get the LookUp Data for Equipment 
	 * load the site and org from the request and fire query to get specific data for createCall Service
	 * 
	 * @throws SignatureGPLookUpException 
	 * @throws InvalidRequestExeption 
	 */
	public void getSiteAndOrgtoLoadEquipmentDataForCreateCall(Exchange exchange) throws SignatureGPLookUpException{
		//loading CustNmbr and adrsCode from the exchange Object
		String adrs = null;
		String custnmbr = null;
		try {
			JSONObject jsonObject = new JSONObject(exchange.getIn().getBody(String.class));
			adrs = jsonObject.getString(EQUIPMENT_ADRSCODE_COLUMN_KEY);
			custnmbr = jsonObject.getString(EQUIPMENT_CUSTNMBR_COLUMN_KEY);
		} catch (JSONException e1) {
			try {
				throw new InvalidRequestExeption("The Request Object Passed is not a valid JsonObject");
			} catch (InvalidRequestExeption e) {
				throw new SignatureGPLookUpException("The Request Object Passed is not a valid JsonObject, It must contain AdrsCode and CustNmbr for BillCustomer");
			}
		}
		String site = exchange.getIn().getHeader(SITE_KEY).toString();
		String org = exchange.getIn().getHeader(ORG_KEY).toString();
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new SignatureGPLookUpException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, EQUIPMENT_TABLENAME);
		Column orgCol = table.getColumnByName(ORG_KEY);
		Column siteCol = table.getColumnByName(SITE_KEY);
		Column eqpmntCustNumCol = table.getColumnByName(EQUIPMENT_CUSTNMBR_COLUMN_KEY);
		Column eqpmntAdrsCol = table.getColumnByName(EQUIPMENT_ADRSCODE_COLUMN_KEY);
		
		Column eqpmntID = table.getColumnByName(EQUIPMENT_ID_COLUMN_KEY);
		Column eqpmntType = table.getColumnByName(EQUIPMENT_TYPE_COLUMN_KEY);
		Column eqpmntDesc = table.getColumnByName(EQUIPMENT_DESC2_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table).select(eqpmntID,eqpmntType,eqpmntDesc).where(siteCol).eq(site).and(orgCol).eq(org).and(eqpmntAdrsCol).eq(adrs).and(eqpmntCustNumCol).eq(custnmbr)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray eqipmntDataJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject equipmentJsonObject = new JSONObject();
				Row row = dataset.getRow();
				equipmentJsonObject.put(EQUIPMENT_ID_COLUMN_KEY, row.getValue(eqpmntID));
				equipmentJsonObject.put(EQUIPMENT_TYPE_COLUMN_KEY, row.getValue(eqpmntType));
				equipmentJsonObject.put(EQUIPMENT_DESC2_COLUMN_KEY, row.getValue(eqpmntDesc));
				eqipmntDataJsonArray.put(equipmentJsonObject);
			}
			responseObject.put(EQUIPMENT_TABLENAME, eqipmntDataJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException | NullPointerException e) {
			throw new SignatureGPLookUpException("The JSONObject could not be constructed, either There is no such data Available or the column Name are not found");
		}
	}
	
	
	/**
	 * method to get the LookUp Data for technician 
	 * load the site and org from the request and fire query to get specific data for createCall Service
	 * 
	 * @throws SignatureGPLookUpException 
	 * @throws InvalidRequestExeption 
	 */
	public void getSiteAndOrgtoLoadTechnicianDataForCreateCall(Exchange exchange) throws SignatureGPLookUpException{
		//loading CustNmbr and adrsCode from the exchange Object
		String site = exchange.getIn().getHeader(SITE_KEY).toString();
		String org = exchange.getIn().getHeader(ORG_KEY).toString();
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new SignatureGPLookUpException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, TECHNICIAN_TABLE);
		Column orgCol = table.getColumnByName(ORG_KEY);
		Column siteCol = table.getColumnByName(SITE_KEY);
		Column techCol = table.getColumnByName(TECHNICIAN_TECH_COLUMN_KEY);
		Column techID = table.getColumnByName(TECHNICIAN_TECHID_COLUMN_KEY);
		Column techName = table.getColumnByName(TECHNICIAN_TECHLONGNAME_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table).select(techCol,techID,techName).where(siteCol).eq(site).and(orgCol).eq(org)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray techJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject equipmentJsonObject = new JSONObject();
				Row row = dataset.getRow();
				equipmentJsonObject.put(TECHNICIAN_TECH_COLUMN_KEY, row.getValue(techCol));
				equipmentJsonObject.put(TECHNICIAN_TECHID_COLUMN_KEY, row.getValue(techID));
				equipmentJsonObject.put(TECHNICIAN_TECHLONGNAME_COLUMN_KEY, row.getValue(techName));
				techJsonArray.put(equipmentJsonObject);
			}
			responseObject.put(TECHNICIAN_TABLE, techJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException | NullPointerException e) {
			throw new SignatureGPLookUpException("The JSONObject could not be constructed, either There is no such data Available or the column Name are not found");
		}
	}
	
	/**
	 * method to get the LookUp Data for technician 
	 * load the site and org from the request and fire query to get specific data for createCall Service
	 * 
	 * @throws SignatureGPLookUpException 
	 * @throws InvalidRequestExeption 
	 */
	public void getSiteAndOrgtoLoadTypeCallForCreateCall(Exchange exchange) throws SignatureGPLookUpException{
		//loading CustNmbr and adrsCode from the exchange Object
		String site = exchange.getIn().getHeader(SITE_KEY).toString();
		String org = exchange.getIn().getHeader(ORG_KEY).toString();
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new SignatureGPLookUpException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, CALLTYPE_TABLENAME);
		Column orgCol = table.getColumnByName(ORG_KEY);
		Column siteCol = table.getColumnByName(SITE_KEY);
		Column typeOfCallCol = table.getColumnByName(CALLTYPE_TYPE_OF_CALL);
		Column typeOfCallShortCol = table.getColumnByName(CALLTYPE_TYPE_OF_CALL_SHORT);
		DataSet dataset = dataContext.query().from(table).select(typeOfCallCol,typeOfCallShortCol).where(siteCol).eq(site).and(orgCol).eq(org)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray techJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject typeOfCallJsonObject = new JSONObject();
				Row row = dataset.getRow();
				typeOfCallJsonObject.put(CALLTYPE_TYPE_OF_CALL, row.getValue(typeOfCallCol));
				typeOfCallJsonObject.put(CALLTYPE_TYPE_OF_CALL_SHORT, row.getValue(typeOfCallShortCol));
				techJsonArray.put(typeOfCallJsonObject);
			}
			responseObject.put(CALLTYPE_TABLENAME, techJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException | NullPointerException e) {
			throw new SignatureGPLookUpException("The JSONObject could not be constructed, either There is no such data Available or the column Name are not found");
		}
	}
	
	/**
	 * method to get the LookUp Data for technician 
	 * load the site and org from the request and fire query to get specific data for createCall Service
	 * 
	 * @throws SignatureGPLookUpException 
	 * @throws InvalidRequestExeption 
	 */
	public void getSiteAndOrgtoLoadDivisionForCreateCall(Exchange exchange) throws SignatureGPLookUpException{
		//loading CustNmbr and adrsCode from the exchange Object
		String site = exchange.getIn().getHeader(SITE_KEY).toString();
		String org = exchange.getIn().getHeader(ORG_KEY).toString();
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new SignatureGPLookUpException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, DIVISION_TABLENAME);
		Column orgCol = table.getColumnByName(ORG_KEY);
		Column siteCol = table.getColumnByName(SITE_KEY);
		Column division = table.getColumnByName(DIVISION_DIVISIONS);
		DataSet dataset = dataContext.query().from(table).select(division).where(siteCol).eq(site).and(orgCol).eq(org)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray divisionJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject typeOfCallJsonObject = new JSONObject();
				Row row = dataset.getRow();
				typeOfCallJsonObject.put(DIVISION_DIVISIONS, row.getValue(division));
				divisionJsonArray.put(typeOfCallJsonObject);
			}
			responseObject.put(DIVISION_TABLENAME, divisionJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException | NullPointerException e) {
			throw new SignatureGPLookUpException("The JSONObject could not be constructed, either There is no such data Available or the column Name are not found");
		}
	}
	
	
}
