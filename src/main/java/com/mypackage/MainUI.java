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
            // searchAlbumsPipeline(location);
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
                songResults = libraryModel.searchAlbum(keyword, false);
            }

            if (songResults == null || songResults.isEmpty()) {
                System.out.println("❗ No songs found for '" + keyword + "'.");
            } else {
                // Print them in a table
                printSongSearchResults(songResults, location);
                // Let user pick a song to "play" or "rate"
                // handleSongSelection(songResults, location);
            }
        }
    }
/**
    private static void searchAlbumsPipeline(String location) {
        System.out.println("\n--- 🎼 Searching for Albums ---");
        System.out.print("🔎 Enter album title or artist keyword (or blank to skip): ");
        String keyword = SCANNER.nextLine().trim();
        if (keyword.isEmpty()) {
            System.out.println("⏭ Skipping album search...");
            return;
        }

        // MODEL CALL:
        // If location = STORE, maybe: musicStore.searchAlbums(keyword)
        // If location = LIBRARY, maybe: libraryModel.searchAlbumsInLibrary(keyword)
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

        printAlbumSearchResults(albumResults, location);
        handleAlbumSelection(albumResults, location);
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
     * Printing Albums in a Table
     */
/**
    private static void printAlbumSearchResults(ArrayList<ArrayList<String>> albumResults, String location) {
        // We'll assume the first 4 fields are [title, artist, genre, year],
        // plus field 6 if not null.
        List<String> header = new ArrayList<>();
        header.add("💿 Title");
        header.add("👤 Artist");
        header.add("🎼 Genre");
        header.add("📅 Year");

        boolean anyAlbum = false;
        for (List<String> row : albumResults) {
            if (row.size() > 6 && row.get(6) != null && !row.get(6).isBlank()) {
                anyAlbum = true;
                break;
            }
        }
        if (anyAlbum) {
            header.add("💿 Album?");
        }

        List<List<String>> tableRows = new ArrayList<>();
        tableRows.add(header);

        for (List<String> row : albumResults) {
            List<String> newRow = new ArrayList<>();
            newRow.add(row.get(0)); // title
            newRow.add(row.get(1)); // artist
            newRow.add(row.get(2)); // genre
            newRow.add(row.get(3)); // year

            if (anyAlbum) {
                String alb = (row.size() > 6) ? row.get(6) : "";
                newRow.add((alb == null) ? "" : alb);
            }

            tableRows.add(newRow);
        }

        TablePrinter.printDynamicTable("Search Results (Albums)", tableRows);
    }

    /**
     * Let the user pick a song row (by index) to play or rate
     */
/**
    private static void handleSongSelection(ArrayList<ArrayList<String>> songResults, String location) {
        while (true) {
            System.out.println("\nEnter the row number of the song to handle, or 0 to skip: ");
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
                List<String> selectedRow = songResults.get(index - 1);
                String songTitle = selectedRow.get(0);
                handleSongActions(songTitle);
            } catch (NumberFormatException e) {
                System.out.println("❗ Please enter a valid number.");
            }
        }
    }

    /**
     * Let the user pick an album row (by index) to see songs, etc.
     */
