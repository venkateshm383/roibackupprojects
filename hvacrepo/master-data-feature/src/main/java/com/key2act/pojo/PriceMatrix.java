package com.key2act.pojo;

public class PriceMatrix {
	
	private String priceMatrixItem;
	private String priceMatrixPrice;
	
	public PriceMatrix() {}
	public PriceMatrix(String priceMatrixItem, String priceMatrixPrice) {
		super();
		this.priceMatrixItem = priceMatrixItem;
		this.priceMatrixPrice = priceMatrixPrice;
	}
	public String getPriceMatrixItem() {
		return priceMatrixItem;
	}
	public void setPriceMatrixItem(String priceMatrixItem) {
		this.priceMatrixItem = priceMatrixItem;
	}
	public String getPriceMatrixPrice() {
		return priceMatrixPrice;
	}
	public void setPriceMatrixPrice(String priceMatrixPrice) {
		this.priceMatrixPrice = priceMatrixPrice;
	}
	
}
