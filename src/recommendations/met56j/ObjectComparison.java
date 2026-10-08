/*
 * File: MainClass.java
 * Srida Kalidindi
 * Class: IT 355 Group Project 01
 */
package recommendations.met56j;

//working example code explaining recommendation MET56-J
//MET56-J: do not use Object.equals() to compare cryptographic keys

import java.security.Key;
import java.util.Arrays;

/**
 * Example of MET56-J: do not use Object.equals() to compare
 * cryptographic keys.
 */
public class ObjectComparison {
	
    /**
     * Runs the demonstration.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        // Two separate key objects holding the SAME key value
        Key key1 = new SimpleKey(new byte[] {1, 2, 3, 4});
        Key key2 = new SimpleKey(new byte[] {1, 2, 3, 4});

        System.out.println("Noncompliant (equals only):   " + keysEqualBad(key1, key2));
        System.out.println("Compliant (compare contents): " + keysEqualGood(key1, key2));
    }

    /** A simple key class that does NOT override equals(), like many real Key classes. */
    static class SimpleKey implements Key {
        private final byte[] bytes;

        /**
         * Creates a key from the given bytes.
         *
         * @param bytes the key value
         */
        SimpleKey(byte[] bytes) { this.bytes = bytes; }

        /** @return the algorithm name */
        public String getAlgorithm() { return "Simple"; }

        /** @return the key format */
        public String getFormat()    { return "RAW"; }

        /** @return a copy of the key bytes */
        public byte[] getEncoded()   { return bytes.clone(); }
    }

    /**
     * NONCOMPLIANT: relies on equals() alone.
     *
     * @param key1 the first key
     * @param key2 the second key
     * @return true if equals() says the keys match
     */
    private static boolean keysEqualBad(Key key1, Key key2) {
        return key1.equals(key2);
    }

    /**
     * COMPLIANT: tries equals() first, then compares the encoded bytes.
     *
     * @param key1 the first key
     * @param key2 the second key
     * @return true if the keys are the same object or have the same bytes
     */
    private static boolean keysEqualGood(Key key1, Key key2) {
        if (key1.equals(key2)) {
            return true;
        }
        return Arrays.equals(key1.getEncoded(), key2.getEncoded());
    }
}