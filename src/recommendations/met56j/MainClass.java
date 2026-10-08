package reccomendations.met56j;

//working example code explaining recommendation MET56-J
//MET56-J: do not use Object.equals() to compare cryptographic keys

import java.security.Key;
import java.util.Arrays;

public class MainClass {

    // A simple key class that does NOT override equals(), like many real Key classes
    static class SimpleKey implements Key {
        private final byte[] bytes;

        SimpleKey(byte[] bytes) { this.bytes = bytes; }

        public String getAlgorithm() { return "Simple"; }
        public String getFormat()    { return "RAW"; }
        public byte[] getEncoded()   { return bytes.clone(); }
    }

    // NONCOMPLIANT: relies on equals() alone
    private static boolean keysEqualBad(Key key1, Key key2) {
        return key1.equals(key2);
    }

    // COMPLIANT: equals() first, then compare the encoded bytes
    private static boolean keysEqualGood(Key key1, Key key2) {
        if (key1.equals(key2)) {
            return true;
        }
        return Arrays.equals(key1.getEncoded(), key2.getEncoded());
    }

    public static void main(String[] args) {
        // Two separate key objects holding the SAME key value
        Key key1 = new SimpleKey(new byte[] {1, 2, 3, 4});
        Key key2 = new SimpleKey(new byte[] {1, 2, 3, 4});

        System.out.println("Noncompliant (equals only):   " + keysEqualBad(key1, key2));
        System.out.println("Compliant (compare contents): " + keysEqualGood(key1, key2));
    }
}
