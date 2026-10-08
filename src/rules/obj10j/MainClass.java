package rules.obj10j;

/** 
 * Demonstrates CERT rule OBJ10-J ( Do not use public static nonfinal fields). 
 * 
 * Important class values should not be directly changeable by outside code.
 * A public, non-final field can be changed to any value, which can cause
 * unexpected or unsafe behavior. 
 * 
 * The noncompliant example uses a public field that outside code can change. 
 * The compliant example keeps the value private and final and provides a 
 * getter to safely read the value. 
 */
public class MainClass {

    /** 
     * NONCOMPLIANT: The setting is public and can be changed by any code. 
     */
    static class NoncompliantSettings {
        public static int maxLoginAttempts = 3;
    }

    /**
    * COMPLIANT: The setting is private and final, so outside code cannot 
    * change it. 
    */
    static class CompliantSettings {
        private static final int MAX_LOGIN_ATTEMPTS = 3;

        public static int getMaxLoginAttempts() {
            return MAX_LOGIN_ATTEMPTS;
        }
    }
    /** 
    * Runs the noncompliant and compliant examples. 
    * The noncompliant value can be changed directly by outside code.
    * The compliant value cannot be changed because the field is private 
    * and final. 
    * 
    *  @param args 
    * */
    public static void main(String[] args) {
        System.out.println("Noncompliant before: " + NoncompliantSettings.maxLoginAttempts);

        // Outside code overwrites the value, and nothing can validate or stop it
        NoncompliantSettings.maxLoginAttempts = 1000000;
        System.out.println("Noncompliant after:  " + NoncompliantSettings.maxLoginAttempts);

        System.out.println("Compliant:           " + CompliantSettings.getMaxLoginAttempts());

        // These lines do not compile, which is the point:
        // CompliantSettings.MAX_LOGIN_ATTEMPTS = 1000000;   // field is private
        // CompliantSettings.getMaxLoginAttempts() = 5;      // a method call can't be assigned to
    }
}
