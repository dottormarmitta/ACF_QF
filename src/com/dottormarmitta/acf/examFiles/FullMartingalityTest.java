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

public class FullMartingalityTest {

	public static void main(String[] args) {
		
		System.out.println("This will test the goodness of our model.");
		System.out.println("Our stocks are assumed to follow a Black&Scholes model.");
		System.out.println("Under BS, the discounted stocks must be martingales.");
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
		int n = 20;
		double[] initialValues = new double[n];
		for (int i = 0; i < n; i++) {
			initialValues[i] = 1.0;
		}
		System.out.println("Initial value vector is: ");
		printProperVector(initialValues);
		double[] vol = {0.370367,0.445588,0.398324,0.380405,0.377829,0.448265,0.435559,
				0.450125,0.353118,0.429132,0.440690,0.368316,0.392728,0.306113,0.372613,
				0.368394,0.339283,0.403478,0.415147,0.349894};
		System.out.println();
		System.out.println("Volatility vector is: ");
		printProperVector(vol);
		System.out.println();
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
		System.out.println("The correlation matrix is: ");
		printProperMatrix(corrMatrix);
		System.out.println();

		// Brownian motion
		MultidimensionalDriver brownian = new MultiBrownianMotion(corrMatrix, n);

		// Stock
		double rf = 0.0;
		System.out.println();
		System.out.print("Enter the risk free rate: ");
		rf = s.nextDouble();
		System.out.println();
		int ns = 0;
		System.out.println();
		System.out.print("Enter the number of simulations to perform: ");
		ns = s.nextInt();
		System.out.println();
		s.close();
		MultiStockProcess myBsStocks = new GeneralBlackScholes(initialValues, rf, vol, n, tenor, brownian);
		DiscountCurve zcbc = new ConstantRateCurve(rf, tenor);
		
		double[][] discountedStocks = new double[20][tenor.getLength()];
		for (int a = 0; a < n; a++) {
			discountedStocks[a][0] = 1.0;
		}
		double[][] currentStocks;
		double currentDF = 0;
		for (int w = 0; w < ns; w++) {
			currentStocks = myBsStocks.getStocksPaths();
			for (int t = 1; t < tenor.getLength(); t++) {
				currentDF = zcbc.getBondValue(t);
				for (int a = 0; a < n; a++) {
					discountedStocks[a][t] += currentStocks[a][t]*currentDF;
				}
			}
		}
		System.out.println("The discounted stock matrix is: ");
		printDiscounted(discountedStocks, ns);
		System.out.println();
	}
	private static void printProperVector(double[] v) {
		for (int i=0; i<v.length; i++) {
			System.out.print(v[i] + " ");
		}
		System.out.println();
	}
	private static void printProperMatrix(double[][] M) {
		for (int i=0; i<M.length; i++) {
			for (int j = 0; j < M[0].length; j++) {
				System.out.print(M[i][j] + " ");
			}
			System.out.println();
		}
	}
	private static void printDiscounted(double[][] M, int ns) {
		for (int i=0; i<M.length; i++) {
			System.out.print(M[i][0] + " ");
			for (int j = 1; j < M[0].length; j++) {
				System.out.print(M[i][j]/ns + " ");
			}
			System.out.println();
		}
	}

}
