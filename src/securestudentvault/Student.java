package securestudentvault;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * OBJ11-J: declared final, so no subclass can ever obtain a partially
 * constructed Student when the constructor throws.
 * SER01-J / SER12-J: custom serialization methods use the exact
 * required signatures.
 */
final class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * SER05-J: serializable nested class is STATIC, so the outer
     * Student is never dragged along.
     */
    static final class Name implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String value;

        /**
         * creates a validated student name
         *
         * @param value the student's name
         */
        Name(String value) {
            this.value = Check.matches(value, SecureStudentVault.NAME_PATTERN, "name");
        }

        /**
         * returns the student's name
         *
         * @return the name value
         */
        String value() {
            return value;
        }

        /**
         * Writes this name to a serialization stream.
         *
         * SER01-J: exact signature (private, void, one ObjectOutputStream
         * parameter, throws IOException), or serialization would ignore it.
         *
         * @param out the stream being written
         * @throws IOException if writing fails
         */
        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
        }

        /**
         * Reads this name from a serialization stream and re-validates it,
         * because deserialization bypasses the constructor's checks.
         *
         * SER01-J: exact signature (private, void, one ObjectInputStream
         * parameter, throws IOException and ClassNotFoundException).
         *
         * @param in the stream being read
         * @throws IOException if reading fails or the name is invalid
         * @throws ClassNotFoundException if a class in the stream cannot be found
         */
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            if (value == null || !SecureStudentVault.NAME_PATTERN.matcher(value).matches()) {
                throw new InvalidObjectException("invalid name");
            }
        }

        /**
         * returns the student's name as text
         *
         * @return the name value
         */
        @Override
        public String toString() {
            return value;
        }
    }

    // OBJ01-J: private fields only.
    private final Name name;
    private final int uid;
    private List<Integer> grades; // not final: readObject() installs a validated defensive copy

    /**
     * creates a student after validating the name and UID
     *
     * @param name the student's name
     * @param uid the student's unique ID
     */
    Student(String name, int uid) { // MET00-J: arguments validated before anything is assigned
        this.name = new Name(name);
        this.uid = Check.inRange(uid, SecureStudentVault.MIN_UID, SecureStudentVault.MAX_UID, "uid");
        this.grades = new ArrayList<>();
    }

    /**
     * returns the student's unique ID
     *
     * @return the student's UID
     */
    int getUid() {
        return uid;
    }

    /**
     * returns the student's name
     *
     * @return the student's name
     */
    Name getName() {
        return name; // immutable, safe to hand out
    }

    /**
     * adds a validated grade to the student's record
     *
     * @param grade the grade to add
     */
    void addGrade(int grade) {
        Check.inRange(grade, 0, 100, "grade"); // MET00-J
        boolean added = grades.add(grade);
        if (!added) { // EXP00-J
            throw new IllegalStateException("grade was not recorded");
        }
    }

    /**
     * OBJ05-J: returns an unmodifiable COPY, never the private list.
     * MET55-J: never null.
     *
     * @return a copy of the student's grades
     */
    List<Integer> getGrades() {
        return List.copyOf(grades);
    }

    /**
     * calculates the student's average grade
     *
     * @return the average grade, or 0.0 if there are no grades
     */
    double average() {
        if (grades.isEmpty()) {
            return 0.0;
        }

        int sum = 0;
        for (int g : grades) {
            sum += g;
        }

        return (double) sum / grades.size();
    }

    /**
     * returns the student's letter grade
     *
     * @return the letter grade
     */
    String letter() {
        return GradeScale.letterFor(average());
    }

    /**
     * creates an independent copy of the student
     *
     * @return a copy of the student
     */
    Student copy() {
        Student c = new Student(name.value(), uid);
        for (int g : grades) {
            c.addGrade(g);
        }
        return c;
    }

    /**
     * Writes this student to a serialization stream.
     *
     * SER01-J: exact, conventional signature (private, void, one
     * ObjectOutputStream parameter, throws IOException).
     *
     * @param out the stream being written
     * @throws IOException if writing fails
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * validates the student's data after deserialization
     *
     * @param in the object input stream
     * @throws IOException if the serialized data is invalid
     * @throws ClassNotFoundException if a serialized class cannot be found
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        // Re-run the constructor's invariants; deserialization bypasses the constructor.
        if (name == null || grades == null) {
            throw new InvalidObjectException("missing fields");
        }

        if (uid < SecureStudentVault.MIN_UID || uid > SecureStudentVault.MAX_UID) {
            throw new InvalidObjectException("uid out of range");
        }

        List<Integer> copy = new ArrayList<>(grades.size());

        for (Integer g : grades) {
            if (g == null || g < 0 || g > 100) {
                throw new InvalidObjectException("grade out of range");
            }
            copy.add(g);
        }

        grades = copy; // defensive copy of untrusted list
    }
}
