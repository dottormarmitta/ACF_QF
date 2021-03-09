package com.dottormarmitta.acf.time;

public interface Tenor {
	
	/**
	 * Return the tenor length
	 * 
	 * @return tenor length
	 */
	public int getLength();
	
	/**
	 * Return time corresponding to a given index
	 * 
	 * @param index
	 * @return time corresponding to given index
	 */
	public double getTime(int index);
	
	/**
	 * Get time step corresponding to given index
	 * It returns:
	 *  dt = (t_i+1 - t_i)
	 *  
	 *  @param index
	 *  @return ti+1 - ti
	 */
	public double getTimeStep(int index);
	
	/**
	 * Return tenor as double
	 * 
	 * @return double vector representing discretization of the interval
	 */
	public double[] getTenor();
	
	/**
	 * This returns terminal time of the tenor
	 * 
	 * @return final time of the tenor
	 */
	public double getTerminalTime();
}
