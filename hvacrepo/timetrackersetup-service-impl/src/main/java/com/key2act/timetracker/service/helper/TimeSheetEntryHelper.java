package com.key2act.timetracker.service.helper;

import static com.key2act.timetracker.util.TimeTrackerConstants.CASSANDRA_TABLE_KEYSPACE;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CLMN_EMP_ID;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CLMN_GL_ACCNT_CODE;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CLMN_ITEM_NO;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CLMN_JOB_COST_CODE;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CLMN_JOB_NO;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CLMN_SERVICE_CALL_COST_CODE;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CLMN_SERVICE_CALL_NO;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CLMN_STATUS;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CLMN_TENANTID;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CLMN_TIME_ENTRY_DTM;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CLMN_TIME_ENTRY_TYPE;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CLMN_TOTAL_HRS;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CUSTOMER_BILL_BILLLED_HRS;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CUSTOMER_BILL_ENTRY_TABLE;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CUSTOMER_BILL_HOLIDAY_HRS;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CUSTOMER_BILL_OVER_TIME_HRS;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CUSTOMER_BILL_PREMIUM_HRS;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_CUSTOMER_BILL_REGULAR_HRS;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_ENTRY_TABLE;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_EQUIPMENTS_ENTRY_TABLE;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_EQUIPMENT_CLMN_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_EQUIPMENT_ID_CLMN_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_NOTES_CLMN_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_NOTES_ENTRY_TABLE;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_NOTE_ID_CLMN_KEY;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_PAYROL_ENTRY_TABLE;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_PAY_ROLL_DEPART;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_PAY_ROLL_EMP_ID;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_PAY_ROLL_FED_CLASS_CODE;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_PAY_ROLL_ITEM_NO;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_PAY_ROLL_POSITION;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_PAY_ROLL_RATE_CLASS;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_PAY_ROLL_STATE;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_PAY_ROLL_SUTATE;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_PAY_ROLL_TENANTID;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_PAY_ROLL_TIME_ENTRY_DTM;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_PAY_ROLL_UNOIN;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_PAY_ROLL_WORK_COMP;
import static com.key2act.timetracker.util.TimeTrackerConstants.TIME_SHEET_TRANSCTION_CLMN_KEY;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.insert.InsertInto;
import org.apache.metamodel.schema.Table;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.eventframework.abstractbean.util.CassandraClusterException;
import com.getusroi.eventframework.abstractbean.util.CassandraConnectionException;
import com.key2act.timetracker.timesheet.TimeSheetTrackerProcessingException;
import com.key2act.timetracker.util.TimeSheetEntryConstants;
import com.key2act.timetracker.util.TimeTrackerConstants;

public class TimeSheetEntryHelper extends AbstractCassandraBean {

	Logger logger = LoggerFactory.getLogger(TimeSheetEntryHelper.class);

