package securestudentvault;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Records security-related events in a protected log file.
 */
final class AuditLog {
    // ERR02-J: java.util.logging handles I/O failures internally instead of throwing from our catch blocks.
    private final Logger logger = Logger.getLogger("vault.audit");
    private final FileHandler handler;

    /**
     * Creates an audit log at the specified file location.
     *
     * @param file the file used to store log messages
     * @throws IOException if the log file cannot be created
     */
    AuditLog(Path file) throws IOException {
        SecureFiles.createPrivateFile(file);                           // FIO01-J: owner-only BEFORE any data
        handler = new FileHandler(file.toString(), true);
        handler.setFormatter(new CompactFormatter());
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
        logger.setLevel(Level.INFO);
    }

    /**
     * Records an informational message.
     *
     * @param msg the message to record
     */
    void info(String msg) {
        logger.log(Level.INFO, LogSafe.clean(msg));
    }

    /**
     * Records a warning message.
     *
     * @param msg the warning message to record
     */
    void warn(String msg) {
        logger.log(Level.WARNING, LogSafe.clean(msg));
    }

    /**
     * Records an error message and its exception type.
     *
     * @param msg the error message to record
     * @param t the exception related to the error
     */
    void error(String msg, Throwable t) {
        logger.log(Level.SEVERE, LogSafe.clean(msg), t);               // never e.printStackTrace()
    }

    /**
     * Writes any pending log messages to the file.
     */
    void flush() {
        handler.flush();
    }

    /**
     * Closes the log file and removes its handler.
     */

    void close() {                                                     // FIO14-J: idempotent cleanup
        logger.removeHandler(handler);
        handler.close();
    }

    /**
     * Formats log messages while avoiding sensitive exception details.
     */
    /** FIO13-J: prints only the exception CLASS, because messages/stack traces can leak data. */
    private static final class CompactFormatter extends java.util.logging.Formatter {
        /**
         * Formats a log record into a single line.
         *
         * @param r the log record to format
         * @return the formatted log message
         */
        @Override
        public String format(LogRecord r) {
            StringBuilder sb = new StringBuilder()
                    .append(r.getInstant()).append(' ')
                    .append(r.getLevel()).append(' ')
                    .append(formatMessage(r));
            Throwable t = r.getThrown();
            if (t != null) {
                sb.append(" [").append(t.getClass().getSimpleName()).append(']');
            }
            return sb.append(System.lineSeparator()).toString();
        }
    }
}
