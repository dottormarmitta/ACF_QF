package com.dottormarmitta.acf.options;

import com.dottormarmitta.acf.discountCurve.DiscountCurve;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.processGeneration.RandomProcess;

public interface Option {
	
	/**
	 * It returns the option value.
	 * 
	 * @param stock, the model followed by the stock
	 * @param strike
	 * @param tenor, the tenor discretization underlying the model and the stock
	 * @param discountcurve
	 * @param numberOfSimulations to be performed
	 * 
	 * @return option value...specified by the implementation
	 */
	public double getValue(RandomProcess stock, double strike, Tenor tenor, DiscountCurve discountCurve, int numberOfsimulations);
}
