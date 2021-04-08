package com.dottormarmitta.acf.tests;

import com.dottormarmitta.acf.time.Tenor;
import com.dottormarmitta.acf.time.TenorImplementation;

public class TenorTest {

	public static void main(String[] args) {
		System.out.println();
		Tenor tenor1 = new TenorImplementation(0.0, 2.0, 0.5);
		Tenor tenor2 = new TenorImplementation(2.0, 4.0, 0.5);
		Tenor tenor3 = new TenorImplementation(0.0, 0.25, 3);
		System.out.println(" Test 1.");
		System.out.println(" Semsetral tenor, [0, 2]:");
		printProperVector(tenor1.getTenor());
		System.out.println();
		System.out.println(" Test 2.");
		System.out.println(" Semsetral tenor, [2, 4]:");
		printProperVector(tenor2.getTenor());
		System.out.println();
		System.out.println(" Test 3.");
		System.out.println(" Monthly tenor, [0, 0.25]:");
		printProperVector(tenor3.getTenor());
		System.out.println();

	}
	private static void printProperVector(double[] v) {
		System.out.print(" [ ");
		for (int i=0; i<v.length; i++) {
			if (i < v.length -1) {
				System.out.print(v[i] + ", ");
			} else {
				System.out.print(v[i] + " ");
			}
		}
		System.out.print("]");
		System.out.println();
	}

}
