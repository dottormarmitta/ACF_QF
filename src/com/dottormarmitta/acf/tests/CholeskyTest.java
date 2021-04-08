package com.dottormarmitta.acf.tests;

import com.dottormarmitta.acf.linearAlgebra.Cholesky;

public class CholeskyTest {

	public static void main(String[] args) {
		double[][] M = { {1.000000,0.129401,0.596789,0.332324,0.001025}
	      ,{0.129401,1.000000,-0.095712,-0.097351,-0.620137}
	      ,{0.596789,-0.095712,1.000000,-0.110847,0.024390}
	      ,{0.332324,-0.097351,-0.110847,1.000000,-0.491478}
	      ,{0.001025,-0.620137,0.024390,-0.491478,1.000000}
	      };
		printProperMatrix(Cholesky.cholesky(M));
		
	}
	
	private static void printProperMatrix(double[][] M) {
		for (int i=0; i<M.length; i++) {
			for (int j = 0; j < M[0].length; j++) {
				System.out.print(M[i][j] + " ");
			}
			System.out.println();
		}
	}

}
