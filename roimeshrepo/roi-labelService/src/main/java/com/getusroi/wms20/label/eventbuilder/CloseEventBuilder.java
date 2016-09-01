package com.getusroi.wms20.label.eventbuilder;

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
import com.getusroi.dynastore.DynaStoreFactory;
import com.getusroi.dynastore.DynaStoreSession;
import com.getusroi.eventframework.camel.eventproducer.AbstractCamelEventBuilder;
import com.getusroi.eventframework.event.ROIEvent;
import com.getusroi.eventframework.jaxb.Event;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.label.service.vo.PrintLabelBatch;

public class CloseEventBuilder extends AbstractCamelEventBuilder {
	protected static final Logger logger = LoggerFactory.getLogger(CloseEventBuilder.class);

	@Override
	public ROIEvent buildEvent(Exchange camelExchange, Event eventConfig) {
		logger.debug(".buildEvent of CloseEventBuilder "+eventConfig.getId());
		MeshHeader meshHeader=(MeshHeader)camelExchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		RequestContext reqContext=super.getRequestContextFromCamelExchange(camelExchange);
		CloseLabelEvent event = new CloseLabelEvent(eventConfig.getId(),reqContext);
		logger.debug("roi event in close event builder : "+event);
		Map<String,Object> genericdata=meshHeader.getGenricdata();
		JSONArray jsonarr=(JSONArray)genericdata.get(MeshHeaderConstant.DATA_KEY);
		JSONObject jsonobj=null;
		try {
			jsonobj = (JSONObject)jsonarr.get(0);
		} catch (JSONException e1) {
			//#TODO need to throw custom exception, change if framework code to support throws for method
			logger.error("error while getting JSON object "+e1);

		}
		String batchid=null;
		
		try {
			batchid = (String)jsonobj.get("batchId");
			logger.debug("batchid : "+batchid);
		} catch (JSONException e) {
			//#TODO need to throw custom exception, change if framework code to support throws for method
			logger.error("error while getting bachid from json object "+e);
		}
		Map<String, Serializable> eventParam=new HashMap<String, Serializable>();
		eventParam.put("batchID",batchid);
		event.setEventParam(eventParam);
		return event;
	}

	
}
