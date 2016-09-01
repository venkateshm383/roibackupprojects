package com.getusroi.wms20.parcel.parcelservice.stamps.beans;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.parcel.parcelservice.stamps.constant.StampsConstant;
import com.getusroi.wms20.parcel.parcelservice.stamps.service.StampsRateServiceBuilder;
import com.getusroi.wms20.parcel.parcelservice.stamps.vo.Shipper;

public class StampsShopRateRequestBuilder extends AbstractROICamelJDBCBean{

	Logger logger = Logger.getLogger(StampsShopRateRequestBuilder.class.getName());
	@Override
	protected void processBean(Exchange exchange) throws Exception {
		String transOne = exchange.getIn().getBody(String.class);
		StampsRateServiceBuilder stampsRateServiceBuilder = new StampsRateServiceBuilder();
		Map<String, Object> mapStore = new HashMap<String, Object>();
		mapStore.put(StampsConstant.XML, transOne);
		int shipId = 0;
		try {
			shipId = stampsRateServiceBuilder.getShipperIdfromXml(mapStore.get(StampsConstant.XML));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			
		}
		mapStore.put(StampsConstant.SHIPPER_ID, shipId);
		Map<String, Object> permaData = getPermastoreData(exchange);
		logger.debug("The permaData available in FedExRateService"+permaData);
		Shipper ship = stampsRateServiceBuilder.setShipperDetails(shipId, permaData);
		String xmlOneTransformed = null;
		try {
			xmlOneTransformed = stampsRateServiceBuilder.getTranformedGenericXml(mapStore.get(StampsConstant.XML), ship,
					permaData);
		} catch (TransformerFactoryConfigurationError e) {
			
		}
		exchange.getIn().setBody(xmlOneTransformed);
		
	}// end of bean
	
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
