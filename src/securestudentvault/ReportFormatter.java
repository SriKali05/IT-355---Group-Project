package src.securestudentvault;
import java.util.List;
/* =====================================================================================
 *  REPORT FORMATTERS   (MET04-J)
 * ===================================================================================== */
abstract class ReportFormatter {
    protected abstract String header();

    protected abstract String row(Student s);

    final String render(List<Student> students) {
        StringBuilder sb = new StringBuilder(header()).append('\n');
        for (Student s : students) {
            sb.append(row(s)).append('\n');
        }
        return sb.toString();
    }
}
