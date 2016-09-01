package com.getusroi.featureinstaller.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.config.ConfigurationContext;
import com.getusroi.config.RequestContext;
import com.getusroi.datacontext.config.DataContextConfigurationException;
import com.getusroi.datacontext.config.DataContextParserException;
import com.getusroi.datacontext.config.IDataContextConfigurationService;
import com.getusroi.datacontext.config.impl.DataContextConfigXMLParser;
import com.getusroi.datacontext.config.impl.DataContextConfigurationService;
import com.getusroi.datacontext.jaxb.FeatureDataContext;
import com.getusroi.dynastore.config.DynaStoreConfigParserException;
import com.getusroi.dynastore.config.DynaStoreConfigurationException;
import com.getusroi.dynastore.config.IDynaStoreConfigurationService;
import com.getusroi.dynastore.config.impl.DynaStoreConfigXmlParser;
import com.getusroi.dynastore.config.impl.DynaStoreConfigurationService;
import com.getusroi.dynastore.config.jaxb.DynastoreConfiguration;
import com.getusroi.dynastore.config.jaxb.DynastoreConfigurations;
import com.getusroi.eventframework.config.EventFrameworkConfigParserException;
import com.getusroi.eventframework.config.EventFrameworkConfigurationException;
import com.getusroi.eventframework.config.EventFrameworkXmlHandler;
import com.getusroi.eventframework.config.IEventFrameworkConfigService;
import com.getusroi.eventframework.config.impl.EventFrameworkConfigService;
import com.getusroi.eventframework.jaxb.DispatchChanel;
import com.getusroi.eventframework.jaxb.DispatchChanels;
import com.getusroi.eventframework.jaxb.Event;
import com.getusroi.eventframework.jaxb.EventDispatcher;
import com.getusroi.eventframework.jaxb.EventFramework;
import com.getusroi.eventframework.jaxb.EventSubscription;
import com.getusroi.eventframework.jaxb.EventSubscriptions;
import com.getusroi.eventframework.jaxb.Events;
import com.getusroi.eventframework.jaxb.SystemEvent;
import com.getusroi.eventframework.jaxb.SystemEvents;
import com.getusroi.feature.config.FeatureConfigParserException;
import com.getusroi.feature.config.FeatureConfigRequestContext;
import com.getusroi.feature.config.FeatureConfigRequestException;
import com.getusroi.feature.config.FeatureConfigurationException;
import com.getusroi.feature.config.IFeatureConfigurationService;
import com.getusroi.feature.config.impl.FeatureConfigXMLParser;
import com.getusroi.feature.config.impl.FeatureConfigurationService;
import com.getusroi.feature.jaxb.FeaturesServiceInfo;
import com.getusroi.featureinstaller.customloader.CustomLoaderFromJar;
import com.getusroi.featuremaster.FeatureMasterServiceException;
import com.getusroi.featuremaster.IFeatureMasterService;
import com.getusroi.featuremaster.impl.FeatureMasterService;
import com.getusroi.featuremetainfo.FeatureMetaInfoConfigParserException;
import com.getusroi.featuremetainfo.impl.FeatureMetaInfoConfigXmlParser;
import com.getusroi.featuremetainfo.jaxb.ConfigFile;
import com.getusroi.featuremetainfo.jaxb.DataContexts;
import com.getusroi.featuremetainfo.jaxb.DynaStoreConfiguration;
import com.getusroi.featuremetainfo.jaxb.DynaStoreConfigurations;
import com.getusroi.featuremetainfo.jaxb.EventResource;
import com.getusroi.featuremetainfo.jaxb.EventResources;
import com.getusroi.featuremetainfo.jaxb.Feature;
import com.getusroi.featuremetainfo.jaxb.FeatureDataContexts;
import com.getusroi.featuremetainfo.jaxb.FeatureGroup;
import com.getusroi.featuremetainfo.jaxb.FeatureImplementation;
import com.getusroi.featuremetainfo.jaxb.FeatureImplementations;
import com.getusroi.featuremetainfo.jaxb.FeatureMetainfo;
import com.getusroi.featuremetainfo.jaxb.IntegrationPipeLineConfigurations;
import com.getusroi.featuremetainfo.jaxb.PermaStoreConfiguration;
import com.getusroi.featuremetainfo.jaxb.PipeConfiguration;
import com.getusroi.featuremetainfo.jaxb.PolicyConfiguration;
import com.getusroi.featuremetainfo.jaxb.PolicyConfigurations;
import com.getusroi.featuremetainfo.jaxb.StaticFileConfiguration;
import com.getusroi.integrationfwk.config.IIntegrationPipeLineConfigurationService;
import com.getusroi.integrationfwk.config.IntegrationPipelineConfigException;
import com.getusroi.integrationfwk.config.IntegrationPipelineConfigParserException;
import com.getusroi.integrationfwk.config.impl.IntegrationPipelineConfigXmlParser;
import com.getusroi.integrationfwk.config.impl.IntegrationPipelineConfigurationService;
import com.getusroi.integrationfwk.config.jaxb.IntegrationPipes;
import com.getusroi.mesh.MeshHeaderConstant;
import com.getusroi.permastore.config.IPermaStoreConfigurationService;
import com.getusroi.permastore.config.PermaStoreConfigParserException;
import com.getusroi.permastore.config.PermaStoreConfigRequestException;
import com.getusroi.permastore.config.PermaStoreConfigurationException;
import com.getusroi.permastore.config.impl.PermaStoreConfigXMLParser;
import com.getusroi.permastore.config.impl.PermaStoreConfigurationService;
import com.getusroi.permastore.config.jaxb.PermaStoreConfigurations;
import com.getusroi.policy.config.IPolicyConfigurationService;
import com.getusroi.policy.config.PolicyConfigXMLParser;
import com.getusroi.policy.config.PolicyConfigXMLParserException;
import com.getusroi.policy.config.PolicyConfigurationException;
import com.getusroi.policy.config.PolicyRequestContext;
import com.getusroi.policy.config.PolicyRequestException;
import com.getusroi.policy.config.impl.PolicyConfigurationService;
import com.getusroi.policy.jaxb.Policies;
import com.getusroi.policy.jaxb.Policy;
import com.getusroi.staticconfig.AddStaticConfigException;
import com.getusroi.staticconfig.IStaticConfigurationService;
import com.getusroi.staticconfig.StaticConfigDuplicateNameofFileException;
import com.getusroi.staticconfig.StaticConfigInitializationException;
import com.getusroi.staticconfig.factory.StaticConfigurationFactory;
import com.getusroi.staticconfig.impl.AccessProtectionException;
import com.getusroi.staticconfig.util.LocalfileUtil;
import com.getusroi.zookeeper.staticconfig.service.impl.InvalidFilePathException;
import com.getusroi.zookeeper.staticconfig.service.impl.ZookeeperFilemanagementServiceImpl;

/**
 * This class is responsible for loading all feature related configuration in
 * non-osgi environment
 * 
 * @author bizruntime
 *
 */
