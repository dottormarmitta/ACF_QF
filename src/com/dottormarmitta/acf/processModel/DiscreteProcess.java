package com.dottormarmitta.acf.processModel;

import com.dottormarmitta.acf.time.Tenor;

public class DiscreteProcess implements ProcessTenor {
	
	private boolean isConst;
	private double[] values;
	private Tenor tenor;

	public DiscreteProcess(Tenor tenor, double[] realizations) {
		if (tenor.getLength() != realizations.length) {
			throw new IllegalArgumentException("Tenor length doesn't agree with realizations array");
		}
		this.isConst = false;
		this.values  = realizations;
		this.tenor   = tenor;
	}
	public DiscreteProcess(Tenor tenor, double constant) {
		this.isConst = true;
		double[] loc = {constant};
		this.values  = loc;
		this.tenor   = tenor;
	}
	
	@Override
	public int getLength() {
		return this.tenor.getLength();
	}

	@Override
	public double getProcessValue(int index) {
		if (this.isConst) {
			return this.values[0];
		}
		return this.values[index];
	}

	@Override
	public double getProcessTAvg(int index) {
		if (this.isConst) {
			return this.values[0];
		}
		if (index > this.values.length) {
			throw new IllegalArgumentException("Process index out of bound");
		}
		return (this.values[index+1] + this.values[index])*0.5;
	}
	
	@Override
	public double getProcessIntegral() {
		double integral = 0.0;
		for (int i = 1; i < values.length; i++) {
			integral += ((this.values[i] + this.values[i-1])*0.5)*this.tenor.getTimeStep(i-1);
		}
		return integral/this.tenor.getTerminalTime();
	}

	@Override
	public double[] asDoubleArray() {
		return this.values;
	}

}
