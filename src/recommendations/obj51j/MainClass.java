package recommendations.obj51;

/**
 * working example for OBJ51-J
 *
 * OBJ51-J: Minimize the accessibility of classes and their members
 *
 * this example keeps an internal class and its data restricted
 * because they do not need to be publicly accessible
 */
public class MainClass {

    /**
     * internal class that does not need to be public
     */
    static class Account {

        /**
         * account balance is kept private
         */
        private double balance;

        /**
         * creates an account with a starting balance
         *
         * @param balance starting account balance
         */
        Account(double balance) {
            this.balance = balance;
        }

        /**
         * displays the account balance
         */
        void displayBalance() {
            System.out.println("Balance: " + balance);
        }
    }

    /**
     * demonstrates limited accessibility of the class
     * and its members
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Account account = new Account(100.00);

        account.displayBalance();
    }
}
