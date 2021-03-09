package com.dottormarmitta.acf.options;

import com.dottormarmitta.acf.discountCurve.DiscountCurve;
import com.dottormarmitta.acf.processGeneration.RandomProcess;
import com.dottormarmitta.acf.time.Tenor;

public class PutOption implements Option {

	public PutOption() {}
	
	
	@Override
	public double getValue(RandomProcess stock, double strike, Tenor tenor, DiscountCurve discountCurve, int numberOfSimulations) {
		double payoff = 0.0;
		double[] localStock;
		for (int w = 0; w < numberOfSimulations; w++) {
			localStock = stock.getRealizations(tenor).asDoubleArray();
			payoff += Math.max(0.0, strike - localStock[tenor.getLength()-1]);
		}
		return payoff/numberOfSimulations*discountCurve.getDiscountFactor(tenor.getTerminalTime());
	}

}
