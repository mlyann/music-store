package la1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryModelTest {

    private LibraryModel library;
    private MusicStore store;
    private Song song1;
    private Song song2;
    private Album album1;
    private Album album2;

    @BeforeEach
    void setUp() {
        store = new MusicStore();
        // Add some songs/albums to the MusicStore for search testing
        store.loadSong("Song One, Artist One, Pop, 2020");
        store.loadSong("Song Two, Artist Two, Rock, 2021");

        // Create sample Song objects
        song1 = new Song("Test Song 1", "Test Artist 1", "Rock", 2005);
        song2 = new Song("Test Song 2", "Test Artist 2", "Pop", 2010);

        // Create sample Albums
        ArrayList<Song> albumSongs1 = new ArrayList<>();
        albumSongs1.add(song1);
        album1 = new Album("Test Album 1", "Test Artist 1", "Rock", 2005, albumSongs1);

        ArrayList<Song> albumSongs2 = new ArrayList<>();
        albumSongs2.add(song2);
        album2 = new Album("Test Album 2", "Test Artist 2", "Pop", 2010, albumSongs2);

        // Manually place them into MusicStore so we can test searching in store
        store.getAlbumMap().put(store.generateKey("Test Album 1", "Test Artist 1"), album1);
        store.getAlbumMap().put(store.generateKey("Test Album 2", "Test Artist 2"), album2);

        // Initialize LibraryModel
        library = new LibraryModel("TestUser", store);
    }

    @Test
    @DisplayName("Constructor sets correct user ID, library starts empty")
    void testConstructor() {
        assertEquals("TestUser", library.getUserID());
        // Initially, the user library should be empty
        assertEquals(0, library.getSongListSize(), "No user songs yet");
        assertEquals(0, library.getAlbumListSize(), "No user albums yet");
    }

    @Test
    @DisplayName("Set and check currentSong, get info")
    void testSetCurrentSongAndCheck() {
        // 1) Search in musicStore
        library.searchSong("Song One", true);
        int resultsSize = library.getSearchSongListSize();
        assertTrue(resultsSize > 0, "Should find 'Song One' in musicStore");

        // 2) Handle selection => add the found song to user library
        boolean handled = library.handleSongSelection(0, resultsSize);
        assertTrue(handled, "Song should be added to user library");
        assertEquals(1, library.getSongListSize());

        // 3) Now userSongSerch => sets searchSongList = user's songs
        library.userSongSerch();

        // 4) setCurrentSong
        boolean setCurrent = library.setCurrentSong(0, "Song One");
        assertTrue(setCurrent, "Should successfully set Song One as currentSong");

        // 5) checkCurrentSong
        assertTrue(library.checkCurrentSong("Song One"));

        // 6) getCurrentSongInfo
        String info = library.getCurrentSongInfo();
        assertNotNull(info, "Current song info should not be null");
        assertTrue(info.contains("Song One"), "Should contain the title");
    }

    @Test
    @DisplayName("Set currentSong without check (index only)")
    void testSetCurrentSongWithoutCheck() {
        // Must have something in searchSongList
        library.searchSong("Song Two", true);
        int size = library.getSearchSongListSize();
        assertTrue(size > 0);

        library.setCurrentSongWithoutCheck(0);
        assertNotNull(library.getCurrentSongInfo());
    }

    @Test
    @DisplayName("Set and check currentAlbum, get info")
    void testSetCurrentAlbumAndCheck() {
        library.searchAlbum("Test Album 1", true);
        assertEquals(1, library.searchAlbumList.size(), "Should find 1 album match in the store");
        boolean set = library.setCurrentAlbum(0, "Test Album 1");
        assertTrue(set, "Should successfully set current album");
        assertTrue(library.checkCurrentAlbum("Test Album 1"), "checkCurrentAlbum should confirm the correct album");
        String info = library.getCurrentAlbumInfo();
        assertNotNull(info);
        assertTrue(info.contains("Test Album 1"), "Should contain album title");
    }

    @Test
    @DisplayName("Search songs in user library vs. music store")
    void testSearchSongInUserLibraryAndStore() {
        var resultsUser = library.searchSong("Song One", false);
        assertTrue(resultsUser.isEmpty(), "Should find nothing in user library so far");
        var resultsStore = library.searchSong("Song One", true);
        assertFalse(resultsStore.isEmpty());
        assertEquals("Song One", resultsStore.get(0).get(0));
    }

    @Test
    @DisplayName("handleSongSelection and allSongSelection add to user library")
    void testHandleSongSelection() {
        // Search 'Song' in music store => should match multiple
        library.searchSong("Song", true);
        int size = library.getSearchSongListSize();
        assertTrue(size >= 2, "We expect at least 2 songs from store (Song One, Song Two)");

        // handleSongSelection => add just one
        boolean handled = library.handleSongSelection(0, size);
        assertTrue(handled);
        assertEquals(1, library.getSongListSize());

        // Now do allSongSelection => add them all
        library.searchSong("Song", true);
        size = library.getSearchSongListSize();
        boolean allHandled = library.allSongSelection(size);
        assertTrue(allHandled);
        // Now user library should have all matched songs
        assertTrue(library.getSongListSize() >= 2);
    }

    @Test
    @DisplayName("printAllArtists: empty vs. after adding a song")
    void testPrintAllArtists() {
        library.printAllArtists();
        library.searchSong("Song One", true);
        library.handleSongSelection(0, library.getSearchSongListSize());

        library.printAllArtists();
    }

    @Test
    @DisplayName("Add album to user library via handleAlbumSelection / allAlbumSelection")
    void testHandleAlbumSelection() {
        library.searchAlbum("Test Album", true); // Should find 2 albums
        int size = library.searchAlbumList.size();
        assertTrue(size >= 2);
        boolean handled = library.handleAlbumSelection(0, size);
        assertTrue(handled);
        assertEquals(1, library.getAlbumListSize(), "User should have 1 album now");
        library.searchAlbum("Test Album", true);
        size = library.searchAlbumList.size();
        boolean allHandled = library.allAlbumSelection(size);
        assertTrue(allHandled);
        assertTrue(library.getAlbumListSize() >= 2);
        assertTrue(library.getSongListSize() >= 2);
    }
}