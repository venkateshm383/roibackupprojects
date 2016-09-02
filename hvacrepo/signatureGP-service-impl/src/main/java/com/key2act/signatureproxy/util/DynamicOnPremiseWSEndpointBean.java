package com.key2act.signatureproxy.util;

import org.apache.camel.Exchange;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * This class has been written because for every Org we will have different Web
 * Service Endpoint, since Cxf Endpoint cannot be generated dynamically we are
 * using this class to play as ana actor to Post to the Web Service
 * 
 * @author bizruntime
 */

public class DynamicOnPremiseWSEndpointBean {

	private final String USER_AGENT = "Mozilla/5.0";

	// HTTP POST request
	public void sendPost(Exchange exchange) throws Exception {
		String request = exchange.getIn().getBody(String.class);
		StringEntity stringEntity = new StringEntity(request, "utf-8");
		stringEntity.setChunked(true);

		String url = (String) exchange.getIn().getHeader(SignatureGPConstants.SIGNATUREGP_WEBSERVICE_ENDPOINT);

		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(stringEntity);
		httpPost.addHeader("Content-Type", "text/xml");
		httpPost.addHeader("SOAPAction", exchange.getIn().getHeader(SignatureGPConstants.SOAP_ACTION_KEY).toString());
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity entity = response.getEntity();

		String strResponse = null;
		if (entity != null) {
			strResponse = EntityUtils.toString(entity);
		}
		exchange.getOut().setBody(strResponse);
	}
}