package com.getusroi.mesh.notifier;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.management.event.AbstractExchangeEvent;
import org.apache.camel.management.event.ExchangeCompletedEvent;
import org.apache.camel.management.event.ExchangeFailedEvent;
import org.apache.camel.management.event.ExchangeSentEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.config.RequestContext;
import com.getusroi.core.datagrid.DataGridService;
import com.getusroi.eventframework.camel.CamelEventSynchProducer;
import com.getusroi.eventframework.event.ROIEvent;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.TransactionalList;
import com.hazelcast.core.TransactionalSet;
import com.hazelcast.transaction.TransactionContext;

public class ROICamelEventNotifier extends EventNotifierSupport {
	final Logger logger = LoggerFactory.getLogger(ROICamelEventNotifier.class);
		
	//EventNotifierHelper evtNotHelper=new EventNotifierHelper();
	CamelEventSynchProducer evtProducer=new CamelEventSynchProducer();
	
	public boolean	isIgnoreCamelContextEvents() {return true;}
	public boolean	isIgnoreExchangeEvents() {return false;}
	public boolean	isIgnoreExchangeCompletedEvent() {return false;}
	public boolean	isIgnoreExchangeCreatedEvent() {return true;}
	public boolean	isIgnoreExchangeFailedEvents() {return false;}
	public boolean	isIgnoreExchangeRedeliveryEvents() {return true;}
	public boolean	isIgnoreExchangeSendingEvents() {return true;}
	public boolean	isIgnoreExchangeSentEvents() {return true;}
	public boolean	isIgnoreRouteEvents() {return true;}
	public boolean	isIgnoreServiceEvents() {return true;}
	
	/**
	 * This method is to check notifier component is enabled or disabled.
	 * @param event : EventObject
	 * @return boolean
	 */
	public boolean isEnabled(EventObject event) {
		logger.debug("isEnabled -- EventObject="+event);
		AbstractExchangeEvent compEvent=(AbstractExchangeEvent)event;
		Exchange exchange=compEvent.getExchange();
		//String routeId=exchange.getFromRouteId();
		//String contextStr=exchange.getContext().getName();		
		//logger.info("isEnabled -- Exchange="+exchange);
		if(event instanceof ExchangeCompletedEvent){
			//logger.info("isEnabled -- CompletedEvent=");
			return true;
			//return evtNotHelper.hasEventForRoute("default",contextStr,routeId);
		}else if(event instanceof ExchangeSentEvent){
			//logger.info("isEnabled -- ExchangeSentEvent="+exchange);
			return false;
		}
		
		return true;
	}

	/**
	 * This method is to notify Exchange Completed (success or faliure)
	 * @param event : EventObject
	 * @return boolean
	 */
		
