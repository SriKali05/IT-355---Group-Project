package src.securestudentvault;
import java.util.regex.Pattern;

// Validation Helpers: (MET00-J)
/**
 * Provides helper methods for validating input values.
 */
final class Check {
    private Check() { }

    /**
     * Checks that a value is within the given range 
     *
     * @param value the value to check
     * @param lo the minimum allowed value
     * @param hi the maximum allowed value
     * @param label the name of the value
     * @return the validated value
     * @throws IllegalArgumentException if the value is outside the range
     */
    static int inRange(int value, int lo, int hi, String label) {
        if (value < lo || value > hi) {
            throw new IllegalArgumentException(label + " must be between " + lo + " and " + hi);
        }
        return value;
    }
    /**
     * Checks that a string is not null and matches a pattern.
     *
     * @param value the string to check
     * @param pattern the required pattern
     * @param label the name of the value
     * @return the validated string
     * @throws IllegalArgumentException if the string is null or has an invalid format
     */
    /** Null check is explicit, so nobody ever needs to catch NullPointerException (ERR08-J). */
    static String matches(String value, Pattern pattern, String label) {
        if (value == null) {
            throw new IllegalArgumentException(label + " must not be null");
        }
        if (!pattern.matcher(value).matches()) {
            throw new IllegalArgumentException(label + " has an invalid format");  
        }
        return value;
    }
    /**
     * Checks that a password is not null and has an allowed length.
     *
     * @param pw the password to check
     * @param minLen the minimum password length
     * @throws IllegalArgumentException if the password is null or has an invalid length
     */
    static void password(char[] pw, int minLen) {
        if (pw == null) {
            throw new IllegalArgumentException("password must not be null");
        }
        if (pw.length < minLen || pw.length > 128) {
            throw new IllegalArgumentException("password length must be " + minLen + "-128 characters");
        }
    }
}
