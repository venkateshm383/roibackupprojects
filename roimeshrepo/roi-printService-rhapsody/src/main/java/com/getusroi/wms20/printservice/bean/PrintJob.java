package com.getusroi.wms20.printservice.bean;

import java.io.IOException;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;
import javax.xml.ws.soap.MTOMFeature;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.config.RequestContext;
import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.printservice.RhapsodyPrintServiceConstant;
import com.getusroi.wms20.printservice.exception.CustomRhapsodyException;
import com.getusroi.wms20.printservice.rhapsodyservice.RhapsodyService;

/**
 * 
 * @author bizruntime
 *
 */
public class PrintJob extends AbstractROICamelJDBCBean {

	Logger logger = LoggerFactory.getLogger(PrintJob.class);

	/**
	 *  processBean, createPrintJob, takes the exchange body to get the data-keys, like batchId and printerId
	 * 
	 * @param Exchange
	 *            Object
	 * @throws JSONException 
	 * @throws IOException 
	 *
	 */	
	@Override
	protected void processBean(Exchange exchange) throws CustomRhapsodyException{
		logger.debug(".processBean()... CreatePrintJob");
		String dataBody = exchange.getIn().getBody(String.class);
		RhapsodyService service = new RhapsodyService();
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		RequestContext requestContext = meshHeader.getRequestContext();
		String printerId = null, batchId = null;
		try {
			JSONObject object = new JSONObject(dataBody);
			String defaultPrinterId = service.getRhapsodyConnectionProperties().get(RhapsodyPrintServiceConstant.DEFAULT_PRINTER);
			JSONArray jsonarr = (JSONArray) object.get(RhapsodyPrintServiceConstant.DATA_KEY);
			//#TODO to ensure the first index of the json is selected, later have to add for-iteration if multiple values are present in json array
			JSONObject jobj1 = (JSONObject) jsonarr.get(0);
			printerId = jobj1.getString(RhapsodyPrintServiceConstant.PRINTER_ID);
			if (printerId.equals("") || printerId.isEmpty() || printerId==null)
			{printerId = defaultPrinterId;}
			
			batchId = jobj1.getString(RhapsodyPrintServiceConstant.BATCH_ID);
			try {
				String soapResponseinJson = soapBuildHelper(requestContext, exchange, batchId, printerId);
				exchange.getIn().setBody(soapResponseinJson);
			} catch (IOException | SOAPException | TransformerException | JSONException e) {
				throw new CustomRhapsodyException("Error sending soap request/Getting soap response: " + e);
			}
		} catch (JSONException | IOException e1) {
			throw new CustomRhapsodyException("Error parsing json from body" + e1);
		}
		

	}//end of method

	/**
	 *  soapBuildHelper, triggers the soap request to the RhapsodyServer
	 * @note As large files, or attachments, are not attached with SoapRequest,
	 *       explicit SoapClient bean has been defined
	 * @param MeshHeader,
	 *            Exchange, Objects, BatchID, PrinterId in string
	 * @return String format of Json converted soapResponse
	 */
	private String soapBuildHelper(RequestContext requestContext, Exchange exchange, String batchId, String printerId)
			throws IOException, SOAPException, TransformerException, JSONException {
		logger.debug(".soapBuildHelper()... CreatePrintJob");
		RhapsodyService service = new RhapsodyService();
		SOAPConnectionFactory soapConnectionFactory;
		SOAPMessage soapMessage = service.generateSoapRequest(requestContext, exchange, batchId, printerId);
		soapConnectionFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection soapConnection = soapConnectionFactory.createConnection();
		new MTOMFeature(true);
		SOAPMessage soapResponse = soapConnection.call(soapMessage,
				service.getRhapsodyConnectionProperties().get(RhapsodyPrintServiceConstant.WSDL_URL));
		String responseString = service.getSoapResponse(soapResponse);
		JSONObject jsonObject = XML.toJSONObject(responseString);

		return jsonObject.toString();
	}//end of method

}
