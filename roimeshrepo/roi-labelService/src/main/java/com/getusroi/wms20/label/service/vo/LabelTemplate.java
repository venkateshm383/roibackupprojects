package com.getusroi.wms20.label.service.vo;

import java.io.Serializable;
import java.util.Arrays;

public class LabelTemplate implements Serializable{
	private  String templateID;
	private  String[][] replacementExpersion;
	private  String templateName;
	private  String templateValue;
	private  String formatType;
	
	public String getTemplateID() {
		return templateID;
	}
	public void setTemplateID(String templateID) {
		this.templateID = templateID;
	}
	public String[][] getReplacementExpersion() {
		return replacementExpersion;
	}
	public void setReplacementExpersion(String[][] replacementExpersion) {
		this.replacementExpersion = replacementExpersion;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getTemplateValue() {
		return templateValue;
	}
	public void setTemplateValue(String templateValue) {
		this.templateValue = templateValue;
	}
	public String getFormatType() {
		return formatType;
	}
	public void setFormatType(String formatType) {
		this.formatType = formatType;
	}
	@Override
	public String toString() {
		return "LabelTemplate [templateID=" + templateID + ", replacementExpersion=" + Arrays.toString(replacementExpersion) + ", templateName="
				+ templateName + ", templateValue=" + templateValue + ", formatType=" + formatType + "]";
	}
	
	
	

}
