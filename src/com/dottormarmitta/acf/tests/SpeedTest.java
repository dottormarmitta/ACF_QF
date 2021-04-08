package com.dottormarmitta.acf.tests;

import java.time.Duration;
import java.time.Instant;

import com.dottormarmitta.acf.interestRates.ConstantRateCurve;
import com.dottormarmitta.acf.interestRates.DiscountCurve;
import com.dottormarmitta.acf.linearAlgebra.Cholesky;
import com.dottormarmitta.acf.parallelImplementation.ACFExamProductParallelImproved;
import com.dottormarmitta.acf.products.ACFExamProduct;
import com.dottormarmitta.acf.products.ACFExamProductEfficient;
import com.dottormarmitta.acf.products.FinancialProduct;
import com.dottormarmitta.acf.stochasticDriver.MultiBrownianMotion;
import com.dottormarmitta.acf.stochasticDriver.MultidimensionalDriver;
import com.dottormarmitta.acf.stockProcess.MultiBlackScholes;
import com.dottormarmitta.acf.stockProcess.MultiStockProcess;
import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.acf.time.TenorImplementation;

public class SpeedTest {

	public static void main(String[] args) {
		System.out.println();
		System.out.println("This will test the difference in performance of our algorithms.");
		System.out.println();

		// Tenor:
		double today = 0.0;
		double maturity = 10.0;
		System.out.print("Maturity of the option is: " + maturity);
		System.out.println();
		int dt = 120;
		Tenor tenor = new TenorImplementation(today, maturity, dt);
		
		// Ask for number of assets and their property:
		double[] vols = {0.370367,0.445588,0.398324,0.380405,0.377829,0.448265,0.435559,
				0.450125,0.353118,0.429132,0.440690,0.368316,0.392728,0.306113,0.372613,
				0.368394,0.339283,0.403478,0.415147,0.349894};
		double[][] corrMatrix = { {1.000000,-0.019288,0.010012,-0.067927,0.019401,-0.031492,-0.282125,0.249796,0.387377,-0.207334,0.018348,-0.181334,0.328681,-0.394533,-0.107928,0.142886,0.135495,-0.349548,-0.204999,-0.014416}
		,{-0.019288,1.000000,0.034550,-0.445229,0.252884,0.188785,-0.167559,-0.342862,-0.012933,0.065565,-0.196677,0.038434,0.406419,0.324921,-0.127753,-0.017430,0.122584,-0.170519,-0.286867,-0.142240}
		,{0.010012,0.034550,1.000000,-0.054940,0.207878,-0.634638,0.023026,0.215593,-0.011752,-0.295118,0.035584,-0.199075,-0.298852,0.130280,0.273791,0.373382,0.059079,0.510707,-0.072966,0.149749}
		,{-0.067927,-0.445229,-0.054940,1.000000,-0.013142,-0.050416,-0.066812,-0.195054,-0.143966,0.008784,-0.158464,-0.044481,-0.198021,-0.415498,0.093208,0.127584,-0.040951,0.325166,0.310570,-0.278745}
		,{0.019401,0.252884,0.207878,-0.013142,1.000000,0.133008,-0.098179,-0.032308,0.187074,-0.002939,0.165800,0.039704,0.369191,-0.159751,0.336307,0.267730,0.157928,0.271479,-0.085776,0.067367}
		,{-0.031492,0.188785,-0.634638,-0.050416,0.133008,1.000000,-0.200398,-0.151158,-0.036307,0.204437,0.186071,0.079680,0.377093,-0.185117,0.064016,-0.450669,0.068358,-0.417982,0.295162,-0.111129}
		,{-0.282125,-0.167559,0.023026,-0.066812,-0.098179,-0.200398,1.000000,0.347648,-0.071562,-0.182880,-0.321696,0.117497,-0.165915,-0.206424,-0.065135,-0.138094,0.306371,-0.049398,-0.021590,0.066661}
		,{0.249796,-0.342862,0.215593,-0.195054,-0.032308,-0.151158,0.347648,1.000000,0.180340,0.111085,0.107398,0.134138,-0.036683,-0.256991,-0.001420,0.064367,0.346691,0.050187,-0.276839,0.279661}
		,{0.387377,-0.012933,-0.011752,-0.143966,0.187074,-0.036307,-0.071562,0.180340,1.000000,-0.137582,0.019710,-0.002400,0.289984,-0.077095,-0.205831,0.323817,-0.138511,-0.030148,-0.425242,0.191266}
		,{-0.207334,0.065565,-0.295118,0.008784,-0.002939,0.204437,-0.182880,0.111085,-0.137582,1.000000,-0.140787,0.307480,0.230404,0.225693,0.052623,-0.294749,0.067049,-0.221167,-0.291767,0.047396}
		,{0.018348,-0.196677,0.035584,-0.158464,0.165800,0.186071,-0.321696,0.107398,0.019710,-0.140787,1.000000,0.113958,0.054181,-0.124716,0.410490,0.115387,0.198067,0.134292,0.015956,0.316109}
		,{-0.181334,0.038434,-0.199075,-0.044481,0.039704,0.079680,0.117497,0.134138,-0.002400,0.307480,0.113958,1.000000,-0.007122,-0.071121,-0.001796,-0.220289,0.045441,-0.094655,-0.323531,-0.199524}
		,{0.328681,0.406419,-0.298852,-0.198021,0.369191,0.377093,-0.165915,-0.036683,0.289984,0.230404,0.054181,-0.007122,1.000000,0.041848,-0.025463,-0.077763,0.380876,-0.265021,-0.226281,0.082431}
		,{-0.394533,0.324921,0.130280,-0.415498,-0.159751,-0.185117,-0.206424,-0.256991,-0.077095,0.225693,-0.124716,-0.071121,0.041848,1.000000,-0.198511,-0.014250,-0.184807,0.174011,-0.181951,0.204204}
		,{-0.107928,-0.127753,0.273791,0.093208,0.336307,0.064016,-0.065135,-0.001420,-0.205831,0.052623,0.410490,-0.001796,-0.025463,-0.198511,1.000000,0.168832,0.241587,0.281593,-0.147882,-0.057443}
		,{0.142886,-0.017430,0.373382,0.127584,0.267730,-0.450669,-0.138094,0.064367,0.323817,-0.294749,0.115387,-0.220289,-0.077763,-0.014250,0.168832,1.000000,-0.262245,0.640556,-0.326834,0.157924}
		,{0.135495,0.122584,0.059079,-0.040951,0.157928,0.068358,0.306371,0.346691,-0.138511,0.067049,0.198067,0.045441,0.380876,-0.184807,0.241587,-0.262245,1.000000,-0.099784,-0.218355,0.075456}
		,{-0.349548,-0.170519,0.510707,0.325166,0.271479,-0.417982,-0.049398,0.050187,-0.030148,-0.221167,0.134292,-0.094655,-0.265021,0.174011,0.281593,0.640556,-0.099784,1.000000,-0.077745,0.085155}
		,{-0.204999,-0.286867,-0.072966,0.310570,-0.085776,0.295162,-0.021590,-0.276839,-0.425242,-0.291767,0.015956,-0.323531,-0.226281,-0.181951,-0.147882,-0.326834,-0.218355,-0.077745,1.000000,-0.075121}
		,{-0.014416,-0.142240,0.149749,-0.278745,0.067367,-0.111129,0.066661,0.279661,0.191266,0.047396,0.316109,-0.199524,0.082431,0.204204,-0.057443,0.157924,0.075456,0.085155,-0.075121,1.000000}
		};
		double[][] chol = Cholesky.cholesky(corrMatrix);
		// Brownian motion
		MultidimensionalDriver brownian = new MultiBrownianMotion(corrMatrix, 20);

		// Stock
		double rf = 0.01;
		System.out.print("Risk free rate is: " + rf);
		System.out.println();
		double d = 0.20; // SOGLIA
		System.out.print("The default threshold is: " + d);
		System.out.println();
		int ns = 10000;
		MultiStockProcess myBsStocks = new MultiBlackScholes(rf, vols, 20, tenor, brownian);

		// DiscountCurve
		DiscountCurve zcbc = new ConstantRateCurve(rf, tenor);
		
		// We price the tranches using the different implementations:
		double[] tested;
		double[] fast;
		double[] numberOfOmega = new double[10];
		double[] juniorBondDeltaPrice = new double[10];
		long[] normalSeconds = new long[10];
		long[] fastSeconds = new long[10];
		long[] parallelSeconds = new long[10];
		
		for (int i = 0; i < 10; i++) {
			FinancialProduct testedProduct = new ACFExamProduct(zcbc, myBsStocks, d, tenor, ns);
			FinancialProduct fastProduct = new ACFExamProductEfficient(rf, vols, brownian, zcbc, d, tenor, ns);
			FinancialProduct parallelProduct = new ACFExamProductParallelImproved(maturity, d, rf, ns, vols, chol);
			
			Instant start1 = Instant.now();
			fast = fastProduct.getValue();
			Instant finish1 = Instant.now();
			
			Instant start = Instant.now();
			tested = testedProduct.getValue();
			Instant finish = Instant.now();
			
			Instant start2 = Instant.now();
			fast = parallelProduct.getValue();
			Instant finish2 = Instant.now();
			
			long normalTime = Duration.between(start, finish).toMillis();
			long fastTime = Duration.between(start1, finish1).toMillis();
			long parallelTime = Duration.between(start2, finish2).toMillis();
			numberOfOmega[i] = ns/1000;
			
			juniorBondDeltaPrice[i] = Math.abs(fast[0] - tested[0])/tested[0];
			
			normalSeconds[i] = normalTime/1000;
			fastSeconds[i] = fastTime/1000;
			parallelSeconds[i] = parallelTime/1000;
			ns *= 2;
		}
		
		System.out.println();
		System.out.println("Simulations (*1000): ");
		printProperVector(numberOfOmega);
		System.out.println();
		System.out.println("Tested algorithm times (sec): ");
		printProperVector(normalSeconds);
		System.out.println();
		System.out.println("Faster algorithm times (sec): ");
		printProperVector(fastSeconds);
		System.out.println();
		System.out.println("Parallel algorithm times (sec): ");
		printProperVector(parallelSeconds);
		System.out.println();
		System.out.println("Relative delta prices (parallel wrt tested): ");
		printProperVector(juniorBondDeltaPrice);
		System.out.println();
		
		
	}
	private static void printProperVector(double[] v) {
		System.out.print("[");
		for (int i=0; i<v.length; i++) {
			if (i < v.length - 1) {
				System.out.print(v[i] + ", ");
			} else {
				System.out.print(v[i]);
			}
		}
		System.out.print("]");
		System.out.println();
	}
	private static void printProperVector(long[] v) {
		System.out.print("[");
		for (int i=0; i<v.length; i++) {
			if (i < v.length - 1) {
				System.out.print(v[i] + ", ");
			} else {
				System.out.print(v[i]);
			}
		}
		System.out.print("]");
		System.out.println();
	}
}

