package com.getusroi.featureinstaller;

import java.io.IOException;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.featureinstaller.util.FeatureMetaInfoResourceException;
import com.getusroi.featureinstaller.util.FeatureMetaInfoResourceUtil;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.mesh.TenantSitePropertiesLoader;
import com.getusroi.mesh.TenantSitePropertiesLoadingException;
import com.getusroi.mesh.load.resource.CamelApplicationRun;

/**
 * This method is used to search featureMetaInfo in all jars available in
 * classpath and load resources available at feature level
 * 
 * @author bizruntime
 *
 */
public class FeatureMetaInfoConfigInstaller {

	final static Logger logger = LoggerFactory.getLogger(FeatureMetaInfoConfigInstaller.class);
	private static final String PATTERN_SEARCH_KEY = "featureMetaInfo.xml";

	/**
	 * This method is used to search featureMetaInfo in all jars available in
	 * classpath and load resources available at feature level
	 * 
	 * @throws FeatureMetaInfoConfigInstallerException
	 */
	public void loadFeatureMetaInfoResources() throws FeatureMetaInfoConfigInstallerException {
		TenantSitePropertiesLoader propLoader = new TenantSitePropertiesLoader();
		try {
			propLoader.setTenantAndSite();
			logger.debug("tenant : " + MeshHeaderConstant.tenant + ", site : " + MeshHeaderConstant.site);
			FeatureMetaInfoResourceUtil fmiResList = new FeatureMetaInfoResourceUtil();
			Pattern pattern = Pattern.compile(PATTERN_SEARCH_KEY);
			try {
				fmiResList.getClassPathResources(pattern);
				CamelApplicationRun runCamelApplication = new CamelApplicationRun();
				try {
					runCamelApplication.startCamelApplication();
				} catch (Exception e) {
					throw new FeatureMetaInfoConfigInstallerException("Unable start camel application ", e);
				}
			} catch (FeatureMetaInfoResourceException | IOException e) {
				throw new FeatureMetaInfoConfigInstallerException(
						"Unable to load " + PATTERN_SEARCH_KEY + " from class path ", e);
			}
		} catch (TenantSitePropertiesLoadingException e1) {
			throw new FeatureMetaInfoConfigInstallerException("Unable to site and tenant from class path : ");
		}
	}// end of method

	public static void main(String[] args) throws FeatureMetaInfoConfigInstallerException {
		FeatureMetaInfoConfigInstaller installer = new FeatureMetaInfoConfigInstaller();
		installer.loadFeatureMetaInfoResources();
	}
}