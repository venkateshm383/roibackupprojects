package com.getusroi.wms20.label.template;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.getusroi.wms20.label.service.vo.LabelTemplate;
import com.getusroi.wms20.label.service.vo.PrintLabel;

/**
 * This class is to replace template data with actual data
 * 
 * @author bizruntime
 *
 */
public class LabelTemplateReplacement {
	static Logger log = LoggerFactory.getLogger(LabelTemplateReplacement.class);

	public String LabelTemplateValueSubstitution(PrintLabel printLabel, LabelTemplate labelTemplate) throws JSONException {
		log.debug("labelTemplate : " + labelTemplate);
		String labeldata = printLabel.getLabeldata();
		JSONObject jsonObject = null;
		String replaceData=null;
	
			jsonObject = new JSONObject(labeldata);
			log.debug("inside LabelTemplateValueSubstitution jsonObject  " + jsonObject.toString());
			ArrayList arrayList = new ArrayList();
			JSONObject sSONObject = new JSONObject(labeldata);
			Iterator jsonkey = sSONObject.keys();
			while (jsonkey.hasNext()) {
				String key = (String) jsonkey.next();
				String jsonvalue = sSONObject.getString(key);
				log.debug("inside LabelTemplateValueSubstitution jsonObject key " + key + " jsonvalue " + jsonvalue);
				arrayList.add(jsonvalue);
			}
			String templateData = labelTemplate.getTemplateValue();
			log.debug("inside LabelTemplateValueSubstitution templateData  " + templateData);
			String[][] replacementIdentfier = labelTemplate.getReplacementExpersion();
			String instruction = replacementIdentfier[0][0];
			log.debug("inside LabelTemplateValueSubstitution replacementIdentfier  " + instruction);
			Pattern pattern = Pattern.compile(instruction);
			 replaceData = replacePatternText(templateData, arrayList, pattern);
			log.debug("inside LabelTemplateValueSubstitution replacePatternText  " + replaceData);
		
		return replaceData;
	}

	/**
	 * This method is used to replace (?i)(~) from template
	 * @param template : template data
	 * @param sourceData : source data
	 * @param pattern : pattern to be searched for
	 * @return String : replace data
	 */
	public String replacePatternText(String template, ArrayList<String> sourceData, Pattern pattern) {
		log.debug(". replaceTextTILDA() of LabelTemplateReplacement");
		int endindex = 0;
		// match the generated pattern in source
		Matcher matcher = pattern.matcher(template);
		StringBuffer sb1 = new StringBuffer();
		// counter
		int count = 0;
		// iterate over all occurence of pattern in template
		while (matcher.find()) {
			endindex = matcher.end();
			log.debug("Start index : " + matcher.start() + ", end index : " + endindex + " data : " + matcher.group());
			// replace the matching pattern with data from arraylist and store
			// in string buffer
			matcher.appendReplacement(sb1, sourceData.get(count));
			count++;
		}
		String tailstring = template.substring(endindex, template.length());
		// append the tail string with template
		sb1 = sb1.append(tailstring);
		return sb1.toString();

	}

	/**
	 * This method is to replace Text FD FS from template
	 * 
	 * @param source
	 *            : template in String
	 * @param arr
	 *            : ArrayList of value
	 * @return String
	 */
	public String replaceTextFDFS(String source, ArrayList<String> arr) {
		log.debug("inside replaceTextFDFS() of LabelTemplateReplacemment  ");
		log.debug("source labeltemplate" + source);
		int endindex = 0;
		// generate pattern to match
		Pattern pattern = Pattern.compile("(?i)(FD~.*?)(.+?)(,)");
		StringBuffer sb1 = new StringBuffer();
		Matcher matcher = pattern.matcher(source);
		log.debug("matcher : " + matcher);
		int count = 0;
		// Iterating over all occurance of matcher
		while (matcher.find()) {
			endindex = matcher.end();
			log.debug("Start index : " + matcher.start() + ", end index : " + endindex + " data : " + matcher.group());
			// replace the matching pattern with data from arraylist and store
			// in string buffer
			matcher.appendReplacement(sb1, "FD" + arr.get(count) + ",");
			count++;
		}
		String tailstring = source.substring(endindex, source.length());
		// concatenating tail string in template
		sb1 = sb1.append(tailstring);
		String templatedata = sb1.toString().replace("~^FS", "^FS");
		log.debug(sb1.toString().replace("~^FS", "^FS"));
		log.debug(templatedata);
		return templatedata;

	}

}