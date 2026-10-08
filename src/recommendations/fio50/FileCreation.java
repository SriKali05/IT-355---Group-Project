package recommendations.fio50;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

/**
 * working example for FIO50-J
 *
 * FIO50-J: Do not make assumptions about file creation
 *
 * this example uses CREATE_NEW so the file is created only when
 * it does not already exist
 */
public class FileCreation {

    /**
     * demonstrates safe file creation
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
    	Scanner s = new Scanner(System.in);
    	System.out.print("Enter filename to create: ");
    	String filename = s.next();
    	s.close();
    	
        createFile(filename);
    }
    
    /**
     * safely creates a new file
     *
     * @param filename name of the file to create
     */
    public static void createFile(String filename) {
        Path path = Paths.get(filename);

        try (OutputStream output = Files.newOutputStream(
                path, StandardOpenOption.CREATE_NEW)) {

            output.write("New file created safely.".getBytes());

            System.out.println("File " + filename + " created safely.");

        } catch (FileAlreadyExistsException e) {
            System.out.println("File already exists.");

        } catch (IOException e) {
            System.out.println("File could not be created.");
        }
    }
}
