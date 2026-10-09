/*
 * Package: securestudentvault
 * File: GradeFormHandler.java
 * Rules covered: IDS14-J, ERR08-J
 * Author: Karsten Tisdale
 * For IT 355 group project
 */
package securestudentvault;

import java.util.Map;

/**
 * Provides one static method for submitting a grade form
 * This class can only be used in a static context, and cannot be instantiated as an object
 */
public class GradeFormHandler {

    /**
     * Private default constructor to ensure that the class cannot be instantiated as an object
     */
    private GradeFormHandler(){
        
    }

    /**
     * Static method that takes in a user HTTP session, a student registry object, and a form input object as {@code Map<String, String>}
     * Performs thorough checks and validation on the user form, and attempts to add the grade to the registry
     * This method is to be called statically, which is the purpose of this class
     * The user's role comes from the SERVER-SIDE session, and a hidden "role" field is ignored
     * 
     * @param session The user HTTP session
     * @param registry The student registry to add the grade to
     * @param form The grade form being submitted
     * @return String clearly stating the reuslt of the method call
     */
    public static String submit(Session session, StudentRegistry registry, Map<String, String> form) {

        // ERR08-J: Do not catch NullPointerException or any of its ancestors
        // Instead of catching NullPointerExceptions, we must check if the provided
        // values are null before we attempt to do anything else with them
        if (session == null || registry == null || form == null) {
            return "REJECTED: malformed request";
        }

        // Validate that the user is a teacher
        if (!session.isTeacher()) {
            return "REJECTED: only teachers may enter grades";
        }
        


        // IDS14-J: Do not trust the contents of hidden form fields
        // The HTML web form fields are hidden and given by the client, and must be sanitized

        String uidText = form.get("studentUid");
        String gradeText = form.get("grade");

        if (uidText == null || gradeText == null){
            return "REJECTED: missing field";
        }
        
        // Validate the format of provided data and attempt to add the grade to the registry
        try{
            // Parse numbers from strings
            int uid = Integer.parseInt(uidText.trim());
            int grade = Integer.parseInt(gradeText.trim());
            // Validate number ranges
            Check.inRange(uid, SecureStudentVault.MIN_UID, SecureStudentVault.MAX_UID, "studentUid");
            Check.inRange(grade, 0, 100, "grade");

            // Attempt to add the grade to the registry
            if(registry.addGrade(uid, grade))
                return "ACCEPTED";
            else
                return "REJECTED: unknown student";
        }
        // Catch block if the provided data has invalid number format
        catch (NumberFormatException e){
            return "REJECTED: non-numeric value";
        }
        // Catch block if the provided data have some other error
        catch (IllegalArgumentException e){
            return "REJECTED: " + e.getMessage();
        }
    }
}