	/**
	 * to add TimeSheet entry details to transaction wise and date wise
	 * 
	 * @param jsonObject
	 * @return
	 * @throws TimeSheetTrackerProcessingException
	 */
	public boolean addTimeTrackerDetails(JSONObject jsonObject)
			throws TimeSheetTrackerProcessingException {
		logger.debug("(.) inside addTimeTrackerDetails method of TimeSheetEntryHelper class ");

		int lineItemNo = 0;
		String entryDateInStringFormat = null;
		String lineIte = null;
		String transctionType = null;
		Date entryDate = null;
		Date weekEndDate=null;
		SimpleDateFormat dateformater = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Connection connection = getCassandraConnection();
			UpdateableDataContext upDataContext = getUpdateableDataContextForCassandra(connection);
			String tenantId = checkJsonKeyExistOrNot(jsonObject,
					TIME_SHEET_CLMN_TENANTID);
			String empId = checkJsonKeyExistOrNot(jsonObject,
					TIME_SHEET_CLMN_EMP_ID);
			JSONArray transctionsArray = checkJsonArrayExistWithKey(
					jsonObject,
					TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_TRANSCTION_KEY);

			if (transctionsArray == null || tenantId == null || empId == null)
				throw new TimeSheetTrackerProcessingException(
						"Error invalid transction details for empId : " + empId);

			for (int i = 0; i < transctionsArray.length(); i++) {
				JSONObject jsoObject = transctionsArray.getJSONObject(i);
				entryDateInStringFormat = checkJsonKeyExistOrNot(jsoObject,
						TIME_SHEET_CLMN_TIME_ENTRY_DTM);
				lineIte = checkJsonKeyExistOrNot(jsoObject,
						TIME_SHEET_CLMN_ITEM_NO);
				transctionType = checkJsonKeyExistOrNot(jsoObject,
						TIME_SHEET_CLMN_TIME_ENTRY_TYPE);

				// To check Already Existed TimeSheet entry
				if (entryDateInStringFormat != null) {
					entryDate = dateformater.parse(entryDateInStringFormat);
					logger.debug("Date Parsed : " + entryDate);
				}
				if (lineIte != null) {
					lineItemNo = Integer.parseInt(lineIte);
				} else {
					lineItemNo = incrementLineItemNumber(tenantId, empId,
							entryDate, TIME_SHEET_ENTRY_TABLE);
				}

				//WeekEnd Date is need to be calculated 
				createHolidayTransctions(weekEndDate, upDataContext, empId, tenantId);
				callTimeSheetInsertMethods(upDataContext, jsoObject,
						lineItemNo, tenantId, empId, entryDate, transctionType);
			}
		} catch (CassandraConnectionException e) {
			throw new TimeSheetTrackerProcessingException(
					" Error in adding Time Tracker Details ", e);

		} catch (Exception e) {

			throw new TimeSheetTrackerProcessingException(
					" Error in adding  track Details  ", e);
		}
		return true;
	}

	/**
	 * To add timesheet details into casandra db
	 * 
	 * @param upDataContext
	 * @param jsonObject
	 * @param lineItemNo
	 * @param tenantId
	 * @param empId
	 * @param entryDate
	 * @param transctionType
	 * @throws TimeSheetTrackerProcessingException
	 * @throws JSONException
	 */
	private void callTimeSheetInsertMethods(
			UpdateableDataContext upDataContext, JSONObject jsonObject,
			int lineItemNo, String tenantId, String empId, Date entryDate,
			String transctionType) throws TimeSheetTrackerProcessingException,
			JSONException {
		
		logger.debug("(.) inside callTimeSheetInsertMethods method of TimeSheetEntryHelper class ");
		insertTimeSheetDetails(upDataContext, jsonObject, lineItemNo, tenantId,
				empId, entryDate);

		JSONArray notesArray = checkJsonArrayExistWithKey(jsonObject,
				TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_NOTES_KEY);
		addTimeSheetNotes(upDataContext, notesArray, lineItemNo, tenantId,
				empId, entryDate, transctionType);

		//Transaction type is "Service"
		if(transctionType.equalsIgnoreCase(TimeSheetEntryConstants.TIME_SHEET_TRANSCTION_SERVICE_TYPE)){
		JSONArray euqipments = checkJsonArrayExistWithKey(jsonObject,
				TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_EQUIPMENT_KEY);
		addTimeSheetEquipments(upDataContext, tenantId, empId, entryDate,
				euqipments, transctionType, lineItemNo);
		}

		JSONArray attachmentJsonArray = checkJsonArrayExistWithKey(jsonObject,
				TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_ATTACHMENTS_KEY);
		addTimeSheetAttachments(upDataContext, attachmentJsonArray, lineItemNo);
		JSONObject payrolDetails = checkJsonObjectExistWithKey(jsonObject,
				TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_PAYROL_KEY);
		addPayrollDetails(upDataContext, tenantId, empId, entryDate,
				lineItemNo, payrolDetails);
		JSONObject customerBilling = checkJsonObjectExistWithKey(
				jsonObject,
				TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_CUSTOMER_BILLING_KEY);
		addCustomerBillingDetails(upDataContext, tenantId, empId, entryDate,
				lineItemNo, customerBilling);
	}

	/**
	 * to add TimeSheet details
	 * 
	 * @param upDataContext
	 * @param jsonObject
	 * @param lineItemNo
	 * @param tenantId
	 * @param empId
	 * @param entryDate
	 * @return
	 * @throws TimeSheetTrackerProcessingException
	 * @throws JSONException
	 */
	private boolean insertTimeSheetDetails(UpdateableDataContext upDataContext,
			JSONObject jsonObject, int lineItemNo, String tenantId,
			String empId, Date entryDate)
			throws TimeSheetTrackerProcessingException, JSONException {
		logger.debug("(.) inside insertTimeSheetDetails method of TimeSheetEntryHelper class ");


		Table table = getTableForDataContext(upDataContext,
				TIME_SHEET_ENTRY_TABLE);
		upDataContext.executeUpdate(new InsertInto(table)
				.value(TIME_SHEET_CLMN_EMP_ID, empId)
				.value(TIME_SHEET_CLMN_TENANTID, tenantId)
				.value(TIME_SHEET_CLMN_ITEM_NO, lineItemNo)
				.value(TIME_SHEET_CLMN_TIME_ENTRY_DTM, entryDate)
				.value(TIME_SHEET_CLMN_GL_ACCNT_CODE,
						checkJsonKeyExistOrNot(jsonObject,
								TIME_SHEET_CLMN_GL_ACCNT_CODE))
				.value(TIME_SHEET_CLMN_JOB_COST_CODE,
						checkJsonKeyExistOrNot(jsonObject,
								TIME_SHEET_CLMN_JOB_COST_CODE))
				.value(TIME_SHEET_CLMN_JOB_NO,
						checkJsonKeyExistOrNot(jsonObject,
								TIME_SHEET_CLMN_JOB_NO))
				.value(TIME_SHEET_CLMN_STATUS,
						checkJsonKeyExistOrNot(jsonObject,
								TIME_SHEET_CLMN_STATUS))
				.value(TIME_SHEET_CLMN_SERVICE_CALL_COST_CODE,
						checkJsonKeyExistOrNot(jsonObject,
								TIME_SHEET_CLMN_SERVICE_CALL_COST_CODE))
				.value(TIME_SHEET_CLMN_SERVICE_CALL_NO,
						checkJsonKeyExistOrNot(jsonObject,
								TIME_SHEET_CLMN_SERVICE_CALL_NO))
				.value(TIME_SHEET_CLMN_TIME_ENTRY_TYPE,
						checkJsonKeyExistOrNot(jsonObject,
								TIME_SHEET_CLMN_TIME_ENTRY_TYPE))
				.value(TIME_SHEET_CLMN_TOTAL_HRS,
						checkDoubleValueExistWithKey(jsonObject,
								TIME_SHEET_CLMN_TOTAL_HRS)));

		return false;
	}

	/**
	 * To increment lineItem Number based on transction perday
	 * 
	 * @param tenantId
	 * @param empId
	 * @param date
	 * @param DbName
	 * @return
	 * @throws TimeSheetTrackerProcessingException
	 */
	private int incrementLineItemNumber(String tenantId, String empId,
			Date date, String DbName)
			throws TimeSheetTrackerProcessingException {
		logger.debug("(.) inside incrementLineItemNumber method of TimeSheetEntryHelper class ");

		Cluster cluster;
		int lineItemNumber = 0;
		try {
			cluster = getCassandraCluster();

			logger.debug("Date inside incrmentLineItemNumber : " + date);

			DataContext dataContext = getDataContextForCassandraByCluster(
					cluster, CASSANDRA_TABLE_KEYSPACE);

			Table table = getTableForDataContext(dataContext, DbName);
			DataSet dataset = dataContext.query().from(table)
					.select(TIME_SHEET_CLMN_ITEM_NO)
					.where(TIME_SHEET_CLMN_TENANTID).eq(tenantId)
					.and(TIME_SHEET_CLMN_EMP_ID).eq(empId)
					.and(TIME_SHEET_CLMN_TIME_ENTRY_DTM)
					.eq(date).execute();
			while (dataset.next()) {
				lineItemNumber = (Integer) dataset.getRow().getValue(0);
			}
			lineItemNumber = lineItemNumber + 1;
		} catch (CassandraClusterException e) {
			throw new TimeSheetTrackerProcessingException(
					"Error in incrementing LineItem Number ", e);
		}
		return lineItemNumber;
	}

	/**
	 * to increment noteid or equipmentid based on number of number of notes or
	 * equipments in transction type
	 * 
	 * @param tenantId
	 * @param empId
	 * @param date
	 * @param lineItemNo
	 * @param selectKey
	 * @param DbName
	 * @return
	 * @throws TimeSheetTrackerProcessingException
	 */
	private int incrementNotesNumber(String tenantId, String empId, Date date,
			int lineItemNo, String selectKey, String DbName)
			throws TimeSheetTrackerProcessingException {
		logger.debug("(.) inside incrementNotesNumber method of TimeSheetEntryHelper class ");

		Cluster cluster;
		int lineItemNumber = 0;
		try {
			cluster = getCassandraCluster();
			DataContext dataContext = getDataContextForCassandraByCluster(
					cluster, CASSANDRA_TABLE_KEYSPACE);

			Table table = getTableForDataContext(dataContext, DbName);
			DataSet dataset = dataContext.query().from(table).select(selectKey)
					.where(TIME_SHEET_CLMN_TENANTID).eq(tenantId)
					.and(TIME_SHEET_CLMN_EMP_ID).eq(empId)
					.and(TIME_SHEET_CLMN_ITEM_NO).eq(lineItemNo)
					.and(TIME_SHEET_CLMN_TIME_ENTRY_DTM)
					.greaterThanOrEquals(date).execute();
			while (dataset.next()) {
				lineItemNumber = (Integer) dataset.getRow().getValue(0);
			}
			lineItemNumber = lineItemNumber + 1;
		} catch (CassandraClusterException e) {
			throw new TimeSheetTrackerProcessingException(
					"Error in incrementing LineItem Number ", e);
		}
		return lineItemNumber;
	}

	/**
	 * to add time notes to db
	 * 
	 * @param upDataContext
	 * @param listOfNotes
	 * @throws TimeSheetTrackerProcessingException
	 */
	private void addTimeSheetNotes(UpdateableDataContext upDataContext,
			JSONArray jsonArray, int lineItemNo, String tenantId, String empId,
			Date entryDate, String transctionType)
			throws TimeSheetTrackerProcessingException {
		logger.debug("(.) inside addTimeSheetNotes method of TimeSheetEntryHelper class ");

		int noteId = 0;
		String notes = null;
		if (jsonArray != null) {
			Table table = getTableForDataContext(upDataContext,
					TIME_SHEET_NOTES_ENTRY_TABLE);

			JSONObject notesJson = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				try {
					notesJson = jsonArray.getJSONObject(i);
					noteId = checkIntegerKeyExistOrNot(
							notesJson,
							TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_NOTE_ID_KEY);
					if (noteId == 0) {
						noteId = incrementNotesNumber(tenantId, empId,
								entryDate, lineItemNo,
								TIME_SHEET_NOTE_ID_CLMN_KEY,
								TIME_SHEET_NOTES_ENTRY_TABLE);
					}
					logger.debug(" NOte Id Generated : " + noteId);
					notes = checkJsonKeyExistOrNot(notesJson,
							TIME_SHEET_NOTES_CLMN_KEY);
					upDataContext.executeUpdate(new InsertInto(table)
							.value(TIME_SHEET_CLMN_EMP_ID, empId)
							.value(TIME_SHEET_CLMN_TENANTID, tenantId)
							.value(TIME_SHEET_NOTE_ID_CLMN_KEY, noteId)
							.value(TIME_SHEET_CLMN_TIME_ENTRY_DTM, entryDate)
							.value(TIME_SHEET_NOTES_CLMN_KEY, notes)
							.value(TIME_SHEET_CLMN_ITEM_NO, lineItemNo)

							.value(TIME_SHEET_TRANSCTION_CLMN_KEY,
									transctionType));
				} catch (JSONException e) {
					throw new TimeSheetTrackerProcessingException(
							"Error in adding Time Sheet Notes for tenant Id : "
									+ tenantId, e);
				}
			}
		}

	}

	/**
	 * #TODO Need to write code to insert attachments or file into casandra file
	 * system
	 * 
	 * @param upDataContext
	 * @param listOfAttachments
	 * @param lineItemNo
	 */
	private void addTimeSheetAttachments(UpdateableDataContext upDataContext,
			JSONArray attachments, int lineItemNo) {
		/*
		 * Table table = getTableForDataContext(upDataContext,
		 * TIME_SHEET_ATTACHMENTS_ENTRY_TABLE);
		 */

		/*
		 * for (Iterator<TimeSheetAttachments> iterator = listOfAttachments
		 * .iterator(); iterator.hasNext();) { TimeSheetAttachments
		 * timeSheetAttachments = (TimeSheetAttachments) iterator .next();
		 * 
		 * upDataContext.executeUpdate(new InsertInto(table)
		 * .value(TIME_SHEET_CLMN_EMP_ID, timeSheetAttachments.getEmployeId())
		 * .value(TIME_SHEET_CLMN_TENANTID, timeSheetAttachments.getTenantId())
		 * .value(TIME_SHEET_CLMN_ITEM_NO, timeSheetAttachments.getItemNo())
		 * .value(TIME_SHEET_CLMN_TIME_ENTRY_DTM, DateUtils.get(new Date()))
		 * .value(TIME_SHEET_ATTACHMENT_CLMN_KEY,
		 * timeSheetAttachments.getAttachments())
		 * .value(TIME_SHEET_CLMN_ITEM_NO, lineItemNo)
		 * .value(TIME_SHEET_TRANSCTION_CLMN_KEY,
		 * timeSheetAttachments.getTransctionType()));
		 * 
		 * }
		 */

	}

	/**
	 * To add timeSheetEquipments into Db
	 * @param upDataContext
	 * @param tenantId
	 * @param empId
	 * @param entryDate
	 * @param equipmentsJsonArray
	 * @param transctionType
	 * @param lineItemNo
	 * @throws JSONException
	 * @throws TimeSheetTrackerProcessingException
	 */
	private void addTimeSheetEquipments(UpdateableDataContext upDataContext,
			String tenantId, String empId, Date entryDate,
			JSONArray equipmentsJsonArray, String transctionType, int lineItemNo)
			throws JSONException, TimeSheetTrackerProcessingException {
		logger.debug("(.) inside addTimeSheetEquipments method of TimeSheetEntryHelper class ");

		Table table = getTableForDataContext(upDataContext,
				TIME_SHEET_EQUIPMENTS_ENTRY_TABLE);
		int equipmentId = 0;
		JSONObject equipmentObj = null;
		if (equipmentsJsonArray != null) {
			for (int i = 0; i < equipmentsJsonArray.length(); i++) {

				equipmentObj = equipmentsJsonArray.getJSONObject(i);
				equipmentId = checkIntegerKeyExistOrNot(
						equipmentObj,
						TimeSheetEntryConstants.TIME_SHEET_ENTRY_JSON_EQUIPMENT_ID_KEY);
				if (equipmentId == 0) {
					equipmentId = incrementNotesNumber(tenantId, empId,
							entryDate, lineItemNo,
							TIME_SHEET_EQUIPMENT_ID_CLMN_KEY,
							TIME_SHEET_EQUIPMENTS_ENTRY_TABLE);
				}
				String equipments = checkJsonKeyExistOrNot(equipmentObj,
						TIME_SHEET_EQUIPMENT_CLMN_KEY);
				upDataContext.executeUpdate(new InsertInto(table)
						.value(TIME_SHEET_CLMN_EMP_ID, empId)
						.value(TIME_SHEET_CLMN_TENANTID, tenantId)
						.value(TIME_SHEET_EQUIPMENT_ID_CLMN_KEY, equipmentId)
						.value(TIME_SHEET_CLMN_TIME_ENTRY_DTM, entryDate)
						.value(TIME_SHEET_EQUIPMENT_CLMN_KEY, equipments)
						.value(TIME_SHEET_CLMN_ITEM_NO, lineItemNo)
						.value(TIME_SHEET_TRANSCTION_CLMN_KEY, transctionType));
			}
		}

	}

	/**
	 * To add payroll details to Db
	 * @param upDataContext
	 * @param tenantId
	 * @param empId
	 * @param entryDate
	 * @param lineItemNo
	 * @param payrolData
	 * @throws JSONException
	 */
	private void addPayrollDetails(UpdateableDataContext upDataContext,
			String tenantId, String empId, Date entryDate, int lineItemNo,
			JSONObject payrolData) throws JSONException {
		logger.debug("(.) inside addPayrollDetails method of TimeSheetEntryHelper class ");

		if (payrolData != null) {
			Table table = getTableForDataContext(upDataContext,
					TIME_SHEET_PAYROL_ENTRY_TABLE);
			upDataContext.executeUpdate(new InsertInto(table)
					.value(TIME_SHEET_PAY_ROLL_EMP_ID, empId)
					.value(TIME_SHEET_PAY_ROLL_TENANTID, tenantId)
					.value(TIME_SHEET_PAY_ROLL_ITEM_NO, lineItemNo)
					.value(TIME_SHEET_PAY_ROLL_TIME_ENTRY_DTM, entryDate)
					.value(TIME_SHEET_PAY_ROLL_SUTATE,
							checkJsonKeyExistOrNot(payrolData,
									TIME_SHEET_PAY_ROLL_SUTATE))
					.value(TIME_SHEET_PAY_ROLL_STATE,
							checkJsonKeyExistOrNot(payrolData,
									TIME_SHEET_PAY_ROLL_STATE))
					.value(TIME_SHEET_PAY_ROLL_WORK_COMP,
							checkJsonKeyExistOrNot(payrolData,
									TIME_SHEET_PAY_ROLL_WORK_COMP))
					.value(TIME_SHEET_PAY_ROLL_UNOIN,
							checkJsonKeyExistOrNot(payrolData,
									TIME_SHEET_PAY_ROLL_UNOIN))
					.value(TIME_SHEET_PAY_ROLL_POSITION,
							checkJsonKeyExistOrNot(payrolData,
									TIME_SHEET_PAY_ROLL_POSITION))
					.value(TIME_SHEET_PAY_ROLL_RATE_CLASS,
							checkJsonKeyExistOrNot(payrolData,
									TIME_SHEET_PAY_ROLL_RATE_CLASS))
					.value(TIME_SHEET_PAY_ROLL_DEPART,
							checkJsonKeyExistOrNot(payrolData,
									TIME_SHEET_PAY_ROLL_DEPART))
					.value(TIME_SHEET_PAY_ROLL_FED_CLASS_CODE,
							checkJsonKeyExistOrNot(payrolData,
									TIME_SHEET_PAY_ROLL_FED_CLASS_CODE)));

		}

	}

	/**
	 * to addCustomerbilling details to DB 
	 * @param upDataContext
	 * @param tenantId
	 * @param empId
	 * @param entryDate
	 * @param lineItemNo
	 * @param customerBilling
	 * @throws JSONException
	 */
	private void addCustomerBillingDetails(UpdateableDataContext upDataContext,
			String tenantId, String empId, Date entryDate, int lineItemNo,
			JSONObject customerBilling) throws JSONException {
		
		logger.debug("(.) inside addCustomerBillingDetails method of TimeSheetEntryHelper class ");


		if (customerBilling != null) {

			Table table = getTableForDataContext(upDataContext,
					TIME_SHEET_CUSTOMER_BILL_ENTRY_TABLE);
			upDataContext.executeUpdate(new InsertInto(table)
					.value(TIME_SHEET_PAY_ROLL_EMP_ID, empId)
					.value(TIME_SHEET_PAY_ROLL_TENANTID, tenantId)
					.value(TIME_SHEET_PAY_ROLL_ITEM_NO, lineItemNo)
					.value(TIME_SHEET_PAY_ROLL_TIME_ENTRY_DTM, entryDate)
					.value(TIME_SHEET_CUSTOMER_BILL_BILLLED_HRS,
							checkDoubleValueExistWithKey(customerBilling,
									TIME_SHEET_CUSTOMER_BILL_BILLLED_HRS))
					.value(TIME_SHEET_CUSTOMER_BILL_HOLIDAY_HRS,
							checkDoubleValueExistWithKey(customerBilling,
									TIME_SHEET_CUSTOMER_BILL_HOLIDAY_HRS))
					.value(TIME_SHEET_CUSTOMER_BILL_OVER_TIME_HRS,
							checkDoubleValueExistWithKey(customerBilling,
									TIME_SHEET_CUSTOMER_BILL_OVER_TIME_HRS))
					.value(TIME_SHEET_CUSTOMER_BILL_PREMIUM_HRS,
							checkDoubleValueExistWithKey(customerBilling,
									TIME_SHEET_CUSTOMER_BILL_PREMIUM_HRS))
					.value(TIME_SHEET_CUSTOMER_BILL_REGULAR_HRS,
							checkDoubleValueExistWithKey(customerBilling,
									TIME_SHEET_CUSTOMER_BILL_REGULAR_HRS)));
		}
	}
	/**
	 * To create default transactions for holidays in time sheet entry 
	 * @throws TimeSheetTrackerProcessingException 
	 */
	public boolean createHolidayTransctions(Date weekEndDate,UpdateableDataContext upDataContext,String empId,String tenantId) throws TimeSheetTrackerProcessingException{
		logger.debug("(.) inside createHolidayTransctions method of TimeSheetEntryHelper class ");
		//Check weather we can create  transactions  on holiday or not 
	boolean isholidayTrnsctionCreate=	checkHolidayTransctionCreateTrueOrNot(tenantId);
	boolean isInserted=false;
	Map<Date, Double> holidays=null;
	//if true 
		if(isholidayTrnsctionCreate){
			//get Week Holidays 
			 holidays=getListOfHolidaysOftheWeek(weekEndDate,tenantId);
				Table table = getTableForDataContext(upDataContext,
						TIME_SHEET_ENTRY_TABLE);
		Set<Entry<Date, Double>>	data= holidays.entrySet();
			 for (Iterator<Entry<Date, Double>> iterator = data.iterator(); iterator.hasNext();) {
				Entry<Date, Double> entry = (Entry<Date, Double>) iterator.next();
			upDataContext.executeUpdate(new InsertInto(table)
						.value(TIME_SHEET_CLMN_EMP_ID, empId)
						.value(TIME_SHEET_CLMN_TENANTID, tenantId)
						.value(TIME_SHEET_CLMN_ITEM_NO, 1)
						.value(TIME_SHEET_CLMN_TIME_ENTRY_DTM, entry.getKey())
						.value(TIME_SHEET_CLMN_TIME_ENTRY_TYPE,TimeSheetEntryConstants.TIME_SHEET_TRANSCTION_UNBILLED_TYPE)
						.value(TIME_SHEET_CLMN_TOTAL_HRS,entry.getValue()));
			isInserted=true;
			}
		}
		return isInserted;
		
	}
	
	/**
	 * To check holiday transactions create is true or not 
	 * @return
	 * @throws TimeSheetTrackerProcessingException
	 */
	private boolean checkHolidayTransctionCreateTrueOrNot(String tenantId) throws TimeSheetTrackerProcessingException{
		logger.debug("(.) inside checkHolidayTransctionCreateTrueOrNot Method of TimeSheetEntryHelper ");
		Cluster cluster;
		boolean holidayCreateTrue = false;
		try {
			cluster = getCassandraCluster();
			DataContext dataContext = getDataContextForCassandraByCluster(
					cluster, CASSANDRA_TABLE_KEYSPACE);

			Table table = getTableForDataContext(dataContext, TimeTrackerConstants.GENERAL_SETUP_TABLENAME);
			DataSet dataset = dataContext.query().from(table).select(TimeTrackerConstants.IS_UNBILLED_LABOUR_TRANS_HOLIDAY_COLUMN_KEY).where(TimeTrackerConstants.TENANTID_COLUMN_KEY).eq(tenantId).execute();
			while (dataset.next()) {
				holidayCreateTrue=(Boolean) dataset.getRow().getValue(0);
			}
			logger.debug(" HolidayTransctionCreateTrue : "+ holidayCreateTrue);
		} catch (CassandraClusterException e) {
			throw new TimeSheetTrackerProcessingException(
					"Error in checking TransctionHolidayCreateTrueOrNot ", e);
		}
		return holidayCreateTrue;
	}
	
	/**
	 * To get List of holidays of the week 
	 * @param weekEndDate
	 * @return
	 * @throws TimeSheetTrackerProcessingException 
	 */
	private Map<Date,Double> getListOfHolidaysOftheWeek(Date weekEndDate,String tenantId) throws TimeSheetTrackerProcessingException{
		logger.debug("(.) inside getListOfHolidaysOftheWeek method of TimeSheetEntryHelper class ");
		Date weekStartDate=getWeekStartDate(weekEndDate);
		Cluster cluster;
		HashMap<Date,Double> mapHolidayDate=new HashMap<Date, Double>();
		Date holdayDate=null;
		double holidayHours=0;
		try {
			cluster = getCassandraCluster();
			DataContext dataContext = getDataContextForCassandraByCluster(
					cluster, CASSANDRA_TABLE_KEYSPACE);
			Table table = getTableForDataContext(dataContext, TimeTrackerConstants.HOLIDAY_PAY_PER_HOUR_SETUP_TABLENAME);
			DataSet dataset = dataContext.query().from(table).select(TimeTrackerConstants.HOLIDAY_DATE_COLUMN_KEY,TimeTrackerConstants.HOLIDAY_PAY_PER_HOUR)
					.where(TimeTrackerConstants.HOLIDAY_DATE_COLUMN_KEY).gte(weekStartDate).and(TimeTrackerConstants.HOLIDAY_DATE_COLUMN_KEY).lte(weekEndDate).and(TimeTrackerConstants.TENANTID_COLUMN_KEY).eq(tenantId).execute();
		while (dataset.next()) {
			holdayDate=(Date) dataset.getRow().getValue(0);
			holidayHours=(Double) dataset.getRow().getValue(1);
			mapHolidayDate.put(holdayDate, holidayHours);			
		}
		logger.debug(" HolidayTransctionmapHolidayDate: "+ mapHolidayDate);

		} catch (CassandraClusterException e) {
			throw new TimeSheetTrackerProcessingException(
					"Error in getting Holidays of the week  ", e);
		}
		return mapHolidayDate;
	}

	/**#TODO
	 * To get Default cost code  
	 * @param tenantId
	 * @return
	 */
	private String getDefaultCostCode(String tenantId){
		
		
		return null;
	}
	/**
	 * TO weekStart date based on weekend date
	 * @param weekEndDate
	 * @return
	 */

	private Date getWeekStartDate(Date weekEndDate) {
			logger.debug("(.) inside getWeekStartDate method of TimeSheetEntryHelper class ");

			Calendar calender = Calendar.getInstance();
			calender.setTime(weekEndDate);
			calender.add(Calendar.DAY_OF_MONTH, -6);
			Date weekStartDate = calender.getTime();

			return weekStartDate;
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

	private int checkIntegerKeyExistOrNot(JSONObject jsonObject, String key)
			throws JSONException {
		if (jsonObject.has(key)) {
			return jsonObject.getInt(key);
		}
		return 0;
	}

	private JSONArray checkJsonArrayExistWithKey(JSONObject jsonObject,
			String key) throws JSONException {
		if (jsonObject.has(key)) {
			return jsonObject.getJSONArray(key);
		}
		return null;
	}

	private JSONObject checkJsonObjectExistWithKey(JSONObject jsonObject,
			String key) throws JSONException {
		if (jsonObject.has(key)) {
			return jsonObject.getJSONObject(key);
		}
		return null;
	}

	private double checkDoubleValueExistWithKey(JSONObject jsonObject,
			String key) throws JSONException {
		if (jsonObject.has(key)) {
			return jsonObject.getDouble(key);
		}
		return 0.0;
	}

	@Override
	protected void processBean(Exchange arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}
