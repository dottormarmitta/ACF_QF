package com.dottormarmitta.acf.tests;

import java.util.Scanner;

import com.dottormarmitta.acf.stochasticDriver.MultiBrownianMotion;
import com.dottormarmitta.acf.stochasticDriver.MultidimensionalDriver;
import com.dottormarmitta.acf.stockProcess.GeneralBlackScholes;
import com.dottormarmitta.acf.stockProcess.MultiStockProcess;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.acf.time.TenorImplementation;

public class BrownianMotionTest {

	public static void main(String[] args) {
		System.out.println();
		System.out.println("This will check we have the right correlations.");
		System.out.println("To keep things simple, we consider only four assets.");
		System.out.println();

		// Scanner user imput
		Scanner s = new Scanner(System.in);

		// Tenor:
		double time = 0.0;
		System.out.print("What is time horizon? ");
		time = s.nextDouble();
		System.out.println();
		// Tenor:
		double[] currentSimulation;
		double a = 0;
		System.out.print("Enter correlation between stock 1 and 2: ");
		a = s.nextDouble();
		System.out.println();
		double b = 0;
		System.out.print("Enter correlation between stock 1 and 3: ");
		b = s.nextDouble();
		System.out.println();
		double c = 0.32;
		System.out.print("Enter correlation between stock 1 and 4: ");
		c = s.nextDouble();
		System.out.println();
		double d = 0;
		System.out.print("Enter correlation between stock 2 and 3: ");
		d = s.nextDouble();
		System.out.println();
		double e = 0;
		System.out.print("Enter correlation between stock 2 and 4: ");
		e = s.nextDouble();
		System.out.println();
		double f = -0;
		System.out.print("Enter correlation between stock 3 and 4: ");
		f = s.nextDouble();
		System.out.println();
		s.close();
		
		// Brownian motion
		double[][] corrMatrix = {{1, a, b, c}, {a, 1, d, e}, {b, d, 1, f}, {c, e, f, 1}};
		MultidimensionalDriver brownian = new MultiBrownianMotion(corrMatrix, 4);
		double firstTimesSecond = 0.0;
		double firstTimesThird  = 0.0;
		double firstTimesFourth = 0.0;
		double secondTimesThird = 0.0;
		int np = 5000000;
		
		for (int w = 0; w < np; w++) {
			currentSimulation = brownian.getCorrelatedBrownian(time);
			firstTimesSecond += currentSimulation[0]*currentSimulation[1];
			firstTimesThird  += currentSimulation[0]*currentSimulation[2];
			firstTimesFourth += currentSimulation[0]*currentSimulation[3];
			secondTimesThird += currentSimulation[1]*currentSimulation[2];
		}

		// Now we benchmark with BlackScholes:
		System.out.println("We check that our algorithm produces Brownian Motion with the right correlation.");
		System.out.println();
		System.out.println("The correlation matrix is: ");
		printProperMatrix(corrMatrix);
		System.out.println();
		System.out.println("Time is: " + time);
		System.out.println();
		System.out.println("Theoretical E[B_1 * B_2] = " + a*time);
		System.out.println("Monte Carlo E[B_1 * B_2] = " + firstTimesSecond/np);
		System.out.println();
		System.out.println("Theoretical E[B_1 * B_3] = " + b*time);
		System.out.println("Monte Carlo E[B_1 * B_3] = " + firstTimesThird/np);
		System.out.println();
		System.out.println("Theoretical E[B_1 * B_4] = " + c*time);
		System.out.println("Monte Carlo E[B_1 * B_4] = " + firstTimesFourth/np);
		System.out.println();
		System.out.println("Theoretical E[B_2 * B_3] = " + d*time);
		System.out.println("Monte Carlo E[B_2 * B_3] = " + secondTimesThird/np);
		System.out.println();
		System.out.println();
		/*
		 * Now, we observe stocks:
		 */
		double today = 0.0;
		double maturity = time;
		int dt = 1;
		Tenor tenor = new TenorImplementation(today, maturity, dt);
		double[] vols = {0.20, 0.25, 0.3, 0.25};
		double rfr = 0.01;
		double e1 = 0.0;
		double e2 = 0.0;
		double e3 = 0.0;
		double covs1s2 = 0.0;
		double covs1s3 = 0.0;
		double covs2s3 = 0.0;
		double covs1s1 = 0.0;
		double[][] currentPath;
		double[] init = {1.0, 1.0, 1.0, 1.0};
		MultiStockProcess myStocks = new GeneralBlackScholes(init, rfr, vols, 4, tenor, brownian);
		/*
		printProperMatrix(myStocks.getStocksPaths());
		System.out.println();
		printProperVector(tenor.getTenor());
		*/
		for (int w = 0; w < np; w++) {
			currentPath = myStocks.getStocksPaths();
			e1 += currentPath[0][1];
			e2 += currentPath[1][1];
			e3 += currentPath[2][1];
			covs1s2 += currentPath[0][1]*currentPath[1][1];
			covs1s3 += currentPath[0][1]*currentPath[2][1];
			covs2s3 += currentPath[1][1]*currentPath[2][1];
			covs1s1 += currentPath[0][1]*currentPath[0][1];
		}
		e1 = e1/np;
		e2 = e2/np;
		e3 = e3/np;
		covs1s2 = covs1s2/np;
		covs1s3 = covs1s3/np;
		covs2s3 = covs2s3/np;
		covs1s1 = covs1s1/np;
		double corrs1s2 = (covs1s2 - e1*e2)/(vols[0]*vols[1]*maturity);
		double corrs1s3 = (covs1s3 - e1*e3)/(vols[0]*vols[2]*maturity);
		double corrs2s3 = (covs2s3 - e3*e2)/(vols[2]*vols[1]*maturity);
		double corrs1s1 = (covs1s1 - e1*e1)/(vols[0]*vols[0]*maturity);
		System.out.println("We now observe stock correlation.");
		System.out.println();
		System.out.println("Theoretical Cor(S_1, S_1) = 1.0");
		System.out.println("Monte carlo Cor(S_1, S_1) = " + corrs1s1);
		System.out.println();
		System.out.println("Theoretical Cor(S_1, S_2) = " + a);
		System.out.println("Monte carlo Cor(S_1, S_2) = " + corrs1s2);
		System.out.println();
		System.out.println("Theoretical Cor(S_1, S_3) = " + b);
		System.out.println("Monte carlo Cor(S_1, S_3) = " + corrs1s3);
		System.out.println();
		System.out.println("Theoretical Cor(S_2, S_3) = " + d);
		System.out.println("Monte carlo Cor(S_2, S_3) = " + corrs2s3);

	}
	private static void printProperMatrix(double[][] M) {
		for (int i=0; i<M.length; i++) {
			for (int j = 0; j < M[0].length; j++) {
				System.out.print(M[i][j] + ", ");
			}
			System.out.println();
		}
	}

}
