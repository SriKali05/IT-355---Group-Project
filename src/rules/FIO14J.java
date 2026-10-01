package src.rules;

//working example code explaining rule FIO14-J
//FIO14-J: perform proper cleanup at program termination

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class FIO14J {

    public static void main(String[] args) throws Exception {
        PrintStream out = new PrintStream(
                new BufferedOutputStream(new FileOutputStream("foo.txt")));
        out.println("hello");
        System.out.println("Wrote \"hello\" to a buffered stream");

        //COMPLIANT: close() flushes the buffer before exiting
        out.close();
        System.out.println("Closed the file, then exiting...");
        Runtime.getRuntime().exit(1);

        //NONCOMPLIANT: to see the problem, delete the two lines above
        //this version does not flush the buffer like the one above
        //and use this instead (exit without closing):
        //System.out.println("Exiting WITHOUT closing the file...");
        //Runtime.getRuntime().exit(1);
    }
}
