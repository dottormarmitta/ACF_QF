package com.dottormarmitta.acf.options;

import com.dottormarmitta.acf.discountCurve.DiscountCurve;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.processGeneration.RandomProcess;

public class CallOption implements Option {
	
	public CallOption() {}
	
	
	@Override
	public double getValue(RandomProcess stock, double strike, Tenor tenor, DiscountCurve discountCurve, int numberOfSimulations) {
		double payoff = 0.0;
		double[] localStock;
		for (int w = 0; w < numberOfSimulations; w++) {
			localStock = stock.getRealizations(tenor).asDoubleArray();
			payoff += Math.max(0.0, localStock[tenor.getLength()-1] - strike);
		}
		return payoff/numberOfSimulations*discountCurve.getDiscountFactor(tenor.getTerminalTime());
	}
}
