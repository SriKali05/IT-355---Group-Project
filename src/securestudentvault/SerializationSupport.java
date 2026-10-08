package src.securestudentvault;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

final class SerializationSupport {
    private SerializationSupport() { }

    static byte[] toBytes(Serializable obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(obj);
        }
        return bos.toByteArray();
    }

    static Object fromBytes(byte[] data) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new SafeObjectInputStream(new ByteArrayInputStream(data))) {
            return in.readObject();
        }
    }

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
