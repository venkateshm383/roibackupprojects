package com.getusroi.integrationfwk.jdbcIntactivity.config.persistence.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.UpdateScript;
import org.apache.metamodel.UpdateableDataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.delete.DeleteFrom;
import org.apache.metamodel.insert.InsertInto;
import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.query.FilterItem;
import org.apache.metamodel.query.Query;
import org.apache.metamodel.schema.Table;
import org.apache.metamodel.update.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.querybuilder.Delete;
import com.getusroi.integrationfwk.jdbcIntactivity.config.helper.JdbcIntActivityExecutionException;
import com.getusroi.integrationfwk.jdbcIntactivity.config.persistence.IJdbcIntActivityService;
import com.getusroi.integrationfwk.jdbcIntactivity.config.persistence.JdbcIntActivityPersistenceException;

public class JdbcIntActivityConfigDAO implements IJdbcIntActivityService {

	private Logger logger = LoggerFactory.getLogger(JdbcIntActivityConfigDAO.class.getName());
	private static final String WHERE_COLKEY = "constraintOne";
	private static final String ASTERIKS = "*";
	private static final String CONDITION_PATTERN="AND|and|OR|or";

	/**
	 * insert operation DAO method to process the configured query
	 * 
	 * @param datacontext
	 * @param table
	 * @param insertColumnKeySet
	 * @param insertListOfValues
	 * @return //#TODO have to figure out success and failure returns
	 * @throws JdbcIntActivityExecutionException
	 */
	@Override
	public int insertActivityConfigParams(JdbcDataContext datacontext, Table table, Set<String> insertColumnKeySet,
			List<Object> insertListOfValues) throws JdbcIntActivityExecutionException {
		logger.debug(".insert() dataContext: " + datacontext + " table: " + table + " ColumnKey: " + insertColumnKeySet
				+ " ColumnValues: " + insertListOfValues);
		InsertInto valuesInsertObject = new InsertInto(table);
		if (insertColumnKeySet.isEmpty()) {
			int valuesCount = insertListOfValues.size();
			for (int i = 0; i < valuesCount; i++) {
				valuesInsertObject.value(i, insertListOfValues.get(i));
			}
			try {
				datacontext.executeUpdate(valuesInsertObject);
			} catch (Exception e) {
				throw new JdbcIntActivityExecutionException("Unable to insert values into table - " + table.getName(),
						e);
			}
		} // .. end of if, processing for non empty columnNames
		else if (!insertColumnKeySet.isEmpty() && insertColumnKeySet.size() == insertListOfValues.size()) {
			int count = 0;
			for (String columnNames : insertColumnKeySet) {
				valuesInsertObject.value(columnNames, insertListOfValues.get(count));
				count++;
			}
			try {
				datacontext.executeUpdate(valuesInsertObject);
			} catch (Exception e) {
				throw new JdbcIntActivityExecutionException("Unable to insert values into table - " + table.getName(),
						e);
			}
		} // .. end of the else-if, giving support for the queries without
			// column names
		else {
			throw new JdbcIntActivityExecutionException("Unable to Insert into the columns -" + insertColumnKeySet);
		} // .. as the executeUpdate is or returnType Void, manually returning
			// an integer(1), always
		return 1;
	}// ..end of the method
	
	/**
	 * overloaded insert operation DAO method to process the configured query for cassandra
	 * 
	 * @param updateableDatacontext : UpdateableDataContext Object of apache metamodel
	 * @param table : Table Object of apache metamodel
	 * @param insertColumnKeySet 
	 * @param insertListOfValues
	 * @return //#TODO have to figure out success and failure returns
	 * @throws JdbcIntActivityExecutionException
	 */
	
