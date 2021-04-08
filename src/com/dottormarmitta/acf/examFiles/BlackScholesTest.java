package com.dottormarmitta.acf.examFiles;

import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

import com.dottormarmitta.acf.Option.CallOption;
import com.dottormarmitta.acf.analytic.BlackScholesAnalytic;
import com.dottormarmitta.acf.interestRates.ConstantRateCurve;
import com.dottormarmitta.acf.interestRates.DiscountCurve;
import com.dottormarmitta.acf.stochasticDriver.MultiBrownianMotion;
import com.dottormarmitta.acf.stochasticDriver.MultidimensionalDriver;
import com.dottormarmitta.acf.stockProcess.GeneralBlackScholes;
import com.dottormarmitta.acf.stockProcess.MultiStockProcess;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.acf.time.TenorImplementation;

public class BlackScholesTest {

	public static void main(String[] args) {

		System.out.println("This will test the goodness of our model.");
		System.out.println("Our stocks are assumed to follow a Black&Scholes model.");
		System.out.println("The program must then produce a Monte Carlo call price which is in line"
				+ " with the analytic formula.");
		System.out.println();

		// Scanner user imput
		Scanner s = new Scanner(System.in);

		// Tenor:
		double today = 0.0;
		double maturity = 0.0;
		System.out.print("What is maturity of the option? ");
		maturity = s.nextDouble();
		System.out.println();
		int dt = 1;
		Tenor tenor = new TenorImplementation(today, maturity, dt);
		
		// Ask for number of assets and their property:
		int n = 0;
		System.out.print("Enter the number "
				+ "of assets you want to consider. ");
		n = s.nextInt();
		System.out.println();
		System.out.println("Now enter the stocks initial values, one at the time: ");
		double[] initialValues = new double[n];
		for (int i = 0; i < n; i++) {
			initialValues[i] = s.nextDouble();
		}
		System.out.println();
		System.out.println("Initial value vector is: ");
		printProperVector(initialValues);
		System.out.println();
		System.out.println("Now enter the strikes values, one at the time: ");
		double[] strikes = new double[n];
		for (int i = 0; i < n; i++) {
			strikes[i] = s.nextDouble();
		}
		System.out.println();
		System.out.println("Strikes vector is: ");
		printProperVector(strikes);
		System.out.println();
		System.out.println("Enter the volatility vector, element by element: ");
		double[] vol = new double[n];
		for (int i = 0; i < n; i++) {
			vol[i] = s.nextDouble();
		}
		System.out.println();
		System.out.println("Volatility vector is: ");
		printProperVector(vol);
		System.out.println();
		double[][] corrMatrix = new double[n][n];
		System.out.println("Enter the elements of the correlation matrix: row by row.");
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				corrMatrix[i][j] = s.nextDouble();
			}
		}
		System.out.println();
		System.out.println("The correlation matrix is: ");
		printProperMatrix(corrMatrix);
		
		// Brownian motion
		MultidimensionalDriver brownian = new MultiBrownianMotion(corrMatrix, n);

		// Stock
		double rf = 0.0;
		System.out.println();
		System.out.print("Enter the risk free rate: ");
		rf = s.nextDouble();
		int ns = 0;
		System.out.println();
		System.out.print("Enter the number of simulations to perform: ");
		ns = s.nextInt();
		MultiStockProcess myBsStocks = new GeneralBlackScholes(initialValues, rf, vol, n, tenor, brownian);

		// DiscountCurve
		DiscountCurve zcbc = new ConstantRateCurve(rf, tenor);
		double callValue;
		
		// Now we benchmark with BlackScholes:
		for (int i = 0; i < n; i++) {
			System.out.println();
			CallOption myCall = new CallOption(myBsStocks, strikes[i], i, zcbc);
			Instant start1 = Instant.now();
			callValue = myCall.getMCValue(ns);
			Instant finished1 = Instant.now();
			System.out.println("Monte Carlo value of the call writtenn on the " + (i+1) + " asset is:  " + callValue);
			System.out.println("Analytic value:     " + BlackScholesAnalytic.analyticCall(initialValues[i], rf, 
					vol[i], strikes[i], maturity-today));
			System.out.println("Simulation elapsed: " + Duration.between(start1, finished1).toMillis() + " millis");
			System.out.println();
		}
		s.close();
	}

	private static void printProperMatrix(double[][] M) {
		for (int i=0; i<M.length; i++) {
			for (int j = 0; j < M[0].length; j++) {
				System.out.print(M[i][j] + " ");
			}
			System.out.println();
		}
	}
	private static void printProperVector(double[] v) {
		System.out.print("[");
		for (int i=0; i<v.length; i++) {
			if (i < v.length -1 ) {
				System.out.print(v[i] + ", ");
			} else {
				System.out.print(v[i]);
			}
		}
		System.out.print("]");
		System.out.println();
	}

}

