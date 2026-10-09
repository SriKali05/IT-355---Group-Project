package securestudentvault;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.util.EnumSet;
import java.util.List;

/* =====================================================================================
 *  FILE UTILITIES   (FIO01-J, FIO08-J, FIO14-J)
 * ===================================================================================== */
/**
 * Creates, writes and reads the vault's private files.
 *
 * FIO01-J (create files with appropriate access permissions):
 * every file and directory is created WITH owner-only permissions in the same
 * call that creates it. Permissions are never loosened-then-tightened, so there
 * is no moment when another user could open the file.
 *
 * Two permission models are supported, chosen by asking the file system
 * that will actually hold the file:
 *   - POSIX (Linux, macOS): rw------- for files, rwx------ for directories
 *   - ACL   (Windows NTFS): a single "allow" entry for the current user, with
 *                           no entries inherited from the parent folder
 * If neither is available, creation is refused rather than silently creating
 * an unprotected file.
 */
final class SecureFiles {

    /** Prevents instantiation of this utility class. */
    private SecureFiles() { }

    /**
     * Creates a new, uniquely named directory under the system temp folder that
     * only the current user can access.
     *
     * FIO01-J: the owner-only permissions are passed to createTempDirectory, so
     * the directory never exists without them. createTempDirectory also picks
     * an unpredictable name and fails instead of reusing an existing directory,
     * which matters because the temp folder is shared with other users.
     *
     * @param prefix the start of the directory name
     * @return the new directory
     * @throws IOException if the directory cannot be created with restricted permissions
     */
    static Path createPrivateTempDirectory(String prefix) throws IOException {
        Path tempRoot = Path.of(System.getProperty("java.io.tmpdir"));
        return Files.createTempDirectory(tempRoot, prefix, ownerOnly(tempRoot, true));
    }

    /**
     * Creates a NEW file that only the current user can access.
     *
     * FIO01-J: the permissions are applied atomically at creation time.
     * If the file already exists, Files.createFile throws
     * FileAlreadyExistsException and the caller must not write to it: an
     * existing file keeps whatever permissions it was created with, which
     * may let other users read it.
     *
     * @param path the file to create
     * @throws java.nio.file.FileAlreadyExistsException if the file already exists
     * @throws IOException if the file cannot be created with restricted permissions
     */
    static void createPrivateFile(Path path) throws IOException {
        Path parent = path.toAbsolutePath().getParent();
        Files.createFile(path, ownerOnly(parent, false));
    }

    /**
     * Writes binary data to a newly created private file.
     *
     * @param path destination file (must not already exist)
     * @param data bytes to write
     * @throws IOException if the file exists or cannot be written
     */
    static void writePrivate(Path path, byte[] data) throws IOException {
        createPrivateFile(path);
        Files.write(path, data);
    }

    /**
     * Writes UTF-8 text to a newly created private file.
     *
     * @param path destination file (must not already exist)
     * @param text text to write
     * @throws IOException if the file exists or cannot be written
     */
    static void writePrivate(Path path, String text) throws IOException {
        createPrivateFile(path);
        // FIO14-J: try-with-resources flushes and closes the writer on every
        // path, including exceptions, so no buffered text can be lost.
        try (Writer w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            w.write(text);
        }
    }

    /**
     * Reads all characters from a reader until end of stream.
     *
     * FIO08-J: read() returns an int that is -1 at the end of the stream.
     * The int is compared with -1 BEFORE it is narrowed to a char; a char can
     * never hold -1, so narrowing first would make the loop never end.
     *
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

    /**
     * Describes who can access a file, for display in the demo output.
     * On POSIX systems this is the permission string (e.g. rw-------).
     * On ACL systems it reports whether the owner is the only user listed.
     *
     * @param p path to inspect
     * @return a short description of the file's access permissions
     */
    static String describe(Path p) {
        try {
            if (Files.getFileStore(p).supportsFileAttributeView(PosixFileAttributeView.class)) {
                return PosixFilePermissions.toString(Files.getPosixFilePermissions(p));
            }
            AclFileAttributeView view = Files.getFileAttributeView(p, AclFileAttributeView.class);
            if (view != null) {
                List<AclEntry> acl = view.getAcl();
                UserPrincipal owner = view.getOwner();
                boolean ownerOnly = !acl.isEmpty()
                        && acl.stream().allMatch(e -> e.principal().equals(owner));
                return ownerOnly
                        ? "ACL: owner only (" + acl.size() + " entry)"
                        : "ACL: " + acl.size() + " entries, NOT owner-only";
            }
        } catch (IOException e) {
            // display only: fall through to "n/a"
        }
        return "n/a";
    }

    /**
     * Builds the "owner only" file attribute for the file system that holds
     * {@code dir}.
     *
     * @param dir the directory the new file or directory will be created in
     * @param forDirectory true when creating a directory
     * @return a file attribute to pass to Files.createFile / createTempDirectory
     * @throws IOException if the file system supports neither POSIX permissions nor ACLs
     */
    private static FileAttribute<?> ownerOnly(Path dir, boolean forDirectory) throws IOException {
        FileStore store = Files.getFileStore(dir);

        if (store.supportsFileAttributeView(PosixFileAttributeView.class)) {
            // Directories need the execute bit so the owner can open them.
            return PosixFilePermissions.asFileAttribute(
                    PosixFilePermissions.fromString(forDirectory ? "rwx------" : "rw-------"));
        }

        if (store.supportsFileAttributeView(AclFileAttributeView.class)) {
            UserPrincipal currentUser = dir.getFileSystem().getUserPrincipalLookupService()
                    .lookupPrincipalByName(System.getProperty("user.name"));

            // One ALLOW entry granting the current user full control. Because
            // this ACL is supplied explicitly, nothing is inherited from the
            // parent folder, so no other user or group appears in it.
            AclEntry.Builder entry = AclEntry.newBuilder()
                    .setType(AclEntryType.ALLOW)
                    .setPrincipal(currentUser)
                    .setPermissions(EnumSet.allOf(AclEntryPermission.class));
            if (forDirectory) {
                // Files that other code creates inside the directory (such as
                // the audit log's lock file) inherit this owner-only entry.
                entry.setFlags(AclEntryFlag.FILE_INHERIT, AclEntryFlag.DIRECTORY_INHERIT);
            }
            List<AclEntry> acl = List.of(entry.build());

            return new FileAttribute<List<AclEntry>>() {
                /**
                 * Names the attribute as the Windows ACL view expects.
                 * @return "acl:acl"
                 */
                @Override
                public String name() {
                    return "acl:acl";
                }

                /**
                 * Returns the owner-only ACL to apply when the file is created.
                 * @return the single-entry ACL
                 */
                @Override
                public List<AclEntry> value() {
                    return acl;
                }
            };
        }

        // Fail closed: never create a sensitive file we cannot protect.
        throw new IOException("file system cannot restrict access to the owner; refusing to create file");
    }
}
