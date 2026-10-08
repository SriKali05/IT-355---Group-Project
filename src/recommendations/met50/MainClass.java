package recommendations.met50;

/**
 * working example for MET50-J
 *
 * MET50-J: Avoid ambiguous or confusing uses of overloading
 *
 * this example uses different method names for different types
 * of searches instead of using confusing overloaded methods
 */
public class MainClass {

    /**
     * finds a student using a student ID
     *
     * @param id student ID
     * @return student information
     */
    public static String getStudentById(int id) {
        return "Student ID: " + id;
    }

    /**
     * finds a student using the student's name
     *
     * @param name student name
     * @return student information
     */
    public static String getStudentByName(String name) {
        return "Student name: " + name;
    }

    /**
     * demonstrates using clear method names instead of
     * confusing overloaded methods
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println(getStudentById(12345));
        System.out.println(getStudentByName("Valerie"));
    }
}
