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
    private final HashMap<String, Playlist> playlists;
    private final Scanner scanner;
    private ArrayList<Song> searchSongList;
    private ArrayList<Album> searchAlbumList;
    private final MusicStore musicStore;
    private final FavoriteList favoriteList;

    public LibraryModel(String UserID, MusicStore musicStore) {
        this.UserID = UserID;
        this.playlists = new HashMap<>();
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

    public int getSongListSize() {
        return UserSongs.size();
    }


    /**
    public ArrayList search (String keyword, boolean isMusicStore, boolean isSong) {
        if (isSong) {
            ArrayList<Song> songs;
            if (isMusicStore) {
                songs = function.searchSong(keyword);
            } else {
                songs = new ArrayList<>(UserSongs.values());
            }
            ArrayList<ArrayList<String>> result = new ArrayList<>();
            for (Song song : songs) {
                result.add(song.toStringList());
            }
            return result;
        } else {
            ArrayList<Album> albums;
            if (isMusicStore) {
                albums = function.searchAlbum(keyword);
            } else {
                albums = new ArrayList<>(UserAlbums.values());
            }
            ArrayList<ArrayList<String>> result = new ArrayList<>();
            for (Album album : albums) {
                result.add(album.toStringList());
            }
        }



        Scanner scanner = new Scanner(System.in);
        String input = "";
        if (map.get("songs") != null) {
            ArrayList<Song> songs = map.get("songs");
            System.out.println("Songs found:");
            for (Song song : songs) {
                if (song.getAlbum() == null) {
                    System.out.println(song.getTitle() + " by " + song.getArtist());
                } else {
                    System.out.println(song.getTitle() + " by " + song.getArtist() + ", album:" + song.getAlbum());
                }
            }

            System.out.println("Do you want to add any of these songs to your library? (enter number)");
            System.out.println("1) Add all songs  2) Select individually  3) Skip");
            while (true) {
                input = scanner.nextLine().trim();
                if (input.equals("1") || input.equals("2") || input.equals("3")) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 1, 2, or 3.\n");
                }
            }

            switch (input) {
                case "1":
                    System.out.println("Add all songs.");
                    for (Song song : songs) {
                        addSong(song);
                    }
                    break;
                case "2":
                    System.out.println("Select individually.");
                    System.out.println();
                    selectSingleSong(songs, false);
                    break;
                case "3":
                    System.out.println("Skip.");
                    break;
            }
        }

        if (map.get("album") != null) {
            ArrayList<Album> albums = map.get("albums");
            System.out.println("albums found:");
            for (Album album : albums) {
                System.out.println(album.getTitle() + " by " + album.getArtist() +
                        " (" + album.getYear() + ", " + album.getGenre() + ")");
                for (Song song : album.getSongs()) {
                    System.out.println("    " + song.getTitle());
                }
            }
            System.out.println("Do you want to add any of these albums to your library? (enter number)");
            System.out.println("1) Add all albums  2) Select individually  3) Skip");
            while (true) {
                input = scanner.nextLine().trim();
                if (input.equals("1") || input.equals("2") || input.equals("3")) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 1, 2, or 3.\n");
                }
            }

            switch (input) {
                case "1":
                    System.out.println("Add all albums.");
                    for (Album album : albums) {
                        addAlbum(album);
                    }
                    break;
                case "2":
                    System.out.println("Select individually.");
                    System.out.println();
                    selectSingleAlbum(albums);
                    break;
                case "3":
                    System.out.println("Skip.");
                    break;
            }
        }
    }
        */

    /**
     * Select a song from the list
     * If the song is found, add it to the user's library
     * If the song is not found, print "No results found for " + keyword
     *      enter the number of the song you want to add to your library
     *      enter 0 to exit
     */

    private void selectSingleSong(ArrayList<Song> songs, boolean isAlbum) {
        int i = 1;
        for (Song song : songs) {
            System.out.print(i + ") ");
            if (song.getAlbum() == null || isAlbum) {
                System.out.println(song.getTitle() + " by " + song.getArtist());
            } else {
                System.out.println(song.getTitle() + " by " + song.getArtist() + ", album:" + song.getAlbum());
            }
        }
        System.out.println("Enter the number of the song you want to add to your library. (enter 0 to exit)");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.matches("\\d+")) {
                int index = Integer.parseInt(input);
                if (index == 0) {
                    return;
                }
                if (index >= 1 && index <= songs.size()) {
                    Song song = songs.get(index - 1);
                    addSong(song);
                    System.out.println(song.getTitle() + " by " + song.getArtist() + " added to your library.");
                } else {
                    System.out.println("Invalid input. Please enter a number between 1 and " + songs.size() + ".");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Select an album from the list
     * If the album is found, add it to the user's library
     * If the album is not found, print "No results found for " + keyword
     *      enter the number of the album you want to add to your library
     *      enter 0 to exit
     *      1) Add all songs with album
     *      2) Select individually song in album (album will not add in your library)
     *      3) Return back
     */
    private void selectSingleAlbum(ArrayList<Album> albums) {
        int i = 1;
        for (Album album : albums) {
            System.out.print(i + ") ");
            System.out.println(album.getTitle() + " by " + album.getArtist() +
                    " (" + album.getYear() + ", " + album.getGenre() + ")");
            for (Song song : album.getSongs()) {
                System.out.println("    " + song.getTitle());
            }
        }
        System.out.println("Enter the number of the album you want to add to your library. (enter 0 to exit)");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.matches("\\d+")) {
                int index = Integer.parseInt(input);
                if (index == 0) {
                    return;
                }
                if (index >= 1 && index <= albums.size()) {
                    Album album = albums.get(index - 1);
                    System.out.println("Do you want to add all songs in this album to your library? ");
                    System.out.println("1) Add all songs with album  " +
                            "2) Select individually (album will not add in your library)  3) Return back");
                    while (true) {
                        input = scanner.nextLine().trim();
                        if (input.equals("1") || input.equals("2") || input.equals("3")) {
                            break;
                        } else {
                            System.out.println("Invalid input. Please enter 1, 2, or 3.\n");
                        }
                    }
                    switch (input) {
                        case "1":
                            System.out.println(album.getTitle() + " by " + album.getArtist() +
                                    " added to your library.");
                            addAlbum(albums.get(index - 1));
                        case "2":
                            System.out.println("Select individually.");
                            System.out.println();
                            selectSingleSong(albums.get(index - 1).getSongs(), true);
                        case "3":
                            System.out.println("Return back.");
                            break;
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number between 1 and " + albums.size() + ".");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    // ==================== playlist ====================

    /**
     * create a playlist
     */
    public void createPlaylist(String playlistName) {
        String key = playlistName.trim().toLowerCase();
        if (!playlists.containsKey(key)) {
            playlists.put(key, new Playlist());
            System.out.println("Playlist '" + playlistName + "' created.");
        } else {
            System.out.println("Playlist '" + playlistName + "' already exists.");
        }
    }

    /**
     * return the list, null if not exist
     */
    public Playlist getPlaylist(String playlistName) {
        return playlists.get(playlistName.trim().toLowerCase());
    }

    /**
     * clean up
     */
    public void clearPlaylist(String playlistName) {
        Playlist pl = getPlaylist(playlistName);
        if (pl != null) {
            pl.clear();
        } else {
            System.out.println("Playlist '" + playlistName + "' does not exist.");
        }
    }

    /**
     * find a song
     */
    private Song findSongInLibrary(String songTitle) {
        for (Song s : UserSongs.values()) {
            if (s.getTitle().equalsIgnoreCase(songTitle)) {
                return s;
            }
        }
        return null;
    }

    /**
     * put the song into playlist
     */
    public void addSongToPlaylist(String playlistName, String songTitle) {
        Song song = findSongInLibrary(songTitle);
        if (song == null) {
            System.out.println("Song '" + songTitle + "' not found in your library.");
            return;
        }
        Playlist pl = getPlaylist(playlistName);
        if (pl != null) {
            pl.addSong(song);
        } else {
            System.out.println("Playlist '" + playlistName + "' does not exist.");
        }
    }

    /**
     * remove a song from playlist
     */
    public void removeSongFromPlaylist(String playlistName, String songTitle) {
        Playlist pl = getPlaylist(playlistName);
        if (pl == null) {
            System.out.println("Playlist '" + playlistName + "' does not exist.");
            return;
        }
        Song found = null;
        for (Song s : pl.getSongs()) {
            if (s.getTitle().equalsIgnoreCase(songTitle)) {
                found = s;
                break;
            }
        }
        if (found != null) {
            pl.removeSong(found);
        } else {
            System.out.println("Song '" + songTitle + "' not found in playlist '" + playlistName + "'.");
        }
    }
    // ==================== fav list ====================

    /**
     * return current fav list
     */
    public FavoriteList getFavoriteList() {
        return favoriteList;
    }

    /**
     * add a song in the fav list
     */
    public void addSongToFavorite(String songTitle) {
        Song song = findSongInLibrary(songTitle);
        if (song == null) {
            System.out.println("Song '" + songTitle + "' not found in your library.");
            return;
        }
        favoriteList.addSong(song);
    }

    /**
     * remove
     */
    public void removeSongFromFavorite(String songTitle) {
        Song found = null;
        for (Song s : favoriteList.getSongs()) {
            if (s.getTitle().equalsIgnoreCase(songTitle)) {
                found = s;
                break;
            }
        }
        if (found != null) {
            favoriteList.removeSong(found);
        } else {
            System.out.println("Song '" + songTitle + "' not found in your favorite list.");
        }
    }



}
