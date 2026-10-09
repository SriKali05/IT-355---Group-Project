/*
 * Package: recommendations.fio51j
 * File: FileIdentification.java
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package recommendations.fio51j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

/**
 * Main class
 */
public class FileIdentification {
	
    /**
     * Main method
     * 
     * Writes a file and then checks if it has been modified by using multiple file attributes.
     * This is useful because if a program wants to retrieve a known file, then an attacker could
     * have tried to modify it after it was last written to. Therefore, checking the file's path
     * and name is not enough to it is the same file. It's more secure to check and ensure that
     * more file attributes, including date created, date modified, and file size are the same
     * as they were when the file was last written to.
     *
     * @param args
     */
    public static void main(String[] args){       
        try {
        	// Write original file
            Path grades = Paths.get("grades");
        	Files.deleteIfExists(grades);
            Files.writeString(grades, "Allaya,B+\nSrida,A-\nValerie,C\n");

            // Store original file attributes
            BasicFileAttributes orig = Files.readAttributes(grades, BasicFileAttributes.class);

            // Replace original file with the modified one which has the same file size
            Files.delete(grades);
            Files.writeString(grades, "Allaya,B+\nSrida,A-\nValerie,A\n");

            // Compare attributes of modified file
            BasicFileAttributes comparison = Files.readAttributes(grades, BasicFileAttributes.class);

            if (orig.size() != comparison.size())
            	System.out.println("File's size has been modified");
            else if (!Objects.equals(orig.creationTime(), comparison.creationTime()))
            	System.out.println("File's creation time has been modified");
            else if (!Objects.equals(orig.lastModifiedTime(), comparison.lastModifiedTime()))
            	System.out.println("File's last updated time has been modified");

            else
            	System.out.println("File has not been modified");
        } 
        catch(Exception e) {
        	System.out.println("Error occurred: " + e);
        }
    }
}
