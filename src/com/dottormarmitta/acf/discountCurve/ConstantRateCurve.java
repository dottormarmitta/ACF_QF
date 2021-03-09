package com.dottormarmitta.acf.discountCurve;

public class ConstantRateCurve implements DiscountCurve {
	
	private double rate;
	
	public ConstantRateCurve(double constantRate) {
		this.rate = constantRate;
	}
	
	@Override
	public double getDiscountFactor(double time) {
		return Math.pow(1+this.rate, -time);
	}

}
