/*
 * Package: rules.obj13j
 * File: MutableObjectReferences.java
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package rules.obj13j;

/**
 * Main class
 */
public class MutableObjectReferences {
	/**
	 * Main method
	 * 
	 * Retrieves the table of square values from the SquareValues class, and
	 * attempts to modify it, then retrieve the table again, demonstrating that
	 * the original table from the SquareValues class was never actually modified.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int[] mySquareValues = SquareValues.getSquareTable();
		System.out.println(mySquareValues[5]);
		
		mySquareValues[5] = -3;
		System.out.println(mySquareValues[5]);
		
		mySquareValues = SquareValues.getSquareTable();
		System.out.println(mySquareValues[5]);
	}
}

