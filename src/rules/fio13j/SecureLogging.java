package rules.fio13j;

/**
 * Demonstrates FIO13-J by recording useful security events
 * without placing a user's password in the log.
 */
public class SecureLogging {

    /**
     * Runs the secure logging example.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        boolean result =
            SecureLoginLogger.authenticate("student", "SecurePass123");

        System.out.println("Authentication result: " + result);
    }
}