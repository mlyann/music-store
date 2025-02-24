package la1; // Or wherever your package is

import java.util.ArrayList;
import java.util.Arrays;
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
                return;
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

    // -------------------------------------------------------------------------
    //                  MAIN MENU FUNCTIONS
    // -------------------------------------------------------------------------
    private static void runMainMenu() {
        while (true) {
            System.out.println("\n---------- 🎵 MAIN MENU 🎵 ----------");
            System.out.println("1) 🔍 Search");
            System.out.println("2) 🎧 Playlist");
            System.out.println("3) ❤️ Favorite List");
            System.out.println("4) 🏠 Library Lists");
            System.out.println("5) ➕ Load Songs single");
            System.out.println("0) 🚪 Quit the application");
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
                case "4":
                    libraryListsMenu();
                    break;
                case "222":
                    System.out.println("🚧 Playlist feature is under construction.");
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

    private static void libraryListsMenu() {
        while (true) {
            System.out.println("\n---------- 🏠 Library Lists 🏠 ----------");
            System.out.println("1) 🎤 Show all songs");
            System.out.println("2) 🎼 Show all albums");
            System.out.println("3) 🎸 Show all artists");
            System.out.println("0) 🔙 Back to Main Menu");
            System.out.print("👉 Enter choice: ");
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    printSongSearchResults("Library Songs", libraryModel.getSongList(), "LIBRARY");
                    break;
                case "2":
                    printAlbumSearchResults(libraryModel.getAlbumList());
                    break;
                case "3":
                    libraryModel.printAllArtists();
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
            System.out.println("\n---------- 🔍 Search MENU 🔍 ----------");
            String location = chooseSearchLocation();
            if (location.equals("BACK")) {
                return;
            }

            System.out.println("\nWhat would you like to search?");
            System.out.println("1) 🎤 Search for songs");
            System.out.println("2) 🎼 Search for albums");
            System.out.println("0) 🔙 Back to Search Menu");
            System.out.println("h) 🚪 Back to Main Menu");
            System.out.print("👉 Enter choice: ");
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    searchSongsPipeline(location);
                    break;
                case "2":
                    searchAlbumsPipeline(location);
                    break;
                case "0":
                    // 使用 continue 让循环继续，而不是直接退出 runSearchMenu
                    continue;
                case "h":
                    System.out.println("🚪 Back to Main Menu");
                    runMainMenu();
                    return;
                default:
                    System.out.println("❗ Invalid choice. Please try again.");
            }
        }
    }


    // -------------------------------------------------------------------------
    //                  SEARCH MENU FUNCTIONS
    // -------------------------------------------------------------------------

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


    // -------------------------------------------------------------------------
    //                  SONG FUNCTIONS
    // -------------------------------------------------------------------------

    /**
     * Search for songs in either the Music Store or the User Library.
     * We expect a List<List<String>> from your model.
     */
    private static void searchSongsPipeline(String location) {
        while (true) {
            System.out.println("\n--- 🎤 Searching for Songs ---");
            System.out.println("🔎 Enter song title or artist keyword  ");
            System.out.println("0) 🔙 Back to search menu: ");
            System.out.println("h) 🚪 Back to Main Menu");
            System.out.print("👉 Enter choice: ");
            String keyword = SCANNER.nextLine().trim();
            if (keyword.equals("0")) {
                System.out.println("🔙 Back to Search Menu");
                break;
            } else if (keyword.equals("h")) {
                System.out.println("🚪 Back to Main Menu");
                runMainMenu();
                return;
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
                System.out.println("\nFound " + songResults.size() + " matching song(s):\n");
                // Print them in a table
                printSongSearchResults("Search Results (Songs)", songResults, location);
                // Let user pick a song to "play" or "rate"
                songSelectionMenu(songResults, location);
            }
        }
    }

    /**
     * Printing Songs in a Table
     */
    private static void printSongSearchResults(String tableTitle,ArrayList<ArrayList<String>> songResults,
                                               String location) {
        if (songResults == null || songResults.isEmpty()) {
            System.out.println("❗ No Songs in Library.");
            return;
        }
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
            header.add("Favorite");
            header.add("Rating  ");
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

        TablePrinter.printDynamicTable(tableTitle, tableRows);
    }

    /**
     * Let the user pick a song row (by index) to play or rate
     */
    private static void songSelectionMenu(ArrayList<ArrayList<String>> songResults, String location) {
        if (location.equals("STORE")) {
            String choice = songSelectionStore();
            switch (choice) {
                case "SKIP" -> {
                    System.out.println("🔙 Back to song search");
                    return;
                }
                case "HOME" -> {
                    System.out.println("🚪 Back to Main Menu");
                    runMainMenu();
                    return;
                }
                case "ALL" -> {
                    if (!libraryModel.allSongSelection(songResults.size())) {
                        System.out.println("❗ System wrong. ");
                        return;
                    }
                    System.out.println("All songs added to library! ");
                    return;
                }
                default -> {
                    handleSongSelection(songResults, location, "Search Results (Songs)");
                    return;
                }
            }
        }
        handleSongSelection(songResults, location, "Search Results (Songs)");
    }



    private static String songSelectionStore() {
        while (true) {
            System.out.println("\nWhich song would you like add to library?");
            System.out.println("1) All songs");
            System.out.println("2) Select single song");
            System.out.println("0) 🔙 Back to song search");
            System.out.println("h) 🚪 Back to Main Menu");
            System.out.print("👉 Enter choice: ");
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    return "ALL";
                case "2":
                    return "SINGLE";
                case "0":
                    return "SKIP";
                case "h":
                    return "HOME";
                default:
                    System.out.println("❗ Invalid choice. Please try again.");

            }
        }
    }

    private static void handleSongSelection(ArrayList<ArrayList<String>> songResults, String location, String title) {
        while (true) {
            System.out.println("UserSongs: " + libraryModel.getSongListSize());

            if (location.equals("STORE")) {
                System.out.println("\nWhich song would you like to add to library?");
            } else {
                System.out.println("\nWhich song would you like to handle?");
            }
            System.out.println("Select the number of the song, or 0 to skip: ");
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
                String songTitle = songResults.get(index - 1).get(0);
                if (location.equals("STORE")) {
                    if (!libraryModel.handleSongSelection(index - 1, songResults.size())) {
                        System.out.println("❗ System wrong. ");
                        break;
                    }
                    System.out.println(String.format("Song [%s] added to library! ", songTitle));
                } else {
                    if (!libraryModel.setCurrentSong(index - 1, songTitle)){
                        System.out.println("❗ System wrong. ");
                        break;
                    }
                    handleSongActions(songTitle);
                }
            } catch (NumberFormatException e) {
                System.out.println("❗ Please enter a valid number.");
            }
        }
    }

    private static void handleSongActions(String songTitle) {
        while (true) {
            if (!libraryModel.checkCurrentSong(songTitle)) {
                System.out.println("❗ System wrong. ");
                return;
            }
            System.out.println("\n🎶 Selected Song: [" + libraryModel.getCurrentSongInfo() + "]");
            boolean isFavourite = libraryModel.isFavourite();
            System.out.println("Actions: ");
            System.out.println("1) ▶️ Play song");
            System.out.println("2) ⭐ Rate song");
            if (isFavourite) {
                System.out.println("3) ❤️ Remove from favorite");
            } else {
                System.out.println("3) ❤️ Add to favorite");
            }
            System.out.println("0) 🔙 Go back");
            System.out.println("h) 🚪 Back to Main Menu");
            System.out.print("👉 Enter choice: ");
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    libraryModel.playSong();
                    break;
                case "2":
                    rateSong();
                    break;
                case "3":
                    if (isFavourite) {
                        libraryModel.removeFavourite();
                        System.out.printf("Song [%s] removed from favorite.%n", songTitle);
                    } else {
                        libraryModel.addFavourite();
                        System.out.printf("Song [%s] added to favorite.%n", songTitle);
                    }
                    break;
                case "0":
                    return;
                case "h":
                    runMainMenu();
                    return;
                default:
                    System.out.println("❗ Invalid choice. Try again.");
                    break;
            }
        }
    }

    // -------------------------------------------------------------------------
    //                  ALBUM FUNCTIONS
    // -------------------------------------------------------------------------

    /**
     * Search for albums by keyword and print each album with its songs.
     * For each matching album, the first line shows the album info with a serial number,
     * followed by its songs (each indented).
     * Finally, prompt the user to either save all albums or select a single album to load.
     */
    private static void searchAlbumsPipeline(String location) {
        while (true) {
            System.out.println("\n--- 🎼 Searching for Albums ---");
            System.out.println("🔎 Enter Album Title or Artist Keyword");
            System.out.println("0) 🔙 Back to Search Menu: ");
            System.out.println("h) 🚪 Back to Main Menu");
            System.out.print("👉 Enter choice: ");
            String keyword = SCANNER.nextLine().trim();
            if (keyword.equals("0")) {
                System.out.println("🔙 Back to Search Menu");
                break;
            } else if (keyword.equals("h")) {
                System.out.println("🚪 Back to Main Menu");
                runMainMenu();
                return;
            }


            // MODEL CALL:
            // If location = STORE, use musicStore.searchAlbums(keyword)
            // If location = LIBRARY, use libraryModel.searchAlbumsInLibrary(keyword)
            ArrayList<ArrayList<String>> albumResults;
            if (location.equals("STORE")) {
                albumResults = libraryModel.searchAlbum(keyword, true);
            } else {
                albumResults = libraryModel.searchAlbum(keyword, false);
            }

            if (albumResults == null || albumResults.isEmpty()) {
                System.out.println("❗ No albums found for '" + keyword + "'.");
            } else {
                System.out.println("\nFound " + albumResults.size() + " matching album(s):\n");
                printAlbumSearchResults(albumResults);
                albumSelectionMenu(albumResults, location);
            }
        }
    }

    public static void printAlbumSearchResults(ArrayList<ArrayList<String>> albumList) {
        if (albumList == null || albumList.isEmpty()) {
            System.out.println("❗ No Albums in Library.");
            return;
        }

        // 构建表格行，第一行为表头
        List<List<String>> tableRows = new ArrayList<>();
        tableRows.add(Arrays.asList("No.", "Title", "Artist", "Genre", "Year", "Album"));

        int albumNo = 1;
        for (ArrayList<String> albumStrList : albumList) {
            // 数据格式必须至少包含4个元素
            if (albumStrList.size() < 4) {
                continue;
            }

            // 分别取出 title、artist、year、genre
            String title = albumStrList.get(0);
            String artist = albumStrList.get(1);
            String year = albumStrList.get(2);
            String genre = albumStrList.get(3);
            // 剩下的为歌曲列表
            List<String> tracks = albumStrList.subList(4, albumStrList.size());

            // 第一行：显示专辑信息及第一首歌曲（如果存在）
            List<String> firstRow = new ArrayList<>();
            firstRow.add(String.valueOf(albumNo));
            firstRow.add(title);
            firstRow.add(artist);
            // 表头要求 Genre 在 Year 前面
            firstRow.add(genre);
            firstRow.add(year);
            firstRow.add(tracks.isEmpty() ? "" : tracks.get(0));
            tableRows.add(firstRow);

            // 如果有多首歌曲，后续行仅在 Album 列显示其它歌曲
            for (int i = 1; i < tracks.size(); i++) {
                tableRows.add(Arrays.asList("", "", "", "", "", tracks.get(i)));
            }

            // 插入 marker 行，用于在当前专辑与下一个专辑间分隔
            tableRows.add(Arrays.asList("###SEPARATOR###"));

            albumNo++;
        }

        // 移除最后一个 marker 行，避免在最后打印额外的分隔线
        if (!tableRows.isEmpty()) {
            List<String> lastRow = tableRows.get(tableRows.size() - 1);
            if (lastRow.size() == 1 && "###SEPARATOR###".equals(lastRow.get(0))) {
                tableRows.remove(tableRows.size() - 1);
            }
        }

        // 调用 TablePrinter 打印整张表格
        TablePrinter.printDynamicTable("Album Search Results", tableRows);
    }

    public static void albumSelectionMenu(ArrayList<ArrayList<String>> albumResults, String location) {
        if (location.equals("STORE")) {
            String choice = albumSelectionStore();
            switch (choice) {
                case "SKIP" -> {
                    System.out.println("🔙 Back to album search");
                    return;
                }
                case "HOME" -> {
                    System.out.println("🚪 Back to Main Menu");
                    runMainMenu();
                    return;
                }
                case "ALL" -> {
                    if (!libraryModel.allAlbumSelection(albumResults.size())) {
                        System.out.println("❗ System wrong. ");
                        return;
                    }
                    System.out.println("All albums added to library! ");
                    return;
                }
                default -> {
                    handleAlbumSelection(albumResults, location);
                    return;
                }
            }
        }
        handleAlbumSelection(albumResults, location);
    }


    private static String albumSelectionStore() {
        while (true) {
            System.out.println("\nWhich album would you like to add to library?");
            System.out.println("1) All albums");
            System.out.println("2) Select single album");
            System.out.println("0) 🔙 Back to album search");
            System.out.println("h) 🚪 Back to Main Menu");
            System.out.print("👉 Enter choice: ");
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    return "ALL";
                case "2":
                    return "SINGLE";
                case "0":
                    return "SKIP";
                case "h":
                    return "HOME";
                default:
                    System.out.println("❗ Invalid choice. Please try again.");
            }
        }
    }


    /**
     * Let the user pick an album row (by index) to see songs, etc.
     */
    private static void handleAlbumSelection(ArrayList<ArrayList<String>> albumResults, String location) {
        while (true) {
            System.out.println("UserAlbums: " + libraryModel.getAlbumListSize());
            if (location.equals("STORE")) {
                System.out.println("\nWhich album would you like to add to library?");
            } else {
                System.out.println("\nWhich album would you like to handle?");
            }
            System.out.println("Select the number of the album, or 0 to skip: ");
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
                String albumTitle = albumResults.get(index - 1).get(0);
                if (location.equals("STORE")) {
                    if (!libraryModel.handleAlbumSelection(index - 1, albumResults.size())) {
                        System.out.println("❗ System wrong. ");
                        break;
                    }
                    System.out.println(String.format("Album [%s] added to library! ", albumTitle));
                } else {
                    if (!libraryModel.setCurrentAlbum(index - 1, albumTitle)) {
                        System.out.println("❗ System wrong. ");
                        break;
                    }
                    handleAlbumActions(albumTitle);
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("❗ Please enter a valid number.");
            }
        }
    }



    private static void handleAlbumActions(String albumTitle) {
        while (true) {
            if (!libraryModel.checkCurrentAlbum(albumTitle)) {
                System.out.println("❗ System wrong. ");
                return;
            }
            System.out.println("\n🎼 Selected Album: [" + libraryModel.getCurrentAlbumInfo() + "]");
            System.out.println("Actions: ");
            System.out.println("1) ▶️ Play album");
            System.out.println("2) \uD83D\uDCC2 Open album");
            System.out.println("0) 🔙 Go back");
            System.out.println("h) 🚪 Back to Main Menu");
            System.out.print("👉 Enter choice: ");
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    if (!libraryModel.playAlbum(albumTitle)) {
                        System.out.println("❗ System wrong. ");
                    }
                    return;
                case "2":
                    openAlbum(albumTitle);
                    continue;
                case "0":
                    return;
                case "h":
                    runMainMenu();
                    return;
            }
        }

    }

    public static void openAlbum(String albumTitle) {
        if (!libraryModel.openAlbum(albumTitle)) {
            System.out.println("❗ System wrong. ");
        }
        ArrayList<ArrayList<String>> albumSongs = libraryModel.SongToString();
        ArrayList<String> albumInfo = albumSongs.remove(0);
        if (albumInfo == null || albumInfo.size() < 4) {
            System.out.println("❗ System wrong. ");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(albumInfo.get(0));
        sb.append(" by ");
        sb.append(albumInfo.get(1));
        sb.append(" (");
        sb.append(albumInfo.get(2));
        sb.append(", ");
        sb.append(albumInfo.get(3));
        sb.append(")\n");
        handleSongSelection(albumSongs, "LIBRARY", sb.toString());

    }

    // -------------------------------------------------------------------------
    //                  PLAYLIST MENU (EXAMPLE)
    // -------------------------------------------------------------------------
    private static void runPlaylistMenu() {
        while (true) {
            System.out.println("\n---------- ▶️ PLAYLIST MENU ▶️ ----------");
            System.out.println("1) 📝 Show current playlist");
            System.out.println("2) 🗑️ Clear playlist");
            System.out.println("3) ➕ Add songs to a playlist");
            System.out.println("4) ❌ Remove songs from a playlist");
            System.out.println("5) ▶️ Play songs in playlist");
            System.out.println("6) ⏩ Play next song in playlist");
            System.out.println("7) ⭐ Rate a song in playlist");
            System.out.println("8) ❤️ Favorite a song in playlist");
            System.out.println("9) 🔄 Shuffle playlist");
            System.out.println("0) 🔙 Back to Main Menu");
            System.out.print("👉 Enter choice: ");

            String choice = SCANNER.nextLine().trim();
            switch (choice) {
                case "1":
                    libraryModel.printPlaylist();
                    break;
                case "2":
                    clearPlaylist();
                    break;
                case "3":
                    addSongToPlaylist();
                    break;
                case "4":
                    removeSongFromPlaylist();
                    libraryModel.printPlaylist();
                    break;
                case "5":
                    playSongInPlaylist();
                    libraryModel.printPlaylist();
                    break;
                case "6":
                    libraryModel.playNextSong();
                    libraryModel.printPlaylist();
                    break;
                case "7":
                    rateSongInPlaylist();
                    libraryModel.printPlaylist();
                    break;
                case "8":
                    favoriteSongInPlaylist();
                    libraryModel.printPlaylist();
                    break;
                case "9":
                    libraryModel.shufflePlaylist();
                    libraryModel.printPlaylist();
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
        System.out.println("🗑️ Cleared all songs in the playlist.");
    }

    private static void addSongToPlaylist() {
        // Print the user songs with numbers
        libraryModel.userSongSerch();   // set the search list to user songs
        libraryModel.printUserSongsTable();
        int size = libraryModel.getSearchSongListSize();
        System.out.print("🎶 Enter the song number to add: ");
        String input = SCANNER.nextLine().trim();
        try {
            int songNumber = Integer.parseInt(input);
            // Get the library songs list to show which title is being added.
            if (songNumber < 1 || songNumber > size) {
                System.out.println("Invalid selection. Please enter a valid number.");
                return;
            }
            libraryModel.setCurrentSongWithoutCheck(songNumber - 1);
            libraryModel.addSongToPlaylist();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    private static void removeSongFromPlaylist() {
        // Print the current playlist with numbers
        libraryModel.playListSearch();  // set the search list to playlist
        libraryModel.printPlaylist();
        int size = libraryModel.getSearchSongListSize();
        System.out.print("🎶 Enter the song number to remove: ");
        String input = SCANNER.nextLine().trim();
        try {
            int songNumber = Integer.parseInt(input);
            // Get the current playlist songs for lookup
            ArrayList<Song> playlistSongs = libraryModel.getPlaylist();
            if (songNumber < 1 || songNumber > size) {
                System.out.println("Invalid selection. Please enter a valid number.");
                return;
            }
            libraryModel.setCurrentSongWithoutCheck(songNumber - 1);
            libraryModel.removeSongFromPlaylist();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    private static void playSongInPlaylist() {
        // Example: your libraryModel might have a method that returns a List<List<String>>:
        if (libraryModel.getPlayerSize() == 0 ) {
            System.out.println("❗ No songs in playlist.");
        }else{
            libraryModel.getPlaying();
        }
    }

    private static void rateSongInPlaylist() {
        // Print the current playlist with numbers
        libraryModel.playListSearch();  // set the search list to playlist
        libraryModel.printPlaylist();
        int size = libraryModel.getSearchSongListSize();
        System.out.print("❤️ Enter the Song Number to Rate: ");
        String input = SCANNER.nextLine().trim();
        try {
            int songNumber = Integer.parseInt(input);
            // Get the current playlist songs for lookup
            ArrayList<Song> playlistSongs = libraryModel.getPlaylist();
            if (songNumber < 1 || songNumber > size) {
                System.out.println("Invalid selection. Please enter a valid number.");
                return;
            }
            libraryModel.setCurrentSongWithoutCheck(songNumber - 1);
            rateSong();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    private static void favoriteSongInPlaylist() {
        libraryModel.playListSearch();  // set the search list to playlist
        libraryModel.printPlaylist();
        int size = libraryModel.getSearchSongListSize();
        System.out.print("⭐ Enter the Song Number to Favourite: ");
        String input = SCANNER.nextLine().trim();
        try {
            int songNumber = Integer.parseInt(input);
            // Get the current playlist songs for lookup
            ArrayList<Song> playlistSongs = libraryModel.getPlaylist();
            if (songNumber < 1 || songNumber > size) {
                System.out.println("Invalid selection. Please enter a valid number.");
                return;
            }
            libraryModel.setCurrentSongWithoutCheck(songNumber - 1);
            boolean isFavourite = libraryModel.isFavourite();
            if (isFavourite) {
                System.out.println("❤️ Remove from favorite?");
            } else {
                System.out.println("❤️ Add to favorite?");
            }
            System.out.println("1) Yes");
            System.out.println("2) No");
            System.out.print("👉 Enter choice: ");
            String choice = SCANNER.nextLine().trim();
            if (choice.equals("1")) {
                if (isFavourite) {
                    libraryModel.removeFavourite();
                    System.out.printf("Song [%s] removed from favorite.%n", libraryModel.getCurrentSongTitle());
                } else {
                    libraryModel.addFavourite();
                    System.out.printf("Song [%s] added to favorite.%n", libraryModel.getCurrentSongTitle());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }

    }

    // -------------------------------------------------------------------------
    //                  FAVORITE LIST MENU
    // -------------------------------------------------------------------------

    private static void runFavoriteMenu() {
        while (true) {
            System.out.println("\n---------- ❤️ FAVORITE LIST ❤️ ----------");
            System.out.println("1) 📝 Show favourite list");
            System.out.println("2) 🗑️ Clear favourite list");
            System.out.println("3) ➕ Add songs to a favourite list");
            System.out.println("4) ❌ Remove songs from a favourite list");
            System.out.println("5) ▶️ Play songs in a favourite list");
            System.out.println("0) 🔙 Back to Main Menu");
            System.out.print("👉 Enter choice: ");

            String choice = SCANNER.nextLine().trim();
            switch (choice) {
                case "1":
                    libraryModel.printFavoriteList();
                    break;
                case "2":
                    libraryModel.clearFavoriteList();
                    System.out.println("🗑️ Cleared all songs in the favourite list.");
                    break;
                case "3":
                    addSongToFavourite();
                    break;
                case "4":
                    removeSongFromFavourite();
                    libraryModel.printFavoriteList();
                    break;
                case "5":
                    playSongInFavourite();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("❗ Invalid choice. Try again.");
            }
        }
    }

    private static void addSongToFavourite() {
        // Print the user songs with numbers
        libraryModel.userSongSerch();   // set the search list to user songs
        libraryModel.printUserSongsTable();
        int size = libraryModel.getSearchSongListSize();
        System.out.print("🎶 Enter the song number to add: ");
        String input = SCANNER.nextLine().trim();
        try {
            int songNumber = Integer.parseInt(input);
            // Get the library songs list to show which title is being added.
            if (songNumber < 1 || songNumber > size) {
                System.out.println("Invalid selection. Please enter a valid number.");
                return;
            }
            libraryModel.setCurrentSongWithoutCheck(songNumber - 1);
            libraryModel.addFavourite();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    private static void removeSongFromFavourite() {
        // Print the current playlist with numbers
        libraryModel.favoriteListSearch();  // set the search list to favorite list
        libraryModel.getFavoriteListSize();
        int size = libraryModel.getSearchSongListSize();
        System.out.print("🎶 Enter the song number to remove: ");
        String input = SCANNER.nextLine().trim();
        try {
            int songNumber = Integer.parseInt(input);
            // Get the library songs list to show which title is being added.
            if (songNumber < 1 || songNumber > size) {
                System.out.println("Invalid selection. Please enter a valid number.");
                return;
            }
            libraryModel.setCurrentSongWithoutCheck(songNumber - 1);
            libraryModel.removeFavourite();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    private static void playSongInFavourite() {
        // Example: your libraryModel might have a method that returns a List<List<String>>:
        if (libraryModel.getFavoriteListSize() == 0 ) {
            System.out.println("❗ No songs in favourite list.");
        }else{
            System.out.println("Songs in favorite list added in the playlist.");
            libraryModel.playFavoriteList();
        }
    }

    // -------------------------------------------------------------------------
    //                  RATE SONG FUNCTION
    // -------------------------------------------------------------------------
    private static void rateSong() {
        while (true) {
            libraryModel.printRating();
            System.out.print(" ⭐ Enter your rating (1 to 5): ");
            String input = SCANNER.nextLine().trim();
            try {
                int rating = Integer.parseInt(input);
                if (rating >= 1 && rating <= 5) {
                    libraryModel.setRating(rating);
                    if (libraryModel.getRating() == 5) {
                        libraryModel.addFavourite();
                        System.out.printf("Song [%s] added to favorite.%n", libraryModel.getCurrentSongTitle());
                    }
                    return;
                } else {
                    System.out.println("❗ Rating must be between 1 and 5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❗ Invalid rating input.");
            }
        }
    }

}

