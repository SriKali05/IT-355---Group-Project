package recommendations.err51j;

/**
 * Demonstrates ERR51-J by using a user-defined exception
 * for a specific application error.
 */
public class MainClass {

    /**
     * Demonstrates handling a specific user-defined exception.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        BankAccount account = new BankAccount(100.00);

        try {
            account.withdraw(150.00);
        } catch (Err51j e) {
            System.out.println("Withdrawal denied: " + e.getMessage());
        }
    }
}