package com.dottormarmitta.acf.tests;

import java.util.stream.IntStream;

public class Counter {
	
	private static int parallelCount = 0;
	private static int sequentialCount = 0;

	public static void main(String[] args) {
		
		int n = 1000;
		
		// I count in parallel:
		IntStream.range(0, n).parallel().forEach(i -> {
			parallelCount++;
		});
		
		// I count sequentially:
		for (int i = 0; i < n; i++) {
			sequentialCount++;
		}
		
		System.out.println("parallelCount   = " 
				+ parallelCount);
		System.out.println("sequentialCount = " 
				+ sequentialCount);

	}

}
