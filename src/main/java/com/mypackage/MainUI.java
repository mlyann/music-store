package la1;

/**
 * =====================================================================================
 * This UI code (MainUI) was generated with assistance from an AI tool,
 * as allowed for the View part of this assignment.
 * Please see the comments for an explanation of each method.
 * =====================================================================================
 */

import java.util.List;
import java.util.Scanner;

public class MainUI {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        // A fancy intro banner with extra icons
        System.out.println("======================================================");
        System.out.println("    🎶 Welcome to the Music Library App (CSC 335) 🎶   ");
        System.out.println("         📅 Date: Feb 21, 2025");
        System.out.println("    👥 Authors: Haocheng Cao & Minglai Yang");
        System.out.println("======================================================");

        runMainMenu();
        System.out.println("🚪 Exiting application. Goodbye!");
    }

    /**
     * This method displays the main menu and prompts the user for an option.
     * The pipeline is:
     *   1) Search
     *   2) Playlist
     *   3) Favorite List
     *   0) Exit
     */
    private static void runMainMenu() {
        while (true) {
            System.out.println("\n---------- 🎵 MAIN MENU 🎵 ----------");
            System.out.println("1) 🔍 Search");
            System.out.println("2) 🎧 Playlist");
            System.out.println("3) ⭐ Favorite List");
            System.out.println("0) 🚪 Exit");
            System.out.print("👉 Enter your choice: ");

            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    runSearchMenu();
                    break;
                case "2":
                    runPlaylistMenu();
                    break;
                case "3":
                    runFavoriteMenu();
                    break;
                case "0":
                    return; // Exit the main menu (and the program)
                default:
                    System.out.println("❗ Invalid choice. Please try again.");
            }
        }
    }

    /**
     * SEARCH MENU
     * ----------------------------------------------------------
     * Pipeline for searching. User first chooses whether to
     * search the music store or the user library.
     * Then chooses to search by song or album.
     * Then prints results, letting the user pick or skip.
     *
     * After picking a specific Song or Album, user can:
     *   - Play
     *   - Rate
     *   - or Skip
     */
    private static void runSearchMenu() {
        System.out.println("\n🔍 [SEARCH MENU]");

        // 1) Choose search location
        String location = chooseSearchLocation();
        if (location.equals("BACK")) {
            return;
        }

        // 2) Search songs
        searchSongsPipeline(location);

        // 3) After finishing or skipping songs, search albums
        searchAlbumsPipeline(location);
    }

    /**
     * Step 1 in searching: ask user "Search Music Store or User Library?"
     */
    private static String chooseSearchLocation() {
        while (true) {
            System.out.println("\nWhere would you like to search?");
            System.out.println("1) 🏪 Music Store");
            System.out.println("2) 🏠 User Library");
            System.out.println("0) 🔙 Back to Main Menu");
            System.out.print("👉 Enter choice: ");
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    return "STORE";
                case "2":
                    return "LIBRARY";
                case "0":
                    return "BACK";
                default:
                    System.out.println("❗ Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Step 2: Search for songs in the chosen location.
     *
     * After we get the search results,
     * we print them in the format:
     *   Song:
     *   1) Song.toString()
     *   2) ...
     *   3) ...
     *
     * The user can:
     *   - choose "0" to skip
     *   - choose a specific index to select a Song
     *
     * Once a Song is selected, we let the user "play" or "rate",
     * then return to the list.
     */
    private static void searchSongsPipeline(String location) {
        System.out.println("\n--- 🎤 Searching for Songs ---");
        System.out.print("🔎 Enter song title or artist keyword (or blank to skip): ");
        String keyword = SCANNER.nextLine().trim();
        if (keyword.isEmpty()) {
            System.out.println("⏭ Skipping song search...");
            return;
        }

        // MODEL CALL: e.g., returns a List of song descriptions
        List<String> songResults = (location.equals("STORE"))
                ? searchSongInMusicStore(keyword)
                : searchSongInUserLibrary(keyword);

        if (songResults == null || songResults.isEmpty()) {
            System.out.println("❗ No songs found for '" + keyword + "'.");
            return;
        }

        boolean done = false;
        while (!done) {
            System.out.println("\n🎵 Songs found:");
            for (int i = 0; i < songResults.size(); i++) {
                System.out.println((i + 1) + ") 🎶 " + songResults.get(i));
            }
            System.out.println("0) ⏭ Skip songs (go to album search)");
            System.out.print("👉 Select a song by number (or 0 to skip): ");
            String choice = SCANNER.nextLine().trim();

            if (choice.equals("0")) {
                done = true; // skip to album search
            } else {
                try {
                    int index = Integer.parseInt(choice) - 1;
                    if (index >= 0 && index < songResults.size()) {
                        String selectedSong = songResults.get(index);
                        // Once a specific song is selected, let user "play" or "rate"
                        handleSongActions(selectedSong);
                    } else {
                        System.out.println("❗ Invalid index. Try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❗ Please enter a valid number.");
                }
            }
        }
    }

    /**
     * Step 3: Search for albums in the chosen location.
     * Similar to the song search pipeline.
     * After we get search results, we display them like:
     *   Album:
     *   1) ...
     *   2) ...
     *
     * Then if the user selects an album, we show the songs in that album
     * and let them play or rate them.
     */
    private static void searchAlbumsPipeline(String location) {
        System.out.println("\n--- 🎼 Searching for Albums ---");
        System.out.print("🔎 Enter album title or artist keyword (or blank to skip): ");
        String keyword = SCANNER.nextLine().trim();
        if (keyword.isEmpty()) {
            System.out.println("⏭ Skipping album search...");
            return;
        }

        // MODEL CALL: e.g., returns a List of album descriptions
        List<String> albumResults = (location.equals("STORE"))
                ? searchAlbumInMusicStore(keyword)
                : searchAlbumInUserLibrary(keyword);

        if (albumResults == null || albumResults.isEmpty()) {
            System.out.println("❗ No albums found for '" + keyword + "'.");
            return;
        }

        boolean done = false;
        while (!done) {
            System.out.println("\n💿 Albums found:");
            for (int i = 0; i < albumResults.size(); i++) {
                System.out.println((i + 1) + ") 💿 " + albumResults.get(i));
            }
            System.out.println("0) 🔙 Skip (return to main menu)");
            System.out.print("👉 Select an album by number (or 0 to skip): ");
            String choice = SCANNER.nextLine().trim();

            if (choice.equals("0")) {
                done = true; // go back to main menu
            } else {
                try {
                    int index = Integer.parseInt(choice) - 1;
                    if (index >= 0 && index < albumResults.size()) {
                        String selectedAlbum = albumResults.get(index);
                        handleAlbumActions(selectedAlbum);
                    } else {
                        System.out.println("❗ Invalid index. Try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❗ Please enter a valid number.");
                }
            }
        }
    }

    /**
     * Once a user selects a Song, they can choose to play or rate it.
     */
    private static void handleSongActions(String selectedSong) {
        System.out.println("\n🎶 Selected Song: " + selectedSong);

        boolean done = false;
        while (!done) {
            System.out.println("Actions: ");
            System.out.println("1) ▶️ Play song");
            System.out.println("2) ⭐ Rate song");
            System.out.println("0) 🔙 Go back to song list");
            System.out.print("👉 Enter your choice: ");
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    // MODEL CALL: playSong(selectedSong);
                    playSong(selectedSong);
                    break;
                case "2":
                    System.out.print("✏️ Enter your rating (1 to 5): ");
                    try {
                        int rating = Integer.parseInt(SCANNER.nextLine().trim());
                        if (rating >= 1 && rating <= 5) {
                            // MODEL CALL: rateSong(selectedSong, rating);
                            rateSong(selectedSong, rating);
                        } else {
                            System.out.println("❗ Rating must be between 1 and 5.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❗ Invalid rating input.");
                    }
                    break;
                case "0":
                    done = true;
                    break;
                default:
                    System.out.println("❗ Invalid choice. Try again.");
            }
        }
    }

    /**
     * Once a user selects an Album, we show the songs in that album,
     * then let them pick a song to "play" or "rate."
     */
    private static void handleAlbumActions(String selectedAlbum) {
        System.out.println("\n🎵 Selected Album: " + selectedAlbum);

        // MODEL CALL: getSongListFromAlbum(selectedAlbum)
        List<String> albumSongs = getSongsInAlbum(selectedAlbum);
        if (albumSongs == null || albumSongs.isEmpty()) {
            System.out.println("❗ No songs found in this album. Returning to album list...");
            return;
        }

        while (true) {
            System.out.println("\n🎶 Songs in " + selectedAlbum + ":");
            for (int i = 0; i < albumSongs.size(); i++) {
                System.out.println((i + 1) + ") 🎶 " + albumSongs.get(i));
            }
            System.out.println("0) 🔙 Back to Album List");
            System.out.print("👉 Select a song to handle: ");
            String choice = SCANNER.nextLine().trim();

            if (choice.equals("0")) {
                break; // go back to album list
            } else {
                try {
                    int index = Integer.parseInt(choice) - 1;
                    if (index >= 0 && index < albumSongs.size()) {
                        String selectedSong = albumSongs.get(index);
                        handleSongActions(selectedSong);
                    } else {
                        System.out.println("❗ Invalid index.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❗ Please enter a valid number.");
                }
            }
        }
    }

    /**
     * PLAYLIST MENU
     * ----------------------------------------------------------
     * The user can:
     *   1) Create a new playlist
     *   2) Clear an existing playlist
     *   3) Add songs to a playlist
     *   4) Remove songs from a playlist
     *   5) Play songs (prompt which one)
     *   6) Rate a song in the playlist
     *   0) Back
     */
    private static void runPlaylistMenu() {
        while (true) {
            System.out.println("\n---------- 🎧 PLAYLIST MENU 🎧 ----------");
            System.out.println("1) ➕ Create a new playlist");
            System.out.println("2) 🗑️ Clear an existing playlist");
            System.out.println("3) ➕ Add songs to a playlist");
            System.out.println("4) ❌ Remove songs from a playlist");
            System.out.println("5) ▶️ Play songs in a playlist");
            System.out.println("6) ⭐ Rate a song in a playlist");
            System.out.println("0) 🔙 Back to Main Menu");
            System.out.print("👉 Enter your choice: ");

            String choice = SCANNER.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.print("✏️ Enter a new playlist name: ");
                    String newPlaylist = SCANNER.nextLine().trim();
                    // MODEL CALL: createPlaylist(newPlaylist);
                    createPlaylist(newPlaylist);
                    break;
                case "2":
                    clearPlaylist();
                    break;
                case "3":
                    addSongToPlaylist();
                    break;
                case "4":
                    removeSongFromPlaylist();
                    break;
                case "5":
                    playSongInPlaylist();
                    break;
                case "6":
                    rateSongInPlaylist();
                    break;
                case "0":
                    return; // back to main menu
                default:
                    System.out.println("❗ Invalid choice. Try again.");
            }
        }
    }

    /**
     * FAVORITE LIST MENU
     * ----------------------------------------------------------
     * The user can see the favorite songs, then optionally:
     *   - Play
     *   - Rate
     */
    private static void runFavoriteMenu() {
        System.out.println("\n---------- ⭐ FAVORITE LIST ⭐ ----------");

        // MODEL CALL: getFavoriteSongs() -> List<String>
        List<String> favorites = getFavoriteSongs();
        if (favorites == null || favorites.isEmpty()) {
            System.out.println("❗ No favorite songs yet!");
            return;
        }

        while (true) {
            System.out.println("\n🎶 Your Favorite Songs:");
            for (int i = 0; i < favorites.size(); i++) {
                System.out.println((i + 1) + ") 🎶 " + favorites.get(i));
            }
            System.out.println("0) 🔙 Back to Main Menu");
            System.out.print("👉 Select a favorite song to handle: ");

            String choice = SCANNER.nextLine().trim();
            if (choice.equals("0")) {
                break;
            }

            try {
                int index = Integer.parseInt(choice) - 1;
                if (index >= 0 && index < favorites.size()) {
                    String selectedSong = favorites.get(index);
                    handleSongActions(selectedSong);
                } else {
                    System.out.println("❗ Invalid index. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❗ Please enter a valid number.");
            }
        }
    }

    // ===========================================================================
    // BELOW ARE ALL STUB / PLACEHOLDER METHODS FOR DEMONSTRATION PURPOSES
    // In your actual application, you should replace these with calls to the Model.
    // ===========================================================================

    /**
     * MODEL CALL STUB: search for songs in the Music Store by a keyword
     */
    private static List<String> searchSongInMusicStore(String keyword) {
        // TODO: implement using your model's MusicStore methods
        return List.of("🎶 SongA by ArtistA (STORE)", "🎶 SongB by ArtistB (STORE)");
    }

    /**
     * MODEL CALL STUB: search for songs in the User Library by a keyword
     */
    private static List<String> searchSongInUserLibrary(String keyword) {
        // TODO: implement using your model's user library methods
        return List.of("🎶 SongX by ArtistX (LIBRARY)", "🎶 SongY by ArtistY (LIBRARY)");
    }

    /**
     * MODEL CALL STUB: search for albums in the Music Store by a keyword
     */
    private static List<String> searchAlbumInMusicStore(String keyword) {
        // TODO: implement using your model
        return List.of("💿 Album1 by ArtistA (STORE)", "💿 Album2 by ArtistB (STORE)");
    }

    /**
     * MODEL CALL STUB: search for albums in the User Library by a keyword
     */
    private static List<String> searchAlbumInUserLibrary(String keyword) {
        // TODO: implement using your model
        return List.of("💿 AlbumX by ArtistX (LIBRARY)", "💿 AlbumY by ArtistY (LIBRARY)");
    }

    /**
     * MODEL CALL STUB: once an album is selected, retrieve its songs
     */
    private static List<String> getSongsInAlbum(String albumDesc) {
        // TODO: parse the albumDesc if needed, or store an ID.
        return List.of("🎶 Song1_of_" + albumDesc, "🎶 Song2_of_" + albumDesc, "🎶 Song3_of_" + albumDesc);
    }

    /**
     * MODEL CALL STUB: play a song
     */
    private static void playSong(String songDesc) {
        // TODO: implement using your model
        System.out.println("▶️ Now playing: " + songDesc + " ...");
    }

    /**
     * MODEL CALL STUB: rate a song (1..5)
     */
    private static void rateSong(String songDesc, int rating) {
        // TODO: implement using your model
        System.out.println("⭐ You rated " + songDesc + " with " + rating + " stars.");
    }

    /**
     * MODEL CALL STUB: create a new playlist
     */
    private static void createPlaylist(String playlistName) {
        // TODO: implement using your model
        System.out.println("🎵 Playlist '" + playlistName + "' created.");
    }

    private static void clearPlaylist() {
        System.out.print("🗑️ Enter the playlist name to clear: ");
        String plName = SCANNER.nextLine().trim();
        // TODO: implement your model call
        System.out.println("🗑️ Cleared all songs from '" + plName + "'.");
    }

    private static void addSongToPlaylist() {
        System.out.print("➕ Enter the playlist name to add songs: ");
        String plName = SCANNER.nextLine().trim();
        System.out.print("🎶 Enter the song to add (title or ID): ");
        String songDesc = SCANNER.nextLine().trim();
        // TODO: implement your model call
        System.out.println("🎶 Added '" + songDesc + "' to playlist '" + plName + "'.");
    }

    private static void removeSongFromPlaylist() {
        System.out.print("❌ Enter the playlist name to remove songs: ");
        String plName = SCANNER.nextLine().trim();
        System.out.print("🎶 Enter the song to remove: ");
        String songDesc = SCANNER.nextLine().trim();
        // TODO: implement your model
        System.out.println("❌ Removed '" + songDesc + "' from playlist '" + plName + "'.");
    }

    private static void playSongInPlaylist() {
        System.out.print("▶️ Enter the playlist name to play a song: ");
        String plName = SCANNER.nextLine().trim();
        // MODEL CALL: get the songs from the model
        List<String> songs = List.of("🎶 SongA_of_" + plName, "🎶 SongB_of_" + plName); // STUB
        if (songs.isEmpty()) {
            System.out.println("❗ No songs in playlist '" + plName + "'.");
            return;
        }
        System.out.println("🎵 Songs in '" + plName + "':");
        for (int i = 0; i < songs.size(); i++) {
            System.out.println((i + 1) + ") 🎶 " + songs.get(i));
        }
        System.out.print("👉 Select a song by number to play: ");
        String choice = SCANNER.nextLine().trim();
        try {
            int index = Integer.parseInt(choice) - 1;
            if (index >= 0 && index < songs.size()) {
                playSong(songs.get(index));
            } else {
                System.out.println("❗ Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❗ Invalid input.");
        }
    }

    private static void rateSongInPlaylist() {
        System.out.print("✏️ Enter the playlist name to rate a song: ");
        String plName = SCANNER.nextLine().trim();
        List<String> songs = List.of("🎶 SongA_of_" + plName, "🎶 SongB_of_" + plName); // STUB
        if (songs.isEmpty()) {
            System.out.println("❗ No songs in playlist '" + plName + "'.");
            return;
        }
        System.out.println("🎵 Songs in '" + plName + "':");
        for (int i = 0; i < songs.size(); i++) {
            System.out.println((i + 1) + ") 🎶 " + songs.get(i));
        }
        System.out.print("👉 Select a song by number to rate: ");
        String choice = SCANNER.nextLine().trim();
        try {
            int index = Integer.parseInt(choice) - 1;
            if (index >= 0 && index < songs.size()) {
                String selectedSong = songs.get(index);
                System.out.print("✏️ Enter rating (1 to 5): ");
                int rating = Integer.parseInt(SCANNER.nextLine().trim());
                if (rating < 1 || rating > 5) {
                    System.out.println("❗ Invalid rating range.");
                } else {
                    rateSong(selectedSong, rating);
                }
            } else {
                System.out.println("❗ Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❗ Invalid input.");
        }
    }

    /**
     * MODEL CALL STUB: get the list of favorite songs
     */
    private static List<String> getFavoriteSongs() {
        // TODO: implement using your model
        return List.of("🎶 FavoriteSong1", "🎶 FavoriteSong2");
    }
}