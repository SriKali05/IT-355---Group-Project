package rules.met04j;

/**
 * child class that overrides the parent method
 * without increasing its accessibility
 */
class Child extends Parent {

    /**
     * overrides the parent method with the same
     * protected accessibility
     */
    @Override
    protected void performAction() {
        System.out.println("Child action");
    }
}
