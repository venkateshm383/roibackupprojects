package com.getusroi.wms20.printservice.cups.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cups4j.CupsPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.wms20.printservice.cups.IPrinterOperations;
import com.getusroi.wms20.printservice.cups.PrintServiceCupsConstant;
import com.getusroi.wms20.printservice.cups.persistence.dao.CupsPersistenceException;
import com.getusroi.wms20.printservice.cups.persistence.dao.CupsPrinterDAO;

/**
 * This method is used to perform cups operation
 * @author bizruntime
 *
 */
public class PrinterOperationsImpl implements IPrinterOperations{
	static Logger log = LoggerFactory.getLogger(PrinterOperationsImpl.class.getName());

	/**
	 * This method is used to get create a print job 
	 * @param datacontent : data content to be printed in String
	 * @param printerUID : printer unique Id in String
	 * @throws CupsPrinterOperationException
	 */
	@Override
	public int createPrintJob(String datacontent, String printerUID) throws CupsPrinterOperationException {
		log.debug(".createPrintJob method of PrinterOperationsImpl");
		String printerName = null;
		CupsPrinterDAO printerDAO = new CupsPrinterDAO();
		try {
			//get printer name by passing printer unique id from db
			printerName = printerDAO.getPrinterNameByPrinterUID(printerUID);
			CupsPrinterService cups4jService = new CupsPrinterService();			
			try {
				//get default printer name and use this if printername from db is not available
				String defaultPrinterName = cups4jService.getDefaultPrinterName();
				if (printerName.equals("") || printerName == null || printerName.isEmpty()) {
					printerName = defaultPrinterName;
				}
				CupsPrinterShellService executeLpstat = new CupsPrinterShellService();				
				try {
					//get printer details and its status value 
					String printerAttributes = executeLpstat.getPrinterArrtibutes(printerName);
					log.debug("printerAttributes: " + printerAttributes);
					String state = executeLpstat.getStatusAllTogether(printerAttributes);
					int jobId = 0;
					if (state.equalsIgnoreCase(PrintServiceCupsConstant.PRINTER_IDLE)
							|| state.equalsIgnoreCase(PrintServiceCupsConstant.PRINTER_ONLINE)) {
						jobId = cups4jService.createCupsPrintJobWithOutPrinterStatus(datacontent, printerName);
					} else if (state.equalsIgnoreCase(PrintServiceCupsConstant.PRINTER_OFFLINE)
							|| state.equalsIgnoreCase(PrintServiceCupsConstant.PRINTER_OTHER)) {
						throw new CupsPrinterOperationException("PrintJob cannot be submitted, as the Printer is in state: " + state);
					}
					return jobId;
				} catch (CupsPrinterServiceExcpetion e1) {
					throw new CupsPrinterOperationException("Error in getting printer attributes and state of the printer for printer Name : "+printerName,e1);
				}

			} catch (CupsPrinterServiceExcpetion e) {
				throw new CupsPrinterOperationException("Error in getting the default printer Name ",e);
			}

		} catch (CupsPersistenceException e) {
			throw new CupsPrinterOperationException("Error in getting the printer Name using printer unique Id : "+printerUID,e);
		}
	}//end of method

	@Override
	public ArrayList<Map<String, Object>> getAllPrinterAttributes() throws CupsPrinterOperationException {
		log.debug(".getAllPrinterAttributes method of PrinterOperationsImpl");
		CupsPrinterService printerService = new CupsPrinterService();
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			List<CupsPrinter> printerDetailFromCupsList = printerService.getPrintersNameAndAttributes();
			if (!(printerDetailFromCupsList.isEmpty()) || printerDetailFromCupsList != null) {
				int listSize = printerDetailFromCupsList.size();
				log.debug("printerName and Attributes : " + listSize);
				CupsPrinter ptinterAttr = null;
				int fetchLen = 0;
				CupsPrinterDAO printerDAO = new CupsPrinterDAO();
				ArrayList<Map<String, String>> printerDetailsListFromDB = printerDAO.getRegisteredPrinters();
				if (!(printerDetailsListFromDB.isEmpty()) || printerDetailsListFromDB != null) {
					fetchLen = printerDetailsListFromDB.size();
					for (int i = 0; i < listSize; i++) {
						CupsPrinterShellService executeLpstat = new CupsPrinterShellService();
						String prntrName = printerDAO.getRegisteredPrinters().get(i)
								.get(PrintServiceCupsConstant.PRINTER_NAME_KEY);
						String prntrId = printerDAO.getRegisteredPrinters().get(i)
								.get(PrintServiceCupsConstant.PRINTERID_KEY);
						String printerAttributes = executeLpstat.getPrinterArrtibutes(prntrName);
						String state = executeLpstat.getStatusAllTogether(printerAttributes);
						Map<String, Object> map = new HashMap<>();
						ptinterAttr = printerService.getPrintersNameAndAttributes().get(i);
						map.put(PrintServiceCupsConstant.PRINTER_NAME_KEY, prntrName);
						map.put(PrintServiceCupsConstant.PRINTERID_KEY, prntrId);
						map.put(PrintServiceCupsConstant.PRINTER_STATE_KEY, state);
						map.put(PrintServiceCupsConstant.PRINTER_DESCRIPTION_KEY, ptinterAttr.getDescription());
						map.put(PrintServiceCupsConstant.PRINTER_LOCATION_KEY, ptinterAttr.getLocation());
						map.put(PrintServiceCupsConstant.PRINTER_URL_KEY, ptinterAttr.getPrinterURL());
						map.put(PrintServiceCupsConstant.PRINTER_CLASS_KEY, ptinterAttr.getClass());
						list.add(map);

					}
				} // end of inner if
			} // end of outer if

		} catch (CupsPrinterServiceExcpetion | CupsPersistenceException e) {
			throw new CupsPrinterOperationException("Error in getting the printer details ",e);
		}

		return list;
	}

	/**
	 * This method is used to get PrintJob status
	 * @throws CupsPrinterOperationException 
	 */
	@Override
	public Map<String, Object> getPrintJobState(int jobId) throws CupsPrinterOperationException {
		CupsPrinterService printerService = new CupsPrinterService();
		Map<String, Object> map;
		try {
			map = printerService.getPrintJobStatus(jobId);			
			return map;
		} catch (CupsPrinterServiceExcpetion e) {
			throw new CupsPrinterOperationException("Error in getting the print Job Status for job Id :  "+jobId,e);
		}
		
	}//end of method

	/**
	 * Thsi method is used to create printjob for pdf 
	 * @param contentInString : pdf content
	 * @param printerName : Printer Name
	 * @return int :  printJob Id
	 */
	@Override
	public int createPrintJobpdf(String contentInString, String printerName) {
		// TODO need to provide the implementation which will read pdf
		return 0;
	}
	
}
