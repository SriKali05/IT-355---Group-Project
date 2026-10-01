package src.rules;

//working example code explaining rule EXP02-J
//EXP02-J: do not use the Object.equals() method to compare two arrays

import java.util.Arrays;

public class EXP02J {

    //NONCOMPLIANT: Object.equals() compares references, not contents
    static void noncompliant() {
        int[] arr1 = new int[20]; // Initialized to 0
        int[] arr2 = new int[20]; // Initialized to 0
        System.out.println("[Noncompliant] arr1.equals(arr2): " + arr1.equals(arr2)); // Prints false
    }

    //COMPLIANT: Arrays.equals() compares contents
    static void compliantContents() {
        int[] arr1 = new int[20]; // Initialized to 0
        int[] arr2 = new int[20]; // Initialized to 0
        System.out.println("[Compliant] Arrays.equals(arr1, arr2): " + Arrays.equals(arr1, arr2)); // Prints true
    }

    //COMPLIANT: == makes it explicit that references are being compared
    static void compliantReferences() {
        int[] arr1 = new int[20]; // Initialized to 0
        int[] arr2 = new int[20]; // Initialized to 0
        System.out.println("[Compliant] arr1 == arr2: " + (arr1 == arr2)); // Prints false
    }

    public static void main(String[] args) {
        noncompliant();
        compliantContents();
        compliantReferences();
    }
}
