package com.getusroi.wms20.label.bartender.eventbuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.config.RequestContext;
import com.getusroi.eventframework.camel.eventproducer.AbstractCamelEventBuilder;
import com.getusroi.eventframework.event.ROIEvent;
import com.getusroi.eventframework.jaxb.Event;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;

public class ProduceEventBuilder extends AbstractCamelEventBuilder {
	protected static final Logger logger = LoggerFactory.getLogger(ProduceEventBuilder.class);

	/**
	 * Build event 
	 */
	@Override
	public ROIEvent buildEvent(Exchange camelExchange, Event eventConfig) {
		logger.debug(".buildEvent of ProduceEventBuilder "+eventConfig.getId());
		MeshHeader meshHeader=(MeshHeader)camelExchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		RequestContext reqContext=super.getRequestContextFromCamelExchange(camelExchange);
		ProduceLabelEvent event = new ProduceLabelEvent(eventConfig.getId(),reqContext);
		logger.debug("roi event in produce event builder : "+event);
		Map<String,Object> genericdata=meshHeader.getGenricdata();
		JSONArray jsonarr=(JSONArray)genericdata.get(MeshHeaderConstant.DATA_KEY);
		JSONObject jsonobj=null;
		try {
			jsonobj = (JSONObject)jsonarr.get(0);
		} catch (JSONException e1) {
			//#TODO parent class not handling exception ( have to add)  
			logger.error("error while getting JSON object "+e1);

		}
		String batchid=null, printerId=null;
		
		try {
			batchid = (String)jsonobj.get("batchId");
			
		Object 	object = jsonobj.get("printerId");
		logger.debug("Printer ID before sending to event "+object);
			if(object instanceof Integer){
			int	id=(Integer)object;
			printerId=id+"";
			}else if(object instanceof String){
				printerId=(String)object;
			}
			
		} catch (JSONException e) {
			//#TODO parent class not handling exception ( have to add)  

			logger.error("error while getting bachid from json object "+e);
		}
		Map<String, Serializable> eventParam=new HashMap<String, Serializable>();
		eventParam.put("batchID",batchid);
		eventParam.put("printerId", printerId);
		event.setEventParam(eventParam);
		return event;
	}

	
}
