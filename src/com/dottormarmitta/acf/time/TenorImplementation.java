package com.dottormarmitta.acf.time;

/**
 * Implementation of the Tenor interface
 * 
 * @author Guglielmo Del Sarto, LMU & UniBo
 * @date April 2021
 */
public class TenorImplementation implements Tenor {
	
	final double[] tenorStructure;

	public TenorImplementation(double startTime, double endTime, int timeSteps) {
		if (startTime > endTime) {
			throw new IllegalArgumentException("Initial time must be lower than final one");
		}
		this.tenorStructure = new double[timeSteps + 1];
		double stepLength = (endTime - startTime)/timeSteps;
		this.tenorStructure[0] = startTime;
		for (int i = 1; i < timeSteps; i++) {
			this.tenorStructure[i] = this.tenorStructure[i-1] + stepLength;
		}
		this.tenorStructure[timeSteps] = endTime;
	}
	
	public TenorImplementation(double startTime, double endTime, double stepLength) {
		if (startTime > endTime) {
			throw new IllegalArgumentException("Initial time must be lower than final one");
		}
		int numberSteps = (int) ((endTime - startTime)/stepLength);
		boolean isDivisible = false;
		if (numberSteps*stepLength == (endTime-startTime)) {
			isDivisible = true;
		}
		if (isDivisible) {
			this.tenorStructure = new double[numberSteps + 1];
		} else {
			this.tenorStructure = new double[numberSteps + 2];
		}
		this.tenorStructure[0] = startTime;
		int j = 1;
		while (j < this.tenorStructure.length && this.tenorStructure[j-1] + stepLength <= endTime) {
			this.tenorStructure[j] = this.tenorStructure[j-1] + stepLength;
			j += 1;
		}
		if (j < this.tenorStructure.length) {
			this.tenorStructure[j] = endTime;
		}
	}
	
	public TenorImplementation(double endTime, double stepLength) {
		this(0.0, endTime, stepLength);
	}
	
	public TenorImplementation(double endTime, int timeSteps) {
		this(0.0, endTime, timeSteps);
	}
	
	@Override
	public int getLength() {
		return this.tenorStructure.length;
	}

	@Override
	public double getTime(int index) {
		return this.tenorStructure[index];
	}

	@Override
	public double getTimeStep(int index) {
		return this.tenorStructure[index+1] - this.tenorStructure[index];
	}
	
	@Override
	public double[] getTenor() {
		return this.tenorStructure;
	}

	@Override
	public double getTerminalTime() {
		return this.tenorStructure[tenorStructure.length-1];
	}

}
