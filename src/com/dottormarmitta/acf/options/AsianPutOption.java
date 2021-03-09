package com.dottormarmitta.acf.options;

import com.dottormarmitta.acf.discountCurve.DiscountCurve;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.processGeneration.RandomProcess;

public class AsianPutOption implements Option {

	public AsianPutOption() {}
	
	
	@Override
	public double getValue(RandomProcess stock, double strike, Tenor tenor, DiscountCurve discountCurve, int numberOfSimulations) {
		double payoff = 0.0;
		for (int w = 0; w < numberOfSimulations; w++) {
			stock.getRealizations(tenor); // We call this method to make sure simulation for this omega is performed
			payoff += Math.max(0.0, strike - stock.getAverage(tenor));
		}
		return payoff/numberOfSimulations*discountCurve.getDiscountFactor(tenor.getTerminalTime());
	}

}
