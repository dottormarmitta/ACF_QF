package com.dottormarmitta.acf.stochasticDriver;


public interface MultidimensionalDriver {
	
	/**
	 * This method returns corellated Brownian increments
	 * 
	 * @param timeStep
	 * @return a vector of correlated BM-increments
	 */
	public double[] getCorrelatedBrownian(double timeStep);
	
}
