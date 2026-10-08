/**
 * File: ReportFormatter.java
 * Srida Kalidindi
 * Class: IT 355 Group Project 01
 */
package securestudentvault;
 
import java.util.List;
/* =====================================================================================
 *  REPORT FORMATTERS   (MET04-J)
 * ===================================================================================== */
 
/**
 * Base class for building a text report of students. Subclasses supply the
 * header and row format, and {@link #render(List)} cannot be overridden.
 */
abstract class ReportFormatter {
 
    /**
     * Gives the header line of the report.
     *
     * @return the header text
     */
    protected abstract String header();
 
    /**
     * Formats one student as a row of the report.
     *
     * @param s the student to format
     * @return the row text
     */
    protected abstract String row(Student s);
 
    /**
     * Builds the full report: the header followed by one row per student.
     *
     * @param students the students to include
     * @return the finished report text
     */
    final String render(List<Student> students) {
        StringBuilder sb = new StringBuilder(header()).append('\n');
        for (Student s : students) {
            sb.append(row(s)).append('\n');
        }
        return sb.toString();
    }
}