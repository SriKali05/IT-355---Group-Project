package securestudentvault;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * maintains registered students and protects its internal records
 * by storing and returning copies.
 */
final class StudentRegistry {
    private final Map<Integer, Student> records = new TreeMap<>();
    // OBJ01-J

    /**
     * registers a copy of a student in the registry
     *
     * @param s the student to register
     * @return true if the student was registered, false if the UID already exists
     */
    synchronized boolean register(Student s) {
        if (s == null) {
            throw new IllegalArgumentException("student must not be null");
        }

        // Store a copy so the caller cannot mutate our state later (OBJ05-J);
        // EXP00-J: putIfAbsent's return value tells us whether the uid was free.
        return records.putIfAbsent(s.getUid(), s.copy()) == null;
    }

    /**
     * adds a grade to a registered student
     *
     * @param uid the student's UID
     * @param grade the grade to add
     * @return true if the student was found and updated
     */
    synchronized boolean addGrade(int uid, int grade) {
        Student s = records.get(uid);

        if (s == null) {
            return false;
        }

        s.addGrade(grade);
        return true;
    }

    /**
     * returns a copy of a registered student
     *
     * @param uid the student's UID
     * @return a student copy, or an empty Optional if not found
     */
    synchronized Optional<Student> snapshot(int uid) {
        Student s = records.get(uid);
        return s == null ? Optional.empty() : Optional.of(s.copy());
    }

    /**
     * MET55-J: returns an empty list, never null, when there is nothing
     * to return.
     *
     * @return copies of all registered students
     */
    synchronized List<Student> snapshots() {
        List<Student> out = new ArrayList<>();

        for (Student s : records.values()) {
            out.add(s.copy());
        }

        return out;
    }

    /**
     * creates a summary of all registered students
     *
     * @return the student summary
     */
    synchronized String summary() {
        return new SummaryView().render();
    }

    /**
     * OBJ08-J: this nested class reads the outer class's private map,
     * so it is itself PRIVATE. Outside code cannot reach it; only an
     * immutable String leaves the outer class.
     */
    private final class SummaryView {

        /**
         * creates a summary containing student UIDs and letter grades
         *
         * @return the formatted student summary
         */
        String render() {
            StringBuilder sb = new StringBuilder();

            for (Student s : records.values()) {
                if (sb.length() > 0) {
                    sb.append("; ");
                }

                sb.append(s.getUid()).append('=').append(s.letter());
            }

            return sb.length() == 0 ? "(empty)" : sb.toString();
        }
    }
}
