package rules.obj11j.noncompliant;

/**
 * Runs the finalizer attack demonstration. If the finalizer attack is successful,
 * the program prints the welcome message even though verification failed.
 */
class BadConstructorExceptions {
    /**
     * Start of program
     * @param args
     */
    public static void main(String[] args) {
        Attacker stolen = Attacker.getStolenInstance();

        if (stolen != null) {
            stolen.greet();
        } else {
            System.out.println("No instance obtained.");
        }
    }
}