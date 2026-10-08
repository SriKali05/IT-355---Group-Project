/**
 * OBJ11-J: this class is NOT final, so it uses the "initialized flag"
 * technique. The flag is set as the very last step of the constructor,
 * and every method checks it first.
 * MET03-J: security-check methods are final.
 */

package securestudentvault;

class Session {
    private final String username;
    private final Role role;
    private volatile boolean initialized;

    /**
     * creates a session after validating the username and role
     *
     * @param username the username for the session
     * @param role the role for the session
     */
    Session(String username, Role role) {
        this.username = Check.matches(username, SecureStudentVault.USER_PATTERN, "username");
        if (role == null) {
            throw new IllegalArgumentException("role must not be null");
        }
        this.role = role;
        this.initialized = true; // last statement
    }

    /**
     * checks that the session has been fully initialized
     *
     * @throws IllegalStateException if the session is not fully initialized
     */
    private void ensureInitialized() {
        if (!initialized) {
            throw new IllegalStateException("session is not fully initialized");
        }
    }

    /**
     * checks whether the session belongs to a teacher
     *
     * @return true if the role is TEACHER
     */
    final boolean isTeacher() {
        ensureInitialized();
        return role == Role.TEACHER;
    }

    /**
     * returns a masked version of the username
     *
     * @return the masked username
     */
    final String maskedName() {
        ensureInitialized();
        return LogSafe.mask(username);
    }
}
