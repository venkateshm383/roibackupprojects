package com.getusroi.integrationfwk.config;

import com.getusroi.config.ConfigurationContext;
import com.getusroi.config.RequestContext;
import com.getusroi.integrationfwk.config.jaxb.IntegrationPipe;

public interface IIntegrationPipeLineConfigurationService {

	/**
	 * service to add the configurations in to the database, where the node will
	 * be decided by the 'configurationContext', and the stored value is from
	 * 'integrationPipe'
	 * 
	 * @param configurationContext
	 * @param integrationPipe
	 * @return
	 * @throws IntegrationPipelineConfigException
	 */
	public void addIntegrationPipelineConfiguration(
			ConfigurationContext configurationContext,
			IntegrationPipe integrationPipe)
			throws IntegrationPipelineConfigException;

	/**
	 * service to get the configurations from the Imap, HazelCast, where the
	 * treeNode will be decided by RequestContext, and by configname o get the
	 * particular configuration
	 * 
	 * @param requestContext
	 * @param configName
	 * @return IntegrationPipelineConfigUnit, object which holds, some key
	 *         attributes and the JAXB object
	 * @throws IntegrationPipelineConfigException
	 */
	public IntegrationPipelineConfigUnit getIntegrationPipeConfiguration(
			RequestContext requestContext, String configName)
			throws IntegrationPipelineConfigException;

	/**
	 * service to delete the pipeline configurations by using
	 * configurationContext and configName
	 * 
	 * @param configurationContext
	 * @param configName
	 * @return
	 * @throws IntegrationPipelineConfigException
	 * @throws invalidIntegrationPipelineConfigException
	 */
	public boolean deleteIntegrationPipelineConfiguration(
			ConfigurationContext configurationContext, String configName)
			throws IntegrationPipelineConfigException,
			InvalidIntegrationPipelineConfigException;

	/**
	 * service to update the integration pipeline configuration
	 * 
	 * @param configContext
	 * @param integrationPipe
	 * @param configNodeDataId
	 * @return
	 */
	public int updateIntegrationPipelineConfiguration(
			ConfigurationContext configContext,
			IntegrationPipe integrationPipe, int configNodeDataId)
			throws IntegrationPipelineConfigException,
			IntegrationPipelineConfigException,InvalidIntegrationPipelineConfigException;

	/**
	 * service to change the status of the pipeline configuration by using the
	 * isEnabled boolean value
	 * 
	 * @param configContext
	 * @param configName
	 * @param isEnabled
	 * @throws IntegrationPipelineConfigException
	 */
	public void changeStatusOfIntegrationPipelineConfig(
			ConfigurationContext configContext, String configName,
			boolean isEnabled) throws IntegrationPipelineConfigException,
			InvalidIntegrationPipelineConfigException;

}
