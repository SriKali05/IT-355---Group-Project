package reccomendations.fio52j;

//working example code explaining recommendation FIO52-J
//FIO52-J: do not store unencrypted sensitive information on the client side

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class MainClass {

    // Simulates the SERVER's memory. This is never sent to the client.
    static final Map<String, String> serverTokens = new HashMap<>();

    // NONCOMPLIANT: the password itself is stored in the cookie on the client
    static String noncompliantCookie(String username, String password) {
        return username + ";" + password;
    }

    // COMPLIANT: the cookie holds only a random token; the server remembers it
    static String compliantCookie(String username) {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        serverTokens.put(username, token); // server keeps the username -> token mapping
        return username + ";" + token;
    }

    // Server-side check for the compliant cookie
    static boolean serverAccepts(String cookie) {
        String[] v = cookie.split(";");
        return v.length == 2 && v[1].equals(serverTokens.get(v[0]));
    }

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