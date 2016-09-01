package com.getusroi.wms20.label.builder;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.getusroi.permastore.config.IPermaStoreCustomCacheObjectBuilder;
import com.getusroi.permastore.config.jaxb.CustomBuilder;
import com.getusroi.wms20.label.LabelPropertyConstant;
import com.getusroi.wms20.label.service.vo.PrinterConfig;

public class PrinterConfigBuilder implements IPermaStoreCustomCacheObjectBuilder {
	final static Logger logger = LoggerFactory.getLogger(LabelTemplateBuilder.class);
	private static Properties PRINTER_CONFIG_PROP = new Properties();

	/**
	 * This method is used to load printer config in Permastore
	 * @param CustomBuilder : CustomBuilder Object
	 */
	@Override
	public Serializable loadDataForCache(CustomBuilder arg0) {
		logger.debug(".loadDataForCache of PrinterConfigBuilder ");

		try {
			loadPrinterConfig();
		} catch (LabelFileLoadingException e) {
			logger.error("Unable to load printerConfig");
		}
		Enumeration enum1 = PRINTER_CONFIG_PROP.keys();
		Map<Integer, PrinterConfig> printerConfigMap = new HashMap<>();
		while (enum1.hasMoreElements()) {
			String printerID = (String) enum1.nextElement();
			String outputType = PRINTER_CONFIG_PROP.getProperty(printerID);
			logger.debug("printerId : " + printerID + ", output type : " + outputType);
			int printerId = Integer.parseInt(printerID);
			PrinterConfig printerConfig = new PrinterConfig();
			printerConfig.setPrinterID(printerId);
			printerConfig.setOutputType(outputType);
			printerConfigMap.put(printerId, printerConfig);
		}
		return (Serializable) printerConfigMap;
	}

	/**
	 * This method is used to load printer config detail 
	 * @throws LabelFileLoadingException 
	 */
	public void loadPrinterConfig() throws LabelFileLoadingException {
		// getting property file from classpath
		InputStream input = PrinterConfigBuilder.class.getClassLoader()
				.getResourceAsStream(LabelPropertyConstant.PRINTER_CONFIG);

		// load the properties file

		try {
			PRINTER_CONFIG_PROP.load(input);
			logger.debug(" files name" + PRINTER_CONFIG_PROP.toString());
		} catch (IOException e) {
			throw new LabelFileLoadingException("Problem in loading the file : " + LabelPropertyConstant.PRINTER_CONFIG, e);

		}

	}

}
