package com.getusroi.wms20.printservice.bean;

import java.util.Map;
import java.util.Properties;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.printservice.PrintNodePropertiesLoadingException;
import com.getusroi.printservice.PrintServicePrintNodeConstant;
import com.getusroi.printservice.PrintServicePrintNodePropertiesHelper;

/**
 * This class is to get printjob status
 * @author bizruntime
 *
 */
public class PrintStatus extends AbstractROICamelJDBCBean{

	Logger logger = LoggerFactory.getLogger(PrintStatus.class);
	
	/**
	 * This method is used to get The printer status
	 * @param exchange : Camel Exchange
	 */
	@Override
	public void processBean(Exchange exchange) throws PrintServiceException {
		logger.debug(".processBean() of PrintStatus");
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		logger.debug("mesh header data "+meshHeader);
		Map<String,Object> genericdata=meshHeader.getGenricdata();
		
		//#TODO json array has to be taken from exchange not from mesh header
		JSONArray jsonarr=(JSONArray)genericdata.get(MeshHeaderConstant.DATA_KEY);
		
		PrintServicePrintNodePropertiesHelper propHelp=new PrintServicePrintNodePropertiesHelper();
		try {
			Properties prop=propHelp.loadingPropertiesFile();
			int jsonArrLen=jsonarr.length();
			for(int i=0;i<jsonArrLen;i++){
				try {
					JSONObject jsonObj=jsonarr.getJSONObject(i);				
					String printJobId = (String)jsonObj.get(PrintServicePrintNodeConstant.PRINT_JOB_ID_KEY);
					String userNameEncodeData = PrintNodeApi.getEncodedUserName();
					exchange.getIn().setHeader(PrintServicePrintNodeConstant.PRINTNODE_AUTHORIZATION,
							PrintServicePrintNodeConstant.PRINTNODE_BASIC+ userNameEncodeData);
					exchange.getIn().setHeader(PrintServicePrintNodeConstant.CAMEL_HTTP_METHOD_KEY,prop.getProperty(PrintServicePrintNodeConstant.CAMEL_HTTP_METHOD_KEY));
					exchange.getIn().setHeader(PrintServicePrintNodeConstant.CAMEL_HTTP_URI_KEY,prop.getProperty(PrintServicePrintNodeConstant.CAMEL_HTTP_URI_KEY)+"printjobs/"+ printJobId+ "/states");
					exchange.getIn().setBody(printJobId);
				} catch (JSONException e) {
					throw new PrintServiceException("Unable to featch json Object at index : "+i,e);
				}
			}
		} catch (PrintNodePropertiesLoadingException e1) {
			throw new PrintServiceException("Unable to load the property file ",e1);
		}	
	}//end of method

	/**
	 * This method is used to get the Latest status of printer based on printjob id
	 * @param exchange : Camel Exchange Object
	 * @return String : response of printer status in json string
	 * @throws PrintServiceException
	 */
	public String getLatestStatus(Exchange exchange) throws PrintServiceException{
		JSONArray jsonArrayPrintJobs;
		try {
			jsonArrayPrintJobs = new JSONArray(exchange.getIn().getBody(String.class));
			JSONArray jsonArrayOfPrintJob=jsonArrayPrintJobs.getJSONArray(0);
			logger.debug("printjob status data in jsonArray format : " + jsonArrayOfPrintJob.getJSONObject(jsonArrayOfPrintJob.length() - 1));
			JSONObject jsonOfPrintJobStatus = jsonArrayOfPrintJob.getJSONObject(jsonArrayOfPrintJob.length() - 1);
			JSONObject jsonObj=new JSONObject();
			jsonObj.put(PrintServicePrintNodeConstant.PRINTER_STATE_KEY, jsonOfPrintJobStatus.get(PrintServicePrintNodeConstant.PRINTER_STATE_KEY));
			exchange.getIn().setBody(jsonObj.toString());
			return jsonObj.toString();
		} catch (JSONException e) {
			throw new PrintServiceException("Unable to featch printer status",e);

		}
		
	}//end of method


}
