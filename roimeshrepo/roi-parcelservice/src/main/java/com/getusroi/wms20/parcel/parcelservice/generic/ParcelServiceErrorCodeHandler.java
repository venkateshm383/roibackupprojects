package com.getusroi.wms20.parcel.parcelservice.generic;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.getusroi.wms20.parcel.parcelservice.exception.ParcelServiceErrorCodePropertiesReaderException;

/**
 * 
 * @author bizruntime
 *
 *         class is to handle the error codes returned by the individual
 *         carriers and setting the Generic codes ~ [roi-parcelservice]..
 */
public class ParcelServiceErrorCodeHandler {

	private Logger logger = Logger.getLogger(ParcelServiceErrorCodeHandler.class);
	private final String PROPERTIES_ERRORCODE = "PARCEL_SERVICE_GENERIC_ERROR_CODES.properties";

	/**
	 * 
	 * @param genericErrorCodeInput
	 * @param errorCodeList
	 * @return
	 * @throws ParcelServiceErrorCodePropertiesReaderException
	 */
	public Map<String, String> errorCodeSetter(String genericErrorCodeInput, String defaultErrorMessage)
			throws ParcelServiceErrorCodePropertiesReaderException {
		logger.debug("the error code "+genericErrorCodeInput+" and the message in errorcode setter is: "+defaultErrorMessage);
		String errorMessage = null;
		try {
			Properties properties = loadGenericErrorCodePropertiesFile();
			errorMessage = properties.getProperty(genericErrorCodeInput);
		} catch (IOException e) {
			throw new ParcelServiceErrorCodePropertiesReaderException("Unable to read the generic error codes properties file: ",e);
		}
		Map<String, String> outMap = new HashMap<>();
		outMap.put("ParcelServiceErrorMessage", errorMessage);
		outMap.put("ParcelServiceErrorCode", genericErrorCodeInput);
		outMap.put("ParcelServiceError-InDetail", defaultErrorMessage);
		return outMap;
	}// ..end of the method

	/**
	 * to load - PARCEL_SERVICE_GENERIC_ERROR_CODES.properties - Properties file
	 * 
	 * @return Properties
	 * @throws ParcelServiceErrorCodePropertiesReaderException
	 * @throws IOException
	 */
	private Properties loadGenericErrorCodePropertiesFile() throws IOException {
		Properties prop = new Properties();
		InputStream input = ParcelServiceErrorCodeHandler.class.getClassLoader().getResourceAsStream(PROPERTIES_ERRORCODE);
		prop.load(input);
		return prop;
	}//..end of the method
	

}
