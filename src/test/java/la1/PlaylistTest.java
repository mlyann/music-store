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
    @Test
    public void testSortSongsByTitle() {
        Song songA = createSong("Alpha", "ArtistZ", "Pop", 2018);
        Song songB = createSong("Alpha", "ArtistY", "Rock", 2019);
        Song songC = createSong("Beta", "ArtistX", "Jazz", 2020);
        playlist.addSong(songC, false);
        playlist.addSong(songB, false);
        playlist.addSong(songA, false);

        playlist.sortSongsByTitle();
        List<Song> sorted = playlist.getSongs();
        assertEquals("Alpha", sorted.get(0).getTitle());
        assertEquals("ArtistY", sorted.get(0).getArtist());
        assertEquals("Alpha", sorted.get(1).getTitle());
        assertEquals("ArtistZ", sorted.get(1).getArtist());
        assertEquals("Beta", sorted.get(2).getTitle());
    }

    @Test
    public void testSortSongsByRating() {
        Song song1 = createSong("Song 1", "ArtistA", "Pop", 2019);
        Song song2 = createSong("Song 2", "ArtistB", "Rock", 2018);
        Song song3 = createSong("Song 3", "ArtistC", "Jazz", 2020);
        // 假设 Rating 枚举中，FIVE > FOUR > THREE
        song1.setRating(Rating.THREE);
        song2.setRating(Rating.FIVE);
        song3.setRating(Rating.FOUR);

        playlist.addSong(song1, false);
        playlist.addSong(song2, false);
        playlist.addSong(song3, false);

        playlist.sortSongsByRating();
        List<Song> sorted = playlist.getSongs();
        assertEquals("Song 2", sorted.get(0).getTitle());
        assertEquals("Song 3", sorted.get(1).getTitle());
        assertEquals("Song 1", sorted.get(2).getTitle());
    }

    @Test
    public void testSortSongsByYear() {
        Song song1 = createSong("Song A", "ArtistA", "Pop", 2021);
        Song song2 = createSong("Song B", "ArtistB", "Rock", 2019);
        Song song3 = createSong("Song C", "ArtistC", "Jazz", 2020);

        playlist.addSong(song1, false);
        playlist.addSong(song2, false);
        playlist.addSong(song3, false);

        playlist.sortSongsByYear();
        List<Song> sorted = playlist.getSongs();
        assertEquals(2019, sorted.get(0).getYear());
        assertEquals(2020, sorted.get(1).getYear());
        assertEquals(2021, sorted.get(2).getYear());
    }

    @Test
    public void testPrintAsTableKeyRating() {
        Song song1 = createSong("Song A", "ArtistA", "Pop", 2020);
        Song song2 = createSong("Song B", "ArtistB", "Rock", 2021);
        song1.setRating(Rating.THREE);
        song2.setRating(Rating.FIVE);

        playlist.addSong(song1, false);
        playlist.addSong(song2, false);
        outContent.reset();

        playlist.printAsTable("rating");
        String output = outContent.toString();
        assertTrue(output.contains("Rating"));
    }

    @Test
    public void testPrintAsTableKeyYear() {
        Song song1 = createSong("Song A", "ArtistA", "Pop", 2020);
        Song song2 = createSong("Song B", "ArtistB", "Rock", 2018);

        playlist.addSong(song1, false);
        playlist.addSong(song2, false);
        outContent.reset();

        playlist.printAsTable("year");
        String output = outContent.toString();
        assertTrue(output.contains("Year"));
        assertTrue(output.indexOf("Song B") < output.indexOf("Song A"));
    }

    @Test
    public void testPrintAsTableKeyShuffle() {
        Song song1 = createSong("Song A", "ArtistA", "Pop", 2020);
        Song song2 = createSong("Song B", "ArtistB", "Rock", 2018);

        playlist.addSong(song1, false);
        playlist.addSong(song2, false);
        outContent.reset();

        playlist.printAsTable("shuffle");
        String output = outContent.toString();
        assertTrue(output.contains("Shuffling the playlist..."));
        assertTrue(output.contains("The playlist has been shuffled."));
    }
    @Test
    public void testSortSongsByRatingEqualRatingAndTitle() {
        Song songA = createSong("Same Title", "Alpha", "Pop", 2020);
        Song songB = createSong("Same Title", "Beta", "Pop", 2020);
        songA.setRating(Rating.FOUR);
        songB.setRating(Rating.FOUR);
        playlist.addSong(songB, false);
        playlist.addSong(songA, false);

        playlist.sortSongsByRating();
        List<Song> sorted = playlist.getSongs();
        assertEquals("Alpha", sorted.get(0).getArtist());
        assertEquals("Beta", sorted.get(1).getArtist());
    }

    @Test
    public void testSortSongsByTitleEqualTitleAndArtist() {
        Song songA = createSong("Same", "Same", "Pop", 2020);
        Song songB = createSong("Same", "Same", "Pop", 2020);
        songA.setRating(Rating.THREE);
        songB.setRating(Rating.FOUR);
        playlist.addSong(songB, false);
        playlist.addSong(songA, false);

        playlist.sortSongsByTitle();
        List<Song> sorted = playlist.getSongs();
        assertEquals(3, sorted.get(0).getRatingInt());
        assertEquals(4, sorted.get(1).getRatingInt());
    }

    @Test
    public void testSortSongsByYearEqualYearAndTitle() {
        Song songA = createSong("Same", "Alpha", "Pop", 2020);
        Song songB = createSong("Same", "Beta", "Pop", 2020);
        playlist.addSong(songB, false);
        playlist.addSong(songA, false);

        playlist.sortSongsByYear();
        List<Song> sorted = playlist.getSongs();
        assertEquals("Alpha", sorted.get(0).getArtist());
        assertEquals("Beta", sorted.get(1).getArtist());
    }
}