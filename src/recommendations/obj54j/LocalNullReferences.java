/*
 * File: LocalNullReferences.java
 * Srida Kalidindi
 * Class: IT 355 Group Project 01
 */
package recommendations.obj54j;

import java.util.Arrays;

//working example code explaining recommendation OBJ54-J
//OBJ54-J: do not attempt to help the garbage collector by setting local reference variables to null
 
/**
 * Example of OBJ54-J: do not attempt to help the garbage collector
 * by setting local reference variables to null.
 */
public class LocalNullReferences {
 
    /**
     * Runs the demonstration, showing a noncompliant null assignment and a
     * compliant block that limits the variable's scope instead.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        int[] examScores = {71, 84, 90, 65, 78};
        System.out.println("Noncompliant curved average: " + noncompliant(examScores));
        System.out.println("Compliant curved average:    " + compliant(examScores));
    }
    
    /**
     * NONCOMPLIANT: Sets the local 'average' reference variable to null
     *
     * @param scores the raw exam scores
     * @return average score after a 5-point curve
     */
    static double noncompliant(int[] scores) {
        int[] curved = scores.clone();
        for(int i = 0; i < scores.length; i++)
        	curved[i] = curved[i] + 5;

        double average = Arrays.stream(curved).average().orElse(0);
        curved = null; // noncompliant: setting local reference variable to null is unnecessary
        
        return average;
    }
    
    /**
     * COMPLIANT: Does not set the local 'average' reference variable to null
     *
     * @param scores the raw exam scores
     * @return the average after a 5-point curve
     */
    static double compliant(int[] scores) {
        int[] curved = scores.clone();
        for(int i = 0; i < scores.length; i++)
        	curved[i] = curved[i] + 5;
        
        double average = Arrays.stream(curved).average().orElse(0);

        return average;
    }
}