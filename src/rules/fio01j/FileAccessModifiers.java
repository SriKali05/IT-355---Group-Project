package rules.fio01j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * Demonstrates FIO01-J by creating a file with restrictive
 * access permissions on a POSIX-compatible file system.
 */
public class FileAccessModifiers {
	
    /**
     * Runs the secure file creation example.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Path file = Paths.get("privateData.txt");

        try {
            createPrivateFile(file);

            System.out.println(
                "Private file created with owner-only permissions."
            );
        } catch (UnsupportedOperationException e) {
            System.out.println(
                "This file system does not support POSIX permissions."
            );
        } catch (IOException e) {
            System.out.println(
                "Unable to create file: " + e.getMessage()
            );
        }
    }

    /**
     * Creates a private file that can be read and written only by its owner.
     *
     * @param filePath location of the file to create
     * @throws IOException if the file cannot be created or written
     */
    public static void createPrivateFile(Path filePath)
            throws IOException {

        Set<PosixFilePermission> permissions =
            PosixFilePermissions.fromString("rw-------");

        Files.createFile(
            filePath,
            PosixFilePermissions.asFileAttribute(permissions)
        );

        Files.write(
            filePath,
            "Private account information".getBytes(StandardCharsets.UTF_8)
        );
    }

}