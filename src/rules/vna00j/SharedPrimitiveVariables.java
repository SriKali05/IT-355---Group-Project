/*
 * Package: rules.vna00j
 * File: SharedPrimitiveVariables.java
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package rules.vna00j;

import java.lang.Thread;

/**
 * Main class
 */
public class SharedPrimitiveVariables {
	/**
	 * Main method
	 * 
	 * Creates 100,000 Thread objects, uses the runnable method of
	 * Thread:Sum with them, and waits for them all to finish. It
	 * then calls getNum() on the ThreadSum object, which always
	 * returns exactly 100,000, because ThreadSum correctly ensures
	 * that individual Threads never read old values.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ThreadIncrement ts = new ThreadIncrement();
		
		Thread[] threads = new Thread[100000];
		for(int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(ts);
			threads[i].start();
		}
		for(int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			}
			catch(InterruptedException e) {
				System.out.println("Thread interruption error.");
			}
		}
		
		System.out.println(ts.getNum());
	}

}