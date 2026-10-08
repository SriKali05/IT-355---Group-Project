package securestudentvault;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Set;

/* =====================================================================================
 *  SERIALIZATION SUPPORT   (SER12-J)
 * ===================================================================================== */
final class SafeObjectInputStream extends ObjectInputStream {
    // SER12-J: allowlist of every class that may appear in a serialized Student.
    private static final Set<String> ALLOWED = Set.of(
            Student.class.getName(),
            Student.Name.class.getName(),
            "java.util.ArrayList",
            "java.lang.Integer",
            "java.lang.Number");

    SafeObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        if (!ALLOWED.contains(desc.getName())) {
            throw new InvalidClassException(desc.getName(), "class is not on the deserialization allowlist");
        }
        return super.resolveClass(desc);
    }
}
