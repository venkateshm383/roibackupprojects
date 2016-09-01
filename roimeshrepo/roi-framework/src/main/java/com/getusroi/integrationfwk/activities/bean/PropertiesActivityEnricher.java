package com.getusroi.integrationfwk.activities.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.getusroi.integrationfwk.config.jaxb.PipeActivity;
import com.getusroi.integrationfwk.config.jaxb.PropertiesMapping;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;

public class PropertiesActivityEnricher {
	
	private Logger logger = LoggerFactory.getLogger(PropertiesActivityEnricher.class.getName());

	/**
	 * This method is to fetch the propertyvalue from the pipeline configuration
	 * and putting it into Exchange's body whose xpath it is fetching from the
	 * pipeline again.
	 * 
	 * @param exchange
	 * @throws PropertyActivityException
	 * @throws TransformerException
	 * @throws ActivityEnricherException
	 */
	public void processorBean(Exchange exchange)
			throws PropertyActivityException, ActivityEnricherException, TransformerException {
		PipeActivity pipeactivity = (PipeActivity) exchange.getIn().getHeader(ActivityConstant.PIPEACTIVITY_HEADER_KEY);
		List<PropertiesMapping> propertyMappingList = pipeactivity.getPropertiesActivity().getPropertiesMapping();
		Document xmlDocument = generateDocumentFromString(exchange.getIn().getBody(String.class));
		logger.debug("xml document  : "+xmlDocument);
		for (int i = 0; i < propertyMappingList.size(); i++) {
			logger.debug("Entered Loop");
			String propertyValue = pipeactivity.getPropertiesActivity().getPropertiesMapping().get(i).getPropertyValue();
			logger.debug("propertyValue : " + propertyValue);
			String nodeToAdd = pipeactivity.getPropertiesActivity().getPropertiesMapping().get(i).getElementToAdd();
			logger.debug("nodeToAdd : " + nodeToAdd);
			String toXpath = pipeactivity.getPropertiesActivity().getPropertiesMapping().get(i).getSetToXpath().toString();
			logger.debug("got the xpath : " + toXpath.toString());
			String propertyValueSource = pipeactivity.getPropertiesActivity().getPropertiesMapping().get(i).getPropertyValueSource();
			logger.debug("got the propertyValueSource : " + propertyValueSource);
			String newXmlDoc = checkPropertyValueSource(propertyValueSource,toXpath, xmlDocument, propertyValue, nodeToAdd,exchange);
			logger.debug("newXMLDoc : " + newXmlDoc);
			xmlDocument = generateDocumentFromString(newXmlDoc);
		}
		exchange.getIn().setBody(documentToString(xmlDocument));
	}// ..end of method