	public int insertActivityConfigParams(UpdateableDataContext updateableDatacontext, Table table, Set<String> insertColumnKeySet,
			List<Object> insertListOfValues) throws JdbcIntActivityExecutionException {
		logger.debug(".insert() dataContext: " + updateableDatacontext + " table: " + table + " ColumnKey: " + insertColumnKeySet
				+ " ColumnValues: " + insertListOfValues);
		InsertInto valuesInsertObject = new InsertInto(table);
		if (insertColumnKeySet.isEmpty()) {
			int valuesCount = insertListOfValues.size();
			for (int i = 0; i < valuesCount; i++) {
				valuesInsertObject.value(i, insertListOfValues.get(i));
			}
			try {
				updateableDatacontext.executeUpdate(valuesInsertObject);
			} catch (Exception e) {
				throw new JdbcIntActivityExecutionException("Unable to insert values into table - " + table.getName(),
						e);
			}
		} // .. end of if, processing for non empty columnNames
		else if (!insertColumnKeySet.isEmpty() && insertColumnKeySet.size() == insertListOfValues.size()) {
			int count = 0;
			for (String columnNames : insertColumnKeySet) {
				valuesInsertObject.value(columnNames, insertListOfValues.get(count));
				count++;
			}
			try {
				updateableDatacontext.executeUpdate(valuesInsertObject);
			} catch (Exception e) {
				throw new JdbcIntActivityExecutionException("Unable to insert values into table - " + table.getName(),
						e);
			}
		} // .. end of the else-if, giving support for the queries without
			// column names
		else {
			throw new JdbcIntActivityExecutionException("Unable to Insert into the columns -" + insertColumnKeySet);
		} // .. as the executeUpdate is or returnType Void, manually returning
			// an integer(1), always
		return 1;
	}// ..end of the method

	/**
	 * update operation DAO method to process the configured query
	 * 
	 * @param datacontext
	 * @param table
	 * @param updateColumnKeySet
	 * @param updateListOfValues
	 * @return //#TODO have to figure out the successful and error returns
	 * @throws JdbcIntActivityExecutionException
	 * @throws JdbcIntActivityPersistenceException
	 */
	@Override
	public int updateActivityConfigParams(JdbcDataContext datacontext, Table table, Set<String> updateColumnKeySet,
			List<Object> updateListOfValues, Map<String, Map<String, Object>> mapOfConstraints)
			throws JdbcIntActivityPersistenceException {
		logger.debug(".inIpdateDml..ColumnKey-ColumnValue: " + updateColumnKeySet + " - " + updateListOfValues);

		Update valueUpdateObject = new Update(table);
		int counter = 0;
		if (!updateColumnKeySet.isEmpty()) {
			for (String updtcolumnNames : updateColumnKeySet) {
				valueUpdateObject.value(updtcolumnNames, updateListOfValues.get(counter));
				counter++;
			}
			try {
				String frstCol = null;
				for (String key : mapOfConstraints.get(WHERE_COLKEY).keySet()) {
					frstCol = key;
				}
				datacontext.executeUpdate(
						valueUpdateObject.where(frstCol).eq(mapOfConstraints.get(WHERE_COLKEY).get(frstCol)));
			} catch (Exception e) {
				throw new JdbcIntActivityPersistenceException(
						"Unable to update the value for the table - " + table.getName(), e);
			}
		} else {
			throw new JdbcIntActivityPersistenceException("Unable to update the columns -" + updateColumnKeySet);
		}
		return 1;
	}// ..end of method
	
	/**
	 * update operation DAO method to process the configured query
	 * 
	 * @param datacontext
	 * @param table
	 * @param updateColumnKeySet
	 * @param updateListOfValues
	 * @return //#TODO have to figure out the successful and error returns
	 * @throws JdbcIntActivityExecutionException
	 * @throws JdbcIntActivityPersistenceException
	 */
	public int updateActivityConfigParamsForCassandra(UpdateableDataContext datacontext, Table table, Set<String> updateColumnKeySet,
			List<Object> updateListOfValues,String whereConstraints)
			throws JdbcIntActivityPersistenceException {
		logger.debug(".inIpdateDml..ColumnKey-ColumnValue: " + updateColumnKeySet + " - " + updateListOfValues);		
		Update valueUpdateObject = new Update(table);
		int counter = 0;
		if (!updateColumnKeySet.isEmpty()) {
			for (String updtcolumnNames : updateColumnKeySet) {
				valueUpdateObject.value(updtcolumnNames, updateListOfValues.get(counter));
				counter++;
			}
			try {
				whereConstraints=createWhereConditionForFilterItem(whereConstraints);
				logger.debug("where constaints : "+whereConstraints);				
				FilterItem fitem = new FilterItem(whereConstraints.trim());
				datacontext.executeUpdate(
						valueUpdateObject.where(fitem));
			} catch (Exception e) {
				throw new JdbcIntActivityPersistenceException(
						"Unable to update the value for the table - " + table.getName(), e);
			}
		} else {
			throw new JdbcIntActivityPersistenceException("Unable to update the columns -" + updateColumnKeySet);
		}
		return 1;
	}// ..end of method

