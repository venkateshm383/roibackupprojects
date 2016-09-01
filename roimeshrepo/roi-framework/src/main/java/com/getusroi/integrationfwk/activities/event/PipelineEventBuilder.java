package com.getusroi.integrationfwk.activities.event;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.getusroi.config.RequestContext;
import com.getusroi.eventframework.camel.eventproducer.AbstractCamelEventBuilder;
import com.getusroi.eventframework.event.ROIEvent;
import com.getusroi.eventframework.jaxb.Event;
import com.getusroi.integrationfwk.activities.bean.ActivityConstant;
import com.getusroi.integrationfwk.config.jaxb.EventData;
import com.getusroi.integrationfwk.config.jaxb.EventPublishActivity;
import com.getusroi.integrationfwk.config.jaxb.PipeActivity;
import com.getusroi.integrationfwk.jdbcIntactivity.config.helper.JdbcIntActivityConfigHelper;
import com.getusroi.integrationfwk.jdbcIntactivity.config.helper.JdbcIntActivityConfigurationException;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;

public class PipelineEventBuilder extends AbstractCamelEventBuilder {
	Logger logger=LoggerFactory.getLogger(PipelineEventBuilder.class);
	
	/**
	 * This method is used to build event for pipeline Activity
	 * @param camelExchange : Camel Exchange Object
	 * @param eventConfig: Event Object
	 */
	@Override
	public ROIEvent buildEvent(Exchange camelExchange, Event eventConfig) {
		logger.debug(".buildEvent method of PipelineEventBuilder");
		MeshHeader meshHeader=(MeshHeader)camelExchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		PipeActivity pipeactivity = (PipeActivity) camelExchange.getIn().getHeader(ActivityConstant.PIPEACTIVITY_HEADER_KEY);
		EventPublishActivity eventPublishActivity=pipeactivity.getEventPublishActivity();
		JdbcIntActivityConfigHelper activityConfigHelper=new JdbcIntActivityConfigHelper();
		String inBodyData = camelExchange.getIn().getBody(String.class);
		PipelineEvent pipelineEvent=null;
		try {
			Document document=activityConfigHelper.generateDocumentFromString(inBodyData);
			pipelineEvent=createPipeLineEvent(eventPublishActivity,document,meshHeader);
		} catch (JdbcIntActivityConfigurationException e) {
			//#TODO, build event doesnot throw exception so catching it
			logger.error("error in geting the document object for incoming exchange body: "+inBodyData,e);
		}
		return pipelineEvent;
	}
	
	/**
	 * This method is used to create pipeline event
	 * @param eventPublishActivity : EventPublishActivity Object
	 * @param document : DOcument Object
	 * @param meshHeader : MeshHeader Object
	 * @return PipelineEvent Object
	 */
	private PipelineEvent createPipeLineEvent(EventPublishActivity eventPublishActivity, Document document,
			MeshHeader meshHeader) {
		RequestContext requestContext=meshHeader.getRequestContext();
		//creating pipeline event object
		PipelineEvent pipelineEvent=new PipelineEvent(eventPublishActivity.getEventName().trim(),requestContext);
		Map<String,Serializable> eventParam=pipelineEvent.getEventParam();
		List<EventData> eventDataList=eventPublishActivity.getEventActivityParams().getEventData();
		XPath xPath = XPathFactory.newInstance().newXPath();
		for(EventData eventData:eventDataList){
			try {
				XPathExpression expr = xPath.compile(eventData.getXpathExpression());
				NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
				if (nodeList != null && nodeList.getLength() > 0) {
					logger.debug("nodename : "+nodeList.item(0).getNodeName()+", value : "+nodeList.item(0).getTextContent());
					eventParam.put(nodeList.item(0).getNodeName().trim(),nodeList.item(0).getTextContent().trim());
				}				
			} catch (XPathExpressionException e) {
				//#TODO, build event doesnot throw exception so catching it
				logger.error("error in processing the xpath : "+eventData.getXpathExpression(),e);
			}
		}
		return pipelineEvent;
	}//end of method createPipeLineEvent

}
