/*
 * Package: rules.ser01j
 * File: Student.java
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package rules.ser01j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Class to store a student's name and university id
 */
public class Student implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private int uid;
	
	/**
	 * Student constructor
	 *
	 * @param name the student's name
	 * @param uid the student's university id (kept between 0 and 999999999)
	 */
	public Student(String name, int uid) {
		this.name = name;
		this.uid = uid;
		this.capUid();
	}
	
	/**
	 * Getter for name
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Getter for uid
	 * 
	 * @return uid
	 */
	public int getUid() {
		return this.uid;
	}
	
	/**
	 * Custom readObject method, which calls the capUid() method
	 * to ensure that the deserialized object is valid. It is crucial
	 * that this method declaration uses exactly the correct format, so
	 * that the security of the serialization process is maintained.
	 * It must be a private void, which throws IOException and ClassNotFoundException,
	 * correctly spell readObject, and has exactly one parameter of type ObjectInputStream.
	 * 
	 * @param in the stream the Student is being read from
	 * @throws IOException if the stream cannot be read
	 * @throws ClassNotFoundException if a class in the stream cannot be found
	 */
	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		this.capUid();
	}
	
	/**
	 * Private helper method, which limits the uid of this object
	 * to a valid range if it's above or below the allowed range.
	 * Used in the constructor and the readObject method
	 */
	private void capUid() {
		if(this.uid > 999999999)
			this.uid = 999999999;
		if(this.uid < 0)
			this.uid = 0;
	}
	
}
