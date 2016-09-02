package com.key2act.signatureproxy.service;
import static com.key2act.signatureproxy.util.SignatureGPConstants.*;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.Table;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mvel2.ast.Sign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datastax.driver.core.Cluster;
import com.getusroi.eventframework.abstractbean.AbstractCassandraBean;
import com.getusroi.eventframework.abstractbean.util.CassandraClusterException;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.staticconfig.StaticConfigInitializationException;
import com.getusroi.staticconfig.impl.AccessProtectionException;
import com.key2act.signatureproxy.InvalidRequestExeption;
import com.key2act.signatureproxy.UnableToGetPermaDataException;
import com.key2act.signatureproxy.UnableToParseRequestException;
import com.key2act.signatureproxy.UnableToparseRequestDataException;
import com.key2act.signatureproxy.impl.SignatureGPHandlerServiceImpl;
import com.key2act.signatureproxy.util.SignatureGPConstants;
import com.key2act.signatureproxy.util.ValidateRequestBean;

public class SignatureGPProcessBean{
	Logger logger = LoggerFactory.getLogger(SignatureGPProcessBean.class);

	/**
	 * method to load PermaData to get the signatureGP Version from tenant Name
	 * being fetched from the MeshHeader #TODO need to verify, the tenantName
	 * should be coming from the request itself.
	 * 
	 * @param exchange
	 * @throws UnableToGetPermaDataException
	 * @throws AccessProtectionException
	 * @throws StaticConfigInitializationException
	 * @throws UnableToparseRequestDataException
	 */
	@SuppressWarnings("unchecked")
	public void getSignatureGPVersionFromTenant(Exchange exchange, String operationName)
			throws UnableToGetPermaDataException, StaticConfigInitializationException, AccessProtectionException,
			UnableToparseRequestDataException {
		Map<String, Object> permaCacheObject;
		String requestObject = exchange.getIn().getBody(String.class);
		try {
			JSONObject requestJsonObject = new JSONObject(requestObject);
			requestJsonObject = requestJsonObject.getJSONArray(MeshHeaderConstant.DATA_KEY).getJSONObject(0);
			requestJsonObject = requestJsonObject.getJSONObject(CREATE_CALL_KEY);
			String tenantID = requestJsonObject.getString(TENANT_NAME);
			ValidateRequestBean validateRequestBean = new ValidateRequestBean();
			boolean isValidate = validateRequestBean.validateRequestForCreateCall(requestJsonObject);
			if (!isValidate) {
				throw new UnableToparseRequestDataException(
						"The request Formed is not Properly constructed : The request Data must contain customernumber, addresscode, technician, dateofservicecall, equipmentid, typecallshort, division, calluserid, callsource, aggregator .. make sure these entities are available in the request");
			}
			// Setting the tenantName into the Header for further processing to
			// get the data from the permastore
			exchange.getIn().setHeader(TENANT_NAME, tenantID);
		} catch (JSONException e) {
			throw new UnableToparseRequestDataException(
					"The request is not Properly Constructed, Please Enter The data inside CreateCall Tag");
		}

		// Loading the permaCache Data
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		Map<String, Object> permaCacheObjectInMap = meshHeader.getPermadata();
		try {
			permaCacheObject = (Map<String, Object>) permaCacheObjectInMap
					.get(TENANT_TO_SIGNATURE_GP_VERSION_AND_ENDPOINT_PERMAKEY);
		} catch (Exception e) {
			throw new UnableToGetPermaDataException("The PermaStore Configuration : "
					+ TENANT_TO_SIGNATURE_GP_VERSION_AND_ENDPOINT_PERMAKEY + "is not found");
		}

		// passing the PermaCacheData along with the exchange and operationName
		// [which is hard coded in the route level specific to the method] to
		// setSignatureGPEndpointAndVersion where WS Endpoint Soap Action and
		// Version Name can be determined
		SignatureGPHandlerServiceImpl signatureGPHandlerServiceImpl = new SignatureGPHandlerServiceImpl();
		signatureGPHandlerServiceImpl.setSignatureGPEndpointAndVersion(exchange, permaCacheObject, operationName);
	}
}