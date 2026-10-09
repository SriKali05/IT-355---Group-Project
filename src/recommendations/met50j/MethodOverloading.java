package recommendations.met50j;

/**
 * working example for MET50-J
 *
 * MET50-J: Avoid ambiguous or confusing uses of overloading
 *
 * this example uses different method names for different types
 * of searches instead of using confusing overloaded methods
 */
public class MethodOverloading {

    /**
     * demonstrates using clear method names instead of
     * confusing overloaded methods
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
    	Student[] studentArr = {new Student(12345, "Valierie"),
    							new Student(54321, "Srida"),
    							new Student(13254, "Allaya")};
    	
        System.out.println(getStudentById(studentArr, 54321));
        System.out.println(getStudentByName(studentArr, "Allaya"));
    }
    
    /**
     * finds a student using a student ID
     *
     * @param arr the students to search
     * @param id student ID
     * @return student information
     */
    public static String getStudentById(Student[] arr, int id) {
        for(int i = 0; i < arr.length; i++)
        	if(arr[i].getId() == id)
				return arr[i].toString();

        return "Student could not be found";
    }

    /**
     * finds a student using the student's name
     *
     * @param arr the students to search
     * @param name student name
     * @return student information
     */
    public static String getStudentByName(Student[] arr, String name) {
        for(int i = 0; i < arr.length; i++)
        	if(arr[i].getName().equals(name))
				return arr[i].toString();
        
        return "Student could not be found";
    }
}
