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

public class StartLabelBean extends AbstractROICamelJDBCBean {
	static Logger log = Logger.getLogger(StartLabelBean.class);

	public void processBean(Exchange exchange)
			throws RoiBartenderServiceProcessBeanException {
		JSONArray jsonArray = null;
		String printerid = null;

		BartenderService bartenderService = new BartenderService();
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(
				MeshHeaderConstant.MESH_HEADER_KEY);
		RequestContext requestContext = meshHeader.getRequestContext();
		log.debug("Request context object  " + requestContext);
		String bodyStringData = exchange.getIn().getBody(String.class);

		try {
			JSONObject bodyData = new JSONObject(bodyStringData);
			log.debug("Body data converted JsonObjet data = " + bodyData);
			jsonArray = bodyData.getJSONArray(MeshHeaderConstant.DATA_KEY);
		} catch (JSONException e1) {
			throw new RoiBartenderServiceProcessBeanException(
					"Error in adding the label ", e1);

		}
		int jsonLen = jsonArray.length();
		// checking for printerid and batchid fron json array
		for (int i = 0; i < jsonLen; i++) {
			JSONObject jobj;
			try {
				jobj = (JSONObject) jsonArray.get(i);
				Object obj = jobj.get(BartenderPropertyConstants.PRINTID_KEY);
				if (obj instanceof String) {
					printerid = (String) obj;
					 
				} else {
					int id = (Integer) jobj
					
							.get(BartenderPropertyConstants.PRINTID_KEY);
					printerid=id+"";
				}
			} catch (JSONException e) {
				throw new RoiBartenderServiceProcessBeanException(
						"Error in starting the label to get batch id for printer id : "
								+ printerid);
			}
		}// end of for loop
		String batchid = null;
		try {
			batchid = bartenderService.labelsStartLabel(printerid, null,
					requestContext);
			log.debug("batchid : " + batchid);
			JSONObject jobj = new JSONObject();
			jobj.put(BartenderPropertyConstants.BATCHID_KEY, batchid);
			exchange.getIn().setBody(jobj.toString());
		} catch (JSONException | BartenderServiceException e1) {
			throw new RoiBartenderServiceProcessBeanException(
					"unable to add batchid into the json object : " + batchid);

		}

	}

}
