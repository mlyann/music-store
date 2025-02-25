package la1;


import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class SongTest {

    // A dummy Album for test cases
    private static class DummyAlbum extends Album {
        public DummyAlbum(String title) {
            // Provide dummy values for artist, genre, year, and an empty list for songs.
            super(title, "DummyArtist", "DummyGenre", 2020, new ArrayList<Song>());
        }
    }

    /**
     * Test that a Song without an album returns null, then setting an album returns the proper title.
     */
    @Test
    public void testSongWithAlbum() {
        Song song = new Song("Test Song", "Test Artist", "Rock", 1999);
        assertNull(song.getAlbum(), "Album should initially be null.");
        // Set album using DummyAlbum.
        DummyAlbum album = new DummyAlbum("Test Album");
        song.setAlbum(album);
        // Verify that the album title is returned properly.
        assertEquals("Test Album", song.getAlbum(), "Album title should be returned from the DummyAlbum.");
    }

    /**
     * Test the copy constructor when the Song has an album.
     */
    @Test
    public void testCopyConstructorWithAlbum() {
        DummyAlbum album = new DummyAlbum("Copy Album");
        Song original = new Song("Original Song", "Artist", "Jazz", 1985);
        original.setAlbum(album);
        original.setFavourite(true);
        original.setRating(Rating.FOUR); // Assuming Rating.FOUR exists.

        Song copy = new Song(original);
        assertEquals("Copy Album", copy.getAlbum(), "Copied song should have the same album title.");
        // Verify that other properties are copied correctly.
        assertEquals(original.getFavourite(), copy.getFavourite(), "Favourite status should be copied.");
        assertEquals(original.getRating(), copy.getRating(), "Rating should be copied.");
    }

    /**
     * Test basic Song creation and default behaviors.
     */
    @Test
    public void testSimpleConstructorDefaults() {
        Song song = new Song("Simple Song", "Simple Artist");
        assertEquals("Simple Song", song.getTitle());
        assertEquals("Simple Artist", song.getArtist());
        // When genre is not provided, it should be set to Genre.UNKNOWN.
        assertEquals(Genre.UNKNOWN.getGenre(), song.getGenre());
        assertEquals(0, song.getYear());
    }

    /**
     * Test that setting a null rating throws an exception.
     */
    @Test
    public void testSetRatingNull() {
        Song song = new Song("Rating Test", "Artist");
        Exception e = assertThrows(IllegalArgumentException.class, () -> song.setRating(null));
        assertEquals("Rating cannot be null. Use Rating.UNRATED to represent no rating.", e.getMessage());
    }

    /**
     * Test the favorite toggle methods.
     */
    @Test
    public void testFavouriteToggle() {
        Song song = new Song("Fav Song", "Artist");
        // Initially not favorite.
        assertFalse(song.isFavourite(), "Song should not be favorite by default.");
        assertEquals("♡", song.getFavourite(), "Default favorite symbol should be ♡.");

        // Set favorite explicitly.
        song.setFavourite(true);
        assertTrue(song.isFavourite(), "Song should be favorite after setting.");
        assertEquals("♥", song.getFavourite(), "Favorite symbol should be ♥ after setting true.");

        // Toggle favorite status.
        song.changeFavourite();
        assertFalse(song.isFavourite(), "Song favorite status should be toggled to false.");
        assertEquals("♡", song.getFavourite(), "Favorite symbol should be ♡ after toggling.");
    }

    /**
     * Test the toStringList method to verify that null album is handled correctly.
     */
    @Test
    public void testToStringListWithoutAndWithAlbum() {
        Song song = new Song("List Song", "Artist", "Classical", 2000);
        // Without album.
        ArrayList<String> listWithoutAlbum = song.toStringList();
        assertEquals("List Song", listWithoutAlbum.get(0));
        assertEquals("Artist", listWithoutAlbum.get(1));
        assertEquals("Classical", listWithoutAlbum.get(2));
        assertEquals("2000", listWithoutAlbum.get(3));
        assertEquals("♡", listWithoutAlbum.get(4));
        assertEquals(Rating.UNRATED.toString(), listWithoutAlbum.get(5));
        assertNull(listWithoutAlbum.get(6), "Album entry should be null when no album is set.");

        // With album.
        DummyAlbum album = new DummyAlbum("List Album");
        song.setAlbum(album);
        ArrayList<String> listWithAlbum = song.toStringList();
        assertEquals("List Album", listWithAlbum.get(6), "Album entry should show the DummyAlbum title.");
    }

    /**
     * Test the toString() method of Song when album is not set and when it is set.
     */
    @Test
    public void testToStringWithoutAndWithAlbum() {
        Song song = new Song("My Song", "My Artist", "Pop", 2020);
        String expectedWithoutAlbum = String.format("[%s, by %s (%s, %d) [%s, %s]",
                "My Song", "My Artist", "Pop", 2020, "♡", Rating.UNRATED.toString());
        String actual = song.toString();
        assertEquals(expectedWithoutAlbum, actual, "toString() should not append album info if album is null.");
        Album album = new Album("My Album", "Album Artist", "Pop", 2020, java.util.Collections.emptyList());
        song.setAlbum(album);
        String expectedWithAlbum = expectedWithoutAlbum + " [" + album.getTitle() + "]";
        actual = song.toString();
        assertEquals(expectedWithAlbum, actual, "toString() should append album info when album is set.");
    }

    /**
     * Test that the Song constructor throws an IllegalArgumentException
     * when title or artist is null or blank.
     */
    @Test
    public void testConstructorValidation() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> new Song(null, "Some Artist"));
        assertEquals("Title and artist are required.", ex.getMessage());
        ex = assertThrows(IllegalArgumentException.class, () -> new Song("   ", "Some Artist"));
        assertEquals("Title and artist are required.", ex.getMessage());
        ex = assertThrows(IllegalArgumentException.class, () -> new Song("Some Title", null));
        assertEquals("Title and artist are required.", ex.getMessage());
        ex = assertThrows(IllegalArgumentException.class, () -> new Song("Some Title", "   "));
        assertEquals("Title and artist are required.", ex.getMessage());
    }
}