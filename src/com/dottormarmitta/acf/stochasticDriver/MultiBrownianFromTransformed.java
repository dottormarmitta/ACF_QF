package com.dottormarmitta.acf.stochasticDriver;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Generate Brownian increments using the Cholesky transformed
 * of the correlation matrix.
 * 
 * @author Guglielmo Del Sarto, LMU & UniBo
 * @date April 2021
 */
public class MultiBrownianFromTransformed implements MultidimensionalDriver {

	private double[][] choleskyTransform;
	private int n;
	private double[] independentBrownian;
	private double[] correlatedBrownian;
	private double cholSum;

	public MultiBrownianFromTransformed(double[][] choleskyMatrix, int dimension) {
		this.choleskyTransform = choleskyMatrix;
		this.n = dimension;
		this.independentBrownian = new double[this.n];
		this.correlatedBrownian  = new double[this.n];
	}

	@Override
	public double[] getCorrelatedBrownian(double timeStep) {
		
		// Generate independent Brownian increments:
		for (int i = 0; i < this.n; i++) {
			this.independentBrownian[i] = ThreadLocalRandom.current().nextGaussian()*timeStep;
		}
		
		// Generate correlated Brownian increments:
		for (int i = 0; i < this.n; i++) {
			cholSum = 0.0;
			for (int j = 0; j < this.n; j++) {
				cholSum += this.choleskyTransform[i][j]*this.independentBrownian[j];
			}
			this.correlatedBrownian[i] = cholSum;
		}
		
		// Return vector of correlated Brownian:
		return this.correlatedBrownian;
	}


}
