package com.dottormarmitta.acf.tests;

import com.dottormarmitta.acf.Option.CallOption;
import com.dottormarmitta.acf.interestRates.ConstantRateCurve;
import com.dottormarmitta.acf.interestRates.DiscountCurve;
import com.dottormarmitta.acf.stochasticDriver.MultiBrownianMotion;
import com.dottormarmitta.acf.stochasticDriver.MultiGammaProcess;
import com.dottormarmitta.acf.stochasticDriver.MultidimensionalDriver;
import com.dottormarmitta.acf.stockProcess.GeneralVarianceGamma;
import com.dottormarmitta.acf.stockProcess.MultiStockProcess;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.acf.time.TenorImplementation;

public class TestGamma {

	public static void main(String[] args) {
		
		System.out.println();
		System.out.println(" We will now test whether our VG model is correct.");
		System.out.println();
		/*
		 * Params
		 */
		double[] vols = {0.1664};
		double th = -0.7678;
		double nu = 0.0622;
		double[] initial = {1.0};
		double[][] corr = {{1.0}};
		int n = 1;
		double rfr = 0.05;
		System.out.println(" Vol: " + vols[0] + ", Theta: " + th + ", Nu: " + nu + ", Risk-free: " + rfr);
		System.out.println();

		/*
		 * Time
		 */
		double T = 0.250;
		double[] bench = {0.0393, 0.0663, 0.882, 0.1075};
		for (int i = 0; i < 4; i++) {
			Tenor tenor = new TenorImplementation(0.0, T, 1);
			DiscountCurve zcbc = new ConstantRateCurve(rfr, tenor);

			/*
			 * Process
			 */
			MultidimensionalDriver brownian = new MultiBrownianMotion(corr, n);
			MultidimensionalDriver gamma = new MultiGammaProcess(nu, n);

			/*
			 * Stock
			 */
			MultiStockProcess vgStock = new GeneralVarianceGamma(initial, vols, gamma, brownian, rfr, th, nu, tenor, n);

			/*
			 * Option
			 */
			CallOption myCall = new CallOption(vgStock, 1.03, 1, zcbc);
			System.out.println(" Maturity: " + T);
			System.out.println(" Monte Carlo Value: " + myCall.getMCValue(1000000));
			System.out.println(" Benchmark Value:   " + bench[i]);
			System.out.println();
			T += 0.25;
		}
	}

}
