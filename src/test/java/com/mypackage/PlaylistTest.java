package la1;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TablePrinter {
    public static void printDynamicTable(String name, ArrayList<ArrayList<String>> rows) {
        // For testing, simply print a header that includes the table name.
        System.out.println("Table: " + name);
        for (ArrayList<String> row : rows) {
            System.out.println(String.join(" | ", row));
        }
    }
}

public class PlaylistTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        outContent.reset();
    }

    // Test getSongs on an empty playlist
    @Test
    public void testGetSongsEmpty() {
        Playlist playlist = new Playlist();
        assertTrue(playlist.getSongs().isEmpty(), "Playlist should be empty initially");
    }

    // Test getPlaying on empty playlist
    @Test
    public void testGetPlayingEmpty() {
        Playlist playlist = new Playlist();
        playlist.getPlaying();
        String expected = "The playlist is empty.";
        assertTrue(outContent.toString().trim().contains(expected));
    }

    // Test getPlaying
    @Test
    public void testGetPlayingNonEmpty() {
        Playlist playlist = new Playlist();
        Song song = new Song("Song A", "Artist A", "Pop", 2020);
        playlist.insertSong(song);
        outContent.reset();
        playlist.getPlaying();
        String expected = "Now playing: [Song A]";
        assertTrue(outContent.toString().trim().contains(expected));
    }

    // Test playNext
    @Test
    public void testPlayNextEmpty() {
        Playlist playlist = new Playlist();
        playlist.playNext();
        String expected = "The playlist is empty.";
        assertTrue(outContent.toString().trim().contains(expected));
    }

    // Test playNext on non-empty playlist and ensure song removal
    @Test
    public void testPlayNextNonEmpty() {
        Playlist playlist = new Playlist();
        Song song = new Song("Song B", "Artist B", "Rock", 2019);
        playlist.insertSong(song);
        outContent.reset();
        playlist.playNext();
        String expected = "Playing: [Song B]";
        assertTrue(outContent.toString().trim().contains(expected));
        assertTrue(playlist.getSongs().isEmpty(), "Song should be removed after playNext");
    }
//    test dup
    @Test
    public void testAddSong() {
        Playlist playlist = new Playlist();
        Song song = new Song("Song C", "Artist C", "Jazz", 2018);
        playlist.addSong(song);
        String expectedAdded = "Song [Song C] added to the playlist.";
        assertTrue(outContent.toString().trim().contains(expectedAdded));

        outContent.reset();
        playlist.addSong(song);
        String expectedDuplicate = "Song [Song C] is already in the playlist.";
        assertTrue(outContent.toString().trim().contains(expectedDuplicate));
    }

    // Test removeSong for song present and not present
    @Test
    public void testRemoveSong() {
        Playlist playlist = new Playlist();
        Song song = new Song("Song D", "Artist D", "Hip-Hop", 2021);
        // Removing a song that is not in the playlist
        playlist.removeSong(song);
        String expectedNotFound = "Song [Song D] not found in the playlist.";
        assertTrue(outContent.toString().trim().contains(expectedNotFound));

        outContent.reset();
        // Add then remove the song
        playlist.addSong(song);
        outContent.reset();
        playlist.removeSong(song);
        String expectedRemoved = "Song [Song D] removed from the playlist.";
        assertTrue(outContent.toString().trim().contains(expectedRemoved));
    }

    // Test clear: add some songs then clear the playlist
    @Test
    public void testClear() {
        Playlist playlist = new Playlist();
        playlist.addSong(new Song("Song E", "Artist E", "Classical", 2015));
        playlist.addSong(new Song("Song F", "Artist F", "Pop", 2016));
        outContent.reset();
        playlist.clear();
        String expected = "The playlist has been cleared.";
        assertTrue(outContent.toString().trim().contains(expected));
        assertTrue(playlist.getSongs().isEmpty(), "Playlist should be empty after clear");
    }

    // Test toString for empty and non-empty playlist
    @Test
    public void testToString() {
        Playlist playlist = new Playlist();
        // For empty playlist
        assertEquals("The playlist is empty.", playlist.toString().trim());

        // For non-empty playlist
        Song song1 = new Song("Song G", "Artist G", "Rock", 2017);
        Song song2 = new Song("Song H", "Artist H", "Electronic", 2022);
        playlist.addSong(song1);
        playlist.addSong(song2);
        String toStringOutput = playlist.toString();
        assertTrue(toStringOutput.contains("Song G") || toStringOutput.contains("Song H"),
                "toString should include song titles");
    }

    // Test printAsTable for empty playlist branch
    @Test
    public void testPrintAsTableEmpty() {
        Playlist playlist = new Playlist();
        playlist.printAsTable("TestTable");
        String expected = "The playlist is empty.";
        assertTrue(outContent.toString().trim().contains(expected));
    }

    @Test
    public void testPrintAsTableNonEmpty() {
        Playlist playlist = new Playlist();
        Song song = new Song("Song I", "Artist I", "Indie", 2014);
        playlist.addSong(song);
        outContent.reset();
        playlist.printAsTable("MyPlaylist");
        String output = outContent.toString();
        assertTrue(output.contains("Table: MyPlaylist"), "Output should contain the table header");
        assertTrue(output.contains("Song I"), "Output should contain the song title");
    }
}