	/**
	 * select operation DAO method to process the configured query
	 * 
	 * @param datacontext
	 * @param selectQuery
	 *            SELECT (amount,dateconfigured) FROM testtable WHERE amount =
	 *            ""
	 * @return //#TODO have to figure out the successful and error response
	 * @throws JdbcIntActivityPersistenceException
	 */
	@Override
	public Row selectActivityConfigParams(DataContext datacontext, Table table1, Table table2,
			List<String> columnSelectKeySet, Map<String, Map<String, Object>> mapOfConstraints, boolean isJoin,
			String joinType) throws JdbcIntActivityPersistenceException {
		String[] colKeyArr = new String[columnSelectKeySet.size()];
		String frstCol = null;
		for (String key : mapOfConstraints.get(WHERE_COLKEY).keySet()) {
			frstCol = key;
		}
		colKeyArr = columnSelectKeySet.toArray(colKeyArr);

		if (!isJoin) {
			return performSelectWhenNotJoin(datacontext, table1, columnSelectKeySet, mapOfConstraints);
		} else {
			Query q;
			switch (joinType) {
			case "INNER JOIN":
				q = datacontext.query().from(table1).innerJoin(table2)
						.on(frstCol, (String) mapOfConstraints.get(WHERE_COLKEY).get(frstCol)).select(colKeyArr)
						.toQuery();

				return performSelectForJoinQueries(datacontext, q);
			case "LEFT OUTER JOIN":
				q = datacontext.query().from(table1).leftJoin(table2)
						.on(frstCol, (String) mapOfConstraints.get(WHERE_COLKEY).get(frstCol)).select(colKeyArr)
						.toQuery();
				return performSelectForJoinQueries(datacontext, q);
			case "RIGHT OUTER JOIN":
				q = datacontext.query().from(table1).rightJoin(table2)
						.on(frstCol, (String) mapOfConstraints.get(WHERE_COLKEY).get(frstCol)).select(colKeyArr)
						.toQuery();
				return performSelectForJoinQueries(datacontext, q);
			default:
				throw new JdbcIntActivityPersistenceException(
						"Unable to perform the operation specifed, which doesn't belongs to an of the join operations..");
			}
		}
	}// ..end of the method

	/**
	 * delete operation DAO method to process the configured query
	 * 
	 * @param datacontext
	 * @param table
	 * @return //#TODO have to figure out successful and error responses to
	 *         return
	 * @throws JdbcIntActivityPersistenceException
	 */
	@Override
	public int deleteActivityConfigParams(JdbcDataContext datacontext, Table table,
			Map<String, Map<String, Object>> mapOfConstraints) throws JdbcIntActivityPersistenceException {
		String frstCol = null;
		try {
			for (String key : mapOfConstraints.get(WHERE_COLKEY).keySet()) {
				frstCol = key;
			}

			Object whereColVal = mapOfConstraints.get(WHERE_COLKEY).get(frstCol);
			if (whereColVal != null) {
				datacontext.executeUpdate(new DeleteFrom(table).where(frstCol).eq(whereColVal));
				return 1;
			} else {
				datacontext.executeUpdate(new DeleteFrom(table));
				return 1;
			}

		} catch (Exception e) {
			throw new JdbcIntActivityPersistenceException(
					"Unable to delete the values from the table - " + table.getName(), e);
		}
	}// ..end of the method
	
