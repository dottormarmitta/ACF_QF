package com.dottormarmitta.acf.interestRates;

import com.dottormarmitta.acf.time.Tenor;

/**
 * Zero Coupon Bond Curve from a constant rate
 * 
 * @author Guglielmo Del Sarto, LMU & UniBo
 * @date April 2021
 */
public class ConstantRateCurve implements DiscountCurve {
	
	private Tenor tenor;
	private double rf;

	public ConstantRateCurve(double rate, Tenor tenor) {
		this.tenor = tenor;
		this.rf = rate;
	}

	@Override
	public double getBondValue(int tenorIndex) {
		return Math.exp(-this.rf * (this.tenor.getTime(tenorIndex) - this.tenor.getTime(0)));
	}

	@Override
	public double getMaturityBond() {
		return Math.exp(-this.rf * (this.tenor.getTerminalTime()));
	}

	@Override
	public double[] getBondCurve() {
		double[] curve = new double[this.tenor.getLength()];
		for (int t = 0; t < this.tenor.getLength(); t++) {
			curve[t] = Math.exp(-this.rf * (this.tenor.getTime(t) - this.tenor.getTime(0)));
		}
		return curve;
	}

}
