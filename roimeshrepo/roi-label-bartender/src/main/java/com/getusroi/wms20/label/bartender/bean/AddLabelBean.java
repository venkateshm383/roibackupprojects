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

public class AddLabelBean extends AbstractROICamelJDBCBean {
	static Logger log = Logger.getLogger(AddLabelBean.class);

	@Override
	protected void processBean(Exchange exchange)
			throws RoiBartenderServiceProcessBeanException {
		log.debug(".processBean method  of AddLabelBean");
		String bodyStringData = exchange.getIn().getBody(String.class);
		log.debug("body data from Exchange " + bodyStringData);

		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(
				MeshHeaderConstant.MESH_HEADER_KEY);
		RequestContext requestContext = meshHeader.getRequestContext();
		log.debug("requestContext data " + requestContext);
		JSONArray jsonArray = null;

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
		String batchid = null;
		String template = null;
		for (int i = 0; i < jsonLen; i++) {
			JSONObject jobj = null;
			try {
				jobj = (JSONObject) jsonArray.get(i);
				batchid = (String) jobj
						.get(BartenderPropertyConstants.BATCHID_KEY);
				template = (String) jobj
						.get(BartenderPropertyConstants.TEMAPLTE_KEY);
				JSONArray jsonarry = jobj
						.getJSONArray(BartenderPropertyConstants.LABEL_KEY);
				BartenderService bartenderService = new BartenderService();
				String response = bartenderService.addLabel(batchid, 1,
						template, jsonarry, requestContext);
				exchange.getIn().setBody(response);
			} catch (JSONException | BartenderServiceException e) {
				throw new RoiBartenderServiceProcessBeanException(
						"Error in adding the label for the batch id :"
								+ batchid + " , template : " + template, e);
			}

		}

	}
}
