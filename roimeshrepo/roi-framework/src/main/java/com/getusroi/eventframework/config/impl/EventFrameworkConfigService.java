package com.getusroi.eventframework.config.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.config.ConfigurationContext;
import com.getusroi.eventframework.camel.eventproducer.AbstractCamelEventBuilder;
import com.getusroi.eventframework.camel.eventproducer.ServiceCompletionFailureEventBuilder;
import com.getusroi.eventframework.camel.eventproducer.ServiceCompletionSuccessEventBuilder;
import com.getusroi.eventframework.config.EventFrameworkConfigurationException;
import com.getusroi.eventframework.config.IEventFrameworkConfigService;
import com.getusroi.eventframework.jaxb.CamelEventProducer;
import com.getusroi.eventframework.jaxb.DispatchChanel;
import com.getusroi.eventframework.jaxb.Event;
import com.getusroi.eventframework.jaxb.EventSubscription;
import com.getusroi.eventframework.jaxb.SystemEvent;

public class EventFrameworkConfigService implements IEventFrameworkConfigService {
	final Logger logger = LoggerFactory.getLogger(IEventFrameworkConfigService.class);
	static final String SERVICE_COMPLETION_SUCCESS="success";
	static final String SERVICE_COMPLETION_FAILURE="failure";
	static final String SERVICE_COMPLETION_ALL="all";
	
	private EventFrameworkConfigHelper configHelper = new EventFrameworkConfigHelper();

	public void addEventFrameworkConfiguration(ConfigurationContext configContext, Event evtFwkConfig) throws EventFrameworkConfigurationException {
		configHelper.addEventFrameworkConfiguration(configContext, evtFwkConfig);
	}// end of method

	public void addEventFrameworkConfiguration(ConfigurationContext configContext, SystemEvent sysevtFwkConfig) throws EventFrameworkConfigurationException {
		configHelper.addEventFrameworkConfiguration(configContext, sysevtFwkConfig);
	}

	public void addEventFrameworkConfiguration(ConfigurationContext configContext, DispatchChanel dispatchChanelConfig)
			throws EventFrameworkConfigurationException {
		configHelper.addEventFrameworkConfiguration(configContext, dispatchChanelConfig);
	}

	public void addEventFrameworkConfiguration(ConfigurationContext configContext, EventSubscription eventSubscriptionConfig)
			throws EventFrameworkConfigurationException {
		configHelper.addEventFrameworkConfiguration(configContext, eventSubscriptionConfig);
	}
	
	public Event getEventConfiguration(ConfigurationContext configContext, String forEventId) throws EventFrameworkConfigurationException {
		return configHelper.getEventConfiguration(configContext, forEventId);
	}

	public DispatchChanel getDispatchChanelConfiguration(ConfigurationContext configContext, String dispatchChanelId)
			throws EventFrameworkConfigurationException {
		return configHelper.getDispatchChanelConfiguration(configContext, dispatchChanelId);
	}

	public EventSubscription getEventSubscriptionConfiguration(ConfigurationContext configContext, String eventSubscriptionId)
			throws EventFrameworkConfigurationException {
		return configHelper.getEventSubscriptionConfiguration(configContext, eventSubscriptionId);
	}
	
	public CamelEventProducer getEventProducerForBean(ConfigurationContext configContext, String serviceName, String beanFQCN)
			throws EventFrameworkConfigurationException {
		return configHelper.getEventProducerForBean(configContext, serviceName, beanFQCN);

	}

	public List<CamelEventProducer> getEventProducerForServiceSuccessCompletion(ConfigurationContext configContext, String serviceName)
			throws EventFrameworkConfigurationException {
		return configHelper.getEventProducerForServiceSuccessCompletion(configContext,serviceName,SERVICE_COMPLETION_SUCCESS);
	}
	public List<CamelEventProducer> getEventProducerForServiceFailedCompletion(ConfigurationContext configContext, String serviceName)
			throws EventFrameworkConfigurationException {
		return configHelper.getEventProducerForServiceSuccessCompletion(configContext,serviceName,SERVICE_COMPLETION_FAILURE);
	}
	public List<CamelEventProducer> getEventProducerForServiceFailedAndSucessCompletion(ConfigurationContext configContext, String serviceName)
			throws EventFrameworkConfigurationException {
		return configHelper.getEventProducerForServiceSuccessCompletion(configContext,serviceName,SERVICE_COMPLETION_ALL);
	}

