package securestudentvault;

/*
 * SecureStudentVault.java  --  IT 355 group project: one program, every rule.
 *
 * A small student-records application. Teachers log in, enter grades through a
 * (simulated) web form, records are serialized to disk, reports are written, and
 * everything is audit-logged. Each rule/recommendation is tagged in a comment
 * where it is applied, e.g.  "// MET00-J".
 *
 * RULE MAP (search for the tag to find the code)
 *   Serena : MET00-J  MET03-J  FIO01-J  OBJ08-J  FIO13-J
 *   Valerie: OBJ05-J  ERR08-J  IDS14-J  OBJ01-J  MET04-J
 *   Karsten: OBJ13-J  FIO02-J  SER01-J  SER05-J  VNA00-J
 *   Allaya : IDS00-J  OBJ11-J  FIO08-J  EXP00-J  OBJ10-J
 *   Srida  : ERR02-J  EXP02-J  IDS07-J  SER12-J  FIO14-J
 *   Bonus  : MET55-J (recommendation)
 *
 * HOW TO RUN (JDK 17+)
 *   Eclipse: Run As > Java Application. h2.jar is already on the build path.
 *   Command line, from the repository root:
 *     javac -cp h2.jar -d out src/securestudentvault/*.java
 *     java -cp "out;h2.jar" securestudentvault.SecureStudentVault      (Windows)
 *     java -cp "out:h2.jar" securestudentvault.SecureStudentVault      (macOS/Linux)
 *   (The single-file form "java SecureStudentVault.java" does NOT work: the
 *   program is split across many files in this package.)
 *
 * The SQL section uses a real in-memory H2 database (h2.jar). If h2.jar is not
 * on the classpath, that section is skipped and the rest of the demo still runs.
 */

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/* =====================================================================================
 *  MAIN CLASS / DEMO DRIVER
 * ===================================================================================== */
/** Runs demonstrations of secure student-record operations and security rules.
 */
public final class SecureStudentVault {

    // OBJ10-J: the only public static fields are final, and of immutable types.
    public static final String APP_NAME = "Secure Student Vault";
    public static final int MIN_UID = 1000;
    public static final int MAX_UID = 9999;

    // Package-private shared validation patterns (final; Pattern is immutable).
    static final Pattern NAME_PATTERN = Pattern.compile("[A-Za-z][A-Za-z '\\-]{0,49}");
    static final Pattern USER_PATTERN = Pattern.compile("[a-z][a-z0-9_]{2,19}");

    // ERR02-J: if the demo fails before the audit log exists (for example while
    // creating the work directory), the failure still goes through a logging
    // API instead of being lost or printed with printStackTrace().
    private static final Logger STARTUP_LOG = Logger.getLogger(SecureStudentVault.class.getName());

    // OBJ01-J: every instance field is private.
    private final StudentRegistry registry = new StudentRegistry();
    private File workDir;
    private AuditLog audit;
    private Session teacher;
    private Session student;
    // OBJ11-J demo: where the finalizer attack hides a rescued Session.
    // volatile because the garbage collector's finalizer thread writes it (VNA00-J).
    private volatile Session rescuedSession;

    /** Starts the demonstration and exits with its resulting status.
         * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        int status = new SecureStudentVault().runDemo();
        // FIO14-J: runDemo() has already closed/flushed everything in a finally block,
        // so exiting here cannot lose buffered data or leave resources open.
        System.exit(status);
    }

    /** Represents an operation expected to be rejected during validation tests.
     */
    private interface Attempt {
        /** Executes the attempted operation.
                 * @throws IOException if an I/O operation fails
                 * @throws GeneralSecurityException if a security operation fails
                 * @throws SQLException if a database operation fails
         */
        void run() throws IOException, GeneralSecurityException, SQLException;
    }

