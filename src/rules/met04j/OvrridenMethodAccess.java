package rules.met04j;

/**
 * working example for MET04-J
 *
 * MET04-J: Do not increase the accessibility of overridden
 * or hidden methods
 *
 * this example keeps the overridden method protected,
 * matching the accessibility of the parent method
 */
public class OvrridenMethodAccess {
    /**
     * demonstrates the compliant implementation
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Child child = new Child();

        child.performAction();
    }


}
