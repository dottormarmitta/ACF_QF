package com.dottormarmitta.acf.tests;

import com.dottormarmitta.acf.stochasticDriver.MultiBrownianMotion;
import com.dottormarmitta.acf.stochasticDriver.MultidimensionalDriver;
import com.dottormarmitta.acf.stockProcess.MultiBlackScholes;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.acf.time.TenorImplementation;

public class MartingalityOfStocksTest {
	//static DecimalFormat df = new DecimalFormat("#.######");

	public static void main(String[] args) {

		// Tenor:
		double today = 0.0;
		double maturity = 3.0;
		int dt = 21;
		Tenor tenor = new TenorImplementation(today, maturity, dt);
		
		// Brownian motion
		double[][] corrMatrix = {{1, 0.3, 0.4}, {0.3, 1, 0.5}, {0.4, 0.5, 1}};
		MultidimensionalDriver brownian = new MultiBrownianMotion(corrMatrix, 3);
		
		// Stock
		double rf = 0.01;
		double[] vol = {0.35, 0.30, 0.25};
		MultiBlackScholes myBsStocks = new MultiBlackScholes(rf, vol, 3, tenor, brownian);
		
		// Martingales:
		double[][] expectedDiscountedStocks = new double[3][dt];
		expectedDiscountedStocks[0][0] = 1.0;
		expectedDiscountedStocks[1][0] = 1.0;
		expectedDiscountedStocks[2][0] = 1.0;
		double[][] simulationMatrix;
		
		int np = 300000;
		for (int i = 0; i < np; i++) {
			simulationMatrix = myBsStocks.getStocksPaths();
			for (int r = 0; r < 3; r++) {
				for (int c = 1; c < dt; c++) {
					expectedDiscountedStocks[r][c] += simulationMatrix[r][c];
				}
			}
		}
		for (int r = 0; r < 3; r++) {
			for (int c = 1; c < dt; c++) {
				expectedDiscountedStocks[r][c] = expectedDiscountedStocks[r][c]/np;
				expectedDiscountedStocks[r][c] *= Math.exp(-rf*tenor.getTime(c));
			}
		}
		
		
		// Now we benchmark with BlackScholes:
		System.out.println();
		System.out.println(" In this exercise we check that our discounted stocks are indeed martingales.");
		System.out.println(" To do so, we have implemented three stocks. Each stock is observed monthly for half a year.");
		System.out.println(" All the stocks have initial value 1.0.");
		System.out.println();
		System.out.println(" Volatility vector is: ");
		printProperVector(vol);
		System.out.println();
		System.out.println(" The correlation matrix is: ");
		printProperMatrix(corrMatrix);
		System.out.println();
		System.out.println(" We calculate E[Si_t/S0_t | f_0], which should give a value around Si_0 = 1.0: ");
		printProperMatrix(expectedDiscountedStocks);
		
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
