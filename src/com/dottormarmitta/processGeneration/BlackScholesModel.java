package com.dottormarmitta.processGeneration;

import com.dottormarmitta.acf.processModel.DiscreteProcess;
import com.dottormarmitta.acf.processModel.ProcessTenor;
import com.dottormarmitta.acf.time.Tenor;

public class BlackScholesModel implements RandomProcess {

	private double mu;
	private double sigma;
	private double vo;
	private ProcessTenor stockProcess;
	
	public BlackScholesModel(double riskFree, double diffusion, double initialValue) {
		this.mu = riskFree;
		this.sigma = diffusion;
		this.vo = initialValue;
	}
	public BlackScholesModel(double riskFree, double dividendYield, double diffusion, double initialValue) {
		this(riskFree - dividendYield, diffusion, initialValue);
	}
	
	@Override
	public ProcessTenor getRealizations(Tenor tenor) {
		// We use the fact that ln(x) follows Bachelier model with changed drift:
		RandomProcess basicDiscretization = new BachelierModel((this.mu - 0.5*(this.sigma*this.sigma)), this.sigma, Math.log(this.vo));
		ProcessTenor untransformedProcess = basicDiscretization.getRealizations(tenor);
		double[] untransformedRealizations = untransformedProcess.asDoubleArray();
		for (int i = 0; i < untransformedRealizations.length; i++) {
			// We want to get back to the original coordinates:
			untransformedRealizations[i] = Math.exp(untransformedRealizations[i]);
		}
		this.stockProcess = new DiscreteProcess(tenor, untransformedRealizations);
		return this.stockProcess;
	}
	
	@Override
	public double getAverage(Tenor tenor){
		/*
		 * This is consistent with the performed simulation
		 */
		if (this.stockProcess == null) {
			throw new IllegalArgumentException("Simulation not yet performed. Impossible to get integrated process");
		}
		return this.stockProcess.getProcessIntegral();
	}

}
