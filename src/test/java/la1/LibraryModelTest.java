package la1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;

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
        Album albumA = createSampleAlbum("A", "AA", "Rock", 2001, 2);
        Album albumB = createSampleAlbum("B", "BB", "Pop", 2020, 1);
        store.getAlbumMap().put(store.generateKey("A", "AA"), albumA);
        store.getAlbumMap().put(store.generateKey("B", "BB"), albumB);
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

    private void setCurrentSongField(Song song) {
        try {
            Field f = LibraryModel.class.getDeclaredField("currentSong");
            f.setAccessible(true);
            f.set(library, song);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("currentSong: " + e.getMessage());
        }
    }

    private void setCurrentAlbumField(Album album) {
        try {
            Field f = LibraryModel.class.getDeclaredField("currentAlbum");
            f.setAccessible(true);
            f.set(library, album);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("currentAlbum: " + e.getMessage());
        }
    }

    private void setCurrentPlaylistField(Playlist playlist) {
        try {
            Field f=LibraryModel.class.getDeclaredField("currentPlaylist");
            f.setAccessible(true);
            f.set(library, playlist);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("currentPlaylist: " + e.getMessage());
        }
    }

    private Playlist getPlayingListField() {
        try {
            Field f=LibraryModel.class.getDeclaredField("playingList");
            f.setAccessible(true);
            return (Playlist) f.get(library);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("playingList: " + e.getMessage());
            return null;
        }
    }

    @Test
    void testPlayAlbumNullCurrentAlbum() {
        boolean result = library.playAlbum("AnyTitle");
        assertFalse(result);
    }

    @Test
    void testPlayAlbumTitleMismatch() {
        Album album = new Album("Real Album", "Any Artist", "Pop", 2000, new ArrayList<>());
        setCurrentAlbumField(album);

        boolean result = library.playAlbum("Wrong Title");
        assertFalse(result);
    }

    @Test
    void testPlayAlbumHappyPath() {
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(new Song("Song1", "Artist", "Rock", 2000));
        songs.add(new Song("Song2", "Artist", "Rock", 2001));
        Album album = new Album("MyAlbum", "Artist", "Rock", 2020, songs);

        setCurrentAlbumField(album);
        boolean ok = library.playAlbum("MyAlbum");
        assertTrue(ok);
        Playlist playing = getPlayingListField();
        assertEquals(2, playing.getSize());
        assertEquals("Song1", playing.getSongs().get(0).getTitle());
        assertEquals("Song2", playing.getSongs().get(1).getTitle());
    }

    @Test
    void testAddSongToPlayListsNoPlaylist() {
        setCurrentPlaylistField(null);
        setCurrentSongField(new Song("Any", "Artist"));
        library.addSongToPlayLists();
    }

    @Test
    void testAddSongToPlayListsNoSong() {
        library.createPlaylist("MyPlaylist");
        library.selectCurrentPlaylist(0);
        setCurrentSongField(null);
        library.addSongToPlayLists();
    }

    @Test
    void testAddSongToPlayListsSuccess() {
        library.createPlaylist("MyPlaylist");
        library.selectCurrentPlaylist(0);
        setCurrentSongField(new Song("SongX", "ArtistX"));
        library.addSongToPlayLists();
        assertEquals(1, library.currentPlistSize());
    }

    @Test
    void testRemoveSongFromPlayListsNoPlaylist() {
        setCurrentPlaylistField(null);
        setCurrentSongField(new Song("SongX", "ArtistX"));
        library.removeSongFromPlayLists();
    }

    @Test
    void testRemoveSongFromPlayListsNoSong() {
        library.createPlaylist("PL");
        library.selectCurrentPlaylist(0);
        setCurrentSongField(null);
        library.removeSongFromPlayLists();
    }

    @Test
    void testRemoveSongFromPlayListsSuccess() {
        library.createPlaylist("PL");
        library.selectCurrentPlaylist(0);
        Song s = new Song("SongA", "ArtistA");
        setCurrentSongField(s);
        library.addSongToPlayLists();
        library.removeSongFromPlayLists();
        assertEquals(0, library.currentPlistSize());
    }
    @Test
    void testRemovePlayListValidIndex() {
        library.createPlaylist("A");
        library.createPlaylist("B");
        assertEquals(2, library.getPlayListsSize());
        Boolean removed = library.removePlayList(0);
        assertTrue(removed);
        assertEquals(1, library.getPlayListsSize());
        removed = library.removePlayList(0);
        assertTrue(removed);
        assertEquals(0, library.getPlayListsSize());
    }

    @Test
    void testRemovePlayListOutOfRange() {
        library.createPlaylist("OnlyOne");
        assertEquals(1, library.getPlayListsSize());
        assertThrows(IndexOutOfBoundsException.class, () -> {
            library.removePlayList(5);
        });
    }
    @Test
    void testIsFavourite() {
        Song s = new Song("TestSong", "TestArtist");
        s.setFavourite(true);
        setCurrentSongField(s);
        assertTrue(library.isFavourite());
        s.setFavourite(false);
        assertFalse(library.isFavourite());
    }
    @Test
    void testGetFavoriteListSize() {
        assertEquals(0, library.getFavoriteListSize());
        Song s = new Song("SongA", "ArtistA");
        setCurrentSongField(s);
        library.addFavourite();
        assertEquals(1, library.getFavoriteListSize());
    }
    @Test
    void testGetFavoriteListSongs() {
        Song s1=new Song("Song1", "Artist1");
        Song s2=new Song("Song2", "Artist2");
        setCurrentSongField(s1);
        library.addFavourite();
        setCurrentSongField(s2);
        library.addFavourite();
        ArrayList<ArrayList<String>> favSongs = library.getFavoriteListSongs();
        assertEquals(2, favSongs.size());
        assertTrue(favSongs.get(0).contains("Song1"));
        assertTrue(favSongs.get(1).contains("Song2"));
    }
    @Test
    void testPrintFavoriteList() {
        library.printFavoriteList("title");
        Song s=new Song("SongA", "ArtistA");
        setCurrentSongField(s);
        library.addFavourite();
        library.printFavoriteList("rating");
    }
    @Test
    void testClearFavoriteList() {
        Song s1 = new Song("Song1", "Artist1");
        Song s2 = new Song("Song2", "Artist2");
        setCurrentSongField(s1);
        library.addFavourite();
        setCurrentSongField(s2);
        library.addFavourite();
        assertEquals(2, library.getFavoriteListSize());
        library.clearFavoriteList();
        assertEquals(0, library.getFavoriteListSize());
        assertFalse(s1.isFavourite());
        assertFalse(s2.isFavourite());
    }
    @Test
    void testAddFavouriteNoSong() {
        setCurrentSongField(null);
        library.addFavourite();
        assertEquals(0, library.getFavoriteListSize());
    }
    @Test
    void testAddFavouriteSuccess() {
        Song s = new Song("SongX", "ArtistX");
        setCurrentSongField(s);
        library.addFavourite();
        assertEquals(1, library.getFavoriteListSize());
        assertTrue(library.isFavourite());
    }
    @Test
    void testRemoveFavouriteNoSong() {
        setCurrentSongField(null);
        library.removeFavourite();
    }
    @Test
    void testRemoveFavouriteSuccess() {
        Song s = new Song("Rem", "ARem");
        setCurrentSongField(s);
        library.addFavourite();
        assertEquals(1, library.getFavoriteListSize());
        library.removeFavourite();
        assertFalse(s.isFavourite());
        assertEquals(0, library.getFavoriteListSize());
    }
    @Test
    void testPlayFavoriteList() {
        Song s1 = new Song("Song1", "Artist1");
        Song s2 = new Song("Song2", "Artist2");
        setCurrentSongField(s1);
        library.addFavourite();
        setCurrentSongField(s2);
        library.addFavourite();
        library.playFavoriteList();
        Playlist playing = getPlayingListField();
        assertEquals(2, playing.getSize());
        assertEquals("Song1", playing.getSongs().get(0).getTitle());
        assertEquals("Song2", playing.getSongs().get(1).getTitle());
    }
    @Test
    void testShufflePlaylist() {
        Song s1 = new Song("Song1", "Artist1");
        Song s2 = new Song("Song2", "Artist2");
        setCurrentSongField(s1);
        library.playSong();
        setCurrentSongField(s2);
        library.playSong();
        assertEquals(2, getPlayingListField().getSize());
        library.printUserSongsTable("shuffle");
    }
    @Test
    void testPrintRating() {
        Song s = new Song("RateMe", "ArtistRate");
        setCurrentSongField(s);
        String ratingText = library.printRating();
        assertTrue(ratingText.contains("RateMe["));
    }
    @Test
    void testSetRating() {
        Song s = new Song("SongRate","ArtistR");
        setCurrentSongField(s);
        library.setRating(4);
        assertEquals(4, s.getRatingInt());
    }
    @Test
    void testGetRating() {
        Song s = new Song("SongRate","ArtistR");
        s.setRating(Rating.THREE);
        setCurrentSongField(s);

        assertEquals(3,library.getRating());
    }
    @Test
    void testPlayFavoriteListRemovesExistingSong() {
        Song songA = new Song("SongA","ArtistA");
        Song songB = new Song("SongB","ArtistB");
        setCurrentSongField(songA);
        library.addFavourite();
        setCurrentSongField(songB);
        library.addFavourite();
        Playlist playing = getPlayingListField();
        playing.addSong(songB, false);
        library.playFavoriteList();
        assertEquals(2, playing.getSize());
        assertSame(songA, playing.getSongs().get(0));
        assertSame(songB, playing.getSongs().get(1));
    }
    @Test
    void testGetPlayerSize() {
        assertEquals(0, library.getPlayerSize());
        Song s = new Song("Song A", "AA");
        setCurrentSongField(s);
        library.playSong();
        assertEquals(1, library.getPlayerSize());
    }
    @Test
    void testGetPlaying() {
        library.getPlaying();
        Song s = new Song("SongX","ArtistX");
        setCurrentSongField(s);
        library.playSong();
        library.getPlaying();
    }
    @Test
    void testPlayNextSong() {
        library.playNextSong();
        Song s1 = new Song("Song1","Artist1");
        Song s2 = new Song("Song2","Artist2");
        setCurrentSongField(s1);
        library.playSong();
        setCurrentSongField(s2);
        library.playSong();
        library.playNextSong();
        Playlist playing = getPlayingListField();
        assertEquals(1, playing.getSize());
        assertEquals("Song1", playing.getSongs().get(0).getTitle());
    }
    @Test
    void testClearPlaylist() {
        Song s1 = new Song("Song1", "Artist1");
        setCurrentSongField(s1);
        library.playSong();
        assertEquals(1, library.getPlayerSize());
        library.clearPlaylist();
        assertEquals(0, library.getPlayerSize());
    }
    @Test
    void testAddSongToPlaylistNoSong() {
        setCurrentSongField(null);
        library.addSongToPlaylist();
        assertEquals(0, library.getPlayerSize());
    }

    @Test
    void testAddSongToPlaylistSuccess() {
        Song s = new Song("SongA", "ArtistA");
        setCurrentSongField(s);
        library.addSongToPlaylist();
        assertEquals(1, library.getPlayerSize());
    }
    @Test
    void testRemoveSongFromPlaylistNoSong() {
        setCurrentSongField(null);
        library.removeSongFromPlaylist();
    }
    @Test
    void testRemoveSongFromPlaylistSuccess() {
        Song s = new Song("Rem", "ARem");
        setCurrentSongField(s);
        library.addSongToPlaylist();
        assertEquals(1, library.getPlayerSize());
        library.removeSongFromPlaylist();
        assertEquals(0, library.getPlayerSize());
    }
    @Test
    void testGetPlaylistSongs() {
        ArrayList<ArrayList<String>> songsEmpty = library.getPlaylistSongs();
        assertTrue(songsEmpty.isEmpty());
        Song s = new Song("SongA", "ArtistA");
        setCurrentSongField(s);
        library.playSong();
        ArrayList<ArrayList<String>> songsList = library.getPlaylistSongs();
        assertEquals(1, songsList.size());
        assertTrue(songsList.get(0).contains("SongA"));
    }
    @Test
    void testGetPlaylist() {
        assertTrue(library.getPlaylist().isEmpty());
        Song s =new Song("SongB", "ArtistB");
        setCurrentSongField(s);
        library.playSong();
        assertEquals(1, library.getPlaylist().size());
        assertEquals("SongB", library.getPlaylist().get(0).getTitle());
    }
    @Test
    void testGetPlayListNames() {
        assertTrue(library.getPlayListNames().isEmpty());
        library.createPlaylist("PL1");
        library.createPlaylist("PL2");
        ArrayList<String> names = library.getPlayListNames();
        assertTrue(names.contains("PL1"));
        assertTrue(names.contains("PL2"));
    }
    @Test
    void testPrintAllPlayLists() {
        library.printAllPlayLists("rating");
        library.createPlaylist("MyPL");
        library.printAllPlayLists("artist");
    }
    @Test
    void testGetCurrentPlayList() {
        ArrayList<ArrayList<String>> emptyPL = library.getCurrentPlayList();
        assertTrue(emptyPL.isEmpty());
        Song s1 = new Song("Song1", "Artist1");
        setCurrentSongField(s1);
        library.playSong();
        ArrayList<ArrayList<String>> result = library.getCurrentPlayList();
        assertEquals(1, result.size());
        assertTrue(result.get(0).contains("Song1"));
    }
    @Test
    void testPrintPlaylist() {
        library.printPlaylist("xxx");
        Song s2 = new Song("Song2", "Artist2");
        setCurrentSongField(s2);
        library.playSong();
        library.printPlaylist("title");
    }
    @Test
    void testPlaySong() {
        setCurrentSongField(null);
        library.playSong();
        Song s = new Song("SongXYZ", "ArtistXYZ");
        setCurrentSongField(s);
        library.playSong();
        assertEquals(1, library.getPlayerSize());
        assertEquals("SongXYZ", getPlayingListField().getSongs().get(0).getTitle());
    }
    @Test
    void testSearchSongInStore() {
        ArrayList<ArrayList<String>> results = library.searchSong("StoreSongA", true);
        assertEquals(results.size(), library.getSearchSongListSize());
        assertEquals(1, results.size());
        assertTrue(results.get(0).contains("StoreSongA"));
    }

    @Test
    void testSearchSongInUserLibrary() {
        ArrayList<ArrayList<String>> results=library.searchSong("StoreSongA",false);
        assertTrue(results.isEmpty());
    }
    @Test
    void testSearchSongSub() {
        ArrayList<Song> fromStore=library.searchSongSub("StoreSongA",true);
        assertEquals(1,fromStore.size());
        assertEquals("StoreSongA", fromStore.get(0).getTitle());
        ArrayList<Song> fromUserLib = library.searchSongSub("StoreSongA",false);
        assertTrue(fromUserLib.isEmpty());
    }
    @Test
    void testAllSongSelection() {
        library.searchSong("StoreSongA",true);
        int realSize = library.getSearchSongListSize();
        assertTrue(realSize >= 1);
        boolean ok = library.allSongSelection(realSize);
        assertTrue(ok);
        assertEquals(realSize, library.getSongListSize());
    }

    @Test
    void testAllSongSelectionMismatch() {
        library.searchSong("StoreSong",true);
        int realSize = library.getSearchSongListSize();
        boolean ok = library.allSongSelection(realSize + 1);
        assertFalse(ok);
        assertEquals(0, library.getSongListSize());
    }
    @Test
    void testHandleSongSelectionSuccess() {
        library.searchSong("StoreSongA",true);
        int realSize = library.getSearchSongListSize();
        assertTrue(realSize > 0);
        boolean ok = library.handleSongSelection(0,realSize);
        assertTrue(ok);
        assertEquals(1, library.getSongListSize());
    }
    @Test
    void testHandleSongSelectionMismatch() {
        library.searchSong("StoreSongA", true);
        int realSize = library.getSearchSongListSize();
        boolean ok = library.handleSongSelection(0, realSize + 10);
        assertFalse(ok);
        assertEquals(0, library.getSongListSize());
    }
    @Test
    void testGetSongListSize() {
        assertEquals(0, library.getSongListSize());
        library.searchSong("StoreSongA", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        assertEquals(1, library.getSongListSize());
    }

    @Test
    void testPrintUserSongsTable() {
        library.printUserSongsTable("rating");
        library.searchSong("StoreSongA", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.printUserSongsTable("title");
    }
    @Test
    void testGetSongList() {
        assertTrue(library.getSongList().isEmpty());
        library.searchSong("StoreSongA", true);
        int size = library.getSearchSongListSize();
        library.allSongSelection(size);
        ArrayList<ArrayList<String>> userSongList = library.getSongList();
        assertEquals(1, userSongList.size());
        assertTrue(userSongList.get(0).contains("StoreSongA") || userSongList.get(0).contains("StoreSongB"));
    }

    @Test
    void testCheckCurrentSong() {
        library.searchSong("StoreSongA", true);
        library.handleSongSelection(0,library.getSearchSongListSize());
        library.userSongSearch();
        library.setCurrentSong(0,"StoreSongA");
        assertTrue(library.checkCurrentSong("StoreSongA"));
        assertFalse(library.checkCurrentSong("NonExistingSong"));
    }

    @Test
    void testSetCurrentSong() {
        library.searchSong("StoreSongB", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.userSongSearch();;
        assertTrue(library.setCurrentSong(0, "StoreSongB"));
        assertFalse(library.setCurrentSong(0, "WrongTitle"));
    }

    @Test
    void testGetCurrentSongInfo() {
        library.searchSong("StoreSongA",true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.userSongSearch();
        library.setCurrentSong(0, "StoreSongA");

        String info = library.getCurrentSongInfo();
        assertNotNull(info);
        assertTrue(info.contains("StoreSongA"));
    }

    @Test
    void testGetCurrentSongTitle() {
        library.searchSong("StoreSongB",true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.userSongSearch();
        library.setCurrentSong(0, "StoreSongB");
        assertEquals("StoreSongB",library.getCurrentSongTitle());
    }

    @Test
    void testSetCurrentSongWithoutCheck() {
        library.searchSong("StoreSongA",true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.userSongSearch();
        library.setCurrentSongWithoutCheck(0);
        assertTrue(library.getCurrentSongInfo().contains("StoreSongA"));
    }
    @Test
    void testUserSongSerch() {
        library.searchSong("StoreSongA",true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.userSongSearch();

        assertEquals(1, library.getSearchSongListSize());
    }
    @Test
    void testPlayListSearch() {
        library.searchSong("StoreSongA",true);
        library.handleSongSelection(0,library.getSearchSongListSize());
        library.userSongSearch();
        library.setCurrentSong(0,"StoreSongA");
        library.playSong();

        library.playListSearch();
        assertEquals(1,library.getSearchSongListSize());
    }
    @Test
    void testFavoriteListSearch() {
        library.searchSong("StoreSongA", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.userSongSearch();
        library.setCurrentSong(0, "StoreSongA");
        library.addFavourite();

        library.favoriteListSearch();
        assertEquals(1, library.getSearchSongListSize());
    }
    @Test
    void testGetSearchSongListSize() {
        library.searchSong("StoreSongA", true);
        assertEquals(1, library.getSearchSongListSize());
    }
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
    void testPrintAllArtistsEmptyLibrary() {
        library.printAllArtists();
        System.out.flush();
        String captured = outContent.toString();
        System.out.println("Captured Output >>>" + captured + "<<<");
        assertTrue(captured.contains("The library is empty [WARNING]"));
    }
    @Test
    void testClearAllPlayLists() {
        assertEquals(0, library.getPlayListsSize());
        library.createPlaylist("PL1");
        library.createPlaylist("PL2");
        assertEquals(2, library.getPlayListsSize());
        library.clearAllPlayLists();
        assertEquals(0, library.getPlayListsSize());
        assertTrue(library.getPlayListNames().isEmpty());
    }

    @Test
    void testSearchAlbumUserLibEmpty() {
        ArrayList<ArrayList<String>> results = library.searchAlbum("Test", false); // user library
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("AAAAAAAAAAAAAA")
    void testAllAlbumSelectionMismatch() {
        library.searchAlbum("Test", true);
        int realSize = library.getSearchAlbumList().size();
        boolean ok = library.allAlbumSelection(realSize + 1);
        assertFalse(ok);
        assertEquals(0, library.getAlbumListSize());
    }
    @Test
    void testGetID() {
        assertEquals("TestUser",library.getUserID());
    }
    @Test
    void testCheckCurrentAlbum() {
        library.searchAlbum("adele",true);
        library.allAlbumSelection(2);
        assertEquals(2, library.getSearchAlbumList().size());
        library.setCurrentAlbum(0, "19");
        assertTrue(library.checkCurrentAlbum("19"));
        assertFalse(library.checkCurrentAlbum("21"));
    }
    @Test
    void testSetCurrentAlbumEmpty() {
        library.searchAlbum("adele", false);
        assertEquals(2, library.getSearchAlbumList().size());
        assertFalse(library.setCurrentAlbum(0, "21"));
    }
    @Test
    void testHandleAlbumSelection() {
        library.searchAlbum("1", true);
        assertFalse(library.handleAlbumSelection(1, 0));
        assertFalse(library.handleAlbumSelection(1, 1));
        library.searchAlbum("adele", true);
        library.allAlbumSelection(2);
        assertEquals(2, library.getSearchAlbumList().size());
        assertTrue(library.handleAlbumSelection(0, 2));
        assertEquals(2, library.getAlbumListSize());
    }
    @Test
    void testOpenAlbum() {
        library.searchAlbum("adele",true);
        library.allAlbumSelection(2);
        library.setCurrentAlbum(0, "19");
        assertFalse(library.openAlbum("19", "rating"));
        library.openAlbum("19", "title");
        assertEquals(12, library.getSearchSongListSize());
    }
    @Test
    void testGetAlbumList () {
        library.searchAlbum("adele", true);
        library.allAlbumSelection(2);
        assertEquals(2, library.getAlbumListSize());
    }

    @Test
    void testPlayListsCurrent() {
        library.searchAlbum("adele",true);
        library.allAlbumSelection(2);
        library.userSongSearch();
        library.setCurrentSong(0, "Someone Like You");
        library.createPlaylist("tests");
        library.selectCurrentPlaylist(0);
        library.addSongToPlayLists();
        library.playListsCurrent();
        assertEquals(1, library.getSearchSongListSize());
    }

    @Test
    void testGetAlbumListSearch() {
        // Setup
        LibraryModel library = new LibraryModel("TestUser", new MusicStore());
        Album album1 = new Album("Album1", "Artist1", "Genre1", 2021, new ArrayList<>());
        Album album2 = new Album("Album2", "Artist2", "Genre2", 2022, new ArrayList<>());
        library.addAlbum(album1);
        library.addAlbum(album2);

        // Execute
        ArrayList<ArrayList<String>> albumList = library.getAlbumList();

        // Verify
        assertEquals(2, albumList.size());
        assertTrue(albumList.get(0).contains("Album1"));
        assertTrue(albumList.get(0).contains("Artist1"));
        assertTrue(albumList.get(1).contains("Album2"));
        assertTrue(albumList.get(1).contains("Artist2"));
    }

    @Test
    void testSongToString() throws Exception {
        Field searchSongListField = LibraryModel.class.getDeclaredField("searchSongList");
        searchSongListField.setAccessible(true);
        ArrayList<Song> searchSongs = new ArrayList<>();
        Song song1 = new Song("Beta", "ArtistA", "Rock", 2012);
        Song song2 = new Song("Alpha", "ArtistB", "Pop", 2010);
        Song song3 = new Song("Alpha", "ArtistA", "Pop", 2010);
        searchSongs.add(song1);
        searchSongs.add(song2);
        searchSongs.add(song3);
        searchSongListField.set(null, searchSongs);
        Field currentAlbumField = LibraryModel.class.getDeclaredField("currentAlbum");
        currentAlbumField.setAccessible(true);
        Album album = createSampleAlbum("AlbumX", "ArtistX", "Jazz", 2005, 3);
        currentAlbumField.set(library, album);
        ArrayList<ArrayList<String>> result = library.SongToString();
        ArrayList<String> albumInfo = album.getAlbumInfo();
        assertEquals(albumInfo, result.get(0));
        assertEquals(song3.toStringList(), result.get(1));
        assertEquals(song2.toStringList(), result.get(2));
        assertEquals(song1.toStringList(), result.get(3));
    }

    @Test
    void testRemoveSongFromLibrary() throws Exception {
        Song song = new Song("TestSong", "TestArtist", "Pop", 2020);
        String key = library.generateKey(song);
        Field userSongsField = LibraryModel.class.getDeclaredField("UserSongs");
        userSongsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        HashMap<String, Song> userSongs = (HashMap<String, Song>) userSongsField.get(library);
        userSongs.put(key, song);
        Field currentSongField = LibraryModel.class.getDeclaredField("currentSong");
        currentSongField.setAccessible(true);
        currentSongField.set(library, song);
        boolean removed = library.removeSongFromLibrary();
        assertTrue(removed);
        assertNull(currentSongField.get(library));
        @SuppressWarnings("unchecked")
        HashMap<String, Song> updatedUserSongs = (HashMap<String, Song>) userSongsField.get(library);
        assertFalse(updatedUserSongs.containsKey(key));
    }

    @Test
    void testPlayCurrentPlayList() throws Exception {
        Playlist playlist = new Playlist("TestPlaylist");
        Song song1 = new Song("Song1", "Artist1", "Pop", 2010);
        Song song2 = new Song("Song2", "Artist2", "Rock", 2011);
        playlist.addSong(song1, false);
        playlist.addSong(song2, false);
        Field currentPlaylistField = LibraryModel.class.getDeclaredField("currentPlaylist");
        currentPlaylistField.setAccessible(true);
        currentPlaylistField.set(library, playlist);
        Field playingListField = LibraryModel.class.getDeclaredField("playingList");
        playingListField.setAccessible(true);
        Playlist playingList = (Playlist) playingListField.get(library);
        playingList.clear(false);
        Field playCountsField = LibraryModel.class.getDeclaredField("playCounts");
        playCountsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        HashMap<String, Integer> playCounts = (HashMap<String, Integer>) playCountsField.get(library);
        playCounts.clear();
        Field recentPlaysField = LibraryModel.class.getDeclaredField("recentPlays");
        recentPlaysField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ArrayList<Song> recentPlays = (ArrayList<Song>) recentPlaysField.get(library);
        recentPlays.clear();
        library.playCurrentPlayList();
        String key1 = library.generateKey(song1);
        String key2 = library.generateKey(song2);
        assertEquals(1, playCounts.get(key1));
        assertEquals(1, playCounts.get(key2));
        assertTrue(recentPlays.contains(song1));
        assertTrue(recentPlays.contains(song2));
        assertTrue(playingList.getSongs().contains(song1));
        assertTrue(playingList.getSongs().contains(song2));
    }

    @Test
    void testGetTopFrequentSongs() throws Exception {
        Field userSongsField = LibraryModel.class.getDeclaredField("UserSongs");
        userSongsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        HashMap<String, Song> userSongs = (HashMap<String, Song>) userSongsField.get(library);
        Song song1 = new Song("Song1", "Artist1", "Pop", 2010);
        Song song2 = new Song("Song2", "Artist2", "Rock", 2011);
        Song song3 = new Song("Song3", "Artist3", "Jazz", 2012);
        userSongs.put(library.generateKey(song1), song1);
        userSongs.put(library.generateKey(song2), song2);
        userSongs.put(library.generateKey(song3), song3);
        Field playCountsField = LibraryModel.class.getDeclaredField("playCounts");
        playCountsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        HashMap<String, Integer> playCounts = (HashMap<String, Integer>) playCountsField.get(library);
        playCounts.clear();
        playCounts.put(library.generateKey(song1), 5);
        playCounts.put(library.generateKey(song2), 10);
        playCounts.put(library.generateKey(song3), 7);
        List<Song> topSongs = library.getTopFrequentSongs();
        assertEquals(3, topSongs.size());
        assertEquals(song2, topSongs.get(0));
        assertEquals(song3, topSongs.get(1));
        assertEquals(song1, topSongs.get(2));
    }

    @Test
    void testPrintFrequentSongsEmpty() {
        try {
            Field playCountsField = LibraryModel.class.getDeclaredField("playCounts");
            playCountsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<String, Integer> playCounts = (HashMap<String, Integer>) playCountsField.get(library);
            playCounts.clear();
        } catch (Exception e) {
            fail(e);
        }
        library.printFrequentSongs();
        String output = outContent.toString();
        assertTrue(output.contains("❗No frequent songs to display."));
        outContent.reset();
    }

    @Test
    void testGetRecentSongs() throws Exception {
        Field recentPlaysField = LibraryModel.class.getDeclaredField("recentPlays");
        recentPlaysField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ArrayList<Song> recentPlays = (ArrayList<Song>) recentPlaysField.get(library);
        recentPlays.clear();
        Song song1 = new Song("Recent1", "Artist1", "Pop", 2010);
        Song song2 = new Song("Recent2", "Artist2", "Rock", 2011);
        recentPlays.add(song1);
        recentPlays.add(song2);
        List<Song> returnedRecent = library.getRecentSongs();
        assertEquals(2, returnedRecent.size());
        assertEquals(song1, returnedRecent.get(0));
        assertEquals(song2, returnedRecent.get(1));
    }

    @Test
    void testPrintRecentSongsEmpty() {
        try {
            Field recentPlaysField = LibraryModel.class.getDeclaredField("recentPlays");
            recentPlaysField.setAccessible(true);
            @SuppressWarnings("unchecked")
            ArrayList<Song> recentPlays = (ArrayList<Song>) recentPlaysField.get(library);
            recentPlays.clear();
        } catch (Exception e) {
            fail(e);
        }
        library.printRecentSongs();
        String output = outContent.toString();
        assertTrue(output.contains("❗No recent songs to display."));
        outContent.reset();
    }

    @Test
    void testGenerateAutomaticPlaylists() throws Exception {
        Field userSongsField = LibraryModel.class.getDeclaredField("UserSongs");
        userSongsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        HashMap<String, Song> userSongs = (HashMap<String, Song>) userSongsField.get(library);
        Song songFav = new Song("FavSong", "FavArtist", "Pop", 2020);
        songFav.setRating(Rating.FIVE);
        Song songOther = new Song("OtherSong", "OtherArtist", "Pop", 2020);
        songOther.setRating(Rating.THREE);
        userSongs.put(library.generateKey(songFav), songFav);
        userSongs.put(library.generateKey(songOther), songOther);
        Field favoriteListField = LibraryModel.class.getDeclaredField("favoriteList");
        favoriteListField.setAccessible(true);
        FavoriteList favoriteList = (FavoriteList) favoriteListField.get(library);
        favoriteList.getSongs().clear();
        favoriteList.getSongs().add(songFav);
        Field playlistsField = LibraryModel.class.getDeclaredField("PlayLists");
        playlistsField.setAccessible(true);
        PlayLists playlists = (PlayLists) playlistsField.get(library);
        library.generateAutomaticPlaylists();
        assertTrue(playlists.getPlayListNames().contains("Favorite Songs"));
        assertTrue(playlists.getPlayListNames().contains("Top Rated"));
    }

    @Test
    void testPrintSongSearchResults() {
        ArrayList<ArrayList<String>> songResults = new ArrayList<>();
        ArrayList<String> row1 = new ArrayList<>();
        row1.add("SongA");
        row1.add("ArtistA");
        row1.add("Pop");
        row1.add("2000");
        row1.add("♡");
        row1.add("★★★☆☆");
        songResults.add(row1);
        LibraryModel.printSongSearchResults("Test Songs", songResults, "LIBRARY", "title");
        String output = outContent.toString();
        assertTrue(output.contains("Test Songs"));
        assertTrue(output.contains("SongA"));
        outContent.reset();
    }

    @Test
    void testSortSearchSongList() throws Exception {
        Field searchSongListField = LibraryModel.class.getDeclaredField("searchSongList");
        searchSongListField.setAccessible(true);
        ArrayList<Song> searchSongs = new ArrayList<>();
        Song song1 = new Song("Beta", "ArtistB", "Rock", 2010);
        Song song2 = new Song("Alpha", "ArtistA", "Pop", 2010);
        searchSongs.add(song1);
        searchSongs.add(song2);
        searchSongListField.set(null, searchSongs);
        LibraryModel.sortSearchSongList("title");
        @SuppressWarnings("unchecked")
        ArrayList<Song> sortedSongs = (ArrayList<Song>) searchSongListField.get(null);
        assertEquals("Alpha", sortedSongs.get(0).getTitle());
        assertEquals("Beta", sortedSongs.get(1).getTitle());
    }

    @Test
    void testPrintAlbumSearchResults() {
        ArrayList<ArrayList<String>> albumResults = new ArrayList<>();
        ArrayList<String> albumRow = new ArrayList<>();
        albumRow.add("Album1");
        albumRow.add("Artist1");
        albumRow.add("2000");
        albumRow.add("Pop");
        albumRow.add("Track1");
        albumRow.add("Track2");
        albumResults.add(albumRow);
        LibraryModel.printAlbumSearchResults(albumResults);
        String output = outContent.toString();
        assertTrue(output.contains("Album Search Results"));
        assertTrue(output.contains("Album1"));
        outContent.reset();
    }

}