package com.dottormarmitta.acf.stockProcess;

/**
 * This class represent a multidimensional stock process
 * 
 * @author Guglielmo Del Sarto, LMU & UniBo
 * @date April 2021
 */
public interface MultiStockProcess {
	
	/**
	 * Returns a matrix containing the stocks paths over the
	 * tenor time grid
	 * 
	 * @return matrix containing StockPaths
	 */
	public double[][] getStocksPaths();

}