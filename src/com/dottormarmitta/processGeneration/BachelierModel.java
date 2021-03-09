package com.dottormarmitta.processGeneration;

import java.util.Random;

import com.dottormarmitta.acf.processModel.DiscreteProcess;
import com.dottormarmitta.acf.processModel.ProcessTenor;
import com.dottormarmitta.acf.time.Tenor;

public class BachelierModel implements RandomProcess {
	
	private double mu;
	private double sigma;
	private double vo;
	private Random myGenerator = new Random();
	private ProcessTenor stockProcess;
	
	public BachelierModel(double drift, double diffusion, double initialValue) {
		this.mu = drift;
		this.sigma = diffusion;
		this.vo = initialValue;
	}
	
	@Override
	public ProcessTenor getRealizations(Tenor tenor) {
		double[] realizations = new double[tenor.getLength()];
		realizations[0] = this.vo;
		for (int i = 1; i < tenor.getLength(); i++) {
			realizations[i] = realizations[i-1] + this.mu * tenor.getTimeStep(i-1) + this.sigma * myGenerator.nextGaussian() * Math.sqrt(tenor.getTimeStep(i-1));
		}
		this.stockProcess = new DiscreteProcess(tenor, realizations);
		return this.stockProcess;
	}

	@Override
	public double getAverage(Tenor tenor) {
		/*
		 * This is consistent with the performed simulation
		 */
		if (this.stockProcess == null) {
			throw new IllegalArgumentException("Simulation not yet performed. Impossible to get integrated process");
		}
		return this.stockProcess.getProcessIntegral();
	}

}
