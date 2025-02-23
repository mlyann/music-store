package la1;
import java.lang.reflect.Array;
import java.util.*;

public class LibraryModel {
    private final String UserID;
    // Function is a class that contains all the functions that can be used in the library
    // UserSongs stores all songs in the user's library, each item is a class Song
    private final HashMap<List<String>, Song> UserSongs;
    // UserAlbums stores all albums in the user's library, each item is a class Album
    private final HashMap<List<String>, Album> UserAlbums;
    private Playlist playlist;
    private final Scanner scanner;
    private ArrayList<Song> searchSongList;
    private ArrayList<Album> searchAlbumList;
    private Song currentSong;
    private Album currentAlbum;

    private final MusicStore musicStore;
    private static FavoriteList favoriteList;

    public LibraryModel(String UserID, MusicStore musicStore) {
        this.UserID = UserID;
        this.playlist = new Playlist();
        this.favoriteList = new FavoriteList();
        this.UserSongs = new HashMap<>();
        this.UserAlbums = new HashMap<>();
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


    /**
     * Add a song to the user's library
     * Title|artist as key, class Song as value
     */
    private void addSong(Song song) {
        UserSongs.put(generateKey(song.getTitle(), song.getArtist()), song);
    }

    /**
     * Add an album to the user's library
     * Title|artist as key, class Album as value
     * also add all songs in the album to the library
     */
    private void addAlbum(Album album) {
        UserAlbums.put(generateKey(album.getTitle(), album.getArtist()), album);
        for (Song song : album.getSongs()) {
            UserSongs.put(generateKey(song.getTitle(), song.getArtist()), song);
        }
    }

    /**
     * Search a song
     * If the song is found, add it to the user's library
     * If the song is not found, print "No results found for " + keyword
     *      for Search:
     *          enter a keyword, automatically search the song and album, match the title or artist
     *          enter number to select the song or album to add to the library
     *          1) Add all songs/albums
     *          2) Select individually
     *          3) Skip
     *      for Select individually:
     *          enter the number of the song/album you want to add to your library
     *          enter 0 to exit
     *          1) Add all songs with album
     *          2) Select individually song in album (album will not add in your library)
     *          3) Return back
     */
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
        return result;
    }

    public ArrayList<Song> searchSongSub(String keyword, boolean isMusicStore) {
        Map<List<String>, Song> songMap;
        if (isMusicStore) {
            songMap = musicStore.getSongMap();
        } else {
            songMap = UserSongs;
        }
        ArrayList<Song> result = new ArrayList<>();
        String lowerKeyword = keyword.trim().toLowerCase();
        // 遍历每个 List<String> key，分别比较其中的每个元素
        for (List<String> key : songMap.keySet()) {
            for (String part : key) {
                if (part.equals(lowerKeyword)) {  // 完全匹配（忽略大小写）
                    result.add(songMap.get(key));
                    break;  // 一旦匹配，避免重复添加
                }
            }
        }
        return result;
    }

    public ArrayList<Album> searchAlbumSub(String keyword, boolean isMusicStore) {
        Map<List<String>, Album> albumMap;
        if (isMusicStore) {
            albumMap = musicStore.getAlbumMap();
        } else {
            albumMap = UserAlbums;
        }
        ArrayList<Album> result = new ArrayList<>();
        String lowerKeyword = keyword.trim().toLowerCase();
        // 同样遍历 key 的每个部分，要求完全匹
        for (List<String> key : albumMap.keySet()) {
            for (String part : key) {
                if (part.equals(lowerKeyword)) {
                    result.add(albumMap.get(key));
                    break;
                }
            }
        }
        return result;
    }

    public boolean allSongSelection(int checkSize) {
        if (checkSize != searchSongList.size()) {
            return false;
        }
        for (Song song : searchSongList) {
            addSong(song);
        }
        return true;
    }

    public boolean handleSongSelection(int index, int checkSize) {
        // make sure the searchSongList print String is the same as the searchSongList Song Object
        if (checkSize != searchSongList.size()) {
            return false;
        }
        addSong(searchSongList.get(index));
        return true;
    }

    public boolean allAlbumSelection(int checkSize) {
        if (checkSize != searchAlbumList.size()) {
            return false;
        }
        for (Album album : searchAlbumList) {
            addAlbum(album);
        }
        return true;
    }

    public boolean handleAlbumSelection(int index, int checkSize) {
        if (checkSize != searchAlbumList.size()) {
            return false;
        }
        addAlbum(searchAlbumList.get(index));
        return true;
    }

    public int getSongListSize() {
        return UserSongs.size();
    }

    public int getAlbumListSize() {
        return UserAlbums.size();
    }

    public boolean setCurrentSong(int index, String songTitle) {
        Song song = searchSongList.get(index);
        if (!song.getTitle().equalsIgnoreCase(songTitle)) {
            return false;
        }
        currentSong = song;
        return true;
    }

