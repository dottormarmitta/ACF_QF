package com.dottormarmitta.acf.products;

import com.dottormarmitta.acf.interestRates.DiscountCurve;
import com.dottormarmitta.acf.time.Tenor;

/**
 * Deterministic upper-bound for the product price.
 * 
 * @author Guglielmo Del Sarto, LMU & UniBo
 * @date April 2021
 */
public class ACFExamProductMaxValue implements FinancialProduct {
		
	// Payoff values:
	private double pj;
	private double pm;
	private double ps;
		
	// Coupon Rates:
	private double cj = 0.075;
	private double cm = 0.025;
	private double cs = 0.012;
	
	// Time step:
	private double dt = 0.08333333333;
	
	private DiscountCurve zcb;
	private Tenor tenor;
		
	public ACFExamProductMaxValue(DiscountCurve zeroCouponBonds, Tenor tenor) {
		this.zcb = zeroCouponBonds;
		this.tenor = tenor;
	}

	@Override
	public double[] getValue() {
		pj = 0.0;
		pm = 0.0;
		ps = 0.0;
		
		for (int t = 1; t < tenor.getLength(); t++) {
			pj += cj*zcb.getBondValue(t)*dt;
			pm += cm*zcb.getBondValue(t)*dt;
			ps += cs*zcb.getBondValue(t)*dt;
		}
		
		pj += zcb.getMaturityBond();
		pm += zcb.getMaturityBond();
		ps += zcb.getMaturityBond();
		double[] values = {pj, pm, ps};
		return values;
	}
	
}
