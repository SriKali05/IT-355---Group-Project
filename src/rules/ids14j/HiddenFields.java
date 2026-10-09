package rules.ids14j;

/**
 * working example for IDS14-J
 *
 * IDS14-J: Do not trust the contents of hidden form fields
 *
 * This example validates a value received from a hidden form field
 * before using it
 */
public class HiddenFields {
	
    /**
     * demonstrates validation of hidden form field data
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        String validRole = "student";
        String modifiedRole = "administrator";

        processForm(validRole);
        processForm(modifiedRole);
    }

    /**
     * processes a hidden form field after validating its value
     *
     * @param role value received from the hidden form field
     */
    public static void processForm(String role) {

        // hidden form fields can be changed by the client,
        // so the value must be validated before it is used
        if (!"student".equals(role) && !"employee".equals(role)) {
            System.out.println("Invalid role rejected.");
            return;
        }

        System.out.println("Accepted role: " + role);
    }
}
