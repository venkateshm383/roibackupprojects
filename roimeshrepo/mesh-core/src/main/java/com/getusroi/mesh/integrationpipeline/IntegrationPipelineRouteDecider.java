package com.getusroi.mesh.integrationpipeline;

import org.apache.camel.Exchange;
import org.slf4j.LoggerFactory;

import com.getusroi.integrationfwk.activities.bean.ActivityConstant;
import com.getusroi.integrationfwk.config.IntegrationPipelineConfigException;
import com.getusroi.integrationfwk.config.IntegrationPipelineConfigUnit;
import com.getusroi.integrationfwk.config.jaxb.IntegrationPipe;
import com.getusroi.integrationfwk.config.jaxb.PipeActivity;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;

public class IntegrationPipelineRouteDecider {

	final org.slf4j.Logger logger = LoggerFactory.getLogger(IntegrationPipelineRouteDecider.class);
	private static final String XSLT_ENRICHER_ROUTE_KEY = "xsltEnricherRoute";
	private static final String FTL_ENRICHER_ROUTE_KEY = "ftlEnricherRoute";
	private static final String FILTER_PIPELINE_ROUTE_KEY = "filterPipelineRoute";
	private static final String JDBC_INTACTIVITY_MYSQL_ROUTE_KEY = "jdbcIntMySQLActivityRoute";
	private static final String JDBC_INTACTIVITY_CASSANDRA_ROUTE_KEY = "jdbcIntCassandraActivityRoute";
	private static final String EMAIL_NOTIFY_ROUTE_KEY = "emailNotifyActivityRoute";
	private static final String EVENT_PUBLISH_PIPELINE_ROUTE_KEY = "eventPublishPipelineRoute";
	private static final String CAMEL_ENDPOINT_ROUTE_KEY = "camelRouteActivity";
	private static final String PIPE_ACTIVITY_HEADER_KEY = "pipeActivity";
	private static final String PROPERTIES_ACTIVITY_ROUTE_KEY = "propertiesEnricherRoute";
	private static final String ROUTE_DECIDER_KEY = "routeDecider";
	private static final String COUNTER_KEY = "counter";
	private static final String CASSANDRA_DBTYPE="CASSANDRA";
	private static final String MYSQL_DBTYPE="MYSQL";

	/**
	 * processor to process the activityConfiguration, loops until the count of
	 * activityConfigNumbers and the processes it from the corresponding
	 * endpoint and listens for the response to further process it.
	 * 
	 * @param exchange
	 * @throws IntegrationPipelineConfigException
	 * @throws InitializingPipelineException
	 */
	public void gettingPipeActivitiesSize(Exchange exchange) throws InitializingPipelineException {
		logger.debug(".gettingPipeactivitiesSize()....");
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		IntegrationPipelineConfigUnit pipelineConfigUnit = (IntegrationPipelineConfigUnit) meshHeader
				.getIntegrationpipelineData().get(MeshHeaderConstant.PIPELINE_CONFIG_KEY);
		IntegrationPipe integrationPipe = pipelineConfigUnit.getIntegrationPipe();
		java.util.List<PipeActivity> pipeactivityList = integrationPipe.getPipeActivity();
		int sizeOfpipeActivities = pipeactivityList.size();
		exchange.getIn().setHeader(COUNTER_KEY, sizeOfpipeActivities);
	}// ..end of the method

	/**
	 * method called as a bean with in the loop, and to dispatch the activity
	 * configurations
	 * 
	 * @param exchange
	 * @throws InitializingPipelineException
	 */
	public void loopandProcessActivity(Exchange exchange) throws InitializingPipelineException {
		String sendvalue = (String) exchange.getIn().getHeader(ActivityConstant.FILTER_RESULT_SEND_KEY);
		String dropvalue = (String) exchange.getIn().getHeader(ActivityConstant.FILTER_RESULT_DROP_KEY);
		logger.debug("send value : " + sendvalue + " , drop value : " + dropvalue);
		//pipeActivityChecking
		IntegrationPipelineConfigUnit pipelineConfigUnit = (IntegrationPipelineConfigUnit) exchange.getIn().getHeader("pipeActivityKey");
		String name = pipelineConfigUnit.getIntegrationPipe().getName();
		logger.debug("Config name: of - : "+exchange.getExchangeId()+" - with name: "+name);
		if (sendvalue == null && dropvalue == null) {
			processActivity(name, exchange);
			//processActivity(MeshHeaderConstant.PIPELINE_CONFIG_KEY, exchange);
		} else if (sendvalue != null && !(sendvalue.isEmpty()) && sendvalue.equalsIgnoreCase("false")) {
			exchange.getIn().setHeader(COUNTER_KEY, 0);
		} else if (dropvalue != null && !(dropvalue.isEmpty()) && dropvalue.equalsIgnoreCase("true")) {
			exchange.getIn().setHeader(COUNTER_KEY, 0);
		} else {
			processActivity(name, exchange);
			//processActivity(MeshHeaderConstant.PIPELINE_CONFIG_KEY, exchange);
		}
	}// ..end of the method

