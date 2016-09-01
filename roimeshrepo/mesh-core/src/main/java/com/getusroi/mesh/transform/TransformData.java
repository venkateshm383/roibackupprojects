package com.getusroi.mesh.transform;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.XML;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.mesh.generic.MeshGenericConstant;



public class TransformData {
	final Logger logger = LoggerFactory.getLogger(TransformData.class);
	
	/**
	 * Custom bean to marshal the XML string to org.Json Object
	 * @param exchange
	 * @throws JSONException
	 * @throws RequestDataTransformationException 
	 */
	public void marshalXmltoJson(Exchange exchange) throws RequestDataTransformationException{
		String exchngeContentType=(String) exchange.getIn().getHeader(Exchange.CONTENT_TYPE);
		if(exchngeContentType !=null && exchngeContentType.equalsIgnoreCase(MeshGenericConstant.HTTP_CONTENT_TYPE_ENCODED_KEY)){
			//request is comming for service request of sac as encoded value
			processRequestForEncodedData(exchange);
		}else{
			//request is for other service
			String bodyIn= exchange.getIn().getBody(String.class);
			//if body is null then check in custom header
			if(bodyIn !=null){
					createJSONRequestFromXMLString(bodyIn, exchange);
			}else{
				throw new RequestDataTransformationException("Unable to transform exchange body as no data availble in exchange body for service : "+exchange.getIn().getHeader(MeshHeaderConstant.SERVICETYPE_KEY)+
						", feature : "+exchange.getIn().getHeader(MeshHeaderConstant.FEATURE_KEY)+" and feature group : "+exchange.getIn().getHeader(MeshHeaderConstant.FEATURE_GROUP_KEY));
			}
		}	
	}//end of method marshalXmltoJson
	
	/**
	 * Custom bean to marshal the XML string to org.Json Object
	 * @param exchange
	 * @throws JSONException
	 */
	public void unmarshalJsonToXML(Exchange exchange) throws JSONException{
		String bodyIn= exchange.getIn().getBody(String.class);
		org.json.JSONObject jsonObject = new org.json.JSONObject(bodyIn);
		JSONArray jsonArray=(JSONArray)jsonObject.get(MeshHeaderConstant.DATA_KEY);
		org.json.JSONObject jobj=(org.json.JSONObject)jsonArray.get(0);
		String xmlString=XML.toString(jsonObject);
		logger.debug("unmarshalled - jsonObject to xml: "+xmlString);
		exchange.getIn().setBody(xmlString);
	}

	public String transformRequestData(Exchange exchange) throws Exception{
		logger.debug(".transformRequestData of Transform data");
		String body=exchange.getIn().getBody(String.class);
		String jsonstring=convertBodyToJSONString(body,exchange);
		return jsonstring;
	}
	
	private String convertBodyToJSONString(String body,Exchange exh) throws ParseException{
		JSONObject data=null;
		JSONObject jsonobj2=null;
		logger.debug("body : "+body);
		JSONParser parser = new JSONParser();
		JSONObject jobj=(JSONObject)parser.parse(body);
		logger.debug("json onj : "+jobj);
		logger.debug("jobj keys : "+jobj.keySet());
		//logger.info("staring : "+jobj.toJSONString());
		Iterator itr=jobj.keySet().iterator();
		String key=null;
		while (itr.hasNext()) {
			logger.debug("itr has elemt");
			 key=(String)itr.next();
			logger.debug("key : "+key);
			data=(JSONObject)jobj.get(key);
			logger.debug("key : "+key+", data : "+data);
			
		}
		if(data!=null){
			logger.debug("data is not null : "+data);
			
			JSONArray jsonArr=new JSONArray();
			jsonArr.put(data);
			logger.debug("json array : "+jsonArr.toString());
			 jsonobj2=new JSONObject();
			jsonobj2.put(MeshHeaderConstant.DATA_KEY,jsonArr);
			logger.debug("json obj : "+jsonobj2.toString());
		}else{
			return null;
		}
		exh.getIn().setHeader(MeshHeaderConstant.SERVICETYPE_KEY, key.toLowerCase().trim());
		return jsonobj2.toJSONString();
	}
	
