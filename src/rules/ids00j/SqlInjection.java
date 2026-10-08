package rules.ids00j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
/**
 * Demonstrates the difference between noncompliant and compliant code for IDS00-J ("Prevent SQL Injection").
 *  using an in-memory H2 database to show how an attack can succeed with noncompliant code and fail with compliant code.
 *  
 * The class has two login methods that both check a username and password in the "users" table, but they do it differently:
 * - loginNoncompliant() directly affs the username and password to the SQL query, which makes it vulnerable to SQL injection.
 * 
 * loginCompliant() uses a parameterized query, which prevents SQL injection and is the safer approach.
 * 
 * The main method tests both login methods using the same malicious input to show the difference between the unsafe and safe approaches.
 * 
 * To run this program input the following command in the terminal:
 * 1) javac -cp h2.jar -d /tmp/out src/rules/IDS00J.java
 * 2) java -cp /tmp/out:h2.jar src.rules.IDS00J
 * 
 * 
 */
public class SqlInjection {
    /**
     * Main method to demonstrate the difference between noncompliant and compliant login methods.
     * 
     * The program:
     * 
     * Creates an in-memory H2 database and adds a user named "alice" with password "secret123".
     * 
     * Tests the noncompliant login using a SQL injection and the wrong password. The attack succeeds because the login is vulnerable.
     * 
     * Tests the compliant login using the same SQL injection. This time, the input is treated as normal text, so the login fails.
     * 
     * Tests the compliant login with the correct username and password to show that normal login still works.
     * @param args
     * @throws Exception if there is a database error
     */
    public static void main(String[] args) throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:h2:mem:demo")) {

            // Set up a tiny database (plain-text password only to keep the demo simple)
            try (Statement s = c.createStatement()) {
                s.execute("CREATE TABLE users (username VARCHAR(50), password VARCHAR(50))");
                s.execute("INSERT INTO users VALUES ('alice', 'secret123')");
            }
            // Test the noncompliant and compliant login methods with the same malicious input
            String attackUser = "alice' OR '1'='1";
            String wrongPass = "wrong";

            // Show how the noncompliant login is vulnerable to SQL injection
            System.out.println("Noncompliant, attack input:");
            System.out.println("  Result: " + (loginNoncompliant(c, attackUser, wrongPass)
                    ? "LOGIN SUCCEEDED (attack worked!)" : "LOGIN REJECTED"));

            // Show how the compliant login is safe against SQL injection
            System.out.println("\nCompliant, attack input:");
            System.out.println("  Result: " + (loginCompliant(c, attackUser, wrongPass)
                    ? "LOGIN SUCCEEDED" : "LOGIN REJECTED (attack failed)"));

            // Show that the compliant login still works with the correct credentials
            System.out.println("\nCompliant, real credentials:");
            System.out.println("  Result: " + (loginCompliant(c, "alice", "secret123")
                    ? "LOGIN SUCCEEDED (legitimate user still works)" : "LOGIN REJECTED"));
        }
    }
	
    /** Maximum allowed length for username and password; matches the VARCHAR(50) columns. */
    private static final int MAX_INPUT_LENGTH = 50;
    /**
     * NONCOMPLIANT: login check that is vulnerable to SQL injection
     * 
     * The username and password are concatenated directly into the SQL string, so any SQL syntax inside 
     * them becomes part of the query itself. For example, if the username is "alice' OR '1'='1", changes the 
     * where condition to always be true. This allows the user to log in without knowing the correct password.
     * 
     * The final SQL query is printed to the console for demonstration purposes, so you can see how the attack works.
     * chan
     * 
     * @param c the database connection
     * @param user the username to check
     * @param pass the password to check
     * @return true if the user exists and the password matches, false otherwise
     * @throws Exception if there is a database error
     */
    static boolean loginNoncompliant(Connection c, String user, String pass) throws Exception {

        // The SQL query is built by concatenating the user and pass values directly into the string, which is unsafe
        String sql = "SELECT * FROM users WHERE username='" + user
                   + "' AND password='" + pass + "'";
        
                   // Print the query to show how the attack works
        System.out.println("  Query sent: " + sql);

        // Execute the query and check if any results are returned
        try (Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next();
        }
    }

    /**
     * COMPLIANT: login check that uses a parameterized query to prevent SQL injection
     * 
     * The input is validated to reject null or overly long values, which helps prevent some attacks.
     * 
     * The SQL query uses ? placeholders instead of directly adding the username and password to the query.
     * The username and password are added separatly using PreparedStatement, so the database treats them as data
     * instead of SQL code.
     * 
     * This prevents special characters or SQL code in the user's input from changing the query
     * 
     * @param c the database connection
     * @param user the username to check
     * @param pass the password to check
     * @return true if the user exists and the password matches, false otherwise
     * @throws Exception if there is a database error
     */
    static boolean loginCompliant(Connection c, String user, String pass) throws Exception {

        // Validate the input before using it: reject null or overly long values
        if (user == null || pass == null
                || user.length() > MAX_INPUT_LENGTH || pass.length() > MAX_INPUT_LENGTH) {
            return false;
        }

        // Use a parameterized query to prevent SQL injection
        String sql = "SELECT * FROM users WHERE username=? AND password=?";

        // The PreparedStatement safely adds the user and pass values to the query, so they cannot change the SQL logic
        try (PreparedStatement stmt = c.prepareStatement(sql)) {

            // Set the parameters for the query using the provided username and password
            stmt.setString(1, user);
            stmt.setString(2, pass);

            // Execute the query and check if any results are returned
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

}