package com.dottormarmitta.acf.tests;

import com.dottormarmitta.acf.Option.CallOption;
import com.dottormarmitta.acf.analytic.BlackScholesAnalytic;
import com.dottormarmitta.acf.interestRates.ConstantRateCurve;
import com.dottormarmitta.acf.interestRates.DiscountCurve;
import com.dottormarmitta.acf.stochasticDriver.MultiBrownianMotion;
import com.dottormarmitta.acf.stochasticDriver.MultidimensionalDriver;
import com.dottormarmitta.acf.stockProcess.MultiBlackScholes;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.acf.time.TenorImplementation;

public class BlackScholesTestWrong {

	public static void main(String[] args) {
		
		// Tenor:
		double today = 0.0;
		double maturity = 1.0;
		int dt = 1;
		Tenor tenor = new TenorImplementation(today, maturity, dt);
		
		// Brownian motion
		double[][] corrMatrix = {{1, 0.3, 0.4}, {0.3, 1, 0.5}, {0.4, 0.5, 1}};
		MultidimensionalDriver brownian = new MultiBrownianMotion(corrMatrix, 3);
		
		// Stock
		double rf = 0.01;
		double[] vol = {0.35, 0.30, 0.25};
		MultiBlackScholes myBsStocks = new MultiBlackScholes(rf, vol, 3, tenor, brownian);
		
		// DiscountCurve
		DiscountCurve zcbc = new ConstantRateCurve(rf, tenor);
		
		// Now we benchmark with BlackScholes:
		System.out.println();
		System.out.println(" In this exercise we check that our algorithm follows indeed a multidimensional Black Scholes model.");
		System.out.println(" To do so, we have implemented three stocks and three options written on them.");
		System.out.println(" All the stocks have initial value 1.0. The option are ATM.");
		System.out.println();
		System.out.println(" Volatility vector is: ");
		printProperVector(vol);
		System.out.println();
		System.out.println(" The correlation matrix is: ");
		printProperMatrix(corrMatrix);
		System.out.println();
		CallOption myCal1 = new CallOption(myBsStocks, 1.0, 0, zcbc);
		CallOption myCal2 = new CallOption(myBsStocks, 1.0, 1, zcbc);
		CallOption myCal3 = new CallOption(myBsStocks, 1.0, 2, zcbc);
		System.out.println(" Monte Carlo call value first stock:  " + myCal1.getMCValue(1000000));
		System.out.println(" Analytic value call first stock:     " + BlackScholesAnalytic.analyticCall(1.0, rf, vol[0], 1.0, maturity-today));
		System.out.println();
		System.out.println(" Monte Carlo call value second stock: " + myCal2.getMCValue(1000000));
		System.out.println(" Analytic value call second stock:    " + BlackScholesAnalytic.analyticCall(1.0, rf, vol[1], 1.0, maturity-today));
		System.out.println();
		System.out.println(" Monte Carlo call value third stock:  " + myCal3.getMCValue(1000000));
		System.out.println(" Analytic value call third stock:     " + BlackScholesAnalytic.analyticCall(1.0, rf, vol[2], 1.0, maturity-today));
	}

	private static void printProperMatrix(double[][] M) {
		for (int i=0; i<M.length; i++) {
			System.out.print(" ");
			for (int j = 0; j < M[0].length; j++) {
				System.out.print(M[i][j] + " ");
			}
			System.out.println();
		}
	}
	private static void printProperVector(double[] v) {
		System.out.print(" ");
		for (int i=0; i<v.length; i++) {
			System.out.print(v[i] + " ");
		}
		System.out.println();
	}

}
