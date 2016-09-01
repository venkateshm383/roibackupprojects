package com.key2act.timetrackerproxy.service;

import static com.key2act.timetrackerproxy.util.TimeTrackerProxyConstants.*;

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
import com.getusroi.mesh.MeshHeaderConstant;
import com.key2act.timetrackerproxy.util.DBConfigurerUtil;
import com.key2act.timetrackerproxy.util.WebServiceDataConfigurerUtil;

public class RequestProcessorBean extends AbstractCassandraBean {
	static Logger logger = LoggerFactory.getLogger(RequestProcessorBean.class);

	/**
	 * Method to load the tenantName from the request and pass name of the
	 * servicetype provided to it for loading the permaData for that tenantID,
	 * For which we are invoking loadPermaDataForTenantAndSetWSEndpointInHeader
	 * method
	 * 
	 * @throws TimeTrackLookUpServiceException
	 */
	public void getTenantNameAndLoadPermaDataForWS(String serviceType, Exchange exchange)
			throws TimeTrackLookUpServiceException {
		String requestString = exchange.getIn().getBody(String.class);
		try {
			JSONObject requestJsonObject = new JSONObject(requestString);
			logger.debug("requestObj : " + requestJsonObject);
			JSONArray requestJsonArray = requestJsonObject.getJSONArray(MeshHeaderConstant.DATA_KEY);
			requestJsonObject = (JSONObject) requestJsonArray.get(0);
			logger.debug("requestObj : " + requestJsonObject);
			String tenantID = requestJsonObject.getString(TENANT_ID_REQUEST_KEY);
			logger.debug("tenantID : " + tenantID);
			WebServiceDataConfigurerUtil webServiceDataConfigurerUtil = new WebServiceDataConfigurerUtil();
			webServiceDataConfigurerUtil.loadPermaDataForTenantAndSetWSEndpointInHeader(exchange, tenantID,
					serviceType);
		} catch (JSONException e1) {
			try {
				throw new IllegalRequestException(
						"The request is not Properly Well Formed the tenantID key must be present inside JSONArray of DATA key");
			} catch (IllegalRequestException e) {
				throw new TimeTrackLookUpServiceException("Illegal request Provided");
			}
		}
	}