	/**
	 * to generate the document object once and all from the xml input which is
	 * of String
	 * 
	 * @param xmlInput
	 * @return documentObject
	 * @throws EmailNotifierException
	 * @throws ParserConfigurationException
	 */
	public Document generateDocumentFromString(String xmlInput) throws PropertyActivityException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document xmlDocument;
		xmlInput = xmlInput.trim();
		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new PropertyActivityException("Unable to initiate the document builder..", e);
		}
		try {
			xmlDocument = builder.parse(new ByteArrayInputStream(xmlInput.getBytes("UTF-16")));
		} catch (SAXException | IOException e) {
			throw new PropertyActivityException("Unable to parse the xmlString into document..", e);
		}
		return xmlDocument;
	}// ..end of method
	
	
	private String checkPropertyValueSource(String propertyValueSource,String toXpath,Document xmlDocument,String propertyValue,String nodeToAdd,Exchange exchange) throws ActivityEnricherException, TransformerException{
		logger.debug(".checkPropertyValueSource method PropertiesActivityEnricher");
		String newXmlDoc =null;
		logger.debug("xml document  : "+xmlDocument);
		if(propertyValueSource != null && propertyValueSource.length()>0 && !(propertyValueSource.isEmpty())){
			switch(propertyValueSource){				
				case "MeshHeader" :logger.debug("propertyValueSource is MeshHeader");
				   newXmlDoc=getNodeValueFromMeshHeaderAndAppend(toXpath, xmlDocument, propertyValue, nodeToAdd,exchange);
				   break;
				   
				case "Exchange" :logger.debug("propertyValueSource is Exchange");
				   newXmlDoc=getNodeValueFromExchangeAndAppend(toXpath, xmlDocument, propertyValue, nodeToAdd,exchange);
				   break;
				   
				   default : logger.debug("propertyValueSource is direct");
				   newXmlDoc=getNodeAndAppend(toXpath, xmlDocument, propertyValue, nodeToAdd);
				   break;				
			}
		}else{
			//#TODO , need to throw proper exception
			logger.debug("propertyValueSource shouldnot be null");
		}
		
		return newXmlDoc;
	}

	/**
	 * to append string(res) into node fetched by using xpath expression
	 * 
	 * @param expression
	 * @param xmlDocument
	 * @param nodeToAdd
	 * @return
	 * @return non duplicate values as set
	 * @throws ActivityEnricherException
	 * @throws TransformerException
	 */
	private String getNodeAndAppend(String expression, Document xmlDocument, String res, String nodeToAdd)
			throws ActivityEnricherException, TransformerException {
		String appendedDocument = null;
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = null;
		try {
			nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
			// Node has been added fetched from the pipline configuration
			Element node = xmlDocument.createElement(nodeToAdd);
			node.setTextContent(res);
			// appended the element as a child to the node fetched from the
			// xpath
			nodeList.item(0).appendChild(node);
			appendedDocument = documentToString(xmlDocument);
		} catch (XPathExpressionException e) {
			throw new ActivityEnricherException("Unable to compile the xpath expression at index - "
					+ " when evaluating document - " + xmlDocument + "..", e);
		}

		return appendedDocument;
	}// ..end of the method
	
	
	/**
	 * to append string(res) into node fetched by using xpath expression
	 * 
	 * @param expression
	 * @param xmlDocument
	 * @param nodeToAdd
	 * @return
	 * @return non duplicate values as set
	 * @throws ActivityEnricherException
	 * @throws TransformerException
	 */
	private String getNodeValueFromMeshHeaderAndAppend(String expression, Document xmlDocument, String res, String nodeToAdd,Exchange exchange)
			throws ActivityEnricherException, TransformerException {
		MeshHeader meshHeader=(MeshHeader)exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String,Object> serviceRequestDataValue=meshHeader.getServiceRequestData();
		String appendedDocument = null;
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = null;
		try {
			nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
			// Node has been added fetched from the pipline configuration
			Element node = xmlDocument.createElement(nodeToAdd);
			node.setTextContent((String)serviceRequestDataValue.get(res));
			// appended the element as a child to the node fetched from the
			// xpath
			nodeList.item(0).appendChild(node);
			appendedDocument = documentToString(xmlDocument);
		} catch (XPathExpressionException e) {
			throw new ActivityEnricherException("Unable to compile the xpath expression at index - "
					+ " when evaluating document - " + xmlDocument + "..", e);
		}

		return appendedDocument;
	}// ..end of the method
	
	/**
	 * to append string(res) into node fetched by using xpath expression
	 * 
	 * @param expression
	 * @param xmlDocument
	 * @param nodeToAdd
	 * @return
	 * @return non duplicate values as set
	 * @throws ActivityEnricherException
	 * @throws TransformerException
	 */
	private String getNodeValueFromExchangeAndAppend(String expression, Document xmlDocument, String res, String nodeToAdd,Exchange exchange)
			throws ActivityEnricherException, TransformerException {
		String appendedDocument = null;
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = null;
		try {
			nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
			// Node has been added fetched from the pipline configuration
			Element node = xmlDocument.createElement(nodeToAdd);
			node.setTextContent((String)exchange.getIn().getHeader(res));
			// appended the element as a child to the node fetched from the
			// xpath
			nodeList.item(0).appendChild(node);
			appendedDocument = documentToString(xmlDocument);
		} catch (XPathExpressionException e) {
			throw new ActivityEnricherException("Unable to compile the xpath expression at index - "
					+ " when evaluating document - " + xmlDocument + "..", e);
		}

		return appendedDocument;
	}// ..end of the method

	/**
	 * locally used to instantiate the transformation factory and to process the
	 * transformation
	 * 
	 * @param inputXml
	 * @param xsltName
	 * @return transformed xml
	 * @throws ActivityEnricherException
	 * @throws TransformerException
	 */
	private String documentToString(Document xmlDocument) throws ActivityEnricherException, TransformerException {
		DOMSource domSource = new DOMSource(xmlDocument);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-16");
		transformer.transform(domSource, result);
		return writer.toString();
	}// ..end of the method
}