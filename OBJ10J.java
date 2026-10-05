/**
 * working example for OBJ10-J
 *
 * OBJ10-J: Do not use public static nonfinal fields
 *
 * this example uses a private static field instead of exposing
 * a public static field that outside code could modify directly
 */
public class OBJ10J {

    /**
     * maximum number of login attempts allowed
     * this value cannot be changed after initialization
     */
    public static final int MAX_LOGIN_ATTEMPTS = 3;

    /**
     * stores the current number of login attempts
     * this field is private so outside code cannot change it directly
     */
    private static int loginAttempts = 0;

    /**
     * Records a login attempt.
     */
    public static void recordLoginAttempt() {
        loginAttempts++;
    }

    /**
     * Returns the current number of login attempts.
     *
     * @return current number of login attempts
     */
    public static int getLoginAttempts() {
        return loginAttempts;
    }

    /**
     * demonstrates controlled access to a static field
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        recordLoginAttempt();
        recordLoginAttempt();
        recordLoginAttempt();

        System.out.println("Maximum attempts: "
                + MAX_LOGIN_ATTEMPTS);

        System.out.println("Current attempts: "
                + getLoginAttempts());
    }
}
