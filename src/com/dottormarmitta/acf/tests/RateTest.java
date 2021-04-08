package com.dottormarmitta.acf.tests;

import com.dottormarmitta.acf.interestRates.ConstantRateCurve;
import com.dottormarmitta.acf.interestRates.DiscountCurve;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.acf.time.TenorImplementation;

public class RateTest {

	public static void main(String[] args) {
		double today = 0.0;
		double maturity = 2.0;
		int dt = 24;
		double rf = 0.01;
		Tenor myTenor = new TenorImplementation(today, maturity, dt);
		DiscountCurve zcbc = new ConstantRateCurve(rf, myTenor);
		System.out.println();
		System.out.println(" Unit of measure is 1 month (i.e. 12 -> 1 yr).");
		System.out.println();
		System.out.println(" Test 1.");
		System.out.println(" P(0,12) = 0.990");
		System.out.println(" Implementation result: " + zcbc.getBondValue(12));
		System.out.println();
		System.out.println(" Test 2.");
		System.out.println(" P(0,18) = 0.985");
		System.out.println(" Implementation result: " + zcbc.getBondValue(18));
		System.out.println();
		System.out.println(" Test 3.");
		System.out.println(" P(0,24) = 0.980");
		System.out.println(" Implementation result: " + zcbc.getBondValue(24));
	}

}
