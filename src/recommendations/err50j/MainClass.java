package recommendations.err50j;

/**
 * Demonstrates CERT rule ERR50-J.
 *
 * Exceptions should not be used for normal program control flow.
 * The noncompliant example uses NumberFormatException to check whether
 * a string is a number. The compliant example checks the characters
 * directly without using an exception.
 */
public class MainClass {

    /**
    * Runs the noncompliant and compliant examples. 
    * The program also compares the time taken by both methods when checking 
    * invalid input. The noncompliant method throws an exception for each
    * invalid input, while the compliant method uses a normal condition check. 
    * 
    *  @param args 
    * */
    public static void main(String[] args) {
        String[] inputs = { "123", "abc", "45x", "" };

        System.out.println("Noncompliant:");
        for (String s : inputs) {
            System.out.println("  \"" + s + "\" is a number? " + isNumberNoncompliant(s));
        }

        System.out.println("\nCompliant:");
        for (String s : inputs) {
            System.out.println("  \"" + s + "\" is a number? " + isNumberCompliant(s));
        }

        // Timing: 100,000 bad inputs, which is the case where the exception path runs every time
        int runs = 100_000;

        long start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
        	isNumberNoncompliant("abc");
        }
        long noncompliantMs = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
        	isNumberCompliant("abc");
        }
        long compliantMs = (System.nanoTime() - start) / 1_000_000;

        System.out.println("\nTime for " + runs + " invalid inputs:");
        System.out.println("  Noncompliant (exceptions): " + noncompliantMs + " ms");
        System.out.println("  Compliant (plain check):   " + compliantMs + " ms");
    }
    
    /** 
    * NONCOMPLIANT: Uses an exception to check whether a string contains
    * only digits. 
    * If the string is not a valid integer, parseInt() throws an exception.
    * The exception is being used as part of the normal program logic. 
    * 
    * @param text the string to check 
    * @return true if the string can be parsed as an integer, otherwise false 
    */
    static boolean isNumberNoncompliant(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false; // exception used as ordinary control flow
        }
    }

    /** 
    * COMPLIANT: Checks the string directly without using an exception.
    * Each character is checked to see if it is a digit. If a character 
    * is not a digit, the method returns false. 
    * @param text the string to check 
    * @return true if the string contains only digits, otherwise false 
    */
    static boolean isNumberCompliant(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}