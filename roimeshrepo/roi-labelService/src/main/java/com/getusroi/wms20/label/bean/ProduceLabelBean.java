package com.getusroi.wms20.label.bean;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.mesh.util.MeshConfigurationUtil;
import com.getusroi.policy.config.PolicyConfigurationException;
import com.getusroi.policy.config.PolicyRequestContext;
import com.getusroi.policy.config.PolicyRequestException;
import com.getusroi.policy.config.impl.PolicyConfigurationService;
import com.getusroi.policy.config.impl.PolicyInvalidRegexExpception;
import com.getusroi.wms20.label.LabelPropertyConstant;
import com.getusroi.wms20.label.service.LabelService;
import com.getusroi.wms20.label.service.LabelServiceException;
import com.getusroi.wms20.label.service.vo.PrinterConfig;
/**
 * This class is used to produce the label for batchid
 * @author bizruntime
 *
 */
public class ProduceLabelBean {
	static Logger log = LoggerFactory.getLogger(ProduceLabelBean.class);

	/**
	 * This method is used to call produce label service to generate label
	 * @param configName : configname in string format
	 * @param exchange : Camel Exchange Object
	 * @return label : response from produce label service
	 * @throws RoiLabelServiceProcessBeanException
	 */
	public String callProduceLable(String configName, Exchange exchange) throws RoiLabelServiceProcessBeanException {
		log.debug(".labelProduceLabel() of ProduceLabelBean");
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> genericdataMap = meshHeader.getGenricdata();
		
		//#TODO data has to come from exchange body instead of mesh Header
		JSONArray jsonArray = (JSONArray) genericdataMap.get(MeshHeaderConstant.DATA_KEY);
		
		log.debug("jsonArray : " + jsonArray);
		int jsonLen = jsonArray.length();
		log.debug("data's in json array for key data is : " + jsonLen);
		String batchid = null;
		int printerId = 0;
		
		//checking to get batchid and printerid from json array
		for (int i = 0; i < jsonLen; i++) {
			JSONObject jobj;
			try {
				jobj = (JSONObject) jsonArray.get(i);
				batchid = (String) jobj.get(LabelPropertyConstant.BATCHID_KEY);
				Object obj=jobj.get(LabelPropertyConstant.PRINTID_KEY);
				if(obj instanceof String){
					String printerIdInString=(String)obj;
					 printerId=Integer.parseInt(printerIdInString);
				}else{
				 printerId=(Integer)jobj.get(LabelPropertyConstant.PRINTID_KEY);
				}
			} catch (JSONException e) {
				throw new RoiLabelServiceProcessBeanException("unable to process produce label for batchid : "+batchid+", printerid : "+printerId);
			}			
		}//end of for loop
		MeshConfigurationUtil meshconfigUtil = new MeshConfigurationUtil();
		String producedLabel=null;
		try {
			meshconfigUtil.getPolicyConfiguration(configName, exchange);
			LabelService labelService = new LabelService();
			PolicyRequestContext requestContext = new PolicyRequestContext(meshHeader.getTenant(), meshHeader.getSite(), meshHeader.getFeatureGroup(),
					meshHeader.getFeatureName(),meshHeader.getVendor(),meshHeader.getVersion());
			requestContext.setRequestVariable(meshHeader.getPolicydata());
			PolicyConfigurationService policyConfigService = new PolicyConfigurationService();
			HashMap permaStoreName=permaStoreName = (HashMap) policyConfigService.getPolicyResponseData(requestContext, configName);
			PrinterConfig printerconfig = (PrinterConfig) permaStoreName.get(printerId);
			 producedLabel=labelService.produceLabel(batchid, printerconfig.getPrinterID(), false, false,meshHeader);
			log.debug("produce label : " + producedLabel);
		} catch (PolicyConfigurationException | PolicyRequestException | PolicyInvalidRegexExpception | LabelServiceException e) {
			throw new RoiLabelServiceProcessBeanException("Error in processing produce label for batchid : "+batchid+", printerid : "+printerId,e);
		}
		return producedLabel;
	}//end of method

}
