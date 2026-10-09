/*
 * Package: rules.ser05j
 * File: Student.java
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package rules.ser05j;

import java.io.Serializable;

/**
 * Stores a Student's university id as an int, and the Student's name as an object
 * of type Name, which is a static inner class. Student implements Serializable,
 * and Name also implements Serializable, which is only acceptable because
 * Name is a static class.
 */
public class Student implements Serializable{
	private static final long serialVersionUID = 1L;
	private int uid;
	private Name sname;
	
	/**
	 * Student constructor, which calls the Name constructor
	 * @param uid the student's university id
	 * @param fname the student's first name
	 * @param lname the student's last name
	 */
	public Student(int uid, String fname, String lname) {
		this.uid = uid;
		this.sname = new Name(fname, lname);
	}
	
	/**
	 * Getter for Student's university id
	 * @return uid
	 */
	public int getUid() {
		return this.uid;
	}
	
	/**
	 * Getter for Student's first name, which calls the getter for its Name object
	 * @return student's first name
	 */
	public String getFirstName() {
		return this.sname.getFname();
	}
	
	/**
	 * Getter for Student's last name, which calls the getter for its Name object
	 * @return student's last name
	 */
	public String getLastName() {
		return this.sname.getLname();
	}
	
	/**
	 * Static inner class, which stores both a first name
	 * and a last name as separate Strings. It implements
	 * Serializable, which is only acceptable for an inner class
	 * because it is also a static class.
	 */
	protected static class Name implements Serializable{
		private static final long serialVersionUID = 1L;
		private String fname;
		private String lname;
		
		/**
		 * Name constructor
		 * @param fname the first name
		 * @param lname the last name
		 */
		protected Name(String fname, String lname) {
			this.fname = fname;
			this.lname = lname;
		}
		
		/**
		 * Getter for first name from Name
		 * @return fname
		 */
		protected String getFname() {
			return this.fname;
		}
		
		/**
		 * Getter for last name from Name
		 * @return lname
		 */
		protected String getLname() {
			return this.lname;
		}
	}
}
