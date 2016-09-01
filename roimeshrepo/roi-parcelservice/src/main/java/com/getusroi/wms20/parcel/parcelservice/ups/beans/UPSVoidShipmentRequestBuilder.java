package com.getusroi.wms20.parcel.parcelservice.ups.beans;

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

import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.parcel.parcelservice.exception.UpsException;
import com.getusroi.wms20.parcel.parcelservice.ups.constant.UPSConstant;
import com.getusroi.wms20.parcel.parcelservice.ups.service.UPSVoidShipServiceBuilder;

public class UPSVoidShipmentRequestBuilder {

	/**
	 * the method called from the impl route
	 * @param exchange
	 * @throws UpsException
	 */
	private Logger logger = LoggerFactory.getLogger(UPSVoidShipmentRequestBuilder.class);

	public void processBean(Exchange exchange) throws UpsException {

		String transOne = exchange.getIn().getBody(String.class);		
		UPSVoidShipServiceBuilder upsRateServiceBuilder = new UPSVoidShipServiceBuilder();
		Map<String, Object> mapStore = new HashMap<String, Object>();
		mapStore.put(UPSConstant.XML, transOne);
		String xmlOneTransformed = null;
		Map<String, Object> permaData = getPermastoreData(exchange);
		try {
			xmlOneTransformed = upsRateServiceBuilder
					.getTranformedGenericXml(mapStore.get(UPSConstant.XML), permaData);
			logger.debug("xml string inside bean: "+xmlOneTransformed);
		} catch (ParserConfigurationException | SAXException | IOException | TransformerFactoryConfigurationError
				| TransformerException e) {
			throw new UpsException("Unable to read/transform the xml and store to map: ",e);
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