	/**
	 * delete operation DAO method to process the configured query
	 * 
	 * @param datacontext
	 * @param table
	 * @return //#TODO have to figure out successful and error responses to
	 *         return
	 * @throws JdbcIntActivityPersistenceException
	 */
	public int deleteActivityConfigParamsForCassandra(UpdateableDataContext datacontext, Table table,
			String whereConstraints) throws JdbcIntActivityPersistenceException {
			if (whereConstraints != null) {	
				whereConstraints=createWhereConditionForFilterItem(whereConstraints);
				logger.debug("where condition after replacement : "+whereConstraints);
				FilterItem fitem = new FilterItem(whereConstraints);
				datacontext.executeUpdate(new DeleteFrom(table).where(fitem));
				return 1;				
				
			} else {
				datacontext.executeUpdate(new DeleteFrom(table));
				return 1;
			}

	}// ..end of the method
	
	private String createWhereConditionForFilterItem(String whereConstraints) throws JdbcIntActivityPersistenceException{
		logger.debug(".createWhereConditionForFilterItem method of JDBCIntActivityConfigDAO");
		String[] arrWhereCondition = whereConstraints.split(CONDITION_PATTERN);
		if(arrWhereCondition != null && arrWhereCondition.length>0){
		for(String whereCondition:arrWhereCondition){
		String[] arrCondition = whereCondition.split("=");		
		if(arrCondition != null && arrCondition.length>0){
			String oldValue=arrCondition[1];
			String newValue=substituteQuotesWithWhereConditionValue(oldValue);
			whereConstraints=whereConstraints.replace(oldValue, newValue);
		}else{
			throw new JdbcIntActivityPersistenceException(
					"where condition for delete operation is not proper : " + whereConstraints);
		}
		}
		}else{
			logger.debug("only one where condition");
			String[] arrCondition = whereConstraints.split("=");	
			if(arrCondition != null && arrCondition.length>0){
				String oldValue=arrCondition[1];
				String newValue=substituteQuotesWithWhereConditionValue(oldValue);
				whereConstraints=whereConstraints.replace(oldValue, newValue);
			}else{
				throw new JdbcIntActivityPersistenceException(
						"where condition for delete operation is not proper : " + whereConstraints);
			}			
		}
		return whereConstraints;
	}
	
	private String substituteQuotesWithWhereConditionValue(String value){
		logger.debug(".substituteQuotesWithWhereConditionValue method of JDBCIntActivityConfigDAO");
		return "'"+value.trim()+"'";
	}

	/**
	 * called after checking the Query , if its not a join based
	 * 
	 * @param datacontext
	 * @param table
	 * @param columnSelectKeySet
	 * @param mapOfConstraints
	 * @return
	 * @throws JdbcIntActivityPersistenceException
	 */
	private Row performSelectWhenNotJoin(DataContext datacontext, Table table,
			List<String> columnSelectKeySet, Map<String, Map<String, Object>> mapOfConstraints)
			throws JdbcIntActivityPersistenceException {

		logger.debug(".selectActivityConfigParams().. - " + columnSelectKeySet + " - " + mapOfConstraints);
		DataSet dataSet = null;
		String[] colKeyArr = new String[columnSelectKeySet.size()];
		colKeyArr = columnSelectKeySet.toArray(colKeyArr);
		if (!ASTERIKS.equals(colKeyArr[1]) || colKeyArr[1] != ASTERIKS) {
			return performSelectColumnsWithConstraints(datacontext, table, colKeyArr, mapOfConstraints, dataSet);
		} // ..end of if, condition check when column names are given

		else if ((ASTERIKS.equals(colKeyArr[1]) && mapOfConstraints.get(WHERE_COLKEY) != null)
				|| (colKeyArr[1] == ASTERIKS && mapOfConstraints.get(WHERE_COLKEY) != null)) {
			return performSelectAllwithConstraints(datacontext, table, colKeyArr, mapOfConstraints, dataSet);
		} // ..end of else-if , condition where checking for '*' with column

		else {
			return performSelectAll(datacontext, table);
		} // ..end of else, condition check for '*' and no column names
	}// ..end of the method

