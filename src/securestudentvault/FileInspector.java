/*
 * Package: securestudentvault
 * File: FileInspector.java
 * Rules covered: IDS07-J, EXP00-J, ERR08-J
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package src.securestudentvault;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.regex.Pattern;
 
/**
 * Provides methods for validating a file's safety
 */
public class FileInspector {
    // Pattern for allowed file name format: must start with a letter/digit (blocks "-option" and ".."), no separators, no spaces
    // And limits the file to 64 characters
    private static final Pattern SAFE_FILE = Pattern.compile("[A-Za-z0-9][A-Za-z0-9_.\\-]{0,63}");

    private final Path baseDir;

    /**
     * Constructor takes a base directory to make a FileInspector object 
     * 
     * @param baseDir Path
     */
    public FileInspector(Path baseDir) {
        // ERR08-J: Do not catch NullPointerException or any of its ancestors
        // Instead, we explicitly check for null value before trying to use the parameter
        if (baseDir == null) {
            throw new IllegalArgumentException("base directory required");
        }
        this.baseDir = baseDir;
    }

    /**
     * Gets size of file in bytes by calling wc using exec()
     * 
     * @return long representing the file size in bytes
     */
    long sizeOf(String fileName) throws IOException {
        
        // IDS07-J: Sanitize untrusted data passed to the Runtime.exec() method
        // Before .exec() is called, check the file name against the valid format (matches throws exceptions as needed)
        Check.matches(fileName, SAFE_FILE, "file name");               
        Path target = baseDir.resolve(fileName);

        // Argument array: no shell is involved, so no metacharacter can start a second command
        Process p = Runtime.getRuntime().exec(new String[] {"wc", "-c", target.toString()});
        String output;


        // Try to read output of wc command
        try (Reader r = new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8)) {
            output = SecureFiles.readAll(r);
        }
        // Try to wait for exec process to finish
        try{
            // EXP00-J: Do not ignore values returned by methods
            // The exit status of p.waitfor() is checked to see if it worked properly
            int exit = p.waitFor();
            if (exit != 0){
                throw new IOException("inspection command failed");
            }
        }
        // catch block for being inerrupted while waiting
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new IOException("interrupted while waiting for command");
        }

        // Parse and return the file size in bytes as a long from the output of wc
        try{
            return Long.parseLong(output.trim().split("\\s+")[0]);
        }
        // catch block for failure to parse the long
        catch (NumberFormatException e) {
            throw new IOException("unexpected command output");
        }
    }
}
