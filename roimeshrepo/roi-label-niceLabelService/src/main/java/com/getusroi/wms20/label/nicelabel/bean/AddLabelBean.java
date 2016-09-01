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
 * This class is used to add label to dynastore
 * @author bizruntime
 *
 */
public class AddLabelBean extends AbstractROICamelJDBCBean {
	static Logger log = Logger.getLogger(AddLabelBean.class);
	
	@Override
	protected void processBean(Exchange exchange) throws RoiNiceLabelServiceProcessBeanException{
		log.debug(".processBean method  of AddLabelBean");
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> genericdataMap = meshHeader.getGenricdata();
		
		//#TODO data has to come from exchange body instead of mesh Header
		JSONArray jsonArray = (JSONArray) genericdataMap.get(MeshHeaderConstant.DATA_KEY);
		
		log.debug("jsonArray : " + jsonArray);
		int jsonLen = jsonArray.length();
		log.debug("data's in json array for key data is : " + jsonLen);
		String batchid = null;
		String template = null;
		for (int i = 0; i < jsonLen; i++) {
			JSONObject jobj = null;
			try {
				jobj = (JSONObject) jsonArray.get(i);
				batchid = (String) jobj.get(NiceLabelPropertyConstant.BATCHID_KEY);
				template = (String) jobj.get(NiceLabelPropertyConstant.TEMAPLTE_KEY);
				JSONArray jsonarry = jobj.getJSONArray(NiceLabelPropertyConstant.LABEL_KEY);
				NiceLabelService nicelabelService = new NiceLabelService();
				String response = nicelabelService.addLabel(batchid, 1, template, jsonarry,meshHeader);
				exchange.getIn().setBody(response);
			} catch (JSONException | NiceLabelServiceException e) {
				throw new RoiNiceLabelServiceProcessBeanException("Error in adding the label for the batch id :"+batchid+" , template : "+template,e);
			}

		}
		/*
		 * This line is only to test db with XA tansaction String sql=
		 * "insert into policy values(343,'dfdf','fgfffg')"; jdbc.execute(sql);
		 * log.debug("policy with value (343,'dfdf','fgfffg') inserted");
		 */
	}
}
