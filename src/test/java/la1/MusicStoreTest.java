package la1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.FileReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.BufferedReader;


public class MusicStoreTest {
    private MusicStore musicStore;
    private Song testSong;
    private Album testAlbum;
    private Playlist testPlaylist;
    private FavoriteList favoriteList;
    private File createTempFile(String fileName, String content) throws IOException {
        File temp = File.createTempFile(fileName, null);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
            bw.write(content);
        }
        return temp;
    }

    @BeforeEach
    void setUp() {
        musicStore = new MusicStore();
        testSong = new Song("Test Song", "Test Artist", "Pop", 2023);
        List<Song> songs = new ArrayList<>();
        songs.add(testSong);
        testAlbum = new Album("Test Album", "Test Artist", "Pop", 2023, songs);
        testPlaylist = new Playlist("Test Playlist");
        favoriteList = new FavoriteList();
    }

    //=====================================
    // MusicStore Tests
    //=====================================

    @Test
    void testParseAlbumFileEmpty() throws IOException {
        File tempAlbumFile = createTempFile("TestAlbum_TestArtist", "");
        Album album = musicStore.parseAlbumFile(tempAlbumFile.getAbsolutePath());
        assertNull(album, "Empty album file should return null.");
    }

    @Test
    void testParseAlbumFileInvalidHeader() throws IOException {
        File tempAlbumFile = createTempFile("TestAlbum_TestArtist", "TestAlbum,TestArtist,Pop\n");
        Album album = musicStore.parseAlbumFile(tempAlbumFile.getAbsolutePath());
        assertNull(album, "Invalid header format should return null.");
    }

    @Test
    void testParseAlbumFileInvalidYear() throws IOException {
        File tempAlbumFile = createTempFile("TestAlbum_TestArtist", "TestAlbum,TestArtist,Pop,NotAYear\n");
        Album album = musicStore.parseAlbumFile(tempAlbumFile.getAbsolutePath());
        assertNull(album);
    }

    @Test
    void testParseAlbumFileWithEmptySongLines() throws IOException {
        // Provide header, plus a few empty lines.
        String content = "TestAlbum,TestArtist,Pop,2023\n\n\n";
        File tempAlbumFile = createTempFile("TestAlbum_TestArtist", content);
        Album album = musicStore.parseAlbumFile(tempAlbumFile.getAbsolutePath());
        assertNotNull(album);
        assertEquals("TestAlbum", album.getTitle());
        assertEquals(0, album.getSongsDirect().size());
    }

    @Test
    void testGetAlbumDoesNotExist() {
        Album album = musicStore.getAlbum("NotExist", "NotExist");
        assertNull(album);
    }

    @Test
    void testLoadSongInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            musicStore.loadSong("Invalid Format");
        });
    }
    @Test
    void testPlaylistAddSong() {
        testPlaylist.addSong(testSong, false);
        assertEquals(1, testPlaylist.getSize());
        assertEquals("Test Song", testPlaylist.getSongs().get(0).getTitle());
    }

    @Test
    void testPlaylistAddSongDuplicate() {
        testPlaylist.addSong(testSong, false);
        testPlaylist.addSong(testSong, false);
        assertEquals(1, testPlaylist.getSize());
    }

    @Test
    void testPlaylistRemoveSong() {
        testPlaylist.addSong(testSong, false);
        testPlaylist.removeSong(testSong);
        assertEquals(0, testPlaylist.getSize());
    }

    @Test
    void testPlaylistRemoveSongNotInList() {
        testPlaylist.removeSong(testSong);
        assertEquals(0, testPlaylist.getSize());
    }

    @Test
    void testPlaylistGetPlayingEmpty() {
        testPlaylist.getPlaying();
        assertEquals(0, testPlaylist.getSize());
    }

    @Test
    void testPlaylistGetPlayingNonEmpty() {
        testPlaylist.addSong(testSong, false);
        testPlaylist.getPlaying();
        assertEquals(1, testPlaylist.getSize());
    }

    @Test
    void testPlaylistPlayNextEmpty() {
        testPlaylist.playNext();
        assertEquals(0, testPlaylist.getSize());
    }

    @Test
    void testPlaylistPlayNextOneSong() {
        testPlaylist.addSong(testSong, false);
        testPlaylist.playNext();
        assertTrue(testPlaylist.getSongs().isEmpty() || testPlaylist.getSongs().size() == 1);
    }

    @Test
    void testPlaylistPlayNextMultipleSongs() {
        Song s1 = new Song("Song1", "Artist1");
        Song s2 = new Song("Song2", "Artist2");
        testPlaylist.addSong(s1, false);
        testPlaylist.addSong(s2, false);
        testPlaylist.playNext();
        assertEquals(1, testPlaylist.getSize());
    }

    @Test
    void testPlaylistInsertSongFront() {
        Song s1 = new Song("Song1", "Artist1");
        testPlaylist.insertSong(s1);
        Song s2 = new Song("Song2", "Artist2");
        testPlaylist.insertSong(s2);
        assertEquals(s2, testPlaylist.getSongs().get(0));
    }

    @Test
    void testPlaylistClear() {
        testPlaylist.addSong(testSong, false);
        testPlaylist.clear(false);
        assertEquals(0, testPlaylist.getSize());
    }

    @Test
    void testPlaylistPrintAsTableEmpty() {
        testPlaylist.printAsTable("title");
        assertTrue(testPlaylist.getSongs().isEmpty());
    }

    @Test
    void testPlaylistShuffleEmpty() {
        testPlaylist.shuffle();
        // No exception, no changes
        assertTrue(testPlaylist.getSongs().isEmpty());
    }

    @Test
    void testPlaylistShuffle() {
        Song s1 = new Song("Song1", "Artist1");
        Song s2 = new Song("Song2", "Artist2");
        testPlaylist.addSong(s1, false);
        testPlaylist.addSong(s2, false);
        testPlaylist.shuffle();
        // Just ensure we still have 2 songs
        assertEquals(2, testPlaylist.getSize());
    }

    @Test
    void testPlaylistToStringEmpty() {
        String result = testPlaylist.toString();
        assertEquals("The playlist is empty.", result);
    }

    @Test
    void testPlaylistToStringNonEmpty() {
        testPlaylist.addSong(testSong, false);
        String result = testPlaylist.toString();
        assertTrue(result.contains("Test Song"));
    }

    //=====================================
    // FavoriteList Tests
    //=====================================

    @Test
    void testFavoriteListAddSong() {
        favoriteList.addSong(testSong, false);
        assertEquals(1, favoriteList.getSize());
    }

    @Test
    void testFavoriteListRemoveSong() {
        favoriteList.addSong(testSong, false);
        favoriteList.removeSong(testSong);
        assertEquals(0, favoriteList.getSize());
    }

    @Test
    void testFavoriteListPrintAsTableEmpty() {
        favoriteList.printAsTable("title");
        assertEquals(0, favoriteList.getSize());
    }

    @Test
    void testFavoriteListPrintAsTableNonEmpty() {
        favoriteList.addSong(testSong, false);
        favoriteList.printAsTable("title");
        assertEquals(1, favoriteList.getSize());
    }

    @Test
    void testFavoriteListToStringEmpty() {
        String result = favoriteList.toString();
        assertEquals("The favorite list is empty.", result);
    }

    @Test
    void testFavoriteListToStringNonEmpty() {
        favoriteList.addSong(testSong, true);
        String result = favoriteList.toString();
        assertTrue(result.contains("Test Song"));
    }

    @Test
    void testLoadSongsFromFile() throws IOException {
        String[] lines = {
                "Sons,The Heavy,Rock,2019",
                "Sigh No More,Mumford & Sons,Alternative,2009",
                "Tapestry,Carol King,Rock,1971",
                "Waking Up,OneRepublic,Rock,2009",
                "A Rush of Blood to the Head,Coldplay,Alternative,2002",
                "Mission Bell,Amos Lee,Singer/Songwriter,2010",
                "Old Ideas,Leonard Cohen,Singer/Songwriter,2012"
        };
        File tempFile = File.createTempFile("songs_122", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            String songLine;
            while ((songLine = reader.readLine()) != null) {
                musicStore.loadSong(songLine);
            }
        }
        assertEquals(166, musicStore.getSongCount());

        Song loadedSons = musicStore.getSongMap().get(musicStore.generateKey("Sons", "The Heavy"));
        assertNotNull(loadedSons);
        assertEquals("Rock", loadedSons.getGenre());
        assertEquals(2019, loadedSons.getYear());
        Song loadedTapestry = musicStore.getSongMap().get(musicStore.generateKey("Tapestry", "Carol King"));
        assertNotNull(loadedTapestry);
        assertEquals("Rock", loadedTapestry.getGenre());
        assertEquals(1971, loadedTapestry.getYear());
    }

    @Test
    void testLoadSongTwoParts() {
        musicStore.loadSong("Hello, Adele");
        assertEquals(164, musicStore.getSongCount());
        Song s = musicStore.getSongMap()
                .get(musicStore.generateKey("Hello", "Adele"));
        assertNotNull(s);
        assertEquals("Hello", s.getTitle());
        assertEquals("Adele", s.getArtist());
        assertEquals(s.getGenre(), "Unknown");
        assertEquals(0, s.getYear());
    }

    @Test
    void testLoadSongThreePartsYear() {
        musicStore.loadSong("Shape of You, Ed Sheeran, 2017");
        assertEquals(164, musicStore.getSongCount());
        Song s = musicStore.getSongMap()
                .get(musicStore.generateKey("Shape of You", "Ed Sheeran"));
        assertNotNull(s);
        assertEquals("Shape of You", s.getTitle());
        assertEquals("Ed Sheeran", s.getArtist());
        assertEquals(s.getGenre(), "Unknown");
        assertEquals(2017, s.getYear());
    }

    @Test
    void testLoadSongThreePartsGenre() {
        musicStore.loadSong("Photograph, Ed Sheeran, Pop");
        assertEquals(164, musicStore.getSongCount());
        Song s = musicStore.getSongMap()
                .get(musicStore.generateKey("Photograph", "Ed Sheeran"));
        assertNotNull(s);
        assertEquals("Photograph", s.getTitle());
        assertEquals("Ed Sheeran", s.getArtist());
        assertEquals("Pop", s.getGenre());
        assertEquals(0, s.getYear());
    }

    @Test
    void testGetAllAlbums() {
        Collection<Album> allAlbums = musicStore.getAllAlbums();
        Album a = new Album("TestAlbum", "TestArtist", "TestGenre", 2020, new ArrayList<>());
        musicStore.getAlbumMap().put(musicStore.generateKey("TestAlbum", "TestArtist"), a);
        allAlbums = musicStore.getAllAlbums();
        assertEquals(15, allAlbums.size());
    }
}
