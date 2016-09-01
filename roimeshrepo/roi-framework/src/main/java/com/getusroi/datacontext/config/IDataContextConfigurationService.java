package com.getusroi.datacontext.config;

import com.getusroi.config.ConfigurationContext;
import com.getusroi.config.RequestContext;
import com.getusroi.datacontext.jaxb.FeatureDataContext;

public interface IDataContextConfigurationService {
	public void addDataContext(ConfigurationContext configContext,FeatureDataContext featureDataContext) throws  DataContextConfigurationException;
	public DataContextConfigurationUnit getDataContextConfiguration(RequestContext requestContext) throws  DataContextConfigurationException;
	public boolean deleteDataContextConfiguration(ConfigurationContext configContext) throws DataContextConfigurationException;
}