public class FeatureMetaInfoResourceUtil {
	final static Logger logger = LoggerFactory.getLogger(FeatureMetaInfoResourceUtil.class);
	private static final String JAVA_CLASSPATH_KEY = "java.class.path";
	private static final String PATH_SEPERATOR_KEY = "path.separator";

	/**
	 * This method is used to get all elements of java.class.path get a
	 * Collection of resources Pattern pattern =
	 * Pattern.compile("featureMetaInfo.xml"); gets all resources
	 * 
	 * @param pattern
	 *            the pattern to match
	 * @return the resources if they are found
	 * @throws FeatureMetaInfoResourceException
	 * @throws IOException
	 */
	public Collection<String> getClassPathResources(Pattern pattern)
			throws FeatureMetaInfoResourceException, IOException {
		final ArrayList<String> resource = new ArrayList<String>();
		final String javaclassPath = System.getProperty(JAVA_CLASSPATH_KEY, ".");
		final String[] javaclassPathElements = javaclassPath.split(System.getProperty(PATH_SEPERATOR_KEY));
		for (final String element : javaclassPathElements) {
			resource.addAll(getResources(element, pattern));
		}
		return resource;
	}

	/**
	 * This method is to search pattern in directory or jar available in
	 * classpath
	 * 
	 * @param element
	 *            : Jar/Directory to be searched
	 * @param pattern
	 *            : pattern to be matched
	 * @return Collection object
	 * @throws FeatureMetaInfoResourceException
	 * @throws IOException
	 */
	private Collection<String> getResources(String element, Pattern pattern)
			throws FeatureMetaInfoResourceException, IOException {
		final ArrayList<String> resourcevalue = new ArrayList<String>();
		final File file = new File(element);
		if (file.isDirectory()) {
			resourcevalue.addAll(getResourcesFromDirectory(file, pattern));
		} else {
			resourcevalue.addAll(getResourcesFromJar(file, pattern, element));
		}
		return resourcevalue;
	}

	/**
	 * This method is to searched the matching pattern in jar
	 * 
	 * @param file
	 *            : name of the file to be searched
	 * @param pattern
	 *            : pattern to be searched
	 * @param element
	 *            : jar to be searched
	 * @return Colection Object
	 * @throws FeatureMetaInfoResourceException
	 * @throws IOException
	 */
	private Collection<String> getResourcesFromJar(File file, Pattern pattern, String element)
			throws FeatureMetaInfoResourceException, IOException {
		final ArrayList<String> resourcevalue = new ArrayList<String>();
		ZipFile zipfile;
		try {
			zipfile = new ZipFile(file);
		} catch (ZipException e) {
			throw new FeatureMetaInfoResourceException(
					"Unable to opens a ZIP file for reading given the specified File object.", e);
		} catch (IOException e) {
			throw new FeatureMetaInfoResourceException("Unable to read from given the File object. = " + file.getName(),
					e);
		}
		final Enumeration enumerator = zipfile.entries();
		while (enumerator.hasMoreElements()) {
			final ZipEntry zipentry = (ZipEntry) enumerator.nextElement();
			final String fileName = zipentry.getName();
			final boolean acceptedFile = pattern.matcher(fileName).matches();
			if (acceptedFile) {
				List<FeatureGroup> featureGroupList = parseAndGetFeatureMetaInfo(fileName, element);
				loadAllResourceFromFeatureLevel(featureGroupList);
				resourcevalue.add(fileName);
			}
		}
		try {
			zipfile.close();
		} catch (IOException e1) {
			throw new FeatureMetaInfoResourceException("Unable to close a ZIP file object for specified File object.",
					e1);
		}
		return resourcevalue;
	}

	/**
	 * This method is to searched the matching pattern in directory
	 * 
	 * @param file
	 *            : name of the file to be searched
	 * @param pattern
	 *            : pattern to be searched
	 * @return Colection Object
	 * @throws FeatureMetaInfoResourceException
	 */
	private Collection<String> getResourcesFromDirectory(File directory, Pattern pattern)
			throws FeatureMetaInfoResourceException {
		final ArrayList<String> resourcevalue = new ArrayList<String>();
		final File[] fileList = directory.listFiles();
		for (File file : fileList) {
			if (file.isDirectory()) {
				resourcevalue.addAll(getResourcesFromDirectory(file, pattern));
			} else {
				try {
					final String fileName = file.getCanonicalPath();
					final boolean acceptedFile = pattern.matcher(fileName).matches();
					if (acceptedFile) {
						// parseAndGetFeatureMetaInfo(fileName);
						resourcevalue.add(fileName);
					}
				} catch (final IOException e) {
					throw new FeatureMetaInfoResourceException(
							"Unable to get the canonical path of file : " + file.getName(), e);
				}
			}
		}
		return resourcevalue;
	}

	/**
	 * This method is used to parse the featureMetaInfo
	 * 
	 * @param featureMetaInfo
	 *            : filename to be parsed
	 * @param element
	 *            : jar from where it has to be loaded
	 * @return List : list of FeatureGroup Object
	 * @throws FeatureMetaInfoResourceException
	 */
	public List<FeatureGroup> parseAndGetFeatureMetaInfo(String featureMetaInfo, String element)
			throws FeatureMetaInfoResourceException {
		logger.debug(
				".parseAndgetFeatureMetaInfo of FeatureMetaInfoExtender " + featureMetaInfo + ",element : " + element);
		URL url;
		try {
			url = new URL("file:" + element);
		} catch (MalformedURLException e1) {
			throw new FeatureMetaInfoResourceException("Unable to create URL from element path : " + element, e1);
		}
		CustomLoaderFromJar customJarLoader = new CustomLoaderFromJar(new URL[] { url });
		URL featureMetaInfoXmlUrl = customJarLoader.getResource(featureMetaInfo);
		logger.debug("featureMetaInfoXmlUrl : " + featureMetaInfoXmlUrl);
		String featurexmlAsString = convertXmlToString(featureMetaInfoXmlUrl, featureMetaInfo);
		List<FeatureGroup> featureGroupList = null;
		if (featurexmlAsString != null) {
			FeatureMetaInfoConfigXmlParser featureMetaInfoParser = new FeatureMetaInfoConfigXmlParser();
			try {
				FeatureMetainfo featureMetaInfo1 = featureMetaInfoParser.marshallConfigXMLtoObject(featurexmlAsString);
				featureGroupList = featureMetaInfo1.getFeatureGroup();
			} catch (FeatureMetaInfoConfigParserException e) {
				throw new FeatureMetaInfoResourceException("Unable to parse featureMetaInfo xml string into object ");
			}
		} // end of if(featurexmlAsString!=null)
		logger.debug("exiting parseAndgetFeatureMetaInfo of FeatureMetaInfoExtender ");
		return featureGroupList;
	}

