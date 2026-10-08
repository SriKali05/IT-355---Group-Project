package rules.obj05j;

/**
 * working example for OBJ05-J
 *
 * OBJ05-J: Do not return references to private mutable class members
 *
 * this example returns a copy of a private array so that outside
 * code cannot directly modify the class's internal data
 */
public class Scores {

    /**
     * private mutable data owned by the class
     */
    private int[] scores = {90, 85, 95};

    /**
     * returns a copy of the scores array
     *
     * @return a copy of the private scores array
     */
    public int[] getScores() {
        return scores.clone();
    }

}
