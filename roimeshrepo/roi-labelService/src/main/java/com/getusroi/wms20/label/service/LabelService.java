package com.getusroi.wms20.label.service;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.config.RequestContext;
import com.getusroi.dynastore.DynaStoreFactory;
import com.getusroi.dynastore.DynaStoreSession;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.permastore.config.PermaStoreConfigRequestException;
import com.getusroi.permastore.config.impl.PermaStoreConfigurationService;
import com.getusroi.wms20.label.LabelPropertyConstant;
import com.getusroi.wms20.label.service.vo.LabelTemplate;
import com.getusroi.wms20.label.service.vo.PrintLabel;
import com.getusroi.wms20.label.service.vo.PrintLabelBatch;
import com.getusroi.wms20.label.service.vo.PrinterConfig;
import com.getusroi.wms20.label.template.LabelTemplateReplacement;
//import com.sun.org.apache.xml.internal.serialize.Printer;

/**
 * This class is a service class for labels operation
 * 
 * @author bizruntime
 * 
 */
public class LabelService {

	static Logger log = LoggerFactory.getLogger(LabelService.class);
	/** The LabelId should be sequencial always*/
	final static AtomicInteger LABEL_SEQ = new AtomicInteger();
	/** BatchId */
	final static AtomicInteger BATCH_SEQ = new AtomicInteger();


	public LabelService() {
		
	}

	/**
	 * This method is for start label service
	 *  This method is to generated label batch id.
	 *  @param printerID : Printerid
	 * @param outputType : printer output type
	 * @return batchid
	 * @throws LabelServiceException 
	 */
	public String labelsStartLabel(int printerID, String outputType,MeshHeader meshHeader) throws LabelServiceException {
		log.debug(" LabelService :inside startlabel() of LabelService ");
		String batchid = null;
		RequestContext reqCxt=meshHeader.getRequestContext();
		try {
			batchid="BATCH_"+getBatchId();
			PrintLabelBatch printLabelBatch = new PrintLabelBatch();
			printLabelBatch.setOutputType(outputType);
			printLabelBatch.setPrinterId(printerID);
			printLabelBatch.setBatchid(batchid);
			
			DynaStoreSession cacheStrore = DynaStoreFactory
					.getDynaStoreSession(reqCxt,batchid);
			
			cacheStrore.addSessionData(batchid+"", printLabelBatch);			
			Map map=cacheStrore.getAllSessionData();
			
			//this to store data into dynastore log
			DynaStoreSession cacheStrore1 = DynaStoreFactory
					.getDynaStoreSession(reqCxt,batchid,map);
			log.debug("LabelDAO getAllSessionData labelsStartLabel " +map.toString() );
		} catch (Exception e) {
			throw new LabelServiceException("Error in start Label Service ",e);
		}
		return batchid;
	}// end of startLabel method

	/**
	 * This method is for add label service
	 *  copies if blank than default value =1
	 * @param batchid
	 *            : int
	 * @param copies
	 *            : int
	 * @param formatType
	 *            : String
	 * @param labelattribute
	 *            : String
	 *
	 * @throws LabelServiceException 
	 */
	public String addLabel(String batchid, int copies, String templateID,
			JSONArray labelattributes,MeshHeader meshHeader) throws LabelServiceException {
		log.debug(" LabelService : inside addlabel of  LabelService");
		String response=null;
		RequestContext reqCxt=meshHeader.getRequestContext();
		DynaStoreSession cacheStrore = DynaStoreFactory
				.getDynaStoreSession(reqCxt, batchid);
		
		PrintLabelBatch printLabelBatch = (PrintLabelBatch) cacheStrore
				.getSessionData(batchid);
		int labelId = 0; // need to generate this through some utility change						
		if(printLabelBatch==null){
			throw new LabelServiceException("Error in adding label as PrintLabelBatch is null for batch id : "+batchid+" in dynastore");
		}
		for (int i = 0; i < labelattributes.length(); i++) {
			JSONObject labelValue;
			try {
				labelValue = labelattributes.getJSONObject(i);
				labelId=PrintLabelBatch.getSquenceId();				
				PrintLabel printLabel = new PrintLabel();
				printLabel.setCopies(copies);
				printLabel.setTemplateID(templateID);
				//#TODO get datatype from templateID or some config
				printLabel.setInternalDataType("JSON");				
				printLabel.setInternalId(labelId);
				printLabel.setGroupSequence(printLabelBatch.getPrinLabels().size() + 1);
				printLabel.setLabeldata(labelValue.toString());
				printLabelBatch.addPrintLabel(labelId + "", printLabel);
			} catch (JSONException e) {
				throw new LabelServiceException("Error in addlabel Label Service ",e);

			}
			
		}
		// printLabelBatch
		cacheStrore.addSessionData(batchid, printLabelBatch);
		JSONObject jobj=new JSONObject();
		 response="Template with id : "+templateID+" added for batchid : "+batchid;
		try {
			jobj.put("response",response);
		} catch (JSONException e) {
			throw new LabelServiceException("Error in sending addlabel Label Service response ",e);
		} 	
	 	return jobj.toString();
		

	}// end of addLabel method

