package securestudentvault;

/* =====================================================================================
 *  LOGGING   (ERR02-J, FIO13-J, FIO01-J, FIO14-J)
 * ===================================================================================== */

final class LogSafe {
    private LogSafe() { }

    /** FIO13-J: identifiers are masked before they reach a log that is outside the trust boundary. */
    static String mask(String id) {
        return (id == null || id.isEmpty()) ? "***" : id.charAt(0) + "***";
    }

    /** Prevents forged log lines via CR/LF. The returned value is used (EXP00-J). */
    static String clean(String s) {
        return s == null ? "" : s.replaceAll("[\\r\\n]", "_");
    }
}
