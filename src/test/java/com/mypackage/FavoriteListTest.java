package la1;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Test cases for FavoriteList, aiming for full coverage.
 */
public class FavoriteListTest {

    /**
     * Test that adding a single song increases the favorite list size.
     */
    @Test
    public void testAddSongIncreasesSize() {
        FavoriteList favList = new FavoriteList();
        Song song = new Song("Title1", "Artist1", "Pop", 2020);
        assertEquals(0, favList.getSize());
        favList.addSong(song);
        assertEquals(1, favList.getSize());
    }

    /**
     * Test that removing a song decreases the favorite list size.
     */
    @Test
    public void testRemoveSongDecreasesSize() {
        FavoriteList favList = new FavoriteList();
        Song song = new Song("Title1", "Artist1", "Pop", 2020);
        favList.addSong(song);
        assertEquals(1, favList.getSize());
        favList.removeSong(song);
        assertEquals(0, favList.getSize());
    }

    /**
     * Test the toString method on an empty FavoriteList.
     */
    @Test
    public void testToStringEmptyList() {
        FavoriteList favList = new FavoriteList();
        String output = favList.toString();
        assertEquals("The favorite list is empty.", output);
    }

    /**
     * Test the toString method on a non-empty FavoriteList.
     */
    @Test
    public void testToStringWithSongs() {
        FavoriteList favList = new FavoriteList();
        Song song1 = new Song("Title1", "Artist1", "Pop", 2020);
        Song song2 = new Song("Title2", "Artist2", "Rock", 2019);
        favList.addSong(song1);
        favList.addSong(song2);

        String output = favList.toString();
        // Expected string starts with header and each song on a new line with index.
        String expectedPattern = "Favorite Songs:\\s*1\\) Title1\\s*2\\) Title2\\s*";
        assertTrue(Pattern.compile(expectedPattern).matcher(output).find());
    }

    /**
     * Test printAsTable method when the favorite list is empty.
     */
    @Test
    public void testPrintAsTableEmpty() {
        FavoriteList favList = new FavoriteList();
        // Capture printed output.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(baos));

        favList.printAsTable();

        System.out.flush();
        System.setOut(originalOut);

        String printed = baos.toString();
        assertTrue(printed.contains("The playlist is empty."));
    }

    /**
     * Test printAsTable method with songs that do not have album information.
     */
    @Test
    public void testPrintAsTableWithoutAlbum() {
        FavoriteList favList = new FavoriteList();
        Song song1 = new Song("Title1", "Artist1", "Pop", 2020);
        Song song2 = new Song("Title2", "Artist2", "Rock", 2019);
        favList.addSong(song1);
        favList.addSong(song2);

        // Ensure songs do not have album set.
        song1.setAlbum(null);
        song2.setAlbum(null);

        // Capture printed output.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(baos));

        favList.printAsTable();

        System.out.flush();
        System.setOut(originalOut);

        String printed = baos.toString();
        // Check for expected headers.
        assertTrue(printed.contains("No."));
        assertTrue(printed.contains("Title"));
        assertTrue(printed.contains("Artist"));
        assertTrue(printed.contains("Genre"));
        assertTrue(printed.contains("Year"));
        assertTrue(printed.contains("Favorite"));
        assertTrue(printed.contains("Rating"));
        // Should not include "Album" in the header.
        assertFalse(printed.contains("ALBUM"));
    }

    /**
     * Test printAsTable method with songs where at least one song has album information.
     */
    @Test
    public void testPrintAsTableWithAlbum() {
        FavoriteList favList = new FavoriteList();
        // Song without album.
        Song song1 = new Song("Title1", "Artist1", "Pop", 2020);
        // Song with album info.
        Song song2 = new Song("Title2", "Artist2", "Rock", 2019);
        song2.setAlbum(new DummyAlbum("BestOf"));
        favList.addSong(song1);
        favList.addSong(song2);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(baos));

