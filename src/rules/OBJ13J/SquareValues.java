/*
 * Package: OBJ13J
 * File: SquareValues.java
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package OBJ13J;

/**
 * Contains a constant array of the square values for the integers from 0 to 10, inclusive.
 * This array is private and must be retrieved by calling the getSquareTable() method, which
 * ensures that the array is not modified.
 */
public class SquareValues {
	private static final int[] squares = {0, 1, 4, 9, 16, 25, 36, 49, 64, 81, 100};
	
	/**
	 * Creates a deep copy of the array of square values and returns it.
	 * By creating a deep copy instead of returning the array itself, it
	 * ensures that other classes cannot modify the array.
	 * 
	 * @return Array of the square values
	 */
	public static final int[] getSquareTable(){
		return squares.clone();
	}
}
