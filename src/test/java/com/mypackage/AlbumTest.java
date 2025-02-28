package la1;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class AlbumTest {

    /**
     * Helper method to create a dummy Song.
     */
    private Song createSong(String title) {
        // Using the simple two-argument constructor.
        return new Song(title, "TestArtist");
    }

    /**
     * Test that the constructor makes a deep copy of the songs list.
     * Modifying the original list after construction should not affect the Album.
     */
    @Test
    public void testConstructorDeepCopy() {
        List<Song> originalSongs = new ArrayList<>();
        originalSongs.add(createSong("Song A"));
        originalSongs.add(createSong("Song B"));

        Album album = new Album("Album1", "Artist1", "Rock", 2021, originalSongs);

        // Modify original list
        originalSongs.clear();
        // Use getSongsDirect() to check the stored list
        ArrayList<Song> storedSongs = album.getSongsDirect();
        assertEquals(2, storedSongs.size(), "Album should keep a deep copy of the original songs list.");
    }

    /**
     * Test that getSongs() returns a deep copy.
     * Modifying the returned list should not affect the album's internal songs.
     */
    @Test
    public void testGetSongsDeepCopy() {
        List<Song> songList = new ArrayList<>();
        songList.add(createSong("Song A"));
        Album album = new Album("Album1", "Artist1", "Rock", 2021, songList);

        ArrayList<Song> copy1 = album.getSongs();
        // Modify the returned copy
        copy1.add(createSong("Song B"));

        // The internal list (via getSongsDirect) remains unchanged.
        ArrayList<Song> internalSongs = album.getSongsDirect();
        assertEquals(1, internalSongs.size(), "getSongs() should return a deep copy that does not affect the internal list.");
    }

    /**
     * Test getSongsTitles() returns the correct list of song titles.
     */
    @Test
    public void testGetSongsTitles() {
        List<Song> songList = new ArrayList<>();
        songList.add(createSong("Alpha"));
        songList.add(createSong("Beta"));
        Album album = new Album("AlbumTitles", "ArtistTitles", "Pop", 2019, songList);

        List<String> titles = album.getSongsTitles();
        assertEquals(Arrays.asList("Alpha", "Beta"), titles, "getSongsTitles should return the titles of the songs in order.");
    }

    /**
     * Test toString() with an empty song list.
     */
    @Test
    public void testToStringEmptySongs() {
        List<Song> emptyList = new ArrayList<>();
        Album album = new Album("EmptyAlbum", "NoArtist", "Jazz", 2000, emptyList);

        String expected = String.format("%s, by %s (%d, %s)\n",
                "EmptyAlbum", "NoArtist", 2000, "Jazz");
        // No songs appended
        assertEquals(expected, album.toString(), "toString() should return only album info when there are no songs.");
    }

    /**
     * Test toString() with songs.
     */
    @Test
    public void testToStringWithSongs() {
        List<Song> songList = new ArrayList<>();
        songList.add(createSong("Song1"));
        songList.add(createSong("Song2"));
        Album album = new Album("FullAlbum", "ArtistFull", "Blues", 1995, songList);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s, by %s (%d, %s)\n", "FullAlbum", "ArtistFull", 1995, "Blues"));
        for (Song s : songList) {
            sb.append("  ").append(s.getTitle()).append("\n");
        }
        assertEquals(sb.toString(), album.toString(), "toString() should include album header and each song title on a new line prefixed by two spaces.");
    }

    /**
     * Test getAlbumInfoString() returns the proper formatted string.
     */
    @Test
    public void testGetAlbumInfoString() {
        List<Song> songList = new ArrayList<>();
        Album album = new Album("InfoAlbum", "InfoArtist", "Country", 1988, songList);
        String expected = String.format("%s, by %s (%d, %s)\n", "InfoAlbum", "InfoArtist", 1988, "Country");
        assertEquals(expected, album.getAlbumInfoString(), "getAlbumInfoString() should return a correctly formatted album header.");
    }

    /**
     * Test getAlbumInfo() returns the correct list.
     */
    @Test
    public void testGetAlbumInfo() {
        List<Song> songList = new ArrayList<>();
        Album album = new Album("InfoListAlbum", "ListArtist", "HipHop", 2010, songList);
        ArrayList<String> expected = new ArrayList<>();
        expected.add("InfoListAlbum");
        expected.add("ListArtist");
        expected.add(String.valueOf(2010));
        expected.add("HipHop");

        assertEquals(expected, album.getAlbumInfo(), "getAlbumInfo() should return album info in a list: title, artist, year, genre.");
    }

    @Test
    public void getGenre() {
        List<Song> songList = new ArrayList<>();
        Album album = new Album("InfoListAlbum", "ListArtist", "HipHop", 2010, songList);
        assertEquals("HipHop", album.getGenre());
    }

    @Test
    public void getYear() {
        List<Song> songList = new ArrayList<>();
        Album album = new Album("InfoListAlbum", "ListArtist", "HipHop", 2010, songList);
        assertEquals(2010, album.getYear());
    }

    /**
     * Test toStringList() returns album info followed by song titles.
     */
    @Test
    public void testToStringList() {
        List<Song> songList = new ArrayList<>();
        songList.add(createSong("First"));
        songList.add(createSong("Second"));
        Album album = new Album("ListAlbum", "ListArtist", "Electronic", 2022, songList);

        ArrayList<String> expected = new ArrayList<>();
        expected.add("ListAlbum");
        expected.add("ListArtist");
        expected.add(String.valueOf(2022));
        expected.add("Electronic");
        expected.add("First");
        expected.add("Second");

        assertEquals(expected, album.toStringList(), "toStringList() should return album info followed by the titles of the songs.");
    }
}