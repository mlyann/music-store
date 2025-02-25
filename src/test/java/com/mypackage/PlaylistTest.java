package la1;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import la1.TablePrinter;

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
        String expected = "No song to play next.";
        assertTrue(outContent.toString().trim().contains(expected));
    }

    // Test playNext on non-empty playlist and ensure song removal
    @Test
    public void testPlayNextNonEmpty() {
        Playlist playlist = new Playlist();
        Song song1 = new Song("Song A", "Artist A", "Pop", 2020);
        Song song2 = new Song("Song B", "Artist B", "Rock", 2019);

        playlist.addSong(song1);
        playlist.addSong(song2);

        outContent.reset();
        playlist.playNext();

        String expected = "Playing: [Song B]";
        assertTrue(outContent.toString().trim().contains(expected));

        assertEquals(1, playlist.getSongs().size(), "Song list should contain only one song after playNext");
        assertEquals(song2, playlist.getSongs().get(0), "The remaining song should be 'Song B'");
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
        assertTrue(output.contains("           🎉 " + "MyPlaylist" + " 🎉              "), "Output should contain the table header");
        assertTrue(output.contains("Song I"), "Output should contain the song title");
    }

    // Test playNext with only one song in the playlist
    @Test
    public void testPlayNextSingleSong() {
        Playlist playlist = new Playlist();
        Song song = new Song("Song Z", "Artist Z", "Blues", 2023);
        playlist.addSong(song);

        outContent.reset();
        playlist.playNext();

        String expected = "No song to play next.";
        assertTrue(outContent.toString().trim().contains(expected));
        assertEquals(1, playlist.getSongs().size(), "Song should not be removed when only one song exists");
    }

    // Test insertSong into an empty playlist
    @Test
    public void testInsertSongIntoEmptyPlaylist() {
        Playlist playlist = new Playlist();
        Song song = new Song("Song Y", "Artist Y", "Country", 2022);

        outContent.reset();
        playlist.insertSong(song);

        assertEquals(1, playlist.getSongs().size(), "Playlist should contain one song after insert");
        assertEquals(song, playlist.getSongs().get(0), "Inserted song should be the first song in the playlist");
    }

    // Test shuffle with only one song in the playlist
    @Test
    public void testShuffleSingleSong() {
        Playlist playlist = new Playlist();
        Song song = new Song("Song X", "Artist X", "Reggae", 2021);
        playlist.addSong(song);

        outContent.reset();
        playlist.shuffle();

        String expected = "The playlist has been shuffled.";
        assertTrue(outContent.toString().trim().contains(expected));
        assertEquals(1, playlist.getSongs().size(), "Playlist should still contain the single song after shuffle");
    }

    // Test printAsTable with songs containing album information (covering the anyAlbum branch)
    @Test
    public void testPrintAsTableWithAlbum() {
        Playlist playlist = new Playlist();
        Song song = new Song("Song W", "Artist W", "Folk", 2020);
        playlist.addSong(song);

        outContent.reset();
        playlist.printAsTable("AlbumPlaylist");
        String output = outContent.toString();

        assertTrue(output.contains("🎉 AlbumPlaylist 🎉"), "Output should contain the table header with album info");
        assertTrue(output.contains("Song W"), "Output should contain the song title");
        assertTrue(output.contains("Artist W"), "Output should contain the album name");
    }
    @Test
    public void testShuffleEmptyPlaylist() {
        Playlist playlist = new Playlist();
        outContent.reset();
        playlist.shuffle();

        String expected = "The playlist is empty.";
        assertTrue(outContent.toString().trim().contains(expected));
    }
    @Test
    public void testPrintAsTableWithoutAlbum() {
        Playlist playlist = new Playlist();

        // Add songs without album information
        Song song1 = new Song("Song J", "Artist J");
        Song song2 = new Song("Song K", "Artist K");

        playlist.addSong(song1);
        playlist.addSong(song2);

        outContent.reset();
        playlist.printAsTable("NoAlbumPlaylist");
        String output = outContent.toString();

        assertTrue(output.contains("🎉 NoAlbumPlaylist 🎉"), "Output should contain the table header without album info");
        assertTrue(output.contains("Song J"), "Output should contain the first song title");
        assertTrue(output.contains("Song K"), "Output should contain the second song title");
        assertFalse(output.contains("The Album"), "Output should not contain the 'Album' column header");
    }
    @Test
    public void testPrintAsTableNoAlbumColumn() {
        Playlist playlist = new Playlist();

        // Add songs without album information
        Song song1 = new Song("Song L", "Artist L", "Classical", 2017);
        Song song2 = new Song("Song M", "Artist M", "Electronic", 2019);

        playlist.addSong(song1);
        playlist.addSong(song2);

        outContent.reset();
        playlist.printAsTable("SimplePlaylist");
        String output = outContent.toString();

        assertTrue(output.contains("🎉 SimplePlaylist 🎉"), "Output should contain the table header");
        assertTrue(output.contains("Song L"), "Output should contain the first song title");
        assertTrue(output.contains("Song M"), "Output should contain the second song title");
        assertFalse(output.contains("ALBUM"), "Output should not contain the 'Album' column header when no albums exist");
    }

    @Test
    public void testAddSongWithAlbum() {
        Playlist playlist = new Playlist();
        ArrayList<Song> array =  new ArrayList<Song>();
        // Initialize an album
        Album album = new Album("Greatest Hits", "Artist A", "Rock", 2020, array);

        // Create a song and associate it with the album
        Song song = new Song("Song A", "Artist A", "Rock", 2020);
        song.setAlbum(album);

        playlist.addSong(song);

        assertEquals(1, playlist.getSongs().size());
        assertEquals("Greatest Hits", playlist.getSongs().get(0).getAlbum());

        outContent.reset();
        playlist.printAsTable("WithAlbumTest");
        String output = outContent.toString();

        assertTrue(output.contains("THE ALBUM"), "Table should contain 'THE ALBUM' column header");
        assertTrue(output.contains("Greatest Hits"), "Output should contain album name 'Greatest Hits'");
    }

    /**
     * Test adding songs without album information
     */
    @Test
    public void testAddSongWithoutAlbum() {
        Playlist playlist = new Playlist();

        // Create a song without album information
        Song song = new Song("Song B", "Artist B", "Pop", 2021);

        playlist.addSong(song);

        assertEquals(1, playlist.getSongs().size());
        assertEquals(null, playlist.getSongs().get(0).getAlbum(), "Default album should be null");

        outContent.reset();
        playlist.printAsTable("NoAlbumTest");
        String output = outContent.toString();

        assertFalse(output.contains("THE ALBUM"), "Table should not contain 'THE ALBUM' column header");
    }

    /**
     * Test album integration with Playlist and Song using setAlbum
     */
    @Test
    public void testAlbumIntegrationWithPlaylist() {
        Playlist playlist = new Playlist();
        ArrayList<Song> array =  new ArrayList<Song>();

        // Initialize an album and associate it with songs
        Album album1 = new Album("Smooth Vibes", "Artist C", "Jazz", 2018, array);
        Album album2 = new Album("Classical Essentials", "Artist C", "Jazz", 2018, array);

        Song song1 = new Song("Song C", "Artist C", "Jazz", 2018);
        song1.setAlbum(album1);

        Song song2 = new Song("Song D", "Artist D", "Classical", 2019);
        song2.setAlbum(album2);

        playlist.addSong(song1);
        playlist.addSong(song2);

        assertEquals(2, playlist.getSongs().size(), "Playlist should contain two songs");

        outContent.reset();
        playlist.printAsTable("AlbumIntegrationTest");
        String output = outContent.toString();

        assertTrue(output.contains("THE ALBUM"), "Output should contain 'THE ALBUM' column header");
        assertTrue(output.contains("Smooth Vibes"), "Output should contain the album 'Smooth Vibes'");
        assertTrue(output.contains("Classical Essentials"), "Output should contain the album 'Classical Essentials'");
    }
    @Test
    public void testNullAlbumDisplaysAsNoAlbum() {
        Playlist playlist = new Playlist();
        ArrayList<Song> array = new ArrayList<Song>();

        // Initialize albums and songs
        Album album1 = new Album("Smooth Vibes", "Artist C", "Jazz", 2018, array);

        // Song with a valid album
        Song song1 = new Song("Song C", "Artist C", "Jazz", 2018);
        song1.setAlbum(album1);

        // Song with a null album (set explicitly to null)
        Song song2 = new Song("Song D", "Artist D", "Classical", 2019);
        song2.setAlbum(null); // Explicitly setting the album to null

        playlist.addSong(song1);
        playlist.addSong(song2);

        assertEquals(2, playlist.getSongs().size(), "Playlist should contain two songs");

        outContent.reset();
        playlist.printAsTable("NullAlbumTest");
        String output = outContent.toString();

        assertTrue(output.contains("THE ALBUM"), "Output should contain 'THE ALBUM' column header");
        assertTrue(output.contains("Smooth Vibes"), "Output should contain the album 'Smooth Vibes'");
        assertTrue(output.contains("NO ALBUM"), "Output should display 'NO ALBUM' for the song with a null album");
    }
}