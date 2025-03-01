package la1;
import java.util.*;

public class LibraryModel {
    private final String UserID;
    // Function is a class that contains all the functions that can be used in the library
    // UserSongs stores all songs in the user's library, each item is a class Song
    private final HashMap<List<String>, Song> UserSongs;
    // UserAlbums stores all albums in the user's library, each item is a class Album
    private final HashMap<List<String>, Album> UserAlbums;
    private final PlayLists PlayLists;
    private Playlist playingList;
    private final Scanner scanner;
    private ArrayList<Song> searchSongList;
    private ArrayList<Album> searchAlbumList;
    private Playlist currentPlaylist;
    private Song currentSong;
    private Album currentAlbum;

    private final MusicStore musicStore;
    private static FavoriteList favoriteList;

    public LibraryModel(String UserID, MusicStore musicStore) {
        this.UserID = UserID;
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

    public List<String> generateKey(String title, String artist) {
        return List.of(title.trim().toLowerCase(), artist.trim().toLowerCase());
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

    public void userSongSerch() {
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

    public void printCurrentPlayList() {
        playingList.printAsTable();
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
    private void addSong(Song song) {
        UserSongs.put(generateKey(song.getTitle(), song.getArtist()), song);
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
        Map<List<String>, Song> songMap;
        if (isMusicStore) {
            songMap = musicStore.getSongMap();
        } else {
            songMap = UserSongs;
        }
        ArrayList<Song> result = new ArrayList<>();
        String lowerKeyword = keyword.trim().toLowerCase();
        for (List<String> key : songMap.keySet()) {
            for (String part : key) {
                if (part.equals(lowerKeyword)) {
                    result.add(songMap.get(key));
                    break;
                }
            }
        }
        return result;
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
        addSong(searchSongList.get(index));
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
    public ArrayList<ArrayList<String>> SongToString () {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        result.add(currentAlbum.getAlbumInfo());
        for (Song song : searchSongList) {
            result.add(song.toStringList());
        }
        return result;
    }



    /**
     * Print the user's song library in a table format
     * The table includes the song's title, artist, genre, year, favorite status, rating, and album
     */
    public void printUserSongsTable() {
        // Prepare the header row. Adjust the column names as needed.
        List<List<String>> tableData = new ArrayList<>();
        List<String> header = Arrays.asList("No.", "Title", "Artist", "Genre", "Year", "Favorite", "Rating", "Album");
        tableData.add(header);

        // Add a separator marker to visually separate the header from the data rows.
        tableData.add(Arrays.asList("###SEPARATOR###"));

        // Iterate over the user's songs and add each one as a row.
        int index = 1;
        for (Song song : UserSongs.values()) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(index++));
            row.add(song.getTitle());
            row.add(song.getArtist());
            row.add(song.getGenre());
            row.add(String.valueOf(song.getYear()));
            row.add(song.getFavourite());
            row.add(song.getRating());
            row.add(song.getAlbum() != null ? song.getAlbum() : "");
            tableData.add(row);
        }

        // Print the table with a title
        TablePrinter.printDynamicTable("User Songs Library", tableData);
    }

    public ArrayList<ArrayList<String>> getSongList() {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for (Song song : UserSongs.values()) {
            result.add(song.toStringList());
        }
        return result;
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
        UserAlbums.put(generateKey(album.getTitle(), album.getArtist()), album);
        for (Song song : album.getSongs()) {
            UserSongs.put(generateKey(song.getTitle(), song.getArtist()), song);
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
            result.add(album.toStringList());
        }
        System.out.println(result);
        return result;
    }

    /**
     * Search for an album in the user's library or the music store
     * @param keyword the search keyword
     * @param isMusicStore true if searching in the music store, false if searching in the user's library
     * @return a list of albums that match the search keyword
     */
    public ArrayList<Album> searchAlbumSub(String keyword, boolean isMusicStore) {
        Map<List<String>, Album> albumMap;
        if (isMusicStore) {
            albumMap = musicStore.getAlbumMap();
        } else {
            albumMap = UserAlbums;
        }
        ArrayList<Album> result = new ArrayList<>();;
        for (List<String> key : albumMap.keySet()) {
            for (String part : key) {
                if (part.equalsIgnoreCase(keyword.trim())) {
                    result.add(albumMap.get(key));
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
        addAlbum(searchAlbumList.get(index));
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
    public boolean openAlbum (String albumTitle) {
        if (!albumTitle.equals(currentAlbum.getTitle())) {
            return false;
        }
        searchSongList = currentAlbum.getSongs();
        return true;
    }

    /**
     * Get the list of album in the user's library
     * @return a list of albums in the user's library in String format
     */
    public ArrayList<ArrayList<String>> getAlbumList () {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for (Album album : UserAlbums.values()) {
            result.add(album.toStringList());
        }
        return result;
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
    public void printPlaylist() {
        playingList.printAsTable();
    }

    /**
     * Play a song in the user's library
     */
    public void playSong() {
        if (currentSong == null) {
            System.out.println("No song selected.");
        } else {
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
        if (! albumTitle.equalsIgnoreCase(currentAlbum.getTitle())) {
            return false;
        } else {
            ArrayList<Song> songs = currentAlbum.getSongs();
            Collections.reverse(songs);
            for (Song song : songs) {
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
        playingList.clear();
    }

    /**
     * Adds a song to the playlist based on the song number shown in the printed table.
     * The song numbering starts at 1.
     */
    public void addSongToPlaylist() {
        if (currentSong == null) {
            System.out.println("No song selected.");
        } else {
            playingList.addSong(currentSong);
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

    public void getPlayLists(String name) {
        PlayLists.printPlayList(name);
    }

    public ArrayList<String> getPlayListNames() {
        return PlayLists.getPlayListNames();
    }

    public void printAllPlayLists() {
        PlayLists.printAllPlayLists();
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
        Collections.reverse(songs);
        for (Song song : songs) {
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

    public void printCurrentPlaylist() {
        currentPlaylist.printAsTable();
    }

    public String getCurrentPlaylistName() {
        return currentPlaylist.getName();
    }
    public ArrayList<Song> getCurrentPlaylist() {
        return new ArrayList<>(currentPlaylist.getSongs());
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

    public void printFavoriteList() {
        favoriteList.printAsTable();
    }

    /**
     * Clear all songs in the favorite list
     */
    public void clearFavoriteList() {
        for (Song song : favoriteList.getSongs()) {
            song.setFavourite(false);
        }
        favoriteList.clear();
    }

    /**
     * Add a song to the favorite list based on the song number shown in the printed table.
     * The song numbering starts at 1.
     */
    public void addFavourite() {
        if (currentSong == null) {
            System.out.println("No song selected.");
        } else {
            favoriteList.addSong(currentSong);
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

    /**
     * Play the favorite list in playing list
     */
    public void playFavoriteList() {
        ArrayList<Song> songs = favoriteList.getSongs();
        Collections.reverse(songs);
        for (Song song : songs) {
            if (playingList.getSongs().contains(song)) {
                playingList.removeSong(song);
            }
            playingList.insertSong(song);
        }
        playingList.getPlaying();
    }

    public void shufflePlaylist() {
        playingList.shuffle();
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

}
