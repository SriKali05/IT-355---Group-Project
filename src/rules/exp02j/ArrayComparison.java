/*
 * File: ArrayComparison.java
 * Author: Srida Kalidindi
 * Class: IT 355 Group Project 01
 */
package rules.exp02j;

//working example code explaining rule EXP02-J
//EXP02-J: do not use the Object.equals() method to compare two arrays

import java.util.Arrays;

/**
 * Example of EXP02-J: do not use the Object.equals() method to compare
 * two arrays.
 */
public class ArrayComparison {
    
	/**
     * Runs the demonstration.
     *
     * @param args not used
     */
    public static void main(String[] args) {
    	char[] answerKey  = {'B', 'D', 'A', 'C', 'A'};
        char[] quizSubmission = {'B', 'D', 'A', 'C', 'A'};
        
        // NONCOMPLIANT: Object.equals() compares references, not contents.
        System.out.println("[Noncompliant] answerKey.equals(quizSubmission): " + answerKey.equals(quizSubmission)); // Prints false

        // COMPLIANT: Arrays.equals() compares the contents of the arrays.
        System.out.println("[Compliant] Arrays.equals(arr1, arr2): " + Arrays.equals(answerKey, quizSubmission)); // Prints true
    }
}