package recommendations.met55j;

import java.util.ArrayList;
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
public class SongPlaylist {
	
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

    /**
    * NONCOMPLIANT: Returns null when no songs match the artist. 
    * 
    * Returning null makes the calling code check for null before using 
    * the returned list. If the caller does not check, a 
    * NullPointerException can occur. 
    */
    static class NoncompliantPlaylist {
        private final List<String> songs = new ArrayList<>();

        NoncompliantPlaylist() {
            songs.add("Artist A - Song 1");
            songs.add("Artist A - Song 2");
        }

        public List<String> findByArtist(String artist) {
            List<String> matches = new ArrayList<>();
            for (String song : songs) {
                if (song.startsWith(artist)) {
                    matches.add(song);
                }
            }
            if (matches.isEmpty()) {
                return null; // in-band error indicator
            }
            return matches;
        }
    }

    /**
     * COMPLIANT: Always returns a list.
     * If no songs match the artist, the method returns an empty list instead
     * of null. The calling code can safely use the returned list without
     * needing to check for null.
     */
    static class CompliantPlaylist {
        private final List<String> songs = new ArrayList<>();

        CompliantPlaylist() {
            songs.add("Artist A - Song 1");
            songs.add("Artist A - Song 2");
        }

        public List<String> findByArtist(String artist) {
            List<String> matches = new ArrayList<>();
            for (String song : songs) {
                if (song.startsWith(artist)) {
                    matches.add(song);
                }
            }
            return matches; // possibly empty, never null
        }
    }
}