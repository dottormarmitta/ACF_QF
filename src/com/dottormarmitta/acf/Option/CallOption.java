package com.dottormarmitta.acf.Option;

import com.dottormarmitta.acf.interestRates.DiscountCurve;
import com.dottormarmitta.acf.stockProcess.MultiStockProcess;

/**
 * Call option using MonteCarlo simulation
 * 
 * @author Guglielmo Del Sarto, LMU & UniBo
 * @date April 2021
 */
public class CallOption {
	
	private MultiStockProcess stocks;
	private int i;
	private double k;
	private DiscountCurve zcb;

	public CallOption(MultiStockProcess stock, double strike, int assetNumber, DiscountCurve zcb) {
		this.stocks = stock;
		this.k = strike;
		this.i = assetNumber;
		this.zcb = zcb;
	}
	
	/**
	 * Get monte carlo Call value
	 * 
	 * @param numberOfSimulation
	 * @return the call price today
	 */
	public double getMCValue(int numberOfSimulation) {
		
		double monteCarlosum = 0.0;
		double[][] currentPaths;
		
		for (int j=0; j < numberOfSimulation; j++) {
			currentPaths = this.stocks.getStocksPaths();
			monteCarlosum += Math.max(currentPaths[this.i-1][currentPaths[0].length - 1] - this.k, 0.0);
		}
		
		return (monteCarlosum/numberOfSimulation)*zcb.getMaturityBond();
	}

}