	/**
	 * This method is used to xml format to string format
	 * 
	 * @param featureMetaInfoXmlUrl
	 *            : URL Object of resource file
	 * @param featureMetaInfo
	 *            : name of xml file to be converted into String
	 * @return String
	 * @throws FeatureMetaInfoResourceException
	 */
	private String convertXmlToString(URL featureMetaInfoXmlUrl, String featureMetaInfo)
			throws FeatureMetaInfoResourceException {
		logger.debug(".convertFeatureMetaInfoXmlToString of FeatureMetaInfoExtender");
		InputStream featureMetaInfoXmlInput = null;
		String featurexmlAsString = null;
		StringBuilder out1 = new StringBuilder();
		if (featureMetaInfoXmlUrl != null) {
			try {
				featureMetaInfoXmlInput = featureMetaInfoXmlUrl.openConnection().getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(featureMetaInfoXmlInput));
				String line;
				try {
					while ((line = reader.readLine()) != null) {
						out1.append(line);
					}
				} catch (IOException e) {
					throw new FeatureMetaInfoResourceException(
							"Unable to open the read for the BufferedReader for the file : " + featureMetaInfo, e);
				}
				logger.debug(out1.toString()); // Prints the string content read
												// from input stream
				try {
					reader.close();
				} catch (IOException e) {
					throw new FeatureMetaInfoResourceException(
							"Unable to close the read for the BufferedReader for the file : " + featureMetaInfo, e);
				}
				featurexmlAsString = out1.toString();
			} catch (IOException e) {
				throw new FeatureMetaInfoResourceException(
						"Unable to open the input stream for the file : " + featureMetaInfo, e);
			}
		} else {
			logger.debug("FeatureMetaInfo.xml file doesn't exist ");
		}
		return featurexmlAsString;
	}// end of method

	/**
	 * This method will load resource from feature level
	 * 
	 * @param featureGroupList
	 *            : List of FeatureGroup available in feature
	 * @throws FeatureMetaInfoResourceException
	 * @throws IOException
	 */
	private void loadAllResourceFromFeatureLevel(List<FeatureGroup> featureGroupList)
			throws FeatureMetaInfoResourceException, IOException {
		if (featureGroupList != null && !featureGroupList.isEmpty()) {
			for (FeatureGroup featureGroup : featureGroupList) {
				String featureGroupName = featureGroup.getName();
				List<Feature> featureList = featureGroup.getFeatures().getFeature();
				logger.debug("featureList : " + featureList.size());
				for (Feature feature : featureList) {
					logger.debug("Feature group Name : " + featureGroupName + ", Feature Name : " + feature.getName()
							+ ", vendor name : " + feature.getVendorName() + ", version : "
							+ feature.getVendorVersion());
					boolean isAvailable = checkFeatureExitInFeatureMaster(featureGroupName, feature.getName());
					logger.debug("is avalable : " + isAvailable);
					if (isAvailable) {
						checkResourceAvailableAndload(feature, featureGroupName);
					} else {
						logger.debug("feature doesn't avaliable in master table with feature group name : "
								+ featureGroupName + " and feature name : " + feature.getName());
					}

				}
			}
		} // end of if(featureGroupList != null && !featureGroupList.isEmpty())
	}// end of method

	/**
	 * This method is used to check if feature exist in FeatureMaster Table or
	 * not
	 * 
	 * @param featureGroup
	 *            : feature group name
	 * @param featureName
	 *            : feature name
	 * @return boolean : True/False
	 * @throws FeatureMetaInfoResourceException
	 */
	private boolean checkFeatureExitInFeatureMaster(String featureGroup, String featureName)
			throws FeatureMetaInfoResourceException {
		logger.debug(".checkFeatureExitInFeatureMaster of FeatureMetaInfoExtender");
		IFeatureMasterService featureMasterServie = new FeatureMasterService();
		boolean isAvailable;
		try {
			ConfigurationContext configContext = new ConfigurationContext(MeshHeaderConstant.tenant,
					MeshHeaderConstant.site, featureGroup, featureName);
			isAvailable = featureMasterServie.checkFeatureExistInFeatureMasterOrNot(configContext);
		} catch (FeatureMasterServiceException e) {
			throw new FeatureMetaInfoResourceException("Unable  find out Feature with feature group name : "
					+ featureGroup + " and feature name : " + featureName + " in Feature master ", e);

		}
		return isAvailable;

	}

	/**
	 * This method is used to check the resource available in
	 * featureMetaInfo.xml and call method to load them
	 * 
	 * @param feature
	 *            : feature name
	 * @param featureGroupName
	 *            : feature group name
	 * @throws FeatureMetaInfoResourceException
	 * @throws IOException
	 */
	public void checkResourceAvailableAndload(Feature feature, String featureGroupName)
			throws FeatureMetaInfoResourceException, IOException {
		logger.debug(".checkResourceAvailableAndCall of FeatureMetaInfoResourceUtil");
		// check if event resource configured and then load
		EventResources eventResources = feature.getEventResources();
		checkEventResourceAndLoad(eventResources, feature, featureGroupName);

		// check if permastore resource configured and then load
		com.getusroi.featuremetainfo.jaxb.PermaStoreConfigurations permastoreConfiguration = feature
				.getPermaStoreConfigurations();
		checkPermastoreResourceAndLoad(permastoreConfiguration, feature, featureGroupName);

		// check if dynastore resource configured and then load
		DynaStoreConfigurations dynastoreConfiguration = feature.getDynaStoreConfigurations();
		checkDynastoreResourceAndLoad(dynastoreConfiguration, feature, featureGroupName);

		// check if policy resource configured and then load
		PolicyConfigurations policyConfiguration = feature.getPolicyConfigurations();
		checkPolicyResourceAndLoad(policyConfiguration, feature, featureGroupName);

		// check if featureImpl resource configured and then load
		FeatureImplementations featureImplementation = feature.getFeatureImplementations();
		checkFeatureImplResourceAndLoad(featureImplementation, feature, featureGroupName);

		// check if featureDataContext is defined or not
		FeatureDataContexts featureDataContexts = feature.getFeatureDataContexts();
		checkFeatureDataContextsResourceAndLoad(featureDataContexts, feature, featureGroupName);

		// check if integrationpipelineConfig is defined or not
		IntegrationPipeLineConfigurations integrationPipes = feature.getIntegrationPipeLineConfigurations();
		checkIntegrationPipeCongigurationsResourceAndLoad(integrationPipes, feature, featureGroupName);

		// load the Static file configurations..
		StaticFileConfiguration staticFileConfiguration = feature.getStaticFileConfiguration();
		storeStaticFileConfigs(staticFileConfiguration, feature, featureGroupName);
	}

	/**
	 * to store the StaticFileConfigs into the zookeeperNode
	 * 
	 * @param staticFileConfiguration
	 * @param feature
	 * @param featureGroupName
	 * @throws IOException
	 * @throws FeatureMetaInfoResourceException
	 */
	private void storeStaticFileConfigs(StaticFileConfiguration staticFileConfiguration, Feature feature,
			String featureGroupName) throws IOException, FeatureMetaInfoResourceException {
		logger.debug(".storeStaticFileConfigs method of FeatureMetaInfoResourceUtil");
		if(staticFileConfiguration!=null){
		logger.debug("StaticConfigFileNames..." + staticFileConfiguration.getConfigFile());
		List<ConfigFile> statConfigs = staticFileConfiguration.getConfigFile();
		String tenantID = MeshHeaderConstant.tenant;
		String siteID = MeshHeaderConstant.site;
		ConfigurationContext configurationContext = new ConfigurationContext(tenantID, siteID, featureGroupName,
				feature.getName(), feature.getVendorName(), feature.getVendorVersion());
		try {
			IStaticConfigurationService staticConfigurationService = StaticConfigurationFactory
					.getFilemanagerInstance();
			if (staticConfigurationService instanceof ZookeeperFilemanagementServiceImpl) {
				for (ConfigFile configFile : statConfigs) {
					String localPath = configFile.getFilePath();
					int index = localPath.length() - 1;
					if (!(localPath.charAt(index) == '/')) {
						localPath = localPath + "/";
					} // ..checking and appending '/' at the end of the
						// directory
					logger.debug("ConfigNames..." + configFile.getFileName() + "---: Path specified is..." + localPath);
					try {
						String contents = null;
						contents = new String(Files.readAllBytes(Paths.get(localPath + configFile.getFileName())));
						staticConfigurationService.addStaticConfiguration(configurationContext,
								configFile.getFileName(), contents);
					} catch (StaticConfigDuplicateNameofFileException | AddStaticConfigException
							| AccessProtectionException | InvalidFilePathException e) {
						// This step is to skip the redundancy of file
						logger.debug("File Not saved in zookeeper nodes.. " + configFile.getFileName());
					}
				}
			}
		} catch (InstantiationException | IllegalAccessException | StaticConfigInitializationException e) {
			throw new FeatureMetaInfoResourceException("Unable to instantiate the StaticFileConfiguration", e);
		}
		}
	}// ..end of the method

	/**
	 * this is the method which is called to load, validate and add to the
	 * database
	 * 
	 * @param integrationPipes
	 * @param feature
	 * @param featureGroupName
	 * @throws FeatureMetaInfoResourceException
	 */
	private void checkIntegrationPipeCongigurationsResourceAndLoad(IntegrationPipeLineConfigurations integrationPipes,
			Feature feature, String featureGroupName) throws FeatureMetaInfoResourceException {
		logger.debug(".checkIntegrationPipeCongigurationsResourceAndLoad()..." + integrationPipes);
		if (integrationPipes != null) {
			List<PipeConfiguration> listOfPipeConfigs = integrationPipes.getPipeConfiguration();
			if (listOfPipeConfigs != null) {
				loadPipeConfigResourcesInFeatureMetaInfo(listOfPipeConfigs, featureGroupName, feature.getName(),
						feature.getVendorName(), feature.getVendorVersion());
			}
		}
	}// ..end of the method

	/**
	 * This method is used to load validate and add integration configuration in
	 * database and cache
	 * 
	 * @param featureDataContexts
	 * @param feature
	 * @param featureGroupName
	 * @throws FeatureMetaInfoResourceException
	 */
	private void checkFeatureDataContextsResourceAndLoad(FeatureDataContexts featureDataContexts, Feature feature,
			String featureGroupName) throws FeatureMetaInfoResourceException {
		logger.debug(".checkFeatureDataContextsResourceAndLoad method of FeatureMeshInfoResourceUtil");
		logger.debug("featureDataContexts : " + featureDataContexts);
		if (featureDataContexts != null) {
			List<DataContexts> dataContextsList = featureDataContexts.getDataContexts();
			if (dataContextsList != null) {
				logger.debug("calling method to  load datacontext");
				loadDataContextResourcesInFeatureMetaInfo(dataContextsList, featureGroupName, feature.getName(),
						feature.getVendorName(), feature.getVendorVersion());
			}
		}

	}// end of method checkFeatureDataContextsResourceAndLoad

	private void loadDataContextResourcesInFeatureMetaInfo(List<DataContexts> dataContextsList, String featureGroupName,
			String name, String vendorName, String vendorVersion) throws FeatureMetaInfoResourceException {
		logger.debug(".loadDataContextResourcesInFeatureMetaInfo method of FeatureMeshInfoResourceUtil");
		for (DataContexts dataContexts : dataContextsList) {
			String dataContextResourceName = dataContexts.getResourceName();
			logger.debug("dataContextResourceName : " + dataContextResourceName);
			URL dataContextResourceUrl = FeatureMetaInfoResourceUtil.class.getClassLoader()
					.getResource(dataContextResourceName);
			if (dataContextResourceUrl != null) {
				String dataContextAsSourceAsString = convertXmlToString(dataContextResourceUrl,
						dataContextResourceName);
				if (dataContextAsSourceAsString != null) {
					DataContextConfigXMLParser dataContextXmlParser = new DataContextConfigXMLParser();
					IDataContextConfigurationService dataContextConfigService = new DataContextConfigurationService();
					ConfigurationContext configContext = null;
					try {
						FeatureDataContext featureDataContext = dataContextXmlParser
								.marshallConfigXMLtoObject(dataContextAsSourceAsString);
						configContext = new ConfigurationContext(MeshHeaderConstant.tenant, MeshHeaderConstant.site,
								featureGroupName, name, vendorName, vendorVersion);
						try {
							dataContextConfigService.addDataContext(configContext, featureDataContext);
						} catch (DataContextConfigurationException e) {
							throw new FeatureMetaInfoResourceException(
									"Unable to add configuration file for feature group :  " + featureGroupName
											+ ", feature name : " + name + ", with context data : " + configContext);

						}
					} catch (DataContextParserException e) {
						throw new FeatureMetaInfoResourceException(
								"Unable to parse datacontext configuration file for feature group :  "
										+ featureGroupName + ", feature name : " + name);

					}

				}
			} else {
				logger.debug("No datacontext config xml exist with name : " + dataContextResourceName);
			}
		}

	}// end of method loadDataContextResourcesInFeatureMetaInfo

	/**
	 * This method is used to check and load event resource defined in
	 * featureMetaInfo.xml
	 * 
	 * @param eventResources
	 *            : EventResources Object of FeatureMetaInfo
	 * @param feature
	 *            : feature Name
	 * @param featureGroupName
	 *            : Feature group name
	 * @throws FeatureMetaInfoResourceException
	 */
	private void checkEventResourceAndLoad(EventResources eventResources, Feature feature, String featureGroupName)
			throws FeatureMetaInfoResourceException {
		if (eventResources != null) {
			List<EventResource> eventResourceList = eventResources.getEventResource();
			if (eventResourceList != null) {
				loadEventResourcesInFeatureMetaInfo(eventResourceList, featureGroupName, feature.getName());
			} else {
				logger.debug("No EventResource is defined in FeatureMetaInfo for feature Group : " + featureGroupName
						+ ", feature name : " + feature + " but empty");
			}
		} else {
			logger.debug("No EventResource configured in FeatureMetaInfo for feature Group : " + featureGroupName
					+ ", feature name : " + feature);
		}
	}

	/**
	 * This method is used to check and load permastore resource defined in
	 * featureMetaInfo.xml
	 * 
	 * @param permastoreConfiguration
	 *            : PermaStoreConfigurations Object of FeatureMetaInfo
	 * @param feature
	 *            : feature Name
	 * @param featureGroupName
	 *            : Feature group name
	 * @throws FeatureMetaInfoResourceException
	 */
	private void checkPermastoreResourceAndLoad(
			com.getusroi.featuremetainfo.jaxb.PermaStoreConfigurations permastoreConfiguration, Feature feature,
			String featureGroupName) throws FeatureMetaInfoResourceException {
		if (permastoreConfiguration != null) {
			List<PermaStoreConfiguration> permastoreConfigList = permastoreConfiguration.getPermaStoreConfiguration();
			if (permastoreConfigList != null) {
				loadPermastoreResourceInFeatureMetaInfo(permastoreConfigList, featureGroupName, feature.getName(),
						feature.getVendorName(), feature.getVendorVersion());
			} else {
				logger.debug("No PermastoreResource is defined in FeatureMetaInfo for feature Group : "
						+ featureGroupName + ", feature name : " + feature + " but empty");
			}
		} else {
			logger.debug("No PermastoreResource configured in FeatureMetaInfo for feature Group : " + featureGroupName
					+ ", feature name : " + feature);
		}
	}

	/**
	 * This method is used to check and load PolicyResource resource defined in
	 * featureMetaInfo.xml
	 * 
	 * @param policyConfiguration
	 *            : PolicyConfigurations Object of FeatureMetaInfo
	 * @param feature
	 *            : feature Name
	 * @param featureGroupName
	 *            : Feature group name
	 * @throws FeatureMetaInfoResourceException
	 */
	private void checkPolicyResourceAndLoad(PolicyConfigurations policyConfiguration, Feature feature,
			String featureGroupName) throws FeatureMetaInfoResourceException {
		if (policyConfiguration != null) {
			List<PolicyConfiguration> policyConfigList = policyConfiguration.getPolicyConfiguration();
			if (policyConfigList != null) {
				loadPolicyResourceInFeatureMetaInfo(policyConfigList, featureGroupName, feature.getName(),
						feature.getVendorName(), feature.getVendorVersion());
			} else {
				logger.debug("No PolicyResource is defined in FeatureMetaInfo for feature Group : " + featureGroupName
						+ ", feature name : " + feature + " but empty");
			}
		} else {
			logger.debug("No PolicyResource configured in FeatureMetaInfo for feature Group : " + featureGroupName
					+ ", feature name : " + feature);
		}
	}

	/**
	 * This method is used to check and load FeatureImplResource resource
	 * defined in featureMetaInfo.xml
	 * 
	 * @param FeatureImplementations
	 *            : FeatureImplementations Object of FeatureMetaInfo
	 * @param feature
	 *            : feature Name
	 * @param featureGroupName
	 *            : Feature group name
	 * @throws FeatureMetaInfoResourceException
	 */
	private void checkFeatureImplResourceAndLoad(FeatureImplementations featureImplementations, Feature feature,
			String featureGroupName) throws FeatureMetaInfoResourceException {
		if (featureImplementations != null) {
			List<FeatureImplementation> featureImplList = featureImplementations.getFeatureImplementation();
			if (featureImplList != null) {
				loadFeatureResourceInFeatureMetaInfo(featureImplList, featureGroupName, feature.getName(),
						feature.getVendorName(), feature.getVendorVersion());
			} else {
				logger.debug("No FeatureImplResource is defined in FeatureMetaInfo for feature Group : "
						+ featureGroupName + ", feature name : " + feature + " but empty");
			}
		} else {
			logger.debug("No FeatureImplResource configured in FeatureMetaInfo for feature Group : " + featureGroupName
					+ ", feature name : " + feature);
		}
	}

	/**
	 * This method is used to check and load DynastoreResource resource defined
	 * in featureMetaInfo.xml
	 * 
	 * @param dynastoreConfiguration
	 *            : DynaStoreConfigurations Object of FeatureMetaInfo
	 * @param feature
	 *            : feature Name
	 * @param featureGroupName
	 *            : Feature group name
	 * @throws FeatureMetaInfoResourceException
	 */
	private void checkDynastoreResourceAndLoad(DynaStoreConfigurations dynastoreConfiguration, Feature feature,
			String featureGroupName) throws FeatureMetaInfoResourceException {
		if (dynastoreConfiguration != null) {
			List<DynaStoreConfiguration> dynaStoreConfigList = feature.getDynaStoreConfigurations()
					.getDynaStoreConfiguration();
			if (dynaStoreConfigList != null) {
				loadDynastoreResourceInFeatureMetaInfo(dynaStoreConfigList, featureGroupName, feature.getName(),
						feature.getVendorName(), feature.getVendorVersion());
			} else {
				logger.debug("No DynastoreResource is defined in FeatureMetaInfo for feature Group : "
						+ featureGroupName + ", feature name : " + feature + " but empty");
			}
		} else {
			logger.debug("No DynastoreResource configured in FeatureMetaInfo for feature Group : " + featureGroupName
					+ ", feature name : " + feature);
		}
	}

	private void loadDynastoreResourceInFeatureMetaInfo(List<DynaStoreConfiguration> dynaStoreConfigList,
			String featureGroupName, String name, String vendorName, String version)
			throws FeatureMetaInfoResourceException {
		logger.debug(".getDynastoreResourceInFeatureMetaInfo of FeatureMetaInfoResourceUtil");
		for (DynaStoreConfiguration dynastoreconfig : dynaStoreConfigList) {
			String dynaResourceName = dynastoreconfig.getResourceName();
			logger.debug("dynaResourceName : " + dynaResourceName);
			URL dynaResourceUrl = FeatureMetaInfoResourceUtil.class.getClassLoader().getResource(dynaResourceName);
			if (dynaResourceUrl != null) {
				String dynastoreAsSourceAsString = convertXmlToString(dynaResourceUrl, dynaResourceName);
				if (dynastoreAsSourceAsString != null) {
					DynaStoreConfigXmlParser parser = new DynaStoreConfigXmlParser();
					IDynaStoreConfigurationService iDynaStoreConfigurationService = null;
					ConfigurationContext configContext = null;
					try {
						DynastoreConfigurations dynastoreConfigurations = parser
								.marshallConfigXMLtoObject(dynastoreAsSourceAsString);
						configContext = new ConfigurationContext(MeshHeaderConstant.tenant, MeshHeaderConstant.site,
								featureGroupName, name, vendorName, version);
						iDynaStoreConfigurationService = new DynaStoreConfigurationService();
						List<DynastoreConfiguration> dynastoreConfigList = dynastoreConfigurations
								.getDynastoreConfiguration();
						for (DynastoreConfiguration dynaConfig : dynastoreConfigList) {
							try {
								iDynaStoreConfigurationService.addDynaStoreConfiguration(configContext, dynaConfig);
							} catch (DynaStoreConfigurationException e) {
								throw new FeatureMetaInfoResourceException(
										"Unable to add configuration file for feature group :  " + featureGroupName
												+ ", feature name : " + name + ", with context data : "
												+ configContext);
							}
						} // end of for
					} catch (DynaStoreConfigParserException e) {
						throw new FeatureMetaInfoResourceException(
								"Unable to parse dynastore configuration file for feature group :  " + featureGroupName
										+ ", feature name : " + name);
					}

				} // end of if(dynastoreAsSourceAsString !=null)
			} else {
				logger.debug("No dynastore config xml exist with name : " + dynaResourceName);
			}
		} // end of for
	}// end of method

	private void loadEventResourcesInFeatureMetaInfo(List<EventResource> eventResourceList, String featureGroupName,
			String featureName) throws FeatureMetaInfoResourceException {
		logger.debug(".getEventResourcesInFeatureMetaInfo of FeatureMetaInfoResourceUtil");
		for (EventResource eventresource : eventResourceList) {
			String eventResourceName = eventresource.getResourceName();
			URL eventResourceUrl = FeatureMetaInfoResourceUtil.class.getClassLoader().getResource(eventResourceName);
			if (eventResourceUrl != null) {
				logger.debug("eventResourceUrl : " + eventResourceUrl);
				String eventSourceAsString = convertXmlToString(eventResourceUrl, eventresource.getResourceName());
				if (eventresource != null) {
					EventFrameworkXmlHandler parser = new EventFrameworkXmlHandler();
					EventFramework eventFrameworkConfig = null;
					IEventFrameworkConfigService eventConfigService = new EventFrameworkConfigService();
					try {
						eventFrameworkConfig = parser.marshallConfigXMLtoObject(eventSourceAsString);
						// prepare the configcontext for eventing
						ConfigurationContext configContext = new ConfigurationContext(MeshHeaderConstant.tenant,
								MeshHeaderConstant.site, featureGroupName, featureName);
						// check dispatcher defined or not and then load
						// configuration
						DispatchChanels dispatcherChanel = eventFrameworkConfig.getDispatchChanels();
						loadEventChanelConfiguration(dispatcherChanel, eventConfigService, configContext);

						// check SystemEvents defined or not and then load
						// configuration
						SystemEvents systemEvent = eventFrameworkConfig.getSystemEvents();
						loadSystemEventConfiguration(systemEvent, eventConfigService, configContext);

						// check Events defined or not and then load
						// configuration
						Events events = eventFrameworkConfig.getEvents();
						loadEventConfiguration(events, eventConfigService, configContext);

						// check event subscription defined or not and then load
						// configuration
						EventSubscriptions eventSusbscriptions = eventFrameworkConfig.getEventSubscriptions();
						loadEventSubscriptionConfiguration(eventSusbscriptions, eventConfigService, configContext);
					} catch (EventFrameworkConfigParserException e) {
						throw new FeatureMetaInfoResourceException(
								"Unable to parse event file : " + eventresource.getResourceName());
					}
				} // end of if(eventresource !=null)
				logger.debug("exiting getEventResourcesInFeatureMetaInfo of FeatureMetaInfoResourceUtil");
			} else {
				logger.debug("no such event source defined : " + eventResourceName);
			}
		} // end of for
	}// end of method

	/**
	 * This method is used to add event susbscription for the event in cache and
	 * in db
	 * 
	 * @param eventSusbscriptions
	 *            : EventSubscriptions Object
	 * @param eventConfigService
	 *            : EventFrameworkConfigService Object
	 * @param configContext
	 *            : ConfigurationContext Object
	 * @throws FeatureMetaInfoResourceException
	 */
	private void loadEventSubscriptionConfiguration(EventSubscriptions eventSusbscriptions,
			IEventFrameworkConfigService eventConfigService, ConfigurationContext configContext)
			throws FeatureMetaInfoResourceException {
		logger.debug(".loadEventSubscriptionConfiguration of FeatureMetaInfoResourceUtil");
		if (eventSusbscriptions != null) {
			List<EventSubscription> eventSubscriptionList = eventSusbscriptions.getEventSubscription();
			for (EventSubscription eventSubscription : eventSubscriptionList) {
				try {
					eventConfigService.addEventFrameworkConfiguration(configContext, eventSubscription);
				} catch (EventFrameworkConfigurationException e) {
					throw new FeatureMetaInfoResourceException("Error in adding eventSubscription configuration ", e);

				}
			} // end of for loop
		} else {
			logger.debug("Event subscription is undefined");

		}

	}// end of method

	/**
	 * This method is used to add channel configuration of event
	 * 
	 * @param dispatcherChanel
	 *            : DispatchChanels Object
	 * @param eventConfigService
	 *            : EventFrameworkConfigService Object
	 * @param configContext
	 *            : ConfigurationContext Object
	 * @throws FeatureMetaInfoResourceException
	 */
	private void loadEventChanelConfiguration(DispatchChanels dispatcherChanel,
			IEventFrameworkConfigService eventConfigService, ConfigurationContext configContext)
			throws FeatureMetaInfoResourceException {
		logger.debug(".loadEventChanelConfiguration of FeatureMetaInfoResourceUtil");
		if (dispatcherChanel != null) {
			List<DispatchChanel> disChanelList = dispatcherChanel.getDispatchChanel();
			// addchanel init cache
			for (DispatchChanel disChanelConfig : disChanelList) {
				try {
					eventConfigService.addEventFrameworkConfiguration(configContext, disChanelConfig);
				} catch (EventFrameworkConfigurationException e) {
					throw new FeatureMetaInfoResourceException("Error in adding channel configuration ", e);

				}
			} // end of for loop
		} else {
			logger.debug("dispatcher channel is not defined for the event configuration");
		}
	}// end of method

	/**
	 * This method is used to add system event configuration of event
	 * 
	 * @param systemEvent
	 *            : SystemEvents Object
	 * @param eventConfigService
	 *            : EventFrameworkConfigService Object
	 * @param configContext
	 *            : ConfigurationContext Object
	 * @throws FeatureMetaInfoResourceException
	 */
	private void loadSystemEventConfiguration(SystemEvents systemEvents,
			IEventFrameworkConfigService eventConfigService, ConfigurationContext configContext)
			throws FeatureMetaInfoResourceException {
		logger.debug(".loadSystemEventConfiguration of FeatureMetaInfoResourceUtil");
		if (systemEvents != null) {
			List<SystemEvent> systemEventList = systemEvents.getSystemEvent();
			// add system events
			for (SystemEvent systemEvent : systemEventList) {
				List<EventDispatcher> eventDispacherList = systemEvent.getEventDispatchers().getEventDispatcher();
				for (EventDispatcher eventDispacher : eventDispacherList) {
					String transformationtype = eventDispacher.getEventTransformation().getType();
					if (transformationtype.equalsIgnoreCase("XML-XSLT")) {
						logger.debug("event for whom xslt defined : " + systemEvent);
						String xslName = eventDispacher.getEventTransformation().getXSLTName();
						URL xslUrl = FeatureMetaInfoResourceUtil.class.getClassLoader().getResource(xslName);
						logger.debug("xsl url : " + xslUrl + " for xslt name : " + xslName);
						String xslAsString = convertXmlToString(xslUrl, xslName);
						logger.debug("xslt As String : " + xslAsString);
						eventDispacher.getEventTransformation().setXsltAsString(xslAsString);
					}
				}
				try {
					eventConfigService.addEventFrameworkConfiguration(configContext, systemEvent);
				} catch (EventFrameworkConfigurationException e) {
					throw new FeatureMetaInfoResourceException("Error in adding System event configuration ", e);
				}
			} // end of for(SystemEvent systemEvent : systemEventList)
		} else {
			logger.debug("System event is not defined for the event configuration");
		}
	}// end of method

	/**
	 * This method is used to add event configuration of event
	 * 
	 * @param events
	 *            : Events Object
	 * @param eventConfigService
	 *            : EventFrameworkConfigService Object
	 * @param configContext
	 *            : ConfigurationContext Object
	 * @throws FeatureMetaInfoResourceException
	 */
	private void loadEventConfiguration(Events events, IEventFrameworkConfigService eventConfigService,
			ConfigurationContext configContext) throws FeatureMetaInfoResourceException {
		logger.debug(".loadSystemEventConfiguration of FeatureMetaInfoResourceUtil");
		if (events != null) {
			List<Event> eventList = events.getEvent();
			// add events
			for (Event event : eventList) {
				List<EventDispatcher> eventDispacherList = event.getEventDispatchers().getEventDispatcher();
				for (EventDispatcher eventDispacher : eventDispacherList) {
					String transformationtype = eventDispacher.getEventTransformation().getType();
					if (transformationtype.equalsIgnoreCase("XML-XSLT")) {
						logger.debug("event for which xslt defined : " + event.getId());
						String xslName = eventDispacher.getEventTransformation().getXSLTName();
						URL xslUrl = FeatureMetaInfoResourceUtil.class.getClassLoader().getResource(xslName);
						logger.debug("xsl url : " + xslUrl + " for xslt name : " + xslName);
						String xslAsString = convertXmlToString(xslUrl, xslName);
						logger.debug("xslt As String : " + xslAsString);
						eventDispacher.getEventTransformation().setXsltAsString(xslAsString);
					}
				}
				try {
					eventConfigService.addEventFrameworkConfiguration(configContext, event);
				} catch (EventFrameworkConfigurationException e) {
					throw new FeatureMetaInfoResourceException("Error in adding  event configuration ", e);

				}

			}
		} else {
			logger.debug("events is not defined for the event configuration");
		}
	}// end of method

	private void loadPermastoreResourceInFeatureMetaInfo(List<PermaStoreConfiguration> permastoreConfigList,
			String featureGroupName, String featureName, String vendorName, String version)
			throws FeatureMetaInfoResourceException {
		logger.debug(".getPermastoreResourceInFeatureMetaInfo of FeatureMetaInfoResourceUtil");
		for (PermaStoreConfiguration permastore : permastoreConfigList) {
			String permastoreResourceName = permastore.getResourceName();
			URL permastoreResourceUrl = FeatureMetaInfoResourceUtil.class.getClassLoader()
					.getResource(permastoreResourceName);
			if (permastoreResourceUrl != null) {
				String permastoreSourceAsString = convertXmlToString(permastoreResourceUrl,
						permastore.getResourceName());
				if (permastoreSourceAsString != null) {
					PermaStoreConfigXMLParser parmastoreConfigParser = new PermaStoreConfigXMLParser();
					PermaStoreConfigurations permastorConfig = null;
					try {
						permastorConfig = parmastoreConfigParser.marshallConfigXMLtoObject(permastoreSourceAsString);
						List<com.getusroi.permastore.config.jaxb.PermaStoreConfiguration> permastoreConfigList1 = permastorConfig
								.getPermaStoreConfiguration();
						for (com.getusroi.permastore.config.jaxb.PermaStoreConfiguration permaStoreConfiguration : permastoreConfigList1) {
							String configname = permaStoreConfiguration.getName();
							IPermaStoreConfigurationService psConfigService = new PermaStoreConfigurationService();
							RequestContext requestContext = new RequestContext(MeshHeaderConstant.tenant,
									MeshHeaderConstant.site, featureGroupName, featureName, vendorName, version);
							ConfigurationContext configurationContext = new ConfigurationContext(
									MeshHeaderConstant.tenant, MeshHeaderConstant.site, featureGroupName, featureName,
									vendorName, version);
							try {
								boolean isExist = psConfigService.checkPermaStoreConfigarationExistOrNot(
										configurationContext, permaStoreConfiguration.getName());
								if (!isExist) {
									psConfigService.addPermaStoreConfiguration(configurationContext,
											permaStoreConfiguration);

								} else {
									logger.debug("Permastore configuration for : " + configname
											+ "already exist for featuregroup : " + featureGroupName + " and feature : "
											+ featureName + " in db");
								}

							} catch (PermaStoreConfigurationException | PermaStoreConfigRequestException e) {
								throw new FeatureMetaInfoResourceException(
										"error in loading the PermastoreConfiguration ", e);

							}
						} // end of if(builderType.equalsIgnoreCase("CUSTOM"))
					} catch (PermaStoreConfigParserException e) {
						throw new FeatureMetaInfoResourceException(
								"Unable to parse permastore file : " + permastore.getResourceName());
					}
				} // end of for
			} else {
				logger.debug("No permastore config xml defined for : " + permastoreResourceName);
			}
		}
		logger.debug("exiting getPermastoreResourceInFeatureMetaInfo of FeatureMetaInfoResourceUtil");

	}// end of method

	/**
	 * This method is used to load the the policy configuration in db and cache
	 * 
	 * @param policyConfigList
	 *            : List<PolicyConfiguration> Object
	 * @param featureGroupName
	 *            : feature group name
	 * @param featureName
	 *            : feature name
	 * @throws FeatureMetaInfoResourceException
	 */
	private void loadPolicyResourceInFeatureMetaInfo(List<PolicyConfiguration> policyConfigList,
			String featureGroupName, String featureName, String vendorName, String version)
			throws FeatureMetaInfoResourceException {
		logger.debug(".getPolicyResourceInFeatureMetaInfo of FeatureMetaInfoResourceUtil");
		for (PolicyConfiguration policyconfig : policyConfigList) {
			String policyconfigResourceName = policyconfig.getResourceName();
			URL policyResourceUrl = FeatureMetaInfoResourceUtil.class.getClassLoader()
					.getResource(policyconfigResourceName);
			if (policyResourceUrl != null) {
				String policyconfigSourceAsString = convertXmlToString(policyResourceUrl,
						policyconfig.getResourceName());
				if (policyconfigSourceAsString != null) {
					PolicyConfigXMLParser policyParser = new PolicyConfigXMLParser();
					try {
						Policies policies = policyParser.marshallConfigXMLtoObject(policyconfigSourceAsString);
						List<Policy> policyList = policies.getPolicy();
						if (!(policyList.isEmpty()) || policyList != null) {
							for (Policy policy : policyList) {
								logger.debug("policy related info : " + policy.getPolicyName());
								IPolicyConfigurationService policyConfigService = new PolicyConfigurationService();
								PolicyRequestContext policyRequestContext = new PolicyRequestContext(
										MeshHeaderConstant.tenant, MeshHeaderConstant.site, featureGroupName,
										featureName, vendorName, version);
								ConfigurationContext configurationContext = new ConfigurationContext(
										MeshHeaderConstant.tenant, MeshHeaderConstant.site, featureGroupName,
										featureName, vendorName, version);
								try {
									boolean isExist = policyConfigService
											.checkPolicyExistInDbAndCache(configurationContext, policy.getPolicyName());
									if (!isExist) {
										policyConfigService.addPolicyConfiguration(configurationContext, policy);
									} else {
										logger.debug("Policy configuration for : " + policy.getPolicyName()
												+ "already exist for featuregroup : " + featureGroupName
												+ " and feature : " + featureName + " in db");
									}
								} catch (PolicyConfigurationException | PolicyRequestException e) {
									throw new FeatureMetaInfoResourceException(
											"error in loading the policyConfiguration for policy = "
													+ policy.getPolicyName(),
											e);
								}
							} // end of for loop
						}
					} catch (PolicyConfigXMLParserException e) {
						throw new FeatureMetaInfoResourceException(
								"Unable to parse policy file : " + policyconfig.getResourceName());
					}

				} // end of if(policyconfigSourceAsString !=null)
			} else {
				logger.debug("No policy config defined for policy : " + policyconfigResourceName);
			}
		}
		logger.debug("exiting getPolicyResourceInFeatureMetaInfo of FeatureMetaInfoResourceUtil");

	}// end of method

	/**
	 * This method is used to load the the feature configuration in db and cache
	 * 
	 * @param featureImplList
	 *            : List<FeatureImplementation> Object
	 * @param featureGroupName
	 *            : feature group name
	 * @param featureName
	 *            : feature Name
	 * @throws FeatureMetaInfoResourceException
	 */
	private void loadFeatureResourceInFeatureMetaInfo(List<FeatureImplementation> featureImplList,
			String featureGroupName, String featureName, String vendor, String version)
			throws FeatureMetaInfoResourceException {
		logger.debug(".getFeatureResourceInFeatureMetaInfo of FeatureMetaInfoResourceUtil");
		for (FeatureImplementation featureImpl : featureImplList) {
			String featureImplResourceName = featureImpl.getResourceName();
			URL featureImplResourceUrl = FeatureMetaInfoResourceUtil.class.getClassLoader()
					.getResource(featureImplResourceName);
			if (featureImplResourceUrl != null) {
				String featureImplSourceAsString = convertXmlToString(featureImplResourceUrl,
						featureImpl.getResourceName());
				logger.debug("feature as String : " + featureImplSourceAsString);
				if (featureImplSourceAsString != null) {
					logger.debug(".feature as string is not null");
					FeatureConfigXMLParser featureparser = new FeatureConfigXMLParser();
					FeaturesServiceInfo feaureServiceInfo = null;
					try {
						feaureServiceInfo = featureparser.marshallConfigXMLtoObject(featureImplSourceAsString);
					} catch (FeatureConfigParserException e) {
						throw new FeatureMetaInfoResourceException(
								"Unable to parse feature file : " + featureImpl.getResourceName());
					}
					// #TODO we have to convert it to a List implementation
					com.getusroi.feature.jaxb.Feature feature1 = feaureServiceInfo.getFeatures().getFeature();
					logger.debug("feature related info : " + feature1.getFeatureName());
					IFeatureConfigurationService featureConfigService = new FeatureConfigurationService();
					FeatureConfigRequestContext requestContext = new FeatureConfigRequestContext(
							MeshHeaderConstant.tenant, MeshHeaderConstant.site, featureGroupName, featureName, vendor,
							version);
					ConfigurationContext configurationContext = requestContext.getConfigurationContext();
					try {
						boolean isExist = featureConfigService.checkFeatureExistInDBAndCache(configurationContext,
								feature1.getFeatureName());
						if (!isExist) {
							featureConfigService.addFeatureConfiguration(configurationContext, feature1);
						} else {
							logger.debug("feature configuration for : " + feature1.getFeatureName()
									+ "already exist for featuregroup : " + featureGroupName + " and feature : "
									+ featureName + " in db");
						}
					} catch (FeatureConfigurationException | FeatureConfigRequestException e) {
						throw new FeatureMetaInfoResourceException(
								"error in loading the feature Configuration for feature = " + feature1.getFeatureName(),
								e);

					}
				} // end of for loop
			} // end of if(featureImplSourceAsString!=null)
		}
		logger.debug("exiting getFeatureResourceInFeatureMetaInfo of FeatureMetaInfoResourceUtil");
	}// end of method

	/**
	 * this is to load the pipeConfigurationfrom the resources
	 * 
	 * @param listOfPipeConfigs
	 * @param featureGroupName
	 * @param name
	 * @param vendorName
	 * @param vendorVersion
	 * @throws FeatureMetaInfoResourceException
	 */
	private void loadPipeConfigResourcesInFeatureMetaInfo(List<PipeConfiguration> listOfPipeConfigs,
			String featureGroupName, String name, String vendorName, String vendorVersion)
			throws FeatureMetaInfoResourceException {
		for (PipeConfiguration pipeConfiguration : listOfPipeConfigs) {
			String pipeConfigResourceName = pipeConfiguration.getResourceName();
			logger.debug("Loading IntegrationpipeConfiguration with name: " + pipeConfigResourceName);
			URL pipeConfigResourceUrl = FeatureMetaInfoResourceUtil.class.getClassLoader()
					.getResource(pipeConfigResourceName);
			if (pipeConfigResourceUrl != null) {
				String pipeConfigasSring = convertXmlToString(pipeConfigResourceUrl, pipeConfigResourceName);
				if (pipeConfigasSring != null) {
					IntegrationPipelineConfigXmlParser pipelineConfigXmlParser = new IntegrationPipelineConfigXmlParser();
					IIntegrationPipeLineConfigurationService pipeLineConfigurationService = new IntegrationPipelineConfigurationService();
					ConfigurationContext configContext = null;
					try {
						IntegrationPipes pipes = pipelineConfigXmlParser.unmarshallConfigXMLtoObject(pipeConfigasSring);
						configContext = new ConfigurationContext(MeshHeaderConstant.tenant, MeshHeaderConstant.site,
								featureGroupName, name, vendorName, vendorVersion);
						try {
							pipeLineConfigurationService.addIntegrationPipelineConfiguration(configContext,
									pipes.getIntegrationPipe().get(0));
						} catch (IntegrationPipelineConfigException e) {
							throw new FeatureMetaInfoResourceException(
									"Unable to load IntegrationPipeConfiguration to the database..", e);
						}
					} catch (IntegrationPipelineConfigParserException e) {
						throw new FeatureMetaInfoResourceException(
								"Unable to add Integrationconfiguration file for feature group :  " + featureGroupName
										+ ", featureName : " + name + ", with pipe data : " + configContext,
								e);
					}
				}
			}
		}
	}// ..end of the method

}