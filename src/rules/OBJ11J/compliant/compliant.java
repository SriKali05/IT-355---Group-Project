package src.rules.OBJ11J.compliant;

/**
 * Alternative compliant example for rule OBJ11-J: declare the class final.
 *
 * A final class cannot be extended, so an attacker cannot write a subclass
 * that overrides finalize() to capture a partially constructed object.
 * The constructor can keep its simple throw-on-failure structure.
 *
 * BankOperations
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
 
/**
 * Shows the normal way to use BankOperations.
 *
 * The program only uses the object if it is created successfully, and
 * BankOperations is now final, so this usage pattern is safe.
 * MainCompliant
 */
class Main{
    public static void main(String[] args) {
        try {
            BankOperations bank = new BankOperations();
            bank.greet();
        } catch (SecurityException e) {
            System.out.println(e.getMessage()); // prints "Access Denied!"
        }
    }
}