package rules.obj11j.compliant;

/**
 * Shows the normal way to use BankOperations.
 *
 * The program only uses the object if it is created successfully, and
 * BankOperations is final, so this usage pattern is safe.
 */
class GoodConstructorExceptions{
    /**
     * Tries to create a BankOperations object. Verification fails, so the
     * constructor throws and the program reports the error instead of using
     * the object.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            BankOperations bank = new BankOperations();
            bank.greet();
        } catch (SecurityException e) {
            System.out.println(e.getMessage()); // prints "Access Denied!"
        }
    }
}