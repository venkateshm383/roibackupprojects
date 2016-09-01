package com.key2act.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.key2act.pojo.PriceMatrix;
import com.key2act.service.InvalidPriceMatrixRequest;
import com.key2act.service.PriceMatrixNotFoundException;
import com.key2act.util.ErrorCodeConstant;
import com.key2act.util.PropertyFileLoader;

/**
 * this class is used to perform dao operation of price matrix
 * @author bizruntime
 *
 */
public class PriceMatrixDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(PriceMatrixDAO.class);
	private static Properties prop = PropertyFileLoader.getProperties();
	
	/**
	 * this method is used to get price matrix details
	 * @param: priceMatrixItem, based on priceMatrixItem, data of price matrix will get 
	 * @return: it return the all data of price matrix in list object
	 * @throws PriceMatrixNotFoundException, If data is not found, it'll throw exception
	 * @throws InvalidPriceMatrixRequest if priceMatrixItem is null then it throw InvalidPriceMatrixRequest
	 */
	public List<PriceMatrix> getPriceMatrix(String priceMatrixItem) throws PriceMatrixNotFoundException, InvalidPriceMatrixRequest {
		logger.debug("Inside getPriceMatrix method of PriceMatrixDao");
		
		if(priceMatrixItem == null)
			throw new InvalidPriceMatrixRequest("Invalid Price Matrix Request");
		
		List<PriceMatrix> matrixs = getPriceMatrixData();
		ArrayList<PriceMatrix> resultMatrixs = new ArrayList<>();
		for(PriceMatrix priceMatrix : matrixs) {
			if(priceMatrix.getPriceMatrixItem().equalsIgnoreCase(priceMatrixItem))
				resultMatrixs.add(priceMatrix);
		}
		if(resultMatrixs.isEmpty()) {
			logger.debug("Price Matrix is Not available");
			throw new PriceMatrixNotFoundException("Error Code:" + ErrorCodeConstant.PRICE_MATRIX_ERROR_CODE + ", " + prop.getProperty(ErrorCodeConstant.PRICE_MATRIX_ERROR_CODE) + priceMatrixItem);
		}
		return resultMatrixs;
	}//end of method
	
	/**
	 * this method is used to get all price matrix
	 * @return: it'll return the array list object of price matrix
	 * @throws PriceMatrixNotFoundException, If data is not found, it'll throw exception 
	 */
	public List<PriceMatrix> getAllPriceMatrix() throws PriceMatrixNotFoundException {
		logger.debug("Inside getAllPriceMatrix method of PriceMatrixDao");
		List<PriceMatrix> resultMatrixs = getPriceMatrixData();
		
		if(resultMatrixs.isEmpty()) {
			logger.debug("Price Matrix is Not available");
			throw new PriceMatrixNotFoundException("Error Code:" + ErrorCodeConstant.PRICE_MATRIX_ERROR_CODE + ", " + prop.getProperty(ErrorCodeConstant.PRICE_MATRIX_ERROR_CODE));
		}
		return resultMatrixs;
	}//end of method
	

	/**
	 * this method is contains hard coded data of price matrix
	 * @return
	 * 			return the hard coded data of price matrix
	 */
	public List<PriceMatrix> getPriceMatrixData() {
		logger.debug("Inside getPriceMatrixData of PriceMatrixDao");
		List<PriceMatrix> priceMatrixs = new ArrayList<>();
		PriceMatrix priceMatrix = new PriceMatrix("AC", "800000");
		priceMatrixs.add(priceMatrix);
		priceMatrix = new PriceMatrix("Cooler", "700000");
		priceMatrixs.add(priceMatrix);
		return priceMatrixs;
	}//end of method

}
