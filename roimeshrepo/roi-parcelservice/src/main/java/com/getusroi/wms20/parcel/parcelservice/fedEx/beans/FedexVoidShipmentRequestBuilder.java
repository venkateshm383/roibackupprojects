package com.getusroi.wms20.parcel.parcelservice.fedEx.beans;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.getusroi.eventframework.abstractbean.AbstractROICamelJDBCBean;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.wms20.parcel.parcelservice.exception.ParcelServiceException;
import com.getusroi.wms20.parcel.parcelservice.fedEx.constant.FedexConstant;
import com.getusroi.wms20.parcel.parcelservice.fedEx.service.FedexVoidShipServiceBuilder;

/**
 * Processor called from the fedex-void shipment-impl
 * 
 * @author BizRuntimeIT Services Pvt-Ltd
 */
public class FedexVoidShipmentRequestBuilder extends AbstractROICamelJDBCBean{
	private Logger log = Logger.getLogger(FedexVoidShipmentRequestBuilder.class.getName());

	/**
	 * the method called from the impl route
	 * @param exchange
	 * @throws ParcelServiceException
	 */
	@Override
	protected void processBean(Exchange exchange) throws ParcelServiceException{

		String transOne = exchange.getIn().getBody(String.class);
		log.debug("The incoming xml: "+transOne);
		FedexVoidShipServiceBuilder fedExRateServiceBuilder = new FedexVoidShipServiceBuilder();

		Map<String, Object> mapStore = new HashMap<String, Object>();
		mapStore.put(FedexConstant.XML, transOne);
		Map<String, Object> permaData = getPermastoreData(exchange);
		log.debug("The permastore data in void: "+permaData);
		String xmlOneTransformed = null;
		try {
			xmlOneTransformed = fedExRateServiceBuilder
					.getTranformedGenericXml(mapStore.get(FedexConstant.XML), permaData);
		} catch (ParserConfigurationException | SAXException | IOException | TransformerFactoryConfigurationError
				| TransformerException e) {
			throw new ParcelServiceException("unable to read the xml/transform the xml: ",e);
		}
		exchange.getIn().setBody(xmlOneTransformed);

	}//end of the method
	
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
