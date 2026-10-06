import java.util.logging.Logger;

/**
 * Demonstrates FIO13-J by recording useful security events
 * without placing a user's password in the log.
 */
public class SecureLoginLogger {

    private static final Logger LOGGER =
        Logger.getLogger(SecureLoginLogger.class.getName());

    /**
     * Attempts to authenticate a user without logging the password.
     *
     * @param username the account attempting to sign in
     * @param password the sensitive password supplied by the user
     * @return true if the demonstration credentials are correct
     */
    public static boolean authenticate(String username, String password) {
        boolean authenticated =
            "student".equals(username)
            && "SecurePass123".equals(password);

        if (authenticated) {
            LOGGER.info("Successful login attempt.");
        } else {
            LOGGER.warning("Failed login attempt.");
        }

        /*
         * The password is intentionally never included in either
         * log message because it is sensitive information.
         */
        return authenticated;
    }

    /**
     * Runs the secure logging example.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        boolean result =
            authenticate("student", "SecurePass123");

        System.out.println("Authentication result: " + result);
    }
}