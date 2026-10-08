/*
 * Package: recommendations.obj55j
 * File: LongLivedContainers.java
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package recommendations.obj55j;

/**
 * Main class
 */
public class LongLivedContainers {
	/**
	 * Main method
	 * 
	 * Creates a new array of size 10 and object type String.
	 * Because the array has an object type, each index must
	 * be manually set to null in order for the garbage collector
	 * to free its memory. This is crucial to do if you are storing
	 * a temporary object in a list or array and are done with it.
	 * If the array was a long-lived object, then this would be beneficial
	 * to ensure that it doesn't take up unnecessary memory with its objects.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String[] s = new String[10];
		s[5] = "temporary string object has been stored in s[5].";
		System.out.println(s[5]);
		s[5] = null;
		System.out.println("Garbage collector can now delete the string that was previously stored in s[5] because s[5] was set to null.");
	}

}
