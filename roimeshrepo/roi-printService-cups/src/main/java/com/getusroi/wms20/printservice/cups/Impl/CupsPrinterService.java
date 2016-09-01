package com.getusroi.wms20.printservice.cups.Impl;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintJobAttributes;
import org.cups4j.PrintRequestResult;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.getusroi.wms20.printservice.cups.PrintServiceCupsConstant;
import com.getusroi.wms20.printservice.cups.bean.CupsPrintServiceBeanException;
import com.getusroi.wms20.printservice.cups.helper.CupsPrinterConfiguration;
import com.getusroi.wms20.printservice.cups.persistence.dao.CupsPrinterDAO;

/**
 * This class is used for cups client operation like getting printers ,getting its attribute etc
 * @author bizruntime
 *
 */
public class CupsPrinterService {
	Logger log = LoggerFactory.getLogger(CupsPrinterService.class);


	/**
	 * This method is used to get Printers name and its associated attributes
	 * 
	 * @return List<CupsPrinter>
	 * @throws CupsPrinterServiceExcpetion
	 */
	public List<CupsPrinter> getPrintersNameAndAttributes() throws CupsPrinterServiceExcpetion {
		log.debug(".getPrintersNameAndAttributes method of CupsPrinterService");
		try {
			CupsClient cupsClient = CupsPrinterConfiguration.configureClient();
			List<CupsPrinter> list = cupsClient.getPrinters();
			log.debug("The List of Cups Printers : " + list);
			return list;
		} catch (Exception e) {
			log.error("Connecting to Cups Printer error: ", e.getCause());
			throw new CupsPrinterServiceExcpetion("Connecting to Cups Printer error: ", e);
		}

	}// end of method

	/**
	 * This method is used to get the default printer name 
	 * @return String : default printer name
	 * @throws CupsPrinterServiceExcpetion 
	 * @throws CupsPrintServiceBeanException
	 */
	public String getDefaultPrinterName() throws CupsPrinterServiceExcpetion{
		log.debug(".getDefaultPrinterNameAndAttributes method of CupsPrinterService");
		try {
			CupsClient cupsClient = CupsPrinterConfiguration.configureClient();
			//cups printer default name
			String printerName = cupsClient.getDefaultPrinter().getName();			
			log.debug("The DefaultPrinter Name: " + printerName);
			return printerName;
		} catch (Exception e) {
			throw new CupsPrinterServiceExcpetion("Connecting to Cups Printer error: ", e);
		}

	}// end of method

	/**
	 * This method is used to create print job without printer status
	 * 
	 * @param content
	 *            : content to be printed
	 * @param printerName
	 *            : printer Name
	 * @return int : Job Id
	 * @throws CupsPrintServiceBeanException
	 */
	public int createCupsPrintJobWithOutPrinterStatus(String content, String printerName)
			throws CupsPrinterServiceExcpetion {
		log.debug(".createCupsPrintJobWithOutPrinterStatus method of CupsPrinterService");
		byte[] document = content.getBytes();
		int jobId = 0;
		try {
			CupsClient cupsClient = CupsPrinterConfiguration.configureClient();
			List<CupsPrinter> printerList = cupsClient.getPrinters();
			// logic to create printjob without status when printername contain in list of printers
			for (int i = 0; i < printerList.size(); i++) {
				String prntrNam = printerList.get(i).getName();
				if (prntrNam.equals(printerName)) {
					URL url = printerList.get(i).getPrinterURL();
					boolean isDefault = printerList.get(i).isDefault();
					CupsPrinter cupsPrinter = new CupsPrinter(url, printerName, isDefault);
					PrintJob printJob = new PrintJob.Builder(document).build();
					Map<String, String> printJobAttributes = new HashMap<>();
					printJobAttributes.put("jobName", printerName + "cups" + i);
					printJob.setAttributes(printJobAttributes);
					PrintRequestResult printRequestResult = cupsPrinter.print(printJob);
					jobId = printRequestResult.getJobId();
					log.debug("PrintJob created successfully! ");
					break;
				}
			}
		} catch (Exception e) {
			throw new CupsPrinterServiceExcpetion("Connecting to Cups Printer error: ", e.getCause());
		}
		return jobId;
	}

	/**
	 * This method is used to get print job status and other attributes
	 * @param jobId : print job if in String
	 * @return Map<String, Object>
	 * @throws CupsPrinterServiceExcpetion
	 */
	public Map<String, Object> getPrintJobStatus(int jobId) throws CupsPrinterServiceExcpetion {
		Map<String, Object> map = new HashMap<>();
		try {
			// logic to get the print job status and other attribute
			CupsClient cupsClient = CupsPrinterConfiguration.configureClient();
			PrintJobAttributes jobAttributes = cupsClient.getJobAttributes(jobId);
			map.put(PrintServiceCupsConstant.PRINTERID_KEY, jobAttributes.getJobID());
			map.put(PrintServiceCupsConstant.PRINTER_STATE_KEY, jobAttributes.getJobState());
			map.put(PrintServiceCupsConstant.PRINTER_CREATION_TIME_KEY, jobAttributes.getJobCreateTime());
			map.put(PrintServiceCupsConstant.PRINTER_COMPLETION_TIME_KEY, jobAttributes.getJobCompleteTime());
			map.put(PrintServiceCupsConstant.PRINT_JOB_NAME_KEY, jobAttributes.getJobName());
			map.put(PrintServiceCupsConstant.PRINT_JOB_URL_KEY, jobAttributes.getJobURL());
			map.put(PrintServiceCupsConstant.PRINT_PAGE_PRINTED_KEY, jobAttributes.getPagesPrinted());
		} catch (Exception e) {
			log.error("Connecting to Cups Printer error: ", e.getCause());
			throw new CupsPrinterServiceExcpetion("Error in getting the job status attributes for job Id : " + jobId,e);
		}
		return map;
	}

	/**
	 * This method is used to check if printer exist in database, if not register it to database
	 * @throws CupsPrinterServiceExcpetion
	 */
	public void checkAndRegsiterPrinter() throws CupsPrinterServiceExcpetion {
		log.debug(".checkAndRegsiterPrinter method of CupsPrinterService");
		List<CupsPrinter> list = null;
		CupsPrinterShellService attrImpl = new CupsPrinterShellService();
		CupsPrinterDAO printerDAO = new CupsPrinterDAO();		
		try {
			CupsClient client = CupsPrinterConfiguration.configureClient();
			list = client.getPrinters();
			for (CupsPrinter cupsPrinter : list) {
				String printerName = cupsPrinter.getName();
				String hostName = attrImpl.getHostName();
				String uniqueId = hostName.toUpperCase().concat(printerName);
				String printerUID = printerDAO.getPrinterUIdByPrinterName(printerName);
				if (printerUID.equals(uniqueId)) {
					continue;
				} else {
					printerDAO.registerPrinterByUniqueIDAndPrinterName(uniqueId, printerName);
				}
			}//end of for loop
		} catch (Exception e) {
			throw new CupsPrinterServiceExcpetion("Unable to register printer due to error in getting cups client connection ",e);
		}

	}//end of method


	/*
	 * public static void main(String[] args) throws IOException {
	 * PrinterAttributesCups4jImpl test = new PrinterAttributesCups4jImpl();
	 * test.getPrintersNameNAttributes(); }
	 */
}
