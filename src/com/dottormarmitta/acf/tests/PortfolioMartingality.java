package com.dottormarmitta.acf.tests;

import com.dottormarmitta.acf.stochasticDriver.MultiBrownianMotion;
import com.dottormarmitta.acf.stochasticDriver.MultidimensionalDriver;
import com.dottormarmitta.acf.stockProcess.MultiBlackScholes;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.acf.time.TenorImplementation;

public class PortfolioMartingality {

	public static void main(String[] args) {
		
		// Tenor:
		double today = 0.0;
		double maturity = 0.5;
		int dt = 6;
		Tenor tenor = new TenorImplementation(today, maturity, dt);
				
		// Brownian motion
		double[][] corrMatrix = {{1, 0.3, 0.4}, {0.3, 1, 0.5}, {0.4, 0.5, 1}};
		MultidimensionalDriver brownian = new MultiBrownianMotion(corrMatrix, 3);
				
		// Stock
		double rf = 0.01;
		double[] vol = {0.35, 0.30, 0.25};
		MultiBlackScholes myBsStocks = new MultiBlackScholes(rf, vol, 3, tenor, brownian);
		
		// Weights:
		double[] weights = {0.3, 0.4, 0.3};
		
		// MonteCarlo simulation:
		int np = 3000000;
		double[][] currentStocks;
		double[] portfolioValue = new double[dt+1];
		portfolioValue[0] = 1.0;
		for (int w = 0; w < np; w++) {
			currentStocks = myBsStocks.getStocksPaths();
			for (int t = 1; t < tenor.getLength(); t++) {
				portfolioValue[t] += currentStocks[0][t]*weights[0] +
						currentStocks[1][t]*weights[1] +
						currentStocks[2][t]*weights[2];
			}
		}
		for (int t = 1; t < tenor.getLength(); t++) {
			portfolioValue[t] /= np;
			portfolioValue[t] *= Math.exp(-rf*tenor.getTime(t));
		}
		// Now we benchmark with BlackScholes:
		System.out.println();
		System.out.println(" In this exercise we check that our discounted portfolio is indeed a martingale.");
		System.out.println(" The portfolio has an initial value 1.0.");
		System.out.println();
		System.out.println(" Weights are: ");
		printProperVector(weights);
		System.out.println();
		System.out.println(" Volatility vector is: ");
		printProperVector(vol);
		System.out.println();
		System.out.println(" The correlation matrix is: ");
		printProperMatrix(corrMatrix);
		System.out.println();
		System.out.println(" The portfolio discounted value is: ");
		printProperVector(portfolioValue);
		

	}

	private static void printProperMatrix(double[][] M) {
		for (int i=0; i<M.length; i++) {
			System.out.print(" ");
			for (int j = 0; j < M[0].length; j++) {
				//System.out.print(df.format(M[i][j]) + " ");
				System.out.print(M[i][j] + " ");
			}
			System.out.println();
		}
	}
	private static void printProperVector(double[] v) {
		System.out.print(" ");
		for (int i=0; i<v.length; i++) {
			//System.out.print(df.format(v[i]) + " ");
			System.out.print(v[i] + " ");
		}
		System.out.println();
	}

}
