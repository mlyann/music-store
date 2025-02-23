package la1; // Or wherever your package is

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainUI {

    // Reference to your backend
    private static MusicStore musicStore;       // your MusicStore class
    private static LibraryModel libraryModel;   // your LibraryModel class

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        // Initialize your backend objects (as needed)
        musicStore = new MusicStore();
        libraryModel = new LibraryModel("Chcking2", musicStore);
        String choice = SCANNER.nextLine().trim();
        inputSongs();

        // Example: load your music store data
        // musicStore.loadAlbums("path/to/albums.txt", "path/to/album_files");

        // A fancy intro banner
        System.out.println("======================================================");
        System.out.println("    🎶 Welcome to the Music Library App (CSC 335) 🎶   ");
        System.out.println("         📅 Date: Feb 21, 2025");
        System.out.println("    👥 Authors: Haocheng Cao & Minglai Yang");
        System.out.println("======================================================");

        runMainMenu();
        System.out.println("🚪 Exiting application. Goodbye!");
    }

    public static void inputSongs() {
        while (true) {
            System.out.print("Enter song details (format: title, artist[, genre, year]) or 0 to exit: ");
            String input = SCANNER.nextLine().trim();
            if (input.equals("0")) {
                break;
            }
            try {
                musicStore.loadSong(input);
                System.out.println("Song loaded successfully.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error loading song: " + e.getMessage());
            }
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        }
    }

    /**
     * MAIN MENU
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
                    System.out.println("🚧 Playlist feature is under construction.");
                    //runPlaylistMenu();
                    break;
                case "3":
                    System.out.println("🚧 Favorite List feature is under construction.");
                    //runFavoriteMenu();
                    break;
                case "4":
                    System.out.println("🚧 ");
                    //runFavoriteMenu();
                    System.out.println(musicStore.printSongs());
                    System.out.println(musicStore.printAlbums());
                    break;
                case "0":
                    return;
                default:
                    System.out.println("❗ Invalid choice. Please try again.");
            }
        }
    }

    /**
     * SEARCH MENU
     */
    private static void runSearchMenu() {
        while (true) {
            System.out.println("\n🔍 [SEARCH MENU]");
            String location = chooseSearchLocation();
            if (location.equals("BACK")) {
                return;
            }

            // 1) Search for songs
            searchSongsPipeline(location);

            // 2) Search for albums
            searchAlbumsPipeline(location);
        }
    }

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
     * Search for songs in either the Music Store or the User Library.
     * We expect a List<List<String>> from your model.
     */
    private static void searchSongsPipeline(String location) {
        while (true) {
            System.out.println("\n--- 🎤 Searching for Songs ---");
            System.out.print("🔎 Enter song title or artist keyword  ");
            System.out.print("\uD83D\uDD19 Enter 0 back to search menu: ");
            String keyword = SCANNER.nextLine().trim();
            if (keyword.equals("0")) {
                System.out.println("⏭ Skipping song search...");
                break;
            }

            // MODEL CALL:
            // If location = STORE, we might do: musicStore.searchSong(keyword)
            // If location = LIBRARY, we might do: libraryModel.searchSongInLibrary(keyword)
            // In either case, we expect a List<List<String>> of up to 7 fields
            ArrayList<ArrayList<String>> songResults;
            if (location.equals("STORE")) {
                // Example method name - adapt to your actual code:
                songResults = libraryModel.searchSong(keyword, true);
            } else {
                // Example method name - adapt to your actual code:
                songResults = libraryModel.searchSong(keyword, false);
            }

            if (songResults == null || songResults.isEmpty()) {
                System.out.println("❗ No songs found for '" + keyword + "'.");
            } else {
                // Print them in a table
                printSongSearchResults(songResults, location);
                // Let user pick a song to "play" or "rate"
                songSelectionMenu(songResults, location);
            }
        }
    }

    /**
     * Search for albums by keyword and print each album with its songs.
     * For each matching album, the first line shows the album info with a serial number,
     * followed by its songs (each indented).
     * Finally, prompt the user to either save all albums or select a single album to load.
     */
    private static void searchAlbumsPipeline(String location) {
        System.out.println("\n--- 🎼 Searching for Albums ---");
        System.out.print("🔎 Enter album title or artist keyword (or blank to skip): ");
        String keyword = SCANNER.nextLine().trim();
        if (keyword.isEmpty()) {
            System.out.println("⏭ Skipping album search...");
            return;
        }

        // MODEL CALL:
        // If location = STORE, use musicStore.searchAlbums(keyword)
        // If location = LIBRARY, use libraryModel.searchAlbumsInLibrary(keyword)
        List<List<String>> albumResults;
        if (location.equals("STORE")) {
            albumResults = musicStore.searchAlbums(keyword);
        } else {
            albumResults = libraryModel.searchAlbumsInLibrary(keyword);
        }

        if (albumResults == null || albumResults.isEmpty()) {
            System.out.println("❗ No albums found for '" + keyword + "'.");
            return;
        }

        System.out.println("\nFound " + albumResults.size() + " matching album(s):\n");
        int albumIndex = 1;
        for (List<String> albumInfo : albumResults) {
            // 假设 albumInfo 的第一个字段为专辑标题，其它字段为相关信息
            String albumTitle = albumInfo.get(0);
            // 打印带序号的专辑信息行，格式为 “🎵 Album: [专辑信息]”
            System.out.println(albumIndex + ") 🎵 Album: " + String.join(" | ", albumInfo));

            // 获取该专辑中的歌曲列表
            List<List<String>> albumSongs = musicStore.getSongsInAlbum(albumTitle);
            if (albumSongs == null || albumSongs.isEmpty()) {
                System.out.println("    (No songs found in this album)");
            } else {
                // 对每个歌曲打印一行，并缩进显示
                for (List<String> songInfo : albumSongs) {
                    System.out.println("    " + String.join(" | ", songInfo));
                }
            }
            System.out.println(); // 空行分隔不同专辑
            albumIndex++;
        }

        // 提示用户操作
        System.out.println("Options:");
        System.out.println("1) Save all albums (all albums have been loaded into the library)");
        System.out.println("2) Select a single album to load into the library");
        System.out.println("0) Return");
        System.out.print("👉 Enter your choice: ");
        String choice = SCANNER.nextLine().trim();

        switch (choice) {
            case "1":
                // 假设 libraryModel 有加载所有专辑的方法
                libraryModel.loadAlbums(albumResults);
                System.out.println("All albums have been loaded into the library.");
                break;
            case "2":
                System.out.print("👉 Enter the album number to load: ");
                try {
                    int selectedIndex = Integer.parseInt(SCANNER.nextLine().trim());
                    if (selectedIndex < 1 || selectedIndex > albumResults.size()) {
                        System.out.println("❗ Invalid album number.");
                    } else {
                        List<String> selectedAlbum = albumResults.get(selectedIndex - 1);
                        String selectedAlbumTitle = selectedAlbum.get(0);
                        // 假设 libraryModel 有加载单个专辑的方法
                        libraryModel.loadAlbum(selectedAlbum);
                        System.out.println("The album: " + selectedAlbumTitle + " has been loaded into the library.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❗ Invalid input. Returning to menu.");
                }
                break;
            case "0":
                return;
            default:
                System.out.println("❗ Invalid choice.");
        }
    }

    /**
     * Printing Songs in a Table
     */
    private static void printSongSearchResults(ArrayList<ArrayList<String>> songResults, String location) {
        // Build the header row with a serial number column.
        List<String> header = new ArrayList<>();
        header.add("No.");
        header.add("Title");
        header.add("Artist");
        header.add("Genre");
        header.add("Year");

        boolean isStore = location.equals("STORE");
        if (!isStore) {
            // For user library, also show favorite and rating.
            header.add("Fav?");
            header.add("Rating");
        }

        // Check if we should include the "Album" column.
        boolean anyAlbum = false;
        for (List<String> row : songResults) {
            if (row.size() > 6 && row.get(6) != null && !row.get(6).isBlank()) {
                anyAlbum = true;
                break;
            }
        }
        if (anyAlbum) {
            header.add("Album");
        }

        // Combine into a 2D structure for TablePrinter.
        List<List<String>> tableRows = new ArrayList<>();
        tableRows.add(header);

        int index = 1;
        for (List<String> row : songResults) {
            // row: [title, artist, genre, year, favorite, rating, album]
            List<String> newRow = new ArrayList<>();
            newRow.add(String.valueOf(index++));  // Serial number
            newRow.add(row.get(0)); // Title
            newRow.add(row.get(1)); // Artist
            newRow.add(row.get(2)); // Genre
            newRow.add(row.get(3)); // Year

            if (!isStore) {
                newRow.add(row.get(4)); // Favorite
                newRow.add(row.get(5)); // Rating
            }

            if (anyAlbum) {
                String album = (row.size() > 6) ? row.get(6) : "";
                newRow.add(album == null ? "" : album);
            }

            tableRows.add(newRow);
        }

        TablePrinter.printDynamicTable("Search Results (Songs)", tableRows);
    }

    /**
     * Let the user pick a song row (by index) to play or rate
     */

    /**
     * Let the user pick a song row (by index) to play or rate
     */

    private static void songSelectionMenu(ArrayList<ArrayList<String>> songResults, String location) {
        System.out.println("UserSongs: " + libraryModel.getSongListSize());
        if (location.equals("STORE")) {
            String choice = songSelectionStore();
            if (choice.equals("SKIP")) {
                System.out.println("⏭ Skipping song search...");
                return;
            } else if (choice.equals("ALL")) {
                if (!libraryModel.allSongSelection(songResults.size())) {
                    System.out.println("❗ System wrong. ");
                    return;
                }
                System.out.println("All songs added to library! ");
                return;
            } else {
                handleSongSelection(songResults, location);
                return;
            }
        }
        handleSongSelection(songResults, location);
    }



    private static String songSelectionStore() {

        while (true) {
            System.out.println("UserSongs: " + libraryModel.getSongListSize());
            System.out.println("\nWhich song would you like add to library?");
            System.out.println("1) All songs");
            System.out.println("2) Select single song");
            System.out.println("0) ⏭ Skipping song search...");
            System.out.print("👉 Enter choice: ");
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    return "ALL";
                case "2":
                    return "SINGLE";
                case "0":
                    return "SKIP";
                default:
                    System.out.println("❗ Invalid choice. Please try again.");

            }
        }
    }


    private static void handleSongSelection(ArrayList<ArrayList<String>> songResults, String location) {
        printSongSearchResults(songResults, location);
        while (true) {
            System.out.println("UserSongs: " + libraryModel.getSongListSize());

            if (location.equals("STORE")) {
                System.out.println("\nWhich song would you like to add to library?");
            } else {
                System.out.println("\nWhich song would you like to handle?");
            }
            System.out.println("Select a song, or 0 to skip: ");
            String choice = SCANNER.nextLine().trim();
            if (choice.equals("0")) {
                break;
            }
            try {
                int index = Integer.parseInt(choice);
                if (index < 1 || index > songResults.size()) {
                    System.out.println("❗ Invalid index. Try again.");
                    continue;
                }
                if (location.equals("STORE")) {
                    if (!libraryModel.handleSongSelection(index - 1, songResults.size())) {
                        System.out.println("❗ System wrong. ");
                        break;
                    }
                    System.out.println(String.format("Song %s added to library! ", songResults.get(index - 1).get(0)));
                } else {
                    List<String> selectedRow = songResults.get(index - 1);
                    String songTitle = selectedRow.get(0);
                    handleSongActions(songTitle);
                }
            } catch (NumberFormatException e) {
                System.out.println("❗ Please enter a valid number.");
            }
        }
    }

    /**
     * Let the user pick an album row (by index) to see songs, etc.
     */
    private static void handleAlbumSelection(ArrayList<ArrayList<String>> albumResults, String location) {
        while (true) {
            System.out.println("\nEnter the row number of the album to handle, or 0 to skip: ");
            String choice = SCANNER.nextLine().trim();
            if (choice.equals("0")) {
                break;
            }
            try {
                int index = Integer.parseInt(choice);
                if (index < 1 || index > albumResults.size()) {
                    System.out.println("❗ Invalid index. Try again.");
                    continue;
                }
                List<String> selectedRow = albumResults.get(index - 1);
                String albumTitle = selectedRow.get(0);
                handleAlbumActions(albumTitle);
            } catch (NumberFormatException e) {
                System.out.println("❗ Please enter a valid number.");
            }
        }
    }

    // ================================================
    //              PLAYLIST MENU (single playlist)
    // ================================================
    private static void runPlaylistMenu() {
        while (true) {
            System.out.println("\n---------- 🎧 PLAYLIST MENU ----------");
            System.out.println("1) Clear playlist");
            System.out.println("2) Add a song to playlist");
            System.out.println("3) Remove a song from playlist");
            System.out.println("4) Display playlist (table)");
            System.out.println("5) Display playlist (list)");
            System.out.println("6) ▶️ Play a song from playlist");
            System.out.println("0) 🔙 Back to Main Menu");
            System.out.print("👉 Enter your choice: ");

            String choice = SCANNER.nextLine().trim();
            switch (choice) {
                case "1":
                    libraryModel.clearPlaylist();
                    break;
                case "2":
                    System.out.print("Enter the song title to add: ");
                    String addTitle = SCANNER.nextLine().trim();
                    libraryModel.addSongToPlaylist(addTitle);
                    break;
                case "3":
                    System.out.print("Enter the song title to remove: ");
                    String removeTitle = SCANNER.nextLine().trim();
                    libraryModel.removeSongFromPlaylist(removeTitle);
                    break;
                case "4":
                    libraryModel.printPlaylist();
                    break;
                case "5":
                    System.out.println(libraryModel.getPlaylistSongs().toString());
                    break;
                case "6":
                    playSongInPlaylist();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("❗ Invalid choice. Try again.");
            }
        }
    }

    private static void clearPlaylist() {
        libraryModel.clearPlaylist();
        System.out.println("🗑️ Cleared all songs.");
    }

    private static void addSongToPlaylist() {
        System.out.print("🎶 Enter the song title (or ID) to add: ");
        String songTitle = SCANNER.nextLine().trim();
        // Possibly search or fetch the Song object from your model
        // libraryModel.addSongToPlaylist(plName, songTitle);
        System.out.println("🎶 Added '" + songTitle + "' to playlist.");
    }

    private static void removeSongFromPlaylist() {
        System.out.print("🎶 Enter the song title (or ID) to remove: ");
        String songTitle = SCANNER.nextLine().trim();
        // libraryModel.removeSongFromPlaylist(plName, songTitle);
        System.out.println("❌ Removed '" + songTitle + "' from playlist.");
    }

    private static void playSongInPlaylist() {
        ArrayList<ArrayList<String>> songs = libraryModel.getPlaylistSongs();
        if (songs == null || songs.isEmpty()) {
            System.out.println("❗ No songs in the playlist.");
            return;
        }
        printSongSearchResults(songs, "LIBRARY");
        handleSongSelection(songs, "LIBRARY");
    }


    private static void rateSongInPlaylist() {
        ArrayList<ArrayList<String>> songs = libraryModel.getPlaylistSongs();
        if (songs == null || songs.isEmpty()) {
            System.out.println("❗ No songs in playlist.");
            return;
        }

        printSongSearchResults(songs, "LIBRARY");
        handleSongSelection(songs, "LIBRARY");
    }

    // ================================================
    //              FAVORITE LIST MENU (single favorite list)
    // ================================================
    private static void runFavoriteMenu() {
        while (true) {
            System.out.println("\n---------- ⭐ FAVORITE LIST MENU ⭐ ----------");
            System.out.println("1) Add a song to favorite list");
            System.out.println("2) Remove a song from favorite list");
            System.out.println("3) Display favorite list (as table)");
            System.out.println("4) Display favorite list (as list)");
            System.out.println("0) 🔙 Back to Main Menu");
            System.out.print("👉 Enter your choice: ");

            String choice = SCANNER.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.print("Enter the song title to add to favorites: ");
                    String addFav = SCANNER.nextLine().trim();
                    libraryModel.addSongToFavorites(addFav);
                    break;
                case "2":
                    System.out.print("Enter the song title to remove from favorites: ");
                    String removeFav = SCANNER.nextLine().trim();
                    libraryModel.removeSongFromFavorites(removeFav);
                    break;
                case "3":
                    libraryModel.getFavoriteList().printAsTable();
                    break;
                case "4":
                    System.out.println(libraryModel.getFavoriteList().toString());
                    break;
                case "0":
                    return;
                default:
                    System.out.println("❗ Invalid choice. Try again.");
            }
        }
    }

    // -------------------------------------------------------------------------
    //                  SONG / ALBUM ACTIONS
    // -------------------------------------------------------------------------
    private static void handleSongActions(String songTitle) {
        System.out.println("\n🎶 Selected Song: " + songTitle);

        boolean done = false;
        while (!done) {
            System.out.println("Actions: ");
            System.out.println("1) ▶️ Play song");
            System.out.println("2) ⭐ Rate song");
            System.out.println("3) ❤️ Add to favorite");
            System.out.println("0) 🔙 Go back");
            System.out.print("👉 Enter your choice: ");
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("🚧 Play feature is under construction.");
                    // libraryModel.playSong(songTitle);
                    break;
                case "2":
                    System.out.print("✏️ Enter your rating (1 to 5): ");
                    /**
                    System.out.print("✏️ Enter your rating (1 to 5): ");
                    try {
                        int rating = Integer.parseInt(SCANNER.nextLine().trim());
                        if (rating >= 1 && rating <= 5) {
                            libraryModel.rateSong(songTitle, rating);
                        } else {
                            System.out.println("❗ Rating must be between 1 and 5.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❗ Invalid rating input.");
                    }
                    break;
                     */
                case "0":
                    done = true;
                    break;
                default:
                    System.out.println("❗ Invalid choice. Try again.");
            }
        }
    }

    private static void handleAlbumActions(String albumTitle) {
        System.out.println("\n🎵 Selected Album: " + albumTitle);

        // Possibly fetch the songs from the album in your library or store
        // e.g. List<List<String>> albumSongs = musicStore.getSongsInAlbum(albumTitle);
        List<List<String>> albumSongs = musicStore.getSongsInAlbum(albumTitle);
        if (albumSongs == null || albumSongs.isEmpty()) {
            System.out.println("❗ No songs found in this album.");
            return;
        }

        // Print them
        printSongSearchResults(albumSongs, "STORE");

        // Let user pick a song
        handleSongSelection(albumSongs, "STORE");
    }
}
