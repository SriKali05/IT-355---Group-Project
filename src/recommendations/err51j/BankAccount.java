package recommendations.err51j;

/**
 * Demonstrates ERR51-J by using a user-defined exception
 * for a specific application error.
 */
public class BankAccount {

    private double balance;

    /**
     * Creates an account with a starting balance.
     *
     * @param startingBalance the initial account balance
     */
    public BankAccount(double startingBalance) {
        balance = startingBalance;
    }

    /**
     * Withdraws money when sufficient funds are available.
     *
     * @param amount amount of money to withdraw
     * @throws ERR51 if the withdrawal exceeds the balance
     */
    public void withdraw(double amount) throws Err51j {
        if (amount > balance) {
            throw new Err51j(
                "The account does not have enough funds."
            );
        }

        balance -= amount;
    }

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