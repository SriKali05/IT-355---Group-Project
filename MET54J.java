/**
 * working example for MET54-J
 *
 * MET54-J: Always provide feedback about the resulting value
 * of a method
 *
 * this example returns a value that tells the caller whether
 * the requested item was found
 */
public class MET54J {

    /**
     * searches for a value in an array
     *
     * @param values array to search
     * @param target value to find
     * @return index of the target, or -1 if it is not found
     */
    public static int findValue(int[] values, int target) {

        for (int i = 0; i < values.length; i++) {

            if (values[i] == target) {
                return i;
            }
        }

        return -1;
    }

    /**
     * demonstrates using the method's return value
     * to determine the result of the operation
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        int[] numbers = {10, 20, 30, 40};

        int index = findValue(numbers, 30);

        if (index != -1) {
            System.out.println("Value found at index: " + index);
        } else {
            System.out.println("Value was not found.");
        }
    }
}