	public List<Event> getEventConfigProducerForServiceSuccessCompletion(ConfigurationContext configContext, String serviceName)
			throws EventFrameworkConfigurationException {
		return configHelper.getEventConfigProducerForServiceSuccessCompletion(configContext,serviceName,SERVICE_COMPLETION_SUCCESS);
	}
	public List<Event> getEventConfigProducerForServiceFailedCompletion(ConfigurationContext configContext, String serviceName)
			throws EventFrameworkConfigurationException {
		return configHelper.getEventConfigProducerForServiceSuccessCompletion(configContext,serviceName,SERVICE_COMPLETION_FAILURE);
	}
	public List<Event> getEventConfigProducerForServiceFailedAndSucessCompletion(ConfigurationContext configContext, String serviceName)
			throws EventFrameworkConfigurationException {
		return configHelper.getEventConfigProducerForServiceSuccessCompletion(configContext,serviceName,SERVICE_COMPLETION_ALL);
	}
	public Event getEventConfigProducerForBean(ConfigurationContext configContext,String serviceName ,String beanFQCN)throws EventFrameworkConfigurationException {
		return configHelper.getEventConfigProducerForBean(configContext, serviceName, beanFQCN);
	}
	
	public SystemEvent getSystemEventConfiguration(ConfigurationContext configContext, String systemEventId)throws EventFrameworkConfigurationException{
		return configHelper.getSystemEventConfiguration(configContext, systemEventId);
	}

	/**
	 * Standard EventBuilder for building ServiceCompletionSuccessEvent. Builder
	 * is configured once for each tenant
	 * 
	 * @param tenantId
	 * @return AbstractCamelEventBuilder
	 */
	public AbstractCamelEventBuilder getServiceCompletionSuccessEventBuilder(ConfigurationContext configContext) {
		return new ServiceCompletionSuccessEventBuilder();
	}

	/**
	 * Standard EventBuilder for building ServiceCompletionFailureEvent. Builder
	 * is configured once for each tenant
	 * 
	 * @param tenantId
	 * @return AbstractCamelEventBuilder
	 */
	public AbstractCamelEventBuilder getServiceCompletionFailureEventBuilder(ConfigurationContext configContext) {
		return new ServiceCompletionFailureEventBuilder();
	}

	
	public boolean changeStatusOfDispactherChanelConfiguration(ConfigurationContext configurationContext,String dispatchChanelId,boolean isEnable) throws  EventFrameworkConfigurationException{
		return configHelper.changeStatusOfDispactherChanelConfiguration(configurationContext, dispatchChanelId, isEnable);
		
	}
	
	public boolean changeStatusOfEventSubscriptionConfiguration(ConfigurationContext configurationContext,String eventSubscriptionId,boolean isEnable) throws  EventFrameworkConfigurationException{
		return configHelper.changeStatusOfEventSubscriptionConfiguraion(configurationContext, eventSubscriptionId, isEnable);
		
	}

	public boolean changeStatusOfEventSubscriber(ConfigurationContext configurationContext,String eventSubscriptionId,String subscriberId,boolean isEnable) throws  EventFrameworkConfigurationException{
		return configHelper.changeStatusOfEventSubscriber(configurationContext, eventSubscriptionId, subscriberId,isEnable);
		
	}
	public boolean changeStatusOfSystemEventConfiguration(ConfigurationContext configurationContext, String systemEventId,boolean isEnable) throws EventFrameworkConfigurationException {
		return configHelper.changeStatusOfSystemEventConfiguration(configurationContext, systemEventId, isEnable);
	}

	@Override
	public boolean deleteDipatcherChanelConfiguration(
			ConfigurationContext configContext, String dispatchChanelId)
			throws EventFrameworkConfigurationException {
		return configHelper.deleteDipatcherChanelConfiguration(configContext, dispatchChanelId);
	}
	
	

	@Override
	public boolean deleteSystemEventConfiguration(
			ConfigurationContext configContext, String systemEventId)
			throws EventFrameworkConfigurationException {
		return configHelper.deleteSystemEventConfiguration(configContext, systemEventId);
	}
	
	@Override
	public boolean deleteEventSubscriptionConfiguration(ConfigurationContext configContext, String subscriptionEventId)throws EventFrameworkConfigurationException {
		return configHelper.deleteEventSubscriptionConfiguration(configContext, subscriptionEventId);
	}

	@Override
	public boolean changeStatusOfEventConfiguration(
			ConfigurationContext configContext, String eventId,
			boolean isEnable) throws EventFrameworkConfigurationException {
		
		return configHelper.changeStatusOfEventConfiguration(configContext, eventId, isEnable);
	}

	@Override
	public boolean deleteEventConfiguration(ConfigurationContext configContext,
			String eventId) throws EventFrameworkConfigurationException {
		
		return configHelper.deleteEventConfiguration(configContext, eventId);
	}
}