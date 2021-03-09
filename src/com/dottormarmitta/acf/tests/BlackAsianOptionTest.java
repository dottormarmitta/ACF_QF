package com.dottormarmitta.acf.tests;

import com.dottormarmitta.acf.discountCurve.ConstantRateCurve;
import com.dottormarmitta.acf.discountCurve.DiscountCurve;
import com.dottormarmitta.acf.options.AsianCallOption;
import com.dottormarmitta.acf.options.AsianPutOption;
import com.dottormarmitta.acf.options.Option;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.acf.time.TenorImplementation;
import com.dottormarmitta.processGeneration.BlackScholesModel;
import com.dottormarmitta.processGeneration.RandomProcess;

public class BlackAsianOptionTest {

	public static void main(String[] args) {

		double today    = 0.0;
		double maturity = 5.0;
		double initialS = 21.0;
		double strike   = 15.0;
		double rfr      = 0.00;
		double vol      = 0.30;
		int   timeSteps = (int) (12*maturity);
		
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
		Option myCallAsian = new AsianCallOption();
		Option myPutAsian = new AsianPutOption();
		
		/*
		 * Ask the price
		 */
		double priceAsianCall = myCallAsian.getValue(myStock, strike, myTenor, discountCurve, 1048576);
		double priceAsianPut = myPutAsian.getValue(myStock, strike, myTenor, discountCurve, 1048576);
		double profPriceCall = 6.664412;
		double profPricePut = 0.650253;
		System.out.println("ASIAN OPTIONS");
		System.out.println("");
		System.out.println("Let us value CALL");
		System.out.println("MonteCarlo price is: " + priceAsianCall);
		System.out.println("Analytic price is:   " + profPriceCall);
		System.out.println("Relative error is:   " + Math.abs(priceAsianCall - profPriceCall)/profPriceCall);
		System.out.println("Percentage error is: " + Math.abs(priceAsianCall - profPriceCall)*100/profPriceCall + " %");
		System.out.println();
		System.out.println("Let us value PUT");
		System.out.println("MonteCarlo price is: " + priceAsianPut);
		System.out.println("Analytic price is:   " + profPricePut);
		System.out.println("Relative error is:   " + Math.abs(priceAsianPut - profPricePut)/profPricePut);
		System.out.println("Percentage error is: " + Math.abs(priceAsianPut - profPricePut)*100/profPricePut + " %");

	}

}
