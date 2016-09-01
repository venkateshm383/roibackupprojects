package com.getusroi.wms20.parcel.parcelservice.fedEx.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.parcel.parcelservice.exception.ParcelServiceException;
import com.getusroi.wms20.parcel.parcelservice.fedEx.constant.FedexConstant;
import com.getusroi.wms20.parcel.parcelservice.fedEx.service.FedexShipmentServiceBuilder;
import com.getusroi.wms20.parcel.parcelservice.fedEx.vo.Shipper;

/**
 * Processor called from fedEx-Create-Shipment
 * 
 * @author BizRuntimeIT Services Pvt-Ltd
 */
public class FedexShipmentBuilder extends AbstractROICamelJDBCBean{
	private Logger log = Logger.getLogger(FedexShipmentBuilder.class.getName());

	/**
	 * the method which is called from the camel fedEx shipment impl route to
	 * process shipment
	 * 
	 * @param exchange
	 * @throws ParcelServiceException
	 */
	@Override
	protected void processBean(Exchange exchange) throws ParcelServiceException {

		String transOne = exchange.getIn().getBody(String.class);
		log.debug("Xml as String: " + transOne);
		Map<String, Object> mapStore = new HashMap<String, Object>();
		mapStore.put(FedexConstant.XML, transOne);
		Map<String, Object> permaData = getPermastoreData(exchange);
		log.debug("PermastoreData: " + permaData);
		String xmlOneTransformed = processxmlTransformation(mapStore, permaData);
		exchange.getIn().setBody(xmlOneTransformed);

	}// end of the method
	
	/**
	 * an aid to transform the incoming xml request to the needed RawXmlFormat
	 * @param mapStore
	 * @param permaData
	 * @return transformedXmlString~from DocumentBuilder
	 * @throws ParcelServiceException
	 */
	private String processxmlTransformation(Map<String, Object> mapStore, Map<String, Object> permaData) throws ParcelServiceException{
		FedexShipmentServiceBuilder fedExShipmentBuilder = new FedexShipmentServiceBuilder();
		int shipId = 0;
		try {
			shipId = fedExShipmentBuilder.getShipperIdfromXml(mapStore.get(FedexConstant.XML));
		} catch (ParserConfigurationException e) {
			throw new ParcelServiceException("Unable to get the shipperId from the Xmlinput : ", e);
		}
		mapStore.put(FedexConstant.SHIPPER_ID, shipId);
		Shipper ship = fedExShipmentBuilder.setShipperDetails(shipId, permaData);
		String xmlOneTransformed = null;
		try {
			xmlOneTransformed = fedExShipmentBuilder.getTranformedGenericXml(mapStore.get(FedexConstant.XML), ship,
					permaData);
			log.debug("The transformed xmlFedEx: : "+xmlOneTransformed);
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			throw new ParcelServiceException("Unable to get the xml Transformed: ", e);
		}
		return xmlOneTransformed;
	}

	/**
	 * to get the permastoredata from the meshheader initialized in meshcore
	 * @param exchange
	 * @return permastore object
	 */
	private Map<String, Object> getPermastoreData(Exchange exchange) {

		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> permaData = meshHeader.getPermadata();

		return permaData;
	}
}
