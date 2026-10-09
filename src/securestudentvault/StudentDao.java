package securestudentvault;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * IDS00-J (prevent SQL injection): looks students up with a parameterized
 * query, so user input is never placed into the SQL text.
 *
 * Input validation (MET00-J) is applied too, but it is NOT what stops SQL
 * injection here. Student names may legitimately contain apostrophes
 * ("Bob O'Neil"), so the name pattern has to allow the quote character, and
 * a payload such as  x' OR 'a' LIKE 'a  passes validation. The
 * PreparedStatement is what makes that payload harmless: the driver sends it
 * to the database as a single string value, so it can only ever be compared
 * with the name column, never executed as SQL.
 */
final class StudentDao {

    // Placeholder "?" keeps user data out of the SQL text entirely.
    private static final String FIND_BY_NAME =
            "SELECT uid, name FROM students WHERE name = ?";

    private final Connection conn;

    /**
     * creates a database access object with the given connection
     *
     * @param conn the database connection
     */
    StudentDao(Connection conn) {
        if (conn == null) {
            throw new IllegalArgumentException("connection required");
        }
        this.conn = conn;
    }

    /**
     * finds students by name after validating the input
     *
     * @param name the student name to search for
     * @return matching student records
     * @throws SQLException if the database operation fails
     */
    List<String> findByName(String name) throws SQLException {
        // MET00-J: validate allowed characters AND maximum length (the pattern
        // allows at most 50 chars, matching the VARCHAR(50) column). This is
        // defense in depth; the placeholder below is the actual IDS00-J defense.
        Check.matches(name, SecureStudentVault.NAME_PATTERN, "name");

        List<String> rows = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(FIND_BY_NAME)) {
            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(rs.getInt("uid") + ":" + rs.getString("name"));
                }
            }
        }

        return rows;
    }
}