	/**
	 * This method is to call producelabel service
	 * 
	 * @param batchid
	 *            : int
	 * @param FormatType
	 *            : String
	 * @param printerId
	 *            : int
	 * @param removeTempDataFlag
	 *            : boolean
	 * @param removePrintData
	 *            : boolean
	 * @return
	 * 
	 * @throws LabelServiceException 
	 */
	public String produceLabel(String batchid, int printerId, boolean removeTempDataFlag, boolean removePrintData,MeshHeader meshHeader) throws LabelServiceException {
		log.debug(" . producelabel() of LabelService ");		
		StringBuffer allLabelSubsituted = new StringBuffer();
		LabelTemplateReplacement labelTemplateReplace=new LabelTemplateReplacement();
		RequestContext reqCxt=meshHeader.getRequestContext();

		try {
			Map<String, PrintLabel> labelMap =getLabeldata(batchid,reqCxt);			
			Set<String> labelkey=labelMap.keySet();
			Iterator<String> keyiterator=labelkey.iterator();			
			while(keyiterator.hasNext()){
				String labelID =(String)keyiterator.next();				
				PrintLabel printLabel=(PrintLabel)labelMap.get(labelID);
				log.debug("print label : "+printLabel);
				String templateID= printLabel.getTemplateID();
				LabelTemplate labelTemplate=getTemplate(templateID,reqCxt);
				log.debug("label template in produle label of Label service: "+labelTemplate);
				String labelsutitutedvalue= labelTemplateReplace.LabelTemplateValueSubstitution(printLabel, labelTemplate);
				allLabelSubsituted.append(labelsutitutedvalue);
				log.debug(" LabelService : inside while of producelabel() of LabelService labelsutitutedvale"+labelsutitutedvalue);
			}				
			DynaStoreSession cacheStrore = DynaStoreFactory
					.getDynaStoreSession(reqCxt, batchid);
			
			PrintLabelBatch printLabelBatch = (PrintLabelBatch) cacheStrore
					.getSessionData(batchid);			
			if(printLabelBatch!=null){
				printLabelBatch.setCompleteSubstitutedLabel(allLabelSubsituted.toString());
				printLabelBatch.setLabelReady(true);
				cacheStrore.addSessionData(batchid, printLabelBatch);
			}else{			
				throw new LabelServiceException("PrintLabelBatch doesnot exist into dynastore for batchid : "+batchid);
			}
			log.debug(" LabelService :  producelabel() allLabelSubsituted "+ allLabelSubsituted.toString());
		} catch (Exception e) {
			throw new LabelServiceException("Error in label data from the dynastore : ",e);

		}		
		JSONObject jobj=new JSONObject();
		try {
			jobj.put("response", allLabelSubsituted.toString());
		} catch (JSONException e) {
			throw new LabelServiceException("Error in sending producelabel Label Service response ",e);
		}	 	
	 	return jobj.toString();
		
	}// end of produceLabel method
	
	/**
	 * This method is used to close the label batch
	 * @param batchid : batch id in string
	 * @param action : what action need to perform
	 * @return claose label response
	 * @throws LabelServiceException
	 */
	public String labelCloseBatch(String batchid, String action,MeshHeader meshHeader) throws LabelServiceException{
		log.debug(" LabelService : inside labelCloseBatch()   ");
		RequestContext reqCxt=meshHeader.getRequestContext();
		DynaStoreSession cacheStrore = DynaStoreFactory
				.getDynaStoreSession(reqCxt, batchid);		
		PrintLabelBatch printLabelBatch = (PrintLabelBatch) cacheStrore
				.getSessionData(batchid);
		log.debug("substitued data : "+printLabelBatch.getCompleteSubstitutedLabel());
		cacheStrore.addSessionData(batchid, printLabelBatch.getCompleteSubstitutedLabel());
		JSONObject jobj=new JSONObject();
		try {
			jobj.put("response","close label is done");
		} catch (JSONException e) {
			throw new LabelServiceException("Error in sending closelabel Label Service response ",e);

		}	 	
	 	return jobj.toString();
	}// end of labelCloseBatch method

