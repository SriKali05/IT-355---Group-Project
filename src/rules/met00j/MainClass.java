package rules.met00j;

/**
 * Runs a simple demonstration of argument validation.
 *
 * @param args command-line arguments
 */
public class MainClass{
	public static void main(String[] args) {
	    StudentScore student = new StudentScore(85);
	
	    System.out.println("Current score: " + student.getScore());
	
	    try {
	        student.setScore(150);
	    } catch (IllegalArgumentException e) {
	        System.out.println("Invalid input: " + e.getMessage());
	    }
	
	    System.out.println(
	        "Score after invalid input: " + student.getScore()
	    );
	}
}