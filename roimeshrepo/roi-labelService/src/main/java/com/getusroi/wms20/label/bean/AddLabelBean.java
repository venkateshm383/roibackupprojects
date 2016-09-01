package com.getusroi.wms20.label.bean;

import java.util.Map;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.label.LabelAttributeLoadingException;
import com.getusroi.wms20.label.LabelPropertyConstant;
import com.getusroi.wms20.label.LoadingLabelAttribute;
import com.getusroi.wms20.label.service.LabelService;

/**
 * This class is used to add label in dynastore against batch id by generating label id
 * @author bizruntime
 *
 */
public class AddLabelBean extends AbstractROICamelJDBCBean {
	static Logger log = LoggerFactory.getLogger(AddLabelBean.class);

	
	/**
	 * This method is used to call label service to add label and its label id in dynastore
	 * @param exchange : Camel Exchange Object
	 */
	@Override
	protected void processBean(Exchange exchange) throws Exception{
		log.debug(".processBean method  of AddLabelBean");
		LoadingLabelAttribute labelAttribute = new LoadingLabelAttribute();
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
				batchid = (String) jobj.get(LabelPropertyConstant.BATCHID_KEY);
				template = (String) jobj.get(LabelPropertyConstant.TEMAPLTE_KEY);
				LabelService labelService = new LabelService();
				JSONArray jsonarry = labelAttribute.getJSONArrayLabel();
				String response = labelService.addLabel(batchid, 1, template, jsonarry,meshHeader);
				exchange.getIn().setBody(response);
			} catch (JSONException | LabelAttributeLoadingException e) {
				throw new RoiLabelServiceProcessBeanException("Error in adding the label for the batch id :"+batchid+" , template : "+template,e);
			}

		}
		/*
		 * This line is only to test db with XA tansaction String sql=
		 * "insert into policy values(343,'dfdf','fgfffg')"; jdbc.execute(sql);
		 * log.debug("policy with value (343,'dfdf','fgfffg') inserted");
		 */
	}

}
