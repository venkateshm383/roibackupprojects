package com.getusroi.wms20.label.bartender;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BartenderConfigReader {
	
	public Properties readBartenderConfigFile(){
		
		Properties properties=new Properties();
		
		InputStream inputStream=BartenderConfigReader.class.getClassLoader().getResourceAsStream(BartenderPropertyConstants.BARTENDR_CONFIG_FILE);
		
		try {
			properties.load(inputStream);
			
		} catch (IOException e) {
			new BartenderConfigLoaderException("Error in loading BartenderConfiguration with given configName="+BartenderPropertyConstants.BARTENDR_CONFIG_FILE);
		}
		
	return properties;
	}

}
