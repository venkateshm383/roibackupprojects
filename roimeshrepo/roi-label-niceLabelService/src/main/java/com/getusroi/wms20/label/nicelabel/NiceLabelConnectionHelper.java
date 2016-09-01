package com.getusroi.wms20.label.nicelabel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class is used to get the properties required to connect with niceLabel
 * 
 * @author bizruntime
 *
 */
public class NiceLabelConnectionHelper {

	/**
	 * This method is used to read nice label connection properties
	 * 
	 * @return Properties Object
	 * @throws NiceLabelConnectionLoadingException
	 */
	public Properties getNiceLabelConnection() throws NiceLabelConnectionLoadingException {
		String url = null;
		Properties properties = new Properties();
		InputStream inStream = NiceLabelConnectionHelper.class.getClassLoader()
				.getResourceAsStream(NiceLabelPropertyConstant.NICELABEL_CONNECTION_FILE);
		if (inStream != null) {
			try {
				properties.load(inStream);
				return properties;
			} catch (IOException e) {
				throw new NiceLabelConnectionLoadingException(
						"Unable to load file : " + NiceLabelPropertyConstant.NICELABEL_CONNECTION_FILE, e);

			}
		} else {
			throw new NiceLabelConnectionLoadingException(
					"PropertyFile " + NiceLabelPropertyConstant.NICELABEL_CONNECTION_FILE + " not found in classpath ");

		}
	}// end of method

}
