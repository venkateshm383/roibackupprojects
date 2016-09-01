package com.getusroi.wms20.parcel.parcelservice.stamps.beans;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.parcel.parcelservice.stamps.constant.StampsConstant;
import com.getusroi.wms20.parcel.parcelservice.stamps.service.StampsVoidShipmentServiceBuilder;

public class StampsVoidShipmentRequestBuilder extends AbstractROICamelJDBCBean{

	final static Logger logger = LoggerFactory.getLogger(StampsVoidShipmentRequestBuilder.class);

	@Override
	protected void processBean(Exchange exchange) throws Exception {
		String transOne = exchange.getIn().getBody(String.class);		
		StampsVoidShipmentServiceBuilder stampsvoidRequestBuilder = new StampsVoidShipmentServiceBuilder();
		Map<String, Object> mapStore = new HashMap<String, Object>();
		mapStore.put(StampsConstant.XML, transOne);
		String xmlOneTransformed = null;
		Map<String, Object> permaData = getPermastoreData(exchange);
		try {
			xmlOneTransformed = stampsvoidRequestBuilder
					.getTranformedGenericXml(mapStore.get(StampsConstant.XML), permaData);
			logger.debug("xml string inside bean: "+xmlOneTransformed);
		} catch (ParserConfigurationException | SAXException | IOException | TransformerFactoryConfigurationError
				| TransformerException e) {
		}
		exchange.getIn().setBody(xmlOneTransformed);

	}
	
	/**
	 * to get the permastoredata from the meshheader initialized in meshcore
	 * @param exchange
	 * @return permastore object
	 */
	private Map<String, Object> getPermastoreData(Exchange exchange){
		
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> permaData = meshHeader.getPermadata();
		
		return permaData;
	}

}
