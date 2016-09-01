package com.getusroi.eventframework.dispatcher.transformer;

import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.getusroi.config.RequestContext;
import com.getusroi.eventframework.event.ROIEvent;

/**
 * This class is to Generate Custom event xml from Event Object
 * @author Deepali
 *
 */
public class XmlTransformerHelper {
	protected static final Logger logger = LoggerFactory.getLogger(XmlTransformerHelper.class);

	/**
	 * This method is to create custom event xml using xslt
	 * @param eventxml : Event xml in String format
	 * @return custom event xml in String format
	 */
	public  String createCustomXml(String eventxml,String xslname,String xsltAsString) {
		logger.debug("inside createCustomXml() in ROIEventXmlTransformer");
		//#TODO PROPER Exception Handling
		StringWriter stringwriter=new StringWriter();
		
		 try {
		    	Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new StringReader(xsltAsString)));

		    //	InputStream inputstr=XmlTransformerHelper.class.getClassLoader().getResourceAsStream(xslname);
		    //	Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(inputstr));
		    	
				try {
					transformer.transform(new StreamSource(new StringReader(eventxml)),new StreamResult(stringwriter));
				} catch (TransformerException e) {
					
					//TODO will create custom exception here
					logger.error("unable to transform event xml to xustom event xml : "+e.getMessage());
				}
			} catch (TransformerConfigurationException e) {
				//TODO will create custom exception here
				logger.error("unable to configure Transformer from classpath for resource : "+xslname+" -- "+e.getMessage());
			} catch (TransformerFactoryConfigurationError e) {
				
				//TODO will create custom exception here

				logger.error("unable to get the TranformerConfiguration Object : "+e.getMessage());
			}
		
		 return stringwriter.toString();
	}



