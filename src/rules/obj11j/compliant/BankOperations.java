package rules.obj11j.compliant;

/**
 * Compliant example for rule OBJ11-J: declare the class final.
 *
 * A final class cannot be extended, so an attacker cannot write a subclass
 * that overrides finalize() to capture a partially constructed object.
 * The constructor can keep its simple throw-on-failure structure.
 */
final class BankOperations {
 
    /**
     * Creates a BankOperations object only if the SSN verification is successful.
     *
     * @throws SecurityException if the SSN verification fails. No subclass can
     *         exist, so nobody can intercept the partially created object.
     */
    public BankOperations() {
        if (!performSSNVerification()) {
            throw new SecurityException("Access Denied!");
        }
    }
 
    /**
     * Simulates an SSN verification check.
     * @return false to simulate a failed verification
     */
    private boolean performSSNVerification() {
        return false;
    }
 
    /**
     * Greets the user. Safe because no malicious subclass can exist.
     */
    public void greet() {
        System.out.println("Welcome! You may use all the features.");
    }

    /*
    * The attacker from the noncompliant example no longer compiles:
    *
    *   class Attacker extends BankOperations { ... }
    *   // error: cannot inherit from final BankOperations
    *
    * If the class must stay non-final, add this instead
    * which prevents any subclass from overriding finalize():
    *
    *   @Override
    *   protected final void finalize() { }
    */
}
