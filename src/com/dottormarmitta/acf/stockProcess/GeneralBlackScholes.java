package com.dottormarmitta.acf.stockProcess;

import com.dottormarmitta.acf.stochasticDriver.MultidimensionalDriver;
import com.dottormarmitta.acf.time.Tenor;

/**
 * Generate BS-stocks dynamics.
 * 
 * @author Guglielmo Del Sarto, LMU & UniBo
 * @date April 2021
 */
public class GeneralBlackScholes implements MultiStockProcess {
	
	private double r;
	private double[] vol;
	private int n;
	private Tenor tenor;
	private MultidimensionalDriver bm;
	private double[][] eulerMatrix;
	private double[][] stocksPaths;
	private double[] brownians;

	public GeneralBlackScholes(double[] initialValue, double riskFree, double[] volatilityVector, int numberOfAssets, 
			Tenor tenor, MultidimensionalDriver brownian) {
		this.r = riskFree;
		this.vol = volatilityVector;
		this.n = numberOfAssets;
		this.tenor = tenor;
		this.bm = brownian;
		this.eulerMatrix = new double[n][this.tenor.getLength()];
		this.stocksPaths = new double[n][this.tenor.getLength()];
		for (int i = 0; i < n; i++) {
			this.eulerMatrix[i][0] = Math.log(initialValue[i]);
			this.stocksPaths[i][0] = initialValue[i];
		}
	}
	
	@Override
	public double[][] getStocksPaths() {
		
		//First loop is over time of my tenor
		for (int t = 1; t < this.tenor.getLength(); t++) {
			
			// I have to build the stochastic driver for this time, in order to perform Euler Discretization
			brownians = this.bm.getCorrelatedBrownian(this.tenor.getTimeStep(t-1));
			
			// Now, I loop over all the assets
			for (int a = 0; a < n; a++) {
				
				// I perform Euler discretization on each asset, for every time step!
				this.eulerMatrix[a][t] = this.eulerMatrix[a][t-1] + (this.r - 0.5*this.vol[a]*this.vol[a])*this.tenor.getTimeStep(t-1) 
						+ this.vol[a]*brownians[a];
				// I get back to original coordinates
				this.stocksPaths[a][t] = Math.exp(this.eulerMatrix[a][t]);
			}
			
		}
		
		// Ready to return stocks paths:
		return this.stocksPaths;
	}
}