/**
 * This method is to convert Event Object to xml format
 * @param event : ROIEvent Object
 * @return : event xml in String format
 */
	public  String convertEventObjectToXml(ROIEvent event){
		logger.debug("inside convertEventToXml method in XmlTranformerHelper");
		Map<String,Serializable> eventParam=event.getEventParam();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("ROIEvent");
		
		doc.appendChild(rootElement);
		
		// event elements
		Element eventid = doc.createElement("event");
		rootElement.appendChild(eventid);

		// set attribute to event element
		Attr attr = doc.createAttribute("id");
		attr.setValue(event.getEventId());
		eventid.setAttributeNode(attr);
		
		// tenant elements
		Element tenantid = doc.createElement("tenant");
		rootElement.appendChild(tenantid);

		// set attribute to tenant element
		RequestContext reqCtx=event.getRequestContext();
		Attr contextAttr = doc.createAttribute("tid");
		contextAttr.setValue(reqCtx.getTenantId());
		tenantid.setAttributeNode(contextAttr);
		
	// site elements
			Element site = doc.createElement("site");
			rootElement.appendChild(site);

			// set attribute to site element
			Attr siteAttr = doc.createAttribute("sid");
			siteAttr.setValue(reqCtx.getSiteId());
			site.setAttributeNode(siteAttr);

			
		// feature group elements
					Element featuregroup = doc.createElement("featuregroup");
					rootElement.appendChild(featuregroup);

					// set attribute to feature group element
					Attr featuregroupAttr = doc.createAttribute("groupid");
					featuregroupAttr.setValue(reqCtx.getFeatureGroup());
					featuregroup.setAttributeNode(featuregroupAttr);
				
					
					// feature  elements
					Element feature = doc.createElement("feature");
					rootElement.appendChild(feature);

					// set attribute to feature  element
					Attr featureAttr = doc.createAttribute("featureid");
					featureAttr.setValue(reqCtx.getFeatureName());
					feature.setAttributeNode(featureAttr);

		
		
		//converting documnt into documnet source
		DOMSource source=new DOMSource(doc);
		logger.debug("exiting convertEventToXml method in XmlTranformerHelper");

		return getEventXmlAsString(source);
	
	}

	/**
	 * This method is to get event xml in String format
	 * @param source : DOMSource Object
	 * @return : event xml in String format
	 */
	private   String getEventXmlAsString(DOMSource source){
		
		logger.debug("inside  getEventXmlInString() in ROIEventXMlTransformer ");
		
		StringWriter stringwriter=new StringWriter();
		
		try {
		    	//getting the object of transformer object
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				try {
					transformer.transform(source,new StreamResult(stringwriter));
				} catch (TransformerException e) {
					logger.error("error in tranforming roievent xml into String writer object");
				}
			} catch (TransformerConfigurationException e) {
				logger.error("error in creating the object for transformer");
			} catch (TransformerFactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		
		
		return stringwriter.toString();
	}


/**
 * This method is to get the child nodes for the element
 * @param obj : Object
 * @param doc : Document Object
 * @param ele : Element Object
 * @return Element 
 * @throws IllegalAccessException
 */
	private  Element getChildNodes(Object obj, Document doc,
			Element ele) throws IllegalAccessException {
		
		//getting the simple name of class in object
		String simpleClassName=obj.getClass().getSimpleName();
		
		//getting the fully qualified class name in object
		String fqcn=obj.getClass().getName();
		
		logger.debug("FQCN : "+fqcn);
		
		//loading class and methods
		Class<?> classobj = null;
		
		try {
			classobj = Class.forName(fqcn);
		} catch (ClassNotFoundException e1) {
			logger.error("unable to load the class of name : "+fqcn+" beacuse : "+e1.getMessage());
		}
		
		//get all the declare method from loaded class
		Method[] methods=classobj.getDeclaredMethods();
		
		
		//creating element using simple name of a class
		Element innerdata = doc.createElement(simpleClassName);
    	ele.appendChild(innerdata);

	
	
		//getting  methods in the class loaded dynamically
		for(Method method : methods){
			String allmethod=method.getName();
			
			
			//getting only those method which starts from get
			if(allmethod.startsWith("get")){
				
				//getting the parameters method take
				Type[] methodParamType=method.getGenericParameterTypes();
				
				//checking if getter method doesnot take any param
				if(methodParamType.length == 0){
				try {
					Object methodObj=method.invoke(obj);
					
					logger.debug("method object : "+methodObj);
					
					if(methodObj instanceof String){
						logger.debug("method return type is string");
						String strdata=(String)methodObj;		
						Element innerdatachild = doc.createElement("childnode");
						innerdatachild.appendChild(doc.createTextNode(strdata));
						innerdata.appendChild(innerdatachild);

					}else if(methodObj instanceof ArrayList<?>){
						logger.debug("method return type is ArrayList");

						List<?> loyobj=(ArrayList<?>)methodObj;
						for(Object objjj : loyobj){
							getChildNodes(objjj,doc,innerdata);	
						}
						
					}else{
						logger.debug("inside else "+methodObj);
						if(methodObj !=null)
						getChildNodes(methodObj,doc,innerdata);
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}//no. of params method take zero
			}
		}
		return innerdata;

	
	}
	
//	public static void main(String[] args) {
//		ROIEvent event=new ROIEvent("biz");
//		Map<String,Serializable> eventParam=new HashMap<>();
//		eventParam.put("company","bizrunitme");
//		eventParam.put("cid","BIZ007");
//		eventParam.put("name","deepali");
//		
//
//		EventMessage msg1=new EventMessage();
//		msg1.setEventid("HRO");
//		msg1.setTenantid("dssd");
//		
//		
//		BizMessage msg=new BizMessage();
//		msg.setEventid(event.getEventId());
//		msg.setTenantid("fgfdg");
//		List<Object> objecteventList=new ArrayList<Object>();
//		objecteventList.add(msg1);
//		msg.setEventparamlist(objecteventList);
//		
//		eventParam.put("customMessage",msg);
//		
//		event.setEventParam(eventParam);
//		XmlTransformerHelper xmlTransformerHelper=new XmlTransformerHelper();
//		String xmlString=xmlTransformerHelper.convertEventObjectToXml(event);
//		logger.info("xml string : "+xmlString);
//		
//		String customString =xmlTransformerHelper.createCustomXml(xmlString,"eventxmlTransformation.xsl");
//		logger.info("custom String : "+customString);
//	}
}
