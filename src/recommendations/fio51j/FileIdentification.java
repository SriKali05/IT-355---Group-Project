/*
 * Package: recommendations.fio51j
 * File: FileIdentification.java
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package recommendations.fio51j;

import java.io.FileInputStream;
import java.util.Scanner;

/**
 * Main class
 */
public class FileIdentification {
	/**
	 * Main method
	 * 
	 * Takes a file path from the user and reads the size of the file
	 * using FileInputStream. It then reads the same file path into
	 * another FileInputStream, and compares the file size to the size
	 * that was calculated during the first read. This helps to ensure
	 * that the file has not been modified since it was last read, and
	 * is actually the same file. The point of this recommendation is that you
	 * aren't relying solely on the file path alone to verify if a file
	 * is the same.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.print("Enter the path of an existing file to read: ");
		Scanner s = new Scanner(System.in);
		String filePath = s.next();
		s.close();
		
		try {
			FileInputStream fin = new FileInputStream(filePath);
			long origFileSize = fin.getChannel().size();
			fin.close();
			
			fin = new FileInputStream(filePath);
			long newFileSize = fin.getChannel().size();
			fin.close();
			
			if(origFileSize == newFileSize)
				System.out.println("Original file size of " + origFileSize +
						" bytes matches the current file size of " + newFileSize + " bytes.");
			else
				System.out.println("Original file size of " + origFileSize +
						" bytes does not match current file size of " + newFileSize +
						" bytes, indicating that it has been modified.");
		}
		catch(Exception e) {
			System.out.println("Error occurred during file processing.");
		}
	}
}
