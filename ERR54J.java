import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Demonstrates ERR54-J by safely managing a reader and writer
 * with a try-with-resources statement.
 */
public class ERR54 {

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

    /**
     * Runs the resource-management example.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Path input = Path.of("input.txt");
        Path output = Path.of("output.txt");

        try {
            copyFile(input, output);
            System.out.println("File copied successfully.");
        } catch (IOException e) {
            System.out.println("File operation failed: " + e.getMessage());
        }
    }
}