	/**
	 * Method accessed locally to get the route names, to be called..
	 * @param configName
	 * @param exchange
	 * @throws InitializingPipelineException
	 */
	public void processActivity(String configName, Exchange exchange) throws InitializingPipelineException {

		int i = Integer.valueOf(exchange.getProperty(Exchange.LOOP_INDEX).toString());
		logger.debug("index value for pipeline : "+i);
		//MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		IntegrationPipelineConfigUnit pipelineConfigUnit = (IntegrationPipelineConfigUnit) exchange.getIn().getHeader("pipeActivityKey");
		//logger.debug("Checking pipeData: at "+i+" and"+pipelineConfigUnit.getConfigData());
		IntegrationPipe integrationPipe = pipelineConfigUnit.getIntegrationPipe();
		java.util.List<PipeActivity> pipeactivityList = integrationPipe.getPipeActivity();
		PipeActivity pipeactivity = pipeactivityList.get(i);
		if (pipeactivity.getFTLEnricherActivity() != null) {
			exchange.getIn().setHeader(ROUTE_DECIDER_KEY, FTL_ENRICHER_ROUTE_KEY);
			exchange.getIn().setHeader(PIPE_ACTIVITY_HEADER_KEY, pipeactivity);
		} else if (pipeactivity.getXSLTEnricherActivity() != null) {
			exchange.getIn().setHeader(ROUTE_DECIDER_KEY, XSLT_ENRICHER_ROUTE_KEY);
			exchange.getIn().setHeader(PIPE_ACTIVITY_HEADER_KEY, pipeactivity);
		} else if (pipeactivity.getJDBCIntActivity() != null) {
			if(pipeactivity.getJDBCIntActivity().getDBConfig().getDbType().equalsIgnoreCase(CASSANDRA_DBTYPE)){
			exchange.getIn().setHeader(ROUTE_DECIDER_KEY, JDBC_INTACTIVITY_CASSANDRA_ROUTE_KEY);
			exchange.getIn().setHeader(PIPE_ACTIVITY_HEADER_KEY, pipeactivity);
			}else if(pipeactivity.getJDBCIntActivity().getDBConfig().getDbType().equalsIgnoreCase(MYSQL_DBTYPE)){
				exchange.getIn().setHeader(ROUTE_DECIDER_KEY, JDBC_INTACTIVITY_MYSQL_ROUTE_KEY);
				exchange.getIn().setHeader(PIPE_ACTIVITY_HEADER_KEY, pipeactivity);
			}
		} else if (pipeactivity.getFilterPipelineActivity() != null) {
			exchange.getIn().setHeader(ROUTE_DECIDER_KEY, FILTER_PIPELINE_ROUTE_KEY);
			exchange.getIn().setHeader(PIPE_ACTIVITY_HEADER_KEY, pipeactivity);
		} else if (pipeactivity.getEmailNotifyActivity() != null) {
			exchange.getIn().setHeader(ROUTE_DECIDER_KEY, EMAIL_NOTIFY_ROUTE_KEY);
			exchange.getIn().setHeader(PIPE_ACTIVITY_HEADER_KEY, pipeactivity);
		} else if (pipeactivity.getCamelRouteEndPoint() != null) {
			exchange.getIn().setHeader(ROUTE_DECIDER_KEY, CAMEL_ENDPOINT_ROUTE_KEY);
			exchange.getIn().setHeader(PIPE_ACTIVITY_HEADER_KEY, pipeactivity);
		} else if (pipeactivity.getPropertiesActivity() != null) {
			exchange.getIn().setHeader(ROUTE_DECIDER_KEY, PROPERTIES_ACTIVITY_ROUTE_KEY);
			exchange.getIn().setHeader(PIPE_ACTIVITY_HEADER_KEY, pipeactivity);
		}else if (pipeactivity.getEventPublishActivity() != null) {
			exchange.getIn().setHeader(ROUTE_DECIDER_KEY, EVENT_PUBLISH_PIPELINE_ROUTE_KEY);
			exchange.getIn().setHeader(PIPE_ACTIVITY_HEADER_KEY, pipeactivity);
		} else {
			throw new InitializingPipelineException("Invalid configuration exist in IntegrationPipeLine Activities..");
		}
	}// end of method processActivity
}
