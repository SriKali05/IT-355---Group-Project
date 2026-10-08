/**
 * File: GradeScale.java
 * Srida Kalidindi
 * Class: IT 355 Group Project 01
 */
package securestudentvault;
 
/**
 * Converts numeric averages to letter grades.
 */
final class GradeScale {
    // OBJ13-J: mutable arrays are private; only clones leave the class.
    private static final int[] THRESHOLDS = {90, 80, 70, 60};
    private static final String[] LETTERS = {"A", "B", "C", "D"};
 
    /** Prevents creating instances of this utility class. */
    private GradeScale() { }
 
    /**
     * Returns a copy of the grade thresholds so callers cannot change them.
     *
     * @return a copy of the minimum average for each letter grade
     */
    static int[] thresholds() {
        return THRESHOLDS.clone();
    }
 
    /**
     * Finds the letter grade for an average.
     *
     * @param average the numeric average
     * @return the letter grade (A, B, C, D, or F)
     */
    static String letterFor(double average) {
        for (int i = 0; i < THRESHOLDS.length; i++) {
            if (average >= THRESHOLDS[i]) {
                return LETTERS[i];
            }
        }
        return "F";
    }
}
