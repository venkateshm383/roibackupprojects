package com.getusroi.wms20.parcel.parcelservice.stamps.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.parcel.parcelservice.stamps.service.StampsShipmentServiceBuilder;
import com.getusroi.wms20.parcel.parcelservice.stamps.constant.StampsConstant;
import com.getusroi.wms20.parcel.parcelservice.stamps.vo.Shipper;

public class StampsShipmentRequestBuilder {

	Logger log = Logger.getLogger(StampsShipmentRequestBuilder.class.getName());

	public void processBean(Exchange exchange)  {

		String transOne = exchange.getIn().getBody(String.class);
		StampsShipmentServiceBuilder stampsShipmentServiceBuilder = new StampsShipmentServiceBuilder();
		Map<String, Object> mapStore = new HashMap<String, Object>();
		mapStore.put(StampsConstant.XML, transOne);
		Map<String, Object> permaData = getPermastoreData(exchange);
		log.debug("THe stamps permastoreData: "+permaData);
		int shipId = 0;
		try {
			shipId = stampsShipmentServiceBuilder.getShipperIdfromXml(mapStore.get(StampsConstant.XML));
		} catch (ParserConfigurationException e) {
			
		}
		mapStore.put(StampsConstant.SHIPPER_ID, shipId);
		Shipper ship = stampsShipmentServiceBuilder.setShipperDetails(mapStore, permaData);
		String xmlOneTransformed = null;
		try {
			xmlOneTransformed = stampsShipmentServiceBuilder.getTranformedGenericXml(mapStore.get(StampsConstant.XML), ship,
					permaData);
		} catch (TransformerFactoryConfigurationError
				| TransformerException e) {
			
		}
		exchange.getIn().setBody(xmlOneTransformed);
		log.debug("permadata in stamps create ship: " + permaData);
	}

	/**
	 * to get the permastoredata from the meshheader initialized in meshcore
	 * 
	 * @param exchange
	 * @return permastore object
	 */
	private Map<String, Object> getPermastoreData(Exchange exchange) {

		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> permaData = meshHeader.getPermadata();

		return permaData;
	}

}
