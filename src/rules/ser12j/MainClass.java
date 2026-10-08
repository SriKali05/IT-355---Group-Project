package rules.ser12j;

//working example code explaining rule SER12-J
//SER12-J: prevent deserialization of untrusted data

import java.io.*;

public class MainClass {

    //Simulates a dangerous class, runs code when it is deserialized
    static class Evil implements Serializable {
        private void readObject(ObjectInputStream in) throws Exception {
            System.out.println(">>> evil code ran during deserialization <<<");
        }
    }

    //COMPLIANT: checks the class name BEFORE the object is created
    static class SafeInputStream extends ObjectInputStream {
        SafeInputStream(InputStream in) throws IOException {
            super(in);
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass cls)
                throws IOException, ClassNotFoundException {
            if (!cls.getName().equals("java.lang.String")) { // whitelist: String only
                throw new InvalidClassException(cls.getName(), "Class not allowed");
            }
            return super.resolveClass(cls);
        }
    }

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
