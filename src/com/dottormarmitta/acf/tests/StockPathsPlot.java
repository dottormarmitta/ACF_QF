package com.dottormarmitta.acf.tests;

import com.dottormarmitta.acf.stochasticDriver.MultiBrownianMotion;
import com.dottormarmitta.acf.stochasticDriver.MultidimensionalDriver;
import com.dottormarmitta.acf.stockProcess.MultiBlackScholes;
import com.dottormarmitta.acf.stockProcess.MultiStockProcess;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.acf.time.TenorImplementation;

public class StockPathsPlot {

	public static void main(String[] args) {
		double[][] corrMatrix = {{1, 0.8, -0.2},{0.8, 1, -0.7},{-0.2, -0.7, 1}};
		MultidimensionalDriver brownian = new MultiBrownianMotion(corrMatrix, 3);
		double today = 0.0;
		double maturity = 2.0;
		int dt = 30;
		Tenor tenor = new TenorImplementation(today, maturity, dt);
		double[] vols = {0.25, 0.25, 0.25};
		double rfr = 0.01;
		MultiStockProcess myStocks = new MultiBlackScholes(rfr, vols, 3, tenor, brownian);
		printProperMatrix(myStocks.getStocksPaths());
		printProperVector(tenor.getTenor());
	}
	private static void printProperMatrix(double[][] M) {
		for (int i=0; i<M.length; i++) {
			System.out.print("[ ");
			for (int j = 0; j < M[0].length; j++) {
				System.out.print(M[i][j] + ", ");
			}
			System.out.print(" ]");
			System.out.println();
		}
	}
	private static void printProperVector(double[] v) {
		System.out.print(" [ ");
		for (int i=0; i<v.length; i++) {
			if (i < v.length -1) {
				System.out.print(v[i] + ", ");
			} else {
				System.out.print(v[i] + " ");
			}
		}
		System.out.print("]");
		System.out.println();
	}

}
