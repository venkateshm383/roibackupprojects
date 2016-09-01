package com.getusroi.wms20.label.service;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONException;

import com.getusroi.wms20.label.service.vo.LabelTemplate;
import com.getusroi.wms20.label.service.vo.PrintLabel;
import com.getusroi.wms20.label.template.LabelTemplateReplacement;

/**
 * This class is used to producel label
 * 
 * @author bizruntime
 * 
 */
public class ProduceLabelPrintFile {

	static Logger log = LoggerFactory.getLogger(ProduceLabelPrintFile.class);

	/**
	 * This method is to produce the label
	 * 
	 * @param batchid
	 *            : int
	 * @param formatType
	 *            : String
	 * @param printerId
	 *            : int
	 * @param removeTempDataFlag
	 *            : boolean
	 * @param removePrintData
	 *            : boolean
	 * @throws JSONException 
	 */
	public String  labelsProduceLabel(Map<String, PrintLabel> labelMap, Map <String, LabelTemplate> labelTemplate) throws JSONException {
		log.debug("inside labelsproducelabel method of ProduceLabelPrintFile ");
		StringBuffer substtitutedLabel = new StringBuffer();
		LabelTemplateReplacement labelTemplateReplace=new LabelTemplateReplacement();
		Collection<PrintLabel> valueSet=labelMap.values();
		for(PrintLabel label:valueSet){
			String templateID= label.getTemplateID();
			LabelTemplate labelTemplateObj=labelTemplate.get(templateID);
		String tempvalue= labelTemplateReplace.LabelTemplateValueSubstitution(label,  labelTemplateObj);
		substtitutedLabel.append(tempvalue);
		}
		return substtitutedLabel.toString();

	}// end of labelsProduceLabel method


}