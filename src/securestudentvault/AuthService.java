package securestudentvault;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/* AUTHENTICATION: AuthService, Session, AttemptCounter */
/**
 * Handles user registration, login, and password verification.
 */
class AuthService {
    private static final int ITERATIONS = 120_000;
    private static final int KEY_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Stores a user's password salt, password hash and assigned role.
     *
     * OBJ01-J: the class is private and its fields are private, so password
     * material can only be read by AuthService. It is a static nested class:
     * it never touches AuthService's private members, so there is no outer
     * data for it to expose (compare StudentRegistry.SummaryView, which is
     * the vault's OBJ08-J example).
     */
    private static final class Credential {
        private final byte[] salt;
        private final byte[] hash;
        private final Role role;

        /**
         * Creates a stored credential.
         *
         * @param salt the random salt used when hashing the password
         * @param hash the PBKDF2 hash of the password
         * @param role the user's assigned role
         */
        private Credential(byte[] salt, byte[] hash, Role role) {
            this.salt = salt;
            this.hash = hash;
            this.role = role;
        }
    }

    private final Map<String, Credential> users = new HashMap<>();
    private final AttemptCounter failures = new AttemptCounter();
    private final AuditLog audit;

    /**
     * Creates an authentication service with the given audit log.
     *
     * @param audit the audit log used to record security events
     * @throws IllegalArgumentException if the audit log is null
     */
    AuthService(AuditLog audit) {
        if (audit == null) {
            throw new IllegalArgumentException("audit log required");
        }
        this.audit = audit;
    }

     /**
     * Registers a new user and securely stores their password.
     *
     * @param username the user's username
     * @param password the user's password
     * @param confirm the password confirmation
     * @param role the user's assigned role
     * @throws GeneralSecurityException if password hashing fails
     * @throws IllegalArgumentException if any input is invalid
     */
    // MET03-J: methods that perform security checks are FINAL (public API) or PRIVATE (helpers),
    // so a subclass cannot override them and skip the check.
    public final synchronized void register(String username, char[] password, char[] confirm, Role role)
            throws GeneralSecurityException {
        try {
            Check.matches(username, SecureStudentVault.USER_PATTERN, "username");     // MET00-J
            Check.password(password, 8);
            if (role == null) {
                throw new IllegalArgumentException("role must not be null");
            }
            // EXP02-J: compare array CONTENTS with Arrays.equals(), not password.equals(confirm).
            if (confirm == null || !Arrays.equals(password, confirm)) {
                throw new IllegalArgumentException("password confirmation does not match");
            }
            byte[] salt = new byte[16];
            RANDOM.nextBytes(salt);
            Credential c = new Credential(salt, hash(password, salt), role);
            if (users.putIfAbsent(username, c) != null) {                              // EXP00-J
                throw new IllegalArgumentException("username already taken");
            }
            // FIO13-J: log a masked name; never the password.
            audit.info("Registered user " + LogSafe.mask(username) + " as " + role);
        } finally {
            wipe(password);
            wipe(confirm);
        }
    }

    /**
     * Attempts to log in a user with the given credentials.
     *
     * @param username the user's username
     * @param password the user's password
     * @return a session if login succeeds, or an empty result if it fails
     * @throws GeneralSecurityException if password verification fails
     */
    public final synchronized Optional<Session> login(String username, char[] password)
            throws GeneralSecurityException {
        try {
            Check.matches(username, SecureStudentVault.USER_PATTERN, "username");     // MET00-J
            Check.password(password, 1);
            Credential c = users.get(username);
            if (c == null || !verifyPassword(c, password)) {
                failures.increment();
                audit.warn("Failed login for " + LogSafe.mask(username));
                if (failures.isAlarmRaised()) {
                    audit.warn("ALERT: repeated login failures detected");
                }
                return Optional.empty();
            }
            audit.info("Login succeeded for " + LogSafe.mask(username));
            return Optional.of(new Session(username, c.role));
        } finally {
            wipe(password);
        }
    }

    /**
     * Returns the number of failed login attempts.
     *
     * @return the number of failed attempts
     */
    public final int failureCount() {
        return failures.get();
    }

    /**
     * Checks whether the failed-login alarm has been triggered.
     *
     * @return true if the alarm is raised
     */
    public final boolean alarmRaised() {
        return failures.isAlarmRaised();
    }

    /**
     * Verifies a password against the stored password hash.
     *
     * @param c the user's stored credentials
     * @param password the password to verify
     * @return true if the password matches
     * @throws GeneralSecurityException if hashing fails
     */
    // MET03-J: private, cannot be overridden.
    private boolean verifyPassword(Credential c, char[] password) throws GeneralSecurityException {
        byte[] attempt = hash(password, c.salt);
        return MessageDigest.isEqual(c.hash, attempt);      // content comparison, constant time
    }

    /**
     * Creates a secure password hash using PBKDF2.
     *
     * @param password the password to hash
     * @param salt the random salt
     * @return the generated password hash
     * @throws GeneralSecurityException if the hashing operation fails
     */
    private static byte[] hash(char[] password, byte[] salt) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }

    /**
     * Clears a password array from memory.
     *
     * @param a the password array to clear
     */
    private static void wipe(char[] a) {
        if (a != null) {
            Arrays.fill(a, '\0');
        }
    }
}
