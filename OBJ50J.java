/**
 * Demonstrates OBJ50-J by creating an immutable Point object.
 * The object's fields cannot be changed after construction.
 */
final class OBJ50J {

    private final int x;
    private final int y;

    /**
     * Creates an immutable point.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public OBJ50J(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x-coordinate.
     *
     * @return the x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate.
     *
     * @return the y-coordinate
     */
    public int getY() {
        return y;
    }
}

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
        final OBJ50J point = new OBJ50J(1, 2);

        System.out.println("x = " + point.getX());
        System.out.println("y = " + point.getY());

        /*
         * The reference cannot point to another object because it is final.
         * The coordinates also cannot be modified because OBJ50J
         * stores them in final fields and provides no setter methods.
         */
    }
}