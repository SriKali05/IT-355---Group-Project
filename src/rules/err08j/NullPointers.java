package rules.err08j;

/**
 * working example for ERR08-J
 *
 * ERR08-J: Do not catch NullPointerException or any of its ancestors
 *
 * this example checks for a null value before using the object
 * instead of catching a NullPointerException
 */
public class NullPointers {
	
    /**
     * demonstrates the compliant approach
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        processName("Valerie");
        processName(null);
    }

    /**
     * processes a name after checking whether it is null
     *
     * @param name the name to process
     */
    public static void processName(String name) {
        if (name == null) {
            System.out.println("Name was null.");
            return;
        }

        System.out.println("Name: " + name);
    }
}
