/*
 * File: ProgramCleanup.java
 * Srida Kalidindi
 * Class: IT 355 Group Project 01
 */
package rules.fio14j;

//working example code explaining rule FIO14-J
//FIO14-J: perform proper cleanup at program termination

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Example of FIO14-J: perform proper cleanup at program termination.
 */
public class ProgramCleanup {

    /**
     * Writes to a buffered file stream, then closes it before exiting so the
     * buffer is flushed. See the comments at the bottom for the noncompliant
     * version.
     *
     * @param args not used
     * @throws Exception if the file cannot be written
     */
    public static void main(String[] args) throws Exception {
        PrintStream out = new PrintStream(
                new BufferedOutputStream(new FileOutputStream("foo.txt")));
        out.println("hello");
        System.out.println("Wrote \"hello\" to a buffered stream");

        //COMPLIANT: close() flushes the buffer before exiting
        out.close();
        System.out.println("Closed the file, then exiting...");
        Runtime.getRuntime().exit(1);

        //NONCOMPLIANT: to see the problem, delete the three lines above
        //this version does not flush the buffer like the one above
        System.out.println("Exiting WITHOUT closing the file...");
        Runtime.getRuntime().exit(1);
    }
}