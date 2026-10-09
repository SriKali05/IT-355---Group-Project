/*
 * Package: securestudentvault
 * File: DemoDb.java
 * Rules covered: supports the IDS00-J demonstration
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package securestudentvault;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Creates the vault's demo database: a real, in-memory H2 database holding
 * one row per registered student.
 *
 * A real SQL engine is used (instead of a fake JDBC driver) so the IDS00-J
 * demonstration is genuine: an injection payload actually changes the result
 * of a concatenated query, and is actually treated as plain data by a
 * PreparedStatement.
 *
 * Requires h2.jar on the classpath (it is in the repository root and on the
 * Eclipse build path). The database exists only in memory and disappears
 * when its connection is closed.
 *
 * Only to be used in a static context, cannot be instantiated as an object.
 */
final class DemoDb {

    /** Private constructor: this is a utility class. */
    private DemoDb() {
    }

    /**
     * Opens a new in-memory database and loads the given students into a
     * {@code students(uid, name)} table.
     *
     * @param students the students to store
     * @return an open connection; the caller must close it (ERR54-J: use try-with-resources)
     * @throws SQLException if the H2 driver is missing or the table cannot be created
     */
    static Connection open(List<Student> students) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:vault");
        try {
            try (Statement st = conn.createStatement()) {
                st.execute("CREATE TABLE students (uid INT PRIMARY KEY, name VARCHAR(50) NOT NULL)");
            }
            // Even our own seed data goes through placeholders (IDS00-J):
            // names such as "Bob O'Neil" contain a quote character.
            try (PreparedStatement insert =
                         conn.prepareStatement("INSERT INTO students (uid, name) VALUES (?, ?)")) {
                for (Student s : students) {
                    insert.setInt(1, s.getUid());
                    insert.setString(2, s.getName().value());
                    insert.executeUpdate();
                }
            }
            return conn;
        } catch (SQLException e) {
            conn.close(); // do not leak the connection if setup fails part-way
            throw e;
        }
    }
}
