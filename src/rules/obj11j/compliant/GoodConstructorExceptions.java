package rules.obj11j.compliant;

/**
 * Shows the normal way to use BankOperations.
 *
 * The program only uses the object if it is created successfully, and
 * BankOperations is now final, so this usage pattern is safe.
 * MainCompliant
 */
class GoodConstructorExceptions{
    public static void main(String[] args) {
        try {
            BankOperations bank = new BankOperations();
            bank.greet();
        } catch (SecurityException e) {
            System.out.println(e.getMessage()); // prints "Access Denied!"
        }
    }
}