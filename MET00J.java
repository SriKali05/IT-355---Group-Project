/**
 * Demonstrates MET00-J by validating a method argument
 * before allowing it to modify an object's state.
 */
public class StudentScore {

    private int score;

    /**
     * Creates a student score with an initial value.
     *
     * @param initialScore the student's starting score
     * @throws IllegalArgumentException if the score is outside 0 through 100
     */
    public StudentScore(int initialScore) {
        setScore(initialScore);
    }

    /**
     * Changes the student's score after validating the argument.
     *
     * @param newScore the new score to store
     * @throws IllegalArgumentException if newScore is outside 0 through 100
     */
    public void setScore(int newScore) {
        if (newScore < 0 || newScore > 100) {
            throw new IllegalArgumentException(
                "Score must be between 0 and 100."
            );
        }

        score = newScore;
    }

    /**
     * Returns the student's current score.
     *
     * @return the current score
     */
    public int getScore() {
        return score;
    }

    /**
     * Runs a simple demonstration of argument validation.
     *
     * @param args command-line arguments
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