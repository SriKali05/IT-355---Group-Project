/*
 * Package: securestudentvault
 * File: CsvReportFormatter.java
 * Rules covered: MET04-J
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package src.securestudentvault;

import java.util.Locale;

/**
 * Defines csv output format for Student grades, and provides methods for getting the format
 */
public class CsvReportFormatter extends ReportFormatter {
    // MET04-J: Do not increase the accessibility of overridden or hidden methods
    // Overriding methods are final and protected, because the
    // methods that are being overridden are also protected

    /**
     * Gets the csv format for the header of a csv student report
     * @return A String with the first line (header) of the csv
     */
    @Override
    protected final String header() {
        return "uid,name,average,letter";
    }

    /**
     * Gets the csv format for a Student object in a csv student report
     * @param s The Student object to get csv format for
     * @return A String with one csv row, representing a single Student
     */
    @Override
    protected final String row(Student s) {
        return s.getUid() + "," + s.getName() + "," + String.format(Locale.ROOT, "%.1f", s.average()) + "," + s.letter();
    }
}
