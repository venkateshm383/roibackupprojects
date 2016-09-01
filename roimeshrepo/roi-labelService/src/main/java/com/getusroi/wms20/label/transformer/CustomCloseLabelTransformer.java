package com.getusroi.wms20.label.transformer;

import java.io.Serializable;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.eventframework.dispatcher.transformer.IROIEventTransformer;
import com.getusroi.eventframework.dispatcher.transformer.ROIEventTransformationException;
import com.getusroi.eventframework.event.ROIEvent;

public class CustomCloseLabelTransformer implements IROIEventTransformer{
	protected static final Logger logger = LoggerFactory.getLogger(CustomCloseLabelTransformer.class);

	@Override
	public Serializable transformEvent(ROIEvent roievent) throws ROIEventTransformationException {
		logger.debug(".transformEvent of CustomCloseLabelTransformer");
		Map<String, Serializable> eventparam=roievent.getEventParam();
		String batchid=(String)eventparam.get("batchID");
		String printerId="";//#TODO get printerId from exchange body
		String jsonData="{\"data\":[{\"batchid\":"+batchid+",\"printerId\":\"\"}]}";
		logger.debug("json data string "+jsonData);
		JSONObject jobj=null;
		try {
			 jobj=new JSONObject(jsonData);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jobj.toString();
	}

}
