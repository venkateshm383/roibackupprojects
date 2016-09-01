package com.getusroi.wms20.label.nicelabel.transformer;

import java.io.Serializable;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.eventframework.dispatcher.transformer.IROIEventTransformer;
import com.getusroi.eventframework.dispatcher.transformer.ROIEventTransformationException;
import com.getusroi.eventframework.event.ROIEvent;

public class CustomProduceLabelTransformer implements IROIEventTransformer{
	protected static final Logger logger = LoggerFactory.getLogger(CustomProduceLabelTransformer.class);

	@Override
	public Serializable transformEvent(ROIEvent roievent) throws ROIEventTransformationException {
		logger.debug(".transformEvent of CustomProduceLabelTransformer");
		Map<String, Serializable> eventparam=roievent.getEventParam();
		String batchid=(String)eventparam.get("batchID");
		logger.debug("Batch id  In CustomProduceLabelTransformer  class ="+batchid);
		Object printerId=eventparam.get("printerId");
		int printerId1 = 0;
		if(printerId instanceof String){
			 printerId1 = Integer.parseInt(printerId.toString());
			logger.debug("printer id  In CustomProduceLabelTransformer if string class ="+printerId1);
		}	else{
			printerId1 = (Integer) printerId;
		logger.debug("printer id  In CustomProduceLabelTransformer  class ="+printerId1);
		}
		String jsonData="{\"data\":[{\"batchid\":"+batchid+",\"printerId\":"+printerId1+"}]}";
		logger.debug("json data string "+jsonData);
		JSONObject jobj=null;
		try {
			 jobj=new JSONObject(jsonData);
		} catch (JSONException e) {
			throw  new ROIEventTransformationException();
		}
		return jobj.toString();
	}

}
