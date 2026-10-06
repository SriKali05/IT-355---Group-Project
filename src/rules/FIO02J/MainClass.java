/*
 * Package: FIO02J
 * File: MainClass.java
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package FIO02J;

import java.io.File;
import java.util.Scanner;

/**
 * Main class
 */
public class MainClass {

	/**
	 * Main method
	 * 
	 * Asks the user for a directory to create, then attempts to make
	 * the directory using .mkdir(). The mkdir() method returns a boolean
	 * value indicating if it was successful, so this return value is
	 * checked to ensure that the directory was successfully created.
	 * If the directory was not created, it reports this to output.
	 * The point of this rule is to never assume that a file method
	 * works. Instead, you should check any return value that provide
	 * information about the method's outcome, and catch any exceptions
	 * that may be thrown if the method fails.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		System.out.print("Enter a directory to create: ");
		String dirName = s.next();
		s.close();
		
		File f = new File(dirName);
		if(f.mkdir())
			System.out.println("The directory " + f.getPath() + " has been created.");
		else
			System.out.println("The desired directory failed to be created.");
	}
}