	@Override
	public void notify(EventObject event) throws Exception {
		logger.debug("notify -- EventObject="+event.getClass());
		if(event instanceof ExchangeCompletedEvent){
			//Exchange/complete route from base till Impl route can be Completed with Failure or Success
			logger.debug("when event is ExchangeComplete Event :");
			ExchangeCompletedEvent compEvent=(ExchangeCompletedEvent)event;
			Exchange exchange=compEvent.getExchange();
			MeshHeader meshHeader=(MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
			if(meshHeader !=null){
			RequestContext reqCtx=meshHeader.getRequestContext();			
			logger.debug("meshHeader in notifier : "+meshHeader);
			//tenat and site
			String tenantid=meshHeader.getTenant();
			String featureName=meshHeader.getFeatureName();
			String serviceName=meshHeader.getServicetype();			

			logger.debug("data into notifier exchngeCompleteEvent : serviceName : "+serviceName+", tenantid : "+tenantid+", feature Name : "+featureName);
			Map exchangeProp=exchange.getProperties();			
			logger.debug("exchnage property values  in ROICamelEventNotifier : "+exchangeProp);
			if (!checkMultipleExchange(exchangeProp)) {
			boolean iscompletedWithFailure=isCompletedWithFailure(exchangeProp);
			logger.debug("valure of isCompletedWithFailure : "+iscompletedWithFailure);
			if(iscompletedWithFailure){
				logger.debug("notify --FialedCompletedEvent-- EventObject="+featureName);
				//Build Event for Route Failed Condition and publish to event Service
				evtProducer.publishEventForFailedRouteCompletion(serviceName, exchange);
				//Build standard mandatory event for Failure and publish to eventService
				evtProducer.publishServiceCompletionFailureSystemEvent(reqCtx, exchange);
				//Just debug logs check to see if hazelcast rolledback or not
				checkHazelCastListValue(meshHeader);
				//Clean Hazelcast EventList. We are done publishing all events.
				deleteHazelCastListforRequest(meshHeader);
				//closing all datasource connection on success
				closeAllDataSourceConnection(meshHeader);
			}
			else{
				logger.debug("notify --Entered CompletedEvent--Feature="+featureName);
				List<ROIEvent> hcList=getHazelCastListforEvent(meshHeader);
				//Publish the event generated by the component during camel Route and gathered in HazelCastList
				if(hcList!=null && !(hcList.isEmpty())){
					evtProducer.publishComponentEvent(hcList);
				}else{
					logger.debug("notify --No CompletedEvent--found for Feature="+serviceName);
				}
				//Build <Configured> Events for Route Sucecss Condition and publish to event Service
				evtProducer.publishEventForRouteCompletion(serviceName, exchange);
				//Build standard mandatory event for serviceName/Route sucess and publish to eventService
				evtProducer.publishServiceCompletionSuccessSystemEvent(reqCtx, exchange);
				//Clean Hazelcast EventList. We are done publishing all events.
				deleteHazelCastListforRequest(meshHeader);
				//closing all datasource connection on success
				closeAllDataSourceConnection(meshHeader);
			}
			logger.debug("notify -- EventObject="+exchange.getIn());
			}}
		}else if(event instanceof ExchangeFailedEvent){
			//Cases were Exchange failed specifically
			logger.debug("inside exchanged failed event : ");
			ExchangeFailedEvent compEvent=(ExchangeFailedEvent)event;
			Exchange exchange=compEvent.getExchange();
			MeshHeader meshHeader=(MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
			if(meshHeader !=null){
			logger.debug("meshHeader in notifier : "+meshHeader);
			RequestContext reqCtx=meshHeader.getRequestContext();
			String servicetype=meshHeader.getServicetype();
			
			//Build Event for Route Failed Condition and publish to event Service
			evtProducer.publishEventForFailedRouteCompletion(servicetype, exchange);
			//Build standard mandatory event for Failure and publish to eventService
			evtProducer.publishServiceCompletionFailureSystemEvent(reqCtx, exchange);
			//Just debug logs check to see if hazelcast rolledback or not
			checkHazelCastListValue(meshHeader);
			//Clean Hazelcast EventList. We are done publishing all events.
			deleteHazelCastListforRequest(meshHeader);			
			//closing all datasource connection on success
			closeAllDataSourceConnection(meshHeader);
			}
		}
		else {
			logger.debug("notify --OtherEventType--Event="+event);
		}
	}
	
	/**
	 * to check the multiplicity of exchanges, for the Multicast-EIP
	 * 
	 * @param exchangeProp
	 * @return true if chances of multipleExchangeExists
	 */
	private boolean checkMultipleExchange(Map exchangeProp) {
		if ((!exchangeProp.containsKey(Exchange.MULTICAST_INDEX))) {
			return false;
			}
		 else {
			exchangeProp.remove(Exchange.MULTICAST_INDEX);
			return true;
		}
	}// ..end of the method
	
	/**
	 * This method is to get List of all ROIEvents Store in hazelcast
	 * @param exchange : Exchange
	 * @return List
	 */
	private List<ROIEvent> getHazelCastListforEvent(MeshHeader meshheader){
		logger.debug("inside getHazelcastListForEvent() in ROICamelNotifier bean");
		HazelcastInstance hazelcastInstance=DataGridService.getDataGridInstance().getHazelcastInstance();
		String requestId=meshheader.getRequestUUID();
		logger.debug(".getHazelCastListforEvent() requestId: "+requestId);
		IList<ROIEvent> eventList=hazelcastInstance.getList(requestId);
		logger.debug("eventList id in getHazelcast : "+eventList);
		
	if(eventList==null && eventList.isEmpty()){
			logger.debug("notify  ------getHazelCastListValue-List Is Empty");
		}
		for(ROIEvent event : eventList){
				logger.debug("notify  ------getHazelCastListValue"+event.getEventId());

		 }
		
		return eventList;
	}
	
	/**
	 * Destroy Hazelcast List having events for the given route/camel exhange based on unique RequestId generated in the baseImpl Route.<br>
	 * @param exchange : Exchange
	 */
	private void deleteHazelCastListforRequest(MeshHeader meshheader){
		logger.debug("inside deleteHazelCastListforRequest() in ROICamelNotifier bean");

		HazelcastInstance hazelcastInstance=DataGridService.getDataGridInstance().getHazelcastInstance();
		String requestId=meshheader.getRequestUUID();
		logger.debug("rquestId in deletehazelcastEventList : "+requestId);
		IList<ROIEvent> eventList=hazelcastInstance.getList(requestId);
		logger.debug("event list in delete hazelcast list : "+eventList);
		if(eventList!=null);
			eventList.destroy();
	}
	
	/**
	 * This method is to check ROIEvents in hazelcast or not
	 * @param exchange : Exchange
	 */
	private void checkHazelCastListValue(MeshHeader meshheader){
		
		logger.debug("inside checkHazelcastListValue() in ROICamelNotifier bean");
		HazelcastInstance hazelcastInstance=DataGridService.getDataGridInstance().getHazelcastInstance();
		String requestId=meshheader.getRequestUUID();
		logger.debug("request id in checkHazelCastListValue : "+requestId);
		IList<ROIEvent> eventList=hazelcastInstance.getList(requestId);
		logger.debug("eventList id in checkHazelCastListValue : "+eventList);

		if(eventList==null || eventList.isEmpty()){
			logger.debug("notify  ------checkHazelCastListValue-List Is Empty");
		}
		for(ROIEvent event:eventList){
			logger.debug("notify  ------checkHazelCastListValue"+event.toString());
		}
	}
	
	
	/**
	 * This method is to check Exchanged finised success or failure
	 * @param exchangeProp
	 * @return
	 */
	private boolean isCompletedWithFailure(Map exchangeProp){
		
		logger.debug("inside isCompleteWithFailure() in ROICamelNotifier bean"); 
		
		Set<String> keys = exchangeProp.keySet();
        for(String key: keys){
            logger.debug(key);
        }
			boolean isfailure =exchangeProp.containsKey("CamelFailureRouteId");
			logger.debug("isFailure is  : "+isfailure);
			
			return isfailure;
	}
	
	/**
	 * This method is used close all open data source connection
	 * @param meshHeader : MeshHeader Object
	 * @throws SQLException
	 */
	private void closeAllDataSourceConnection(MeshHeader meshHeader) throws SQLException{
		logger.debug(".closeAllConnection method of ROICamelEventNotifier");
		Map<Object, Object> mapResourceHolder=meshHeader.getResourceHolder();
		for (Map.Entry<Object, Object> entry : mapResourceHolder.entrySet())
		{
			logger.debug(entry.getKey() + "/" + entry.getValue());
		    Connection connection=(Connection)entry.getValue();
		    if(connection != null)
		    	if(!(connection.isClosed()))
		    			connection.close();
		    
		}//end of for
	}//end of method closeAllConnection
	
	
}
