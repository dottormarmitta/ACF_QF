package com.dottormarmitta.acf.linearAlgebra;

/**
 * Static mean and variance modules.
 * 
 * @author Guglielmo Del Sarto, LMU & UniBo
 * @date April 2021
 */
public class BasicStatistic {

	/**
	 * Return the mean of a double array
	 * 
	 * @param sample
	 * @return sample mean
	 */
	public static double getMean(double[] sample) {
		double sum = getSum(sample);
		return sum/sample.length;
	}

	/**
	 * Return the sample variance of a double array
	 * (not unbiased, as we divide by n)
	 * 
	 * @param sample
	 * @param mean
	 * @return sampleVariance
	 */
	public static double getVariance(double[] sample, double mean) {
		for (int i = 0; i < sample.length; i++) {
			sample[i] = Math.pow(sample[i]*sample[i] - mean, 2);
		}
		return getMean(sample);
	}

	// Khahan summation:
	private static double getSum(double[] elements) {
		double sum = 0.0;
		double compensation = 0.0;
		for (int i = 0; i < elements.length; i++) {
			double y = elements[i] - compensation;
			double t = sum + y;
			compensation = (t - sum) - y;
			sum = t;
		}
		return sum;
	}
}
