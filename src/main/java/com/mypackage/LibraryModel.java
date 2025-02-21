package la1;
import java.lang.reflect.Array;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LibraryModel {
    private final String UserID;
    // Function is a class that contains all the functions that can be used in the library
    private final Function function;
    // UserSongs stores all songs in the user's library, each item is a class Song
    private final HashMap<String, Song> UserSongs;
    // UserAlbums stores all albums in the user's library, each item is a class Album
    private final HashMap<String, Album> UserAlbums;
    private final ArrayList<Song> Playlists;
    private final Scanner scanner;

    public LibraryModel(String UserID) {
        this.UserID = UserID;
        this.function = new Function();
        this.UserSongs = new HashMap<>();
        this.UserAlbums = new HashMap<>();
        this.Playlists = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }

    public String getUserID() {
        return UserID;
    }

    public String generateKey(String title, String artist) {
        return (title.trim().toLowerCase() + "|" + artist.trim().toLowerCase());
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
    public void search (String keyword) {
        Map<String, ArrayList> map = function.search(keyword);
        if (map == null) {
            return;
        }
        Scanner scanner = new Scanner(System.in);
        String input = "";
        if (map.get("songs") != null) {
            ArrayList songs = map.get("songs");
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
            ArrayList albums = map.get("albums");
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
}
