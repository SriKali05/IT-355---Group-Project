/**
 * File: MainClass.java
 * Srida Kalidindi
 * Class: IT 355 Group Project 01
 */
package recommendations.fio52j;
 
//working example code explaining recommendation FIO52-J
//FIO52-J: do not store unencrypted sensitive information on the client side
 
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
 
/**
 * Example of FIO52-J: do not store unencrypted sensitive information
 * on the client side.
 */
public class MainClass {
 
    /** Simulates the server's memory. This is never sent to the client. */
    static final Map<String, String> serverTokens = new HashMap<>();
 
    /**
     * NONCOMPLIANT: the password itself is stored in the cookie on the client.
     *
     * @param username the user's name
     * @param password the user's password
     * @return the cookie string
     */
    static String noncompliantCookie(String username, String password) {
        return username + ";" + password;
    }
 
    /**
     * COMPLIANT: the cookie holds only a random token; the server remembers it.
     *
     * @param username the user's name
     * @return the cookie string
     */
    static String compliantCookie(String username) {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        serverTokens.put(username, token); // server keeps the username -> token mapping
        return username + ";" + token;
    }
 
    /**
     * Server-side check for the compliant cookie.
     *
     * @param cookie the cookie sent by the client
     * @return true if the token matches the one the server stored
     */
    static boolean serverAccepts(String cookie) {
        String[] v = cookie.split(";");
        return v.length == 2 && v[1].equals(serverTokens.get(v[0]));
    }
 
    /**
     * Runs the demonstration.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        // Noncompliant: an attacker who reads the client's cookie gets the password
        String bad = noncompliantCookie("alice", "MyPassword123");
        System.out.println("Noncompliant cookie on client: " + bad);
        System.out.println("Attacker reads password: " + bad.split(";")[1]);
 
        System.out.println();
 
        // Compliant: an attacker who reads the cookie gets only a random token
        String good = compliantCookie("alice");
        System.out.println("Compliant cookie on client:    " + good);
        System.out.println("Server accepts real cookie:    " + serverAccepts(good));
        System.out.println("Server accepts forged cookie:  " + serverAccepts("alice;guess"));
    }
}