package com.key2act.signatureproxy;

import java.util.Map;

import org.apache.camel.Exchange;

import com.getusroi.mesh.MeshHeader;
import com.getusroi.staticconfig.StaticConfigInitializationException;
import com.getusroi.staticconfig.impl.AccessProtectionException;

public interface ISignatureGPHandlerService {

	/**
	 * method to work on permastore which will fetch the signature GP URL to
	 * post to and the Name of the signature GP Version which we will get from
	 * the tenant Name
	 * 
	 * @param exchange
	 * @param permaCacheObject
	 * @param meshHeader
	 * @param operationName
	 * @throws UnableToGetPermaDataException
	 * @throws AccessProtectionException 
	 * @throws StaticConfigInitializationException 
	 */
	public void setSignatureGPEndpointAndVersion(Exchange exchange,
			Map<String, Object> permaCacheObject,
			String operationName) throws UnableToGetPermaDataException, StaticConfigInitializationException, AccessProtectionException;

	/**
	 * method to get the pipeline Name which can be fetched from the permastore
	 * fetched from the values passed which is operationName and
	 * signatureGP_version
	 * 
	 * @param exchange
	 * @param operationName
	 * @param signatureGP_Version
	 * @throws UnableToGetPermaDataException
	 * @throws AccessProtectionException 
	 * @throws StaticConfigInitializationException 
	 */
	public void getXSLForSignatureGPVersionAndOperationName(
			Exchange exchange, String operationName, String signatureGP_Version)
			throws UnableToGetPermaDataException, StaticConfigInitializationException, AccessProtectionException;
}
