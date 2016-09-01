package com.getusroi.wms20.label.bean;

import java.util.Map;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.label.LabelPropertyConstant;
import com.getusroi.wms20.label.service.LabelService;
import com.getusroi.wms20.label.service.LabelServiceException;

/**
 * This class is to call printservice by sending batchid to generate printjob
 * 
 * Note : this function only to showcase how other feature can be called using eventing.Actual Implementation is different
 * @author bizruntime
 *
 */
public class CloseLabelBean extends AbstractROICamelJDBCBean {
	protected static final Logger logger = LoggerFactory.getLogger(CloseLabelBean.class);

	/**
	 * This method is used to call label service to call another feature for further process
	 * @param exch : Camel exchange Object
	 */
	@Override
	protected void processBean(Exchange exch) throws RoiLabelServiceProcessBeanException {
		logger.debug(".processBean() closeLabelBean");
		LabelService labelService = new LabelService();
		MeshHeader meshHeader = (MeshHeader) exch.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> genericdataMap = meshHeader.getGenricdata();
		
		//#TODO data has to come from exchange body instead of mesh Header
		JSONArray jsonArray = (JSONArray) genericdataMap.get(MeshHeaderConstant.DATA_KEY);
		
		int jsonLen = jsonArray.length();
		logger.debug("data's in json array for key data is : " + jsonLen);
		String batchid = null;
		//checking json array to get the batchid
		for (int i = 0; i < jsonLen; i++) {
			JSONObject jobj = null;
			try {
				jobj = (JSONObject) jsonArray.get(i);
				batchid = (String) jobj.get(LabelPropertyConstant.BATCHID_KEY);
				String response=labelService.labelCloseBatch(batchid, "close",meshHeader);
				exch.getIn().setBody(response);
			} catch (JSONException | LabelServiceException e) {
				throw new RoiLabelServiceProcessBeanException("Error in processing close label for batch id : "+batchid,e);
			}

		}//end of for loop
	}//end of method

}
