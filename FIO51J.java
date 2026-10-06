import java.io.File;

/**
 * working example for FIO51-J
 *
 * FIO51-J: Identify files using multiple file attributes
 *
 * this example checks the file name, file size, and last
 * modified time before using the file
 */
public class FIO51J {

    /**
     * checks whether a file matches the expected attributes
     *
     * @param file file to check
     * @param expectedName expected file name
     * @param expectedSize expected file size
     * @param expectedModified expected last modified time
     * @return true if all expected attributes match
     */
    public static boolean isExpectedFile(
            File file,
            String expectedName,
            long expectedSize,
            long expectedModified) {

        return file.exists()
                && file.isFile()
                && file.getName().equals(expectedName)
                && file.length() == expectedSize
                && file.lastModified() == expectedModified;
    }

    /**
     * demonstrates checking multiple file attributes
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        File file = new File("data.txt");

        String expectedName = "data.txt";
        long expectedSize = file.length();
        long expectedModified = file.lastModified();

        if (isExpectedFile(
                file,
                expectedName,
                expectedSize,
                expectedModified)) {

            System.out.println("File matches expected attributes.");
        } else {
            System.out.println("File does not match expected attributes.");
        }
    }
}
