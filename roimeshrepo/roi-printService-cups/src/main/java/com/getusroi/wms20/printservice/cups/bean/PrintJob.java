package com.getusroi.wms20.printservice.cups.bean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.component.restlet.RestletConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Response;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.dynastore.DynaStoreFactory;
import com.getusroi.dynastore.DynaStoreSession;
import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.printservice.cups.IPrinterOperations;
import com.getusroi.wms20.printservice.cups.PrintServiceCupsConstant;
import com.getusroi.wms20.printservice.cups.Impl.CupsPrinterOperationException;
import com.getusroi.wms20.printservice.cups.Impl.CupsPrinterService;
import com.getusroi.wms20.printservice.cups.Impl.CupsPrinterServiceExcpetion;
import com.getusroi.wms20.printservice.cups.Impl.CupsPrinterShellService;
import com.getusroi.wms20.printservice.cups.Impl.PrinterOperationsImpl;

/**
 * This class is used to create print job
 * 
 * @author bizruntime
 *
 */
public class PrintJob extends AbstractROICamelJDBCBean {

	Logger logger = LoggerFactory.getLogger(PrintJob.class);

	/**
	 * This method is used to create the print job for print node
	 * 
	 * @param :
	 *            exchange : camel Exchange
	 * @throws CupsPrintServiceBeanException
	 * @throws CupsPrinterServiceExcpetion
	 * @throws IOException
	 */
	@Override
	protected void processBean(Exchange exchange)
			throws CupsPrintServiceBeanException, CupsPrinterServiceExcpetion, IOException {
		logger.debug(".processBean method of cups PrintJob");
		IPrinterOperations iPrinterOperations = new PrinterOperationsImpl();
		logger.debug("inside printjob method ");
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		logger.debug("mesh header data " + meshHeader);
		int jobId = 0;
		JSONObject jsonObject = null;
		jsonObject = getPrintabelData(meshHeader, exchange);
		String printerName = null, contentInString = null, contentType = null;
		try {
			printerName = jsonObject.getString(PrintServiceCupsConstant.PRINTERID_KEY);
			contentInString = jsonObject.getString("content");
			contentType = PrintServiceCupsConstant.CUPS_CONTENT_TYPE_KEY;
			if (contentType.equals(PrintServiceCupsConstant.CUPS_CONTENT_TYPE_KEY)) {
				try {
					jobId = iPrinterOperations.createPrintJob(contentInString, printerName);
				} catch (CupsPrinterOperationException e) {
					throw new CupsPrintServiceBeanException("Unable to create printjob for printerName :" + printerName
							+ ", content type : " + contentType, e);

				}
				logger.debug("The PrintJobId: " + jobId);
			} else if (contentType.equals("pdf")) {
				try {
					jobId = iPrinterOperations.createPrintJobpdf(contentInString, printerName);
				} catch (CupsPrinterOperationException e) {
					throw new CupsPrintServiceBeanException("Unable to create printjob for printerName :" + printerName
							+ ", content type : " + contentType, e);

				}
			}
			JSONObject jsonResponse = new JSONObject();
			jsonObject.put("response", jobId);
			exchange.getIn().setBody(jsonResponse.toString());

		} catch (CupsPrintServiceBeanException | JSONException e) {
			throw new CupsPrintServiceBeanException("Creating printJob exception occured" + e);
		}

	}

	/**
	 * This method is used to get printer deatils from print node server
	 * 
	 * @param exchange
	 *            : Camel Exchange Object
	 */
	public void setdataHeader(Exchange exchange) {
		String body = exchange.getIn().getBody(String.class);
		exchange.getIn().setHeader(PrintServiceCupsConstant.PRINTER_DETAILS_KEY, body);
	}

