package com.dottormarmitta.acf.stockProcess;

import com.dottormarmitta.acf.stochasticDriver.MultidimensionalDriver;
import com.dottormarmitta.acf.time.Tenor;

public class GeneralVarianceGamma implements MultiStockProcess {
	
	private double[] vols;
	private MultidimensionalDriver gamma;
	private MultidimensionalDriver brownian;
	private double rf;
	private double th;
	private Tenor tenor;
	private int n;
	private double[][] currentPath;
	private double[] currentNorm;
	private double[] currentGamma;
	private double[][] x; 
	private double[] omega;

	public GeneralVarianceGamma(double[] initials, double[] vols, MultidimensionalDriver gammaDriver,
			MultidimensionalDriver brownianDriver, double rfr, double theta, double nu, Tenor tenor,
			int numberOfAssets) {
		this.vols = vols;
		this.gamma = gammaDriver;
		this.brownian = brownianDriver;
		this.rf = rfr;
		this.th = theta;
		this.tenor = tenor;
		this.n = numberOfAssets;
		this.currentPath = new double[n][this.tenor.getLength()];
		this.omega = new double[n];
		for (int i = 0; i < n; i++) {
			this.omega[i] = (Math.log(1 - theta*nu - vols[i]*vols[i]*nu*0.5))/nu;
			this.currentPath[i][0] = initials[i];
		}
		this.x = new double[n][this.tenor.getLength()];
	}

	@Override
	public double[][] getStocksPaths() {
		double time;
		for (int t = 1; t < this.tenor.getLength(); t++) {
			time = this.tenor.getTimeStep(t-1);
			currentNorm = this.brownian.getCorrelatedBrownian(1);
			currentGamma = this.gamma.getCorrelatedBrownian(time);
			
			for (int a = 0; a < this.n; a++) {
				this.x[a][t] = this.th*currentGamma[a] + this.vols[a]*
						Math.sqrt(currentGamma[a])*currentNorm[a];
				this.currentPath[a][t] = this.currentPath[a][t-1]*Math.exp((this.rf + omega[a])*time +
						this.x[a][t]);
			}
		}
		return this.currentPath;
	}

}
