package com.getusroi.wms20.label.bartender.bean;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.config.RequestContext;
import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.label.bartender.BartenderPropertyConstants;
import com.getusroi.wms20.label.bartender.service.BartenderService;
import com.getusroi.wms20.label.bartender.service.BartenderServiceException;


/**
 * This class is used to produce the label for batchid
 * @author bizruntime
 *
 */
public class ProduceLabelBean extends AbstractROICamelJDBCBean {
	static Logger log = Logger.getLogger(ProduceLabelBean.class);

	/**
	 * This method is used to call produce nice label service to generate label
	 * @param exchange : Camel Exchange Object
	 * @return label : response from produce label service
	 * @throws RoiBartenderServiceProcessBeanException 
	 * 
	 */
	@Override
	protected void processBean(Exchange exchange) throws RoiBartenderServiceProcessBeanException {
		JSONArray jsonArray=null;
		String batchid = null;
		int printerId = 0;
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		RequestContext requestContext=meshHeader.getRequestContext();
		log.info("requestContext data "+requestContext);
		String bodyStringData = exchange.getIn().getBody(String.class);

		try {
			JSONObject bodyData = new JSONObject(bodyStringData);
			log.debug("Body data converted JsonObjet data = " + bodyData);
			jsonArray = bodyData.getJSONArray(MeshHeaderConstant.DATA_KEY);
		} catch (JSONException e1) {
			throw new RoiBartenderServiceProcessBeanException(
					"Error in adding the label ", e1);

		}
		log.debug("jsonArray : " + jsonArray);
		int jsonLen = jsonArray.length();
		log.debug("data size in json array for key data is : " + jsonLen);
		
		//checking in json array to get the printerid and batchid
		for (int i = 0; i < jsonLen; i++) {
			try {
				JSONObject jobj = (JSONObject) jsonArray.get(i);
				batchid = (String) jobj.get(BartenderPropertyConstants.BATCHID_KEY);
				Object obj = jobj.get(BartenderPropertyConstants.PRINTID_KEY);
				if (obj instanceof String) {
					String printerIdInString = (String) obj;
					printerId = Integer.parseInt(printerIdInString);
				} else {
					printerId = (Integer) jobj.get(BartenderPropertyConstants.PRINTID_KEY);
				}
				BartenderService bartenderService = new BartenderService();				
				byte[] data=null;
				try {
					//logic to produce label get response as pdf , write pdf to file and get its content and store in exchange body
					data =bartenderService.produceLabel(batchid, printerId, false, false,requestContext);
					JSONObject jsonresponse=new JSONObject();
					jsonresponse.put("response", data);		
					log.debug("produce Label Response Data "+jsonresponse.toString());
					exchange.getIn().setBody(jsonresponse.toString());
				} catch (BartenderServiceException e1) {
					throw new RoiBartenderServiceProcessBeanException("Unable to process produce label of barTender ",e1);
				}
	
			} catch (JSONException e) {				
				throw new RoiBartenderServiceProcessBeanException("unable to process produce label for batchid : "+batchid+", printerid : "+printerId);
			}
		}//end of for  loop
		

	}//end of method

	

}
