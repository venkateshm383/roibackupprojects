package com.getusroi.featuremaster.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.config.ConfigurationContext;
import com.getusroi.config.persistence.ConfigPersistenceException;
import com.getusroi.config.persistence.IConfigPersistenceService;
import com.getusroi.config.persistence.InvalidNodeTreeException;
import com.getusroi.config.persistence.impl.ConfigPersistenceServiceMySqlImpl;
import com.getusroi.featuremaster.FeatureMasterServiceException;
import com.getusroi.featuremaster.IFeatureMasterService;

public class FeatureMasterService implements IFeatureMasterService {
	
	final Logger logger = LoggerFactory.getLogger(FeatureMasterService.class);


	/**
	 * check wether the feature is Exist in Feature MAster or not 
	 * @param tenantId
	 * @param siteId
	 * @param featureGroup
	 * @param featureName
	 */
	public boolean checkFeatureExistInFeatureMasterOrNot(ConfigurationContext configContext ) throws FeatureMasterServiceException {
		
		logger.debug("inside checkFeatureExistInFeatureMasterOrNot method  ");
		int featureMasterId=0;
		
		try {
			int siteNodeId=getApplicableNodeIdForSite(configContext.getTenantId(), configContext.getSiteId());
			
			IConfigPersistenceService configPersistenceService = new ConfigPersistenceServiceMySqlImpl();

			featureMasterId=configPersistenceService.getFeatureMasterIdByFeatureAndFeaturegroup(configContext.getFeatureName(), configContext.getFeatureGroup(), siteNodeId);
			logger.debug("feature found in FeatureMaster with MasterNodeId : "+featureMasterId);
			if(featureMasterId==0)
				return false;
			
			
		} catch (InvalidNodeTreeException | ConfigPersistenceException e) {

			throw new FeatureMasterServiceException("Failed find out Feature in Feature master "+e);

		}
		return true;
		
	}
	
	
	/**
	 * to get siteNodId By tenant name,site name,feature group 
	 * 
	 * @param tenantId
	 * @param siteId
	 * @param featureGroup
	 * @return siteNodeID
	 * @throws InvalidNodeTreeException
	 * @throws ConfigPersistenceException
	 */
	
	private Integer getApplicableNodeIdForSite(String tenantId, String siteId)
			throws InvalidNodeTreeException, ConfigPersistenceException {
		logger.debug("Finding ParentNodeId for Tenant=" + tenantId + "-siteId=" + siteId );

		IConfigPersistenceService configPersistenceService = new ConfigPersistenceServiceMySqlImpl();
		return configPersistenceService.getApplicableNodeId(tenantId, siteId);
	}// 





}