/**
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

    // -------------------------------------------------------------------------
    //                  PLAYLIST MENU (EXAMPLE)
    // -------------------------------------------------------------------------
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
                    libraryModel.createPlaylist(newPlaylist);
                    System.out.println("🎵 Playlist '" + newPlaylist + "' created.");
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
                    return;
                default:
                    System.out.println("❗ Invalid choice. Try again.");
            }
        }
    }

    private static void clearPlaylist() {
        System.out.print("🗑️ Enter the playlist name to clear: ");
        String plName = SCANNER.nextLine().trim();
        libraryModel.clearPlaylist(plName);
        System.out.println("🗑️ Cleared all songs from '" + plName + "'.");
    }

    private static void addSongToPlaylist() {
        System.out.print("➕ Enter the playlist name to add songs: ");
        String plName = SCANNER.nextLine().trim();
        System.out.print("🎶 Enter the song title (or ID) to add: ");
        String songTitle = SCANNER.nextLine().trim();
        // Possibly search or fetch the Song object from your model
        // libraryModel.addSongToPlaylist(plName, songTitle);
        System.out.println("🎶 Added '" + songTitle + "' to playlist '" + plName + "'.");
    }

    private static void removeSongFromPlaylist() {
        System.out.print("❌ Enter the playlist name to remove songs: ");
        String plName = SCANNER.nextLine().trim();
        System.out.print("🎶 Enter the song title (or ID) to remove: ");
        String songTitle = SCANNER.nextLine().trim();
        // libraryModel.removeSongFromPlaylist(plName, songTitle);
        System.out.println("❌ Removed '" + songTitle + "' from playlist '" + plName + "'.");
    }

    private static void playSongInPlaylist() {
        System.out.print("▶️ Enter the playlist name to play songs: ");
        String plName = SCANNER.nextLine().trim();

        // Example: your libraryModel might have a method that returns a List<List<String>>:
        List<List<String>> songs = libraryModel.getPlaylistSongs(plName);
        if (songs == null || songs.isEmpty()) {
            System.out.println("❗ No songs in playlist '" + plName + "'.");
            return;
        }

        // Print them
        printSongSearchResults(songs, "LIBRARY");

        // Let the user pick one to play
        handleSongSelection(songs, "LIBRARY");
    }

    private static void rateSongInPlaylist() {
        System.out.print("✏️ Enter the playlist name to rate a song: ");
        String plName = SCANNER.nextLine().trim();

        List<List<String>> songs = libraryModel.getPlaylistSongs(plName);
        if (songs == null || songs.isEmpty()) {
            System.out.println("❗ No songs in playlist '" + plName + "'.");
            return;
        }

        printSongSearchResults(songs, "LIBRARY");
        handleSongSelection(songs, "LIBRARY");
    }

    // -------------------------------------------------------------------------
    //                  FAVORITE LIST MENU
    // -------------------------------------------------------------------------
    private static void runFavoriteMenu() {
        System.out.println("\n---------- ⭐ FAVORITE LIST ⭐ ----------");
        List<List<String>> favorites = libraryModel.getFavoriteSongs();
        if (favorites == null || favorites.isEmpty()) {
            System.out.println("❗ No favorite songs yet!");
            return;
        }

        // Print them in a table (like user library)
        printSongSearchResults(favorites, "LIBRARY");

        // Let user pick one
        handleSongSelection(favorites, "LIBRARY");
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
            System.out.println("0) 🔙 Go back");
            System.out.print("👉 Enter your choice: ");
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    libraryModel.playSong(songTitle);
                    break;
                case "2":
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
*/
    // -------------------------------------------------------------------------
    //                     TABLE PRINTER (STATIC NESTED CLASS)
    // -------------------------------------------------------------------------
    private static class TablePrinter {

        public static void printDynamicTable(String tableTitle, List<List<String>> rows) {
            if (rows == null || rows.isEmpty()) {
                System.out.println("No data to display.");
                return;
            }

            // Print a fancy title
            System.out.println("===================================================");
            System.out.println("           🎉 " + tableTitle + " 🎉              ");
            System.out.println("===================================================");

            // Number of columns
            int colCount = rows.get(0).size();

            // Compute max width of each column
            int[] colWidths = new int[colCount];
            for (List<String> row : rows) {
                for (int c = 0; c < colCount; c++) {
                    String cell = (row.get(c) == null) ? "" : row.get(c);
                    colWidths[c] = Math.max(colWidths[c], cell.length());
                }
            }

            // Build separator line
            String separator = buildSeparatorLine(colWidths);

            // Print header row (first row), then separator
            System.out.println(separator);
            printRow(rows.get(0), colWidths);
            System.out.println(separator);

            // Print data rows
            for (int r = 1; r < rows.size(); r++) {
                printRow(rows.get(r), colWidths);
            }

            // Bottom line
            System.out.println(separator);
        }

        private static String buildSeparatorLine(int[] colWidths) {
            StringBuilder sb = new StringBuilder();
            for (int width : colWidths) {
                sb.append("+-");
                for (int i = 0; i < width; i++) {
                    sb.append("-");
                }
                sb.append("-");
            }
            sb.append("+");
            return sb.toString();
        }

        private static void printRow(List<String> row, int[] colWidths) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < colWidths.length; i++) {
                String cell = (row.get(i) == null) ? "" : row.get(i);
                sb.append("| ").append(padRight(cell, colWidths[i])).append(" ");
            }
            sb.append("|");
            System.out.println(sb.toString());
        }

        private static String padRight(String text, int width) {
            if (text.length() >= width) {
                return text;
            }
            StringBuilder sb = new StringBuilder(text);
            while (sb.length() < width) {
                sb.append(" ");
            }
            return sb.toString();
        }
    }

}