package com.getusroi.wms20.printservice.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.config.RequestContext;
import com.getusroi.dynastore.DynaStoreFactory;
import com.getusroi.dynastore.DynaStoreSession;
import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.printservice.PrintNodePropertiesLoadingException;
import com.getusroi.printservice.PrintServicePrintNodeConstant;
import com.getusroi.printservice.PrintServicePrintNodePropertiesHelper;

/**
 * This class is used to create print job
 * @author bizruntime
 *
 */
public class PrintJob extends AbstractROICamelJDBCBean {

	Logger logger = LoggerFactory.getLogger(PrintJob.class);

	/**
	 * This method is used to create the print job for print node
	 * @param : exchange : camle Exchange
	 * @throws PrintServiceException 
	 */
	@Override
	public void processBean(Exchange exchange) throws PrintServiceException {
		logger.debug(".processBean method of PrintJob");
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		logger.debug("mesh header datat " + meshHeader);
		String userNameEncodeData = PrintNodeApi.getEncodedUserName();
		PrintServicePrintNodePropertiesHelper propHelp=new PrintServicePrintNodePropertiesHelper();
		JSONObject printJobJson = null;
		try {
			Properties prop=propHelp.loadingPropertiesFile();
			//get the printable data which need to send to print nide server
			printJobJson = getPrintabelData(meshHeader, exchange);
			logger.debug("Json requestData: "+printJobJson);
			logger.debug("userNameEncodeData : " + userNameEncodeData);
			//set the camel http header to make call to printnode server
			exchange.getIn().setHeader("Authorization","Basic " + userNameEncodeData);
			exchange.getIn().setHeader(PrintServicePrintNodeConstant.CAMEL_HTTP_METHOD_KEY, "POST");
			exchange.getIn().setHeader(PrintServicePrintNodeConstant.HTTP_CONTENT_TYPE_KEY,prop.getProperty(PrintServicePrintNodeConstant.HTTP_CONTENT_TYPE_KEY));
			exchange.getIn().setHeader(PrintServicePrintNodeConstant.CAMEL_HTTP_URI_KEY,prop.getProperty(PrintServicePrintNodeConstant.CAMEL_HTTP_URI_KEY)+"printjobs");
			//get the response data from printnode server and store in json object
			String output=sendPrintableDataToPrintNode(printJobJson,userNameEncodeData,prop);
			JSONObject jsonResponse=new JSONObject();
			try {
				jsonResponse.put("response",output);
			} catch (JSONException e) {
				throw new PrintServiceException("Unable to create response json object for creating print job ",e);
			}
			exchange.getIn().setBody(jsonResponse.toString());

		} catch (PrintServiceException | PrintNodePropertiesLoadingException e1) {
			throw new PrintServiceException("Getting the Printable data encountered exception: "+ e1);

		}	
	
	}//end of method

