package com.dottormarmitta.acf.parallelImplementation;

import java.util.stream.IntStream;

import com.dottormarmitta.acf.interestRates.ConstantRateCurve;
import com.dottormarmitta.acf.interestRates.DiscountCurve;
import com.dottormarmitta.acf.products.FinancialProduct;
import com.dottormarmitta.acf.stochasticDriver.MultiBrownianFromTransformed;
import com.dottormarmitta.acf.stochasticDriver.MultidimensionalDriver;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.acf.time.TenorImplementation;

/**
 * Parallel bonds pricing algorithm.
 * THIS DOESN'T WORK PROPERLY.
 * PLEASE REFER TO THE IMPROVED ONE!
 * 
 * @version 1.0
 * @author Guglielmo Del Sarto, LMU & UniBo
 * @date April 2021
 */
public class ACFExamProductParallel implements FinancialProduct {

	// Number of simulations:
	private static int numberOfSimulations;

	// Curve and time:
	private static Tenor tenor;
	private static double[] discountCurve;
	private static int tenorLength;
	private static double rfr;
	private static final double dt = 0.08333333333;

	// Payoffs:
	private static double juniorTrance;
	private static double mezzanTrance;
	private static double seniorTrance;

	// Coupon rates:
	private static final double juniorRate = 0.075;
	private static final double mezzanRate = 0.025;
	private static final double seniorRate = 0.012;

	// Correlations and vols:
	private static double[] vols;
	private static double[][] choleskyMatrix;

	// Default Threshold
	private static double def;
	
	// Testing:
	private static int iterationsPerformed = 0;

	// Constructor:
	@SuppressWarnings("static-access")
	public ACFExamProductParallel(double maturity, double defThresh, double rfr, int numberOfSimulations, double[] vols, double[][] cholesky) {
		this.numberOfSimulations = numberOfSimulations;
		this.tenor = new TenorImplementation(0.0, maturity, (int) (maturity*12));
		this.tenorLength = this.tenor.getLength();
		DiscountCurve zcbc = new ConstantRateCurve(rfr, this.tenor);
		this.discountCurve = zcbc.getBondCurve();
		this.choleskyMatrix = cholesky;
		this.vols = vols;
		this.def = Math.log(defThresh);
	}

	@SuppressWarnings("static-access")
	@Override
	public double[] getValue() {

		// Each time I ask the value, this must be resetted
		this.juniorTrance = 0.0;
		this.mezzanTrance = 0.0;
		this.seniorTrance = 0.0;

		// I parallelize the loop over different omegas:
		IntStream.range(0, numberOfSimulations).parallel().forEach(i -> {
			
			this.iterationsPerformed ++;
			
			// What I need every time:
			boolean[] isDefaulted = new boolean[20];
			double nj = 1.0;
			double nm = 1.0;
			double ns = 1.0;
			double[][] eulerMatrix = new double[20][this.tenorLength];
			double[] correlatedIncrements;
			MultidimensionalDriver brownianIncrementsGenerator = new MultiBrownianFromTransformed(this.choleskyMatrix, 20);

			// Loop over time:
			for (int t = 1; t < this.tenorLength; t++) {
				correlatedIncrements = brownianIncrementsGenerator.getCorrelatedBrownian(dt);

				// Loop over assets:
				for (int a = 0; a < 20; a++) {
					eulerMatrix[a][t] = eulerMatrix[a][t-1] + (this.rfr - 0.5*this.vols[a]*this.vols[a])*dt 
							+ this.vols[a]*correlatedIncrements[a];

					// Check if the asset has defaulted:
					if (eulerMatrix[a][t] < this.def && !isDefaulted[a]) {

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

				this.juniorTrance += nj*this.juniorRate*this.discountCurve[t]*dt;
				this.mezzanTrance += nm*this.mezzanRate*this.discountCurve[t]*dt;
				this.seniorTrance += ns*this.seniorRate*this.discountCurve[t]*dt;
			}

			this.juniorTrance += nj*this.discountCurve[this.tenorLength - 1];
			this.mezzanTrance += nm*this.discountCurve[this.tenorLength - 1];
			this.seniorTrance += ns*this.discountCurve[this.tenorLength - 1];

		});

		// Now that I finished my montecarlo sum, I am ready to return the values:
		double[] values = new double[3];
		values[0] = this.juniorTrance/this.iterationsPerformed;
		values[1] = this.mezzanTrance/this.iterationsPerformed;
		values[2] = this.seniorTrance/this.iterationsPerformed;
		System.out.println("Iterations performed: " + this.iterationsPerformed);
		return values;
	}
}
