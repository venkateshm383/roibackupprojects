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
	 * @throws RoiNiceLabelServiceProcessBeanException 
	 * 
	 */
	@Override
	protected void processBean(Exchange exchange) throws RoiNiceLabelServiceProcessBeanException {
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> genericdataMap = meshHeader.getGenricdata();
		
		//#TODO data has to come from exchange body instead of mesh Header
		JSONArray jsonArray = (JSONArray) genericdataMap.get(MeshHeaderConstant.DATA_KEY);
		
		log.debug("jsonArray : " + jsonArray);
		int jsonLen = jsonArray.length();
		log.debug("data size in json array for key data is : " + jsonLen);
		String batchid = null;
		int printerId = 0;
		//checking in json array to get the printerid and batchid
		for (int i = 0; i < jsonLen; i++) {
			try {
				JSONObject jobj = (JSONObject) jsonArray.get(i);
				batchid = (String) jobj.get(NiceLabelPropertyConstant.BATCHID_KEY);
				Object obj = jobj.get(NiceLabelPropertyConstant.PRINTID_KEY);
				if (obj instanceof String) {
					log.debug(""+(String)obj);
					String printerIdInString = (String) obj;
					printerId = Integer.parseInt(printerIdInString);
					log.debug("inside if condition: "+printerId);

				} else {
					printerId = (Integer) jobj.get(NiceLabelPropertyConstant.PRINTID_KEY);
					log.debug("inside else condition "+printerId);
				}
				NiceLabelService nicelabelService = new NiceLabelService();				
				byte[] data=null;
				try {
					//logic to produce label get response as pdf , write pdf to file and get its content and store in exchange body
					nicelabelService.produceLabel(batchid, printerId, false, false,meshHeader);
					data=nicelabelService.readFromPdfFile(batchid, meshHeader);
					JSONObject jsonresponse=new JSONObject();
					jsonresponse.put("response", data.toString());		
					exchange.getIn().setBody(jsonresponse.toString());
				} catch (NiceLabelServiceException e1) {
					throw new RoiNiceLabelServiceProcessBeanException("Unable to process produce label of niceLabel ",e1);
				}
	
			} catch (JSONException e) {				
				throw new RoiNiceLabelServiceProcessBeanException("unable to process produce label for batchid : "+batchid+", printerid : "+printerId);
			}
		}
		

	}//end of method

	

}
