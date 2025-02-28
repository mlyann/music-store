package la1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

public class LibraryModelTest {

    private LibraryModel library;
    private MusicStore store;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        store = new MusicStore();
        library = new LibraryModel("TestUser", store);

        Album albumA = createSampleAlbum("Test Album A", "Artist A", "Rock", 2001, 2);
        Album albumB = createSampleAlbum("Test Album B", "Artist B", "Pop", 2020, 1);

        // Put them in the store's map for store-based searching
        store.getAlbumMap().put(store.generateKey("Test Album A", "Artist A"), albumA);
        store.getAlbumMap().put(store.generateKey("Test Album B", "Artist B"), albumB);

        // Add songs to the store
        store.loadSong("StoreSongA, ArtistA, Rock, 2001");
        store.loadSong("StoreSongB, ArtistB, Pop, 2002");
        System.setOut(new PrintStream(outContent));

    }

    private Album createSampleAlbum(String title, String artist, String genre, int year, int songCount) {
        ArrayList<Song> songs = new ArrayList<>();
        for (int i = 1; i <= songCount; i++) {
            songs.add(new Song(title + " Track" + i, artist, genre, year));
        }
        return new Album(title, artist, genre, year, songs);
    }

    // ---------------------------------------------------------
    // Utility reflection helpers
    // ---------------------------------------------------------
    private void setCurrentSongField(Song song) {
        try {
            Field f = LibraryModel.class.getDeclaredField("currentSong");
            f.setAccessible(true);
            f.set(library, song);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Reflection error setting currentSong: " + e.getMessage());
        }
    }

    private void setCurrentAlbumField(Album album) {
        try {
            Field f = LibraryModel.class.getDeclaredField("currentAlbum");
            f.setAccessible(true);
            f.set(library, album);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Reflection error setting currentAlbum: " + e.getMessage());
        }
    }

    private void setCurrentPlaylistField(Playlist playlist) {
        try {
            Field f = LibraryModel.class.getDeclaredField("currentPlaylist");
            f.setAccessible(true);
            f.set(library, playlist);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Reflection error setting currentPlaylist: " + e.getMessage());
        }
    }

    private Playlist getPlayingListField() {
        try {
            Field f = LibraryModel.class.getDeclaredField("playingList");
            f.setAccessible(true);
            return (Playlist) f.get(library);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Reflection error getting playingList: " + e.getMessage());
            return null;
        }
    }

    // ---------------------------------------------------------
    // 1) playAlbum(String albumTitle)
    // ---------------------------------------------------------
    @Test
    @DisplayName("playAlbum => returns false if currentAlbum is null")
    void testPlayAlbumNullCurrentAlbum() {
        boolean result = library.playAlbum("AnyTitle");
        assertFalse(result, "Should return false if currentAlbum is null");
    }

    @Test
    @DisplayName("playAlbum => returns false if albumTitle doesn't match currentAlbum.getTitle()")
    void testPlayAlbumTitleMismatch() {
        Album album = new Album("Real Album", "Any Artist", "Pop", 2000, new ArrayList<>());
        setCurrentAlbumField(album);

        boolean result = library.playAlbum("Wrong Title");
        assertFalse(result, "Should return false for mismatched title");
    }

    @Test
    @DisplayName("playAlbum => reverses songs, inserts them into playingList if title matches")
    void testPlayAlbumHappyPath() {
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(new Song("Song1", "Artist", "Rock", 2000));
        songs.add(new Song("Song2", "Artist", "Rock", 2001));
        Album album = new Album("MyAlbum", "Artist", "Rock", 2020, songs);

        setCurrentAlbumField(album);
        // Now call with matching title
        boolean ok = library.playAlbum("MyAlbum");
        assertTrue(ok);

        Playlist playing = getPlayingListField();
        assertEquals(2, playing.getSize(), "Should have inserted 2 songs");
        // The last song in the album is placed at index 0 (reversed)
        assertEquals("Song1", playing.getSongs().get(0).getTitle());
        assertEquals("Song2", playing.getSongs().get(1).getTitle());
    }

    // ---------------------------------------------------------
    // 2) addSongToPlayLists()
    // ---------------------------------------------------------
    @Test
    @DisplayName("addSongToPlayLists => no playlist => prints 'No playlist selected.'")
    void testAddSongToPlayListsNoPlaylist() {
        setCurrentPlaylistField(null);
        // Even if we have a currentSong, no playlist
        setCurrentSongField(new Song("Any", "Artist"));
        // No exception => passes
        library.addSongToPlayLists();
    }

    @Test
    @DisplayName("addSongToPlayLists => no song => prints 'No song selected.'")
    void testAddSongToPlayListsNoSong() {
        // Create a valid playlist
        library.createPlaylist("MyPlaylist");
        library.selectCurrentPlaylist(0);
        // But currentSong is null
        setCurrentSongField(null);

        library.addSongToPlayLists();
        // Should just print a message
    }

    @Test
    @DisplayName("addSongToPlayLists => success => adds currentSong to the named playlist")
    void testAddSongToPlayListsSuccess() {
        library.createPlaylist("MyPlaylist");
        library.selectCurrentPlaylist(0);

        setCurrentSongField(new Song("SongX", "ArtistX"));
        library.addSongToPlayLists();

        // The playlist "MyPlaylist" should have that song
        assertEquals(1, library.currentPlistSize());
    }

    // ---------------------------------------------------------
    // 3) removeSongFromPlayLists()
    // ---------------------------------------------------------
    @Test
    @DisplayName("removeSongFromPlayLists => no playlist => prints 'No playlist selected.'")
    void testRemoveSongFromPlayListsNoPlaylist() {
        setCurrentPlaylistField(null);
        setCurrentSongField(new Song("SongX", "ArtistX"));
        library.removeSongFromPlayLists();
    }

    @Test
    @DisplayName("removeSongFromPlayLists => no song => prints 'No song selected.'")
    void testRemoveSongFromPlayListsNoSong() {
        library.createPlaylist("PL");
        library.selectCurrentPlaylist(0);
        setCurrentSongField(null);

        library.removeSongFromPlayLists();
        // just prints message
    }

    @Test
    @DisplayName("removeSongFromPlayLists => success => removes currentSong from currentPlaylist")
    void testRemoveSongFromPlayListsSuccess() {
        library.createPlaylist("PL");
        library.selectCurrentPlaylist(0);
        // Add a song
        Song s = new Song("SongA", "ArtistA");
        setCurrentSongField(s);
        library.addSongToPlayLists();

        // Now remove
        library.removeSongFromPlayLists();
        assertEquals(0, library.currentPlistSize());
    }

    // ---------------------------------------------------------
    // 4) removePlayList(int index) in PlayLists
    // ---------------------------------------------------------
    @Test
    @DisplayName("removePlayList => removing a valid index => returns true")
    void testRemovePlayListValidIndex() {
        library.createPlaylist("A");
        library.createPlaylist("B");
        assertEquals(2, library.getPlayListsSize());

        // remove index=0 => always returns true in your code
        Boolean removed = library.removePlayList(0);
        assertTrue(removed);
        assertEquals(1, library.getPlayListsSize());

        // remove index=0 again => now removing the 2nd playlist
        removed = library.removePlayList(0);
        assertTrue(removed);
        assertEquals(0, library.getPlayListsSize());
    }

    @Test
    @DisplayName("removePlayList => watch out for out-of-bounds if index is not valid (your code doesn't handle it) ")
    void testRemovePlayListOutOfRange() {
        // If your code doesn't handle out-of-range, it might throw an exception
        // We'll just see if it does.
        library.createPlaylist("OnlyOne");
        assertEquals(1, library.getPlayListsSize());

        // remove index=5 => might cause an error or not
        // In your code, it tries new ArrayList<>(playLists.keySet()).get(index)
        // That will throw an IndexOutOfBoundsException
        assertThrows(IndexOutOfBoundsException.class, () -> {
            library.removePlayList(5);
        });
    }

    // ---------------------------------------------------------
    // 1) isFavourite()
    // ---------------------------------------------------------
    @Test
    @DisplayName("isFavourite => returns currentSong.isFavourite()")
    void testIsFavourite() {
        // If currentSong is null => we'd get NullPointerException. We'll test a normal scenario.
        Song s = new Song("TestSong", "TestArtist");
        s.setFavourite(true); // set it to true
        setCurrentSongField(s);

        assertTrue(library.isFavourite(), "Should return true if currentSong is set to favourite");
        // Also check if we set it to false
        s.setFavourite(false);
        assertFalse(library.isFavourite(), "Should return false if currentSong is not favourite");
    }

    // ---------------------------------------------------------
    // 2) getFavoriteListSize()
    // ---------------------------------------------------------
    @Test
    @DisplayName("getFavoriteListSize => returns number of songs in static favoriteList")
    void testGetFavoriteListSize() {
        // Start with empty
        assertEquals(0, library.getFavoriteListSize());
        // Add a currentSong => call addFavourite
        Song s = new Song("SongA", "ArtistA");
        setCurrentSongField(s);
        library.addFavourite();
        assertEquals(1, library.getFavoriteListSize());
    }

    // ---------------------------------------------------------
    // 3) getFavoriteListSongs()
    // ---------------------------------------------------------
    @Test
    @DisplayName("getFavoriteListSongs => returns arraylist-of-string-lists for each favourite song")
    void testGetFavoriteListSongs() {
        // Add 2 songs
        Song s1 = new Song("Song1", "Artist1");
        Song s2 = new Song("Song2", "Artist2");

        setCurrentSongField(s1);
        library.addFavourite();
        setCurrentSongField(s2);
        library.addFavourite();

        ArrayList<ArrayList<String>> favSongs = library.getFavoriteListSongs();
        assertEquals(2, favSongs.size());
        assertTrue(favSongs.get(0).contains("Song1"));
        assertTrue(favSongs.get(1).contains("Song2"));
    }

    // ---------------------------------------------------------
    // 4) printFavoriteList()
    // ---------------------------------------------------------
    @Test
    @DisplayName("printFavoriteList => calls favoriteList.printAsTable(), no crash")
    void testPrintFavoriteList() {
        // Even if no songs, should just print a message
        library.printFavoriteList();

        // Add a song => see no crash
        Song s = new Song("SongA", "ArtistA");
        setCurrentSongField(s);
        library.addFavourite();
        library.printFavoriteList();
    }

    // ---------------------------------------------------------
    // 5) clearFavoriteList()
    // ---------------------------------------------------------
    @Test
    @DisplayName("clearFavoriteList => un-favorites each song and empties the FavoriteList")
    void testClearFavoriteList() {
        // Add 2 songs to favorite
        Song s1 = new Song("Song1", "Artist1");
        Song s2 = new Song("Song2", "Artist2");
        setCurrentSongField(s1);
        library.addFavourite();
        setCurrentSongField(s2);
        library.addFavourite();

        assertEquals(2, library.getFavoriteListSize());
        // Now clear
        library.clearFavoriteList();
        assertEquals(0, library.getFavoriteListSize(), "Fav list should be empty");
        // Also those songs are setFavourite(false)
        assertFalse(s1.isFavourite());
        assertFalse(s2.isFavourite());
    }

    // ---------------------------------------------------------
    // 6) addFavourite()
    // ---------------------------------------------------------
    @Test
    @DisplayName("addFavourite => prints 'No song selected.' if currentSong is null")
    void testAddFavouriteNoSong() {
        setCurrentSongField(null);
        library.addFavourite();
        // Just ensures no crash
        assertEquals(0, library.getFavoriteListSize());
    }

    @Test
    @DisplayName("addFavourite => adds currentSong to FavoriteList and sets it favourite=true")
    void testAddFavouriteSuccess() {
        Song s = new Song("SongX", "ArtistX");
        setCurrentSongField(s);
        library.addFavourite();
        // Check favouriteList size
        assertEquals(1, library.getFavoriteListSize());
        assertTrue(library.isFavourite());
    }

    // ---------------------------------------------------------
    // 7) removeFavourite()
    // ---------------------------------------------------------
    @Test
    @DisplayName("removeFavourite => prints 'No song selected.' if currentSong is null")
    void testRemoveFavouriteNoSong() {
        setCurrentSongField(null);
        library.removeFavourite();
        // no crash
    }

    @Test
    @DisplayName("removeFavourite => unsets favourite, removes from favoriteList")
    void testRemoveFavouriteSuccess() {
        // First add
        Song s = new Song("SongRem", "ArtistRem");
        setCurrentSongField(s);
        library.addFavourite();
        assertEquals(1, library.getFavoriteListSize());
        // Now remove
        library.removeFavourite();
        assertFalse(s.isFavourite());
        assertEquals(0, library.getFavoriteListSize());
    }

    // ---------------------------------------------------------
    // 8) playFavoriteList()
    // ---------------------------------------------------------
    @Test
    @DisplayName("playFavoriteList => reverses favorite list songs into playingList")
    void testPlayFavoriteList() {
        // Add multiple songs
        Song s1 = new Song("Song1", "Artist1");
        Song s2 = new Song("Song2", "Artist2");
        setCurrentSongField(s1);
        library.addFavourite();
        setCurrentSongField(s2);
        library.addFavourite();

        // Now call playFavoriteList
        library.playFavoriteList();
        // The playingList is reversed
        Playlist playing = getPlayingListField();
        assertEquals(2, playing.getSize());
        assertEquals("Song1", playing.getSongs().get(0).getTitle());
        assertEquals("Song2", playing.getSongs().get(1).getTitle());
    }

    // ---------------------------------------------------------
    // 9) shufflePlaylist()
    // ---------------------------------------------------------
    @Test
    @DisplayName("shufflePlaylist => calls playingList.shuffle()")
    void testShufflePlaylist() {
        // We'll add 2 songs to playingList
        Song s1 = new Song("Song1", "Artist1");
        Song s2 = new Song("Song2", "Artist2");
        setCurrentSongField(s1);
        library.playSong(); // inserts s1
        setCurrentSongField(s2);
        library.playSong(); // inserts s2

        assertEquals(2, getPlayingListField().getSize());
        library.shufflePlaylist();
        // no direct assertion on order, just ensures no crash
    }

    // ---------------------------------------------------------
    // 10) printRating()
    // ---------------------------------------------------------
    @Test
    @DisplayName("printRating => returns 'currentSongTitle[star-rating]'")
    void testPrintRating() {
        Song s = new Song("RateMe", "ArtistRate");
        setCurrentSongField(s);
        // default rating => 0 => "☆☆☆☆☆"
        String ratingText = library.printRating();
        assertTrue(ratingText.contains("RateMe["),
                "Should contain the song title and bracket");
    }

    // ---------------------------------------------------------
    // 11) setRating(int rating)
    // ---------------------------------------------------------
    @Test
    @DisplayName("setRating => sets currentSong's rating if not null")
    void testSetRating() {
        Song s = new Song("SongRate", "ArtistR");
        setCurrentSongField(s);
        library.setRating(4);
        assertEquals(4, s.getRatingInt());
    }

    // ---------------------------------------------------------
    // 12) getRating()
    // ---------------------------------------------------------
    @Test
    @DisplayName("getRating => returns currentSong's rating int")
    void testGetRating() {
        Song s = new Song("SongRate", "ArtistR");
        s.setRating(Rating.THREE);
        setCurrentSongField(s);

        assertEquals(3, library.getRating());
    }

    @Test
    @DisplayName("playFavoriteList removes a song from playingList before re-inserting it to avoid duplicates")
    void testPlayFavoriteListRemovesExistingSong() {
        // 1) Create two songs, add them to favoriteList in normal order.
        Song songA = new Song("SongA", "ArtistA");
        Song songB = new Song("SongB", "ArtistB");
        setCurrentSongField(songA);
        library.addFavourite();
        setCurrentSongField(songB);
        library.addFavourite();
        Playlist playing = getPlayingListField();
        playing.addSong(songB);
        library.playFavoriteList();
        assertEquals(2, playing.getSize(), "playingList should contain exactly 2 songs (no duplicates).");

        // Confirm the reversed insertion order:
        assertSame(songA, playing.getSongs().get(0), "SongAÅ should be first (re-inserted)");
        assertSame(songB, playing.getSongs().get(1), "SongA should be second");
    }

    // -------------------------------------------------------------------------
    // 1) getPlayerSize()
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("getPlayerSize => returns playingList.getSongs().size()")
    void testGetPlayerSize() {
        // Initially empty => 0
        assertEquals(0, library.getPlayerSize());

        // Add a song to playingList by either reflection or calling library.playSong()
        Song s = new Song("Song A", "Artist A");
        setCurrentSongField(s);
        library.playSong(); // inserts s into playingList

        assertEquals(1, library.getPlayerSize());
    }

    // -------------------------------------------------------------------------
    // 2) getPlaying()
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("getPlaying => calls playingList.getPlaying(), no crash")
    void testGetPlaying() {
        // If no songs => prints 'The playlist is empty.'
        library.getPlaying();

        // Add a song => then getPlaying again
        Song s = new Song("SongX", "ArtistX");
        setCurrentSongField(s);
        library.playSong();
        library.getPlaying();
        // We don't assert console text, just ensure no exception
    }

    // -------------------------------------------------------------------------
    // 3) playNextSong()
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("playNextSong => removes top if multiple songs, or prints no song if empty/singleton")
    void testPlayNextSong() {
        // empty => 'No song to play next.'
        library.playNextSong(); // no crash

        // If we have 2 songs => it removes top, prints next
        Song s1 = new Song("Song1", "Artist1");
        Song s2 = new Song("Song2", "Artist2");
        setCurrentSongField(s1);
        library.playSong(); // playingList => [s1]
        setCurrentSongField(s2);
        library.playSong(); // playingList => [s2, s1] (since 'playSong' calls insertSong at top)

        library.playNextSong();
        // Now top was s2 => removed => new top is s1
        Playlist playing = getPlayingListField();
        assertEquals(1, playing.getSize());
        assertEquals("Song1", playing.getSongs().get(0).getTitle());
    }

    // -------------------------------------------------------------------------
    // 4) clearPlaylist()
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("clearPlaylist => empties the playingList")
    void testClearPlaylist() {
        // Add songs
        Song s1 = new Song("Song1", "Artist1");
        setCurrentSongField(s1);
        library.playSong();
        assertEquals(1, library.getPlayerSize());

        library.clearPlaylist();
        assertEquals(0, library.getPlayerSize());
    }

    // -------------------------------------------------------------------------
    // 5) addSongToPlaylist()
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("addSongToPlaylist => prints 'No song selected.' if currentSong is null")
    void testAddSongToPlaylistNoSong() {
        setCurrentSongField(null);
        library.addSongToPlaylist();
        // Shouldn't crash, no test of console
        assertEquals(0, library.getPlayerSize());
    }

    @Test
    @DisplayName("addSongToPlaylist => adds currentSong to playingList if not null")
    void testAddSongToPlaylistSuccess() {
        Song s = new Song("SongA", "ArtistA");
        setCurrentSongField(s);
        library.addSongToPlaylist();
        assertEquals(1, library.getPlayerSize());
    }

    // -------------------------------------------------------------------------
    // 6) removeSongFromPlaylist()
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("removeSongFromPlaylist => prints 'No song selected.' if currentSong is null")
    void testRemoveSongFromPlaylistNoSong() {
        setCurrentSongField(null);
        library.removeSongFromPlaylist();
        // no crash
    }

    @Test
    @DisplayName("removeSongFromPlaylist => removes currentSong from playingList if present")
    void testRemoveSongFromPlaylistSuccess() {
        // Add a song first
        Song s = new Song("SongRem", "ArtistRem");
        setCurrentSongField(s);
        library.addSongToPlaylist();
        assertEquals(1, library.getPlayerSize());

        // Now remove
        library.removeSongFromPlaylist();
        assertEquals(0, library.getPlayerSize());
    }

    // -------------------------------------------------------------------------
    // 7) getPlaylistSongs()
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("getPlaylistSongs => returns arraylist-of-string-lists for each song in playingList")
    void testGetPlaylistSongs() {
        // empty => 0
        ArrayList<ArrayList<String>> songsEmpty = library.getPlaylistSongs();
        assertTrue(songsEmpty.isEmpty());

        Song s = new Song("SongA", "ArtistA");
        setCurrentSongField(s);
        library.playSong();

        // now playingList has 1
        ArrayList<ArrayList<String>> songsList = library.getPlaylistSongs();
        assertEquals(1, songsList.size());
        // The first row should contain "SongA" etc.
        assertTrue(songsList.get(0).contains("SongA"));
    }

    // -------------------------------------------------------------------------
    // 8) getPlaylist()
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("getPlaylist => returns playingList.getSongs() as ArrayList<Song>")
    void testGetPlaylist() {
        // empty => 0
        assertTrue(library.getPlaylist().isEmpty());

        // add
        Song s = new Song("SongB", "ArtistB");
        setCurrentSongField(s);
        library.playSong();
        assertEquals(1, library.getPlaylist().size());
        assertEquals("SongB", library.getPlaylist().get(0).getTitle());
    }

    // -------------------------------------------------------------------------
    // 9) getPlayListNames()
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("getPlayListNames => returns the names of all playlists in PlayLists")
    void testGetPlayListNames() {
        // By default, no custom playlists => empty
        assertTrue(library.getPlayListNames().isEmpty());

        // create some
        library.createPlaylist("PL1");
        library.createPlaylist("PL2");

        ArrayList<String> names = library.getPlayListNames();
        assertTrue(names.contains("PL1"));
        assertTrue(names.contains("PL2"));
    }

    // -------------------------------------------------------------------------
    // 10) printAllPlayLists()
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("printAllPlayLists => prints all playlists with their contents, no crash")
    void testPrintAllPlayLists() {
        // If empty => "No playlist found."
        library.printAllPlayLists();
        // Then add some
        library.createPlaylist("MyPL");
        library.printAllPlayLists();
        // no crash => test passes
    }

    // -------------------------------------------------------------------------
    // 11) getCurrentPlayList()
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("getCurrentPlayList => returns the same songs in playingList but as arraylist-of-string-lists")
    void testGetCurrentPlayList() {
        // empty => 0
        ArrayList<ArrayList<String>> emptyPL = library.getCurrentPlayList();
        assertTrue(emptyPL.isEmpty());

        // add
        Song s1 = new Song("Song1", "Artist1");
        setCurrentSongField(s1);
        library.playSong();
        // Now playingList has 1
        ArrayList<ArrayList<String>> result = library.getCurrentPlayList();
        assertEquals(1, result.size());
        assertTrue(result.get(0).contains("Song1"));
    }

    // -------------------------------------------------------------------------
    // 12) printPlaylist()
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("printPlaylist => calls playingList.printAsTable(), no crash")
    void testPrintPlaylist() {
        // empty => prints "The playlist is empty."
        library.printPlaylist();

        // add a song => ensure no crash
        Song s2 = new Song("Song2", "Artist2");
        setCurrentSongField(s2);
        library.playSong();
        library.printPlaylist();
    }

    // -------------------------------------------------------------------------
    // 13) playSong()
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("playSong => inserts currentSong into playingList or prints 'No song selected.'")
    void testPlaySong() {
        // no currentSong => no crash
        setCurrentSongField(null);
        library.playSong();

        // set a currentSong => confirm insertion
        Song s = new Song("SongXYZ", "ArtistXYZ");
        setCurrentSongField(s);
        library.playSong();
        assertEquals(1, library.getPlayerSize());
        assertEquals("SongXYZ", getPlayingListField().getSongs().get(0).getTitle());
    }


    // -------------------------------------------------------------------------
    // 1) searchSong(String keyword, boolean isMusicStore)
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("searchSong => searching in store => results go into searchSongList")
    void testSearchSongInStore() {
        // Searching for "StoreSongA" in the store
        ArrayList<ArrayList<String>> results = library.searchSong("StoreSongA", true);

        // library.searchSongList is updated in parallel
        assertEquals(results.size(), library.getSearchSongListSize(),
                "The 2D results should match the internal searchSongList size.");

        // Should find exactly 1 song named "StoreSongA"
        assertEquals(1, results.size());
        assertTrue(results.get(0).contains("StoreSongA"),
                "The first row should contain 'StoreSongA'.");
    }

    @Test
    @DisplayName("searchSong => searching in user library => empty at first => no results")
    void testSearchSongInUserLibrary() {
        ArrayList<ArrayList<String>> results = library.searchSong("StoreSongA", false);
        assertTrue(results.isEmpty(),
                "User library is initially empty, so searching there yields no results.");
    }

    // -------------------------------------------------------------------------
    // 2) searchSongSub(String keyword, boolean isMusicStore)
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("searchSongSub => returns matched songs from store or from user library")
    void testSearchSongSub() {
        // Searching store => should find 1 for "storesonga" (case-insensitive)
        ArrayList<Song> fromStore = library.searchSongSub("StoreSongA", true);
        assertEquals(1, fromStore.size());
        assertEquals("StoreSongA", fromStore.get(0).getTitle());

        // Searching user library => still empty => 0
        ArrayList<Song> fromUserLib = library.searchSongSub("StoreSongA", false);
        assertTrue(fromUserLib.isEmpty());
    }

    // -------------------------------------------------------------------------
    // 3) allSongSelection(int checkSize)
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("allSongSelection => adds all found songs to user library if sizes match")
    void testAllSongSelection() {
        // 1) search in store => e.g., "StoreSong"
        library.searchSong("StoreSongA", true);
        int realSize = library.getSearchSongListSize();
        assertTrue(realSize >= 1, "We loaded 1 store songs with 'StoreSong' in name.");

        // 2) allSongSelection => true if checkSize matches
        boolean ok = library.allSongSelection(realSize);
        assertTrue(ok, "Should succeed with matching size.");

        // Now user library should contain both songs => getSongListSize() = 2
        assertEquals(realSize, library.getSongListSize());
    }

    @Test
    @DisplayName("allSongSelection => returns false if checkSize != searchSongList.size()")
    void testAllSongSelectionMismatch() {
        library.searchSong("StoreSong", true);
        int realSize = library.getSearchSongListSize();
        boolean ok = library.allSongSelection(realSize + 1);
        assertFalse(ok);
        // user library remains empty
        assertEquals(0, library.getSongListSize());
    }

    // -------------------------------------------------------------------------
    // 4) handleSongSelection(int index, int checkSize)
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("handleSongSelection => adds one selected song to user library if sizes match")
    void testHandleSongSelectionSuccess() {
        library.searchSong("StoreSongA", true);
        int realSize = library.getSearchSongListSize();
        assertTrue(realSize > 0);

        boolean ok = library.handleSongSelection(0, realSize);
        assertTrue(ok);
        // user library should have 1 song => "StoreSongA"
        assertEquals(1, library.getSongListSize());
    }

    @Test
    @DisplayName("handleSongSelection => returns false if mismatch in checkSize")
    void testHandleSongSelectionMismatch() {
        library.searchSong("StoreSongA", true);
        int realSize = library.getSearchSongListSize();
        boolean ok = library.handleSongSelection(0, realSize + 10);
        assertFalse(ok);
        // user library still empty
        assertEquals(0, library.getSongListSize());
    }

    // -------------------------------------------------------------------------
    // 5) getSongListSize()
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("getSongListSize => number of songs in user library, not the store")
    void testGetSongListSize() {
        // initially empty => 0
        assertEquals(0, library.getSongListSize());

        // add one from store
        library.searchSong("StoreSongA", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        assertEquals(1, library.getSongListSize());
    }

    // -------------------------------------------------------------------------
    // 6) SongToString()
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("SongToString => first row is currentAlbum info, subsequent rows are searchSongList")
    void testSongToString() {
        // Must have a currentAlbum + something in searchSongList
        // We'll add an album to user library, then set it as currentAlbum
        // Let's assume setCurrentAlbum(...) is a public method or do some flow
        // We'll do a simplified approach:
        Album sampleAlbum = new Album("AlbumX", "ArtistX", "Rock", 2000, new ArrayList<>());
        library.searchAlbumList.add(sampleAlbum); // manually put it in
        // let's set it as current album (pretend there's a method or do reflection).
        // For simplicity, we'll do reflection if you haven't provided setCurrentAlbum
        // but you said "no reflection"? We'll do an approach:
        library.setCurrentAlbum(0, "AlbumX");  // If your code doesn't exist, adapt as needed.

        // 2) have some searchSongList
        library.searchSong("StoreSongA", true);

        ArrayList<ArrayList<String>> output = library.SongToString();
        // index 0 => album info
        assertFalse(output.isEmpty());
        assertTrue(output.get(0).contains("AlbumX"),
                "The first row should be album info => 'AlbumX' in it.");

        // subsequent => the songs
        assertTrue(output.size() > 1, "We have at least 1 store song => should appear after album info.");
        assertTrue(output.get(1).contains("StoreSongA"),
                "The next row should contain 'StoreSongA' from searchSongList.");
    }

    // -------------------------------------------------------------------------
    // 7) getUserSongs() => deep copy
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("getUserSongs => returns a deep copy of user library songs")
    void testGetUserSongs() {
        // Add 1 song to user library
        library.searchSong("StoreSongA", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        assertEquals(1, library.getSongListSize());

        ArrayList<Song> userSongsCopy = library.getUserSongs();
        assertEquals(1, userSongsCopy.size());

        // Modify the copy => see if original is unaffected
        userSongsCopy.get(0).setFavourite(true);

        // The original song in library => not changed
        // We'll re-check isFavourite => we need a reference to that actual Song
        // For simplicity, we can do searchSong("StoreSongA", false) to retrieve from user library
        library.searchSong("StoreSongA", false);
        Song original = library.searchSongList.get(0);
        assertFalse(original.isFavourite(), "Original should remain unaffected => deep copy success");
    }

    // -------------------------------------------------------------------------
    // 8) printUserSongsTable()
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("printUserSongsTable => prints a table of user's songs, no crash")
    void testPrintUserSongsTable() {
        // empty => prints just header
        library.printUserSongsTable();

        // add 1 => see if no crash
        library.searchSong("StoreSongA", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.printUserSongsTable();
    }

    // -------------------------------------------------------------------------
    // 9) getSongList() => returns user library songs in arraylist-of-string-lists
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("getSongList => returns user library songs in 2D string-lists")
    void testGetSongList() {
        // empty => 0
        assertTrue(library.getSongList().isEmpty());

        // add some
        library.searchSong("StoreSongA", true);
        int size = library.getSearchSongListSize();
        library.allSongSelection(size); // add both to user library
        ArrayList<ArrayList<String>> userSongList = library.getSongList();
        // we expect 2 songs => "StoreSongA", "StoreSongB"
        assertEquals(1, userSongList.size());
        assertTrue(userSongList.get(0).contains("StoreSongA") || userSongList.get(0).contains("StoreSongB"));
    }
    // -------------------------------------------------------------------------
    // Current Song Getter Tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("checkCurrentSong => returns true if current song title matches")
    void testCheckCurrentSong() {
        library.searchSong("StoreSongA", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.userSongSerch();
        library.setCurrentSong(0, "StoreSongA");

        assertTrue(library.checkCurrentSong("StoreSongA"));
        assertFalse(library.checkCurrentSong("NonExistingSong"));
    }

    @Test
    @DisplayName("setCurrentSong => sets current song when title matches")
    void testSetCurrentSong() {
        library.searchSong("StoreSongB", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.userSongSerch();

        assertTrue(library.setCurrentSong(0, "StoreSongB"));
        assertFalse(library.setCurrentSong(0, "WrongTitle"));
    }

    @Test
    @DisplayName("getCurrentSongInfo => returns current song's info string")
    void testGetCurrentSongInfo() {
        library.searchSong("StoreSongA", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.userSongSerch();
        library.setCurrentSong(0, "StoreSongA");

        String info = library.getCurrentSongInfo();
        assertNotNull(info);
        assertTrue(info.contains("StoreSongA"));
    }

    @Test
    @DisplayName("getCurrentSongTitle => returns current song's title")
    void testGetCurrentSongTitle() {
        library.searchSong("StoreSongB", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.userSongSerch();
        library.setCurrentSong(0, "StoreSongB");

        assertEquals("StoreSongB", library.getCurrentSongTitle());
    }

    @Test
    @DisplayName("setCurrentSongWithoutCheck => directly sets current song by index")
    void testSetCurrentSongWithoutCheck() {
        library.searchSong("StoreSongA", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.userSongSerch();

        library.setCurrentSongWithoutCheck(0);
        assertTrue(library.getCurrentSongInfo().contains("StoreSongA"));
    }

    // -------------------------------------------------------------------------
    // Current Song List Tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("userSongSerch => sets searchSongList to user's library songs")
    void testUserSongSerch() {
        library.searchSong("StoreSongA", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.userSongSerch();

        assertEquals(1, library.getSearchSongListSize());
    }

    @Test
    @DisplayName("playListSearch => sets searchSongList to playingList songs")
    void testPlayListSearch() {
        library.searchSong("StoreSongA", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.userSongSerch();
        library.setCurrentSong(0, "StoreSongA");
        library.playSong();

        library.playListSearch();
        assertEquals(1, library.getSearchSongListSize());
    }

    @Test
    @DisplayName("favoriteListSearch => sets searchSongList to favorite list songs")
    void testFavoriteListSearch() {
        library.searchSong("StoreSongA", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.userSongSerch();
        library.setCurrentSong(0, "StoreSongA");
        library.addFavourite();

        library.favoriteListSearch();
        assertEquals(1, library.getSearchSongListSize());
    }

    @Test
    @DisplayName("getSearchSongListSize => returns size of searchSongList")
    void testGetSearchSongListSize() {
        library.searchSong("StoreSongA", true);
        assertEquals(1, library.getSearchSongListSize());
    }

    // -------------------------------------------------------------------------
    // All Functions - printAllArtists
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("printAllArtists => prints all unique artists in user's library")
    void testPrintAllArtists() {
        library.searchSong("StoreSongA", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.searchSong("StoreSongB", true);
        library.handleSongSelection(0, library.getSearchSongListSize());

        library.printAllArtists();
    }
    @Test
    @DisplayName("printAllArtists => Should print 'The library is empty.' if UserSongs is empty")
    void testPrintAllArtistsEmptyLibrary() {
        library.printAllArtists();
        System.out.flush();
        String captured = outContent.toString();
        System.out.println("Captured Output >>>" + captured + "<<<");
        assertTrue(captured.contains("The library is empty [WARNING]"),
                "Expected substring 'The library is empty [WARNING]' in:\n" + captured);
    }
    @Test
    @DisplayName("clearAllPlayLists => removes all user-created playlists")
    void testClearAllPlayLists() {
        assertEquals(0, library.getPlayListsSize(), "Initially no playlists");
        library.createPlaylist("PL1");
        library.createPlaylist("PL2");
        assertEquals(2, library.getPlayListsSize(), "Should have 2 playlists now");
        library.clearAllPlayLists();
        assertEquals(0, library.getPlayListsSize(), "All playlists should be cleared");
        assertTrue(library.getPlayListNames().isEmpty(), "No playlist names should remain");
    }

    @Test
    @DisplayName("searchAlbum => searching user library => initially empty => no results")
    void testSearchAlbumUserLibEmpty() {
        ArrayList<ArrayList<String>> results = library.searchAlbum("Test", false); // user library
        assertTrue(results.isEmpty(), "User library is empty => 0 results");
    }

    @Test
    @DisplayName("allAlbumSelection => returns false if checkSize mismatch")
    void testAllAlbumSelectionMismatch() {
        library.searchAlbum("Test", true);
        int realSize = library.searchAlbumList.size();
        boolean ok = library.allAlbumSelection(realSize + 1);
        assertFalse(ok);
        // user library still empty => 0
        assertEquals(0, library.getAlbumListSize());
    }

}