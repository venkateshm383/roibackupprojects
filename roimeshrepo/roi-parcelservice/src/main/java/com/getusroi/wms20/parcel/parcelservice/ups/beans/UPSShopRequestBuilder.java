package com.getusroi.wms20.parcel.parcelservice.ups.beans;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.parcel.parcelservice.exception.UpsException;
import com.getusroi.wms20.parcel.parcelservice.ups.constant.UPSConstant;
import com.getusroi.wms20.parcel.parcelservice.ups.service.UPSRateServiceBuilder;
import com.getusroi.wms20.parcel.parcelservice.ups.vo.Shipper;

public class UPSShopRequestBuilder {
	private Logger log = Logger.getLogger(UPSShopRequestBuilder.class.getName());
	/**
	 * used to process the rate request for ups
	 * @param exchange
	 * @throws UpsException
	 */
	public void processBean(Exchange exchange) throws UpsException {

		String transOne = exchange.getIn().getBody(String.class);
		UPSRateServiceBuilder upsRateServiceBuilder = new UPSRateServiceBuilder();
		Map<String, Object> mapStore = new HashMap<String, Object>();
		mapStore.put(UPSConstant.XML, transOne);
		int shipId = 0;
		try {
			shipId = upsRateServiceBuilder.getShipperIdfromXml(mapStore.get(UPSConstant.XML));
		} catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
			throw new UpsException("unable to get the shipperId from the xml: ", e);
		}
		mapStore.put(UPSConstant.SHIPPER_ID, shipId);
		Map<String, Object> permaData = getPermastoreData(exchange);
		log.debug("THe permadata in the upsRateBuilder: "+permaData);
		Shipper ship = upsRateServiceBuilder.setShipperDetails(mapStore, permaData);
		String xmlOneTransformed = null;
		try {
			xmlOneTransformed = upsRateServiceBuilder.getTranformedGenericXml(mapStore.get(UPSConstant.XML), ship,
					permaData);
		} catch (ParserConfigurationException | SAXException | IOException | TransformerFactoryConfigurationError
				| TransformerException e) {
			throw new UpsException("unable to transform/append the xml in ups: ", e);
		}
		exchange.getIn().setBody(xmlOneTransformed);

	}// end of the method
	
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