	public String transformRestRequestData(Exchange exchange) throws Exception{
		String body=null;
		String httpMethod=(String)exchange.getIn().getHeader("CamelHttpMethod");
		if(httpMethod.equalsIgnoreCase("GET")){
			String httpQuery=(String)exchange.getIn().getHeader("CamelHttpQuery");
			Map<String,Object> mapdata=new HashMap<>();
			logger.debug("http query data : "+httpQuery);
			String[] splitQueryString=httpQuery.split("&");
			int noOfqueryEle=splitQueryString.length;
			for(int i=0;i<=noOfqueryEle-1;i++){
				String[] splitQueryString1=splitQueryString[i].split("=");
				mapdata.put(splitQueryString1[0].trim(),splitQueryString1[1].trim());
			}			
			logger.debug("map data : "+mapdata);
			
			org.json.JSONObject jobj=new org.json.JSONObject(mapdata.toString());
			logger.debug("jobj : "+jobj.toString());
			JSONArray jsonArr=new JSONArray();
			jsonArr.put(jobj);
			JSONObject jobj1=new JSONObject();
			jobj1.put(MeshHeaderConstant.DATA_KEY,jsonArr);
			logger.debug("jobj1 : "+jobj1.toJSONString());
			logger.debug("feature group : "+exchange.getIn().getHeader("featuregroup")+", feature : "+exchange.getIn().getHeader("feature")+", service : "+exchange.getIn().getHeader("servicetype"));
			return jobj1.toJSONString();
		}else if(httpMethod.equalsIgnoreCase("POST")){
			 body=exchange.getIn().getBody(String.class);
			 logger.debug("body for post request : "+body);
			logger.debug("feature group : "+exchange.getIn().getHeader("featuregroup")+", feature : "+exchange.getIn().getHeader("feature")+", service : "+exchange.getIn().getHeader("servicetype"));
		}else{
			logger.debug("UNSUPPORTED http method");
		}
		return body;
	}
	
	/**
	 * This method is used to see if encoded request data available in body or custom header and convert into required json data.
	 * @param exchange : Camel Exchange Object
	 * @throws RequestDataTransformationException
	 */
	private void processRequestForEncodedData(Exchange exchange) throws RequestDataTransformationException {
		String bodyIn= exchange.getIn().getBody(String.class);
		//if body is null then check in custom header
		if(bodyIn !=null){
				createJSONRequestFromXMLString(bodyIn, exchange);
			}else{
				String headerDataValue=(String)exchange.getIn().getHeader(MeshGenericConstant.ENCODED_REQUEST_DATA_KEY);
				logger.debug(" message value in header : "+headerDataValue);
				createJSONRequestFromXMLString(headerDataValue, exchange);

			}
	}//end of method processRequestForEncodedData
	
	/**
	 * This method is used to convert xml request data into JSON format
	 * @param xmlRequest : request XML string
	 * @param exchange : Camel Exchnage Object
	 * @throws RequestDataTransformationException
	 */
	private void createJSONRequestFromXMLString(String xmlRequest,Exchange exchange) throws RequestDataTransformationException{
		logger.debug(".createJSONRequestFromXMLString method of TransformData");
		org.json.JSONObject jsonObject;
		try {
			jsonObject = XML.toJSONObject(xmlRequest);
			logger.debug("marshalled - jsonObject from body: "+jsonObject.toString());
			JSONArray jsonArray=new JSONArray();
			jsonArray.put(jsonObject);
			org.json.JSONObject reqJsonObject=new org.json.JSONObject();
			reqJsonObject.put(MeshHeaderConstant.DATA_KEY, jsonArray);
			exchange.getIn().setBody(reqJsonObject.toString());
		} catch (JSONException e) {
			throw new RequestDataTransformationException("Unable to transform xml data "+xmlRequest+" into json ",e);
		}
		
	}//end of method createJSONRequestFromXMLString

}
