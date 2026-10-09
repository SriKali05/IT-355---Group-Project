package rules.met00j;

/**
 * Demonstrates MET00-J (validate method arguments) using StudentScore,
 * which rejects scores outside the range 0 to 100.
 */
public class ArgumentValidation{
	/**
	 * Creates a valid score, then tries to set an invalid one and shows
	 * that the invalid value is rejected and the old score is kept.
	 *
	 * @param args command-line arguments (not used)
	 */
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