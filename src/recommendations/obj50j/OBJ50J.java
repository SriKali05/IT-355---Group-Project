package recommendations.obj50j;

/**
 * Demonstrates OBJ50-J by creating an immutable Point object.
 * The object's fields cannot be changed after construction.
 */
final class Obj50j {

    private final int x;
    private final int y;

    /**
     * Creates an immutable point.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public Obj50j(int x, int y) {
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
