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
 * 
 * @version 2.0
 * @author Guglielmo Del Sarto, LMU & UniBo
 * @date April 2021
 */
public class ACFExamProductParallelImproved implements FinancialProduct {

	// Number of simulations:
	private int numberOfSimulations;

	// Curve and time:
	private Tenor tenor;
	private double[] discountCurve;
	private int tenorLength;
	private double rfr;
	private final double dt = 0.08333333333;
	private final double sqrtDt = Math.sqrt(0.08333333333);

	// Payoffs:
	private double[] juniorTrance;
	private double[] mezzanTrance;
	private double[] seniorTrance;

	// Coupon rates:
	private final double juniorRate = 0.075;
	private final double mezzanRate = 0.025;
	private final double seniorRate = 0.012;

	// Notionals:
	private double[] nj;
	private double[] ns;
	private double[] nm;

	// Correlations and volatilities:
	private double[] vols;
	private double[][] choleskyMatrix;

	// Default Threshold
	private double def;

	// Constructor:
	public ACFExamProductParallelImproved(double maturity, double defThresh, double rfr, int numberOfSimulations, double[] vols, double[][] cholesky) {
		this.numberOfSimulations = numberOfSimulations;
		this.tenor = new TenorImplementation(0.0, maturity, (int) (maturity*12));
		this.tenorLength = this.tenor.getLength();
		DiscountCurve zcbc = new ConstantRateCurve(rfr, this.tenor);
		this.discountCurve = zcbc.getBondCurve();
		this.choleskyMatrix = cholesky;
		this.vols = vols;
		this.rfr = rfr;
		this.def = Math.log(defThresh);
		this.juniorTrance = new double[numberOfSimulations];
		this.mezzanTrance = new double[numberOfSimulations];
		this.seniorTrance = new double[numberOfSimulations];
		this.nj = new double[numberOfSimulations];
		this.nm = new double[numberOfSimulations];
		this.ns = new double[numberOfSimulations];
		IntStream.range(0, numberOfSimulations).parallel().forEach(i -> {
			this.nj[i] = 1.0;
			this.nm[i] = 1.0;
			this.ns[i] = 1.0;
		});
	}

	@Override
	public double[] getValue() {

		// I parallelize the loop over different omegas:
		IntStream.range(0, numberOfSimulations).parallel().forEach(i -> {

			/*
			 *  What I need every omega. This, is allocated every time:
			 *  we need to prevent memory leaks!!!
			 */
			boolean[] isDefaulted = new boolean[20];
			double[][] eulerMatrix = new double[20][this.tenorLength];
			double[] correlatedIncrements = new double[20];
			MultidimensionalDriver brownianIncrementsGenerator = new MultiBrownianFromTransformed(this.choleskyMatrix, 20);

			// Loop over time:
			for (int t = 1; t < this.tenorLength; t++) {
				correlatedIncrements = brownianIncrementsGenerator.getCorrelatedBrownian(sqrtDt);

				// Loop over assets:
				for (int a = 0; a < 20; a++) {
					eulerMatrix[a][t] = eulerMatrix[a][t-1] + (this.rfr - 0.5*this.vols[a]*this.vols[a])*dt 
							+ this.vols[a]*correlatedIncrements[a];

					// Check if the asset has defaulted:
					if (eulerMatrix[a][t] < this.def && !isDefaulted[a]) {

						isDefaulted[a] = true;
						if (nj[i] > 0) {
							nj[i] -= 0.25;
						} else if (nm[i] > 0) {
							nm[i] -= 0.25;
						} else if (ns[i] > 0) {
							ns[i] -= 0.25;
						}
					}
				}

				this.juniorTrance[i] += nj[i]*this.juniorRate*this.discountCurve[t]*dt;
				this.mezzanTrance[i] += nm[i]*this.mezzanRate*this.discountCurve[t]*dt;
				this.seniorTrance[i] += ns[i]*this.seniorRate*this.discountCurve[t]*dt;
			}

			this.juniorTrance[i] += nj[i]*this.discountCurve[this.tenorLength - 1];
			this.mezzanTrance[i] += nm[i]*this.discountCurve[this.tenorLength - 1];
			this.seniorTrance[i] += ns[i]*this.discountCurve[this.tenorLength - 1];

		});

		// Now that I finished my Montecarlo sum, I am ready to return the values:
		double[] values = new double[3];
		double[] summedUp = arraySum(this.juniorTrance, this.mezzanTrance, this.seniorTrance);
		values[0] = summedUp[0]/(this.numberOfSimulations);
		values[1] = summedUp[1]/(this.numberOfSimulations);
		values[2] = summedUp[2]/(this.numberOfSimulations);
		return values;
	}

	private static double[] arraySum(double[] array1, double[] array2, double[] array3) {

		double sum1 = 0.0;
		double sum2 = 0.0;
		double sum3 = 0.0;
		for (int i = 0; i < array1.length; i++) {
			sum1 += array1[i];
			sum2 += array2[i];
			sum3 += array3[i];
		}
		return new double[] {sum1, sum2, sum3};

	}

}
