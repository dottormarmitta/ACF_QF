package com.dottormarmitta.acf.parallelImplementation;

import java.util.stream.IntStream;

import com.dottormarmitta.acf.interestRates.ConstantRateCurve;
import com.dottormarmitta.acf.interestRates.DiscountCurve;
import com.dottormarmitta.acf.stochasticDriver.MultiBrownianFromTransformed;
import com.dottormarmitta.acf.stochasticDriver.MultidimensionalDriver;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.acf.time.TenorImplementation;

public class RateFinderParallelNotional {
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
	private double[] juniorTranceCoupon;
	private double[] mezzanTranceCoupon;
	private double[] seniorTranceCoupon;
	private double[] juniorTranceFace;
	private double[] mezzanTranceFace;
	private double[] seniorTranceFace;

	// Notionals:
	private double[] nj;
	private double[] ns;
	private double[] nm;

	// Correlations and volatilities:
	private double[] vols;
	private double[][] choleskyMatrix;

	// Default Threshold
	private double def;
	private double notional;

	// Constructor:
	public RateFinderParallelNotional(double notional, double maturity, double defThresh, double rfr, 
			int numberOfSimulations, double[] vols, double[][] cholesky) {
		this.notional = notional;
		this.numberOfSimulations = numberOfSimulations;
		this.tenor = new TenorImplementation(0.0, maturity, (int) (maturity*12));
		this.tenorLength = this.tenor.getLength();
		DiscountCurve zcbc = new ConstantRateCurve(rfr, this.tenor);
		this.discountCurve = zcbc.getBondCurve();
		this.choleskyMatrix = cholesky;
		this.vols = vols;
		this.rfr = rfr;
		this.def = Math.log(defThresh);
		this.juniorTranceCoupon = new double[numberOfSimulations];
		this.mezzanTranceCoupon = new double[numberOfSimulations];
		this.seniorTranceCoupon = new double[numberOfSimulations];
		this.juniorTranceFace = new double[numberOfSimulations];
		this.mezzanTranceFace = new double[numberOfSimulations];
		this.seniorTranceFace = new double[numberOfSimulations];
		this.nj = new double[numberOfSimulations];
		this.nm = new double[numberOfSimulations];
		this.ns = new double[numberOfSimulations];
		IntStream.range(0, numberOfSimulations).parallel().forEach(i -> {
			this.nj[i] = 1.0;
			this.nm[i] = 1.0;
			this.ns[i] = 1.0;
		});
	}

	public double[] getEquivalentRates() {

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

				this.juniorTranceCoupon[i] += nj[i]*this.discountCurve[t]*dt;
				this.mezzanTranceCoupon[i] += nm[i]*this.discountCurve[t]*dt;
				this.seniorTranceCoupon[i] += ns[i]*this.discountCurve[t]*dt;
			}

			this.juniorTranceFace[i] += nj[i]*this.discountCurve[this.tenorLength - 1];
			this.mezzanTranceFace[i] += nm[i]*this.discountCurve[this.tenorLength - 1];
			this.seniorTranceFace[i] += ns[i]*this.discountCurve[this.tenorLength - 1];

		});

		// Now that I finished my Montecarlo sum, I am ready to return calculate the fair rates:
		double[] wrappedValues = new double[6];
		wrappedValues = arraySum(this.juniorTranceCoupon, this.juniorTranceFace,
				this.mezzanTranceCoupon, this.mezzanTranceFace,
				this.seniorTranceCoupon, this.seniorTranceFace,
				this.numberOfSimulations);
		double juniorRate = (notional - wrappedValues[1]*notional)/(wrappedValues[0]*notional);
		double mezzanRate = (notional - wrappedValues[3]*notional)/(wrappedValues[2]*notional);
		double seniorRate = (notional - wrappedValues[5]*notional)/(wrappedValues[4]*notional);
		return new double[] {juniorRate, mezzanRate, seniorRate};
	}

	private static double[] arraySum(double[] array1, double[] array2, double[] array3,
			double[] array4, double[] array5, double[] array6, int np) {

		double sum1 = 0.0;
		double sum2 = 0.0;
		double sum3 = 0.0;
		double sum4 = 0.0;
		double sum5 = 0.0;
		double sum6 = 0.0;

		for (int i = 0; i < array1.length; i++) {
			sum1 += array1[i];
			sum2 += array2[i];
			sum3 += array3[i];
			sum4 += array4[i];
			sum5 += array5[i];
			sum6 += array6[i];
		}
		return new double[] {sum1/np, sum2/np, sum3/np, sum4/np, sum5/np, sum6/np};

	}

}