        favList.printAsTable();
        System.out.flush();
        System.setOut(originalOut);
        String printed = baos.toString();
        assertTrue(printed.contains("Album"));
        assertTrue(printed.contains("BestOf"));
    }

    /**
     * Test multiple operations: adding several songs, removing one, and verifying state.
     */
    @Test
    public void testMultipleOperations() {
        FavoriteList favList = new FavoriteList();
        Song song1 = new Song("Title1", "Artist1", "Pop", 2020);
        Song song2 = new Song("Title2", "Artist2", "Rock", 2019);
        Song song3 = new Song("Title3", "Artist3", "Jazz", 2021);

        // Set album for song1 and song3.
        song1.setAlbum(new DummyAlbum("Album1"));
        song3.setAlbum(new DummyAlbum("Album3"));

        favList.addSong(song1);
        favList.addSong(song2);
        favList.addSong(song3);
        assertEquals(3, favList.getSize());

        // Remove the second song.
        favList.removeSong(song2);
        assertEquals(2, favList.getSize());

        String output = favList.toString();
        // Verify that output contains song1 and song3, but not song2.
        assertTrue(output.contains("Title1"));
        assertTrue(output.contains("Title3"));
        assertFalse(output.contains("Title2"));
    }

    /**
     * Test case specifically to verify the extraction of the album information from the song's toStringList.
     * This tests the line:
     */
    @Test
    public void testAlbumColumnExtraction() {
        FavoriteList favList = new FavoriteList();
        // Create a dummy song whose toStringList returns only 6 elements (no album info)
        DummySongShort songShort = new DummySongShort("ShortSong", "ArtistX");
        DummySongLong songLong = new DummySongLong("LongSong", "ArtistY", "DummyAlbum");
        favList.addSong(songShort);
        favList.addSong(songLong);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(baos));
        favList.printAsTable();
        System.out.flush();
        System.setOut(originalOut);

        String printed = baos.toString();
        assertTrue(printed.contains("Album"));
        assertTrue(printed.contains("DummyAlbum"));
    }

    /**
     * DummyAlbum class for testing purposes.
     * This updated DummyAlbum provides default values for artist, genre, year, and an empty song list.
     */
    private static class DummyAlbum extends Album {
        public DummyAlbum(String title) {
            super(title, "DummyArtist", "Unknown", 0, new ArrayList<Song>());
        }

        @Override
        public String getTitle() {
            return super.getTitle();
        }
    }

    /**
     * DummySongShort simulates a Song whose toStringList returns only 6 elements,
     * thereby not providing album information.
     */
    private static class DummySongShort extends Song {
        public DummySongShort(String title, String artist) {
            super(title, artist, "Genre", 2000);
        }

        @Override
        public ArrayList<String> toStringList() {
            ArrayList<String> list = new ArrayList<>();
            list.add(getTitle());
            list.add(getArtist());
            list.add(getGenre());
            list.add(String.valueOf(getYear()));
            list.add(getFavourite());
            list.add(getRating());
            return list;
        }
    }

    /**
     * DummySongLong simulates a Song whose toStringList returns 7 elements,
     * including valid album information.
     * MING: This DummySong is only used here in FavoriteListTest, nowhere else.
     */
    private static class DummySongLong extends Song {
        public DummySongLong(String title, String artist, String albumTitle) {
            super(title, artist, "Genre", 2000);
            // Set album using DummyAlbum.
            setAlbum(new DummyAlbum(albumTitle));
        }

        @Override
        public ArrayList<String> toStringList() {
            ArrayList<String> list = new ArrayList<>();
            list.add(getTitle());
            list.add(getArtist());
            list.add(getGenre());
            list.add(String.valueOf(getYear()));
            list.add(getFavourite());
            list.add(getRating());
            if (getAlbum() != null) {
                list.add(getAlbum());
            } else {
                list.add(null);
            }
            return list;
        }
    }
}