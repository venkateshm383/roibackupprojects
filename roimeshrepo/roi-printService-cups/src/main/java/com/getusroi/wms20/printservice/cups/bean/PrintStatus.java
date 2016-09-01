package com.getusroi.wms20.printservice.cups.bean;

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

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.printservice.cups.IPrinterOperations;
import com.getusroi.wms20.printservice.cups.PrintServiceCupsConstant;
import com.getusroi.wms20.printservice.cups.Impl.CupsPrinterOperationException;
import com.getusroi.wms20.printservice.cups.Impl.PrinterOperationsImpl;

/**
 * This class is used to get the print job  status
 * @author bizruntime
 *
 */
public class PrintStatus extends AbstractROICamelJDBCBean {

	Logger logger = LoggerFactory.getLogger(PrintStatus.class);

	
	/**
	 * This method is used to get the print job status
	 * @param : Camel Exchange Object
	 */
	@Override
	protected void processBean(Exchange exchange) throws CupsPrintServiceBeanException {
		logger.debug(".checkPrintStatus() PrintStatus");
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		logger.debug("mesh header datat " + meshHeader);
		Map<String, Object> genericdata = meshHeader.getGenricdata();
		
		//#TODO this has to be taken from exchange body instead of MeshHeader
		JSONArray jsonarr = (JSONArray) genericdata.get(MeshHeaderConstant.DATA_KEY);
		
		int jsonArrLen = jsonarr.length();
		//iterating over json array to get printjob Id
		for (int i = 0; i < jsonArrLen; i++) {
			try {
				JSONObject jsonObj = jsonarr.getJSONObject(i);
				String printJobId = (String) jsonObj.get("printJobId");
				IPrinterOperations PrintOperations = new PrinterOperationsImpl();
				Map<String, Object> status=null;
				try {
					status = PrintOperations.getPrintJobState(Integer.parseInt(printJobId));
					JSONObject object = new JSONObject(status);
					exchange.getIn().setBody(object.toString());
				} catch (NumberFormatException | CupsPrinterOperationException e) {
					throw new CupsPrintServiceBeanException("Error in getting the print Job Status for print job Id : "+printJobId,e);
				}
				
			} catch (JSONException e) {
				throw new CupsPrintServiceBeanException("Unable to featch json Object at index : " + i, e);
			}
		}//end of for loop

	}//end of method

	/**
	 * This method is used to get restlet response and store in exchange body
	 * @param exchange : Camel Exchange Object
	 */
	public void restletResponse(Exchange exchange) {
		Response response = exchange.getIn().getHeader(RestletConstants.RESTLET_RESPONSE, Response.class);
		response.setStatus(Status.SERVER_ERROR_INTERNAL);
		exchange.getIn().setHeader(RestletConstants.RESTLET_RESPONSE, response);
		exchange.getIn().setBody("The printer not fount");
	}//end of method

}
