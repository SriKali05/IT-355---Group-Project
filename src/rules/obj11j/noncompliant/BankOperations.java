package rules.obj11j.noncompliant;

/**
 * Noncompliant example code explaining rule OBJ11-J (be wary of letting constructors throw exceptions)
 * 
 * class that checks if a user is allowed to use its functions when the object created
 * 
 * Vulnerability: if the security check fails, the constrcutor throws an exception, so the user
 * does not recieve the object. However, the object has already been created in memory.
 * Because the class is not final and does not prevent finalization, an attacker could 
 * create a subclass and use the finalize() method to get accesss to the partially created object 
 * and bypass the security check.
 * BankOperations
 */
class BankOperations {

    /**
     * Creates a Bank operations object only if the SSN verification is successful.
     *  @throws SecurityException if the SSN verification fails. The object is not fully created,
     *  but an attacker could still access it through a subclass using the finalize() method.
     */
    public BankOperations() {
        // Perform a security check (e.g., verify the user's SSN)
        if (!performSSNVerification()) {
            throw new SecurityException("Access Denied!");
        }
    }

    /**
     * Simulates an SSN verification check
     * @return false to simulate a failed verification
     */
    private boolean performSSNVerification() {
        return false;
    }

    /**
     * does not perform any security checks
     * 
     * it assumers the constructor already verifies the user, which 
     * makes the operation vulnerable if an attacker gains access to the object
     */
    public void greet() {
        System.out.println("Welcome! You may use all the features.");
    }
}
