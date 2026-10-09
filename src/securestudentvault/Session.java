package securestudentvault;

/**
 * A logged-in user's server-side session: who they are and what role they have.
 *
 * OBJ11-J (be wary of letting constructors throw exceptions): the constructor
 * throws if its arguments are invalid, but by then the object has already been
 * allocated. Because this class is NOT final, a subclass could override
 * finalize() and recover that half-built object (see SecureStudentVault.stealHalfBuiltSession). To make
 * such an object useless, this class uses the "initialized flag" technique:
 * the flag is set as the very last step of the constructor, and every method
 * checks it before doing anything.
 *
 * MET03-J: the methods that make security decisions are final, so a subclass
 * cannot override them to skip the initialized check.
 */
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
