/*
 * Package: rules.ser05j
 * File: MainClass.java
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package rules.ser05j;

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
	 * Separately creates a Student and a Name object, showing that
	 * they work independently of each other, even though Student makes
	 * use of the Name class. It then uses Seralization to write the
	 * Student and Name objects, and read them back into new variables.
	 * The output shows that the deserialized objects match the contents
	 * of the original objects. Because the static class Name implements
	 * Serializable, it automatically gets serealized during the Student's
	 * serialization process.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Student s1 = new Student(392415234, "Robert", "Adams");
		Student s2 = null;
		Student.Name n1 = new Student.Name("Harold", "Bauer");
		Student.Name n2 = null;
		System.out.println("UID: " + s1.getUid() + " First Name: " + s1.getFirstName() + " Last name: " + s1.getLastName());
		System.out.println("First Name: " + n1.getFname() + " Last name: " + n1.getLname());

		try {
			FileOutputStream fout = new FileOutputStream("Student");
		    ObjectOutputStream oout = new ObjectOutputStream(fout);
		    oout.writeObject(s1);
		    oout.close();
		    System.out.println("Student has been serialized.");
		    
			fout = new FileOutputStream("Name");
		    oout = new ObjectOutputStream(fout);
		    oout.writeObject(n1);
		    oout.close();
		    System.out.println("Name has been serialized.");

			FileInputStream fin = new FileInputStream("Student");
		    ObjectInputStream oin = new ObjectInputStream(fin);
		    s2 = (Student) oin.readObject();
		    oin.close();
		    System.out.println("Student has been deserialized.");
		    
			fin = new FileInputStream("Name");
		    oin = new ObjectInputStream(fin);
		    n2 = (Student.Name) oin.readObject();
		    oin.close();
		    System.out.println("Name has been deserialized.");
		}
		catch(Exception e) {
			System.out.println("Error occurred in serialization or deserialization.");
		}
		
		System.out.println("UID: " + s2.getUid() + " First Name: " + s2.getFirstName() + " Last name: " + s2.getLastName());
		System.out.println("First Name: " + n2.getFname() + " Last name: " + n2.getLname());
	}
}