	/**
	 * This method is used to get the printable data for batch id
	 * 
	 * @param meshHeader
	 *            : Mesh Header Object
	 * @param exchange
	 *            : camel exchange Object
	 * @return JSONObject
	 * @throws CupsPrintServiceBeanException
	 * @throws CupsPrinterServiceExcpetion
	 * @throws IOException
	 */
	private JSONObject getPrintabelData(MeshHeader meshHeader, Exchange exchange)
			throws CupsPrintServiceBeanException, CupsPrinterServiceExcpetion, IOException {
		logger.info(".getJsonPrintJob() of PrintJob  ");
		Map<String, Object> genericdata = meshHeader.getGenricdata();

		// #TODO its has to be taken form exchange body instead of mesh header
		JSONArray jsonarr = (JSONArray) genericdata.get(MeshHeaderConstant.DATA_KEY);

		// String printerId =PrintServiceCupsConstant.PRINTER_ID_KEY;
		String printerId = null;
		JSONObject jsonobj = null;
		// expecting batchid and printer id on first index of json array
		try {
			jsonobj = (JSONObject) jsonarr.get(0);
			logger.info("The Json object incoming: " + jsonobj);
			String batchid = (String) jsonobj.get(PrintServiceCupsConstant.BATCHID_KEY);
			logger.debug("batchid : " + batchid);

			printerId = (String) jsonobj.get(PrintServiceCupsConstant.PRINTERID_KEY);

			if (printerId.equals("") || printerId.isEmpty() || printerId == null) {
				CupsPrinterService cups4jService = new CupsPrinterService();
				String defaultPrinterName = cups4jService.getDefaultPrinterName();
				CupsPrinterShellService shellService = new CupsPrinterShellService();
				printerId = shellService.getHostName().toUpperCase() + defaultPrinterName;
				logger.info("Default printerId: " + printerId);
			}

			byte[] contentFromBatch = getCompleteLabelForBatchId(meshHeader.getTenant(), meshHeader.getSite(), batchid);
			logger.debug("contentFromBatch : " + contentFromBatch);
			logger.info("The printerId: " + printerId);
			String title = batchid + "_target";
			String source = batchid + "_source";
			String printJobJson = null;

			printJobJson = "{ \"printerId\": " + printerId + ", \"title\": \"" + title + "\", \"contentType\": \""
					+ PrintServiceCupsConstant.CUPS_CONTENT_TYPE_KEY + "\", \"content\": \"" + contentFromBatch
					+ "\", \"source\": \"" + source + "\" }";
			JSONObject jsonOfPrintJob = null;
			try {
				jsonOfPrintJob = new JSONObject(printJobJson);
				logger.debug("jsonOfPrintJob : " + jsonOfPrintJob);
				return jsonOfPrintJob;
			} catch (JSONException e) {
				throw new CupsPrintServiceBeanException(
						"Error while byteStreaming the print url/print data for batchid : " + batchid + ", printerId : "
								+ printerId + ", data : " + contentFromBatch,
						e);
			}
		} catch (JSONException | UnsupportedEncodingException e1) {
			throw new CupsPrintServiceBeanException("Unable to parse the json Object : " + jsonobj.toString(), e1);
		}

	}// end of method

	/**
	 * This method is used to get the label data store in dynastore against
	 * batchid
	 * 
	 * @param tenant
	 *            : tenant id in string
	 * @param site
	 *            : site in string
	 * @param batchid
	 *            : batch id in string
	 * @return String : label data
	 * @throws UnsupportedEncodingException
	 */
	private byte[] getCompleteLabelForBatchId(String tenant, String site, String batchid)
			throws UnsupportedEncodingException {
		logger.debug(".getCompleteLabelForBatchId() of PrintJob");
		logger.debug(".getCompleteLabelForBatchId() of PrintJob");
		byte[] completeSubstitutedLabel = null;
		// get the printable data store in dynastore againt batchid
		DynaStoreSession cacheStrore = DynaStoreFactory.getDynaStoreSession(tenant, site, batchid);
		Object labeldata = (Object) cacheStrore.getSessionData(batchid);
		if (labeldata instanceof String) {
			String labelDataString = (String) labeldata;
			String replacedValue = labelDataString.trim().replace("\n", "").replace("\r", "");
			completeSubstitutedLabel = replacedValue.getBytes("UTF-8");
		} else if (labeldata instanceof byte[]) {
			completeSubstitutedLabel = (byte[]) labeldata;
		}
		return completeSubstitutedLabel;

	}

	public void restletResponse(Exchange exchange) {
		Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);
		response.setStatus(Status.SERVER_ERROR_INTERNAL);
		exchange.getIn().setHeader(RestletConstants.RESTLET_RESPONSE, response);
		exchange.getIn().setBody("The printer not fount");
	}

	/**
	 * This method is used to register all the cups printer to db
	 * 
	 * @param exchange
	 * @throws CupsPrintServiceBeanException
	 */
	public void registerPrinter(Exchange exchange) throws CupsPrintServiceBeanException {
		CupsPrinterService cupsPrinterService = new CupsPrinterService();
		try {
			cupsPrinterService.checkAndRegsiterPrinter();
		} catch (CupsPrinterServiceExcpetion e) {
			throw new CupsPrintServiceBeanException("Unable to register the printers ", e);
		}

	}

	/**
	 * This method is used to printer details
	 * 
	 * @param exchange
	 *            : Camel Exchange Object
	 * @throws CupsPrintServiceBeanException
	 */
	public void getPrintersAttr(Exchange exchange) throws CupsPrintServiceBeanException {
		IPrinterOperations iPrinterOperations = new PrinterOperationsImpl();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;
		ArrayList<Map<String, Object>> list;
		try {
			list = iPrinterOperations.getAllPrinterAttributes();
			for (int i = 0; i < list.size(); i++) {
				jsonObject = new JSONObject(list.get(i));
				jsonArray.put(jsonObject);
			}
			exchange.getIn().setBody(jsonArray);
		} catch (CupsPrinterOperationException e) {
			throw new CupsPrintServiceBeanException("Unable to all Printer Attributes ", e);
		}

	}// end of method

}
