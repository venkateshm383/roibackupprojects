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
import com.getusroi.wms20.parcel.parcelservice.fedEx.service.FedexRateServiceBuilder;
import com.getusroi.wms20.parcel.parcelservice.fedEx.vo.Shipper;

/**
 * Processor called from the fedEx-Rates-Impl
 * 
 * @author BizRuntimeIT Services Pvt-Ltd
 */
public class FedexShopRequestBuilder extends AbstractROICamelJDBCBean {
	private Logger log = Logger.getLogger(FedexShopRequestBuilder.class.getName());

	/**
	 * called from the camel route to process the rates
	 * 
	 * @param exchange
	 * @throws ParcelServiceException
	 */
	@Override
	protected void processBean(Exchange exchange) throws ParcelServiceException {

		logger.debug(".fedExRequestBuilderBean..");
		String transOne = exchange.getIn().getBody(String.class);
		Map<String, Object> mapStore = new HashMap<String, Object>();
		mapStore.put(FedexConstant.XML, transOne);
		Map<String, Object> permaData = getPermastoreData(exchange);
		logger.debug("permaData in fedEximpl Bean: "+permaData);
		String xmlOneTransformed = processXmlTransformation(permaData, mapStore);
		logger.debug("the xml after transformation: "+xmlOneTransformed);
		exchange.getIn().setBody(xmlOneTransformed);

	}// ..end of the method

	public void fedexTransformer(Exchange exchange) throws ParcelServiceException{
		logger.debug(".fedExRequestBuilderBean..");
		String transOne = exchange.getIn().getBody(String.class);
		Map<String, Object> mapStore = new HashMap<String, Object>();
		mapStore.put(FedexConstant.XML, transOne);
		Map<String, Object> permaData = getPermastoreData(exchange);
		logger.debug("permaData in fedEximpl Bean: "+permaData);
		String xmlOneTransformed = processXmlTransformation(permaData, mapStore);
		logger.debug("the xml after transformation: "+xmlOneTransformed);
		exchange.getIn().setBody(xmlOneTransformed);
	}
	
	/**
	 * just to aid the transformation of the input xml as it is required for the
	 * GenericXml
	 * 
	 * @param permaData
	 * @param mapStore
	 * @return string transformedXml
	 * @throws ParcelServiceException
	 */
	private String processXmlTransformation(Map<String, Object> permaData, Map<String, Object> mapStore)
			throws ParcelServiceException {
		FedexRateServiceBuilder fedExRateServiceBuilder = new FedexRateServiceBuilder();
		int shipId;
		try {
			shipId = fedExRateServiceBuilder.getShipperIdfromXml(mapStore.get(FedexConstant.XML));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new ParcelServiceException("unable to parse the xml to get the shipperId ", e);
		}
		mapStore.put(FedexConstant.SHIPPER_ID, shipId);

		log.debug("The permaData available in FedExRateService" + permaData);
		Shipper ship = fedExRateServiceBuilder.setShipperDetails(shipId, permaData);
		String xmlOneTransformed = null;
		try {
			xmlOneTransformed = fedExRateServiceBuilder.getTranformedGenericXml(mapStore.get(FedexConstant.XML), ship,
					permaData);
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			throw new ParcelServiceException("unable to transform/append to the xml. Exception occured: ", e);
		}
		return xmlOneTransformed;
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
