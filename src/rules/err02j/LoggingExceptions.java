/*
 * File: LoggingExceptions.java
 * Srida Kalidindi
 * Class: IT 355 Group Project 01
 */
package rules.err02j;

//working example code explaining rule ERR02-J
//ERR02-J: prevent exceptions while logging data

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Example of ERR02-J: prevent exceptions while logging data.
 */
public class LoggingExceptions {
	
    /**
     * Runs the demonstration.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        noncompliant();
        System.out.println();
        compliant();
    }

    /** Logger used by the compliant example. */
    private static final Logger logger =
            Logger.getLogger(LoggingExceptions.class.getName());

    /**
     * NONCOMPLIANT: writes the exception to System.err.
     */
    static void noncompliant() {
        try {
            System.out.println("[Noncompliant] Attempting security-sensitive action...");
            throw new SecurityException("Access denied for user: attacker");
        } catch (SecurityException se) {
            System.err.println(se);
            System.out.println("[Noncompliant] Recovered (but logging was not reliable)");
        }
    }

    /**
     * COMPLIANT: uses java.util.logging.Logger to record the exception.
     */
    static void compliant() {
        try {
            System.out.println("[Compliant] Attempting security-sensitive action...");
            throw new SecurityException("Access denied for user: attacker");
        } catch (SecurityException se) {
            logger.log(Level.SEVERE, "Security exception caught", se);
            System.out.println("[Compliant] Recovered (logged via Logger)");
        }
    }
}
