package rules.obj05j;

/**
 * working example for OBJ05-J
 *
 * OBJ05-J: Do not return references to private mutable class members
 *
 * this example returns a copy of a private array so that outside
 * code cannot directly modify the class's internal data
 */
public class MainClass {

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

    /**
     * demonstrates that changing the returned array does not
     * change the original private array
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        OBJ05J example = new OBJ05J();

        int[] returnedScores = example.getScores();

        // Modify the returned copy.
        returnedScores[0] = 0;

        System.out.println("Returned score: " + returnedScores[0]);

        // the original private data is unchanged
        System.out.println("Original score: " + example.getScores()[0]);
    }
}
