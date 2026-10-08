package recommendations.obj50j;

/**
 * Demonstrates the difference between a final reference
 * and an immutable referenced object.
 */
public class ImmutableReferenceExample {

    /**
     * Creates and displays an immutable Point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        final Obj50j point = new Obj50j(1, 2);

        System.out.println("x = " + point.getX());
        System.out.println("y = " + point.getY());

        /*
         * The reference cannot point to another object because it is final.
         * The coordinates also cannot be modified because OBJ50J
         * stores them in final fields and provides no setter methods.
         */
    }
}