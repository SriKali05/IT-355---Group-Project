package recommendations.err54j;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Demonstrates ERR54-J by safely managing a reader and writer
 * with a try-with-resources statement.
 */
public class CloseableResources {
    
    /**
     * Runs the resource-management example.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
    	Scanner s = new Scanner(System.in);
    	System.out.print("Enter input file to copy: ");
    	String inFileName = s.nextLine().trim();
    	s.close();
    	
        try {
            Path input = Path.of(inFileName);
        	Path filename = input.getFileName();
        	Path output = input.resolveSibling("copy-" + filename);
        	
            copyFile(input, output);
            System.out.println("File copied successfully to " + output);
        }
        catch (IOException e) {
            System.out.println("File operation failed: " + e.getMessage());
        }
    }
    
    /**
     * Copies text from one file to another.
     * Both resources are closed automatically.
     *
     * @param input the file to read
     * @param output the file to write
     * @throws IOException if a file operation fails
     */
    public static void copyFile(Path input, Path output)
            throws IOException {

        try (BufferedReader reader = Files.newBufferedReader(input);
             BufferedWriter writer = Files.newBufferedWriter(output)) {

            String line;

            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}