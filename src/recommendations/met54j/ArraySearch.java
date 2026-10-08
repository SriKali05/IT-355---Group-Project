/*
 * Package: recommendations.met54j
 * File: ArraySearch.java
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package recommendations.met54j;

/**
 * Main class
 */
public class ArraySearch {	
	/**
	 * Main method
	 * 
	 * Creates an array of ints, and an array of ints to search for in the first array.
	 * It calls the findFirstInstance method to search for the each int from the second array,
	 * and prints the first index of each int. If findFirstInstance returns -1, it uses this
	 * information to state that the int could not be found.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int[] numbers = {3, 4, 19, -4, 0, 290, 12, -4, 1, 99};
		int[] numsToFind = {4, 100, 99, -2};
		
		for(int i = 0; i < numsToFind.length; i++) {
			int result = findFirstInstance(numsToFind[i], numbers);
			if(result > -1)
				System.out.println("First index of " + numsToFind[i] + " is " + result);
			else
				System.out.println("First index of " + numsToFind[i] + " could not be located");
		}
	}
	
	/**
	 * Takes an int and an int array, then returns the index of
	 * the first instance where the int appears. If the int is never
	 * found in any index of the array, then it returns -1. This
	 * value is impossible to be returned by the function if the
	 * int was found in the array, because there is no index -1.
	 * Therefore, the return value of -1 reliably provides information
	 * about the outcome of the method, which is the point of this reocommendation.
	 * 
	 * @param item - int to search for
	 * @param arr  - array of ints to search in
	 * @return index of the first appearance of the int within the array
	 */
	public static int findFirstInstance(int item, int[] arr) {
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] == item) {
				return i;
			}
		}
		
		return -1;
	}
}
