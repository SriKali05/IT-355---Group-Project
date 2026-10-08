/*
 * File: UntrustedDeserialization.java
 * Srida Kalidindi
 * Class: IT 355 Group Project 01
 */
package rules.ser12j;
 
//working example code explaining rule SER12-J
//SER12-J: prevent deserialization of untrusted data
 
import java.io.*;
 
/**
 * Example of SER12-J: prevent deserialization of untrusted data.
 */
public class UntrustedDeserialization {
    /**
     * Runs the demonstration, deserializing the same untrusted bytes with a
     * normal stream and then with the whitelisting stream.
     *
     * @param args not used
     * @throws Exception if serialization fails
     */
    public static void main(String[] args) throws Exception {
        //Simulates untrusted input from an attacker
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(new Evil());
        byte[] untrusted = baos.toByteArray();
 
        //NONCOMPLIANT: reads whatever class the bytes describe
        System.out.println("Noncompliant: deserializing...");
        new ObjectInputStream(new ByteArrayInputStream(untrusted)).readObject();
 
        //COMPLIANT: only whitelisted classes are allowed
        System.out.println("Compliant: deserializing with whitelist...");
        try {
            new SafeInputStream(new ByteArrayInputStream(untrusted)).readObject();
        } catch (InvalidClassException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
 
}
