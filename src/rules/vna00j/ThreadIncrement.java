/*
 * Package: rules.vna00j
 * File: ThreadSum.java
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package rules.vna00j;

/**
 * Uses Threads to count to a number, with each Thread incrementing the
 * instance variable 'num' by 1
 */
public class ThreadIncrement implements Runnable{
	private int num = 0;
	
	/**
	 * Getter for  num
	 * 
	 * @return the object instance's num value
	 */
	public int getNum() {
		return this.num;
	}

	/**
	 * Runnable method which gets called for Thread instances
	 * calls increment() on this object instance
	 */
	@Override
	public void run() {
		increment();
	}
	
	/**
	 * increments the 'num' variable of the class by 1, to be called by Threads
	 * the 'synchronized' keyword ensures that only one thread can execute this
	 * method at a time, which is one way of ensuring that threads never read
	 * old values of 'num', the goal of this rule
	 */
	private synchronized void increment() {
		this.num++;
	}
}