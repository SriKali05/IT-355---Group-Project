/**
 * File: MainClass.java
 * Srida Kalidindi
 * Class: IT 355 Group Project 01
 */
package rules.ids07j;
 
//working example code explaining rule IDS07-J
//IDS07-J: sanitize untrusted data passed to the Runtime.exec() method
 
import java.io.File;
import java.util.regex.Pattern;
 
/**
 * Example of IDS07-J: sanitize untrusted data passed to the
 * Runtime.exec() method. 
 */
public class MainClass {
 
    /** Simulates untrusted input: the attacker adds a second command using &amp;. */
    static final String UNTRUSTED_DIR = "dummy & echo INJECTED";
 
    /**
     * Builds the command that lists a directory, using cmd.exe on Windows
     * and /bin/sh on Mac/Linux.
     *
     * @param dir the directory to list
     * @return the command as an array of arguments
     */
    static String[] shellCommand(String dir) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return new String[] {"cmd.exe", "/C", "dir " + dir};
        }
        return new String[] {"/bin/sh", "-c", "ls " + dir};
    }
 
    /**
     * NONCOMPLIANT: untrusted data is concatenated into a shell command.
     *
     * @param dir the untrusted directory name
     * @throws Exception if the command cannot be run
     */
    static void noncompliant(String dir) throws Exception {
        Process proc = Runtime.getRuntime().exec(shellCommand(dir));
        proc.waitFor();
        String out = new String(proc.getInputStream().readAllBytes()).trim();
        System.out.println("[Noncompliant] Output: " + out);
    }
 
    /**
     * COMPLIANT: only whitelisted characters are allowed through.
     *
     * @param dir the untrusted directory name
     * @throws Exception if the command cannot be run
     */
    static void compliantSanitize(String dir) throws Exception {
        if (!Pattern.matches("[0-9A-Za-z@.]+", dir)) {
            System.out.println("[Compliant] Rejected invalid directory: " + dir);
            return;
        }
        Process proc = Runtime.getRuntime().exec(shellCommand(dir));
        proc.waitFor();
        System.out.println(new String(proc.getInputStream().readAllBytes()));
    }
 
    /**
     * COMPLIANT: avoids Runtime.exec() entirely.
     *
     * @param dir the untrusted directory name
     */
    static void compliantNoExec(String dir) {
        File f = new File(dir);
        if (!f.isDirectory()) {
            System.out.println("[Compliant] Not a directory: " + dir);
        } else {
            for (String name : f.list()) {
                System.out.println(name);
            }
        }
    }
 
    /**
     * Runs the demonstration.
     *
     * @param args not used
     * @throws Exception if a command cannot be run
     */
    public static void main(String[] args) throws Exception {
        noncompliant(UNTRUSTED_DIR);
        compliantSanitize(UNTRUSTED_DIR);
        compliantNoExec(UNTRUSTED_DIR);
    }
}
 