package src.securestudentvault;

final class GradeScale {
    // OBJ13-J: mutable arrays are private; only clones leave the class.
    private static final int[] THRESHOLDS = {90, 80, 70, 60};
    private static final String[] LETTERS = {"A", "B", "C", "D"};

    private GradeScale() { }

    static int[] thresholds() {
        return THRESHOLDS.clone();
    }

    static String letterFor(double average) {
        for (int i = 0; i < THRESHOLDS.length; i++) {
            if (average >= THRESHOLDS[i]) {
                return LETTERS[i];
            }
        }
        return "F";
    }
}
