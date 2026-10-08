package rules.met00j;

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

}