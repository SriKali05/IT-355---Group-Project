import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/* =====================================================================================
 *  FILE UTILITIES   (FIO01-J, FIO02-J, FIO08-J, FIO14-J)
 * ===================================================================================== */
final class SecureFiles {
    private static final Set<PosixFilePermission> OWNER_RW = PosixFilePermissions.fromString("rw-------");

    /** Prevents instantiation of this utility class.
     */
    private SecureFiles() { }

    /** FIO02-J: File.mkdir() reports failure by returning false, so the result must be checked. */
    /** Creates a new directory and restricts access to its owner.
         * @param dir directory to create
         * @throws IOException if creation or permission changes fail
     */
    static void createNewDirectory(File dir) throws IOException {
        if (!dir.mkdir()) {
            throw new IOException("could not create directory");
        }
        restrictToOwner(dir);
    }

    /** FIO01-J: permissions are supplied AT CREATION TIME (atomic), not changed afterwards. */
    /** Creates a file with owner-only permissions when supported.
         * @param path file path
         * @throws IOException if file creation or permission changes fail
     */
    static void createPrivateFile(Path path) throws IOException {
        try {
            Files.createFile(path, PosixFilePermissions.asFileAttribute(OWNER_RW));
        } catch (FileAlreadyExistsException e) {
            // Reusing an existing file is fine for append-style logs.
        } catch (UnsupportedOperationException e) {
            // Non-POSIX file system (e.g. Windows): best-effort fallback, every result checked (FIO02-J).
            File f = path.toFile();
            if (!f.createNewFile() && !f.isFile()) {
                throw new IOException("could not create file");
            }
            restrictToOwner(f);
        }
    }

    /** Writes binary data to a privately created file.
         * @param path destination file
         * @param data bytes to write
         * @throws IOException if writing fails
     */
    static void writePrivate(Path path, byte[] data) throws IOException {
        createPrivateFile(path);
        Files.write(path, data);
    }

    /** Writes text as UTF-8 to a privately created file.
         * @param path destination file
         * @param text text to write
         * @throws IOException if writing fails
     */
    static void writePrivate(Path path, String text) throws IOException {
        createPrivateFile(path);
        // FIO14-J: the writer is flushed and closed on every path, including exceptions.
        try (Writer w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            w.write(text);
        }
    }

    /** FIO08-J: keep read()'s result as an int and test for -1 BEFORE narrowing to char. */
    /** Reads all characters from a reader until end of stream.
         * @param reader source reader
         * @return complete text
         * @throws IOException if reading fails
     */
    static String readAll(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            sb.append((char) c);
        }
        return sb.toString();
    }

    /** Returns POSIX permission text, or n/a when unavailable.
         * @param p path to inspect
         * @return permission description
     */
    static String describe(Path p) {
        try {
            return PosixFilePermissions.toString(Files.getPosixFilePermissions(p));
        } catch (UnsupportedOperationException | IOException e) {
            return "n/a";                                              // display only
        }
    }

    /** Restricts access permissions to the file owner where supported.
         * @param f file or directory to protect
         * @throws IOException if a required permission change fails
     */
    private static void restrictToOwner(File f) throws IOException {
        if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
            // POSIX (Linux/macOS): set exact owner-only permissions in one call.
            String perms = f.isDirectory() ? "rwx------" : "rw-------";
            Files.setPosixFilePermissions(f.toPath(), PosixFilePermissions.fromString(perms));
            return;
        }
        // Non-POSIX (Windows): java.io.File cannot remove read/execute permission there, so
        // setReadable(false)/setExecutable(false) always return false. Only the owner-grant calls are
        // meaningful; the file keeps the ACL inherited from its parent (the per-user temp directory).
        boolean ok = f.setReadable(true, true) & f.setWritable(true, true);
        if (!ok) {
            throw new IOException("could not restrict permissions");
        }
    }
}
