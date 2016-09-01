package com.key2act.service;

import java.util.List;

import com.key2act.pojo.PriceMatrix;

public interface PriceMatrixService {

	/**
	 * this method is used to get list of price matrix objects
	 * 
	 * @param priceMatrixItem
	 *            based on priceMatrixItem price matrix service data will be
	 *            fetched
	 * @return it'll return the list of price matrix objects
	 * @throws PriceMatrixNotFoundException
	 *             if price matrix is not found then it throw
	 *             PriceMatrixNotFoundException
	 * @throws InvalidPriceMatrixRequest
	 *             if priceMatrixItem is null then it throw
	 *             InvalidPriceMatrixRequest
	 */
	public List<PriceMatrix> getPriceMatrix(String priceMatrixItem)
			throws PriceMatrixNotFoundException, InvalidPriceMatrixRequest;

	/**
	 * this method is used to get list of price matrix objects
	 * 
	 * @return it'll return the list of price matrix objects
	 * @throws PriceMatrixNotFoundException
	 *             if price matrix is not found then it throw
	 *             PriceMatrixNotFoundException
	 */
	public List<PriceMatrix> getAllPriceMatrix() throws PriceMatrixNotFoundException;

}
