package com.key2act.timetracker.service.helper;

import static com.key2act.timetracker.util.TimeTrackerConstants.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.camel.Exchange;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.Table;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.eventframework.abstractbean.util.CassandraClusterException;
import com.key2act.timetracker.timesheet.TimeSheetTrackerProcessingException;
import com.key2act.timetracker.util.TimeSheetEntryConstants;

public class TimeSheetDetailsGetHelper extends AbstractCassandraBean {

	Logger logger = LoggerFactory.getLogger(TimeSheetDetailsGetHelper.class);

	
	/**
	 * To get TimeSheet Details 
	 * @param weekEndDate
	 * @param empId
	 * @param tenantId
	 * @return
	 * @throws TimeSheetTrackerProcessingException
	 * @throws JSONException 
	 */
	public JSONObject getTimeSheetDetails(JSONObject jsonObject
			) throws TimeSheetTrackerProcessingException, JSONException {
		JSONObject timeSheetJson=new JSONObject();


		String tenantId=checkJsonKeyExistOrNot(jsonObject, TIME_SHEET_CLMN_TENANTID);
				String weekEndDate=checkJsonKeyExistOrNot(jsonObject,TIME_SHEET_ENTRY_JSON_WEEK_END_DATE_KEY );
				String empId=checkJsonKeyExistOrNot(jsonObject,TIME_SHEET_CLMN_EMP_ID );
		
				if(tenantId==null||empId==null||weekEndDate==null)
					throw new TimeSheetTrackerProcessingException("Error invalid request");
		try {
					
		Cluster cluster = getCassandraCluster();

		DataContext dataContext = getDataContextForCassandraByCluster(cluster,CASSANDRA_TABLE_KEYSPACE);
		Table table = getTableForDataContext(dataContext,TIME_SHEET_ENTRY_TABLE);
		Date weekEdDate;
	
		weekEdDate = formatDate(weekEndDate);
		Date getWeekStartDate=getWeekStartDate(weekEdDate);
		timeSheetJson.put(TIME_SHEET_CLMN_TENANTID, tenantId);
		timeSheetJson.put(TIME_SHEET_CLMN_EMP_ID, empId);

		
		logger.debug(" Tenant ID : "+ tenantId  +" emapid : "+ empId  +" staartDate :  "+ getWeekStartDate  +"   weekEndDate : "+weekEdDate);
		DataSet dataset = dataContext
				.query()
				.from(table)
				.selectAll()
				.where(TIME_SHEET_CLMN_TENANTID).eq(tenantId).and(TIME_SHEET_CLMN_EMP_ID)
				.eq(empId).and(TIME_SHEET_CLMN_TIME_ENTRY_DTM).greaterThanOrEquals(getWeekStartDate).and(TIME_SHEET_CLMN_TIME_ENTRY_DTM).lessThanOrEquals(weekEdDate).execute();

		getTimeSheetDetailsFromDataSet(dataset,timeSheetJson,tenantId,empId);
		logger.debug(" json Object Data "+timeSheetJson);
		} catch (ParseException e) {
		throw new TimeSheetTrackerProcessingException("Error in getting Time Sheet Details : for Emp Id : "+ empId +"  Tenant Id : "+ tenantId , e);
		} catch (IndexOutOfBoundsException e) {
			throw new TimeSheetTrackerProcessingException("Error in getting Time Sheet Details : for Emp Id : "+ empId +"  Tenant Id : "+ tenantId , e);
		} catch (JSONException e) {
			throw new TimeSheetTrackerProcessingException("Error in getting Time Sheet Details : for Emp Id : "+ empId +"  Tenant Id : "+ tenantId , e);
		} catch (CassandraClusterException e) {
			throw new TimeSheetTrackerProcessingException("Error in getting Time Sheet Details : for Emp Id : "+ empId +"  Tenant Id : "+ tenantId , e);
		}
		
		return timeSheetJson;
	}
	
	
	/**
	 * To map time Sheet Entry Details to json from DB 
	 * @param dataset
	 * @param timeSheetJson
	 * @param tenantId
	 * @param empId
	 * @throws IndexOutOfBoundsException
	 * @throws JSONException
	 * @throws ParseException
	 * @throws CassandraClusterException
	 */
	private void getTimeSheetDetailsFromDataSet(DataSet dataset,JSONObject timeSheetJson,String tenantId,String empId ) throws IndexOutOfBoundsException, JSONException, ParseException, CassandraClusterException {

		Iterator<Row> listrow = dataset.iterator();
		JSONArray transcationsJsonArray=new JSONArray();
		
		JSONObject transcation=null;
		while (listrow.hasNext()) {
			Row row = listrow.next();
			transcation=new JSONObject();
			transcation.put(TIME_SHEET_CLMN_TIME_ENTRY_DTM, formatDateToString((Date)row.getValue(2)));
			transcation.put(TIME_SHEET_CLMN_ITEM_NO, (Integer)row.getValue(3));
			transcation.put(TIME_SHEET_CLMN_GL_ACCNT_CODE,row.getValue(4) != null ? row.getValue(4).toString() : null );
			transcation.put(TIME_SHEET_CLMN_JOB_COST_CODE,row.getValue(5) != null ? row.getValue(5).toString() : null );
			transcation.put(TIME_SHEET_CLMN_JOB_NO,row.getValue(6) != null ? row.getValue(6).toString() : null );
			transcation.put(TIME_SHEET_CLMN_SERVICE_CALL_NO,row.getValue(7) != null ? row.getValue(7).toString() : null );
			transcation.put(TIME_SHEET_CLMN_SERVICE_CALL_COST_CODE,row.getValue(8) != null ? row.getValue(8).toString() : null );
			transcation.put(TIME_SHEET_CLMN_STATUS,row.getValue(9) != null ? (Boolean)row.getValue(9) : null );
			transcation.put(TIME_SHEET_CLMN_TIME_ENTRY_TYPE,row.getValue(10) != null ? row.getValue(10).toString() : null );
			
			getTimeSheetNotes(tenantId, empId, (Date)row.getValue(2), (Integer)row.getValue(3), transcation);
			getTimeSheetEquipments(tenantId, empId, (Date)row.getValue(2),  (Integer)row.getValue(3), transcation);
			getTimeSheetCustomerDetails(tenantId, empId, (Date)row.getValue(2), (Integer)row.getValue(3), transcation);
			getTimeSheetPayrollDetails(tenantId, empId, (Date)row.getValue(2), (Integer)row.getValue(3), transcation);
			transcationsJsonArray.put(transcation);
			
		}
		timeSheetJson.put(TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_TRANSCTION_KEY, transcationsJsonArray);
	}
	

