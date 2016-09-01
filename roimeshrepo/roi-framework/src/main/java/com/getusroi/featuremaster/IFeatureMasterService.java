package com.getusroi.featuremaster;

import com.getusroi.config.ConfigurationContext;

public interface IFeatureMasterService {
	
	
	
	public boolean checkFeatureExistInFeatureMasterOrNot(ConfigurationContext configContext) throws FeatureMasterServiceException;

}
