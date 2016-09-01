package com.getusroi.printservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.getusroi.mesh.feature.routing.ExecutionFeatureDynamic;

public class PrintServicePrintNodePropertiesHelper {

	/**
	 * This method is to load the properties file from the classpath
	 * @param filetoload : name of the file to be loaded
	 * @return Properties Object
	 * @throws PrintNodePropertiesLoadingException
	 */
	public Properties loadingPropertiesFile() throws PrintNodePropertiesLoadingException {
		Properties prop = new Properties();
		InputStream input1 = PrintServicePrintNodePropertiesHelper.class.getClassLoader().getResourceAsStream(PrintServicePrintNodeConstant.PRINTNODE_PROPERTY_FILE);
		try {
			prop.load(input1);
		} catch (IOException e) {
			throw new PrintNodePropertiesLoadingException("unable to load property file = " +PrintServicePrintNodeConstant.PRINTNODE_PROPERTY_FILE, e);
		}
		return prop;
	}
}