	/**
	 * called when select columns from table with constraint
	 * 
	 * @param datacontext
	 * @param table
	 * @param colKeyArr
	 * @param mapOfConstraints
	 * @param dataSet
	 * @return rowsetString
	 * @throws JdbcIntActivityPersistenceException
	 */
	private Row performSelectColumnsWithConstraints(DataContext datacontext, Table table, String[] colKeyArr,
			Map<String, Map<String, Object>> mapOfConstraints, DataSet dataSet)
			throws JdbcIntActivityPersistenceException {
		logger.debug(".performSelectColumnsWithConstraints()..."+colKeyArr+" - "+mapOfConstraints);
		String frstCol = null;
		for (String key : mapOfConstraints.get(WHERE_COLKEY).keySet()) {
			frstCol = key;
		}
		try {
			DataSet ds = dataSet;
			Query q = datacontext.query().from(table).select(colKeyArr).where(frstCol)
					.eq(mapOfConstraints.get(WHERE_COLKEY).get(frstCol)).toQuery();
			ds = datacontext.executeQuery(q);
			Row row = null;
			try {
				while (ds.next()) {
					row = ds.getRow();
					logger.debug("checking row obj in select with constraint: "+row.getValues()[0]);
				}
			} finally {
				ds.close();
			}
			return row;
		} catch (Exception e) {
			throw new JdbcIntActivityPersistenceException(
					"Unable to retreive DataSet from the table - " + table.getName(), e);
		}
	}// ..end of the method

	/**
	 * called when select * with constraints
	 * 
	 * @param datacontext
	 * @param table
	 * @param colKeyArr
	 * @param mapOfConstraints
	 * @param dataSet
	 * @return rowsetString
	 * @throws JdbcIntActivityPersistenceException
	 */
	private Row performSelectAllwithConstraints(DataContext datacontext, Table table, String[] colKeyArr,
			Map<String, Map<String, Object>> mapOfConstraints, DataSet dataSet)
			throws JdbcIntActivityPersistenceException {
		String frstCol = null;
		for (String key : mapOfConstraints.get(WHERE_COLKEY).keySet()) {
			frstCol = key;
		}
		try {
			DataSet ds = dataSet;
			Query q = datacontext.query().from(table).select(colKeyArr).where(frstCol)
					.eq(mapOfConstraints.get(WHERE_COLKEY).get(frstCol)).toQuery();
			ds = datacontext.executeQuery(q);
			Row row = null;
			try {
				while (ds.next()) {
					row = ds.getRow();
				}
			} finally {
				ds.close();
			}
			return row;
		} catch (Exception e) {
			throw new JdbcIntActivityPersistenceException(
					"Unable to retreive DataSet from the table - " + table.getName(), e);
		}
	}// ..end of the method

	/**
	 * called when select * from table is configured
	 * 
	 * @param datacontext
	 * @param table
	 * @param dataSet
	 * @return rowsetString
	 */
	private Row performSelectAll(DataContext datacontext, Table table) {
		logger.debug("routed when there is no column names..");
		Query q = datacontext.query().from(table).selectAll().toQuery();
		DataSet ds = datacontext.executeQuery(q);
		Row row = null;
		try {
			while (ds.next()) {
				row = ds.getRow();
			}
		} finally {
			ds.close();
		}
		return row;
	}// ..end of the method

	/**
	 * to get the result-set with INNER_JOIN operation on tables eg: SELECT gid,
	 * first_name, last_name, pid, gardener_id, plant_name FROM Gardners INNER
	 * JOIN Plantings ON gid = gardener_id
	 * 
	 * @param datacontext
	 * @param table1
	 * @param table2
	 * @param columnSelectKeySet
	 * @param mapOfConstraints
	 * @return row string from ineerJoin operation
	 */
	private Row performSelectForJoinQueries(DataContext datacontext, Query q) {
		DataSet dataSet = null;
		dataSet = datacontext.executeQuery(q);
		Row row = null;
		try {
			while (dataSet.next()) {
				row = dataSet.getRow();
			}
		} finally {
			dataSet.close();
		}
		return row;
	}// ..end of the method
}
