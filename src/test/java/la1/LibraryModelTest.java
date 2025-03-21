package la1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static la1.LibraryModel.*;
import static la1.MainUI.printSongSearchResults;
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
    void testGetSearchSongList() {
        library.searchSong("adele", true);
        assertEquals(24, getSearchSongList().size());
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
        album.addAllSongsToLibrary();
        boolean ok = library.playAlbum("MyAlbum");
        assertTrue(ok);
        Playlist playing = getPlayingListField();
        assertEquals(2, playing.getSize());
        assertEquals("Song1", playing.getSongs().get(0).getTitle());
        assertEquals("Song2", playing.getSongs().get(1).getTitle());
    }
    @Test
    @DisplayName("Test playAlbum with >10 songs: recentPlays is truncated to 10 and order is as expected")
    public void testPlayAlbumRecentPlaysOverflow() {
        ArrayList<Song> albumSongs = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            albumSongs.add(new Song("Song" + i, "Artist", "Rock", 2000 + i));
        }
        Album album = new Album("MyAlbum", "Artist", "Rock", 2020, albumSongs);
        setCurrentAlbumField(album);
        // Also add all album songs to the library.
        album.addAllSongsToLibrary();
        boolean played = library.playAlbum("MyAlbum");
        assertTrue(played, "playAlbum should return true for a valid album");
        List<Song> recent = library.getRecentSongs();
        assertEquals(10, recent.size(), "recentPlays should be truncated to 10 songs");

        for (int i = 0; i < 10; i++) {
            assertEquals("Song" + (i + 1), recent.get(i).getTitle(),
                    "Expected recent play at index " + i + " to be Song" + (i + 1));
        }
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
        String key = "key";
        library.printCurrentPlaylist(key);
        assertFalse(outContent.toString().contains("key"));

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
        library.printUserSongsTable("aa");
        library.printUserSongsTable("artist");
        library.printUserSongsTable("title");
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
        Song s1 = new Song("SongA","ArtistX");
        setCurrentSongField(s1);
        Song s2 = new Song("SongB","ArtistX");
        setCurrentSongField(s2);
        Song s3 = new Song("SongC","ArtistX");
        setCurrentSongField(s3);
        Song s4 = new Song("SongD","ArtistX");
        setCurrentSongField(s4);
        Song s5 = new Song("SongE","ArtistX");
        setCurrentSongField(s5);
        Song s6 = new Song("SongF","ArtistX");
        setCurrentSongField(s6);
        Song s7 = new Song("SongG","ArtistX");
        setCurrentSongField(s7);
        Song s8 = new Song("SongH","ArtistX");
        setCurrentSongField(s8);
        Song s9 = new Song("SongXG","ArtistX");
        setCurrentSongField(s9);
        Song s10 = new Song("SongM","ArtistX");
        setCurrentSongField(s10);
        Song s101 = new Song("SongMGG","ArtistX");
        setCurrentSongField(s101);
        Song sG = new Song("SongGG","ArtistX");
        setCurrentSongField(sG);
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

        library.searchSong("adele", true);
        library.allSongSelection(24);
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
    void testRemoveSongFromUserLibrary() {
        assertFalse(library.removeSongFromLibrary());
        ArrayList<Song> songs = new ArrayList<>();
        Song song1 = new Song("Song1", "Artist1", "Rock", 2000);
        songs.add(song1);
        Album album = new Album("AlbumX", "ArtistX", "Jazz", 2005, songs);
        song1.setAlbum(album);
        album.addAllSongsToLibrary();
        library.addAlbum(album);
        library.searchSong("Song1", false);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.userSongSearch();
        library.setCurrentSong(0, "Song1");
        assertTrue(library.removeSongFromLibrary());
        Song song2 = new Song("Song2", "Artist2", "Rock", 2000);
        library.addSong(song2);
        library.searchSong("Song2", false);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.userSongSearch();
        library.setCurrentSong(0, "Song2");
        assertTrue(library.removeSongFromLibrary());
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
    void testGetCurrentAlbumInfo() {
        library.searchSong("adele", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.handleSongSelection(1, library.getSearchSongListSize());
        library.searchSong("adele", false);
        library.userSongSearch();
        library.setCurrentSong(0, "Crazy for You");
        library.getSongAlbum();
        String output = "19, by Adele (2008, Pop),  Not all songs are in the library.";
        assertEquals(output, library.getCurrentAlbumInfo());
        assertFalse(library.isCurrentAlbumComplete());
    }


    @Test
    void testCheckCurrentAlbum() {
        library.searchAlbum("adele",true);
        library.allAlbumSelection(2);
        assertEquals(2, library.getSearchAlbumList().size());
        library.setCurrentAlbum(1, "19");
        assertTrue(library.checkCurrentAlbum("19"));
        assertFalse(library.checkCurrentAlbum("21"));
    }
    @Test
    void testSetCurrentAlbumEmpty() {
        library.searchAlbum("adele", true);
        library.allAlbumSelection(2);
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
        library.setCurrentAlbum(1, "19");
        assertFalse(library.openAlbum("21", "rating"));
        library.openAlbum("19", "title");
        assertEquals(12, library.getSearchSongListSize());
    }

    @Test
    void testShowCompleteAlbumStore() {
        library.searchSong("coldplay", true);
        library.handleSongSelection(0, library.getSearchSongListSize());
        library.handleSongSelection(1, library.getSearchSongListSize());
        library.searchSong("coldplay", false);
        library.userSongSearch();
        library.setCurrentSong(0, "Politik");
        library.getSongAlbum();
        assertTrue(library.showCompleteAlbumStore("A Rush of Blood to the Head"));
        assertFalse(library.showCompleteAlbumStore("X&Y"));

    }

    @Test
    void testRemoveAlbumFromLibrary() {
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(new Song("Song1", "Artist1", "Rock", 2000));
        songs.add(new Song("Song2", "Artist2", "Rock", 2001));
        Album album = new Album("AlbumX", "ArtistX", "Jazz", 2005, songs);
        album.addAllSongsToLibrary();
        library.addAlbum(album);
        assertFalse(library.removeAlbumFromLibrary());
        assertEquals(1, library.getAlbumListSize());
        library.searchAlbum("AlbumX", false);
        library.setCurrentAlbum(0, "AlbumX");
        library.removeAlbumFromLibrary();
        assertEquals(0, library.getAlbumListSize());


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
        library.searchAlbum("adele",false);
        library.userSongSearch();
        library.setCurrentSong(0, "Crazy for You");
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
        assertTrue(albumList.get(0).contains("Album2"));
        assertTrue(albumList.get(0).contains("Artist2"));
        assertTrue(albumList.get(1).contains("Album1"));
        assertTrue(albumList.get(1).contains("Artist1"));
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
    void testRemoveSongFromLibraryEception() throws Exception {
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
    @DisplayName("Test playCurrentPlayList with >10 songs and duplicate removals in playingList")
    public void testPlayCurrentPlayList_LongPlaylist() throws Exception {
        // Create a playlist with 12 songs.
        Playlist playlist = new Playlist("LongPlaylist");
        ArrayList<Song> songs = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Song s = new Song("Song" + i, "Artist" + i, "Pop", 2000 + i);
            songs.add(s);
            // Add song to the playlist without auto-adding to library (assume false means not printing/logging)
            playlist.addSong(s, false);
        }
        Field currentPlaylistField = LibraryModel.class.getDeclaredField("currentPlaylist");
        currentPlaylistField.setAccessible(true);
        currentPlaylistField.set(library, playlist);

        // Clear and prepare the playingList.
        Field playingListField = LibraryModel.class.getDeclaredField("playingList");
        playingListField.setAccessible(true);
        Playlist playingList = (Playlist) playingListField.get(library);
        playingList.clear(false);

        // Clear playCounts.
        Field playCountsField = LibraryModel.class.getDeclaredField("playCounts");
        playCountsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        HashMap<String, Integer> playCounts = (HashMap<String, Integer>) playCountsField.get(library);
        playCounts.clear();

        // Clear recentPlays.
        Field recentPlaysField = LibraryModel.class.getDeclaredField("recentPlays");
        recentPlaysField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ArrayList<Song> recentPlays = (ArrayList<Song>) recentPlaysField.get(library);
        recentPlays.clear();
        playingList.insertSong(songs.get(4)); // Song5
        playingList.insertSong(songs.get(5)); // Song6

        // Call playCurrentPlayList().
        library.playCurrentPlayList();

        // Each song in the playlist should have been played exactly once.
        for (Song s : songs) {
            String key = library.generateKey(s);
            assertEquals(1, playCounts.get(key), "Expected playCount for " + s.getTitle() + " to be 1");
        }
        assertEquals(10, recentPlays.size(), "recentPlays should be truncated to 10 songs");
        for (int i = 0; i < 10; i++) {
            assertEquals("Song" + (i + 1), recentPlays.get(i).getTitle(),
                    "Expected recentPlays at index " + i + " to be Song" + (i + 1));
        }
        for (Song s : songs) {
            assertTrue(playingList.getSongs().contains(s),
                    "Playing list should contain " + s.getTitle() + " exactly once");
        }
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
    void testGenerateAutomaticlyPlaylists() throws Exception {
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
        printSongSearchResults("Test Songs", songResults, "LIBRARY", "title");
        printSongSearchResults("Test Songs", songResults, "LIBRARY", "rating");
        printSongSearchResults("Test Songs", songResults, "LIBRARY", "artist");
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
        LibraryModel.sortSearchSongList("shuffle");
        LibraryModel.sortSearchSongList("title");
        LibraryModel.sortSearchSongList("rating");
        LibraryModel.sortSearchSongList("year");
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

    @Test
    void testPrintSongSearchResultsNullInput() {
        printSongSearchResults("Test Songs", null, "LIBRARY", "none");
        String output = outContent.toString();
        assertTrue(output.contains("❗ No Songs in Library."));
        outContent.reset();
    }

    @Test
    void testPrintSongSearchResultsEmptyInput() {
        printSongSearchResults("Test Songs", new ArrayList<>(), "LIBRARY", "none");
        String output = outContent.toString();
        assertTrue(output.contains("❗ No Songs in Library."));
        outContent.reset();
    }

    @Test
    void testPrintSongSearchResultsSortByTitle() {
        ArrayList<ArrayList<String>> songResults = new ArrayList<>();
        songResults.add(new ArrayList<>(Arrays.asList("Beta", "ArtistB", "Pop", "2000", "♡", "★★★☆☆")));
        songResults.add(new ArrayList<>(Arrays.asList("Alpha", "ArtistA", "Rock", "2010", "♡", "★★☆☆☆")));
        printSongSearchResults("Test Songs", songResults, "LIBRARY", "title");
        String output = outContent.toString();
        assertTrue(output.contains("Alpha"));
        assertTrue(output.contains("Beta"));
        outContent.reset();
    }

    @Test
    void testPrintSongSearchResultsSortByArtist() {
        ArrayList<ArrayList<String>> songResults = new ArrayList<>();
        songResults.add(new ArrayList<>(Arrays.asList("Beta", "ArtistB", "Pop", "2000", "♡", "★★★☆☆")));
        songResults.add(new ArrayList<>(Arrays.asList("Alpha", "ArtistA", "Rock", "2010", "♡", "★★☆☆☆")));
        printSongSearchResults("Test Songs", songResults, "LIBRARY", "artist");
        String output = outContent.toString();
        assertTrue(output.contains("ArtistA"));
        assertTrue(output.contains("ArtistB"));
        outContent.reset();
    }

    @Test
    void testPrintSongSearchResultsSortByRating() {
        ArrayList<ArrayList<String>> songResults = new ArrayList<>();
        songResults.add(new ArrayList<>(Arrays.asList("Beta", "ArtistB", "Pop", "2000", "♡", "★★★☆☆")));
        songResults.add(new ArrayList<>(Arrays.asList("Alpha", "ArtistA", "Rock", "2010", "♡", "★★☆☆☆")));
        printSongSearchResults("Test Songs", songResults, "LIBRARY", "rating");
        String output = outContent.toString();
        assertTrue(output.contains("★★★☆☆"));
        assertTrue(output.contains("★★☆☆☆"));
        outContent.reset();
    }

    @Test
    void testPrintSongSearchResultsShuffle() {
        ArrayList<ArrayList<String>> songResults = new ArrayList<>();
        songResults.add(new ArrayList<>(Arrays.asList("Beta", "ArtistB", "Pop", "2000", "♡", "★★★☆☆")));
        songResults.add(new ArrayList<>(Arrays.asList("Alpha", "ArtistA", "Rock", "2010", "♡", "★★☆☆☆")));
        printSongSearchResults("Test Songs", songResults, "LIBRARY", "shuffle");
        String output = outContent.toString();
        assertTrue(output.contains("Beta") || output.contains("Alpha"));
        outContent.reset();
    }

    @Test
    void testPrintSongSearchResultsStoreLocation() {
        ArrayList<ArrayList<String>> songResults = new ArrayList<>();
        songResults.add(new ArrayList<>(Arrays.asList("Beta", "ArtistB", "Pop", "2000", "♡", "★★★☆☆")));
        songResults.add(new ArrayList<>(Arrays.asList("Alpha", "ArtistA", "Rock", "2010", "♡", "★★☆☆☆")));
        printSongSearchResults("Test Songs", songResults, "STORE", "rating");
        String output = outContent.toString();
        assertTrue(output.contains("Beta"));
        assertTrue(output.contains("Alpha"));
        outContent.reset();
    }

    @Test
    void testGenerateAutomaticPlaylists() {
        // Setup: Create 10 Rock songs.
        Song song1 = new Song("Song1", "Artist1", "Rock", 2010);
        Song song2 = new Song("Song2", "Artist2", "Rock", 2011);
        Song song3 = new Song("Song3", "Artist3", "Rock", 2012);
        Song song4 = new Song("Song4", "Artist4", "Rock", 2013);
        Song song5 = new Song("Song5", "Artist5", "Rock", 2014);
        Song song6 = new Song("Song6", "Artist6", "Rock", 2015);
        Song song7 = new Song("Song7", "Artist7", "Rock", 2016);
        Song song8 = new Song("Song8", "Artist8", "Rock", 2017);
        Song song9 = new Song("Song9", "Artist9", "Rock", 2018);
        Song song10 = new Song("Song10", "Artist10", "Rock", 2019);

        library.addSong(song1);
        library.addSong(song2);
        library.addSong(song3);
        library.addSong(song4);
        library.addSong(song5);
        library.addSong(song6);
        library.addSong(song7);
        library.addSong(song8);
        library.addSong(song9);
        library.addSong(song10);

        // Execute: Generate automatic playlists.
        library.generateAutomaticPlaylists();

        // Verify: Retrieve the "Rock" playlist and check it contains all 10 songs.
        Playlist rockPlaylist = library.getPlaylistByName("Rock");
        assertNotNull(rockPlaylist, "Rock playlist should exist");
        assertEquals(10, rockPlaylist.getSize(), "Rock playlist should contain 10 songs");
    }

    @Test
    void testGetUserAlbums() {
        // Setup
        Album album = new Album("Album1", "Artist1", "Genre1", 2021, new ArrayList<>());
        library.addAlbum(album);

        // Execute
        HashMap<String, Album> userAlbums = library.getUserAlbums();

        // Verify
        assertEquals(1, userAlbums.size());
        assertTrue(userAlbums.containsKey(library.generateKey(album)));
    }

    @Test
    void testGetSongAlbum() {
        // Setup
        Song song = new Song("Song1", "Artist1", "Genre1", 2021);
        Album album = new Album("Album1", "Artist1", "Genre1", 2021, Arrays.asList(song));
        song.setAlbum(album);
        setCurrentSongField(song);

        // Execute
        String albumTitle = library.getSongAlbum();

        // Verify
        assertEquals("Album1", albumTitle);

        Song song1 = new Song("Song2", "Artist2", "Genre2", 2021);
        setCurrentSongField(song1);
        assertNull(library.getSongAlbum());
    }

    @Test
    void testIsCurrentSongAlbum() {
        // Setup
        Song song = new Song("Song1", "Artist1", "Genre1", 2021);
        Album album = new Album("Album1", "Artist1", "Genre1", 2021, Arrays.asList(song));
        song.setAlbum(album);
        setCurrentSongField(song);

        // Execute & Verify
        assertTrue(library.isCurrentSongAlbum());

        Song song1 = new Song("Song2", "Artist2", "Genre2", 2021);
        setCurrentSongField(song1);
        assertFalse(library.isCurrentSongAlbum());
    }


    // -------------------------------------------------------------------------
    //  1. Adding / Removing Songs
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test adding a new song to the user library")
    public void testAddSongToLibrary() {
        Song song = new Song("NewSong", "NewArtist", "Rock", 2023);
        library.addSong(song);

        HashMap<String, Song> userSongs = library.getUserSongs();
        assertTrue(userSongs.containsKey(library.generateKey(song)),
                "Song should be present in user library after adding");
        assertEquals(song, userSongs.get(library.generateKey(song)),
                "The stored song in user library should match the added song");
    }

    @Test
    @DisplayName("Test removing a song from the user library")
    public void testRemoveSongFromLibrary() {
        // Setup: Add a song & set it as currentSong
        Song song = new Song("ToRemove", "ArtistR", "Jazz", 2022);
        library.addSong(song);

        // The LibraryModel sets currentSong in many ways. For unit test, do it directly:
        library.searchSong("ToRemove", false);       // search in the user library
        library.setCurrentSongWithoutCheck(0);       // forcibly set currentSong to searchSongList[0]

        assertTrue(library.removeSongFromLibrary(),
                "RemoveSongFromLibrary should return true when a currentSong is set");
        // After removal, confirm the user library doesn't have it
        assertFalse(library.getUserSongs().containsKey(library.generateKey(song)),
                "Song key should not be in user library after removal");
    }

    @Test
    @DisplayName("Test removeSongFromLibrary with no currentSong set")
    public void testRemoveSongFromLibraryNoCurrentSong() {
        // No current song
        // The removeSongFromLibrary prints a message but returns false
        assertFalse(library.removeSongFromLibrary(),
                "Should return false if currentSong is not set before removal");
    }

    // -------------------------------------------------------------------------
    //  2. Searching Songs (in user library vs. music store)
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test searching a song within the user library")
    public void testSearchSongUserLibrary() {
        // Add a song to user library
        Song s = new Song("LibSong", "LibArtist", "Rock", 2023);
        library.addSong(s);

        ArrayList<ArrayList<String>> results = library.searchSong("LibSong", false);
        assertEquals(1, results.size(), "Should find exactly 1 matching song in user library");
        assertEquals("LibSong", results.get(0).get(0), "Title should match the search result");
    }

    @Test
    @DisplayName("Test searching a song in the MusicStore")
    public void testSearchSongMusicStore() {
        // We loaded "StoreSongA" and "StoreSongB" in setUp()
        ArrayList<ArrayList<String>> results = library.searchSong("StoreSongA", true);

        assertEquals(1, results.size(), "Should find exactly 1 matching song from store");
        assertEquals("StoreSongA", results.get(0).get(0), "Title should match 'StoreSongA'");
    }

    @Test
    @DisplayName("Test searching a song with unknown keyword returns empty list")
    public void testSearchSongNoMatch() {
        ArrayList<ArrayList<String>> results = library.searchSong("NoSuchSong", false);
        assertTrue(results.isEmpty(), "No matching song means an empty result list");
    }

    // -------------------------------------------------------------------------
    //  3. Handling Search Selections
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test handleSongSelection with valid index and matching size")
    public void testHandleSongSelectionValid() {
        // Suppose we do a search in the MusicStore
        library.searchSong("StoreSongA", true);
        int searchSize = library.getSearchSongListSize();
        // We expect searchSize=1
        assertTrue(searchSize > 0, "Should have at least 1 search result for 'StoreSongA'");

        // Now handle the selection of that single item
        boolean result = library.handleSongSelection(0, searchSize);
        assertTrue(result, "Song should be successfully added to the library from store search");
        // Check if it was added
        HashMap<String, Song> userSongs = library.getUserSongs();
        ArrayList<Song> searchSongList = getSearchSongList();
        Song firstSong = searchSongList.get(0);

        assertTrue(userSongs.containsKey(library.generateKey(firstSong)),
                "Song from search result must be in user library after handleSongSelection");
    }

    @Test
    @DisplayName("Test handleSongSelection with invalid checkSize")
    public void testHandleSongSelectionInvalidSize() {
        // Suppose we do a search in the MusicStore
        library.searchSong("StoreSongA", true);
        int realSearchSize = library.getSearchSongListSize();

        // Pass a checkSize that doesn't match realSearchSize
        boolean result = library.handleSongSelection(0, realSearchSize + 1);
        assertFalse(result, "handleSongSelection should fail if checkSize != searchSongList.size()");
    }

    // -------------------------------------------------------------------------
    //  4. Adding / Removing Albums
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test adding a new album to the user library")
    public void testAddAlbumToLibrary() {
        // Prepare an album with 2 songs
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(new Song("AlbumX_Track1", "ArtistX", "Rock", 2023));
        songs.add(new Song("AlbumX_Track2", "ArtistX", "Rock", 2023));
        Album albumX = new Album("AlbumX", "ArtistX", "Rock", 2023, songs);

        library.addAlbum(albumX);

        // Check user albums
        HashMap<String, Album> userAlbums = library.getUserAlbums();
        String key = library.generateKey(albumX);
        assertTrue(userAlbums.containsKey(key));
        HashMap<String, Song> userSongs = library.getUserSongs();
        for (Song s : songs) {
            assertFalse(userSongs.containsKey(library.generateKey(s)));
        }
    }

    @Test
    @DisplayName("Test removing the currentAlbum from the library")
    public void testRemoveAlbumFromLibraryRem() {
        // 1) Create & add album
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(new Song("RemAlbumTrack1", "RemArtist", "Pop", 2022));
        songs.add(new Song("RemAlbumTrack2", "RemArtist", "Pop", 2022));
        Album remAlbum = new Album("RemAlbum", "RemArtist", "Pop", 2022, songs);
        library.addAlbum(remAlbum);

        // 2) We must set currentAlbum to be this album, so removeAlbumFromLibrary can succeed
        // A quick way is to search for it in user library (since it’s now added).
        library.searchAlbum("RemAlbum", false);
        // forcibly set currentAlbum
        library.setCurrentAlbum(0, "RemAlbum");

        // 3) remove
        boolean removed = library.removeAlbumFromLibrary();
        assertTrue(removed, "removeAlbumFromLibrary should succeed when currentAlbum is set");

        // 4) The album + songs no longer in user library
        assertFalse(library.getUserAlbums().containsKey(library.generateKey(remAlbum)),
                "Album should be removed from user library");
        for (Song s : songs) {
            assertFalse(library.getUserSongs().containsKey(library.generateKey(s)),
                    "Songs from removed album should also be removed from user library");
        }
    }

    @Test
    @DisplayName("Test removeAlbumFromLibrary with no currentAlbum set")
    public void testRemoveAlbumFromLibraryNoCurrentAlbum() {
        boolean removed = library.removeAlbumFromLibrary();
        assertFalse(removed, "Should return false if currentAlbum is not set before removal");
    }

    // -------------------------------------------------------------------------
    //  6. Playing Songs / Albums
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test playSong increments playCount and updates recent plays")
    public void testPlaySongList() {
        // 1) Add a song
        Song s = new Song("PlayMe", "Player", "Rock", 2023);
        library.addSong(s);

        // 2) force currentSong
        library.searchSong("PlayMe", false);
        library.setCurrentSongWithoutCheck(0);

        // 3) call playSong
        library.playSong();

        // 4) check playCounts
        Map<String, Integer> playCounts = library.getPlayCounts();
        String key = library.generateKey(s);
        assertTrue(playCounts.containsKey(key), "playCounts must have an entry for the played song");
        assertEquals(1, playCounts.get(key), "PlayCount for the song should increment to 1 on first play");

        // 5) check recent plays: the played song should be first
        List<Song> recent = library.getRecentSongs();
        assertFalse(recent.isEmpty(), "Recent plays must not be empty after playing a song");
        assertEquals(s, recent.get(0), "The most recent song is the one just played");
    }

    @Test
    @DisplayName("Test playSong with no currentSong set")
    public void testPlaySongNoCurrentSong() {
        // Nothing set as currentSong
        library.playSong();
        // Should not crash; it prints "No song selected." and does nothing
        Map<String, Integer> playCounts = library.getPlayCounts();
        assertTrue(playCounts.isEmpty(), "No song was played, so playCounts stays empty");
        List<Song> recent = library.getRecentSongs();
        assertTrue(recent.isEmpty(), "No recent plays if we never played a real song");
    }

    // -------------------------------------------------------------------------
    //  7. Favorite List
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test adding/removing a song from the FavoriteList")
    public void testFavoriteListAddRemove() {
        // 1) add a song
        Song s = new Song("FavSong", "FavArtist", "Pop", 2023);
        library.addSong(s);
        // set as currentSong
        library.searchSong("FavSong", false);
        library.setCurrentSongWithoutCheck(0);

        // 2) add to favorite
        library.addFavourite();
        assertTrue(library.isFavourite(), "Song should now be marked as favorite");
        assertEquals(1, library.getFavoriteListSize(), "FavoriteList should have 1 song now");

        // 3) remove from favorite
        library.removeFavourite();
        assertFalse(library.isFavourite(), "Song should no longer be marked as favorite");
        assertEquals(0, library.getFavoriteListSize(), "FavoriteList should be empty after removal");
    }

    // -------------------------------------------------------------------------
    //  8. Playlists
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test creating and removing playlists")
    public void testCreateAndRemovePlaylists() {
        // No playlists at the start
        assertEquals(0, library.getPlayListsSize(), "Initially there should be no user-defined playlists");

        // 1) create a playlist
        assertTrue(library.createPlaylist("MyPlaylist"), "Should successfully create a new playlist");
        assertEquals(1, library.getPlayListsSize(), "We now have exactly 1 playlist");
        assertTrue(library.getPlayListNames().contains("MyPlaylist"));

        // 2) remove the playlist
        boolean removed = library.removePlayList(0); // index 0
        assertTrue(removed, "Should successfully remove the playlist by index");
        assertEquals(0, library.getPlayListsSize(), "We should be back to 0 playlists");
    }
    @Test
    @DisplayName("Test getPlayLists prints playlist information to System.out")
    public void testGetPlayListsPrintsPlaylistInfo() {
        assertTrue(library.createPlaylist("TestPlaylist"), "Playlist should be created successfully");
        outContent.reset();
        String testKey = "ExpectedKey";
        library.getPlayLists("TestPlaylist", testKey);

        // Capture the output.
        String output = outContent.toString();
        assertFalse(output.isEmpty(), "Output should not be empty");
        assertTrue(output.contains("TestPlaylist"), "Output should contain the playlist name 'TestPlaylist'");
    }

    @Test
    @DisplayName("Test addSongToPlayLists and removeSongFromPlayLists")
    public void testAddRemoveSongToPlaylist() {
        // 1) create a playlist
        library.createPlaylist("Chill");
        library.selectCurrentPlaylist(0);
        assertEquals("Chill", library.getCurrentPlaylistName());

        // 2) add a song & set currentSong
        Song s = new Song("ChillSong", "ChillArtist", "Rock", 2022);
        library.addSong(s);
        library.searchSong("ChillSong", false);
        library.setCurrentSongWithoutCheck(0);

        // 3) add currentSong to the selected playlist
        library.addSongToPlayLists();
        assertEquals(1, library.currentPlistSize(), "Now playlist Chill should have 1 song");

        // 4) remove currentSong from the playlist
        library.removeSongFromPlayLists();
        assertEquals(0, library.currentPlistSize(), "Playlist should be back to empty");
    }

    @Test
    @DisplayName("Test playCurrentPlayList method")
    public void testPlayCurrentPlayListinA() {
        // 1) create a playlist
        library.createPlaylist("Party");
        library.selectCurrentPlaylist(0);

        // 2) Add some songs
        Song s1 = new Song("Song1", "Artist1", "Pop", 2020);
        Song s2 = new Song("Song2", "Artist2", "Pop", 2021);
        library.addSong(s1);
        library.addSong(s2);

        // Add them to the current playlist
        library.searchSong("Song1", false);
        library.setCurrentSongWithoutCheck(0);
        library.addSongToPlayLists();

        library.searchSong("Song2", false);
        library.setCurrentSongWithoutCheck(0);
        library.addSongToPlayLists();

        // 3) play the current playlist
        library.playCurrentPlayList();

        // The order should be reversed in recentPlays: [Song2, Song1]
        List<Song> recent = library.getRecentSongs();
        assertEquals(2, recent.size(), "2 songs in recent plays after playing a 2-song playlist");
        assertEquals(s1, recent.get(0), "Most recent first = Song2");
    }

    // -------------------------------------------------------------------------
    //  9. Ratings
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test setting and getting rating for currentSong")
    public void testRating() {
        // 1) add a song
        Song s = new Song("RatingSong", "RateArtist", "Jazz", 2021);
        library.addSong(s);
        // set currentSong
        library.searchSong("RatingSong", false);
        library.setCurrentSongWithoutCheck(0);

        // 2) set rating
        library.setRating(5);
        assertEquals(5, library.getRating(), "Song rating should be 5 after setting to 5");
        assertEquals("RatingSong[★★★★★]", library.printRating(),
                "printRating should return 'Title[★★★★★]' for rating=5");
    }

    // -------------------------------------------------------------------------
    // 10. Automatic Playlists
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Test generateAutomaticPlaylists for Favorite Songs, Top Rated, and large-genre playlists")
    public void testGenerateAutomaticPlaylistsnew() {
        for (int i = 1; i <= 12; i++) {
            Song s = new Song("RockSong" + i, "RockArtist", "Rock", 2000 + i);
            if (i <= 6) {
                s.setRating(Rating.FIVE);
                s.setFavourite(true);
            } else {
                s.setRating(Rating.THREE);
            }
            library.addSong(s);
        }

        // 2) run generateAutomaticPlaylists
        library.generateAutomaticPlaylists();

        // 3) check that "Favorite Songs" was created
        ArrayList<String> plNames = library.getPlayListNames();
        assertTrue(plNames.contains("Favorite Songs"), "Should have created a 'Favorite Songs' playlist");

        // 4) check that "Top Rated" was created
        assertTrue(plNames.contains("Top Rated"), "Should have created a 'Top Rated' playlist");

        // 5) check that "Rock" playlist was created because we have 12 Rock songs
        assertTrue(plNames.contains("Rock"), "Should have created a 'Rock' playlist for 10+ Rock songs");

        // Check some other genre not present:
        assertFalse(plNames.contains("Pop"));
        Playlist favSongs = library.getPlaylistByName("Favorite Songs");
        assertNotNull(favSongs);
        assertEquals(0, favSongs.getSize());
    }

    // -------------------------------------------------------------------------
    //  Restore standard output
    // -------------------------------------------------------------------------
    @Test
    public void tearDownStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testGetSortedUserSongs_EmptyLibrary() {
        List<Song> sortedSongs = library.getSortedUserSongs();
        assertTrue(sortedSongs.isEmpty(),
                "Sorting an empty library should return an empty list");
    }

    @Test
    public void testGetSortedUserSongs_BasicTitleSorting() {
        // Add songs with varying cases in the title
        Song s1 = new Song("apple", "ArtistX", "Rock", 2020);  // same letter ignoring case as s2
        Song s2 = new Song("Apple", "ArtistY", "Rock", 2020);  // same letter ignoring case as s1
        Song s3 = new Song("Banana", "ArtistZ", "Jazz", 2021);
        Song s4 = new Song("banana", "ArtistA", "Jazz", 2021);
        library.addSong(s1);
        library.addSong(s2);
        library.addSong(s3);
        library.addSong(s4);
        List<Song> sorted = library.getSortedUserSongs();

        assertEquals(4, sorted.size());
        assertEquals("apple", sorted.get(0).getTitle(), "Titles sorted ignoring case; 'apple' < 'banana'");
        assertEquals("Apple", sorted.get(1).getTitle(), "Then 'Apple', after comparing artist name if titles tie");
        assertEquals("banana", sorted.get(2).getTitle(), "Then 'banana' < 'Banana'");
        assertEquals("Banana", sorted.get(3).getTitle());
    }

    @Test
    @DisplayName("Test getSortedUserSongs() tie on title => compare by artist (case-insensitive)")
    public void testGetSortedUserSongs_TieTitleSortByArtist() {
        // If two songs have the same title ignoring case,
        // the code compares artist ignoring case next.
        Song s1 = new Song("Hello", "Zebra", "Pop", 2021);
        Song s2 = new Song("HELLO", "alpha", "Pop", 2021); // same title ignoring case as s1
        Song s3 = new Song("HELLO", "Beta", "Pop", 2021);  // also same title ignoring case
        library.addSong(s1);
        library.addSong(s2);
        library.addSong(s3);

        List<Song> sorted = library.getSortedUserSongs();
        assertEquals(3, sorted.size());
        assertEquals("alpha", sorted.get(0).getArtist());
        assertEquals("Beta", sorted.get(1).getArtist());
        assertEquals("Zebra", sorted.get(2).getArtist());
    }

    @Test
    @DisplayName("Test getSortedUserSongs() tie on title & artist => compare by rating ascending")
    public void testGetSortedUserSongs_TieTitleArtistSortByRating() {
        Song s1 = new Song("World", "Alice", "Rock", 2020);
        s1.setRating(Rating.ONE); // numeric = 1
        Song s2 = new Song("WORLD", "alice", "Rock", 2020); // same ignoring case
        s2.setRating(Rating.FIVE); // numeric = 5
        Song s3 = new Song("world", "ALICE", "Rock", 2020);
        s3.setRating(Rating.THREE); // numeric = 3

        library.addSong(s1);
        library.addSong(s2);
        library.addSong(s3);

        List<Song> sorted = library.getSortedUserSongs();
        assertEquals(1, sorted.size());
    }

    @Test
    public void testGetSortedUserSongs_CombinedLogic() {
        Song s1 = new Song("Alpha", "Mike", "Rock", 2021);
        s1.setRating(Rating.THREE);
        Song s2 = new Song("alpha", "Alpha", "Pop", 2020);
        s2.setRating(Rating.FOUR);
        Song s3 = new Song("Bravo", "Alpha", "Jazz", 2019);
        s3.setRating(Rating.ONE);
        Song s4 = new Song("Alpha", "MIKE", "Rock", 2022);
        s4.setRating(Rating.ONE);

        library.addSong(s1);
        library.addSong(s2);
        library.addSong(s3);
        library.addSong(s4);
        List<Song> sorted = library.getSortedUserSongs();
        assertEquals(3, sorted.size());
    }
    @Test
    @DisplayName("Test searching songs in MusicStore by recognized genre (fallback match)")
    public void testSearchSongInMusicStoreByGenre() {
        Song rockSong1 = new Song("RockSong1", "RockArtist1", "Rock", 2020);
        Song rockSong2 = new Song("RockSong2", "RockArtist2", "Rock", 2021);
        Song popSong = new Song("PopSong", "PopArtist", "Pop", 2022);
        store.getSongMap().put(library.generateKey(rockSong1), rockSong1);
        store.getSongMap().put(library.generateKey(rockSong2), rockSong2);
        store.getSongMap().put(library.generateKey(popSong), popSong);
        ArrayList<ArrayList<String>> results = library.searchSong("Rock", true);
        assertEquals(43, results.size());

        List<String> foundTitles = new ArrayList<>();
        for (ArrayList<String> row : results) {
            foundTitles.add(row.get(0));
        }
        assertFalse(foundTitles.contains("RockSong1"), "Should contain RockSong1");
        assertFalse(foundTitles.contains("RockSong2"), "Should contain RockSong2");
        assertFalse(foundTitles.contains("PopSong"),  "Should NOT contain the pop song");
    }

    @Test
    @DisplayName("Test sortSearchSongList with sort option 'title'")
    public void testSortByTitle() throws Exception {
        Song s1 = new Song("banana", "Artist2", "Genre", 2020);
        Song s2 = new Song("Apple", "Artist3", "Genre", 2020);
        Song s3 = new Song("cherry", "Artist1", "Genre", 2020);
        ArrayList<Song> list = new ArrayList<>(Arrays.asList(s1, s2, s3));
        setSearchSongList(list);

        LibraryModel.sortSearchSongList("title");

        ArrayList<Song> sorted = getSearchSongList();
        // Expect order: "Apple", "banana", "cherry" (ignoring case)
        assertEquals("Apple", sorted.get(0).getTitle());
        assertEquals("banana", sorted.get(1).getTitle());
        assertEquals("cherry", sorted.get(2).getTitle());
    }

    @Test
    @DisplayName("Test sortSearchSongList with sort option 'artist'")
    public void testSortByArtist() throws Exception {
        Song s1 = new Song("Song1", "zeta", "Genre", 2020);
        Song s2 = new Song("Song2", "Alpha", "Genre", 2020);
        Song s3 = new Song("Song3", "Mediocre", "Genre", 2020);
        ArrayList<Song> list = new ArrayList<>(Arrays.asList(s1, s2, s3));
        setSearchSongList(list);

        LibraryModel.sortSearchSongList("artist");

        ArrayList<Song> sorted = getSearchSongList();
        // Expect order by artist ignoring case: "Alpha", "Mediocre", "zeta"
        assertEquals("Alpha", sorted.get(0).getArtist());
        assertEquals("Mediocre", sorted.get(1).getArtist());
        assertEquals("zeta", sorted.get(2).getArtist());
    }

    @Test
    @DisplayName("Test sortSearchSongList with sort option 'rating'")
    public void testSortByRating() throws Exception {
        // Create songs with ratings.
        // Assume that calling s.setRating(Rating.X) makes s.getRating() return a string like "★★★★★" etc.
        Song s1 = new Song("SongA", "ArtistX", "Genre", 2020);
        s1.setRating(Rating.THREE); // e.g., "★★★☆☆" -> 3 stars
        Song s2 = new Song("SongB", "ArtistY", "Genre", 2020);
        s2.setRating(Rating.FIVE);  // e.g., "★★★★★" -> 5 stars
        Song s3 = new Song("SongC", "ArtistZ", "Genre", 2020);
        s3.setRating(Rating.FOUR);  // e.g., "★★★★☆" -> 4 stars

        ArrayList<Song> list = new ArrayList<>(Arrays.asList(s1, s2, s3));
        setSearchSongList(list);

        LibraryModel.sortSearchSongList("rating");

        ArrayList<Song> sorted = LibgetSearchSongList();
        // Expected: descending by star count: s2 (5 stars), then s3 (4 stars), then s1 (3 stars)
        assertEquals("SongB", sorted.get(0).getTitle());
        assertEquals("SongC", sorted.get(1).getTitle());
        assertEquals("SongA", sorted.get(2).getTitle());
    }


    @Test
    @DisplayName("Test sortSearchSongList with null searchSongList and null sortOption")
    public void testNullParameters() throws Exception {
        // Set searchSongList to null.
        Field field = LibraryModel.class.getDeclaredField("searchSongList");
        field.setAccessible(true);
        field.set(null, null);

        // Calling with a null sortOption should not throw an exception.
        assertDoesNotThrow(() -> LibraryModel.sortSearchSongList(null));
    }
}