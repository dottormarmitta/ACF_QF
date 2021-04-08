package com.dottormarmitta.acf.products;

import com.dottormarmitta.acf.interestRates.DiscountCurve;
import com.dottormarmitta.acf.stockProcess.MultiStockProcess;
import com.dottormarmitta.acf.time.Tenor;

/**
 * Tested algorithm to find product price.
 * WARNING: slow
 * 
 * @author Guglielmo Del Sarto, LMU & UniBo
 * @date April 2021
 */
public class ACFExamProduct implements FinancialProduct {

	// Notionals:
	private double nj;
	private double nm;
	private double ns;

	// Payoff values:
	private double pj = 0.0;
	private double pm = 0.0;
	private double ps = 0.0;

	// Time step:
	private double[] couponRates = {0.075, 0.025, 0.012};
	private double[][] currentStocksEvolution;
	private MultiStockProcess stocks;
	private int j;
	private double dt = 0.08333333333;
	private Tenor tenor;
	private double d;
	private double[] zcbc;

	public ACFExamProduct(DiscountCurve zeroCouponBonds, MultiStockProcess stocks, double defaultTreshold,
			Tenor tenor, int numberOfSimulations) {
		this.d = defaultTreshold;
		this.zcbc = zeroCouponBonds.getBondCurve();
		this.stocks = stocks;
		this.tenor = tenor;
		this.j = numberOfSimulations;
	}

	@Override
	public double[] getValue() {

		// I loop over the different omegas:
		for (int w = 0; w < j; w++) {

			// Reset the values at each different path:
			currentStocksEvolution = this.stocks.getStocksPaths();
			boolean[] isDefaulted = new boolean[20];
			nj = 1.0;
			nm = 1.0;
			ns = 1.0;

			// I loop over the time grid:
			for (int t = 1; t < tenor.getLength(); t++) {

				// I loop over the assets:
				for (int a = 0; a < 20; a++) {

					// Check if the asset has defaulted:
					if (currentStocksEvolution[a][t] < this.d && !isDefaulted[a]) {

						isDefaulted[a] = true;
						if (nj > 0) {
							nj -= 0.25;
						} else if (nm > 0) {
							nm -= 0.25;
						} else if (ns > 0) {
							ns -= 0.25;
						}
					}

				}

				// I update the payoffs:
				pj += nj*couponRates[0]*zcbc[t]*dt;
				pm += nm*couponRates[1]*zcbc[t]*dt;
				ps += ns*couponRates[2]*zcbc[t]*dt;

			}

			// I add the remaining notionals back:
			pj += nj*zcbc[zcbc.length - 1];
			pm += nm*zcbc[zcbc.length - 1];
			ps += ns*zcbc[zcbc.length - 1];

		}

		// Now that I finished my montecarlo sum, I am ready to return the values:
		double[] values = new double[3];
		for (int i = 0; i < 3; i++) {
			values[0] = pj/j;
			values[1] = pm/j;
			values[2] = ps/j;
		}
		return values;
	}
}
