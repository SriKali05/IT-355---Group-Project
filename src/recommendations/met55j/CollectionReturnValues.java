package recommendations.met55j;

import java.util.List;

/** 
* Demonstrates CERT rule MET55-J. 
*
* Methods that return collections should return an empty collection instead 
* of null when there are no results. Returning null can cause a
* NullPointerException when the calling code tries to use the result. 
*
* The noncompliant example returns null when no songs are found. 
* The compliant example always returns a list, even when it is empty. 
*/
public class CollectionReturnValues {
	
    /**
     * Runs the noncompliant and compliant examples.
     * The noncompliant example returns null when no songs are found, which
     * causes a NullPointerException when the result is used.
     * The compliant example returns an empty list, so the result can be
     * used safely even when no songs are found.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Noncompliant:");
        try {
            List<String> result = new NoncompliantPlaylist().findByArtist("Artist B");
            System.out.println("  Songs found: " + result.size());
        } catch (NullPointerException e) {
            System.out.println("  NullPointerException: client did not expect null");
        }

        System.out.println("\nCompliant:");
        List<String> result = new CompliantPlaylist().findByArtist("Artist B");
        System.out.println("  Songs found: " + result.size());
    }
}