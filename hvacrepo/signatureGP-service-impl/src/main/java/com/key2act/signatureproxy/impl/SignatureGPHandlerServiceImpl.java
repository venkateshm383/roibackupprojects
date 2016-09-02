package com.key2act.signatureproxy.impl;

import java.util.Map;

import org.apache.camel.Exchange;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.config.RequestContext;
import com.getusroi.featuremetainfo.jaxb.Feature;
import com.getusroi.integrationfwk.activities.bean.FtlEnricher;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.staticconfig.StaticConfigInitializationException;
import com.getusroi.staticconfig.impl.AccessProtectionException;
import com.getusroi.staticconfig.util.LocalfileUtil;
import com.key2act.signatureproxy.ISignatureGPHandlerService;
import com.key2act.signatureproxy.UnableToGetPermaDataException;
import com.key2act.signatureproxy.UnableToparseRequestDataException;
import com.key2act.signatureproxy.service.SignatureGPProcessBean;
import com.key2act.signatureproxy.util.SignatureGPConstants;

public class SignatureGPHandlerServiceImpl implements ISignatureGPHandlerService {
	private Logger logger = LoggerFactory.getLogger(SignatureGPHandlerServiceImpl.class.getName());

	/**
	 * method to get the tenantName from the meshHeader and store the url in the
	 * header when passed with the permaKeyObject. also get the signatureVersion
	 * and pass it on for further processing
	 * 
	 * @throws UnableToGetPermaDataException
	 * @throws AccessProtectionException
	 * @throws StaticConfigInitializationException
	 */
	public void setSignatureGPEndpointAndVersion(Exchange exchange, Map<String, Object> permaCacheObject,
			String operationName)
			throws UnableToGetPermaDataException, StaticConfigInitializationException, AccessProtectionException {
		// got the name of tenant and search it in the permaStore Configuration
		// as JsonObject
		String tenantName = (String) exchange.getIn().getHeader(SignatureGPConstants.TENANT_NAME);
		JSONObject tenantJsonObject = (JSONObject) permaCacheObject.get(tenantName);
		// get the Keys and store it in Headers
		exchange.getIn().setHeader(SignatureGPConstants.SIGNATUREGP_WEBSERVICE_ENDPOINT,
				tenantJsonObject.get(SignatureGPConstants.SIGNATUREGP_WEBSERVICE_ENDPOINT));
		exchange.getIn().setHeader(SignatureGPConstants.SOAP_ACTION_KEY, tenantJsonObject.get(SignatureGPConstants.SOAP_ACTION_KEY));
		getXSLForSignatureGPVersionAndOperationName(exchange, operationName,
				(String) tenantJsonObject.get(SignatureGPConstants.SIGNATUREGP_VERSION_KEY));
	}

	/**
	 * method to get the Operation Name and Version name and fetch the pipeline
	 * Name from the permaStore Configuration, check in permaStore of the same
	 * version's JSONOBJECT and get the pipelineName and set it in the header
	 * 
	 * @throws UnableToGetPermaDataException
	 * @throws AccessProtectionException
	 * @throws StaticConfigInitializationException
	 * @throws UnableToparseRequestDataException
	 */
	@SuppressWarnings("unchecked")
	public void getXSLForSignatureGPVersionAndOperationName(Exchange exchange, String operationName,
			String signatureGP_Version)
			throws UnableToGetPermaDataException, StaticConfigInitializationException, AccessProtectionException {
		Map<String, Object> permaCacheObject;
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> permaCacheObjectInMap = meshHeader.getPermadata();
		try {
			permaCacheObject = (Map<String, Object>) permaCacheObjectInMap
					.get(SignatureGPConstants.SIGNATURE_GP_VERSION_TO_XSLT_PERMAKEY);
		} catch (Exception e) {
			throw new UnableToGetPermaDataException("The PermaStore Configuration : "
					+ SignatureGPConstants.SIGNATURE_GP_VERSION_TO_XSLT_PERMAKEY + "is not found");
		}
		JSONObject operationConfigurer = (JSONObject) permaCacheObject.get(signatureGP_Version);
		// get the name of the xsl associated with the OperationName
		Object xslName = operationConfigurer.get(operationName);
		JSONObject reqTrnfrmer = new JSONObject();
		reqTrnfrmer = (JSONObject) xslName;
		LocalfileUtil localfileUtil = new LocalfileUtil();
		RequestContext requestContext = meshHeader.getRequestContext();
		String xslfilepath = localfileUtil.getStaticFilePath(requestContext,
				reqTrnfrmer.get("RequestTransformerFile").toString());
		exchange.getIn().setHeader(SignatureGPConstants.OPERATION_BASED_XSL_FILE_KEY, xslfilepath);
		logger.debug("xsltFilePath : " + xslfilepath);
		exchange.getIn().setHeader("org.restlet.http.headers", "");
	}
}