package com.getusroi.wms20.printservice.cups;

import java.util.ArrayList;
import java.util.Map;

import com.getusroi.wms20.printservice.cups.Impl.CupsPrinterOperationException;
import com.getusroi.wms20.printservice.cups.bean.CupsPrintServiceBeanException;

public interface IPrinterOperations {

	public int createPrintJob(String contentInString, String printerName) throws CupsPrinterOperationException;
	public ArrayList<Map<String, Object>> getAllPrinterAttributes() throws CupsPrinterOperationException;
	public Map<String, Object> getPrintJobState(int jobId)throws CupsPrinterOperationException;
	public int createPrintJobpdf(String contentInString, String printerName)throws CupsPrinterOperationException;


}