	/**
	 * This method is used to get Label data 
	 * @param labelMap : Map Object 
	 * @param labelID : label Id
	 * @return PrintLabel Object
	 */
	public PrintLabel getLabelData(Map<String, PrintLabel> labelMap, String labelID) {
		log.debug(" . getLabelBatchData()   labelMap "+labelMap.toString());
		PrintLabel batchLabel=labelMap.get(labelID);
		return batchLabel;
	}

	/**
	 * This method is to get Label Id
	 * @return int : label id
	 */
	public  int getlabelId() {
		log.debug(" .getlabelId() of LabelService  ");
		int seq = LABEL_SEQ.incrementAndGet();
		log.debug("squencenumber  >> " + seq);
		log.debug(" LabelService : inside getlabelId() seq  " +seq);
		return seq;

	}
	
	/**
	 * This method is used to geth the batchid
	 * @return int  batchid
	 */
	public  int getBatchId() {
		log.debug(".getBatchId() of LabelService  ");
		int seq = BATCH_SEQ.incrementAndGet();
		log.debug("squencenumber  >> " + seq);
		log.debug(" LabelService : inside getBatchId() seq  " +seq);
		return seq;
	}

	/**
	 * This method is used to get the labeData
	 * @param batchID : batchid
	 * @return Map<String,PrintLabel>
	 */
	public   Map<String,PrintLabel> getLabeldata(String batchID,RequestContext reqCxt) {
		log.debug(".getLabeldata of LabelService");
		
		DynaStoreSession cacheStrore = DynaStoreFactory
				.getDynaStoreSession(reqCxt,batchID);
		
		PrintLabelBatch printLabelBatch = (PrintLabelBatch) cacheStrore
				.getSessionData(batchID);
		log.debug("LabelDAO getAllSessionData getLabeldata " +printLabelBatch.getPrinLabels().toString() );
		return printLabelBatch.getPrinLabels();
	}
	
	/**
	 * This method is used to printerconfig store in permastore
	 * @param printerID : PrinterId
	 * @return PrinterConfig Object
	 * @throws PermaStoreConfigRequestException
	 */
	public PrinterConfig getPrinterConfig(int printerID) throws PermaStoreConfigRequestException{
		log.debug(" LabelService : inside getPrinterConfig()   ");
		PermaStoreConfigurationService permaConfigService=new PermaStoreConfigurationService();
		Object cacheObj=permaConfigService.getPermaStoreCachedObject(null,LabelPropertyConstant.PRINTERCONFIG_KEY);
		Map<String,PrinterConfig> labelTemplateMap=null;
		if(cacheObj instanceof Map){
			 labelTemplateMap=(Map<String,PrinterConfig>)cacheObj;
		}
		PrinterConfig printerConfig=labelTemplateMap.get(printerID);
		log.debug(" LabelService : inside getPrinterConfig() of printerConfig  "+printerConfig.toString());
		return printerConfig;		
	}
	
	/**
	 * This method is to get the template store in permastore
	 * @param templateID : template id
	 * @return LabelTemplate Object
	 * @throws PermaStoreConfigRequestException
	 */
	public  LabelTemplate getTemplate(String templateID,RequestContext requestContext) throws PermaStoreConfigRequestException {
		log.debug(" .getTemplate() of label Service  ");
		PermaStoreConfigurationService permaConfigService=new PermaStoreConfigurationService();
		
		//RequestContext requestContext=new RequestContext(reqCxt,"label","labelservice");
		
		Object cacheObj=permaConfigService.getPermaStoreCachedObject(requestContext, LabelPropertyConstant.LABELTEMPLATE_KEY);
		Map<String,LabelTemplate> labelTemplateMap=null;
		if(cacheObj instanceof Map){
			 labelTemplateMap=(Map<String,LabelTemplate>)cacheObj;
		}
		LabelTemplate labelTemplate=labelTemplateMap.get(templateID);
		return labelTemplate;
	}

}