/**
 * File: LogSafe.java
 * Srida Kalidindi
 * Class: IT 355 Group Project 01
 */
package src.securestudentvault;
 
/* =====================================================================================
 *  LOGGING   (ERR02-J, FIO13-J, FIO01-J, FIO14-J)
 * ===================================================================================== */
 
/**
 * Helper methods that make log messages safe to write.
 */
final class LogSafe {
 
    /** Prevents creating instances of this utility class. */
    private LogSafe() { }
 
    /**
     * FIO13-J: identifiers are masked before they reach a log that is outside the trust boundary.
     *
     * @param id the identifier to mask
     * @return the first character followed by ***, or *** if the id is null or empty
     */
    static String mask(String id) {
        return (id == null || id.isEmpty()) ? "***" : id.charAt(0) + "***";
    }
 
    /**
     * Prevents forged log lines via CR/LF. The returned value is used (EXP00-J).
     *
     * @param s the text to clean
     * @return the text with line breaks replaced by underscores, or an empty string if null
     */
    static String clean(String s) {
        return s == null ? "" : s.replaceAll("[\\r\\n]", "_");
    }
}
 