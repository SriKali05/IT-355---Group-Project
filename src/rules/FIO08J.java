package src.rules;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/** 
 * Demonstrates CERT rule FIO08-J: "Distinguish between characters or bytes from a stream and -1" 
 * 
 * The read() method returns an int. A value of -1 means the end of the 
 * stream has been reached. The value should be compared with -1 before 
 * converting it to a byte or char. 
 * 
 * The noncompliant methods convert the value before checking for -1,
 * which can cause the program to miss the end of the stream. 
 * 
 * The compliant methods check for -1 first and then convert the value. 
 */
public class FIO08J {

    /** 
     * NONCOMPLIANT: Reads bytes from an input stream. 
     * 
     * The value returned by read() is converted to a byte before checking
     * for -1. This is unsafe because the value -1 can become a valid byte 
     * value after the conversion, causing the program to miss the end of
     * the stream. 
     * 
     * @param in the input stream to read from 
     * @return the number of bytes read 
     * @throws IOException if an input error occurs 
     */
    static int readBytesNoncompliant(InputStream in) throws IOException {
        int count = 0;
        byte data;
        // Convert to byte before checking for the end of the stream
        while ((data = (byte) in.read()) != -1) {
            System.out.printf("  read byte: 0x%02X%n", data);
            count++;
        }
        return count;
    }

    /**
     * COMPLIANT: Reads bytes from an input stream. 
     * 
     * The value returned by read() is stored as an int and checked for -1 
     * before it is converted to a byte. This correctly detects the end of 
     * the stream. 
     * 
     * @param in the input stream to read from
     * @return the number of bytes read
     * @throws IOException if an input error occurs 
     */
    static int readBytesCompliant(InputStream in) throws IOException {
        int count = 0;
        int inbuff;
        byte data;
        
        // Check for -1 before converting the value to a byte
        while ((inbuff = in.read()) != -1) {
            data = (byte) inbuff;
            System.out.printf("  read byte: 0x%02X%n", data);
            count++;
        }
        return count;
    }

    /**
     * NONCOMPLIANT: Reads characters from a reader. 
     * 
     * The value returned by read() is converted to a char before checking 
     * for -1. Since a char cannot store -1, the value becomes 0xFFFF instead.
     * This means the program does not correctly detect the end of the stream. 
     * 
     * @param in the reader to read from
     * @return the number of characters read
     * @throws IOException if an input error occurs */

    static int readCharsNoncompliant(Reader in) throws IOException {
        int count = 0;
        char data;
        // Convert to char before checking for the end of the stream
        while ((data = (char) in.read()) != -1) {
            System.out.printf("  read char: 0x%04X%n", (int) data);
            count++;
            
            // Stop the loop so the demonstration does not run forever
            if (count >= 6) { 
                System.out.println("  ... never sees end of stream, bailing out");
                break;
            }
        }
        return count;
    }

    /** 
     * COMPLIANT: Reads characters from a reader. 
     * 
     * The value returned by read() is stored as an int and checked for -1 
     * before it is converted to a char. This correctly detects the end of 
     * the stream. 
     * 
     * @param in the reader to read from 
     * @return the number of characters read
     * @throws IOException if an input error occurs 
     */
    static int readCharsCompliant(Reader in) throws IOException {
        int count = 0;
        int inbuff;
        char data;

        // Check for -1 before converting the value to a char
        while ((inbuff = in.read()) != -1) {
            data = (char) inbuff;
            System.out.printf("  read char: 0x%04X%n", (int) data);
            count++;
        }
        return count;
    }

    /** 
    * Runs the demonstration using both the noncompliant and compliant 
    * methods. 
    * 
    * The byte examples show what happens when a byte value of 0xFF is 
    * read. The character examples show what happens when the value 0xFFFF 
    * is read. 
    * 
    * The compliant methods correctly detect the end of the stream, 
    * while the noncompliant character method does not. 
    * 
    * @param args 
    * @throws IOException if an input error occurs 
    */
    public static void main(String[] args) throws IOException {
        byte[] bytes = { 0x41, (byte) 0xFF, 0x42 };   // 'A', 0xFF, 'B'
        char[] chars = { 'A', '\uFFFF', 'B' };        // 'A', 0xFFFF, 'B'

        System.out.println("Byte, NONCOMPLIANT (expected 3 bytes):");
        int n = readBytesNoncompliant(new ByteArrayInputStream(bytes));
        System.out.println("  total = " + n + "\n");

        System.out.println("Byte, COMPLIANT (expected 3 bytes):");
        n = readBytesCompliant(new ByteArrayInputStream(bytes));
        System.out.println("  total = " + n + "\n");

        System.out.println("Char, NONCOMPLIANT (expected 3 chars):");
        n = readCharsNoncompliant(new CharArrayReader(chars));
        System.out.println("  total = " + n + "\n");

        System.out.println("Char, COMPLIANT (expected 3 chars):");
        n = readCharsCompliant(new CharArrayReader(chars));
        System.out.println("  total = " + n);
    }
}