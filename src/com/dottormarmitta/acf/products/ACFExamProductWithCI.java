package com.dottormarmitta.acf.products;

import com.dottormarmitta.acf.interestRates.DiscountCurve;
import com.dottormarmitta.acf.linearAlgebra.BasicStatistic;
import com.dottormarmitta.acf.stochasticDriver.MultidimensionalDriver;
import com.dottormarmitta.acf.time.Tenor;

/**
 * Algorithm to find product price with confidence intervals.
 * WARNING: slow
 * 
 * @author Guglielmo Del Sarto, LMU & UniBo
 * @date April 2021
 */
public class ACFExamProductWithCI implements FinancialProduct {

	// Notionals:
	private double nj;
	private double nm;
	private double ns;


	// Time step:
	private double[] couponRates = {0.075, 0.025, 0.012};
	private double dt;

	// Class-specific fields:
	private double[][] eulerMatrix;
	private double d;
	private DiscountCurve zcb;
	private Tenor tenor;
	private int j;
	private double r;
	private double[] vols;
	private MultidimensionalDriver brownian;
	private double[] brownianIncrements;
	private double currentBondValue;

	public ACFExamProductWithCI(double rfr, double[] vols, MultidimensionalDriver brownian,  DiscountCurve zeroCouponBonds, double defaultTreshold,
			Tenor tenor, int numberOfSimulations) {
		this.r = rfr;
		this.vols = vols;
		this.brownian = brownian;
		this.d = Math.log(defaultTreshold);
		this.zcb = zeroCouponBonds;
		this.tenor = tenor;
		this.j = numberOfSimulations;
		this.eulerMatrix  = new double[20][tenor.getLength()];
		for (int a = 0; a < 20; a++) {
			eulerMatrix[a][0]  = 0.0;
		}
	}

	@Override
	public double[] getValue() {

		// Payoff values:
		double[] pj = new double[j];
		double[] pm = new double[j];
		double[] ps = new double[j];

		// Loop over omegas
		for (int w = 0; w < j; w++) {

			// Reset the values at each different path:
			boolean[] isDefaulted = new boolean[20];
			boolean allDefaulted = false;
			nj = 1.0;
			nm = 1.0;
			ns = 1.0;

			// Loop over time grid
			for (int t = 1; t < tenor.getLength(); t++) {

				dt = this.tenor.getTimeStep(t-1);

				// Get the stochastic driver:
				brownianIncrements = brownian.getCorrelatedBrownian(dt);

				// Loop over assets
				for (int a = 0; a < 20; a++) {

					// Simulate the current matrix:
					this.eulerMatrix[a][t] = this.eulerMatrix[a][t-1] + (this.r - 0.5*this.vols[a]*this.vols[a])*dt 
							+ this.vols[a]*brownianIncrements[a];

					// Check if the asset has defaulted:
					if (this.eulerMatrix[a][t] < this.d && !isDefaulted[a]) {

						isDefaulted[a] = true;
						if (nj > 0) {
							nj -= 0.25;
						} else if (nm > 0) {
							nm -= 0.25;
						} else if (ns > 0) {
							ns -= 0.25;
						} else {
							allDefaulted = true;
						}

					}

				}
				if (allDefaulted) {
					break;
				}

				// I update the payoffs:
				currentBondValue = zcb.getBondValue(t);
				pj[w] += nj*couponRates[0]*currentBondValue*dt;
				pm[w] += nm*couponRates[1]*currentBondValue*dt;
				ps[w] += ns*couponRates[2]*currentBondValue*dt;

			}

			// I add the remaining notionals back:
			currentBondValue = zcb.getMaturityBond();
			pj[w] += nj*currentBondValue;
			pm[w] += nm*currentBondValue;
			ps[w] += ns*currentBondValue;

		}

		// Now that I finished my montecarlo sum, I am ready to return the values:
		double[] outputVector = new double[6];
		
		// Junior Stats
		outputVector[0] = BasicStatistic.getMean(pj);
		outputVector[1] = BasicStatistic.getVariance(pj, outputVector[0]);

		// Mezzan stats
		outputVector[2] = BasicStatistic.getMean(pm);
		outputVector[3] = BasicStatistic.getVariance(pm, outputVector[2]);

		// Senior stats
		outputVector[4] = BasicStatistic.getMean(ps);
		outputVector[5] = BasicStatistic.getVariance(ps, outputVector[3]);

		return outputVector;
	}

}
