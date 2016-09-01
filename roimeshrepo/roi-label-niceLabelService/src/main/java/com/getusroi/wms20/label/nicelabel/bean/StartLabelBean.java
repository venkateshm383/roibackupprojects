package com.getusroi.wms20.label.nicelabel.bean;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.label.nicelabel.NiceLabelPropertyConstant;
import com.getusroi.wms20.label.nicelabel.service.NiceLabelService;
import com.getusroi.wms20.label.nicelabel.service.NiceLabelServiceException;
/**
 * This class is used to get batchid by invoking start label
 * @author bizruntime
 *
 */
public class StartLabelBean extends AbstractROICamelJDBCBean {
	static Logger log = Logger.getLogger(StartLabelBean.class);

	/**
	 * This method is used to call nice label service to for start label and get batc id
	 * @param : Camel exchange
	 */
	@Override
	public void processBean(Exchange exchange) throws RoiNiceLabelServiceProcessBeanException {
		NiceLabelService labelService = new NiceLabelService();
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> genericdataMap = meshHeader.getGenricdata();

		// #TODO data has to come from exchange body instead of mesh Header
		JSONArray jsonArray = (JSONArray) genericdataMap.get(MeshHeaderConstant.DATA_KEY);

		int jsonLen = jsonArray.length();
		int printerid = 0;
		//checking for printerid and batchid fron json array
		for (int i = 0; i < jsonLen; i++) {
			JSONObject jobj;
			try {
				jobj = (JSONObject) jsonArray.get(i);
				Object obj = jobj.get(NiceLabelPropertyConstant.PRINTID_KEY);
				if (obj instanceof String) {
					String printerIdInString = (String) obj;
					printerid = Integer.parseInt(printerIdInString);
				} else {
					printerid = (Integer) jobj.get(NiceLabelPropertyConstant.PRINTID_KEY);
				}
			} catch (JSONException e) {
				throw new RoiNiceLabelServiceProcessBeanException("Error in starting the label to get batch id for printer id : " + printerid);
			}
		}
		String batchid = null;
		try {
			batchid = labelService.labelsStartLabel(printerid, null,meshHeader);
			log.debug("batchid : " + batchid);
			JSONObject jobj = new JSONObject();
			jobj.put(NiceLabelPropertyConstant.BATCHID_KEY, batchid);
			exchange.getIn().setBody("batch id : " + jobj.toString());
		} catch (JSONException | NiceLabelServiceException e1) {
			throw new RoiNiceLabelServiceProcessBeanException("unable to add batchid into the json object : "+batchid);

		}

		// return jobj.toString();

	}

}
