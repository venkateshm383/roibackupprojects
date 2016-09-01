package com.getusroi.wms20.label.nicelabel.transformer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.getusroi.eventframework.dispatcher.transformer.IROIEventTransformer;
import com.getusroi.eventframework.dispatcher.transformer.ROIEventTransformationException;
import com.getusroi.eventframework.event.ROIEvent;

public class CustomAddedLabelTransformer implements IROIEventTransformer{

	@Override
	public Serializable transformEvent(ROIEvent roievent) throws ROIEventTransformationException {
		Map<String,Object> customObject=new HashMap<String,Object>();
		String customMessage="This is a custom message to showcase the Custom add event transformation";
		customObject.put("CustomMessage", customMessage);
		customObject.put("roievent data", roievent);
		return (Serializable)customObject;
	}

}
