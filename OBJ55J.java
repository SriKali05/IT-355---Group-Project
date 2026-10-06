/**
 * working example for OBJ55-J
 *
 * OBJ55-J: Remove short-lived objects from long-lived container objects
 *
 * this example removes a reference to a temporary object
 * after it is no longer needed
 */
public class OBJ55J {

    /**
     * simple class representing a temporary message
     */
    static class Message {

        /**
         * stores the message text
         */
        private String text;

        /**
         * creates a message
         *
         * @param text message text
         */
        Message(String text) {
            this.text = text;
        }

        /**
         * Returns the message text.
         *
         * @return message text
         */
        public String getText() {
            return text;
        }
    }

    /**
     * Demonstrates removing a temporary object reference
     * from a long-lived array
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Message[] messages = new Message[3];

        Message temporaryMessage =
                new Message("Temporary message");

        messages[0] = temporaryMessage;

        System.out.println(messages[0].getText());

        // remove the reference when the object is no longer needed
        messages[0] = null;

        System.out.println("Temporary reference removed.");
    }
}
