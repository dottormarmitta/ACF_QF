package com.dottormarmitta.acf.examFiles;

import java.util.Scanner;

import com.dottormarmitta.acf.interestRates.ConstantRateCurve;
import com.dottormarmitta.acf.interestRates.DiscountCurve;
import com.dottormarmitta.acf.stochasticDriver.MultiBrownianMotion;
import com.dottormarmitta.acf.stochasticDriver.MultidimensionalDriver;
import com.dottormarmitta.acf.stockProcess.GeneralBlackScholes;
import com.dottormarmitta.acf.stockProcess.MultiStockProcess;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.acf.time.TenorImplementation;

public class PortfolioMartingalityTest {

	public static void main(String[] args) {

		System.out.println("This will test the goodness of our model.");
		System.out.println("Our stocks are assumed to follow a Black&Scholes model.");
		System.out.println("Under BS, the discounted portfolio value is a martingale.");
		System.out.println("Observations over time are 3*maturity.");
		System.out.println();

		// Scanner user imput
		Scanner s = new Scanner(System.in);

		// Tenor:
		double today = 0.0;
		double maturity = 0.0;
		System.out.print("What is the time horizon? ");
		maturity = s.nextDouble();
		System.out.println();
		int dt = (int) (maturity*3);
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

		System.out.println("Enter the volatility vector, element by element: ");
		double[] vol = new double[n];
		for (int i = 0; i < n; i++) {
			vol[i] = s.nextDouble();
		}
		System.out.println();
		System.out.println("Volatility vector is: ");
		System.out.println();
		printProperVector(vol);
		// Weights:
		double[] weights = new double[n];
		double sum = 0.0;
		System.out.println("Enter the portfolio weights vector, element by element: ");
		for (int i = 0; i < n; i++) {
			weights[i] = s.nextDouble();
			sum += weights[i];
		}
		if (sum < 0.99 || sum > 1.001) {
			System.out.println("WARNING: weights may not sum up to one. Check them!");
		}
		System.out.println();
		System.out.println("Weights vector is: ");
		printProperVector(weights);
		double[][] corrMatrix = new double[n][n];
		System.out.println();
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
		DiscountCurve zcbc = new ConstantRateCurve(rf, tenor);
		MultiStockProcess myBsStocks = new GeneralBlackScholes(initialValues, rf, vol, n, tenor, brownian);
		System.out.println();
		s.close();
		
		double[] portfolioValue = new double[tenor.getLength()];
		double initial = 0.0;
		for (int i = 0; i < n; i++) {
			initial += weights[i]*initialValues[i];
		}
		portfolioValue[0] = initial;
		double[][] currentPaths;
		double currentDF = 0;
		for (int w = 0; w < ns; w++) {
			currentPaths = myBsStocks.getStocksPaths();
			for (int t = 1; t < tenor.getLength(); t++) {
				currentDF = zcbc.getBondValue(t);
				for (int a = 0; a < n; a++) {
					portfolioValue[t] += weights[a]*currentPaths[a][t]*currentDF;
				}
			}
		}
		System.out.println("Discounted portfolio value is: ");
		printDiscounted(portfolioValue, ns);

	}

	private static void printProperMatrix(double[][] M) {
		for (int i=0; i<M.length; i++) {
			for (int j = 0; j < M[0].length; j++) {
				//System.out.print(df.format(M[i][j]) + " ");
				System.out.print(M[i][j] + " ");
			}
			System.out.println();
		}
	}
	private static void printProperVector(double[] v) {
		for (int i=0; i<v.length; i++) {
			//System.out.print(df.format(v[i]) + " ");
			System.out.print(v[i] + " ");
		}
		System.out.println();
	}
	private static void printDiscounted(double[] M, int ns) {
		System.out.print(M[0] + " ");
		for (int i=1; i < M.length; i++) {
				System.out.print(M[i]/ns + " ");
		}
	}


}
