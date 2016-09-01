package com.getusroi.wms20.parcel.parcelservice.ups.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.parcel.parcelservice.exception.UpsException;
import com.getusroi.wms20.parcel.parcelservice.ups.constant.UPSConstant;
import com.getusroi.wms20.parcel.parcelservice.ups.service.UPSShipmentServiceBuilder;
import com.getusroi.wms20.parcel.parcelservice.ups.vo.Shipper;

public class UPSShipmentRequestBuilder {

	private Logger log = Logger.getLogger(UPSShipmentRequestBuilder.class.getName());

	public void processBean(Exchange exchange) throws UpsException {

		String transOne = exchange.getIn().getBody(String.class);
		UPSShipmentServiceBuilder upsShipmentServiceBuilder = new UPSShipmentServiceBuilder();
		Map<String, Object> mapStore = new HashMap<String, Object>();
		mapStore.put(UPSConstant.XML, transOne);
		Map<String, Object> permaData = getPermastoreData(exchange);
		int shipId;
		try {
			shipId = upsShipmentServiceBuilder.getShipperIdfromXml(mapStore.get(UPSConstant.XML));
		} catch (ParserConfigurationException e) {
			throw new UpsException("Unable to parse the xml input to get the ShipperId: ", e);
		}
		mapStore.put(UPSConstant.SHIPPER_ID, shipId);
		Shipper ship = upsShipmentServiceBuilder.setShipperDetails(mapStore, permaData);
		String xmlOneTransformed;
		try {
			xmlOneTransformed = upsShipmentServiceBuilder.getTranformedGenericXml(mapStore.get(UPSConstant.XML), ship,
					permaData);
		} catch (TransformerFactoryConfigurationError
				| TransformerException e) {
			throw new UpsException("Unable to transform the generic input to the Raw format: ", e);
		}
		exchange.getIn().setBody(xmlOneTransformed);
		log.debug("permadata in ups create ship: " + permaData);
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
