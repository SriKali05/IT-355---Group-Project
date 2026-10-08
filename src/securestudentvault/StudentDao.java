import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * IDS00-J: provides database access using a parameterized SQL query
 * so user input is not directly placed into the SQL statement.
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
        // Validate: allowed characters AND maximum length (pattern allows at most 50 chars).
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
