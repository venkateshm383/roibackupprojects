package com.getusroi.wms20.label.eventbuilder;

import com.getusroi.config.RequestContext;
import com.getusroi.eventframework.event.ROIEvent;

public class CloseLabelEvent extends ROIEvent{

	private static final long serialVersionUID = 3708013399441884346L;
	String batchid=null;

	public CloseLabelEvent(String eventid,RequestContext reqContext) {
		super(eventid,reqContext);
	}

	public String getBatchid() {
		return batchid;
	}

	public void setBatchid(String batchid) {
		this.batchid = batchid;
	}
	
	

}
