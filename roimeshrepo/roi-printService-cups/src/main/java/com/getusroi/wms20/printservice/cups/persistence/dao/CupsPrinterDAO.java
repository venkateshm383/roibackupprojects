package com.getusroi.wms20.printservice.cups.persistence.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.getusroi.wms20.printservice.cups.helper.CupsPrinterConfiguration;

public class CupsPrinterDAO {
	Logger log = LoggerFactory.getLogger(CupsPrinterDAO.class);
	
	private static final String PRINTERNAME_BY_PRINTERUID_QUERY = "select printerName from printerregisters where printerUniqueId=?";
	private static final String REGISTERED_PRINTER_QUERY = "select printerName,printerUniqueId from printerregisters";
	private static final String INSERT_PRINTER_QUERY = "insert into printerregisters (printerName,printerUniqueId) values(?,?)";
	private static final String PRINTER_UNIQUEID_BY_PRINTERNAME_QUERY = "select printerUniqueId from printerregisters where printerName=?";
	/*
	 * public static void main(String[] args){ CupsPrinterDAO fetch = new
	 * CupsPrinterDAO();
	 * System.out.println(fetch.getRegisteredPrinters().size()); }
	 */

	/**
	 * This method is used to get then printer name by using printer unique Id
	 * from db
	 * 
	 * @param printerUID
	 *            : printer unique Id
	 * @return String : Printer Name
	 * @throws CupsPersistenceException
	 */
	public String getPrinterNameByPrinterUID(String printerUID) throws CupsPersistenceException {
		log.debug(".getPrinterNameByPrinterUID from CupsPrinterDAO");
		String sql = PRINTERNAME_BY_PRINTERUID_QUERY;
		String printerName = null;
		try {
			Connection conn = CupsPrinterConfiguration.getDbConnection();
			PreparedStatement pstatement = conn.prepareStatement(sql);
			pstatement.setString(1, printerUID);
			ResultSet rs = pstatement.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					printerName = rs.getString("printerName");
				}
			} else {
				throw new CupsPersistenceException(
						"There is no printer available with the uniqueId" + printerUID + " that you have requested");
			}
			CupsPrinterConfiguration.dbCleanup(conn, pstatement, rs);
			return printerName.toString();
		} catch (SQLException | ClassNotFoundException | IOException e) {
			throw new CupsPersistenceException("Error in getting printer name using printer Id : " + printerUID, e);
		}

	}// end of method

	/**
	 * This method is used to get all printer deatils from db
	 * 
	 * @return ArrayList<Map<String,String>>
	 * @throws CupsPersistenceException
	 */
	public ArrayList<Map<String, String>> getRegisteredPrinters() throws CupsPersistenceException {
		log.debug(".getRegisteredPrinters from CupsPrinterDAO");
		String sql = REGISTERED_PRINTER_QUERY;
		ArrayList<Map<String, String>> list = new ArrayList<>();
		try {
			Connection conn = CupsPrinterConfiguration.getDbConnection();
			PreparedStatement pstatement = conn.prepareStatement(sql);
			ResultSet rs = pstatement.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					String printerName = rs.getString("printerName");
					String printerId = rs.getString("printerUniqueId");
					Map<String, String> map = new HashMap<>();
					map.put("printerName", printerName);
					map.put("printerId", printerId);
					list.add(map);
				}
			} else {
				throw new CupsPersistenceException("There is no printer available into the db");
			}
			CupsPrinterConfiguration.dbCleanup(conn, pstatement, rs);
			return list;
		} catch (SQLException | ClassNotFoundException | IOException e) {
			throw new CupsPersistenceException("Error in getting the list of all printers from DB ", e);
		}

	}// end of method

	/**
	 * This method is used to register new printer in db using printer unique id
	 * and printer name
	 * 
	 * @param uniqueId
	 *            : Printer unique id in String
	 * @param printerName
	 *            : Printer Name in String
	 * @throws CupsPersistenceException
	 */
	public void registerPrinterByUniqueIDAndPrinterName(String uniqueId, String printerName)
			throws CupsPersistenceException {
		log.debug(".getPrinterUIdByPrinterName method of CupsPrinterDAO");
		try {
			Connection conn = CupsPrinterConfiguration.getDbConnection();
			String sql = INSERT_PRINTER_QUERY;
			PreparedStatement pstatement = conn.prepareStatement(sql);
			pstatement.setString(1, printerName);
			pstatement.setString(2, uniqueId);
			pstatement.executeUpdate();
			CupsPrinterConfiguration.dbCleanUp(conn, pstatement);
		} catch (SQLException | ClassNotFoundException | IOException e) {
			throw new CupsPersistenceException("Error in inserting new printer in db with unique id : " + uniqueId
					+ ", and printer name : " + printerName, e);

		}
	}// end of method

	/**
	 * This method is used to get PrinterUniqueId from db using printerName
	 * 
	 * @param printerName
	 *            : PrinterName in String
	 * @return String : Printer Unique Id
	 * @throws CupsPersistenceException
	 */
	public String getPrinterUIdByPrinterName(String printerName) throws CupsPersistenceException {
		log.debug(".getPrinterUIdByPrinterName method of CupsPrinterDAO");
		String printerUniqueId = null;
		Connection conn = null;
		PreparedStatement pstatement = null;
		ResultSet rs = null;

		try {
			conn = CupsPrinterConfiguration.getDbConnection();
			String sql = PRINTER_UNIQUEID_BY_PRINTERNAME_QUERY;
			pstatement = conn.prepareStatement(sql);
			pstatement.setString(1, printerName);
			rs = pstatement.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					printerUniqueId = rs.getString("printerUniqueId");
				}
			} else {
				throw new CupsPersistenceException(
						"Unable to get the printer unique id use printer Name : " + printerName);
			}

		} catch (SQLException | ClassNotFoundException | IOException e) {
			throw new CupsPersistenceException(
					"Error in getting new printer  unique id using printer name : " + printerName, e);
		}
		CupsPrinterConfiguration.dbCleanup(conn, pstatement, rs);
		return printerUniqueId.toString();
	}// end of method

}
