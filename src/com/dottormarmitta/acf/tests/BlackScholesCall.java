package com.dottormarmitta.acf.tests;

import com.dottormarmitta.acf.discountCurve.ConstantRateCurve;
import com.dottormarmitta.acf.discountCurve.DiscountCurve;
import com.dottormarmitta.acf.options.CallOption;
import com.dottormarmitta.acf.options.Option;
import com.dottormarmitta.acf.processGeneration.BlackScholesModel;
import com.dottormarmitta.acf.processGeneration.RandomProcess;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.acf.time.TenorImplementation;

public class BlackScholesCall {

	public static void main(String[] args) {
		double today    = 0.0;
		double maturity = 2.5;
		double initialS = 300;
		double strike   = 250;
		double rfr      = 0.03;
		double vol      = 0.30;
		int   timeSteps = 1;
		
		/*
		 * Build the tenor
		 */
		Tenor myTenor = new TenorImplementation(today, maturity, timeSteps);
		
		/*
		 * Build stock process
		 */
		RandomProcess myStock = new BlackScholesModel(rfr, vol, initialS);
		
		/*
		 * Dicount curve
		 */
		DiscountCurve discountCurve = new ConstantRateCurve(rfr);
		
		/*
		 * Create call
		 */
		Option myCall = new CallOption();
		
		/*
		 * Ask the price
		 */
		double price = myCall.getValue(myStock, strike, myTenor, discountCurve, 1000000);
		double analytic = 90.88;
		System.out.println("STANDARD CALL");
		System.out.println();
		System.out.println("MonteCarlo price is: " + price);
		System.out.println("Analytic price is:   " + analytic);
		System.out.println("Relative error is:   " + Math.abs(price - analytic)/analytic);
		System.out.println("Percentage error is: " + Math.abs(price - analytic)*100/analytic + " %");
		System.out.println();
		System.out.println("Let us introduce dividend yield");
		/*
		 * Build stock process
		 */
		double dy = 0.002;
		RandomProcess myStockDiv = new BlackScholesModel(rfr, dy, vol, initialS);
		
		/*
		 * Ask the price
		 */
		double priceDiv = myCall.getValue(myStockDiv, strike, myTenor, discountCurve, 2000000);
		double analyticDiv = 89.71;
		System.out.println("MonteCarlo price with div is: " + priceDiv);
		System.out.println("Analytic price with div is:   " + analyticDiv);
		System.out.println("Relative error with div is:   " + Math.abs(priceDiv - analyticDiv)/analyticDiv);
		System.out.println("Percentage error with div is: " + Math.abs(priceDiv - analyticDiv)*100/analyticDiv + " %");
		
	}

}