	/**
	 * To get TimeSheet Note Details from DB and map to json object 
	 * @param tenantId
	 * @param empId
	 * @param entry
	 * @param lineItemNo
	 * @param transctionJSon
	 * @throws CassandraClusterException
	 * @throws IndexOutOfBoundsException
	 * @throws JSONException
	 */
	private void getTimeSheetNotes(String tenantId,String empId,Date entry ,int lineItemNo,JSONObject transctionJSon) throws CassandraClusterException, IndexOutOfBoundsException, JSONException{
		
		Cluster cluster = getCassandraCluster();
		DataContext dataContext = getDataContextForCassandraByCluster(cluster,CASSANDRA_TABLE_KEYSPACE);
		Table table = getTableForDataContext(dataContext,TIME_SHEET_NOTES_ENTRY_TABLE);
		
		DataSet dataset = dataContext
				.query()
				.from(table)
				.selectAll()
				.where(TIME_SHEET_CLMN_TENANTID).eq(tenantId).and(TIME_SHEET_CLMN_EMP_ID)
				.eq(empId).and(TIME_SHEET_CLMN_TIME_ENTRY_DTM).eq(entry).and(TIME_SHEET_CLMN_ITEM_NO).eq(lineItemNo).execute();
	
			JSONArray equipmentJsonArray=new JSONArray();
			 JSONObject notesJsonObject=null;
			Row row=null;
			Iterator<Row> notesList = dataset.iterator();

			while (notesList.hasNext()) {
				 row=notesList.next();
				  notesJsonObject=new JSONObject();
				  notesJsonObject.put(TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_NOTE_ID_KEY,(Integer)row.getValue(4));
				  notesJsonObject.put(TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_NOTES_KEY, row.getValue(5)!=null?row.getValue(5).toString() : null );
				 equipmentJsonArray.put(notesJsonObject);
			}
			transctionJSon.put(TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_NOTES_KEY, equipmentJsonArray);
	}
	
	
	/**
	 * To getTimeSheet Equipment details from DB  and map to json 
	 * @param tenantId
	 * @param empId
	 * @param entry
	 * @param lineItemNo
	 * @param transctionJSon
	 * @throws CassandraClusterException
	 * @throws IndexOutOfBoundsException
	 * @throws JSONException
	 */
	
private void getTimeSheetEquipments(String tenantId,String empId,Date entry ,int lineItemNo,JSONObject transctionJSon) throws CassandraClusterException, IndexOutOfBoundsException, JSONException{
		
		Cluster cluster = getCassandraCluster();
		DataContext dataContext = getDataContextForCassandraByCluster(cluster,CASSANDRA_TABLE_KEYSPACE);
		Table table = getTableForDataContext(dataContext,TIME_SHEET_EQUIPMENTS_ENTRY_TABLE);
		
		DataSet dataset = dataContext
				.query()
				.from(table)
				.selectAll()
				.where(TIME_SHEET_CLMN_TENANTID).eq(tenantId).and(TIME_SHEET_CLMN_EMP_ID)
				.eq(empId).and(TIME_SHEET_CLMN_TIME_ENTRY_DTM).eq(entry).and(TIME_SHEET_CLMN_ITEM_NO).eq(lineItemNo).execute();
	
			JSONArray equipmentJsonArray=new JSONArray();
			 JSONObject equipmentJsonObject=null;
			Row row=null;
			Iterator<Row> equipmentJsonList = dataset.iterator();

			while (equipmentJsonList.hasNext()) {
				 row=equipmentJsonList.next();
				  equipmentJsonObject=new JSONObject();
				  equipmentJsonObject.put(TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_EQUIPMENT_ID_KEY,(Integer)row.getValue(4));
				  equipmentJsonObject.put(TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_EQUIPMENT_KEY, row.getValue(5)!=null?row.getValue(5).toString() : null );
				 equipmentJsonArray.put(equipmentJsonObject);
			}
			transctionJSon.put(TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_EQUIPMENT_KEY, equipmentJsonArray);
	
}

/**
 * To get timesheet of customerbilling from db and map to json
 * @param tenantId
 * @param empId
 * @param entry
 * @param lineItemNo
 * @param transctionJSon
 * @throws CassandraClusterException
 * @throws IndexOutOfBoundsException
 * @throws JSONException
 */

private void getTimeSheetCustomerDetails(String tenantId,String empId,Date entry ,int lineItemNo,JSONObject transctionJSon) throws CassandraClusterException, IndexOutOfBoundsException, JSONException{
	
	Cluster cluster = getCassandraCluster();
	DataContext dataContext = getDataContextForCassandraByCluster(cluster,CASSANDRA_TABLE_KEYSPACE);
	Table table = getTableForDataContext(dataContext,TIME_SHEET_CUSTOMER_BILL_ENTRY_TABLE);
	
	DataSet dataset = dataContext
			.query()
			.from(table)
			.selectAll()
			.where(TIME_SHEET_CLMN_TENANTID).eq(tenantId).and(TIME_SHEET_CLMN_EMP_ID)
			.eq(empId).and(TIME_SHEET_CLMN_TIME_ENTRY_DTM).eq(entry).and(TIME_SHEET_CLMN_ITEM_NO).eq(lineItemNo).execute();

		 JSONObject customerBillingJsonObject=null;
		Row row=null;
		Iterator<Row> customerJsonList = dataset.iterator();

		while (customerJsonList.hasNext()) {
			 row=customerJsonList.next();

			 customerBillingJsonObject=new JSONObject();
			 customerBillingJsonObject.put(TIME_SHEET_CUSTOMER_BILL_BILLLED_HRS,(Double)row.getValue(4));
			 customerBillingJsonObject.put(TIME_SHEET_CUSTOMER_BILL_HOLIDAY_HRS, row.getValue(5)!=null?(Double)row.getValue(5) : 0.0 );
			 customerBillingJsonObject.put(TIME_SHEET_CUSTOMER_BILL_OVER_TIME_HRS, row.getValue(6)!=null?(Double)row.getValue(6) : 0.0 );
			 customerBillingJsonObject.put(TIME_SHEET_CUSTOMER_BILL_PREMIUM_HRS, row.getValue(7)!=null?(Double)row.getValue(7) : 0.0 );
			 customerBillingJsonObject.put(TIME_SHEET_CUSTOMER_BILL_REGULAR_HRS, row.getValue(8)!=null?(Double)row.getValue(8) : 0.0 );

		}
		transctionJSon.put(TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_CUSTOMER_BILLING_KEY ,customerBillingJsonObject);
}


/**
 * To get TimeSheet payroll details  from db and map to json 
 *  * @param tenantId
 * @param empId
 * @param entry
 * @param lineItemNo
 * @param transctionJSon
 * @throws CassandraClusterException
 * @throws IndexOutOfBoundsException
 * @throws JSONException
 */
private void getTimeSheetPayrollDetails(String tenantId,String empId,Date entry ,int lineItemNo,JSONObject transctionJSon) throws CassandraClusterException, IndexOutOfBoundsException, JSONException{
	
	Cluster cluster = getCassandraCluster();
	DataContext dataContext = getDataContextForCassandraByCluster(cluster,CASSANDRA_TABLE_KEYSPACE);
	Table table = getTableForDataContext(dataContext,TIME_SHEET_PAYROL_ENTRY_TABLE);
	
	DataSet dataset = dataContext
			.query()
			.from(table)
			.selectAll()
			.where(TIME_SHEET_CLMN_TENANTID).eq(tenantId).and(TIME_SHEET_CLMN_EMP_ID)
			.eq(empId).and(TIME_SHEET_CLMN_TIME_ENTRY_DTM).eq(entry).and(TIME_SHEET_CLMN_ITEM_NO).eq(lineItemNo).execute();

		 JSONObject payrollJsonObject=null;
		Row row=null;
		Iterator<Row> payRollJsonList = dataset.iterator();

		while (payRollJsonList.hasNext()) {
			 row=payRollJsonList.next();
			 
			
			 payrollJsonObject=new JSONObject();
			 payrollJsonObject.put(TIME_SHEET_PAY_ROLL_DEPART,row.getValue(4)!=null?row.getValue(4).toString() :null );
			 payrollJsonObject.put(TIME_SHEET_PAY_ROLL_FED_CLASS_CODE, row.getValue(5)!=null?row.getValue(5).toString() :null  );
			 payrollJsonObject.put(TIME_SHEET_PAY_ROLL_POSITION, row.getValue(6)!=null?row.getValue(6).toString() :null );
			 payrollJsonObject.put(TIME_SHEET_PAY_ROLL_RATE_CLASS,row.getValue(7)!=null?row.getValue(7).toString() :null  );
			 payrollJsonObject.put(TIME_SHEET_PAY_ROLL_STATE, row.getValue(8)!=null?row.getValue(8).toString() :null );
			 payrollJsonObject.put(TIME_SHEET_PAY_ROLL_SUTATE, row.getValue(9)!=null?row.getValue(9).toString() :null );
			 payrollJsonObject.put(TIME_SHEET_PAY_ROLL_UNOIN,row.getValue(10)!=null?row.getValue(10).toString() :null  );
			 payrollJsonObject.put(TIME_SHEET_PAY_ROLL_WORK_COMP, row.getValue(11)!=null?row.getValue(11).toString() :null );

		}
		transctionJSon.put(TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_PAYROL_KEY ,payrollJsonObject);
}
	
	

/**
 * TO weekStart date based on weekend date
 * @param weekEndDate
 * @return
 */

	private Date getWeekStartDate(Date weekEndDate) {

		Calendar calender = Calendar.getInstance();
		calender.setTime(weekEndDate);
		calender.add(Calendar.DAY_OF_MONTH, -6);
		Date weekStartDate = calender.getTime();

		return weekStartDate;
	}
	
	/**
	 * To parse given String Date to Date
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	private Date formatDate(String date) throws ParseException {
		SimpleDateFormat dateformater = new SimpleDateFormat("dd/MM/yyyy");
		Date dte = dateformater.parse(date);
		return dte;

	}
	
	/**
	 * To parse given Date to String 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	private String formatDateToString(Date date) throws ParseException {
		SimpleDateFormat dateformater = new SimpleDateFormat("dd/MM/yyyy");
		String dte = dateformater.format(date);
		return dte;

	}
	
	/**
	 * 
	 * @param jsonObject
	 * @param key
	 * @return
	 * @throws JSONException
	 */
	private String checkJsonKeyExistOrNot(JSONObject jsonObject, String key)
			throws JSONException {
		if (jsonObject.has(key)) {
			return jsonObject.getString(key);
		}
		return null;
	}
	
	@Override
	protected void processBean(Exchange arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}
