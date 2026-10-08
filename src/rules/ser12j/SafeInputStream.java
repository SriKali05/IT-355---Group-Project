/*
 * File: SafeInputStream.java
 * Srida Kalidindi
 * Class: IT 355 Group Project 01
 */
package rules.ser12j;
 
//working example code explaining rule SER12-J
//SER12-J: prevent deserialization of untrusted data
 
import java.io.*;

/** COMPLIANT: checks the class name BEFORE the object is created. */
class SafeInputStream extends ObjectInputStream {
 
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
