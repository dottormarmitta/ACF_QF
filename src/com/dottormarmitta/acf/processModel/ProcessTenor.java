package com.dottormarmitta.acf.processModel;

public interface ProcessTenor {
	
	/*
	 * Get length
	 */
	public int getLength();
	
	/*
	 * Ask the process to return its value, for a given index
	 */
	public double getProcessValue(int index);
	
	/*
	 * Ask the process to return its time average, for a given index.
	 * Time avg is defined as:
	 * 
	 * (p_i+1 - p_i)/2
	 */
	public double getProcessTAvg(int index);
	
	/*
	 * Ask the process to return its time average, for a given index.
	 * Time avg is defined as
	 */
	public double getProcessIntegral();
	
	/*
	 * Perform time-wise e of a process
	 */
	public double[] asDoubleArray();
}
