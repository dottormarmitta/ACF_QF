package com.dottormarmitta.acf.interestRates;

/**
 * Zero Coupon Bond Curve interface
 * 
 * @author Guglielmo Del Sarto, LMU & UniBo
 * @date April 2021
 */
public interface DiscountCurve {
	
	/**
	 * This method returns the zcb given a tenor index
	 * 
	 * @param tenorIndex
	 * @return the Bond P(t_0, t_i)
	 */
	public double getBondValue(int tenorIndex);
	
	/**
	 * This method returns the zcb curve as array of double
	 * 
	 * @rertun curve for each tenor time
	 */
	public double[] getBondCurve();
	
	/**
	 * This method returns the zcb P(0,T)
	 * 
	 * @return P(t_0, T)
	 */
	public double getMaturityBond();
	
}
