package rules.obj01j;

/**
 * working example for OBJ01-J
 *
 * OBJ01-J: Limit accessibility of fields
 *
 * this example keeps the account balance field private so that
 * outside code cannot directly change it. Public methods are
 * provided to safely access and update the balance
 */
public class Obj01j {

    /**
     * stores the account balance
     * the field is private so outside code cannot access it directly
     */
    private double balance;

    /**
     * creates an account with an initial balance
     *
     * @param initialBalance starting account balance
     */
    public Obj01j(double initialBalance) {
        balance = initialBalance;
    }

    /**
     * returns the current account balance
     *
     * @return current account balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * adds money to the account
     *
     * @param amount amount to deposit
     */
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    /**
     * removes money from the account when sufficient
     * funds are available
     *
     * @param amount amount to withdraw
     * @return true if the withdrawal was successful,
     *         otherwise false
     */
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }

        return false;
    }

}

