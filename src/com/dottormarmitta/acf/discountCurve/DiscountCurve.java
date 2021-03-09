package com.dottormarmitta.acf.discountCurve;

public interface DiscountCurve {
	
	/**
	 * Get discount factor (ZCB) given maturity
	 */
	public double getDiscountFactor(double time);

}
