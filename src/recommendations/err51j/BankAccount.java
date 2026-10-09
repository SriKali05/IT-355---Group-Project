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
     * @throws BankException if the withdrawal exceeds the balance
     */
    public void withdraw(double amount) throws BankException {
        if (amount > balance) {
            throw new BankException(
                "The account does not have enough funds."
            );
        }

        balance -= amount;
    }

}