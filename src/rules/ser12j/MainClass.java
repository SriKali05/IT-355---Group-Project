/**
 * File: MainClass.java
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
public class MainClass {
 
    /** Simulates a dangerous class that runs code when it is deserialized. */
    static class Evil implements Serializable {
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
 
    /** COMPLIANT: checks the class name BEFORE the object is created. */
    static class SafeInputStream extends ObjectInputStream {
 
        /**
         * Creates a stream that only allows whitelisted classes.
         *
         * @param in the underlying input stream
         * @throws IOException if the stream cannot be read
         */
        SafeInputStream(InputStream in) throws IOException {
            super(in);
        }
 
        /**
         * Rejects any class that is not on the whitelist (String only).
         *
         * @param cls the class described in the stream
         * @return the resolved class
         * @throws IOException if the class is not allowed
         * @throws ClassNotFoundException if the class cannot be found
         */
        @Override
        protected Class<?> resolveClass(ObjectStreamClass cls)
                throws IOException, ClassNotFoundException {
            if (!cls.getName().equals("java.lang.String")) { // whitelist: String only
                throw new InvalidClassException(cls.getName(), "Class not allowed");
            }
            return super.resolveClass(cls);
        }
    }
 
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
