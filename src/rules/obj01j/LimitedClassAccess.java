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
public class LimitedClassAccess {

    /**
     * demonstrates limiting field accessibility by using
     * methods instead of allowing direct access to the field
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        BankAccount account = new BankAccount(100.00);

        System.out.println("Starting balance: "
                + account.getBalance());

        account.deposit(50.00);

        System.out.println("After deposit: "
                + account.getBalance());

        boolean successful = account.withdraw(25.00);

        System.out.println("Withdrawal successful: "
                + successful);

        System.out.println("Final balance: "
                + account.getBalance());
    }
}

