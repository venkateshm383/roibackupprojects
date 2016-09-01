package com.getusroi.wms20.label.bartender.eventbuilder;

import com.getusroi.config.RequestContext;
import com.getusroi.eventframework.event.ROIEvent;

public class ProduceLabelEvent extends ROIEvent{

	private static final long serialVersionUID = 3708013399441884346L;
	String batchid=null;

	public ProduceLabelEvent(String eventid,RequestContext reqContext) {
		super(eventid,reqContext);
	}

	public String getBatchid() {
		return batchid;
	}

	public void setBatchid(String batchid) {
		this.batchid = batchid;
	}
	
	

}
