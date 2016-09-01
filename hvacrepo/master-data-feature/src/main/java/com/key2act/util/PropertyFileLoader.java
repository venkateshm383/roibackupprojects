package com.key2act.util;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyFileLoader {

	private static Properties prop = null;
	private static final Logger logger = LoggerFactory.getLogger(PropertyFileLoader.class);
	static {
		prop = new Properties();
		InputStream inputStream;
		try {
			inputStream = ClassLoader.class.getResourceAsStream("/errorCode.properties");
			prop.load(inputStream);
		} catch (Exception e) {
			logger.error("Property File Not Found");
		}
	}

	public static Properties getProperties() {
		return prop;
	}

}