	/**
	 * This method is used to send printable data to printnode server and send back its response
	 * @param printJobJson : Printable data in JSON format
	 * @param userNameEncodeData : User encode data
	 * @param prop : properties file
	 * @return String : response data from server
	 * @throws PrintServiceException
	 */
	private String sendPrintableDataToPrintNode(JSONObject printJobJson,String userNameEncodeData,Properties prop) throws PrintServiceException{
		logger.debug(".sendPrintableDataToPrintNode method of printjob bean");
		URL url = null;
		HttpURLConnection conn = null;
		try {
			//making connecting with printnode server for creating print job using http post method
			url = new URL(prop.getProperty(PrintServicePrintNodeConstant.CAMEL_HTTP_URI_KEY)+"printjobs");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty(PrintServicePrintNodeConstant.PRINTNODE_AUTHORIZATION,
					PrintServicePrintNodeConstant.PRINTNODE_BASIC+ userNameEncodeData);
			conn.setRequestProperty(PrintServicePrintNodeConstant.HTTP_CONTENT_TYPE_KEY,prop.getProperty(PrintServicePrintNodeConstant.HTTP_CONTENT_TYPE_KEY));
			byte[] outputInBytes = null;
			try {
				//data to be post to printnode server
				outputInBytes = printJobJson.toString().getBytes("UTF-8");
				OutputStream os = null;
				try {
					os = conn.getOutputStream();
					os.write(outputInBytes);
					os.close();
				} catch (IOException e2) {
					throw new PrintServiceException("Write to data api exception ",e2);
				}				
			} catch (UnsupportedEncodingException e3) {
				throw new PrintServiceException("Encoding unsupported from the string"+ e3);
			}
			//getting the response from server 
			BufferedReader br = null;
			String output = null;
			try {
				br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));
				while ((output = br.readLine()) != null) {
					logger.debug("data coming from server : " + output);
				}
			} catch (IOException e) {
				throw new PrintServiceException("Respose not in format: " + e.getMessage());
			}
			conn.disconnect();
			return output;
		} catch (IOException e) {
			throw new PrintServiceException("Unable to make connection with url : "+url,e);
		}
		
	}//end of method 

	/**
	 * This method is used setup the authentication to make connection with print node server for all printers
	 * @param exchange : Camel Exchange Object
	 * @throws PrintServiceException
	 */
	public void getPrintersAuth(Exchange exchange) throws PrintServiceException {
		logger.debug(".getPrintersAuth of printJob bean");
		String userNameEncodeData = PrintNodeApi.getEncodedUserName();
		PrintServicePrintNodePropertiesHelper propHelp=new PrintServicePrintNodePropertiesHelper();
		try {
			Properties prop=propHelp.loadingPropertiesFile();
			exchange.getIn().setHeader(PrintServicePrintNodeConstant.PRINTNODE_AUTHORIZATION,
					PrintServicePrintNodeConstant.PRINTNODE_BASIC+ userNameEncodeData);
			exchange.getIn().setHeader(PrintServicePrintNodeConstant.HTTP_CONTENT_TYPE_KEY,prop.getProperty(PrintServicePrintNodeConstant.HTTP_CONTENT_TYPE_KEY));
			exchange.getIn().setHeader(PrintServicePrintNodeConstant.CAMEL_HTTP_METHOD_KEY,prop.getProperty(PrintServicePrintNodeConstant.CAMEL_HTTP_METHOD_KEY));
			exchange.getIn().setHeader(PrintServicePrintNodeConstant.CAMEL_HTTP_URI_KEY,prop.getProperty(PrintServicePrintNodeConstant.CAMEL_HTTP_URI_KEY)+"printers");
			logger.debug(".getPrintersAuth of printJob bean "+prop.getProperty(PrintServicePrintNodeConstant.CAMEL_HTTP_URI_KEY)+"printers");
			
		} catch (PrintNodePropertiesLoadingException e) {
			throw new PrintServiceException("Unable to load the property file ",e);
		}
		
	}//end of method
	
	/**
	 * This method is used to get printer deatils from print node server
	 * @param exchange : Camel Exchange Object
	 */
	public void setdataHeader(Exchange exchange) {
		String body = exchange.getIn().getBody(String.class);
		exchange.getIn().setHeader(PrintServicePrintNodeConstant.PRINTER_DETAILS_KEY, body);
	}//end of method

	/**
	 * This method is used to generate the json data required by printnode server to create print job
	 * @param meshHeader : Mesh header Object
	 * @param exchange : Camel exchange Obejct
	 * @return JSONObject : json object to create print job
	 * @throws PrintServiceException
	 */
	private JSONObject getPrintabelData(MeshHeader meshHeader, Exchange exchange)
			throws PrintServiceException {
		logger.debug(".getJsonPrintJob() of PrintJob  ");
		RequestContext reqCxt=meshHeader.getRequestContext();
		Map<String, Object> genericdata = meshHeader.getGenricdata();		
		//#TODO This has to be taken from camel exchange instead of mesh Header
		JSONArray jsonarr = (JSONArray) genericdata.get(MeshHeaderConstant.DATA_KEY);
		
		JSONObject jsonOfPrintJob=null;
		String printerId = null;
		//get the latest online printer
		String printerIdDefault = getLatestConfiguredOnlinePrinterId(exchange.getIn().getHeaders().get(PrintServicePrintNodeConstant.PRINTER_DETAILS_KEY).toString());
		PrintServicePrintNodePropertiesHelper propHelp=new PrintServicePrintNodePropertiesHelper();
		try {
			//excpecting always json array will have only one json object containg batchid and printerid
			JSONObject jsonobj = (JSONObject) jsonarr.get(0);
			String batchid = (String) jsonobj.get(PrintServicePrintNodeConstant.BATCHID_KEY);
			logger.debug("batchid : " + batchid);
			byte[] contentFromBatch=null;
			try {
				contentFromBatch = getCompleteLabelForBatchId(reqCxt, batchid);				
				logger.debug("contentFromBatch : " + contentFromBatch);
				String title = batchid + "_target";
				String soursce = batchid + "_source";
				String printJobJson = null;
				try {
					//logic to see if printer id is coming in request if yes, then also check its not empty.If empty use default printer id
					if (jsonobj.has(PrintServicePrintNodeConstant.PRINTERID_KEY)) {
						printerId = jsonobj.get(PrintServicePrintNodeConstant.PRINTERID_KEY).toString();
						if(printerId.isEmpty() || printerId.equals(""))
							printerId = printerIdDefault;
					} else {
						//printer id is not availbel in request data therefore use default online printer id
						printerId = printerIdDefault;
					}
					Properties prop=propHelp.loadingPropertiesFile();
					String contentEncodedata = net.iharder.Base64.encodeBytes(contentFromBatch);
					printJobJson = "{ \"printerId\": "+printerId+ ", \"title\": \""+title+ "\", \"contentType\": \""+prop.get(PrintServicePrintNodeConstant.PRINTNODE_CONTENT_TYPE_KEY)+"\", \"content\": \""+contentEncodedata+"\", \"source\": \""+ soursce + "\" }";
					try {
						jsonOfPrintJob = new JSONObject(printJobJson);
						logger.debug("jsonOfPrintJob : " + jsonOfPrintJob);
						return jsonOfPrintJob;

					} catch (JSONException e) {
						throw new PrintServiceException("Error while encoding the print url/print data : "+jsonOfPrintJob.toString() + ", data : "+ contentFromBatch, e);
					}					
				} catch (JSONException | PrintNodePropertiesLoadingException e) {
					throw new PrintServiceException("Getting printerId failed or loading propety file failed",e);
				}
			} catch (UnsupportedEncodingException e1) {
				throw new PrintServiceException("unable to get the data store for batch id in dynastore for printing ",e1);
			}
		} catch (JSONException e1) {
			throw new PrintServiceException("Failed to get batchId",e1);
		}
		
	}//end of method

	/**
	 * This method is used to get the label data store in dynastore against batchid
	 * @param tenant : tenant id in string
	 * @param site : site in string
	 * @param batchid : batch id in string
	 * @return String : label data
	 * @throws UnsupportedEncodingException 
	 */
	private byte[] getCompleteLabelForBatchId(RequestContext reqCxt,
			String batchid) throws UnsupportedEncodingException{
		logger.debug(".getCompleteLabelForBatchId() of PrintJob");
		byte[] completeSubstitutedLabel=null;
		//get the printable data store in dynastore againt batchid
		DynaStoreSession cacheStrore = DynaStoreFactory.getDynaStoreSession(reqCxt, batchid);
		Object labeldata = (Object) cacheStrore
				.getSessionData(batchid);	
		if(labeldata instanceof String){
			String labelDataString=(String)labeldata;
			String replacedValue = labelDataString.trim().replace("\n", "").replace("\r","");
			completeSubstitutedLabel = replacedValue.getBytes("UTF-8");
		}else if(labeldata instanceof byte[]){
			completeSubstitutedLabel =(byte[])labeldata;
		}
	
		return completeSubstitutedLabel;

	}

	/**
	 * This method is to get the Latest configured default online printer
	 * @param jsonarrayFromPrintNodeApi : detail about all printer configured in json array string format
	 * @return String : printer id which id active
	 * @throws PrintServiceException
	 */
	private String getLatestConfiguredOnlinePrinterId(String jsonarrayFromPrintNodeApi) throws PrintServiceException {
		logger.debug(".getLatestConfiguredOnlinePrinterId of PrintJob");
		String printerId = "";
		JSONArray unsortedJsonArray = null;
		try {
			unsortedJsonArray = new JSONArray(jsonarrayFromPrintNodeApi);
			try {
				JSONArray sortedJsonArray=sortPrinterBasedOnCreateTime(unsortedJsonArray);
				printerId=getOnlinePrinterBasedState(sortedJsonArray);
			} catch (PrintServiceException e) {
				throw new PrintServiceException("Unable to get the default printer id which is online ",e);
			}
		} catch (JSONException e1) {
			throw new PrintServiceException("Json Array format not suitable from Api: "+ e1.getMessage());

		}	
		return printerId;
	}

	/**
	 * This method is used to sort the printers configured based on creation time
	 * @param unsortedJsonArray : List of all printers with detail in unsorted json array
	 * @return JSONArray : List of all printers with detail in sorted json array
	 * @throws PrintServiceException
	 */
	private JSONArray sortPrinterBasedOnCreateTime(JSONArray unsortedJsonArray) throws PrintServiceException{
		logger.debug(".sortPrinterBasedOnCreateTime of PrintJob");
		JSONArray sortedJsonArray = new JSONArray();
		List<JSONObject> unsortedJsonInList = new ArrayList<JSONObject>();
		for (int i = 0; i < unsortedJsonArray.length(); i++) {
			try {
				unsortedJsonInList.add(unsortedJsonArray.getJSONObject(i));
			} catch (JSONException e) {
				throw new PrintServiceException("Failed iterate over JSON Array "+unsortedJsonArray.toString()+" for index : "+i,e);
			}
		}
		if(unsortedJsonInList !=null || !(unsortedJsonInList.isEmpty())){
		//logic to sort json list based on creation time stamp
		Collections.sort(unsortedJsonInList, new Comparator<JSONObject>() {			
			@Override
			public int compare(JSONObject oldobj, JSONObject newobj) {
				String valoldobj=null;
				String valnewobj=null;
				try {
					valoldobj = (String) oldobj.get(PrintServicePrintNodeConstant.PRINTER_CREATION_TIME_KEY);
					valnewobj = (String) newobj.get(PrintServicePrintNodeConstant.PRINTER_CREATION_TIME_KEY);
				} catch (JSONException e) {					
					logger.error("Unable to get value from jsob object for key : "+PrintServicePrintNodeConstant.PRINTER_CREATION_TIME_KEY,e);

				}
				return valoldobj.compareTo(valnewobj);
			}
		});		
		//storing sorted list into sorted JSON array
		for (int i = 0; i < unsortedJsonArray.length(); i++) {
			sortedJsonArray.put(unsortedJsonInList.get(i));
		}
		}//end of if
		return sortedJsonArray;
	}//end of method
	
	/**
	 * This method is used to give latest printer id which is online
	 * @param sortedJsonArray : List of all printers with detail in sorted json array
	 * @return String : PrinterID
	 * @throws PrintServiceException
	 */
	private String getOnlinePrinterBasedState(JSONArray sortedJsonArray) throws PrintServiceException{
		logger.debug(".getOnlinePrinterBasedState method of PrintJob");
		JSONObject jsonObject=null;
		String printerId=null;
		//logic to iterate over each printer info and  get the printer id whose state is online
		for (int i = sortedJsonArray.length() - 1; i >= 0; i--) {
			try {
				jsonObject = sortedJsonArray.getJSONObject(i);
				try {
					String state = jsonObject.getString(PrintServicePrintNodeConstant.PRINTER_STATE_KEY);
					if (state.equals(PrintServicePrintNodeConstant.PRINTER_ONLINE_KEY)) {
						try {
							printerId = jsonObject.get(PrintServicePrintNodeConstant.PRINTER_ID_KEY).toString();
						} catch (JSONException e) {
							throw new PrintServiceException("JsonObject Parse Exception for Key:Id "
									+ e.getMessage());
						}
						break;
						}
				} catch (JSONException e) {
					throw new PrintServiceException("JsonObject Parse Exception for Key:state "
							+ e.getMessage());
				}
			} catch (JSONException e) {
				throw new PrintServiceException("Sorted JsonArray Parse exception"
						+ e.getMessage());
			}			
		}//end of  for loop
		return printerId;
	}//end of method 
}