	/**
	 * method to Get the <b>RestClass</b> data according to the tenantID
	 * provided in the requestObj
	 * 
	 * @throws TimeTrackLookUpServiceException
	 */
	public void getTenantNameAndLoadRestClassConfigurationsForCDC(String servicetype, Exchange exchange)
			throws TimeTrackLookUpServiceException {
		String tenantID = (String) exchange.getIn().getHeader(TENANT_ID_COLUMN_KEY);
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new TimeTrackLookUpServiceException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, RATECLASS_TABLENAME);
		Column tenant = table.getColumnByName(TENANT_ID_COLUMN_KEY);
		Column rateClass = table.getColumnByName(RATECLASS_COLUMN_KEY);
		Column rateDesc = table.getColumnByName(RATEDESCRIPTION_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table).select(rateClass, rateDesc).where(tenant).eq(tenantID)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray rateClassDetailJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject rateClassJsonObject = new JSONObject();
				Row row = dataset.getRow();
				rateClassJsonObject.put(RATECLASS_COLUMN_KEY, row.getValue(rateClass));
				rateClassJsonObject.put(RATEDESCRIPTION_COLUMN_KEY, row.getValue(rateDesc));
				rateClassDetailJsonArray.put(rateClassJsonObject);
			}
			responseObject.put(RATECLASS_TABLENAME, rateClassDetailJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException e) {
			throw new TimeTrackLookUpServiceException();
		}
	}

	/**
	 * method to Get the <b>UnionClass</b> data according to the tenantID
	 * provided in the requestObj
	 * 
	 * @throws TimeTrackLookUpServiceException
	 */
	public void getTenantNameAndLoadUnionConfigurationsForCDC(String servicetype, Exchange exchange)
			throws TimeTrackLookUpServiceException {
		String tenantID = (String) exchange.getIn().getHeader(TENANT_ID_COLUMN_KEY);
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new TimeTrackLookUpServiceException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, UNION_TABLENAME);
		Column tenant = table.getColumnByName(TENANT_ID_COLUMN_KEY);
		Column unionCode = table.getColumnByName(UNIONCODE_COLUMN_KEY);
		Column unionDesc = table.getColumnByName(UNION_DESCRIPTION_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table).select(unionCode, unionDesc).where(tenant).eq(tenantID)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray rateClassDetailJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject rateClassJsonObject = new JSONObject();
				Row row = dataset.getRow();
				rateClassJsonObject.put(UNIONCODE_COLUMN_KEY, row.getValue(unionCode));
				rateClassJsonObject.put(UNION_DESCRIPTION_COLUMN_KEY, row.getValue(unionDesc));
				rateClassDetailJsonArray.put(rateClassJsonObject);
			}
			responseObject.put(UNION_TABLENAME, rateClassDetailJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException e) {
			throw new TimeTrackLookUpServiceException();
		}
	}

	/**
	 * method to Get the <b>FedClass</b> data according to the tenantID provided
	 * in the requestObj
	 * 
	 * @throws TimeTrackLookUpServiceException
	 */
	public void getTenantNameAndLoadFedClassConfigurationsForCDC(String servicetype, Exchange exchange)
			throws TimeTrackLookUpServiceException {
		String tenantID = (String) exchange.getIn().getHeader(TENANT_ID_COLUMN_KEY);
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new TimeTrackLookUpServiceException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, FEDCLASS_TABLENAME);
		Column tenant = table.getColumnByName(TENANT_ID_COLUMN_KEY);
		Column fedClassCode = table.getColumnByName(FEDCLASS_CODE_COLUMN_KEY);
		Column fedClassDesc = table.getColumnByName(FEDCLASS_DESC_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table).select(fedClassCode, fedClassDesc).where(tenant).eq(tenantID)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray rateClassDetailJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject rateClassJsonObject = new JSONObject();
				Row row = dataset.getRow();
				rateClassJsonObject.put(UNIONCODE_COLUMN_KEY, row.getValue(fedClassCode));
				rateClassJsonObject.put(UNION_DESCRIPTION_COLUMN_KEY, row.getValue(fedClassDesc));
				rateClassDetailJsonArray.put(rateClassJsonObject);
			}
			responseObject.put(UNION_TABLENAME, rateClassDetailJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException e) {
			throw new TimeTrackLookUpServiceException();
		}
	}

	
	/**
	 * method to Get the <b>Department</b> data according to the tenantID provided
	 * in the requestObj
	 * 
	 * @throws TimeTrackLookUpServiceException
	 */
	public void getTenantNameAndLoadDepartmentConfigurationsForCDC(String servicetype, Exchange exchange)
			throws TimeTrackLookUpServiceException {
		String tenantID = (String) exchange.getIn().getHeader(TENANT_ID_COLUMN_KEY);
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new TimeTrackLookUpServiceException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, DEPARTMENT_TABLENAME);
		Column tenant = table.getColumnByName(TENANT_ID_COLUMN_KEY);
		Column deptID = table.getColumnByName(DEPARTMENT_ID_COLUMN_KEY);
		Column deptDesc = table.getColumnByName(DEPARTMENT_DESC_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table).select(deptID, deptDesc).where(tenant).eq(tenantID)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray rateClassDetailJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject rateClassJsonObject = new JSONObject();
				Row row = dataset.getRow();
				rateClassJsonObject.put(DEPARTMENT_ID_COLUMN_KEY, row.getValue(deptID));
				rateClassJsonObject.put(DEPARTMENT_DESC_COLUMN_KEY, row.getValue(deptDesc));
				rateClassDetailJsonArray.put(rateClassJsonObject);
			}
			responseObject.put(DEPARTMENT_TABLENAME, rateClassDetailJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException e) {
			throw new TimeTrackLookUpServiceException();
		}
	}
	
	/**
	 * method to Get the <b>Position</b> data according to the tenantID provided
	 * in the requestObj
	 * 
	 * @throws TimeTrackLookUpServiceException
	 */
	public void getTenantNameAndLoadPositionConfigurationsForCDC(String servicetype, Exchange exchange)
			throws TimeTrackLookUpServiceException {
		String tenantID = (String) exchange.getIn().getHeader(TENANT_ID_COLUMN_KEY);
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new TimeTrackLookUpServiceException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, POSITION_TABLENAME);
		Column tenant = table.getColumnByName(TENANT_ID_COLUMN_KEY);
		Column positionTitle = table.getColumnByName(POSITION_TITLE_COLUMN_KEY);
		Column positionDesc = table.getColumnByName(POSITION_DESC_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table).select(positionTitle, positionDesc).where(tenant).eq(tenantID)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray rateClassDetailJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject rateClassJsonObject = new JSONObject();
				Row row = dataset.getRow();
				rateClassJsonObject.put(POSITION_TITLE_COLUMN_KEY, row.getValue(positionTitle));
				rateClassJsonObject.put(POSITION_DESC_COLUMN_KEY, row.getValue(positionDesc));
				rateClassDetailJsonArray.put(rateClassJsonObject);
			}
			responseObject.put(POSITION_TABLENAME, rateClassDetailJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException e) {
			throw new TimeTrackLookUpServiceException();
		}
	}
	
	/**
	 * method to Get the <b>State</b> data according to the tenantID provided
	 * in the requestObj
	 * 
	 * @throws TimeTrackLookUpServiceException
	 */
	public void getTenantNameAndLoadStateConfigurationsForCDC(String servicetype, Exchange exchange)
			throws TimeTrackLookUpServiceException {
		String tenantID = (String) exchange.getIn().getHeader(TENANT_ID_COLUMN_KEY);
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new TimeTrackLookUpServiceException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, PAYROLL_STATE_TABLENAME);
		Column tenant = table.getColumnByName(TENANT_ID_COLUMN_KEY);
		Column payrollStateCode = table.getColumnByName(PAYROLL_STATE_CODE_COLUMN_KEY);
		Column payrollStateName = table.getColumnByName(PAYROLL_STATE_NAME_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table).select(payrollStateCode, payrollStateName).where(tenant).eq(tenantID)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray rateClassDetailJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject rateClassJsonObject = new JSONObject();
				Row row = dataset.getRow();
				rateClassJsonObject.put(PAYROLL_STATE_CODE_COLUMN_KEY, row.getValue(payrollStateCode));
				rateClassJsonObject.put(PAYROLL_STATE_NAME_COLUMN_KEY, row.getValue(payrollStateName));
				rateClassDetailJsonArray.put(rateClassJsonObject);
			}
			responseObject.put(PAYROLL_STATE_TABLENAME, rateClassDetailJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException e) {
			throw new TimeTrackLookUpServiceException();
		}
	}
	
	/**
	 * method to Get the <b>Suta State</b> data according to the tenantID provided
	 * in the requestObj
	 * 
	 * @throws TimeTrackLookUpServiceException
	 */
	public void getTenantNameAndLoadSutaStateConfigurationsForCDC(String servicetype, Exchange exchange)
			throws TimeTrackLookUpServiceException {
		String tenantID = (String) exchange.getIn().getHeader(TENANT_ID_COLUMN_KEY);
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new TimeTrackLookUpServiceException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, EMPLOYEE_TABLENAME);
		Column tenant = table.getColumnByName(TENANT_ID_COLUMN_KEY);
		Column empSutaState = table.getColumnByName(EMPLOYEE_SUTASTAT_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table).select(empSutaState).where(tenant).eq(tenantID)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray rateClassDetailJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject rateClassJsonObject = new JSONObject();
				Row row = dataset.getRow();
				rateClassJsonObject.put(EMPLOYEE_SUTASTAT_COLUMN_KEY, row.getValue(empSutaState));
				rateClassDetailJsonArray.put(rateClassJsonObject);
			}
			responseObject.put(EMPLOYEE_TABLENAME, rateClassDetailJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException e) {
			throw new TimeTrackLookUpServiceException();
		}
	}
	
	/**
	 * method to Get the <b>WorkersComp</b> data according to the tenantID provided
	 * in the requestObj
	 * 
	 * @throws TimeTrackLookUpServiceException
	 */
	public void getTenantNameAndLoadWorkersCompConfigurationsForCDC(String servicetype, Exchange exchange)
			throws TimeTrackLookUpServiceException {
		String tenantID = (String) exchange.getIn().getHeader(TENANT_ID_COLUMN_KEY);
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new TimeTrackLookUpServiceException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, WORKERS_COMP_TABLENAME);
		Column tenant = table.getColumnByName(TENANT_ID_COLUMN_KEY);
		Column workersCompNum = table.getColumnByName(WORKERS_COMP_NUMBER_COLUMN_KEY);
		Column workersCompDesc = table.getColumnByName(WORKERS_COMP_DESC_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table).select(workersCompNum, workersCompDesc).where(tenant).eq(tenantID)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray rateClassDetailJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject rateClassJsonObject = new JSONObject();
				Row row = dataset.getRow();
				rateClassJsonObject.put(WORKERS_COMP_NUMBER_COLUMN_KEY, row.getValue(workersCompNum));
				rateClassJsonObject.put(WORKERS_COMP_DESC_COLUMN_KEY, row.getValue(workersCompDesc));
				rateClassDetailJsonArray.put(rateClassJsonObject);
			}
			responseObject.put(WORKERS_COMP_TABLENAME, rateClassDetailJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException e) {
			throw new TimeTrackLookUpServiceException();
		}
	}
	
	/**
	 * method to Get the <b>Employee</b> data according to the tenantID provided
	 * in the requestObj
	 * 
	 * @throws TimeTrackLookUpServiceException
	 */
	public void getTenantNameAndLoadEmployeeDataConfigurationsForCDC(String servicetype, Exchange exchange)
			throws TimeTrackLookUpServiceException {
		String tenantID = (String) exchange.getIn().getHeader(TENANT_ID_COLUMN_KEY);
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new TimeTrackLookUpServiceException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, EMPLOYEE_TABLENAME);
		Column tenant = table.getColumnByName(TENANT_ID_COLUMN_KEY);
		Column empID = table.getColumnByName(EMPLOYEE_ID_COLUMN_KEY);
		Column empFname = table.getColumnByName(EMPLOYEE_FIRSTNAME_COLUMN_KEY);
		Column empLname = table.getColumnByName(EMPLOYEE_LASTNAME_COLUMN_KEY);
		DataSet dataset = dataContext.query().from(table).select(empID, empFname, empLname).where(tenant).eq(tenantID)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray rateClassDetailJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject rateClassJsonObject = new JSONObject();
				Row row = dataset.getRow();
				rateClassJsonObject.put(EMPLOYEE_ID_COLUMN_KEY, row.getValue(empID));
				rateClassJsonObject.put(EMPLOYEE_FIRSTNAME_COLUMN_KEY, row.getValue(empFname));
				rateClassJsonObject.put(EMPLOYEE_LASTNAME_COLUMN_KEY, row.getValue(empLname));
				rateClassDetailJsonArray.put(rateClassJsonObject);
			}
			responseObject.put(WORKERS_COMP_TABLENAME, rateClassDetailJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException e) {
			throw new TimeTrackLookUpServiceException();
		}
	}
	
	/**
	 * method to Get the <b>WorkersComp</b> data according to the tenantID provided
	 * in the requestObj
	 * 
	 * @throws TimeTrackLookUpServiceException
	 */
	public void getTenantNameAndLoadEquipmentConfigurationsForCDC(String servicetype, Exchange exchange)
			throws TimeTrackLookUpServiceException {
		String tenantID = (String) exchange.getIn().getHeader(TENANT_ID_COLUMN_KEY);
		Cluster cluster;
		try {
			cluster = getCassandraCluster();
		} catch (CassandraClusterException e) {
			throw new TimeTrackLookUpServiceException("Cannot fetch the Cassandra Cluster from the configuration");
		}
		String keySpace = CASSANDRA_TABLE_KEYSPACE;
		DataContext dataContext = getDataContextForCassandraByCluster(cluster, keySpace);
		Table table = getTableForDataContext(dataContext, EQUIPMENT_TABLENAME);
		Column tenant = table.getColumnByName(TENANT_ID_COLUMN_KEY);
		Column equipmentID = table.getColumnByName(EQUIPMENT_ID_COLUMN_KEY);
		Column equipmentWensoftMasterID = table.getColumnByName(EQUIPMENT_WENNSOFT_MASTER_ID_COLUMN_KEY);
		Column equipmentType = table.getColumnByName(EQUIPMENT_TYPE);
		Column equipmentDesc = table.getColumnByName(EQUIPMENT_DESCRIPTION);
		DataSet dataset = dataContext.query().from(table).select(equipmentID, equipmentWensoftMasterID, equipmentType, equipmentDesc).where(tenant).eq(tenantID)
				.execute();
		JSONObject responseObject = new JSONObject();
		JSONArray rateClassDetailJsonArray = new JSONArray();
		try {
			while (dataset.next()) {
				JSONObject rateClassJsonObject = new JSONObject();
				Row row = dataset.getRow();
				rateClassJsonObject.put(EQUIPMENT_ID_COLUMN_KEY, row.getValue(equipmentID));
				rateClassJsonObject.put(EQUIPMENT_WENNSOFT_MASTER_ID_COLUMN_KEY, row.getValue(equipmentWensoftMasterID));
				rateClassJsonObject.put(EQUIPMENT_TYPE, row.getValue(equipmentType));
				rateClassJsonObject.put(EQUIPMENT_DESCRIPTION, row.getValue(equipmentDesc));
				rateClassDetailJsonArray.put(rateClassJsonObject);
			}
			responseObject.put(EQUIPMENT_TABLENAME, rateClassDetailJsonArray);
			exchange.getOut().setBody(responseObject.toString());
		} catch (JSONException e) {
			throw new TimeTrackLookUpServiceException();
		}
	}
	
	
	/**
	 * method of processBean to load the tenantID from the request put it in the
	 * header
	 * 
	 * @throws TimeTrackLookUpServiceException
	 */
	@Override
	protected void processBean(Exchange exchange) throws TimeTrackLookUpServiceException {
		String requestString = exchange.getIn().getBody(String.class);
		JSONObject requestJsonObject;
		try {
			requestJsonObject = new JSONObject(requestString);
			requestJsonObject = (JSONObject) requestJsonObject.getJSONArray(MeshHeaderConstant.DATA_KEY)
					.getJSONObject(0);
			String tenantID = requestJsonObject.getString(TENANT_ID_REQUEST_KEY);
			exchange.getIn().setHeader(TENANT_ID_COLUMN_KEY, tenantID);
		} catch (JSONException e) {
			try {
				throw new IllegalRequestException(
						"The JSON Data passed into the RestClassList Data Service must contain tenantID");
			} catch (IllegalRequestException e1) {
				throw new TimeTrackLookUpServiceException("Request is invalid");
			}
		}
	}
}