package com.getusroi.featuremaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.config.ConfigurationContext;
import com.getusroi.config.persistence.ConfigNodeData;
import com.getusroi.config.persistence.ConfigPersistenceException;
import com.getusroi.config.persistence.IConfigPersistenceService;
import com.getusroi.config.persistence.impl.ConfigPersistenceServiceMySqlImpl;
import com.getusroi.config.util.GenericTestConstant;
import com.getusroi.core.datagrid.DataGridService;
import com.getusroi.feature.config.FeatureConfigParserException;
import com.getusroi.feature.config.FeatureConfigRequestContext;
import com.getusroi.feature.config.FeatureConfigurationConstant;
import com.getusroi.feature.config.FeatureConfigurationException;
import com.getusroi.feature.config.FeatureConfigurationServiceTest;
import com.getusroi.feature.config.FeatureConfigurationUnit;
import com.getusroi.feature.config.FeatureTestConstant;
import com.getusroi.feature.config.IFeatureConfigurationService;
import com.getusroi.feature.config.impl.FeatureConfigXMLParser;
import com.getusroi.feature.config.impl.FeatureConfigurationService;
import com.getusroi.feature.jaxb.Feature;
import com.getusroi.feature.jaxb.FeaturesServiceInfo;
import com.getusroi.featuremaster.impl.FeatureMasterService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class FeatureMasterServiceTest {
	
	
	final Logger logger = LoggerFactory.getLogger(FeatureConfigurationServiceTest.class);
	private Feature featureConfigurationList;
	private IFeatureConfigurationService featureConfigService;
	private IFeatureMasterService featureMasterService;

	/**
	 * This method is used to marshal featureserivce.xml to pojo and return root
	 * object
	 * 
	 * @return FeaturesServiceInfo : return a FeaturesServiceInfo object
	 * @throws FeatureConfigParserException
	 */
	private FeaturesServiceInfo getFeatureConfiguration() throws FeatureConfigParserException {
		FeatureConfigXMLParser parser = new FeatureConfigXMLParser();
		InputStream inputstream = FeatureConfigurationServiceTest.class.getClassLoader().getResourceAsStream(FeatureTestConstant.configfileToParse1);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
		StringBuilder out1 = new StringBuilder();
		String line=null;
		try {
			while ((line = reader.readLine()) != null) {
			    out1.append(line);
			    
			}
			reader.close();
		} catch (IOException e) {
		throw new FeatureConfigParserException("feature file doesnot exist in classpath",e);
		}
		
		String featureConfigxml=out1.toString();
		FeaturesServiceInfo featureConfiguration = parser.marshallConfigXMLtoObject(featureConfigxml);
		return featureConfiguration;
	}// end of getFeatureConfiguration method

	/**
	 * This method is called before any other test can executed, purpose is to
	 * load configuration in database and cache
	 * 
	 * @throws FeatureConfigParserException
	 * @throws ConfigPersistenceException
	 * @throws FeatureConfigurationException
	 */
	@Before
	public void loadConfiguration() throws FeatureConfigParserException, ConfigPersistenceException, FeatureConfigurationException {
		FeaturesServiceInfo featureConfiguration = getFeatureConfiguration();
		featureConfigService = new FeatureConfigurationService();
	
		// Clear all DB Data First for nodeId parcel and label service feature
		IConfigPersistenceService pesrsistence1 = new ConfigPersistenceServiceMySqlImpl();
		pesrsistence1.deleteConfigNodeDataByNodeId(FeatureTestConstant.getConfigNodeId());	
		pesrsistence1.deleteConfigNodeDataByNodeId(FeatureTestConstant.TEST_VENDOR_NODEID);	

		featureConfigurationList = featureConfiguration.getFeatures().getFeature();


	}// end of loadConfiguration test method

	@Test
	public void testIfFeatureExistInFeatureMaster() throws FeatureConfigurationException, FeatureMasterServiceException {
		Feature feature = getFeatureByName("Parcel");
		featureConfigService = new FeatureConfigurationService();
		featureMasterService = new FeatureMasterService();
		ConfigurationContext configurationContext = new ConfigurationContext(GenericTestConstant.getTenant(),
				GenericTestConstant.getSite(), GenericTestConstant.TEST_FEATUREGROUP, "Parcel",FeatureTestConstant.TEST_FEATURE_VENDOR,FeatureTestConstant.TEST_FEATURE_VERSION);
		boolean isExsist = featureMasterService.checkFeatureExistInFeatureMasterOrNot(configurationContext);
		Assert.assertTrue(isExsist);
		ConfigurationContext confiContext = new ConfigurationContext(GenericTestConstant.getTenant(),
				GenericTestConstant.getSite());
		featureConfigService.addFeatureConfiguration(configurationContext, feature);
		FeatureConfigRequestContext requestContext = new FeatureConfigRequestContext(GenericTestConstant.getTenant(),
				GenericTestConstant.getSite(), GenericTestConstant.getFeatureGroup(), "Parcel",FeatureTestConstant.TEST_FEATURE_VENDOR,FeatureTestConstant.TEST_FEATURE_VERSION);
		IFeatureConfigurationService fsConfigService = new FeatureConfigurationService();
		IConfigPersistenceService pesrsistence = new ConfigPersistenceServiceMySqlImpl();
		FeatureConfigurationUnit fcu = fsConfigService.getFeatureConfiguration(requestContext, "Parcel");
		logger.debug("feature configuration unit : " + fcu);
		Object cachedata = fcu.getConfigData();
		logger.debug("ConfigObject is =" + fcu);
		Assert.assertNotNull("Cached FeatureConfiguration  should not be null", fcu);
		Assert.assertNotNull("Serilizable Object is not null : " + cachedata);
		Assert.assertTrue(cachedata instanceof Feature);
		Feature feat = (Feature) cachedata;
		Assert.assertEquals("feature name is Parcel", feat.getFeatureName(), "Parcel");
	}// end of testFeatureUpload method

	
	@Test
	public void testIfFeatureNotExistInFeatureMaster() throws FeatureConfigurationException, FeatureMasterServiceException {
		
		featureMasterService=new FeatureMasterService();
		ConfigurationContext configurationContext=new ConfigurationContext(GenericTestConstant.getTenant(), GenericTestConstant.getSite(), GenericTestConstant.TEST_FEATUREGROUP, "NEXISTFeature");
		boolean isExsist=featureMasterService.checkFeatureExistInFeatureMasterOrNot(configurationContext);
		Assert.assertFalse(isExsist);

	}// end of testFeatureUpload method

	
	
	/**
	 * This method is used to check if configuration data exist in cache or not
	 * 
	 * @param configName
	 *           : Name of the feature
	 * @return Cache Object
	 */
	private Object checkCacheForConfig(String configName) {
		// Check if data is loaded in the Hazelcast or not
		HazelcastInstance hazelcastInstance = DataGridService.getDataGridInstance().getHazelcastInstance();
		// gap-26-PSC
		IMap map = hazelcastInstance.getMap(GenericTestConstant.getTenant() + "-"
				+ FeatureConfigurationUnit.getConfigGroupKey(new Integer(FeatureTestConstant.getConfigNodeId())));// 121
		logger.debug("GroupLevel Map=" + map);
		Object cachedObj = map.get(configName);
		logger.debug("CheckCacheForConfig() ConfigObject is =" + cachedObj);
		return cachedObj;

	}

	/**
	 * This method is used to check any entry exist in database with specified
	 * configname and feature nodeId
	 * 
	 * @param configName
	 *           : Name of the feature
	 * @param nodeId
	 *           : Feature Node id
	 * @return ConfigNodeData Object
	 * @throws ConfigPersistenceException
	 */
	private ConfigNodeData checkDBForConfig(String configName, int nodeId) throws ConfigPersistenceException {
		IConfigPersistenceService pesrsistence = new ConfigPersistenceServiceMySqlImpl();
		ConfigNodeData configData = pesrsistence.getConfigNodeDatabyNameAndNodeId(nodeId, configName, FeatureConfigurationConstant.FEATURE_CONFIG_TYPE);
		return configData;
	}

	/**
	 * This is used to get the feature object by name
	 * 
	 * @param name
	 *           : name of the feature whose object we want to get
	 * @return Feature object
	 */
	private Feature getFeatureByName(String name) {
		
			if (featureConfigurationList.getFeatureName().equalsIgnoreCase(name))
				return featureConfigurationList;
		
		return null;
	}

}
