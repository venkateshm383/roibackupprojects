package com.getusroi.mesh.header.initializer;

import java.util.Map;

import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.getusroi.core.datagrid.DataGridService;
import com.getusroi.mesh.MeshHeader;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.osgi.helper.OSGIEnvironmentHelper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.transaction.HazelcastXAResource;
import com.hazelcast.transaction.TransactionContext;

public class FeatureHeaderInitializer implements Processor {

	protected static final Logger logger = LoggerFactory
			.getLogger(FeatureHeaderInitializer.class);
	private UserTransactionManager userTransactionManager;

	/**
	 * This method is to set user transaction manager object
	 * 
	 * @param userTransactionManager
	 *            : UserTransactionManager Object
	 */
	public void setUserTransactionManager(
			UserTransactionManager userTransactionManager) {
		this.userTransactionManager = userTransactionManager;
	}

	private TransactionManager transactionManager;

	/**
	 * This method is to set user transaction manager object
	 * 
	 * @param TransactionManager
	 *            : UserTransactionManager Object
	 */
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * This method is used to set header for request servicetype,tenantid and
	 * data
	 * 
	 * @param exchange
	 *            : Exchange Object
	 */
	public void process(Exchange exchange) throws Exception {

		logger.debug("inside Header Initializer bean process()");
		logger.debug("exchange messgae : " + exchange.getIn().getHeaders());
		String completdata = exchange.getIn().getBody(String.class);
		logger.debug("complete data: " + completdata);

		JSONObject jobj = new JSONObject(completdata);
		String tenant = MeshHeaderConstant.tenant;
		String site = MeshHeaderConstant.site;
		String servicetype = (String) exchange.getIn().getHeader(
				MeshHeaderConstant.SERVICETYPE_KEY);
		String featureGroup = (String) exchange.getIn().getHeader(
				MeshHeaderConstant.FEATURE_GROUP_KEY);
		String featureName = (String) exchange.getIn().getHeader(
				MeshHeaderConstant.FEATURE_KEY);
		JSONArray data = (JSONArray) jobj.get(MeshHeaderConstant.DATA_KEY);
		String endpointType = (String) exchange.getIn().getHeader(
				MeshHeaderConstant.ENDPOINT_TYPE_KEY);

		MeshHeader meshHeader = new MeshHeader();
		meshHeader.setTenant(tenant);
		meshHeader.setSite(site);
		meshHeader.setServicetype(servicetype);
		// meshHeader.setOperation(operation);
		meshHeader.setFeatureGroup(featureGroup);
		meshHeader.setFeatureName(featureName);
		if (endpointType != null) {
			meshHeader.setEndpointType(endpointType);
		} else {
			meshHeader.setEndpointType("HTTP-JSON");
		}
		
		Map<String, Object> genricdata = meshHeader.getGenricdata();
		genricdata.put(MeshHeaderConstant.DATA_KEY, data);
		logger.debug("mesh header in meshInitializer class : " + meshHeader);
		hazelcastTransaction(meshHeader);
		exchange.getIn().setHeader(MeshHeaderConstant.MESH_HEADER_KEY,
				meshHeader);
		exchange.getIn().setBody(completdata);

	}
	
	/**
	 * This method ised used to take xml request and convert it into json format requried 
	 * @param exchange : Exchange Object
	 */
	public void processXmlRequest(Exchange exchange){
		logger.debug(".processXmlRequest method of FeatureHeaderInitializer");
		String xmlRequest = exchange.getIn().getBody(String.class);
		logger.debug("xml request : "+xmlRequest);
		String jsonData="{\"data\":[{\"xmldata\":"+xmlRequest+"}]";
		logger.debug("required jsondata : "+jsonData);
	}

	/**
	 * This methis is to start singleton hazelcast instance and set into header
	 * 
	 * @param exchange
	 *            :Exchange Object
	 * @throws SystemException
	 */
	private void hazelcastTransaction(MeshHeader meshheader)
			throws SystemException {

		logger.debug("inside service Processor bean hazelcast Transaction()");

		HazelcastInstance hazelcastInstance = DataGridService
				.getDataGridInstance().getHazelcastInstance();
		logger.debug(" hazelcast instance value in transactionalExecution() : "
				+ hazelcastInstance);
		TransactionContext context1 = null;
		logger.debug("OSGIEnvironmentHelper.isOSGIEnabled in initializer : "
						+ OSGIEnvironmentHelper.isOSGIEnabled);
		if (OSGIEnvironmentHelper.isOSGIEnabled) {
			context1 = hazelcastEnlistingInOsgi(hazelcastInstance);

		} else {

			context1 = hazelcastEnlistingInNonOsgi(hazelcastInstance);
		}

		logger.debug("hazelcastTransactionContext : " + context1);
		meshheader.setHazelcastTransactionalContext(context1);

		logger.debug("--------------------");

	}

	private TransactionContext hazelcastEnlistingInNonOsgi(
			HazelcastInstance hazelcastInstance) {
		HazelcastXAResource xaResource = hazelcastInstance.getXAResource();
		logger.debug("xa resource object in : " + xaResource);
		Transaction transaction = null;
		try {
			logger.debug("*******inside try block for enlist *********User transaction="
					+ userTransactionManager);
			transaction = userTransactionManager.getTransaction();
			logger.debug("*******inside try block for enlist *********transaction="
					+ transaction);
			transaction.enlistResource(xaResource);
			logger.debug("*******successfuly enlisted *********");

		} catch (Exception e) {
			logger.error("exception while enlisting in hazelcast transaction : "
					+ e);
			e.printStackTrace();
		}

		TransactionContext context1 = xaResource.getTransactionContext();

		return context1;
	}

	private TransactionContext hazelcastEnlistingInOsgi(
			HazelcastInstance hazelcastInstance) {
		logger.debug(".hazelcastEnlistingInOsgi of FeatureHeaderInitializer ");
		HazelcastXAResource xaResource = hazelcastInstance.getXAResource();
		logger.debug("xa resource object in : " + xaResource);
		Transaction transaction = null;
		try {
			logger.debug("*******inside try block for enlist *********User transaction="
					+ transactionManager);
			transaction = transactionManager.getTransaction();
			logger.debug("*******inside try block for enlist *********transaction="
					+ transaction);
			transaction.enlistResource(xaResource);
			logger.debug("*******successfuly enlisted *********");

		} catch (Exception e) {
			logger.error("exception while enlisting in hazelcast transaction : "
					+ e);
			e.printStackTrace();
		}

		TransactionContext context1 = xaResource.getTransactionContext();
		return context1;
	}

	
}