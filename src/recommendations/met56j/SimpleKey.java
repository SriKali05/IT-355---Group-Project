/*
 * File: SimpleKey.java
 * Srida Kalidindi
 * Class: IT 355 Group Project 01
 */
package recommendations.met56j;

//working example code explaining recommendation MET56-J
//MET56-J: do not use Object.equals() to compare cryptographic keys

import java.security.Key;

/** A simple key class that does NOT override equals(), like many real Key classes. */
public class SimpleKey implements Key {
    private final byte[] bytes;

    /**
     * Creates a key from the given bytes.
     *
     * @param bytes the key value
     */
    SimpleKey(byte[] bytes) { this.bytes = bytes; }

    /**
     * Returns the name of the algorithm this key is for.
     * @return the algorithm name
     */
    public String getAlgorithm() { return "Simple"; }

    /**
     * Returns the encoding format of the key bytes.
     * @return the key format
     */
    public String getFormat()    { return "RAW"; }

    /**
     * Returns the key bytes. A copy is returned so callers cannot change the key.
     * @return a copy of the key bytes
     */
    public byte[] getEncoded()   { return bytes.clone(); }
}