package com.key2act.timetrackerproxy.util;

import static com.key2act.timetrackerproxy.util.TimeTrackerProxyConstants.*;

import java.util.Map;
import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.config.RequestContext;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.staticconfig.StaticConfigInitializationException;
import com.getusroi.staticconfig.impl.AccessProtectionException;
import com.getusroi.staticconfig.util.LocalfileUtil;
import com.key2act.timetrackerproxy.service.IllegalRequestException;
import com.key2act.timetrackerproxy.service.InvalidXSLFilePath;
import com.key2act.timetrackerproxy.service.LookUpRequestInvalidException;
import com.key2act.timetrackerproxy.service.PermaStoreParserExceptionForTenantData;
import com.key2act.timetrackerproxy.service.TimeTrackLookUpServiceException;

public class WebServiceDataConfigurerUtil {

	static Logger logger = LoggerFactory.getLogger(WebServiceDataConfigurerUtil.class);

	/**
	 * method to load the
	 * tenant-version-WebServiceMappingEndpoint-connectionString-mapping
	 * permastore and get the WebService Endpoint and connectionString and
	 * version, the method sets the webService Endpoint into Header and passes
	 * the connectionString and version to
	 * loadPermaDataForVersionAndSetXslInHeader method for further processing
	 * 
	 * @param exchange
	 * @param tenantID
	 * @param serviceType
	 * @throws TimeTrackLookUpServiceException
	 */
	public void loadPermaDataForTenantAndSetWSEndpointInHeader(Exchange exchange, String tenantID, String serviceType)
			throws TimeTrackLookUpServiceException {
		JSONObject permaJsonObject = null;
		MeshHeader meshHeader = (MeshHeader) exchange.getIn().getHeader(MeshHeaderConstant.MESH_HEADER_KEY);
		RequestContext reqCtx = meshHeader.getRequestContext();
		Map<String, Object> permaCachedata = meshHeader.getPermadata();
		Object permaJsonObjectInString = permaCachedata.get(TENANT_TO_VERSION_WSURL_MAPPING_PERMA_CONFIG_KEY);
		try {
			permaJsonObject = new JSONObject(permaJsonObjectInString.toString());
			permaJsonObject = permaJsonObject.getJSONObject(tenantID);
		} catch (JSONException e) {
			try {
				throw new LookUpRequestInvalidException("TenantID not found in the permaStore");
			} catch (LookUpRequestInvalidException e1) {
				throw new TimeTrackLookUpServiceException("PermaStore is not configured Correctly");
			}
		}
		try {
			exchange.getIn().setHeader(WEBSERVICE_ENDPOINT_PERMA_KEY,
					permaJsonObject.getString(WEBSERVICE_ENDPOINT_PERMA_KEY));
			String version = permaJsonObject.getString(VERSION_PERMA_KEY);
			String connectionString = permaJsonObject.getString(CONNECTION_STRING_PERMA_KEY);
			loadPermaDataForVersionAndSetXslInHeader(exchange, version, connectionString, permaCachedata, serviceType,
					reqCtx);
		} catch (JSONException e) {
			try {
				throw new PermaStoreParserExceptionForTenantData(
						"The ConnectionString or the Web Service URL in the permaStore is not configured Properly with the tenantName provided in the request");
			} catch (PermaStoreParserExceptionForTenantData e1) {
				throw new TimeTrackLookUpServiceException("PermaStore is not configure correctly.");
			}
		}
	}

	/**
	 * method to load the data from the VERSION_TO_XSL_CONNECTION_STRING
	 * permaStore after which using the version we will get the transformerXSL
	 * FileNames from which calling the LocalFileUtil's getStaticFilePath method
	 * we can get the full path of that file and set it into the exchange's
	 * header. And then converting the exchange Body into from json to xml, so
	 * that XSL transformation can be performed
	 * 
	 * @throws TimeTrackLookUpServiceException
	 */
	public void loadPermaDataForVersionAndSetXslInHeader(Exchange exchange, String version, String connectionString,
			Map<String, Object> permaCachedata, String serviceType, RequestContext reqCtx)
			throws TimeTrackLookUpServiceException {
		Object permaObject = permaCachedata.get(VERSION_TO_XSL_CONNECTION_STRING);
		try {
			JSONObject permaJsonObject = new JSONObject(permaObject.toString());
			permaJsonObject = permaJsonObject.getJSONObject(version);
			permaJsonObject = permaJsonObject.getJSONObject(serviceType);

			LocalfileUtil localfileUtil = new LocalfileUtil();
			try {
				String requestXsl = localfileUtil.getStaticFilePath(reqCtx,
						permaJsonObject.getString(REQUEST_TRANSFORMER_XSL_FILE_PERMA_KEY));
				String responseXsl = localfileUtil.getStaticFilePath(reqCtx,
						permaJsonObject.getString(RESPONSE_TRANSFORMER_XSL_FILE_PERMA_KEY));
				logger.debug("requestXsl : " + requestXsl);
				logger.debug("responseXsl : " + responseXsl);
				exchange.getOut().setHeader(REQUEST_TRANSFORMER_XSL_FILE_PERMA_KEY, requestXsl);
				exchange.getOut().setHeader(RESPONSE_TRANSFORMER_XSL_FILE_PERMA_KEY, responseXsl);
			} catch (StaticConfigInitializationException e) {
				try {
					throw new InvalidXSLFilePath("The filepath fetched from static Config Base Directory is not valid");
				} catch (InvalidXSLFilePath e1) {
					throw new TimeTrackLookUpServiceException("The xsl file is not valid");
				}
			} catch (AccessProtectionException e) {
				throw new TimeTrackLookUpServiceException("The xsl Files doesnt have the access rights");
			}
			JSONObject connectionStringJsonObject = new JSONObject();
			connectionStringJsonObject.put(CONNECTION_STRING_PERMA_KEY, connectionString);
			JSONObject serviceDataJsonObject = new JSONObject();
			serviceDataJsonObject.put(SERVICE_DATA_KEY, connectionStringJsonObject);
			exchange.getOut().setBody(XML.toString(serviceDataJsonObject));
		} catch (JSONException e) {
			try {
				throw new PermaStoreParserExceptionForTenantData(
						"Unable to get the the Required Key from the PermaStore to create a JSON object for the Services");
			} catch (PermaStoreParserExceptionForTenantData e1) {
				throw new TimeTrackLookUpServiceException("The PermaStore is not Configured Properly");
			}
		}
	}
}
