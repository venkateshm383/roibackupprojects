package com.key2act.service.impl;

import java.util.List;

import com.key2act.dao.PriceMatrixDAO;
import com.key2act.pojo.PriceMatrix;
import com.key2act.service.InvalidPriceMatrixRequest;
import com.key2act.service.PriceMatrixNotFoundException;
import com.key2act.service.PriceMatrixService;

/**
 * this class is used to perform service methods for Price Matrix
 * @author bizruntime
 *
 */
public class PriceMatrixServiceImpl implements PriceMatrixService {

	/**
	 * this method is used to get list of price matrix objects
	 * @param priceMatrixItem
	 * 			based on priceMatrixItem price matrix service data will be fetched
	 * @return
	 * 		it'll return the list of price matrix objects
	 * @throws PriceMatrixNotFoundException
	 */
	@Override
	public List<PriceMatrix> getPriceMatrix(String priceMatrixItem) throws PriceMatrixNotFoundException, InvalidPriceMatrixRequest {
		if(priceMatrixItem == null)
			throw new InvalidPriceMatrixRequest("Invalid Price Matrix Request");
		PriceMatrixDAO matrixDao = new PriceMatrixDAO();
		List<PriceMatrix> priceMatrixs = matrixDao.getPriceMatrix(priceMatrixItem);
		return priceMatrixs;
	}

	/**
	 * this method is used to get list of price matrix objects
	 * @return
	 * 		it'll return the list of price matrix objects
	 * @throws PriceMatrixNotFoundException
	 */
	@Override
	public List<PriceMatrix> getAllPriceMatrix() throws PriceMatrixNotFoundException {
		PriceMatrixDAO matrixDao = new PriceMatrixDAO();
		List<PriceMatrix> priceMatrixs = matrixDao.getAllPriceMatrix();
		return priceMatrixs;
	}

}
