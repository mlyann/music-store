package la1;


import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class SongTest {
    private static class DummyAlbum extends Album {
        public DummyAlbum(String title) {
            super(title, "DummyArtist", "DummyGenre", 2020, new ArrayList<Song>());
        }
    }

    @Test
    public void testSongWithAlbum() {
        Song song = new Song("Test Song", "Test Artist", "Rock", 1999);
        assertNull(song.getAlbum(), "Album should initially be null.");
        DummyAlbum album = new DummyAlbum("Test Album");
        song.setAlbum(album);
        assertEquals("Test Album", song.getAlbum());
    }
    @Test
    public void testCopyConstructorWithAlbum() {
        DummyAlbum album = new DummyAlbum("Copy Album");
        Song original = new Song("Original Song", "Artist", "Jazz", 1985);
        original.setAlbum(album);
        original.setFavourite(true);
        original.setRating(Rating.FOUR);
        Song copy = new Song(original);
        assertEquals("Copy Album", copy.getAlbum());
        assertEquals(original.getFavourite(), copy.getFavourite());
        assertEquals(original.getRating(), copy.getRating());
    }

    @Test
    public void testSimpleConstructorDefaults() {
        Song song = new Song("Simple Song", "Simple Artist");
        assertEquals("Simple Song", song.getTitle());
        assertEquals("Simple Artist", song.getArtist());
        assertEquals(Genre.UNKNOWN.getGenre(), song.getGenre());
        assertEquals(0, song.getYear());
    }

    @Test
    public void testSetRatingNull() {
        Song song = new Song("Rating Test", "Artist");
        Exception e = assertThrows(IllegalArgumentException.class, () -> song.setRating(null));
        assertEquals("Rating cannot be null. Use Rating.UNRATED to represent no rating.", e.getMessage());
    }

    @Test
    public void testFavouriteToggle() {
        Song song = new Song("Fav Song", "Artist");
        assertFalse(song.isFavourite());
        assertEquals("♡", song.getFavourite());
        song.setFavourite(true);
        assertTrue(song.isFavourite());
        assertEquals("♥", song.getFavourite());
        song.changeFavourite();
        assertFalse(song.isFavourite());
        assertEquals("♡", song.getFavourite());
    }

    @Test
    public void testToStringListWithoutAndWithAlbum() {
        Song song = new Song("List Song", "Artist", "Classical", 2000);
        ArrayList<String> listWithoutAlbum = song.toStringList();
        assertEquals("List Song", listWithoutAlbum.get(0));
        assertEquals("Artist", listWithoutAlbum.get(1));
        assertEquals("Classical", listWithoutAlbum.get(2));
        assertEquals("2000", listWithoutAlbum.get(3));
        assertEquals("♡", listWithoutAlbum.get(4));
        assertEquals(Rating.UNRATED.toString(), listWithoutAlbum.get(5));
        assertNull(listWithoutAlbum.get(6), "Album entry should be null when no album is set.");
        DummyAlbum album = new DummyAlbum("List Album");
        song.setAlbum(album);
        ArrayList<String> listWithAlbum = song.toStringList();
        assertEquals("List Album", listWithAlbum.get(6), "Album entry should show the DummyAlbum title.");
    }
    @Test
    public void testToStringWithoutAndWithAlbum() {
        Song song = new Song("My Song", "My Artist", "Pop", 2020);
        String expectedWithoutAlbum = String.format("[%s, by %s (%s, %d) [%s, %s]",
                "My Song", "My Artist", "Pop", 2020, "♡", Rating.UNRATED.toString());
        String actual = song.toString();
        assertEquals(expectedWithoutAlbum, actual);
        Album album = new Album("My Album", "Album Artist", "Pop", 2020, java.util.Collections.emptyList());
        song.setAlbum(album);
        String expectedWithAlbum = expectedWithoutAlbum + " [" + album.getTitle() + "]";
        actual = song.toString();
        assertEquals(expectedWithAlbum, actual);
    }

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