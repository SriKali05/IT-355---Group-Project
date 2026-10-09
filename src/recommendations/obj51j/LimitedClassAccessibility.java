package recommendations.obj51j;

/**
 * working example for OBJ51-J
 *
 * OBJ51-J: Minimize the accessibility of classes and their members
 *
 * this example keeps an internal class and its data restricted
 * because they do not need to be publicly accessible
 */
public class LimitedClassAccessibility {
	
    /**
     * demonstrates limited accessibility of the class
     * and its members
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        BankAccount account = new BankAccount(100.00);
        account.displayBalance();
    }

}