    /** Runs all demonstrations and performs cleanup.
         * @return process status, zero on success
     */
    private int runDemo() {
        int status = 0;
        try {
            setup();
            demoAuthentication();
            demoDomainObjects();
            demoForms();
            demoSerialization();
            demoSql();
            demoFilesAndReports();
            demoCommandExecution();
            demoConcurrency();
            demoAuditLog();
        } catch (IOException | GeneralSecurityException | SQLException | ReflectiveOperationException e) {
            // ERR08-J: only specific, expected exception types are caught (never NPE,
            // RuntimeException, Exception or Throwable).
            status = 1;
            // ERR02-J: record the failure through a logging API, never printStackTrace().
            // Before setup() finishes there is no audit log yet, so use the startup logger;
            // otherwise the reason for the failure would be lost.
            if (audit != null) {
                audit.error("Demo aborted", e);
            } else {
                STARTUP_LOG.log(Level.SEVERE, "Demo aborted before the audit log was available", e);
            }
            System.out.println("Demo aborted: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            status = 1;
        } finally {
            status |= cleanup();                     // FIO14-J: cleanup happens on every path
        }
        System.out.println("\nExit status: " + status);
        return status;
    }

    // ---------------------------------------------------------------- 1. setup
    /** Creates the working directory and audit log.
         * @throws IOException if setup fails
     */
    private void setup() throws IOException {
        banner("1. Setup  (FIO01-J, ERR02-J)");
        // FIO01-J: the work directory is created WITH owner-only permissions in a
        // single step (POSIX rwx------ or a Windows owner-only ACL), inside the
        // shared temp folder, under an unpredictable name.
        workDir = SecureFiles.createPrivateTempDirectory("vault-").toFile();
        // FIO01-J: the audit log file is likewise created owner-only before any data is written.
        // ERR02-J: AuditLog records events through java.util.logging.
        audit = new AuditLog(workDir.toPath().resolve("audit.log"));
        audit.info(APP_NAME + " started");
        out("Work directory permissions: " + SecureFiles.describe(workDir.toPath()));
        out("Audit log permissions:      " + SecureFiles.describe(workDir.toPath().resolve("audit.log")));
    }

    // ----------------------------------------------------- 2. authentication
    /** Demonstrates registration, login, and authentication rejection.
         * @throws IOException if an I/O operation fails
         * @throws GeneralSecurityException if a security operation fails
         * @throws SQLException if a database operation fails
     */
    private void demoAuthentication() throws IOException, GeneralSecurityException, SQLException {
        banner("2. Authentication  (MET00-J, MET03-J, EXP02-J, FIO13-J, VNA00-J)");
        AuthService auth = new AuthService(audit);

        auth.register("teacher_kim", "Tr0ub4dor&3x".toCharArray(), "Tr0ub4dor&3x".toCharArray(), Role.TEACHER);
        auth.register("sam_student", "c0rrect-horse".toCharArray(), "c0rrect-horse".toCharArray(), Role.STUDENT);
        out("Registered 2 users (passwords are PBKDF2-hashed and never logged).");

        expectRejected("mismatched password confirmation (EXP02-J: Arrays.equals on char[])", () ->
                auth.register("eve_user", "password-one".toCharArray(), "password-two".toCharArray(), Role.STUDENT));
        expectRejected("too-short password (MET00-J)", () ->
                auth.register("eve_user", "abc".toCharArray(), "abc".toCharArray(), Role.STUDENT));
        expectRejected("null username (MET00-J)", () ->
                auth.register(null, "longenough1".toCharArray(), "longenough1".toCharArray(), Role.STUDENT));

        teacher = auth.login("teacher_kim", "Tr0ub4dor&3x".toCharArray()).orElse(null);
        student = auth.login("sam_student", "c0rrect-horse".toCharArray()).orElse(null);
        if (teacher == null || student == null) {                      // EXP00-J: Optional result used
            throw new GeneralSecurityException("demo login failed unexpectedly");
        }
        out("Valid logins succeeded.");

        for (int i = 0; i < 3; i++) {
            Optional<Session> bad = auth.login("teacher_kim", "wrong-password".toCharArray());
            if (bad.isPresent()) {
                throw new GeneralSecurityException("wrong password was accepted");
            }
        }
        out("3 bad logins rejected; failure counter = " + auth.failureCount()
                + ", alarm raised = " + auth.alarmRaised());
    }

    // ------------------------------------------------------ 3. domain objects
    /** Demonstrates domain-object validation and defensive copying.
         * @throws IOException if an I/O operation fails
         * @throws GeneralSecurityException if a security operation fails
         * @throws SQLException if a database operation fails
     */
    private void demoDomainObjects() throws IOException, GeneralSecurityException, SQLException {
        banner("3. Domain objects  (OBJ05/08/11/13-J, MET00-J, EXP02-J, MET55-J)");

        Student ada = new Student("Ada Lovelace", 1001);
        ada.addGrade(95);
        ada.addGrade(88);
        Student bob = new Student("Bob O'Neil", 1002);
        if (!registry.register(ada) || !registry.register(bob)) {      // EXP00-J: result checked
            throw new IllegalStateException("registration failed");
        }
        boolean dup = registry.register(new Student("Ada Clone", 1001));
        out("Duplicate uid registration accepted? " + dup);

        // MET00-J: constructors and methods validate their arguments.
        expectRejected("uid out of range (MET00-J)", () -> new Student("Bad Uid", 5));
        expectRejected("illegal characters in name (MET00-J)", () -> new Student("Robert'); DROP", 1003));
        expectRejected("grade out of range (MET00-J)", () -> ada.addGrade(150));

        // OBJ05-J: getter returns an immutable copy, not the private list.
        List<Integer> view = ada.getGrades();
        try {
            view.add(100);
            out("  [FAIL] caller modified internal grades!");
        } catch (UnsupportedOperationException e) {
            out("  [ok]   getGrades() returned an immutable copy; internal size still " + ada.getGrades().size());
        }

        // OBJ13-J: static array is private; getter returns a clone.
        int[] thresholds = GradeScale.thresholds();
        thresholds[0] = -3;
        out("  [ok]   caller scribbled on its copy; internal A-threshold is still " + GradeScale.thresholds()[0]);

        // EXP02-J: compare array CONTENTS with Arrays.equals, not Object.equals.
        boolean viaEquals = GradeScale.thresholds().equals(GradeScale.thresholds());
        boolean viaArrays = Arrays.equals(GradeScale.thresholds(), GradeScale.thresholds());
        out("  Object.equals(): " + viaEquals + "  vs  Arrays.equals(): " + viaArrays);

        // OBJ08-J: summary comes from a PRIVATE nested class; only a String escapes.
        out("  Registry summary -> " + registry.summary());

        // MET55-J: empty collection, never null.
        out("  snapshots() for an empty registry would be empty, not null: "
                + new StudentRegistry().snapshots().isEmpty());

        // OBJ11-J: when a constructor throws, the half-built object must stay unusable.
        // Student is protected by being final: no subclass can override finalize().
        out("  [ok]   OBJ11-J: Student is final, so no finalizer subclass can exist: "
                + Modifier.isFinal(Student.class.getModifiers()));
        // Session is not final, so actually attempt the finalizer attack on it.
        // Its "initialized flag" makes the rescued object refuse every call.
        Session stolen = stealHalfBuiltSession();
        if (stolen == null) {
            out("  [ok]   OBJ11-J: finalizer attack on Session recovered nothing");
        } else {
            try {
                stolen.isTeacher();
                out("  [FAIL] half-built Session recovered by a finalizer was usable!");
            } catch (IllegalStateException e) {
                out("  [ok]   OBJ11-J: finalizer recovered a half-built Session, but it refuses to work ("
                        + e.getMessage() + ")");
            }
        }
    }

    /**
     * OBJ11-J demonstration: plays the attacker and tries a "finalizer attack" on Session.
     *
     * 1. It creates an anonymous subclass of Session with a null role. Session's
     *    constructor rejects that and throws IllegalArgumentException.
     * 2. By then the object has already been allocated. It is never returned,
     *    but it still exists until garbage collection.
     * 3. Because Session is not final, the subclass can override finalize(). When
     *    the garbage collector finalizes the rejected object, finalize() stores it
     *    in a field, bringing the half-built object back.
     *
     * Session defends itself with the "initialized flag" technique (see Session),
     * so the rescued object refuses every call. Student defends itself the other
     * way OBJ11-J allows: it is final, so this kind of subclass cannot be written.
     *
     * @return the rescued, partially constructed Session, or null if the garbage
     *         collector did not finalize it in time
     */
    @SuppressWarnings({"deprecation", "removal"}) // finalize() and runFinalization() ARE the attack
    private Session stealHalfBuiltSession() {
        try {
            new Session("eve_user", null) {           // null role: the constructor throws
                /**
                 * Runs during garbage collection and stores the rejected,
                 * half-built object in a field, making it reachable again.
                 */
                @Override
                protected void finalize() {
                    rescuedSession = this;            // resurrect the rejected object
                }
            };
        } catch (IllegalArgumentException expected) {
            // The constructor refused, exactly as intended; the object still exists in memory.
        }
        // Ask the JVM to collect garbage and run finalizers until the rejected object
        // has been rescued, or give up after a bounded number of tries.
        for (int attempt = 0; attempt < 50 && rescuedSession == null; attempt++) {
            System.gc();
            System.runFinalization();
        }
        return rescuedSession;
    }

    // ------------------------------------------------------------ 4. web form
    /** Demonstrates validation of submitted grade-form fields.
     */
    private void demoForms() {
        banner("4. Hidden form fields  (IDS14-J, ERR08-J, MET00-J)");
        Map<String, String> honest = Map.of("studentUid", "1001", "grade", "92");
        out("  teacher, honest form        -> " + GradeFormHandler.submit(teacher, registry, honest));

        Map<String, String> tamperedRole = Map.of("studentUid", "1001", "grade", "100", "role", "TEACHER");
        out("  student forging role=TEACHER -> " + GradeFormHandler.submit(student, registry, tamperedRole));

        Map<String, String> tamperedUid = Map.of("studentUid", "1001 OR 1=1", "grade", "50");
        out("  tampered hidden uid          -> " + GradeFormHandler.submit(teacher, registry, tamperedUid));

        Map<String, String> unknownUid = Map.of("studentUid", "9999", "grade", "50");
        out("  well-formed but unknown uid  -> " + GradeFormHandler.submit(teacher, registry, unknownUid));

        Map<String, String> missing = Map.of("studentUid", "1001");
        out("  missing field (null check)   -> " + GradeFormHandler.submit(teacher, registry, missing));

        Map<String, String> badGrade = Map.of("studentUid", "1001", "grade", "1000");
        out("  grade out of range           -> " + GradeFormHandler.submit(teacher, registry, badGrade));
    }

    // ------------------------------------------------------- 5. serialization
    /** Demonstrates secure serialization and rejection of forged data.
         * @throws IOException if serialization or file access fails
         * @throws ReflectiveOperationException if reflective field access fails
     */
    private void demoSerialization() throws IOException, ReflectiveOperationException {
        banner("5. Serialization  (SER01-J, SER05-J, SER12-J, FIO01-J)");
        Student snap = registry.snapshot(1001).orElseThrow(() -> new IllegalStateException("student 1001 missing"));

        byte[] bytes = SerializationSupport.toBytes(snap);
        Path file = workDir.toPath().resolve("student-1001.ser");
        SecureFiles.writePrivate(file, bytes);                         // FIO01-J
        out("Wrote " + bytes.length + " bytes, permissions " + SecureFiles.describe(file));

        Student back = SerializationSupport.toStudent(Files.readAllBytes(file));
        out("Round trip OK: " + back.getName() + " uid=" + back.getUid() + " grades=" + back.getGrades());

        // SER05-J: the STATIC nested Student.Name can also be serialized on its own.
        Student.Name nameOnly = (Student.Name) SerializationSupport.fromBytes(
                SerializationSupport.toBytes(snap.getName()));
        out("Static nested class round trip OK: " + nameOnly);

        // SER12-J: class allowlist rejects unexpected types before they are created.
        try {
            SerializationSupport.fromBytes(SerializationSupport.toBytes(new java.util.Date()));
            out("  [FAIL] unexpected class was deserialized!");
        } catch (InvalidClassException e) {
            out("  [ok]   SER12-J allowlist rejected a foreign class");
        } catch (ClassNotFoundException e) {
            out("  [ok]   unknown class rejected");
        }

        // SER01-J: private readObject(ObjectInputStream) re-validates forged streams.
        Student forged = new Student("Eve Forger", 1002);
        Field uid = Student.class.getDeclaredField("uid");
        uid.setAccessible(true);
        uid.setInt(forged, 5);                                         // bypass the constructor
        try {
            SerializationSupport.toStudent(SerializationSupport.toBytes(forged));
            out("  [FAIL] forged uid accepted!");
        } catch (InvalidObjectException e) {
            out("  [ok]   SER01-J readObject() rejected the forged uid (" + e.getMessage() + ")");
        }
    }

    // ------------------------------------------------------------------ 6. SQL
    /** Demonstrates parameterized database lookups and input rejection.
         * @throws SQLException if a database operation fails
         * @throws IOException if an I/O operation fails
         * @throws GeneralSecurityException if a security operation fails
     */
    private void demoSql() throws SQLException, IOException, GeneralSecurityException {
        banner("6. SQL  (IDS00-J, MET00-J)");

        Connection db;
        try {
            db = DemoDb.open(registry.snapshots());
        } catch (SQLException e) {
            // SQLState 08001 = no driver can handle the URL, i.e. h2.jar is not on the classpath.
            if ("08001".equals(e.getSQLState())) {
                out("  (skipped: H2 database driver not found - put h2.jar on the classpath)");
                return;
            }
            throw e;
        }

        // ERR54-J: try-with-resources closes the connection (and drops the in-memory database).
        try (Connection conn = db) {
            StudentDao dao = new StudentDao(conn);
            out("  lookup \"Ada Lovelace\"  -> " + dao.findByName("Ada Lovelace"));
            out("  lookup \"Bob O'Neil\"    -> " + dao.findByName("Bob O'Neil")
                    + "   (the apostrophe is just data)");

            // This payload uses only letters, spaces and apostrophes, so it PASSES the
            // MET00-J name validation. Only the parameterized query stops it.
            String payload = "x' OR 'a' LIKE 'a";
            out("  attack payload \"" + payload + "\" passes name validation: "
                    + NAME_PATTERN.matcher(payload).matches());
            out("    NONCOMPLIANT concatenated query (for comparison) -> "
                    + lookupByConcatenation(conn, payload) + "   <- every student leaked");
            out("    COMPLIANT StudentDao (PreparedStatement)         -> "
                    + dao.findByName(payload) + "   <- no student has that literal name");

            expectRejected("over-long name (MET00-J length check)", () -> dao.findByName("A".repeat(500)));
        }
    }

    /**
     * NONCOMPLIANT with IDS00-J. Exists ONLY to show what the vault's real
     * lookup (StudentDao.findByName) prevents; nothing else in the program calls it.
     *
     * The name is pasted into the SQL text, so the quote characters in the
     * payload end the string early and the rest of the payload becomes SQL:
     *   WHERE name = 'x' OR 'a' LIKE 'a'
     * which is true for every row.
     *
     * @param conn open database connection
     * @param name the untrusted name
     * @return the rows the query returned, as "uid:name"
     * @throws SQLException if the query fails
     */
    private static List<String> lookupByConcatenation(Connection conn, String name) throws SQLException {
        String sql = "SELECT uid, name FROM students WHERE name = '" + name + "'";
        List<String> rows = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(rs.getInt("uid") + ":" + rs.getString("name"));
            }
        }
        return rows;
    }

    // ------------------------------------------------------ 7. files / reports
    /** Creates and reads a report using secure file helpers.
         * @throws IOException if file operations fail
     */
    private void demoFilesAndReports() throws IOException {
        banner("7. Files and reports  (FIO01/08/14-J, MET04-J)");
        Path report = workDir.toPath().resolve("report.csv");
        ReportFormatter formatter = new CsvReportFormatter();          // MET04-J: subclass keeps 'protected'
        SecureFiles.writePrivate(report, formatter.render(registry.snapshots()));   // FIO14-J inside
        out("report.csv permissions: " + SecureFiles.describe(report));

        // FIO01-J: permissions are only applied when a file is CREATED. Writing into a
        // file that already exists would keep its old (possibly open) permissions, so
        // the vault refuses to reuse one instead of silently overwriting it.
        try {
            SecureFiles.writePrivate(report, "second export");
            out("  [FAIL] an existing file was reused");
        } catch (FileAlreadyExistsException e) {
            out("  [ok]   refused to write into an existing file (its permissions are not ours to trust)");
        }

        String content;
        try (Reader r = Files.newBufferedReader(report, StandardCharsets.UTF_8)) {
            content = SecureFiles.readAll(r);                          // FIO08-J
        }
        out(content.trim());
    }

    // ------------------------------------------------- 8. external command
    /** Demonstrates validation of arguments passed to an external command.
     */
    private void demoCommandExecution() {
        banner("8. Runtime.exec()  (IDS07-J)");
        FileInspector inspector = new FileInspector(workDir.toPath());
        try {
            out("  size of report.csv via 'wc -c' = " + inspector.sizeOf("report.csv") + " bytes");
        } catch (IOException e) {
            audit.error("Inspector unavailable", e);
            out("  (skipped: external tool unavailable on this OS)");
        }
        for (String evil : new String[] {"report.csv; rm -rf /", "../../etc/passwd", "-rf", "a b"}) {
            try {
                inspector.sizeOf(evil);
                out("  [FAIL] accepted " + evil);
            } catch (IllegalArgumentException e) {
                out("  [ok]   rejected '" + evil + "' before any process was started");
            } catch (IOException e) {
                out("  [FAIL] unexpected IO failure");
            }
        }
    }

    // --------------------------------------------------------- 9. concurrency
    /** Demonstrates thread-safe updates to a shared counter.
         * @throws InterruptedException if waiting for a thread is interrupted
     */
    private void demoConcurrency() throws InterruptedException {
        banner("9. Shared primitives across threads  (VNA00-J)");
        AttemptCounter counter = new AttemptCounter();
        Thread[] threads = new Thread[8];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    counter.increment();
                }
            });
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();
        }
        out("8 threads x 1000 increments = " + counter.get() + " (expected 8000)");
    }

    // ------------------------------------------------------------ 10. log review
    /** Flushes and displays the audit log.
         * @throws IOException if the log cannot be read
     */
    private void demoAuditLog() throws IOException {
        banner("10. Audit log contents  (FIO13-J, ERR02-J)");
        audit.flush();
        try (Reader r = Files.newBufferedReader(workDir.toPath().resolve("audit.log"), StandardCharsets.UTF_8)) {
            out(SecureFiles.readAll(r).trim());
        }
        out("\nNote: no passwords, and only masked user names, appear above.");
    }

    // ---------------------------------------------------------------- cleanup
    /** Closes the audit log and removes temporary files.
         * @return zero if cleanup succeeded, otherwise one
     */
    private int cleanup() {
        banner("11. Cleanup  (FIO02-J, FIO14-J)");
        int status = 0;
        if (audit != null) {
            audit.close();                                             // FIO14-J: flush + release handlers
        }
        if (workDir != null) {
            // FIO02-J: java.io.File reports failure through return values, not exceptions,
            // so every result is checked. listFiles() returns null (EXP00-J) if the
            // directory cannot be read; delete() returns false if a file could not be removed.
            int removed = 0;
            File[] children = workDir.listFiles();
            if (children == null) {
                out("  could not list " + workDir.getName());
                status = 1;
            } else {
                for (File f : children) {
                    if (f.delete()) {
                        removed++;
                    } else {
                        out("  could not delete " + f.getName());
                        status = 1;
                    }
                }
            }
            if (workDir.delete()) {
                out("  removed " + removed + " files and the work directory; every delete() result was checked");
            } else {
                out("  could not delete the work directory " + workDir.getName());
                status = 1;
            }
        }
        return status;
    }

    // ---------------------------------------------------------------- helpers
    /** Runs an operation and reports whether invalid input was rejected.
         * @param label description of the attempted operation
         * @param attempt operation to execute
         * @throws IOException if an I/O operation fails
         * @throws GeneralSecurityException if a security operation fails
         * @throws SQLException if a database operation fails
     */
    private static void expectRejected(String label, Attempt attempt)
            throws IOException, GeneralSecurityException, SQLException {
        try {
            attempt.run();
            out("  [FAIL] " + label + " was ACCEPTED");
        } catch (IllegalArgumentException e) {
            out("  [ok]   " + label + " rejected: " + e.getMessage());
        }
    }

    /** Prints a heading for a demonstration section.
         * @param title heading text
     */
    private static void banner(String title) {
        out("\n=== " + title + " ===");
    }

    /** Prints a message to standard output.
         * @param s message to print
     */
    private static void out(String s) {
        System.out.println(s);
    }
}
