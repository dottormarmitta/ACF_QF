package com.dottormarmitta.acf.stochasticDriver;

import java.util.Random;

import com.dottormarmitta.acf.linearAlgebra.Cholesky;

/**
 * Generate Brownian increments using the correlation matrix.
 * 
 * @author Guglielmo Del Sarto, LMU & UniBo
 * @date April 2021
 */
public class MultiBrownianMotion implements MultidimensionalDriver {
	
	private double[][] choleskyTransform;
	private int n;
	private Random myGenerator = new Random();
	private double[] independentBrownian;
	private double[] correlatedBrownian;
	private double cholSum;

	public MultiBrownianMotion(double[][] correlationMatrix, int dimension) {
		this.choleskyTransform = Cholesky.cholesky(correlationMatrix);
		this.n = dimension;
		this.independentBrownian = new double[this.n];
		this.correlatedBrownian  = new double[this.n];
	}

	@Override
	public double[] getCorrelatedBrownian(double timeStep) {
		
		double timeSquared = Math.sqrt(timeStep);
		// Generate independent Brownian increments:
		for (int i = 0; i < this.n; i++) {
			this.independentBrownian[i] = this.myGenerator.nextGaussian()*timeSquared;
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
