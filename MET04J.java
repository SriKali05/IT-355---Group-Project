/**
 * working example for MET04-J
 *
 * MET04-J: Do not increase the accessibility of overridden
 * or hidden methods
 *
 * this example keeps the overridden method protected,
 * matching the accessibility of the parent method
 */
public class MET04J {

    /**
     * parent class with a protected method
     */
    static class Parent {

        /**
         * performs an action for subclasses
         */
        protected void performAction() {
            System.out.println("Parent action");
        }
    }

    /**
     * child class that overrides the parent method
     * without increasing its accessibility
     */
    static class Child extends Parent {

        /**
         * overrides the parent method with the same
         * protected accessibility
         */
        @Override
        protected void performAction() {
            System.out.println("Child action");
        }
    }

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
