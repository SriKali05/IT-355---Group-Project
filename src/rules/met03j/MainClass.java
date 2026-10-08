package met01j;

/**
 * Demonstrates MET03-J by preventing a security-sensitive
 * method from being overridden.
 */
public class MainClass {

    private final String contents = "Confidential employee information";

    /**
     * Reads the protected document only when the user is authorized.
     * The method is final so a subclass cannot remove the check.
     *
     * @param authorized true when the user has permission
     */
    public final void readDocument(boolean authorized) {
        if (!authorized) {
            System.out.println("Access denied.");
            return;
        }

        System.out.println("Document: " + contents);
    }

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