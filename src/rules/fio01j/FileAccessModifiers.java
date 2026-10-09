package rules.fio01j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.TreeSet;

/**
 * Demonstrates FIO01-J (create files with appropriate access permissions)
 * by creating a file that only its owner can read and write.
 *
 * The permissions are supplied in the same call that creates the file, so
 * the file never exists, even briefly, with looser permissions.
 *
 * Operating systems describe permissions in two different ways, so the
 * program asks the file system that will hold the file which one it uses:
 *   - POSIX (Linux, macOS): the permission string rw------- (owner may read
 *     and write; group and others get nothing)
 *   - ACL (Windows NTFS): an access control list with a single "allow"
 *     entry for the current user. Because the list is given explicitly,
 *     no entries are inherited from the parent folder, so no other user or
 *     group (not even Administrators or SYSTEM) is granted access.
 */
public class FileAccessModifiers {

    /**
     * Runs the secure file creation example and prints the permissions the
     * new file actually ended up with.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Path file = Paths.get("privateData.txt");

        try {
            // Demo reset: remove the file left by a previous run. createFile()
            // below refuses to reuse an existing file, because an existing file
            // keeps whatever permissions it was originally created with.
            if (Files.deleteIfExists(file)) {
                System.out.println("Removed existing privateData.txt");
            }

            createPrivateFile(file);

            System.out.println("Private file created with owner-only permissions.");
            System.out.println("Permissions now on " + file + ":");
            System.out.println("  " + describePermissions(file));
        } catch (UnsupportedOperationException e) {
            System.out.println(
                "This file system supports neither POSIX permissions nor ACLs."
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
     * @throws IOException if the file already exists or cannot be created or written
     * @throws UnsupportedOperationException if the file system cannot restrict permissions
     */
    public static void createPrivateFile(Path filePath)
            throws IOException {

        // Permissions are applied atomically, as part of creating the file.
        Files.createFile(filePath, ownerOnlyAttribute(filePath));

        Files.write(
            filePath,
            "Private account information".getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Builds the "owner may read and write, nobody else may do anything"
     * attribute in whichever form the file system holding the file uses.
     *
     * @param filePath the file that is about to be created
     * @return the attribute to pass to Files.createFile()
     * @throws IOException if the file system or current user cannot be looked up
     * @throws UnsupportedOperationException if neither POSIX permissions nor ACLs are supported
     */
    private static FileAttribute<?> ownerOnlyAttribute(Path filePath) throws IOException {
        // Ask the drive/partition that will actually hold the file, not just
        // the operating system: a USB stick formatted FAT32 supports neither model.
        FileStore store = Files.getFileStore(filePath.toAbsolutePath().getParent());

        if (store.supportsFileAttributeView(PosixFileAttributeView.class)) {
            // Linux and macOS
            return PosixFilePermissions.asFileAttribute(
                PosixFilePermissions.fromString("rw-------")
            );
        }

        if (store.supportsFileAttributeView(AclFileAttributeView.class)) {
            // Windows: one ALLOW entry for the current user only
            UserPrincipal owner = filePath.getFileSystem()
                .getUserPrincipalLookupService()
                .lookupPrincipalByName(System.getProperty("user.name"));

            AclEntry ownerReadWrite = AclEntry.newBuilder()
                .setType(AclEntryType.ALLOW)
                .setPrincipal(owner)
                .setPermissions(
                    // read and write the contents
                    AclEntryPermission.READ_DATA, AclEntryPermission.WRITE_DATA,
                    AclEntryPermission.APPEND_DATA,
                    // read and write the file's attributes; Windows needs these
                    // (including the "named" ones) to open a file for writing
                    AclEntryPermission.READ_ATTRIBUTES, AclEntryPermission.WRITE_ATTRIBUTES,
                    AclEntryPermission.READ_NAMED_ATTRS, AclEntryPermission.WRITE_NAMED_ATTRS,
                    // let the owner view and change these permissions, and delete the file
                    AclEntryPermission.READ_ACL, AclEntryPermission.WRITE_ACL,
                    AclEntryPermission.DELETE, AclEntryPermission.SYNCHRONIZE
                )
                .build();

            List<AclEntry> acl = List.of(ownerReadWrite);

            // Java has no ready-made helper for ACLs (unlike PosixFilePermissions),
            // so the attribute is built directly: "acl:acl" is the name the
            // Windows file system expects for an initial access control list.
            return new FileAttribute<List<AclEntry>>() {
                /**
                 * Names the attribute for the ACL file attribute view.
                 * @return "acl:acl"
                 */
                @Override
                public String name() {
                    return "acl:acl";
                }

                /**
                 * Returns the ACL to apply when the file is created.
                 * @return the single-entry, owner-only ACL
                 */
                @Override
                public List<AclEntry> value() {
                    return acl;
                }
            };
        }

        throw new UnsupportedOperationException("no supported permission model");
    }

    /**
     * Describes a file's permissions so the result of the demo can be seen.
     *
     * @param filePath the file to describe
     * @return the POSIX permission string, or one line per ACL entry
     * @throws IOException if the permissions cannot be read
     */
    private static String describePermissions(Path filePath) throws IOException {
        if (Files.getFileStore(filePath).supportsFileAttributeView(PosixFileAttributeView.class)) {
            return PosixFilePermissions.toString(Files.getPosixFilePermissions(filePath));
        }

        AclFileAttributeView view =
            Files.getFileAttributeView(filePath, AclFileAttributeView.class);
        List<AclEntry> acl = view.getAcl();
        UserPrincipal owner = view.getOwner();

        StringBuilder sb = new StringBuilder();
        boolean ownerOnly = !acl.isEmpty();
        for (AclEntry entry : acl) {
            sb.append(entry.type()).append(' ')
              .append(entry.principal().getName()).append(": ")
              .append(new TreeSet<>(entry.permissions())) // sorted, so the output is stable
              .append(System.lineSeparator()).append("  ");
            if (!entry.principal().equals(owner)) {
                ownerOnly = false;
            }
        }
        sb.append(ownerOnly
            ? "Only the owner appears in the ACL; no other user or group has access."
            : "WARNING: users other than the owner appear in the ACL.");
        return sb.toString();
    }
}
