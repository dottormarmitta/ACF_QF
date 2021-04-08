package com.dottormarmitta.acf.linearAlgebra;

/**
 * Static Cholesky module.
 * 
 * @author https://introcs.cs.princeton.edu/java/95linear/Cholesky.java.html
 */
public class Cholesky {

	/**
	 * Get Cholesky transform:
	 * 
	 * @param A the matrix
	 * @return L the Cholesky transform
	 */
	public static double[][] cholesky(double[][] A) {
		if (!isSquare(A)) {
			throw new RuntimeException("Matrix is not square");
		}
		if (!isSymmetric(A)) {
			throw new RuntimeException("Matrix is not symmetric");
		}

		int N  = A.length;
		double[][] L = new double[N][N];

		for (int i = 0; i < N; i++)  {
			for (int j = 0; j <= i; j++) {
				double sum = 0.0;
				for (int k = 0; k < j; k++) {
					sum += L[i][k] * L[j][k];
				}
				if (i == j) L[i][i] = Math.sqrt(A[i][i] - sum);
				else        L[i][j] = 1.0 / L[j][j] * (A[i][j] - sum);
			}
			if (L[i][i] <= 0) {
				throw new RuntimeException("Matrix not positive definite");
			}
		}
		return L;
	}

	// is symmetric
	private static boolean isSymmetric(double[][] A) {
		int N = A.length;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < i; j++) {
				if (A[i][j] != A[j][i]) return false;
			}
		}
		return true;
	}

	// is square
	private static boolean isSquare(double[][] A) {
		int N = A.length;
		for (int i = 0; i < N; i++) {
			if (A[i].length != N) return false;
		}
		return true;
	}

}
