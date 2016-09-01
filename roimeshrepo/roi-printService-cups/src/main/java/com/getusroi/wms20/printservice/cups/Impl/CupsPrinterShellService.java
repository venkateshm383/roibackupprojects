package com.getusroi.wms20.printservice.cups.Impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.wms20.printservice.cups.PrintServiceCupsConstant;
import com.getusroi.wms20.printservice.cups.bean.CupsPrintServiceBeanException;

/**
 * This class is used for all cups shell programming
 * @author bizruntime
 *
 */
public class CupsPrinterShellService {

	Logger log = LoggerFactory.getLogger(CupsPrinterShellService.class);

	
	/**
	 * This method is used to get the printer status from shell
	 * @return List<String> : all printer status and other details
	 * @throws CupsPrinterServiceExcpetion
	 */
	private List<String> getPrinterstatusFromShell() throws CupsPrinterServiceExcpetion{
		log.debug(".getPrinterstatusFromShell method of CupsPrinterShellService");
		String str = null;
		List<String> list = null;
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("lpstat -p");
			StringBuffer sbfr = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while ((str = br.readLine()) != null) {
				log.debug("The printer and its Status is : " + str);
				sbfr.append(str);
				sbfr.append(",");
			}
			String[] strings = sbfr.toString().split(",");
			list = Arrays.asList(strings);
			log.debug("From the List : " + list);
			proc.waitFor();
			int exitVal = proc.exitValue();
			log.info("Process exitValue: " + exitVal);
			proc.destroy();
			return list;
		} catch (Throwable t) {
			throw new CupsPrinterServiceExcpetion("executing comand failed : ",t);			
		}
		
	}//end of method

	/**
	 * This method is used to check if printer is in ideal state
	 * @param str : printer deatils containing status
	 * @return
	 */
	public String getParsedValueofPrinterStatus(String str) {
		log.debug(".getParsedValueofPrinterStatus method of CupsPrinterShellService");
		String txt = str;
		String state = null;
		String re1 = ".*?";
		String re2 = "(idle)";
		Pattern p = Pattern.compile(re1 + re2, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(txt);
		if (m.find()) {
			state = m.group(1);
		}
		return state.toUpperCase();
	}

	/**
	 * This method is used to check if printer is in enabled or offline state
	 * @param str : printer details in string containing status
	 * @return printer status
	 * @throws NullPointerException
	 */
	private String disabledOREnabledStatus(String str) throws NullPointerException {
		String txt = str;
		String re1 = ".*?";
		String re2 = "(enabled)";
		String var1 = null;
		Pattern p = Pattern.compile(re1 + re2, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(txt);
		if (m.find()) {
			var1 = m.group(1);
		}
		return var1.toString();
	}

	/**
	 * This method is used to find out the printer state
	 * @param str : printer details containing status
	 * @return String enable or OFFLINE
	 */
	public String getStatusAllTogether(String str) {
		String state = null;
		try {
			String status = disabledOREnabledStatus(str);
			if (status.equals("enabled")) {
				state = getParsedValueofPrinterStatus(str);
			} else {
				state =PrintServiceCupsConstant.PRINTER_OFFLINE;
			}
		} catch (NullPointerException npe) {
			state =PrintServiceCupsConstant.PRINTER_OFFLINE;
		}
		return state.toUpperCase();
	}//end of method

	/**
	 * This method is used to get specific printer detail
	 * @param printerName : Printer Name
	 * @return printer detail
	 * @throws CupsPrinterServiceExcpetion
	 */
	public String getPrinterArrtibutes(String printerName) throws CupsPrinterServiceExcpetion{
		log.debug(".getPrinterArrtibutes method of CupsPrinterShellService");
		String str = null;
		int srtlen = 0;
		String concRev = null;
		int nameLen = printerName.length();		
		try {
			List inputArray = getPrinterstatusFromShell();
			for (int i = 0; i < inputArray.size(); i++) {
				String newString;
				srtlen = ((String) inputArray.get(i)).length();
				if (srtlen <= 8 || !inputArray.get(i).toString().contains(printerName)) {
					StringBuilder builder = new StringBuilder();
					builder.append(printerName);
					concRev = builder.reverse().toString().concat(printerName.toString());
					newString = inputArray.get(i).toString().concat(concRev);
				} else {
					newString = inputArray.get(i).toString();
				}
				if (newString.substring(8, 8 + nameLen).equals(printerName)) {
					str = (String) inputArray.get(i);
					break;
				}
			}
			log.debug("The string which contains name: " + printerName + " is: " + str);
			return str;
		} catch (CupsPrinterServiceExcpetion e) {
			throw new CupsPrinterServiceExcpetion("Error getting printer details from shell : ",e);
		}		
	}//end of method
	
	/**
	 * This methos is used to get the host name
	 * @return String : host name
	 * @throws IOException
	 */
	public String getHostName() throws IOException{
		log.debug(".getHostName method of CupsPrinterShellService");
		 String str = null, hostname=null;		   
				Runtime rt = Runtime.getRuntime();
				Process proc = rt.exec("hostname");				
				BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				while ((str = br.readLine()) != null) {
					hostname = str;
				}		
		return hostname;
		
	}
	
	/**
	 * This method is used to get the List of all printer names
	 * @return List<String> : List of all printers name
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public List<String> getPrinterNamesFromShell() throws IOException, InterruptedException{
		log.debug(".getPrinterNamesFromShell method of CupsPrinterShellService");
		 String str = null, strhname=null;List<String> list = null;		   
				Runtime rt = Runtime.getRuntime();
				Process proc = rt.exec("lpstat -s");
				StringBuilder stringBuilder = new StringBuilder();
				BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				while ((str = br.readLine()) != null) {
					stringBuilder.append(str);
					stringBuilder.append(",");
				}
				String[] strings = stringBuilder.toString().split(",");
				list = Arrays.asList(strings);
				log.debug("From the List : " + list);
				proc.waitFor();
				int exitVal = proc.exitValue();
				log.debug("Process exitValue: " + exitVal);
				proc.destroy();		   
		return list;
	}//end of method
	
}
