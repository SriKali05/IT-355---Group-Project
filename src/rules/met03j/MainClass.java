package rules.met03j;

/**
 * Demonstrates MET03-J with authorized and unauthorized access attempts
 */
public class MainClass {
    /**
     * Demonstrates authorized and unauthorized access attempts.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SecureDocument document = new SecureDocument();

        document.readDocument(false);
        document.readDocument(true);
    }
}