package recommendations.obj51j;

/**
 * class that does not need to be public
 */
class BankAccount {

    /**
     * account balance is kept private
     */
    private double balance;

    /**
     * creates an account with a starting balance
     *
     * @param balance starting account balance
     */
    BankAccount(double balance) {
        this.balance = balance;
    }

    /**
     * displays the account balance
     */
    void displayBalance() {
        System.out.println("Balance: " + balance);
    }
}
