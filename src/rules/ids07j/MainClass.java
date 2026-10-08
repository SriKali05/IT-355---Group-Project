package rules.ids07j;

//working example code explaining rule IDS07-J
//IDS07-J: sanitize untrusted data passed to the Runtime.exec() method

import java.io.File;
import java.util.regex.Pattern;

public class MainClass {

    //this is a working example for windows.

    //Simulates untrusted input: the attacker adds a second command using &
    static final String UNTRUSTED_DIR = "dummy & echo INJECTED";

    //NONCOMPLIANT: untrusted data is concatenated into a cmd.exe command
    static void noncompliant(String dir) throws Exception {
        Process proc = Runtime.getRuntime().exec(new String[] {"cmd.exe /C dir " + dir});
        proc.waitFor();
        String out = new String(proc.getInputStream().readAllBytes()).trim();
        System.out.println("[Noncompliant] Output: " + out);
    }

    //COMPLIANT: only whitelisted characters are allowed through
    static void compliantSanitize(String dir) throws Exception {
        if (!Pattern.matches("[0-9A-Za-z@.]+", dir)) {
            System.out.println("[Compliant] Rejected invalid directory: " + dir);
            return;
        }
        Process proc = Runtime.getRuntime().exec(new String[] {"cmd.exe /C dir " + dir});
        proc.waitFor();
        System.out.println(new String(proc.getInputStream().readAllBytes()));
    }

    //COMPLIANT: avoid Runtime.exec() entirely
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

    public static void main(String[] args) throws Exception {
        noncompliant(UNTRUSTED_DIR);
        compliantSanitize(UNTRUSTED_DIR);
        compliantNoExec(UNTRUSTED_DIR);
    }
}