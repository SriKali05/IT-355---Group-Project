/*
 * Package: rules.ser01j
 * File: MainClass.java
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package rules.ser01j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Main class
 */
public class MainClass {
	/**
	 * Main method
	 * 
	 * Creates a Student object and performs serialization and deserialization
	 * on it, demonstrating that it correctly stores the object and gets it back.
	 * The security aspect here is entirely in how the Student class implements
	 * the serialization process internally.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Student s1 = new Student("John", 987312490);
		Student s2 = null;
		System.out.println("Student has name " + s1.getName() + " and UID " + s1.getUid());
		
		try {
			FileOutputStream fout = new FileOutputStream("Student");
		    ObjectOutputStream oout = new ObjectOutputStream(fout);
		    oout.writeObject(s1);
		    oout.close();
		    System.out.println("Student has been serialized.");

			FileInputStream fin = new FileInputStream("Student");
		    ObjectInputStream oin = new ObjectInputStream(fin);
		    s2 = (Student) oin.readObject();
		    oin.close();
		    System.out.println("Student has been deserialized.");
		}
		catch(Exception e) {
			System.out.println("Error occurred in serialization or deserialization.");
		}
		
		System.out.println("Student has name " + s2.getName() + " and UID " + s2.getUid());
	}
}