package com.getusroi.wms20.label.bean;

import java.util.Map;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.label.LabelPropertyConstant;
import com.getusroi.wms20.label.service.LabelService;
import com.getusroi.wms20.label.service.LabelServiceException;
import com.getusroi.wms20.label.service.vo.PrinterConfig;
/**
 * This class is used to  generate batch id
 * @author bizruntime
 *
 */
public class StartLabelBean {
	static Logger log = LoggerFactory.getLogger(StartLabelBean.class);

	/**
	 * This method is used to call label service to generate the batchid
	 * @param configname : config name in String
	 * @param exchange : Camel exchange Object
	 * @return  batchid in string format
	 * @throws RoiLabelServiceProcessBeanException
	 */
	public String callLabelStartLabel(String configname, Exchange exchange) throws RoiLabelServiceProcessBeanException{
		log.debug(".callLabelStartLabel of StartLabelBean");
		LabelService labelService = new LabelService();
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> genericdataMap = meshHeader.getGenricdata();
		
		//#TODO data has to come from exchange body instead of mesh Header
		JSONArray jsonArray = (JSONArray) genericdataMap.get(MeshHeaderConstant.DATA_KEY);
		
		log.debug("jsonArray : " + jsonArray);
		int jsonLen = jsonArray.length();
		log.debug("data's in json array for key data is : " + jsonLen);
		int printerid = 0;
		
		//checking to get printerid into any of the json array object
		for (int i = 0; i < jsonLen; i++) {
			JSONObject jobj;
			try {
				jobj = (JSONObject) jsonArray.get(i);
				Object obj = jobj.get(LabelPropertyConstant.PRINTID_KEY);
				if (obj instanceof String) {
					String printerIdInString = (String) obj;
					printerid = Integer.parseInt(printerIdInString);
				} else {
					printerid = (Integer) jobj.get(LabelPropertyConstant.PRINTID_KEY);
				}
			} catch (JSONException e) {
				throw new RoiLabelServiceProcessBeanException("Error in starting the label to get batch id for printer id : "+printerid);
			}
			
		}
		Map<String, Object> permaDataMap = meshHeader.getPermadata();
		log.debug("perma data : " + permaDataMap);
		Map<Integer, PrinterConfig> printerMap = (Map<Integer, PrinterConfig>) permaDataMap.get(configname);
		log.debug("printerMap : " + printerMap);
		PrinterConfig printerConfig = printerMap.get(printerid);
		log.debug("printer id : " + printerConfig.getPrinterID() + ", output : " + printerConfig.getOutputType());
		String batchid=null;
		JSONObject jobj = new JSONObject();
		try {
			batchid = labelService.labelsStartLabel(printerConfig.getPrinterID(), printerConfig.getOutputType(),meshHeader);
			
			jobj.put(LabelPropertyConstant.BATCHID_KEY, batchid);
		} catch (LabelServiceException | JSONException e1) {
			throw new RoiLabelServiceProcessBeanException("unable to add batchid into the json object : "+batchid);

		}		
		exchange.getIn().setBody("batch id : " + jobj.toString());
		return jobj.toString();
	}//end of method

}
