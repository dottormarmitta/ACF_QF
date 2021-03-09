package com.dottormarmitta.acf.options;

import com.dottormarmitta.acf.discountCurve.DiscountCurve;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.processGeneration.RandomProcess;

public class AsianCallOption implements Option {

	public AsianCallOption() {}
	
	
	@Override
	public double getValue(RandomProcess stock, double strike, Tenor tenor, DiscountCurve discountCurve, int numberOfSimulations) {
		double payoff = 0.0;
		for (int w = 0; w < numberOfSimulations; w++) {
			stock.getRealizations(tenor); // We call this method to make sure simulation for this omega is performed
			payoff += Math.max(0.0, stock.getAverage(tenor) - strike);
		}
		return payoff/numberOfSimulations*discountCurve.getDiscountFactor(tenor.getTerminalTime());
	}

}
