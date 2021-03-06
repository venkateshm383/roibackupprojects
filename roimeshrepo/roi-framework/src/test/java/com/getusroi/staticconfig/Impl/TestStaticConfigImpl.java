package com.getusroi.staticconfig.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.config.ConfigurationContext;
import com.getusroi.config.RequestContext;
import com.getusroi.staticconfig.AddStaticConfigException;
import com.getusroi.staticconfig.StaticConfigDuplicateNameofFileException;
import com.getusroi.staticconfig.StaticConfigFetchException;
import com.getusroi.staticconfig.StaticConfigInitializationException;
import com.getusroi.staticconfig.impl.AccessProtectionException;
import com.getusroi.staticconfig.impl.FileStaticConfigurationServiceImpl;

/**
 * 
 * Test class just to create proper directory into your system for creating it
 * according to your Configuration COntext
 * 
 * @author bizruntime
 *
 */
public class TestStaticConfigImpl {
	final Logger logger = LoggerFactory.getLogger(TestStaticConfigImpl.class);
	//@Test
	public void testAddingConfigFile() throws StaticConfigInitializationException, AddStaticConfigException, StaticConfigDuplicateNameofFileException, AccessProtectionException {
		FileStaticConfigurationServiceImpl fileStaticConfigurationServiceImpl = new FileStaticConfigurationServiceImpl();
		ConfigurationContext ctx = new ConfigurationContext("gap", "site1", "sacGroup", "sac", "key2act", "1.1");
		fileStaticConfigurationServiceImpl.addStaticConfiguration(ctx, "test.xsl", "test file");
	}
	
	public void testGetStaticConfig() throws StaticConfigInitializationException, StaticConfigFetchException, AccessProtectionException {
		FileStaticConfigurationServiceImpl fileStaticConfigurationServiceImpl = new FileStaticConfigurationServiceImpl();
		RequestContext requestContext = new RequestContext("gap", "site1", "sacGroup", "sac", "key2act", "1.0");
		String filePath = fileStaticConfigurationServiceImpl.getStaticConfiguration(requestContext, "test.xsl");
		logger.debug(filePath);
	}
}
