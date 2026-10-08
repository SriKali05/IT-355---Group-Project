package securestudentvault;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

final class SerializationSupport {
    /** Prevents instantiation of this utility class.
     */
    private SerializationSupport() { }

    /** Serializes an object into a byte array.
         * @param obj object to serialize
         * @return serialized bytes
         * @throws IOException if serialization fails
     */
    static byte[] toBytes(Serializable obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(obj);
        }
        return bos.toByteArray();
    }

    /** Deserializes bytes using the restricted object input stream.
         * @param data serialized bytes
         * @return deserialized object
         * @throws IOException if reading or validation fails
         * @throws ClassNotFoundException if a serialized class cannot be found
     */
    static Object fromBytes(byte[] data) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new SafeObjectInputStream(new ByteArrayInputStream(data))) {
            return in.readObject();
        }
    }

    /** Deserializes bytes and requires the result to be a Student.
         * @param data serialized student bytes
         * @return deserialized student
         * @throws IOException if deserialization fails or the object type is invalid
     */
    static Student toStudent(byte[] data) throws IOException {
        try {
            Object o = fromBytes(data);
            if (!(o instanceof Student)) {
                throw new InvalidObjectException("unexpected type");
            }
            return (Student) o;
        } catch (ClassNotFoundException e) {
            throw new InvalidObjectException("unknown class in stream");
        }
    }
}