    public boolean setCurrentAlbum(int index, String albumTitle) {
        Album album = searchAlbumList.get(index);
        if (!album.getTitle().equalsIgnoreCase(albumTitle)) {
            return false;
        }
        currentAlbum = album;
        return true;
    }

    public boolean openAlbum (String albumTitle) {
        if (!albumTitle.equals(currentAlbum.getTitle())) {
            return false;
        }
        searchSongList = currentAlbum.getSongs();
        return true;
    }

    public ArrayList<ArrayList<String>> getCurrentPlayList () {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for (Song album : playlist.getSongs()) {
            result.add(album.toStringList());
        }
        return result;
    }

    public ArrayList<ArrayList<String>> SongToString () {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        result.add(currentAlbum.getAlbumInfo());
        for (Song song : searchSongList) {
            result.add(song.toStringList());
        }
        return result;
    }

    public void printPlaylist() {
        String name = "Playlist";
        playlist.printAsTable(name);
    }

    public void playSong() {
        if (currentSong == null) {
            System.out.println("No song selected.");
        } else {
            playlist.insertSong(currentSong);
            playlist.getPlaying();
        }
    }

    public boolean playAlbum(String albumTitle) {
        if (! albumTitle.equalsIgnoreCase(currentAlbum.getTitle())) {
            return false;
        }
        if (currentAlbum == null) {
            System.out.println("No album selected.");
            return false;
        } else {
            ArrayList<Song> songs = currentAlbum.getSongs();
            Collections.reverse(songs);
            for (Song song : songs) {
                playlist.insertSong(song);
            }
            playlist.getPlaying();
            return true;
        }
    }

    public void playNextSong() {
        playlist.playNext();
    }



    /**
     * return copy of the list
     */
    public FavoriteList getFavoriteList() {
        return favoriteList;
    }

    public void clearPlaylist() {
        playlist.clear();
    }

    /**
     * Adds a song to the playlist based on the song number shown in the printed table.
     * The song numbering starts at 1.
     */
    public void addSongToPlaylist(int number) {
        ArrayList<Song> librarySongs = new ArrayList<>(UserSongs.values());
        if (number < 1 || number > librarySongs.size()) {
            System.out.println("Invalid selection. Please enter a number between 1 and " + librarySongs.size() + ".");
            return;
        }
        Song selectedSong = librarySongs.get(number - 1);
        playlist.addSong(selectedSong);
    }


    /**
     * remove song via title
     */
    public void removeSongFromPlaylist(int number) {
        ArrayList<Song> playlistSongs = new ArrayList<>(playlist.getSongs());
        if (number < 1 || number > playlistSongs.size()) {
            System.out.println("Invalid selection. Please enter a number between 1 and " + playlistSongs.size() + ".");
            return;
        }
        Song songToRemove = playlistSongs.get(number - 1);
        playlist.removeSong(songToRemove);
    }

    /**
     * add song based on fovorite list
     */
    public void addSongToFavorites(int number) {
        ArrayList<Song> librarySongs = new ArrayList<>(UserSongs.values());
        if (number < 1 || number > librarySongs.size()) {
            System.out.println("Invalid selection. Please enter a number between 1 and " + librarySongs.size() + ".");
            return;
        }
        Song selectedSong = librarySongs.get(number - 1);
        favoriteList.addSong(selectedSong);
        System.out.println("Added song: " + selectedSong.getTitle() + " to your favorite list.");
    }

    /**
     * remove song form favorite list
     */
    public void removeSongFromFavorites(int number) {
        ArrayList<Song> favoriteSongs = new ArrayList<>(favoriteList.getSongs());
        if (number < 1 || number > favoriteSongs.size()) {
            System.out.println("Invalid selection. Please enter a number between 1 and " + favoriteSongs.size() + ".");
            return;
        }
        Song songToRemove = favoriteSongs.get(number - 1);
        favoriteList.removeSong(songToRemove);
        System.out.println("Removed song: " + songToRemove.getTitle() + " from your favorite list.");
    }

    public ArrayList<ArrayList<String>> getPlaylistSongs() {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for (Song s : playlist.getSongs()) {
            result.add(s.toStringList());
        }
        return result;
    }

    public ArrayList<Song> getPlaylist() {
        return playlist.getSongs();
    }
    /**
     * Returns a deep copy of the user's songs as an ArrayList.
     * Each Song in the returned list is a new object created using the copy constructor.
     */
    public ArrayList<Song> getUserSongs() {
        ArrayList<Song> deepCopyList = new ArrayList<>();
        for (Song song : UserSongs.values()) {
            deepCopyList.add(new Song(song));
        }
        return deepCopyList;
    }


    /**
     * Prints a table of the user's songs with a numbered "No." column.
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

}
