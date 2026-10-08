package recommendations.met50;

/**
 * working example for MET50-J
 *
 * MET50-J: Avoid ambiguous or confusing uses of overloading
 *
 * this example uses different method names for different types
 * of searches instead of using confusing overloaded methods
 */
public class Student {
	private String name;
	private int id;
	
	/**
	 * constructor
	 * @param name
	 * @param id
	 */
	public Student(int id, String name) {
		this.name = name;
		this.id = id;
	}
	
	/**
	 * gets student name
	 * @return name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * gets student id
	 * @return id
	 */
	public int getId() {
		return this.id;
	}	
	
	public String toString() {
		return "Student name: " + this.name + "; Student id: " + this.id;
	}
}
