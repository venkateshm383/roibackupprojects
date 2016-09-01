package com.getusroi.wms20.label.builder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.permastore.config.IPermaStoreCustomCacheObjectBuilder;
import com.getusroi.permastore.config.jaxb.CustomBuilder;
import com.getusroi.wms20.label.LabelPropertyConstant;
import com.getusroi.wms20.label.service.LabelService;
import com.getusroi.wms20.label.service.vo.LabelTemplate;

/**
 * This class is used to load the template into the permastore
 * @author bizruntime
 *
 */
public class LabelTemplateBuilder implements IPermaStoreCustomCacheObjectBuilder {

	final static Logger logger = LoggerFactory.getLogger(LabelTemplateBuilder.class);

	private static Properties TEMPLATE_FORMAT_TYPE_PROP = new Properties();
	private static Properties TEMPLATE_LIST_PROP = new Properties();
	private static Properties TEMPLATE_RULE_PROP = new Properties();

	/**
	 * This method is used to load template data for permastore cache
	 * @param CustomBuilder : CustomBuilder config Object
	 * @return Serializable Object
	 */
	public Serializable loadDataForCache(CustomBuilder CustomBuilder) {
		logger.debug(".loadDataForCache of LabelTemplateBuilder ");
		LabelService labelService = new LabelService();
		Map<String, LabelTemplate> labelTemplateMap = null;
		try {
			labelTemplateMap = addtemplates(labelService);
		} catch (LabelFileLoadingException e) {
			logger.error("unable to add template into permastore", e);
		}
		return (Serializable) labelTemplateMap;
	}

	/**
	 * This method is used to create label template Object
	 * @param labelService : Label Service Object
	 * @return Map<String, LabelTemplate>
	 * @throws LabelFileLoadingException
	 */
	public Map<String, LabelTemplate> addtemplates(LabelService labelService) throws LabelFileLoadingException {
		logger.debug(".addtemplates of LabelTemplateBuilder ");
		loadTemplateValue();
		loadTemplateRule();
		Enumeration enum1 = TEMPLATE_LIST_PROP.keys();
		Map<String, LabelTemplate> labelTemplateMap = new HashMap<>();

		while (enum1.hasMoreElements()) {
			String templateid = (String) enum1.nextElement();
			String filepath = TEMPLATE_LIST_PROP.getProperty(templateid);
			logger.debug("file name : " + templateid + ", file path : " + filepath);
			String fileconetent = readTemplate(filepath);
			logger.debug("file content : " + fileconetent);
			String rule = TEMPLATE_RULE_PROP.getProperty(templateid);
			String[][] expression = { { rule }, { "" } };
			logger.debug("after -file --expression : " + expression[0][0]);
			LabelTemplate labelTemplate = new LabelTemplate();
			labelTemplate.setFormatType("JSON");
			labelTemplate.setReplacementExpersion(expression);
			labelTemplate.setTemplateID(templateid);
			labelTemplate.setTemplateName(templateid);
			labelTemplate.setTemplateValue(fileconetent);
			labelTemplateMap.put(templateid, labelTemplate);
		}
		return labelTemplateMap;
	}

	/**
	 * This method is used to read template from file path
	 * @param filepath : file path to get info about load template
	 * @return String
	 * @throws LabelFileLoadingException
	 */
	private String readTemplate(String filepath) throws LabelFileLoadingException {
		StringBuffer sb = new StringBuffer();
		FileReader fr = null;
		logger.debug("file path ---: "+filepath);
		try {
			fr = new FileReader(filepath);
		} catch (FileNotFoundException e1) {
			throw new LabelFileLoadingException("File doesnot exist at location : " + filepath, e1);
		}
		BufferedReader br = new BufferedReader(fr);
		String thisLine = null;
		try {
			// open input stream test.txt for reading purpose.
			while ((thisLine = br.readLine()) != null) {
				sb.append(thisLine + "\n");
			}
		} catch (Exception e) {
			throw new LabelFileLoadingException("Problem in appending file content in string builder", e);

		}
		return sb.toString();
	}

	/**
	 * This method is used to load the template value
	 * @throws LabelFileLoadingException
	 */
	private void loadTemplateValue() throws LabelFileLoadingException {
		// getting property file from classpath
		InputStream input = LabelTemplateBuilder.class.getClassLoader().getResourceAsStream(LabelPropertyConstant.TEMPLATE_LIST);
		// load the properties file
		try {
			TEMPLATE_LIST_PROP.load(input);
			logger.debug(" files name" + TEMPLATE_LIST_PROP.toString());
		} catch (IOException e) {
			throw new LabelFileLoadingException("Problem in loading the file : " + LabelPropertyConstant.TEMPLATE_LIST, e);
		}
	}

	/**
	 * This method is used to load template format
	 * @throws LabelFileLoadingException
	 */
	private void loadTemplateFormat() throws LabelFileLoadingException {
		logger.debug(".loadTemplateFormat of LabelTemplateBuilder ");
		InputStream input = LabelTemplateBuilder.class.getClassLoader().getResourceAsStream(LabelPropertyConstant.TEMPLATE_FORMAT_TYPE);
		try {
			TEMPLATE_FORMAT_TYPE_PROP.load(input);
			logger.debug(" files name : " + TEMPLATE_FORMAT_TYPE_PROP.toString());
		} catch (IOException e) {
			throw new LabelFileLoadingException("Problem in loading the file TEMPLATE_FORMAT_TYPE : " + LabelPropertyConstant.TEMPLATE_FORMAT_TYPE, e);
		}
	}

	/**
	 * This method is used to laod the template rule
	 * @throws LabelFileLoadingException
	 */
	private void loadTemplateRule() throws LabelFileLoadingException {
		logger.debug(".loadTemplateRule of LabelTemplateBuilder ");
		InputStream input = LabelTemplateBuilder.class.getClassLoader().getResourceAsStream(LabelPropertyConstant.TEMPLATE_RULE);
		// load the properties file
		try {
			TEMPLATE_RULE_PROP.load(input);
			logger.debug(" files name" + TEMPLATE_RULE_PROP.toString());
		} catch (IOException e) {
			throw new LabelFileLoadingException("Problem in loading the file TEMPLATE_RULE : " + LabelPropertyConstant.TEMPLATE_RULE, e);
		}

	}
	
	
}
