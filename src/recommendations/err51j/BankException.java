package recommendations.err51j;

/**
 * A specific exception used when an account does not contain
 * enough money to complete a withdrawal.
 */
class BankException extends Exception {

    /**
     * Creates the exception with a useful explanation.
     *
     * @param message explanation of the failed withdrawal
     */
    public BankException(String message) {
        super(message);
    }
}
