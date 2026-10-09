package recommendations.met55j;

import java.util.ArrayList;
import java.util.List;

/**
 * COMPLIANT: Always returns a list.
 * If no songs match the artist, the method returns an empty list instead
 * of null. The calling code can safely use the returned list without
 * needing to check for null.
 */
public class CompliantPlaylist {
    private final List<String> songs = new ArrayList<>();

    /**
     * Creates a playlist containing two songs by Artist A.
     */
    CompliantPlaylist() {
        songs.add("Artist A - Song 1");
        songs.add("Artist A - Song 2");
    }

    /**
     * Finds every song by the given artist.
     *
     * @param artist the artist to search for
     * @return the matching songs; an empty list (never null) if there are none
     */
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