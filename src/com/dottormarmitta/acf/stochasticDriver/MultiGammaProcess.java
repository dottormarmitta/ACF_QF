package com.dottormarmitta.acf.stochasticDriver;

import org.apache.commons.math3.distribution.GammaDistribution;

public class MultiGammaProcess implements MultidimensionalDriver {
	
	private double nu;
	private int n;
	private double[] driver;

	public MultiGammaProcess(double nu, int n) {
		this.nu = nu;
		this.n = n;
		this.driver = new double[n];
	}

	@Override
	public double[] getCorrelatedBrownian(double timeStep) {
		for (int i = 0; i < this.n; i++) {
			driver[i] = new GammaDistribution(timeStep/this.nu, this.nu).sample();
		}
		return driver;
	}

}
