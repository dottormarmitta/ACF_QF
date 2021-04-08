package com.dottormarmitta.acf.products;

public interface FinancialProduct {
	
	/**
	 * Returns the risk neutral value of the financial product for given time
	 * 
	 * @param tenorIndex, the tenor index corresponding to the valuation time
	 * @return the financial product value
	 */
	public double[] getValue();

}
