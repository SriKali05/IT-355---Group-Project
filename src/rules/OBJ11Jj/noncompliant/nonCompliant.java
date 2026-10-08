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
/** 
 * A subclass that demonstrates a finalizer attack
 * 
 * 1) The attacker creates an Attacker object.
 * 2) the bankOperations constructor fails the security check
 * 3) even though the constructor fails, the object still exists in memory
 * 4) the object becomes avaliable for garbage collection
 * 5) The finalize() method saves the object in stolenInstance
 * 6) the attacker can then use the recovered object without passing the oiginal security check
 */
class Attacker extends BankOperations {

    // The stolen instance of the BankOperations object
    private static Attacker stolenInstance;

    /**
     * Calls the BankOperations constructor
     * 
     * The constructor throws a SecurityException because verification
     */
    public Attacker() {
        super(); 
    }

    /**    
     * Saves the object before it is removed in memory
     */
    @Override
    protected void finalize() {
        stolenInstance = this;
    }

    /**
     * Attempts to recover the object after the constructor fails
     * @return the recovered object, or null if the attack fails
     */
    public static Attacker getStolenInstance() {
        try {
            // Attempt to create an Attacker object, which will fail the security check
            new Attacker();
        } catch (SecurityException e) {
            // The constructor failed, but the object may still be in memory and available for garbage collection
        }

        System.gc();

        // Wait for a short time to allow the garbage collector to run and finalize the object
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Return the stolen instance if it was successfully recovered
        return stolenInstance;
    }
}
/**
 * Runs the finalizer attack demonstration. If the finalizer attack is successful,
 * the program prints the welcom message even though verification failed.
 */
class Main {
    /**
     * Start of program
     * @param args
     */
    public static void main(String[] args) {
        Attacker stolen = Attacker.getStolenInstance();

        if (stolen != null) {
            stolen.greet();
        } else {
            System.out.println("No instance obtained.");
        }
    }
}