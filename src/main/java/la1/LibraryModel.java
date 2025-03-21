package la1;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryModel {
    private final String UserID;
    private String encryptedPassword;
    // Function is a class that contains all the functions that can be used in the library
    // UserSongs stores all songs in the user's library, each item is a class Song
    private final HashMap<String, Song> UserSongs;
    // UserAlbums stores all albums in the user's library, each item is a class Album
    private final HashMap<String, Album> UserAlbums;
    private final PlayLists PlayLists;
    private Playlist playingList;
    private final transient Scanner scanner;
    private static ArrayList<Song> searchSongList;
    private ArrayList<Album> searchAlbumList;
    private Playlist currentPlaylist;
    private Song currentSong;
    private Album currentAlbum;
    private Map<String, Integer> playCounts = new HashMap<>();
    private List<Song> recentPlays = new ArrayList<>();

    private transient MusicStore musicStore;
    private FavoriteList favoriteList;

    public LibraryModel(String UserID, MusicStore musicStore) {
        this.UserID = UserID;
        this.encryptedPassword = "";
        this.favoriteList = new FavoriteList();
        this.UserSongs = new HashMap<>();
        this.UserAlbums = new HashMap<>();
        this.playingList = new PlayingList("Playing List");
        this.PlayLists = new PlayLists();
        this.scanner = new Scanner(System.in);
        this.searchSongList = new ArrayList<>();
        this.searchAlbumList = new ArrayList<>();
        this.musicStore = musicStore;

    }

    public String getUserID() {
        return UserID;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public MusicStore getMusicStore() {
        return musicStore;
    }

    public void setMusicStore(MusicStore musicStore) {
        this.musicStore = musicStore;
    }

    public String generateKey(Song song) {
        return (song.getTitle() + "|" + song.getArtist() + "|" + song.getGenre()).toLowerCase();
    }

    public String generateKey(Album album) {
        return (album.getTitle() + "|" + album.getArtist() + "|" + album.getGenre()).toLowerCase();
    }


    public static ArrayList<Song> getSearchSongList() {
        return new ArrayList<>(searchSongList);
    }

    public HashMap<String, Song> getUserSongs() {
        return new HashMap<>(UserSongs);
    }

    public HashMap<String, Album> getUserAlbums() {
        return new HashMap<>(UserAlbums);
    }

    public String getSongAlbum() {
        currentAlbum = currentSong.getAlbum();
        if (currentAlbum == null) {
            return null;
        }
        return currentAlbum.getTitle();
    }

    public boolean isCurrentSongAlbum() {
        return currentSong.getAlbum() != null;
    }

    // -------------------------------------------------------------------------
    //                  ALL CURRENT VARIABLE
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    //                  CURRENT SONG GETTER
    // -------------------------------------------------------------------------

    public boolean checkCurrentSong(String title) {
        return currentSong.getTitle().equalsIgnoreCase(title);
    }


    public boolean setCurrentSong(int index, String songTitle) {
        Song song = searchSongList.get(index);
        if (!song.getTitle().equalsIgnoreCase(songTitle)) {
            return false;
        }
        currentSong = song;
        return true;
    }

    public String getCurrentSongInfo() {
        return currentSong.toString();
    }

    public String getCurrentSongTitle() {
        return currentSong.getTitle();
    }

    public void setCurrentSongWithoutCheck(int index) {
        currentSong = searchSongList.get(index);
    }


    // -------------------------------------------------------------------------
    //                  CURRENT ALBUM GETTER
    // -------------------------------------------------------------------------

    public boolean checkCurrentAlbum(String title) {
        return currentAlbum.getTitle().equalsIgnoreCase(title);
    }

    public boolean setCurrentAlbum(int index, String albumTitle) {
        Album album = searchAlbumList.get(index);
        if (!album.getTitle().equalsIgnoreCase(albumTitle)) {
            return false;
        }
        currentAlbum = album;
        return true;
    }

    public String getCurrentAlbumInfo() {
        return currentAlbum.getAlbumInfoString();
    }

    public Boolean isCurrentAlbumComplete() {
        return currentAlbum.isComplete();
    }


    public ArrayList<Album> getSearchAlbumList() {
        ArrayList<Album> copy = new ArrayList<>(searchAlbumList.size());
        for (Album album : searchAlbumList) {
            copy.add(new Album(album)); // Assuming Album has a copy constructor
        }
        return copy;
    }
    // -------------------------------------------------------------------------
    //                  CURRENT SONG LIST
    // -------------------------------------------------------------------------

    public void userSongSearch() {
        searchSongList = new ArrayList<>(UserSongs.values());
    }

    public void playListSearch() {
        searchSongList = new ArrayList<>(playingList.getSongs());
    }

    public void favoriteListSearch() {
        searchSongList = new ArrayList<>(favoriteList.getSongs());
    }

    public void playListsCurrent() { searchSongList = new ArrayList<>(currentPlaylist.getSongs());}

    public int getSearchSongListSize() {
        return searchSongList.size();
    }




    // -------------------------------------------------------------------------
    //                  ALL FUNCTIONS
    // -------------------------------------------------------------------------

    /**
     * Print all artists in the user's library
     */
    public void printAllArtists() {
        if (UserSongs.isEmpty()) {
            System.out.println("The library is empty [WARNING]");
            return;
        }
        Set<String> artists = new HashSet<>();
        for (Song song : UserSongs.values()) {
            artists.add(song.getArtist());
        }
        System.out.println("All artists in the library:");
        for (String artist : artists) {
            System.out.println(artist);
        }
    }

    // -------------------------------------------------------------------------
    //                  SONG FUNCTIONS
    // -------------------------------------------------------------------------

    /**
     * Add a song to the user's library
     * Title|artist as key, class Song as value
     */
    public void addSong(Song song) {
        UserSongs.put(generateKey(song), song);
    }

    public ArrayList<ArrayList<String>> searchSong (String keyword, boolean isMusicStore) {
        searchSongList = new ArrayList<>();
        ArrayList<Song> songs;
        if (isMusicStore) {
            songs = searchSongSub(keyword, true);
        } else {
            songs = searchSongSub(keyword, false);
        }
        searchSongList = songs;

        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for (Song song : songs) {
            result.add(song.toStringList());
        }
        return result;
    }


    /**

     * Search for a song in the user's library or the music store
     * @param keyword the search keyword
     * @param isMusicStore true if searching in the music store, false if searching in the user's library
     * @return a list of songs that match the search keyword
     */
    public ArrayList<Song> searchSongSub(String keyword, boolean isMusicStore) {
        Map<String, Song> songMap;
        if (isMusicStore) {
            songMap = musicStore.getSongMap();
        } else {
            songMap = UserSongs;
        }
        ArrayList<Song> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase().trim();
        Genre searchedGenre = null;
        if (isMusicStore) {
            searchedGenre = Genre.fromString(keyword);
        }

        for (Map.Entry<String, Song> entry : songMap.entrySet()) {
            Song song = entry.getValue();
            boolean isMatch = false;
            String key = entry.getKey();
            String[] parts = key.split("\\|");

            for (String part : parts) {
                if (part.toLowerCase().trim().equals(lowerKeyword)) {
                    isMatch = true;
                    break;
                }
            }
            if (!isMatch && isMusicStore && searchedGenre != null
                    && searchedGenre != Genre.UNKNOWN && searchedGenre != Genre.OTHER) {
                if (song.getGenre().equalsIgnoreCase(searchedGenre.getGenre())) {
                    isMatch = true;
                }
            }
            if (isMatch) {
                result.add(song);
            }
        }
        return result;
    }

//    copied from Playlist
public List<Song> getSortedUserSongs() {
    List<Song> sortedSongs = new ArrayList<>(UserSongs.values());
    Collections.sort(sortedSongs, (s1, s2) -> {
        int cmp = s1.getTitle().compareToIgnoreCase(s2.getTitle());
        if (cmp != 0) {
            return cmp;
        }
        return s1.getArtist().compareToIgnoreCase(s2.getArtist());
    });

    return sortedSongs;
}

    /**
     * Add all songs in the search result to the user's library
     * @param checkSize the size of the search result
     * @return true if all songs are added successfully,
     *      false if the size of the search result is not equal to checkSize
     */
    public boolean allSongSelection(int checkSize) {
        if (checkSize != searchSongList.size()) {
            return false;
        }
        for (Song song : searchSongList) {
            addSong(song);
            if (song.getAlbum() != null) {
                addAlbum(song.getAlbum());
                song.getAlbum().addSongToAlbumLibrary(song);
            }
        }
        return true;
    }

    /**
     * handle the song selection based on the index and the size of the search result
     * @param index the index of the song in the search result
     * @param checkSize the size of the search result
     */
    public boolean handleSongSelection(int index, int checkSize) {
        // make sure the searchSongList print String is the same as the searchSongList Song Object
        if (checkSize != searchSongList.size()) {
            return false;
        }
        Song song = searchSongList.get(index);
        addSong(song);
        if (song.getAlbum() != null) {
            addAlbum(song.getAlbum());
            song.getAlbum().addSongToAlbumLibrary(song);
        }
        return true;
    }

    /**
     * Get the size of the user's song library
     * @return the size of the user's song library
     */
    public int getSongListSize() {
        return UserSongs.size();
    }

    /**
     * Print the user's song library in a table format
     * @return a list of songs in the user's library in String format
     */
    public ArrayList<ArrayList<String>> SongToString() {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        if (currentAlbum != null) {
            result.add(currentAlbum.getAlbumInfo());
        }
        // similar sorting method as Playlist.sort()
        ArrayList<Song> sortedSongs = new ArrayList<>(searchSongList);
        Collections.sort(sortedSongs, (s1, s2) -> {
            int cmp = s1.getTitle().compareToIgnoreCase(s2.getTitle());
            if (cmp != 0) {
                return cmp;
            }
            cmp = s1.getArtist().compareToIgnoreCase(s2.getArtist());
            if (cmp != 0) {
                return cmp;
            }
            return Integer.compare(s1.getRatingInt(), s2.getRatingInt());
        });
        for (Song song : sortedSongs) {
            result.add(song.toStringList());
        }
        return result;
    }


    /**
     * Print the user's song library in a table format with custom sorting.
     * Sorting options:
     *   "title"  - sort by song title (alphabetically)
     *   "artist" - sort by artist (alphabetically)
     *   "rating" - sort by rating (descending order)
     */
    public void printUserSongsTable(String sortMethod) {
        // make a deep copy first!
        List<Song> songs = new ArrayList<>(UserSongs.values());
        switch (sortMethod.toLowerCase()) {
            case "title":
                Collections.sort(songs, (s1, s2) -> s1.getTitle().compareToIgnoreCase(s2.getTitle()));
                break;
            case "artist":
                Collections.sort(songs, (s1, s2) -> s1.getArtist().compareToIgnoreCase(s2.getArtist()));
                break;
            case "rating":
                Collections.sort(songs, (s1, s2) -> Integer.compare(s2.getRatingInt(), s1.getRatingInt()));
                break;
            case "shuffle":
                Collections.shuffle(songs);
                break;
            default:
                Collections.sort(songs, (s1, s2) -> s1.getTitle().compareToIgnoreCase(s2.getTitle()));
                break;
        }

        // keep same sequence
        searchSongList = new ArrayList<>(songs);
        List<List<String>> tableData = new ArrayList<>();
        List<String> header = Arrays.asList("No.", "Title", "Artist", "Genre", "Year", "Favorite", "Rating", "Album");
        tableData.add(header);
        tableData.add(Arrays.asList("###SEPARATOR###"));

        int index = 1;
        for (Song song : songs) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(index++));
            row.add(song.getTitle());
            row.add(song.getArtist());
            row.add(song.getGenre());
            row.add(String.valueOf(song.getYear()));
            row.add(song.getFavourite());
            row.add(song.getRating());
            row.add(song.getAlbumTitle() != null ? song.getAlbumTitle() : "");
            tableData.add(row);
        }
        TablePrinter.printDynamicTable("User Songs Library", tableData);
    }

    public ArrayList<ArrayList<String>> getSongList() {
        List<Song> sortedSongs = getSortedUserSongs();
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for (Song song : sortedSongs) {
            result.add(song.toStringList());
        }
        return result;
    }

    public boolean removeSongFromLibrary() {
        if (currentSong == null) {
            System.out.println("No song selected.");
            return false;
        }
        UserSongs.remove(generateKey(currentSong));
        if (currentSong.getAlbum() != null) {
            currentSong.getAlbum().removeSongFromAlbumLibrary(currentSong);
        }
        currentSong = null;
        return true;
    }


    // -------------------------------------------------------------------------
    //                  ALBUM FUNCTIONS
    // -------------------------------------------------------------------------

    /**
     * Add an album to the user's library
     * Title|artist as key, class Album as value
     * also add all songs in the album to the library
     */
    public void addAlbum(Album album) {
        UserAlbums.put(generateKey(album), album);
        for (Song song : album.getSongs()) {
            UserSongs.put(generateKey(song), song);
        }
    }

    /**
     * Search for an album in the user's library or the music store
     * @param keyword the search keyword
     * @param isMusicStore true if searching in the music store, false if searching in the user's library
     * @return a list of albums that match the search keyword
     */

    public ArrayList<ArrayList<String>> searchAlbum (String keyword, boolean isMusicStore) {
        searchAlbumList = new ArrayList<>();
        ArrayList<Album> albums;
        if (isMusicStore) {
            albums = searchAlbumSub(keyword, true);
        } else {
            albums = searchAlbumSub(keyword, false);
        }
        searchAlbumList = albums;
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for (Album album : albums) {
            result.add(album.toStringList(isMusicStore));
        }
        return result;
    }

    /**
     * Search for an album in the user's library or the music store
     * @param keyword the search keyword
     * @param isMusicStore true if searching in the music store, false if searching in the user's library
     * @return a list of albums that match the search keyword
     */
    public ArrayList<Album> searchAlbumSub(String keyword, boolean isMusicStore) {
        Map<String, Album> albumMap;
        if (isMusicStore) {
            albumMap = musicStore.getAlbumMap();
        } else {
            albumMap = UserAlbums;
        }

        ArrayList<Album> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase().trim();

        for (Map.Entry<String, Album> entry : albumMap.entrySet()) {
            String key = entry.getKey();
            String[] parts = key.split("\\|");

            for (String part : parts) {
                if (part.toLowerCase().trim().equals(lowerKeyword)) {
                    result.add(entry.getValue());
                    break;
                }
            }
        }

        return result;
    }




    /**
     * Add all albums in the search result to the user's library
     * @param checkSize the size of the search result
     * @return true if all albums are added successfully,
     *      false if the size of the search result is not equal to checkSize
     */
    public boolean allAlbumSelection(int checkSize) {
        if (checkSize != searchAlbumList.size()) {
            return false;
        }
        for (Album album : searchAlbumList) {
            album.addAllSongsToLibrary();
            addAlbum(album);
        }
        return true;
    }

    /**
     * Add an album to the user's library based on the album number shown in the printed table.
     * The album numbering starts at 1.
     * @param index the album number
     * @param checkSize the size of the search result
     * @return true if the album is added successfully,
     *      false if the size of the search result is not equal to checkSize
     */
    public boolean handleAlbumSelection(int index, int checkSize) {
        if (checkSize != searchAlbumList.size()) {
            return false;
        }
        if (searchAlbumList.size() == 0){
            return false;
        }
        Album album = searchAlbumList.get(index);
        album.addAllSongsToLibrary();
        addAlbum(album);
        return true;
    }

    /**
     * Get the size of the user's album library
     * @return the size of the user's album library
     */
    public int getAlbumListSize() {
        return UserAlbums.size();
    }

    /**
     * Open an album in the user's library
     * @param albumTitle the title of the album to open
     * @return true if the album is opened successfully, false if the album is not found
     */
    public boolean openAlbum (String albumTitle, String sort) {
        if (!albumTitle.equals(currentAlbum.getTitle())) {
            return false;
        }
        searchSongList = currentAlbum.getSongs();
        sortSearchSongList(sort);
        return true;
    }

    public boolean showCompleteAlbumStore(String albumTitle) {
        if (!albumTitle.equals(currentAlbum.getTitle())) {
            return false;
        }
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        result.add(currentAlbum.toStringList(true));
        printAlbumSearchResults(result);
        return true;
    }

    /**
     * Get the list of album in the user's library
     * @return a list of albums in the user's library in String format
     */
    public ArrayList<ArrayList<String>> getAlbumList () {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for (Album album : UserAlbums.values()) {
            result.add(album.toStringList(false));
        }
        return result;
    }

    public boolean removeAlbumFromLibrary() {
        if (currentAlbum == null) {
            System.out.println("No album selected.");
            return false;
        }
        UserAlbums.remove(generateKey(currentAlbum));
        for (Song song : currentAlbum.getSongs()) {
            UserSongs.remove(generateKey(song));
        }
        currentAlbum = null;
        return true;
    }

    // -------------------------------------------------------------------------
    //                  PLAYINGLIST FUNCTIONS
    // -------------------------------------------------------------------------

    /**
     * Get the current playing list
     * @return a list of songs in the current playing list in String format
     */
    public ArrayList<ArrayList<String>> getCurrentPlayList () {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for (Song album : playingList.getSongs()) {
            result.add(album.toStringList());
        }
        return result;
    }


    /**
     * Print the current playing list in a table format
     */
    public void printPlaylist(String key) {
        playingList.printAsTable(key);
    }

    /**
     * Play a song in the user's library
     */
    public void playSong() {
        if (currentSong == null) {
            System.out.println("No song selected.");
        } else {
            playCounts.put(generateKey(currentSong), playCounts.getOrDefault(currentSong, 0) + 1);
            recentPlays.remove(currentSong);
            recentPlays.add(0, currentSong);
            if (recentPlays.size() > 10) {
                recentPlays.remove(recentPlays.size() - 1);
            }
            playingList.insertSong(currentSong);
            playingList.getPlaying();
        }
    }
    /**
     * Play a Album in the user's library with play all songs in the album
     * @param albumTitle the title of the album to play
     * @return true if the album is played successfully, false if the album is not found
     */
    public boolean playAlbum(String albumTitle) {
        if (currentAlbum == null) {
            System.out.println("No album selected.");
            return false;
        }
        if (!albumTitle.equalsIgnoreCase(currentAlbum.getTitle())) {
            return false;
        } else {
            ArrayList<Song> songs = currentAlbum.getSongs();
            // reverse the list to play songs from last to first
            Collections.reverse(songs);

            for (Song song : songs) {
                // Update stats as well
                playCounts.put(generateKey(song), playCounts.getOrDefault(song, 0) + 1);
                // Update recent plays
                recentPlays.remove(song);
                recentPlays.add(0, song);
                if (recentPlays.size() > 10) {
                    recentPlays.remove(recentPlays.size() - 1);
                }
                playingList.insertSong(song);
            }
            playingList.getPlaying();
            return true;
        }
    }

    public int getPlayerSize() {
        return playingList.getSongs().size();
    }

    public void getPlaying() {
        playingList.getPlaying();
    }

    public void playNextSong() {
        playingList.playNext();
    }


    public void clearPlaylist() {
        playingList.clear(true);
    }

    /**
     * Adds a song to the playlist based on the song number shown in the printed table.
     * The song numbering starts at 1.
     */
    public void addSongToPlaylist() {
        if (currentSong == null) {
            System.out.println("No song selected.");
        } else {
            playingList.addSong(currentSong, true);
        }
    }

    /**
     * Removes a song from the playlist based on the song number shown in the printed table.
     * The song numbering starts at 1.
     */
    public void removeSongFromPlaylist() {
        if (currentSong == null) {
            System.out.println("No song selected.");
        } else {
            playingList.removeSong(currentSong);
        }
    }

    /**
     * Get songs in playing list in String format
     * @return a list of songs in the playing list in String format
     */
    public ArrayList<ArrayList<String>> getPlaylistSongs() {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for (Song s : playingList.getSongs()) {
            result.add(s.toStringList());
        }
        return result;
    }

    public ArrayList<Song> getPlaylist() {
        return playingList.getSongs();
    }


    // -------------------------------------------------------------------------
    //                  PLAYLISTS FUNCTIONS
    // -------------------------------------------------------------------------

    public void getPlayLists(String name, String key) {
        PlayLists.printPlayList(name,key);
    }

    public ArrayList<String> getPlayListNames() {
        return PlayLists.getPlayListNames();
    }

    public void printAllPlayLists(String key) {
        PlayLists.printAllPlayLists(key);
    }

    public Boolean createPlaylist (String name) {
        return PlayLists.addPlayList(name);
    }

    public Boolean removePlayList(int index) {
        return PlayLists.removePlayList(index);
    }

    public int getPlayListsSize() {
        return PlayLists.getSize();
    }

    public void clearAllPlayLists() {
        PlayLists.clearAllPlayLists();
    }

    /**
     *  Add a song to the playlist based on the song number shown in the printed table.
     *  The song numbering starts at 1.
     */
    public void addSongToPlayLists() {
        if (currentPlaylist == null) {
            System.out.println("No playlist selected.");
        } else if (currentSong == null) {
            System.out.println("No song selected.");
        } else {
            assert currentPlaylist != null;
            PlayLists.addSongToPlayList(currentPlaylist.getName(), currentSong);
        }
    }

    /**
     *  Remove a song from the playlist based on the song number shown in the printed table.
     *  The song numbering starts at 1.
     */
    public void removeSongFromPlayLists() {
        if (currentPlaylist == null) {
            System.out.println("No playlist selected.");
        } else if (currentSong == null) {
            System.out.println("No song selected.");
        } else {
            assert currentPlaylist != null;
            PlayLists.removeSongFromPlayList(currentPlaylist.getName(), currentSong);
        }
    }


    public void selectCurrentPlaylist(int index) {
        currentPlaylist = PlayLists.getPlayList(index);
    }

    /**
     * Play the current playlist in playing list
     */
    public void playCurrentPlayList() {
        ArrayList<Song> songs = currentPlaylist.getSongs();
        // Reverse the list to play most recent songs first.
        Collections.reverse(songs);

        for (Song song : songs) {
            // Update stats first
            playCounts.put(generateKey(song), playCounts.getOrDefault(song, 0) + 1);
            recentPlays.remove(song);
            recentPlays.add(0, song);
            if (recentPlays.size() > 10) {
                recentPlays.remove(recentPlays.size() - 1);
            }
            if (playingList.getSongs().contains(song)) {
                playingList.removeSong(song);
            }
            playingList.insertSong(song);
        }
        playingList.getPlaying();
    }

    public int currentPlistSize() {
        return currentPlaylist.getSize();
    }

    public void printCurrentPlaylist(String key) {
        currentPlaylist.printAsTable(key);
    }

    public String getCurrentPlaylistName() {
        return currentPlaylist.getName();
    }

    //-------------------------------------------------------------------------
    //                  FAVORITE LIST FUNCTIONS
    // -------------------------------------------------------------------------

    public boolean isFavourite() {
        return currentSong.isFavourite();
    }

    public int getFavoriteListSize() {
        return favoriteList.getSize();
    }

    /**
     * Get songs in favorite list in String format
     * @return a list of songs in the favorite list in String format
     */
    public ArrayList<ArrayList<String>> getFavoriteListSongs() {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for (Song s : favoriteList.getSongs()) {
            result.add(s.toStringList());
        }
        return result;
    }

    public void printFavoriteList(String key) {
        favoriteList.printAsTable(key);
    }

    /**
     * Clear all songs in the favorite list
     */
    public void clearFavoriteList() {
        for (Song song : favoriteList.getSongs()) {
            song.setFavourite(false);
        }
        favoriteList.clear(true);
    }

    /**
     * Add a song to the favorite list based on the song number shown in the printed table.
     * The song numbering starts at 1.
     */
    public void addFavourite() {
        if (currentSong == null) {
            System.out.println("No song selected.");
        } else {
            favoriteList.addSong(currentSong, true);
            currentSong.setFavourite(true);
        }
    }

    /**
     * Remove a song from the favorite list based on the song number shown in the printed table.
     * The song numbering starts at 1.
     */
    public void removeFavourite() {
        if (currentSong == null) {
            System.out.println("No song selected.");
        } else {
            currentSong.setFavourite(false);
            favoriteList.removeSong(currentSong);
        }
    }

    public Map<String, Integer> getPlayCounts() {
        return new HashMap<>(playCounts);
    }

    /**
     * Play the favorite list in playing list
     */
    public void playFavoriteList() {
        ArrayList<Song> songs = favoriteList.getSongs();
        // Reverse to play most recent favorite first.
        Collections.reverse(songs);

        for (Song song : songs) {
            playCounts.put(generateKey(song), playCounts.getOrDefault(song,0) + 1);
            recentPlays.remove(song);
            recentPlays.add(0, song);
            if (recentPlays.size() > 10) {
                recentPlays.remove(recentPlays.size() - 1);
            }
            if (playingList.getSongs().contains(song)) {
                playingList.removeSong(song);
            }
            playingList.insertSong(song);
        }
        playingList.getPlaying();
    }


    // -------------------------------------------------------------------------
    //                  RATING FUNCTIONS
    // -------------------------------------------------------------------------

    public String printRating() {
        return currentSong.getTitle() + "[" + currentSong.getRating() + "]";
    }

    public void setRating(int rating) {
        currentSong.setRating(Rating.fromInt(rating));
    }

    public int getRating() {
        return currentSong.getRatingInt();
    }

    // -------------------------------------------------------------------------
    //                  STATS FUNCTIONS
    // -------------------------------------------------------------------------

    public List<Song> getTopFrequentSongs() {
        return playCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .map(entry -> {
                    String songKey = entry.getKey();
                    return UserSongs.get(songKey); // 在这里获得 Song
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public List<Song> getRecentSongs() {
        return new ArrayList<>(recentPlays);
    }


//    C: F generate automatic playlists.
    public void generateAutomaticPlaylists() {
        //i. Favorite Songs (all the songs the user has marked as favorite or rated as
        //5)
        String favPlaylistName = "Favorite Songs";
        if (!PlayLists.getPlayListNames().contains(favPlaylistName)) {
            PlayLists.addPlayList(favPlaylistName);
        }
        Playlist autoFavorite = PlayLists.getPlayListByName(favPlaylistName);
        if (autoFavorite != null) {
            autoFavorite.clear(false);
            for (Song song : favoriteList.getSongs()) {
                autoFavorite.addSong(song, false);
            }
        }
        //iii. Top Rated (all the songs rated as 4 or 5)
        //*The user should be able to specify how they want the list sorted when they do the query.

        String topRatedName = "Top Rated";
        if (!PlayLists.getPlayListNames().contains(topRatedName)) {
            PlayLists.addPlayList(topRatedName);
        }
        Playlist topRatedPlaylist = PlayLists.getPlayListByName(topRatedName);
        if (topRatedPlaylist != null) {
            topRatedPlaylist.clear(false);
            for (Song song : UserSongs.values()) {
                if (song.getRatingInt() >= 4) {
                    topRatedPlaylist.addSong(song, false);
                }
            }
        }

        //ii. Any genre for which there are at least 10 songs in the library. For
        //example, if the user’s library has 15 ROCK songs, 8 COUNTRY songs,
        //and 20 CLASSICAL songs, there should be playlists for ROCK and
        //CLASSICAL, but not COUNTRY.
        HashMap<String, ArrayList<Song>> genreMap = new HashMap<>();
        for (Song song : UserSongs.values()) {
            String genre = song.getGenre();
            if (!genreMap.containsKey(genre)) {
                genreMap.put(genre, new ArrayList<>());
            }
            genreMap.get(genre).add(song);
        }
        for (Map.Entry<String, ArrayList<Song>> entry : genreMap.entrySet()) {
            String genreName = entry.getKey();
            ArrayList<Song> songsOfGenre = entry.getValue();
            if (songsOfGenre.size() >= 10) {
                if (!PlayLists.getPlayListNames().contains(genreName)) {
                    PlayLists.addPlayList(genreName);
                }
                Playlist genrePlaylist = PlayLists.getPlayListByName(genreName);
                if (genrePlaylist != null) {
                    genrePlaylist.clear(false);
                    for (Song song : songsOfGenre) {
                        genrePlaylist.addSong(song, false);
                    }
                }
            }
        }
    }


    public static void sortSearchSongList(String sortOption) {
        if (searchSongList == null || sortOption == null) return;
        switch (sortOption.toLowerCase()) {
            case "title":
                Collections.sort(searchSongList, (s1, s2) -> s1.getTitle().compareToIgnoreCase(s2.getTitle()));
                break;
            case "artist":
                Collections.sort(searchSongList, (s1, s2) -> s1.getArtist().compareToIgnoreCase(s2.getArtist()));
                break;
            case "rating":
                Collections.sort(searchSongList, (s1, s2) -> {
                    int star1 = s1.getRating().contains("★") ? s1.getRating().replace("☆", "").length() : 0;
                    int star2 = s2.getRating().contains("★") ? s2.getRating().replace("☆", "").length() : 0;
                    int cmp = Integer.compare(star2, star1);
                    if (cmp != 0) return cmp;
                    cmp = s1.getTitle().compareToIgnoreCase(s2.getTitle());
                    if (cmp != 0) return cmp;
                    return s1.getArtist().compareToIgnoreCase(s2.getArtist());
                });
                break;
            case "shuffle":
                Collections.shuffle(searchSongList);
                break;
            default:
                break;
        }
    }

    /**
     * Print the search results for albums in a table format.
     * @param albumList the list of albums to print
     */
    public static void printAlbumSearchResults(ArrayList<ArrayList<String>> albumList) {
        if (albumList == null || albumList.isEmpty()) {
            System.out.println("❗ No Albums in Library.");
            return;
        }
        List<List<String>> tableRows = new ArrayList<>();
        tableRows.add(Arrays.asList("No.", "Title", "Artist", "Genre", "Year", "Album"));

        int albumNo = 1;
        for (ArrayList<String> albumStrList : albumList) {
            if (albumStrList.size() < 4) {
                continue;
            }
            String title = albumStrList.get(0);
            String artist = albumStrList.get(1);
            String year = albumStrList.get(2);
            String genre = albumStrList.get(3);
            List<String> tracks = albumStrList.subList(4, albumStrList.size());
            List<String> firstRow = new ArrayList<>();
            firstRow.add(String.valueOf(albumNo));
            firstRow.add(title);
            firstRow.add(artist);
            firstRow.add(genre);
            firstRow.add(year);
            firstRow.add(tracks.isEmpty() ? "" : tracks.get(0));
            tableRows.add(firstRow);
            for (int i = 1; i < tracks.size(); i++) {
                tableRows.add(Arrays.asList("", "", "", "", "", tracks.get(i)));
            }
            tableRows.add(Arrays.asList("###SEPARATOR###"));

            albumNo++;
        }
        if (!tableRows.isEmpty()) {
            List<String> lastRow = tableRows.get(tableRows.size() - 1);
            if (lastRow.size() == 1 && "###SEPARATOR###".equals(lastRow.get(0))) {
                tableRows.remove(tableRows.size() - 1);
            }
        }
        TablePrinter.printDynamicTable("Album Search Results", tableRows);
    }

    public Playlist getPlaylistByName(String name) {
        return PlayLists.getPlayListByName(name);
    }

    public static void setSearchSongList(ArrayList<Song> list) {
        searchSongList = list;
    }

    public static ArrayList<Song> LibgetSearchSongList() {
        return new ArrayList<>(searchSongList);
    }
}

