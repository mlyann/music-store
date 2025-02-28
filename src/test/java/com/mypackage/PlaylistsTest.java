package la1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PlaylistsTest {
    private PlayLists playLists;
    private Song testA;
    private Song testB;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        playLists = new PlayLists();
        testA = new Song("A", "AA", "Rock", 2020);
        testB = new Song("B", "BB", "Pop", 2021);
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testAddPlayList() {
        assertTrue(playLists.addPlayList("1"));
        assertFalse(playLists.addPlayList("1"));
        assertEquals(1, playLists.getSize());
    }

    @Test
    public void testRemovePlayList() {
        playLists.addPlayList("1");
        playLists.addPlayList("2");
        assertTrue(playLists.removePlayList(0));
        assertEquals(1, playLists.getSize());
    }

    @Test
    public void testGetPlayList() {
        playLists.addPlayList("1");
        assertNotNull(playLists.getPlayList(0));
        assertNull(playLists.getPlayList(9));
    }

    @Test
    public void testGetSize() {
        assertEquals(0, playLists.getSize());
        playLists.addPlayList("1");
        assertEquals(1, playLists.getSize());
    }

    @Test
    public void testAddSongToPlayList() {
        playLists.addPlayList("1");
        playLists.addSongToPlayList("1", testA);
        Playlist playlist = playLists.getPlayList(0);
        assertEquals(1, playlist.getSize());
        assertEquals("A", playlist.getSongs().get(0).getTitle());
        System.out.println("Song [A] added to the playlist.");
    }

    @Test
    public void testRemoveSongFromPlayList() {
        playLists.addPlayList("1");
        playLists.addSongToPlayList("1", testA);
        playLists.removeSongFromPlayList("1", testA);
        Playlist playlist = playLists.getPlayList(0);
        assertEquals(0, playlist.getSize());
    }

    @Test
    public void testClearAllPlayLists() {
        playLists.addPlayList("1");
        playLists.addPlayList("2");
        playLists.clearAllPlayLists();
        assertEquals(0, playLists.getSize());
    }

    @Test
    public void testGetPlayListNames() {
        playLists.addPlayList("1");
        playLists.addPlayList("2");
        ArrayList<String> playListNames = playLists.getPlayListNames();
        assertEquals(2, playListNames.size());
        assertTrue(playListNames.contains("1"));
        assertTrue(playListNames.contains("2"));
    }

    @Test
    public void testAddSongToNonList() {
        playLists.addSongToPlayList("9", testA);
        assertTrue(outContent.toString().contains("Playlist [9] does not exist."));
    }

    @Test
    public void testRemoveSongFromOutBoundary() {
        playLists.removeSongFromPlayList("9", testA);
        assertTrue(outContent.toString().contains("Playlist [9] does not exist."));
    }
}
