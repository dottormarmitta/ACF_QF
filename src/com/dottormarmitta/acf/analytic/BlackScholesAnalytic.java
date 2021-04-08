package com.dottormarmitta.acf.analytic;

/**
 * Static Black-Scholes modules.
 * 
 * @author Guglielmo Del Sarto, LMU & UniBo
 * @date April 2021
 */
public class BlackScholesAnalytic {
	
	/**
	 * This method return analytic black-Scholes value
	 * 
	 * @param initial, stock initial value
	 * @param rf, the risk free rate
	 * @param vol, annualized volatility
	 * @param strike, strike price
	 * @param periodLength, difference between valuation time and option maturity
	 * @return BS call value
	 */
	public static double analyticCall(double initial, double rf, double vol, double strike, double periodLength) {
		return initial*CNDF(d1(initial, rf, strike, periodLength, vol)) - strike*CNDF(d2(initial, rf, strike, periodLength, vol))*Math.exp(-rf*periodLength);
	}
	
	private static double d1(double initial, double rf, double strike, double periodLength, double vol) {
		double num = Math.log(initial/strike) + (rf + 0.5*vol*vol)*periodLength;
		double den = vol*Math.sqrt(periodLength);
		return num/den;
	}
	
	private static double d2(double initial, double rf, double strike, double periodLength, double vol) {
		return d1(initial, rf, strike, periodLength, vol) - vol*Math.sqrt(periodLength);
	}
	
	private static double CNDF(double x) {
	    int neg = (x < 0d) ? 1 : 0;
	    if ( neg == 1) 
	        x *= -1d;
	    double k = (1d / ( 1d + 0.2316419 * x));
	    double y = (((( 1.330274429 * k - 1.821255978) * k + 1.781477937) *
	                   k - 0.356563782) * k + 0.319381530) * k;
	    y = 1.0 - 0.398942280401 * Math.exp(-0.5 * x * x) * y;
	    return (1d - neg) * y + neg * (1d - y);
	}

}
