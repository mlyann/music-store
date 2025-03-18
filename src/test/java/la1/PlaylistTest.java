package la1;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaylistTest {

    private Playlist playlist;
    private Song songWithAlbum;
    private Song songWithoutAlbum;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private Song createSong(String title, String artist, String genre, int year) {
        return new Song(title, artist, genre, year);
    }
    private Album createAlbum(String title, String artist, String genre, int year, List<Song> songs) {
        return new Album(title, artist, genre, year, songs);
    }

    @BeforeEach
    public void setUp() {
        playlist = new Playlist("Test Playlist");
        System.setOut(new PrintStream(outContent));
        songWithoutAlbum = createSong("No Album Song", "Artist1", "Rock", 2020);
        songWithAlbum = createSong("With Album Song", "Artist2", "Pop", 2021);
        Album album = createAlbum("Test Album", "Album Artist", "Pop", 2021, new ArrayList<>());
        songWithAlbum.setAlbum(album);
    }

    @Test
    public void testGetName() {
        assertEquals("Test Playlist", playlist.getName());
    }
    @Test
    public void testPrintAsTableWithoutAnyAlbum() {
        boolean auto = false;
        playlist.addSong(songWithoutAlbum, auto);
        outContent.reset();
        playlist.printAsTable("title");
        String output = outContent.toString();
        assertFalse(output.contains("THE ALBUM"));
        assertFalse(output.contains("NO ALBUM"));
    }

    @Test
    public void testPrintAsTableWithAlbumColumnWhenAlbumInfoPresent() {
        playlist.addSong(songWithAlbum,false);
        outContent.reset();
        playlist.printAsTable("title");
        String output = outContent.toString();
        assertTrue(output.contains("THE ALBUM"));
        assertTrue(output.contains("Test Album"));
    }

    @Test
    public void testPrintAsTableWithAlbumColumnWhenAlbumInfoMissing() {
        playlist.addSong(songWithoutAlbum,false);
        Song songCustom = new Song("Custom Song", "Artist3", "Jazz", 2019) {
            @Override
            public ArrayList<String> toStringList() {
                ArrayList<String> list = new ArrayList<>(Arrays.asList(
                        getTitle(), getArtist(), getGenre(), String.valueOf(getYear()),
                        getFavourite(), getRating(), null
                ));
                return list;
            }
        };
        playlist.addSong(songCustom,false);
        outContent.reset();
        playlist.printAsTable("title");
        String output = outContent.toString();
        assertFalse(output.contains("THE ALBUM"));
    }
    @Test
    public void testGetSongsAndSize() {
        assertEquals(0, playlist.getSize());
        playlist.addSong(songWithoutAlbum,false);
        assertEquals(1, playlist.getSize());
        assertTrue(playlist.getSongs().contains(songWithoutAlbum));
    }

    @Test
    public void testInsertSong() {
        playlist.insertSong(songWithoutAlbum);
        assertEquals(songWithoutAlbum, playlist.getSongs().get(0));
    }

    @Test
    public void testGetPlayingEmpty() {
        playlist.getPlaying();
        assertTrue(outContent.toString().contains("The playlist is empty."));
        outContent.reset();
    }

    @Test
    public void testGetPlayingNonEmpty() {
        playlist.insertSong(songWithoutAlbum);
        playlist.getPlaying();
        assertTrue(outContent.toString().contains("Now playing: [No Album Song]"));
        outContent.reset();
    }

    @Test
    public void testPlayNextEmpty() {
        playlist.playNext();
        assertTrue(outContent.toString().contains("No song to play next."));
        outContent.reset();
    }

    @Test
    public void testPlayNextSingleSong() {
        playlist.insertSong(songWithoutAlbum);
        playlist.playNext();
        assertTrue(outContent.toString().contains("No song to play next."));
        outContent.reset();
    }

    @Test
    public void testPlayNextMultipleSongs() {
        playlist.insertSong(songWithoutAlbum);
        playlist.insertSong(songWithAlbum);
        outContent.reset();
        playlist.playNext();
        assertTrue(outContent.toString().contains("Playing: [No Album Song]"));
        outContent.reset();
    }

    @Test
    public void testAddSongNew() {
        playlist.addSong(songWithoutAlbum, true);
        assertTrue(outContent.toString().contains("Song [No Album Song] added to the playlist."));
        outContent.reset();
    }

    @Test
    public void testAddSongExisting() {
        playlist.addSong(songWithoutAlbum, true);
        outContent.reset();
        playlist.addSong(songWithoutAlbum, true);
        assertTrue(outContent.toString().contains("Song [No Album Song] is already in the playlist."));
        outContent.reset();
    }

    @Test
    public void testRemoveSongExisting() {
        playlist.addSong(songWithoutAlbum, true);
        outContent.reset();
        playlist.removeSong(songWithoutAlbum);
        assertTrue(outContent.toString().contains("Song [No Album Song] removed from the playlist."));
        outContent.reset();
    }

    @Test
    public void testRemoveSongNonExisting() {
        playlist.addSong(songWithoutAlbum, true);
        outContent.reset();
        playlist.removeSong(songWithAlbum);
        assertTrue(outContent.toString().contains("Song [With Album Song] not found in the playlist."));
        outContent.reset();
    }

    @Test
    public void testClear() {
        playlist.addSong(songWithoutAlbum, true);
        playlist.addSong(songWithAlbum, true);
        outContent.reset();
        playlist.clear(true);
        assertEquals(0, playlist.getSize());
        assertTrue(outContent.toString().contains("The playlist has been cleared."));
        outContent.reset();
    }

    @Test
    public void testPrintAsTableEmpty() {
        playlist.printAsTable("title");
        assertTrue(outContent.toString().contains("The playlist is empty."));
        outContent.reset();
    }

    @Test
    public void testPrintAsTableNonEmpty() {
        playlist.addSong(songWithoutAlbum, true);
        playlist.addSong(songWithAlbum, false);
        outContent.reset();
        playlist.printAsTable("title");
        String output = outContent.toString();
        assertTrue(output.contains("THE ALBUM"));
        assertTrue(output.contains("Test Album"));
        assertTrue(output.contains("NO ALBUM"));
        outContent.reset();
    }

    @Test
    public void testShuffleEmpty() {
        playlist.shuffle();
        assertTrue(outContent.toString().contains("The playlist is empty."));
        outContent.reset();
    }

    @Test
    public void testShuffleNonEmpty() {
        playlist.addSong(songWithoutAlbum, false);
        playlist.addSong(songWithAlbum, false);
        outContent.reset();
        playlist.shuffle();
        String output = outContent.toString();
        assertTrue(output.contains("Shuffling the playlist..."));
        assertTrue(output.contains("The playlist has been shuffled."));
        outContent.reset();
    }

    @Test
    public void testToStringEmpty() {
        assertEquals("The playlist is empty.", playlist.toString());
    }

    @Test
    public void testToStringNonEmpty() {
        playlist.insertSong(songWithoutAlbum);
        playlist.insertSong(songWithAlbum);
        String str = playlist.toString();
        assertTrue(str.contains("1) With Album Song"));
        assertTrue(str.contains("2) No Album Song"));
    }


}