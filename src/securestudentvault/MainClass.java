package src.securestudentvault;

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
 * Run with:  java SecureStudentVault.java      (JDK 17+; no external libraries)
 * The JDBC part uses an in-memory stub Connection so no database driver is needed.
 */

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/* =====================================================================================
 *  MAIN CLASS / DEMO DRIVER
 * ===================================================================================== */
public final class MainClass {

    // OBJ10-J: the only public static fields are final, and of immutable types.
    public static final String APP_NAME = "Secure Student Vault";
    public static final int MIN_UID = 1000;
    public static final int MAX_UID = 9999;

    // Package-private shared validation patterns (final; Pattern is immutable).
    static final Pattern NAME_PATTERN = Pattern.compile("[A-Za-z][A-Za-z '\\-]{0,49}");
    static final Pattern USER_PATTERN = Pattern.compile("[a-z][a-z0-9_]{2,19}");

    // OBJ01-J: every instance field is private.
    private final StudentRegistry registry = new StudentRegistry();
    private File workDir;
    private AuditLog audit;
    private Session teacher;
    private Session student;

    public static void main(String[] args) {
        int status = new SecureStudentVault().runDemo();
        // FIO14-J: runDemo() has already closed/flushed everything in a finally block,
        // so exiting here cannot lose buffered data or leave resources open.
        System.exit(status);
    }

    private interface Attempt {
        void run() throws IOException, GeneralSecurityException, SQLException;
    }

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
            if (audit != null) {
                audit.error("Demo aborted", e);      // ERR02-J: logging API, not printStackTrace()
            }
            System.out.println("Demo aborted: " + e.getClass().getSimpleName());
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
    private void setup() throws IOException {
        banner("1. Setup  (FIO01-J, FIO02-J, ERR02-J)");
        workDir = new File(System.getProperty("java.io.tmpdir"), "vault-" + System.nanoTime());
        SecureFiles.createNewDirectory(workDir);                       // FIO02-J: mkdir() result checked
        audit = new AuditLog(workDir.toPath().resolve("audit.log"));   // FIO01-J: private log file
        audit.info(APP_NAME + " started");
        out("Work directory created, owner-only. Audit log permissions: "
                + SecureFiles.describe(workDir.toPath().resolve("audit.log")));
    }

    // ----------------------------------------------------- 2. authentication
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
    private void demoDomainObjects() throws IOException, GeneralSecurityException, SQLException {
        banner("3. Domain objects  (OBJ01/05/08/10/11/13-J, MET00-J, EXP02-J, MET55-J)");

        Student ada = new Student("Ada Lovelace", 1001);
        ada.addGrade(95);
        ada.addGrade(88);
        Student bob = new Student("Bob O'Neil", 1002);
        if (!registry.register(ada) || !registry.register(bob)) {      // EXP00-J: result checked
            throw new IllegalStateException("registration failed");
        }
        boolean dup = registry.register(new Student("Ada Clone", 1001));
        out("Duplicate uid registration accepted? " + dup);

        // OBJ11-J: constructor failure leaves no usable object (Student is final).
        expectRejected("uid out of range (MET00-J + OBJ11-J)", () -> new Student("Bad Uid", 5));
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
    }

    // ------------------------------------------------------------ 4. web form
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
    private void demoSql() throws SQLException, IOException, GeneralSecurityException {
        banner("6. SQL  (IDS00-J, MET00-J)");
        List<String> trace = new ArrayList<>();
        try (Connection conn = DemoDb.connection(trace)) {
            StudentDao dao = new StudentDao(conn);
            out("  lookup 'Ada Lovelace' -> " + dao.findByName("Ada Lovelace"));
            expectRejected("injection payload  x' OR '1'='1", () -> dao.findByName("x' OR '1'='1"));
            expectRejected("over-long name (length check)", () -> dao.findByName("A".repeat(500)));
        }
        for (String line : trace) {
            out("    stub-db> " + line);
        }
    }

    // ------------------------------------------------------ 7. files / reports
    private void demoFilesAndReports() throws IOException {
        banner("7. Files and reports  (FIO01/02/08/14-J, MET04-J, EXP00-J)");
        Path report = workDir.toPath().resolve("report.csv");
        ReportFormatter formatter = new CsvReportFormatter();          // MET04-J: subclass keeps 'protected'
        SecureFiles.writePrivate(report, formatter.render(registry.snapshots()));   // FIO14-J inside
        out("report.csv permissions: " + SecureFiles.describe(report));

        String content;
        try (Reader r = Files.newBufferedReader(report, StandardCharsets.UTF_8)) {
            content = SecureFiles.readAll(r);                          // FIO08-J
        }
        out(content.trim());
    }

    // ------------------------------------------------- 8. external command
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
    private void demoAuditLog() throws IOException {
        banner("10. Audit log contents  (FIO13-J, ERR02-J)");
        audit.flush();
        try (Reader r = Files.newBufferedReader(workDir.toPath().resolve("audit.log"), StandardCharsets.UTF_8)) {
            out(SecureFiles.readAll(r).trim());
        }
        out("\nNote: no passwords, and only masked user names, appear above.");
    }

    // ---------------------------------------------------------------- cleanup
    private int cleanup() {
        int status = 0;
        if (audit != null) {
            audit.close();                                             // FIO14-J: flush + release handlers
        }
        if (workDir != null) {
            File[] children = workDir.listFiles();                     // FIO02-J / EXP00-J: may be null
            if (children == null) {
                status = 1;
            } else {
                for (File f : children) {
                    if (!f.delete()) {                                 // FIO02-J: delete() result checked
                        status = 1;
                    }
                }
            }
            if (!workDir.delete()) {
                status = 1;
            }
        }
        return status;
    }

    // ---------------------------------------------------------------- helpers
    private static void expectRejected(String label, Attempt attempt)
            throws IOException, GeneralSecurityException, SQLException {
        try {
            attempt.run();
            out("  [FAIL] " + label + " was ACCEPTED");
        } catch (IllegalArgumentException e) {
            out("  [ok]   " + label + " rejected: " + e.getMessage());
        }
    }

    private static void banner(String title) {
        out("\n=== " + title + " ===");
    }

    private static void out(String s) {
        System.out.println(s);
    }
}
