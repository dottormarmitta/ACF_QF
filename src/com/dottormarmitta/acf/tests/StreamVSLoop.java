package com.dottormarmitta.acf.tests;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.IntStream;

public class StreamVSLoop {
	
	static int n = 100000000;
	static int a = 0;
	static int b = 0;
	
	private static void increment() {
		a++;
	}

	public static void main(String[] args) {
		
		Instant start = Instant.now();
		IntStream.range(0, n).parallel().forEach(i -> increment());
		Instant finish = Instant.now();

		Instant start1 = Instant.now();
		for (int i = 0; i < n; i++) {
			b++;
		}
		Instant finish1 = Instant.now();

		System.out.println(" Parallel took: " + Duration.between(start, finish).toMillis() + " seconds");
		System.out.println(" Efficien took: " + Duration.between(start1, finish1).toMillis() + " seconds");
		System.out.println("a = " + a);
		System.out.println("b = " + b);
	}

}
