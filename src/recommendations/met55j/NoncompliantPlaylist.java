package recommendations.met55j;

import java.util.ArrayList;
import java.util.List;

/**
* NONCOMPLIANT: Returns null when no songs match the artist. 
* 
* Returning null makes the calling code check for null before using 
* the returned list. If the caller does not check, a 
* NullPointerException can occur. 
*/
public class NoncompliantPlaylist {
    private final List<String> songs = new ArrayList<>();

    /**
     * Creates a playlist containing two songs by Artist A.
     */
    NoncompliantPlaylist() {
        songs.add("Artist A - Song 1");
        songs.add("Artist A - Song 2");
    }

    /**
     * Finds every song by the given artist.
     *
     * @param artist the artist to search for
     * @return the matching songs, or null if there are none (the
     *         noncompliant behavior this example demonstrates)
     */
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