/*
 * File: Evil.java
 * Srida Kalidindi
 * Class: IT 355 Group Project 01
 */
package rules.ser12j;
 
//working example code explaining rule SER12-J
//SER12-J: prevent deserialization of untrusted data
 
import java.io.*;

/** Simulates a dangerous class that runs code when it is deserialized. */
class Evil implements Serializable {
    /**
     * Runs automatically during deserialization.
     *
     * @param in the stream being read
     * @throws Exception if reading fails
     */
    private void readObject(ObjectInputStream in) throws Exception {
        System.out.println(">>> evil code ran during deserialization <<<");
    }
}
