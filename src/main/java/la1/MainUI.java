package la1; // Or wherever your package is

import java.util.*;
import java.util.stream.Collectors;

public class MainUI {
    private static NavigationState currentState;
    private static MusicStore musicStore;
    private static LibraryModel libraryModel;
    private static final Scanner SCANNER = new Scanner(System.in);


    public static void init(LibraryModel model) {
        MainUI.libraryModel = model;
        musicStore = model.getMusicStore();
        currentState = NavigationState.MAIN_MENU;
    }

    // -------------------------------------------------------------------------
    //                  MAIN MENU FUNCTIONS
    // -------------------------------------------------------------------------
    public static void runMainMenu() {
        currentState = NavigationState.MAIN_MENU;

        if (libraryModel == null || musicStore == null) {
            System.out.println("⚠️ Main UI has not been initialized.");
            return;
        }

        boolean running = true;
        while (running && currentState == NavigationState.MAIN_MENU) {
            System.out.println("======================================================");
            System.out.println("    🎶 Welcome to the Music Library App (CSC 335) 🎶   ");
            System.out.println("         📅 Date: March 08, 2025");
            System.out.println("    👥 Authors: Haocheng Cao & Minglai Yang");
            System.out.println("    👤 Current User: " + libraryModel.getUserID());
            System.out.println("======================================================");

            System.out.println("\n---------- 🎵 MAIN MENU 🎵 ----------");
            System.out.println("1) 🔍 Search");
            System.out.println("2) 🎧 PlayingList");
            System.out.println("3) 📝 View all PlayLists");
            System.out.println("4) ❤️ Favorite List");
            System.out.println("5) 🏠 Library Lists");
            System.out.println("6) ➕ Load Songs single");
            System.out.println("7) 📊 Stats");
            System.out.println("0) 🚪 Quit the application");
            System.out.print("👉 Enter your choice: ");

            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    currentState = NavigationState.SEARCH_MENU;
                    runSearchMenu();
                    currentState = NavigationState.MAIN_MENU;
                    break;
                case "2":
                    currentState = NavigationState.PLAYLIST_MENU;
                    runPlaylistMenu();
                    currentState = NavigationState.MAIN_MENU;
                    break;
                case "3":
                    currentState = NavigationState.PLAYLIST_MENU;
                    runPlayListsMenu();
                    currentState = NavigationState.MAIN_MENU;
                    break;
                case "4":
                    currentState = NavigationState.FAVORITE_MENU;
                    runFavoriteMenu();
                    currentState = NavigationState.MAIN_MENU;
                    break;
                case "5":
                    currentState = NavigationState.LIBRARY_LISTS_MENU;
                    libraryListsMenu();
                    currentState = NavigationState.MAIN_MENU;
                    break;
                case "6":
                    inputSongs();
                    break;
                case "7":
                    currentState = NavigationState.STATS_MENU;
                    runStatsMenu();
                    currentState = NavigationState.MAIN_MENU;
                    break;
                case "0":
                    running = false;
                    currentState = NavigationState.LOGIN_MENU;
                    break;
                default:
                    System.out.println("❗ Invalid choice. Please try again.");
            }
        }

        System.out.println("🚪 Logging out. Goodbye, " + libraryModel.getUserID() + "!");
        cleanUp();
    }

    public static void cleanUp() {
        libraryModel = null;
        musicStore = null;
        currentState = null;
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

    private static void libraryListsMenu() {
        while (true) {
            System.out.println("\n---------- 🏠 Library Lists 🏠 ----------");
            System.out.println("1) 🎤 Show all songs");
            System.out.println("2) 🎼 Show all albums");
            System.out.println("3) 🎸 Show all artists");
            System.out.println("4) 📝 Show all playlists");
            System.out.println("0) 🔙 Back to Main Menu");
            System.out.print("👉 Enter choice: ");
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    String sort = chooseSortingMethod();
                    printSongSearchResults("Library Songs", libraryModel.getSongList(), "LIBRARY", sort);
                    break;
                case "2":
                    libraryModel.printAlbumSearchResults(libraryModel.getAlbumList());
                    break;
                case "3":
                    libraryModel.printAllArtists();
                    break;
                case "4":
                    System.out.println("\n---------- How do you want to sort playlists? ----------");
                    System.out.println("1) 🎸 By Title");
                    System.out.println("2) \uD83E\uDDD1\u200D\uD83C\uDFA8 By Artist");
                    System.out.println("3) ⭐ By Rating");
                    System.out.println("4) 🔢 No Sorting");
                    System.out.println("0) 🔙 Back to Library Lists");
                    System.out.print("👉 Enter your choice: ");
                    String sortChoice = SCANNER.nextLine().trim();
                    switch (sortChoice) {
                        case "1":
                            libraryModel.printAllPlayLists("title");
                            break;
                        case "2":
                            libraryModel.printAllPlayLists("artist");
                            break;
                        case "3":
                            libraryModel.printAllPlayLists("rating");
                            break;
                        case "4":
                            libraryModel.printAllPlayLists("none");
                            break;
                        case "0":
                            break;
                        default:
                            System.out.println("❗ Invalid sort choice. Returning to Library Lists.");
                            break;
                    }
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
            if (location.equals("LIBRARY")) {
                System.out.println("3) 📝 Search for playlists");
            }
            System.out.println("0) 🔙 Back to Search Menu");
            System.out.println("h) 🚪 Back to Main Menu");
            System.out.print("👉 Enter choice: ");
            String choice = SCANNER.nextLine().trim();
            switch (choice) {
                case "1":
                    searchSongsPipeline(location);
                    if (currentState == NavigationState.MAIN_MENU) {
                        return;
                    }
                    break;
                case "2":
                    searchAlbumsPipeline(location);
                    if (currentState == NavigationState.MAIN_MENU) {
                        return;
                    }
                    break;
                case "3":
                    if (location.equals("LIBRARY")) {
                        searchPlaylistsPipeline();
                        if (currentState == NavigationState.MAIN_MENU) {
                            return;
                        }
                    } else {
                        System.out.println("❗ Invalid choice. Please try again.");
                    }
                    break;
                case "0":
                    continue;
                case "h":
                    System.out.println("🚪 Back to Main Menu");
                    currentState = NavigationState.MAIN_MENU;
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
            System.out.println("🔎 Enter song title or artist keyword (or GENRE): ");
            System.out.println("0) 🔙 Back to search menu: ");
            System.out.println("h) 🚪 Back to Main Menu");
            System.out.print("👉 Enter choice: ");
            String keyword = SCANNER.nextLine().trim();
            if (keyword.equals("0")) {
                System.out.println("🔙 Back to Search Menu");
                return;
            } else if (keyword.equals("h")) {
                System.out.println("🚪 Back to Main Menu");
                currentState = NavigationState.MAIN_MENU;
                return;
            }
            ArrayList<ArrayList<String>> songResults = libraryModel.searchSong(keyword, location.equals("STORE"));
            if (songResults == null || songResults.isEmpty()) {
                System.out.println("❗ No songs found for '" + keyword + "'.");
            } else {
                String sort = chooseSortingMethod();
                libraryModel.sortSearchSongList(sort);
                ArrayList<ArrayList<String>> sortedResults = new ArrayList<>();
                for (Song s : libraryModel.getSearchSongList()) {
                    sortedResults.add(s.toStringList());
                }
                printSongSearchResults("Search Results (Songs)", sortedResults, location, sort);
                songSelectionMenu(sortedResults, location);
            }
        }
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
                    currentState = NavigationState.MAIN_MENU;
                    return;
                }
                case "ALL" -> {
                    if (!libraryModel.allSongSelection(songResults.size())) {
                        System.out.println("❗ System wrong. 1 ");
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
        return;
    }


    /**
     * Prompt the user to select a song from the search results.
     * The user can choose to add all songs, select a single song, or skip.
     * If the user selects a single song, prompt the user to select a song by index.
     * @return the user's choice
     */
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

    /**
     * Prompt the user to select a song from the search results.
     * The user can choose to add all songs, select a single song, or skip.
     * If the user selects a single song, prompt the user to select a song by index.
     * @param songResults the list of songs to choose from
     * @param location the location of the search (STORE or LIBRARY)
     */

    private static void handleSongSelection(ArrayList<ArrayList<String>> songResults, String location, String title) {
        while (true) {
            System.out.println("UserSongs: " + libraryModel.getSearchSongList().size());
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
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("❗ Please enter a valid number.");
            }
        }
    }

    /**
     * Handle the user's actions for a selected song.
     * @param songTitle the title of the selected song
     */

     private static void handleSongActions(String songTitle) {
        while (true) {
            if (!libraryModel.checkCurrentSong(songTitle)) {
                System.out.println("❗ System wrong. 4");
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
            System.out.println("4) ➕ Add to a playlist");
            System.out.println("5) ❌ Remove from a playlist");
            System.out.println("6) ❗ Remove from Library");
            if (libraryModel.isCurrentSongAlbum()) {
                System.out.println("7) 🎼 Open Album");
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
                case "4":
                    if (openPlayList()) {
                        libraryModel.addSongToPlayLists();
                    }
                    break;
                case "5":
                    if (openPlayList()) {
                        libraryModel.removeSongFromPlayLists();
                    }
                    break;
                case "6":
                    if (removeSongFromLibrary()) {
                        return;
                    }
                    break;
                case "7":
                    openSongAlbum();
                    break;
                case "0":
                    return;
                case "h":
                    currentState = NavigationState.MAIN_MENU;
                    return;
                default:
                    System.out.println("❗ Invalid choice. Try again.");
                    break;
            }
        }
    }

    private static boolean removeSongFromLibrary() {
        System.out.print("Are you sure to remove ");
        System.out.print(libraryModel.getCurrentSongInfo());
        System.out.println(" from the library? ");
        System.out.println("1) ❌ Remove it ");
        System.out.println("0) 🔙 Go back");
        System.out.print("👉 Enter choice: ");
        String choice = SCANNER.nextLine().trim();
        while (true) {
            if (choice.equals("1")) {
                if (!libraryModel.removeSongFromLibrary()) {
                    System.out.println("❗ System wrong. 4");
                } else {
                    System.out.println("Song removed from library.");
                    return true;
                }
            } else if (choice.equals("0")) {
                return false;
            } else {
                System.out.println("❗ Invalid choice. Please try again.");
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
                currentState = NavigationState.MAIN_MENU;
                return;
            }
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
                libraryModel.printAlbumSearchResults(albumResults);
                albumSelectionMenu(albumResults, location);
            }
        }
    }



    /**
     * Prompt the user to select an album from the search results.
     * The user can choose to add all albums, select a single album, or skip.
     * If the user selects a single album, prompt the user to select an album by index.
     * @param albumResults the list of albums to choose from
     * @param location the location of the search (STORE or LIBRARY)
     */
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
                    currentState = NavigationState.MAIN_MENU;
                    return;
                }
                case "ALL" -> {
                    if (!libraryModel.allAlbumSelection(albumResults.size())) {
                        System.out.println("❗ System wrong. 5");
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

    /**
     * Prompt the user to select an album from the search results.
     * The user can choose to add all albums, select a single album, or skip.
     * If the user selects a single album, prompt the user to select an album by index.
     * @return the user's choice
     */
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
                        System.out.println("❗ System wrong. 6");
                        break;
                    }
                    System.out.println(String.format("Album [%s] added to library! ", albumTitle));
                } else {
                    if (!libraryModel.setCurrentAlbum(index - 1, albumTitle)) {
                        System.out.println("❗ System wrong. 7");
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


    /**
     * Handle the user's actions for a selected album.
     * @param albumTitle the title of the selected album
     */
    private static void handleAlbumActions(String albumTitle) {
        while (true) {
            if (!libraryModel.checkCurrentAlbum(albumTitle)) {
                System.out.println("❗ System wrong. 8");
                return;
            }
            System.out.println("\n🎼 Selected Album: [" + libraryModel.getCurrentAlbumInfo() + "]");
            System.out.println("Actions: ");
            System.out.println("1) ▶️ Play album");
            System.out.println("2) \uD83D\uDCC2 Open album");
            System.out.println("3) ❗ Remove from Library");
            if(!libraryModel.isCurrentAlbumComplete()) {
                System.out.println("4) 📝 Check complete album");
            }
            System.out.println("0) 🔙 Go back");
            System.out.println("h) 🚪 Back to Main Menu");
            System.out.print("👉 Enter choice: ");
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    if (!libraryModel.playAlbum(albumTitle)) {
                        System.out.println("❗ System wrong. 9");
                    }
                    return;
                case "2":
                    openAlbum(albumTitle);
                    continue;
                case "3":
                    if (removeAlbumFromLibrary()) {
                        return;
                    }
                    break;
                case "4":
                    if (!libraryModel.isCurrentAlbumComplete()) {
                        showCompleteAlbum(albumTitle);
                        continue;
                    } else
                        System.out.println("❗ Invalid choice. Please try again.");
                case "0":
                    return;
                case "h":
                    currentState = NavigationState.MAIN_MENU;
                    return;
                default:
                    System.out.println("❗ Invalid choice. Please try again.");
            }
        }

    }

    /**
     * Open an album to view its songs.
     * @param albumTitle the title of the album to open
     */
    public static void openAlbum(String albumTitle) {
        String sort = chooseSortingMethod();
        if (!libraryModel.openAlbum(albumTitle, sort)) {
            System.out.println("❗ System wrong. 10");
        }
        ArrayList<ArrayList<String>> albumSongs = libraryModel.SongToString();
        ArrayList<String> albumInfo = albumSongs.remove(0);
        if (albumInfo == null || albumInfo.size() < 4) {
            System.out.println("❗ System wrong. 11");
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
        printSongSearchResults("Album Songs", albumSongs, "LIBRARY", sort);
        handleSongSelection(albumSongs, "LIBRARY", sb.toString());

    }

    private static void openSongAlbum() {
        if (libraryModel.getSongAlbum() == null) {
            System.out.println("❗ System wrong. 14");
            return;
        }
        String albumTitle = libraryModel.getSongAlbum();
        openAlbum(albumTitle);
    }

    private static void showCompleteAlbum(String albumTitle) {
        if (libraryModel.showCompleteAlbumStore(albumTitle)) {
            System.out.println("Please add Songs through search in Music Store.");
        } else {
            System.out.println("❗ System wrong. 12");
        }
    }


    private static boolean removeAlbumFromLibrary() {
        System.out.print("Are you sure to remove ");
        System.out.print(libraryModel.getCurrentAlbumInfo());
        System.out.println(" from the library? ");
        System.out.println("1) ❌ Remove it ");
        System.out.println("0) 🔙 Go back");
        System.out.print("👉 Enter choice: ");
        String choice = SCANNER.nextLine().trim();
        while (true) {
            if (choice.equals("1")) {
                if (!libraryModel.removeAlbumFromLibrary()) {
                    System.out.println("❗ System wrong. 4");
                } else {
                    System.out.println("Album removed from library.");
                    return true;
                }
            } else if (choice.equals("0")) {
                return false;
            } else {
                System.out.println("❗ Invalid choice. Please try again.");
            }
        }
    }

    // -------------------------------------------------------------------------
    //                  PLAYLIST MENU (EXAMPLE)
    // -------------------------------------------------------------------------
    /**
     * Run the playlist menu.
     */
    private static void runPlaylistMenu() {
        String sortOrder = "none"; // 初始排序方式为 none
        while (true) {
            System.out.println("\n---------- ▶️ PLAYLIST MENU ▶️ ----------");
            System.out.println("1) 📝 Show current playlist (Sorted by: " + sortOrder + ")");
            System.out.println("2) 🗑️ Clear playlist");
            System.out.println("3) ➕ Add songs to a playlist");
            System.out.println("4) ❌ Remove songs from a playlist");
            System.out.println("5) ▶️ Play songs in playlist");
            System.out.println("6) ⏩ Play next song in playlist");
            System.out.println("7) ⭐ Rate a song in playlist");
            System.out.println("8) ❤️ Favorite a song in playlist");
            System.out.println("9) 🔢 Sorting");
            System.out.println("0) 🔙 Back to Main Menu");
            System.out.print("👉 Enter choice: ");

            String choice = SCANNER.nextLine().trim();
            switch (choice) {
                case "1":
                    libraryModel.printPlaylist(sortOrder);
                    break;
                case "2":
                    clearPlaylist();
                    break;
                case "3":
                    addSongToPlaylist();
                    break;
                case "4":
                    removeSongFromPlaylist();
                    libraryModel.printPlaylist(sortOrder);
                    break;
                case "5":
                    playSongInPlaylist();
                    libraryModel.printPlaylist(sortOrder);
                    break;
                case "6":
                    libraryModel.playNextSong();
                    libraryModel.printPlaylist(sortOrder);
                    break;
                case "7":
                    rateSongInPlaylist();
                    libraryModel.printPlaylist(sortOrder);
                    break;
                case "8":
                    favoriteSongInPlaylist();
                    libraryModel.printPlaylist(sortOrder);
                    break;
                case "9":
                    System.out.println("\n---------- How do you want to sort playlists? ----------");
                    System.out.println("1) 🎸 By Title");
                    System.out.println("2) \uD83E\uDDD1\u200D\uD83C\uDFA8 By Artist");
                    System.out.println("3) ⭐ By Rating");
                    System.out.println("4) 🔢 No Sorting");
                    System.out.println("5) 🔀 Shuffle");
                    System.out.println("0) 🔙 Back to Playlist Menu");
                    System.out.print("👉 Enter your choice: ");
                    String sortChoice = SCANNER.nextLine().trim();
                    switch (sortChoice) {
                        case "1":
                            sortOrder = "title";
                            System.out.println("Sorting by Artist selected.");
                            break;
                        case "2":
                            sortOrder = "artist";
                            System.out.println("Sorting by Year selected.");
                            break;
                        case "3":
                            sortOrder = "rating";
                            System.out.println("Sorting by Rating selected.");
                            break;
                        case "4":
                            sortOrder = "none";
                            System.out.println("No Sorting selected.");
                            break;
                        case "5":
                            sortOrder = "shuffle";
                            System.out.println("Shuffle selected.");
                            break;
                        case "0":
                            break;
                        default:
                            System.out.println("❗ Invalid sorting choice.");
                            break;
                    }
                    break;
                case "0":
                    return;
                default:
                    System.out.println("❗ Invalid choice. Try again.");
                    break;
            }
        }
    }

    /**
     * Clear the playlist
     */
    private static void clearPlaylist() {
        libraryModel.clearPlaylist();
        System.out.println("🗑️ Cleared all songs in the playlist.");
    }

    /**
     * Add a song to the playlist
     */
    private static void addSongToPlaylist() {
        // Print the user songs with numbers
        libraryModel.userSongSearch();   // set the search list to user songs
        String sort = chooseSortingMethod();
        libraryModel.printUserSongsTable(sort);
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

    /**
     * remove a song from the playlist
     */
    private static void removeSongFromPlaylist() {
        libraryModel.playListSearch();
        String sortKey = chooseSortingMethod();
        libraryModel.printPlaylist(sortKey);

        int size = libraryModel.getSearchSongListSize();
        System.out.print("🎶 Enter the song number to remove: ");
        String input = SCANNER.nextLine().trim();
        try {
            int songNumber = Integer.parseInt(input);
            if (songNumber < 1 || songNumber > size) {
                System.out.println("Invalid selection. Please enter a valid number.");
                return;
            }
            libraryModel.setCurrentSongWithoutCheck(songNumber - 1);
            libraryModel.removeSongFromPlaylist();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
        sortKey = chooseSortingMethod();
        libraryModel.printPlaylist(sortKey);
    }

    /**
     * let the user pick which sorting
     */
    private static String chooseSortingMethod() {
        System.out.println("\n---------- How do you want to sort the playlist? ----------");
        System.out.println("1) 🎸 By Title");
        System.out.println("2) \uD83E\uDDD1\u200D\uD83C\uDFA8 By Artist");
        System.out.println("3) ⭐ By Rating");
        System.out.println("4) 🔢 No Sorting");
        System.out.println("5) 🔀 Shuffle");
        System.out.print("👉 Enter your choice: ");
        String choice = SCANNER.nextLine().trim();
        switch (choice) {
            case "1":
                return "title";
            case "2":
                return "artist";
            case "3":
                return "rating";
            case "4":
                return "none";
            case "5":
                return "shuffle";
            default:
                System.out.println("❗No Sorting");
                return "none";
        }
    }

    /**
     * Play the song in the playlist
     */
    private static void playSongInPlaylist() {
        // Example: your libraryModel might have a method that returns a List<List<String>>:
        if (libraryModel.getPlayerSize() == 0 ) {
            System.out.println("❗ No songs in playlist.");
        }else{
            libraryModel.getPlaying();
        }
    }

    /**
     * Rate a song in the playlist
     */
    private static void rateSongInPlaylist() {
        // Print the current playlist with numbers
        libraryModel.playListSearch();  // set the search list to playlist
        String sortKey = chooseSortingMethod();
        libraryModel.printPlaylist(sortKey);
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

    /**
     * Rate a song
     */
    private static void favoriteSongInPlaylist() {
        libraryModel.playListSearch();  // set the search list to playlist
        String sortKey = chooseSortingMethod();
        libraryModel.printPlaylist(sortKey);
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
    //                  PLAYLISTS FUNCTIONS
    // -------------------------------------------------------------------------
    /**
     * Search for playlists in the User Library.
     */
    private static void searchPlaylistsPipeline() {
        libraryModel.generateAutomaticPlaylists();
        while (true) {
            System.out.println("\n--- 📝 Searching for Playlists ---");
            System.out.println("🔎 Enter Playlist Name");
            System.out.println("0) 🔙 Back to Search Menu: ");
            System.out.println("h) 🚪 Back to Main Menu");
            System.out.print("👉 Enter choice: ");
            String keyword = SCANNER.nextLine().trim();
            if (keyword.equals("0")) {
                System.out.println("🔙 Back to Search Menu");
                break;
            } else if (keyword.equals("h")) {
                System.out.println("🚪 Back to Main Menu");
                currentState = NavigationState.MAIN_MENU;
                return;
            }
            String sortKey = chooseSortingMethod();
            libraryModel.getPlayLists(keyword,sortKey);
        }
    }

    /**
     * Search for songs in the User Library.
     */
    private static void runPlayListsMenu() {
        libraryModel.generateAutomaticPlaylists();
        while (true) {
            System.out.println("\n---------- 🎵 PLAYLISTS MENU 🎵 ----------");
            System.out.println("1) 📝 Open a playlists");
            System.out.println("2) 🗑️ Clear all playlists");
            System.out.println("3) ➕ Add a playlist");
            System.out.println("4) ❌ Delete a playlist");
            System.out.println("0) 🔙 Back to Main Menu");
            System.out.print("👉 Enter choice: ");

            String choice = SCANNER.nextLine().trim();
            switch (choice) {
                case "1":
                    if (openPlayList()) {
                        playListSubMenu();
                    }
                    if (currentState == NavigationState.MAIN_MENU) {
                        return;
                    }
                    break;

                case "2":
                    libraryModel.clearAllPlayLists();
                    System.out.println("🗑️ Cleared all playlists.");
                    if (currentState == NavigationState.MAIN_MENU) {
                        return;
                    }
                    break;

                case "3":
                    createNewPlaylist();
                    if (currentState == NavigationState.MAIN_MENU) {
                        return;
                    }
                    break;

                case "4":
                    deletePlaylist();
                    if (currentState == NavigationState.MAIN_MENU) {
                        return;
                    }
                    break;

                case "0":
                    return;

                default:
                    System.out.println("❗ Invalid choice. Try again.");
                    if (currentState == NavigationState.MAIN_MENU) {
                        return;
                    }
                    break;
            }
        }
    }

    /**
     * Run the playlist submenu
     */
    private static void playListSubMenu() {
        libraryModel.generateAutomaticPlaylists();
        System.out.println("Selected Playlist: " + libraryModel.getCurrentPlaylistName());
        while (true) {
            System.out.println("\n---------- 🎵 PLAYLISTS SUBMENU 🎵 ----------");
            System.out.println("1) 📝 Add song to playlist");
            System.out.println("2) 🗑️ Delete song from playlist");
            System.out.println("3) ⏩ play this playlist");
            System.out.println("0) 🔙 Go back");
            System.out.println("h) 🚪 Back to Main Menu");
            System.out.print("👉 Enter choice: ");

            String choice = SCANNER.nextLine().trim();
            switch (choice) {
                case "1":
                    searchSongToPlaylists();
                    if (currentState == NavigationState.MAIN_MENU) {
                        return;
                    }
                    break;
                case "2":
                    searchSongFromPlaylists();
                    if (currentState == NavigationState.MAIN_MENU) {
                        return;
                    }
                    break;
                case "3":
                    libraryModel.playCurrentPlayList();
                    if (currentState == NavigationState.MAIN_MENU) {
                        return;
                    }
                    break;
                case "0":
                    return;
                case "h":
                    currentState = NavigationState.MAIN_MENU;
                    return;
                default:
                    System.out.println("❗ Invalid choice. Try again.");
            }
        }
    }

    /**
     * Create a new playlist
     */
    private static void createNewPlaylist() {
        while (true) {
            System.out.println("🎵 Enter the playlist name: ");
            System.out.println("0) 🔙 Go back");
            System.out.print("👉 Enter choice: ");
            String playlistName = SCANNER.nextLine().trim();
            if (playlistName.equals("0")) {
                return;
            }
            if (playlistName.isEmpty()) {
                System.out.println("❗ Playlist name cannot be empty.");
            }
            if (libraryModel.createPlaylist(playlistName)) {
                System.out.println("🎵 Playlist created: " + playlistName);
                return;
            } else {
                System.out.println("❗ Playlist already exists: " + playlistName);
            }
        }
    }

    /**
     * Open a playlist
     */
    private static boolean openPlayList() {
        libraryModel.generateAutomaticPlaylists();
        String sortKey = chooseSortingMethod();
        libraryModel.printAllPlayLists(sortKey);
        System.out.println("🎵 Enter the playlist number to open: ");
        System.out.println("0) 🔙 Go back");
        System.out.print("👉 Enter choice: ");
        while (true) {
            String input = SCANNER.nextLine().trim();
            if (input.equals("0")) {
                return false;
            }
            try {
                int playlistNumber = Integer.parseInt(input);
                if (playlistNumber < 1 || playlistNumber > libraryModel.getPlayListsSize()) {
                    System.out.println("Invalid selection. Please enter a valid number.");
                } else {
                    libraryModel.selectCurrentPlaylist(playlistNumber - 1);
                    String sortKey1 = chooseSortingMethod();
                    libraryModel.printCurrentPlaylist(sortKey1);
                    return true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Delete a playlist
     */
    private static void deletePlaylist() {
        libraryModel.generateAutomaticPlaylists();
        String sortKey = chooseSortingMethod();
        libraryModel.printAllPlayLists(sortKey);
        System.out.println("🎵 Enter the playlist number to delete: ");
        System.out.println("0) 🔙 Go back");
        System.out.print("👉 Enter choice: ");
        while (true) {
            String input = SCANNER.nextLine().trim();
            if (input.equals("0")) {
                return;
            }
            try {
                int playlistNumber = Integer.parseInt(input);
                if (playlistNumber < 1 || playlistNumber > libraryModel.getPlayListsSize()) {
                    System.out.println("Invalid selection. Please enter a valid number.");
                } else {
                    libraryModel.removePlayList(playlistNumber - 1);
                    System.out.println("🎵 Playlist deleted.");
                    String sortKey1 = chooseSortingMethod();
                    libraryModel.printAllPlayLists(sortKey1);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Search for songs to add to playlists
     */
    private static void searchSongToPlaylists() {
        libraryModel.generateAutomaticPlaylists();
        // Print the user songs with numbers
        libraryModel.userSongSearch();   // set the search list to user songs
        String sort = chooseSortingMethod();
        libraryModel.printUserSongsTable(sort);
        int size = libraryModel.getSearchSongListSize();
        System.out.println("🎶 Enter the song number to add: ");
        System.out.println("0) 🔙 Go back");
        System.out.print("👉 Enter choice: ");
        while (true) {
            String input = SCANNER.nextLine().trim();
            if (input.equals("0")) {
                return;
            }
            try {
                int songNumber = Integer.parseInt(input);
                // Get the library songs list to show which title is being added.
                if (songNumber < 1 || songNumber > size) {
                    System.out.println("Invalid selection. Please enter a valid number.");
                    return;
                } else {
                    libraryModel.setCurrentSongWithoutCheck(songNumber - 1);
                    libraryModel.addSongToPlayLists();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Search for songs to remove from playlists
     */
    private static void searchSongFromPlaylists() {
        libraryModel.generateAutomaticPlaylists();
        // Print the current playlist with numbers
        libraryModel.playListsCurrent();  // set the search list to playlist
        String sortKey = chooseSortingMethod();
        libraryModel.printCurrentPlaylist(sortKey);
        int size = libraryModel.currentPlistSize();
        System.out.println("🎶 Enter the song number to remove: ");
        System.out.println("0) 🔙 Go back");
        System.out.print("👉 Enter choice: ");
        while (true) {
            String input = SCANNER.nextLine().trim();
            if (input.equals("0")) {
                return;
            }
            try {
                int songNumber = Integer.parseInt(input);
                if (songNumber < 1 || songNumber > size) {
                    System.out.println("Invalid selection. Please enter a valid number.");
                } else {
                    libraryModel.setCurrentSongWithoutCheck(songNumber - 1);
                    libraryModel.removeSongFromPlayLists();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }



    // -------------------------------------------------------------------------
    //                  FAVORITE LIST MENU
    // -------------------------------------------------------------------------
    /**
     * Run the favorite list menu.
     */
    private static void runFavoriteMenu() {
        while (true) {
            System.out.println("\n---------- ❤️ FAVORITE LIST ❤️ ----------");
            System.out.println("1) 📝 Show favourite list");
            System.out.println("2) 🗑️ Clear favourite list");
            System.out.println("3) ➕ Add songs to a favourite list");
            System.out.println("4) \uD83D\uDC94 Remove songs from a favourite list");
            System.out.println("5) ▶️ Play songs in a favourite list");
            System.out.println("0) 🔙 Back to Main Menu");
            System.out.print("👉 Enter choice: ");

            String choice = SCANNER.nextLine().trim();
            switch (choice) {
                case "1":
                    String sortKey = chooseSortingMethod();
                    libraryModel.printFavoriteList(sortKey);
                    if (currentState == NavigationState.MAIN_MENU) {
                        return;
                    }
                    break;
                case "2":
                    libraryModel.clearFavoriteList();
                    System.out.println("🗑️ Cleared all songs in the favourite list.");
                    if (currentState == NavigationState.MAIN_MENU) {
                        return;
                    }
                    break;
                case "3":
                    addSongToFavourite();
                    if (currentState == NavigationState.MAIN_MENU) {
                        return;
                    }
                    break;
                case "4":
                    removeSongFromFavourite();
                    String sortKey1 = chooseSortingMethod();
                    libraryModel.printFavoriteList(sortKey1);
                    if (currentState == NavigationState.MAIN_MENU) {
                        return;
                    }
                    break;
                case "5":
                    playSongInFavourite();
                    if (currentState == NavigationState.MAIN_MENU) {
                        return;
                    }
                    break;
                case "0":
                    return;
                    
                default:
                    System.out.println("❗ Invalid choice. Try again.");
            }
        }
    }

    /**
     * Add a song to the favorite list
     */
    private static void addSongToFavourite() {
        // Print the user songs with numbers
        libraryModel.userSongSearch();   // set the search list to user songs
        String sort = chooseSortingMethod();
        libraryModel.printUserSongsTable(sort);
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

    /**
     * Remove a song from the favorite list
     */
    private static void removeSongFromFavourite() {
        libraryModel.favoriteListSearch();
        libraryModel.getFavoriteListSize();
        int size = libraryModel.getSearchSongListSize();
        System.out.print("🎶 Enter the song number to remove: ");
        String input = SCANNER.nextLine().trim();
        try {
            int songNumber = Integer.parseInt(input);
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

    /**
     * Play the song in the favorite list
     */
    private static void playSongInFavourite() {
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
    /**
     * Rate a song
     */
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

    private static void runStatsMenu() {
        System.out.println("\n---------- 📊 STATS ----------");

        // Print the 10 Most Frequently Played Songs.
        System.out.println("\n10 Most Frequently Played Songs:");
        printFrequentSongs();
        System.out.println("\n10 Most Recently Played Songs:");
        printRecentSongs();
        currentState = NavigationState.MAIN_MENU;
    }

    public List<Song> getTopFrequentSongs() {
        return libraryModel.getPlayCounts().entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .map(entry -> {
                    String songKey = entry.getKey();
                    return libraryModel.getUserSongs().get(songKey); // 在这里获得 Song
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static void printFrequentSongs() {
        List<Song> frequentSongs = libraryModel.getTopFrequentSongs();

        if (frequentSongs.isEmpty()) {
            System.out.println("❗No frequent songs to display.");
            return;
        }

        List<List<String>> tableData = new ArrayList<>();
        tableData.add(Arrays.asList("Rank", "Title", "Artist", "Play Count"));
        tableData.add(Arrays.asList("###SEPARATOR###"));

        int rank = 1;
        for (Song song : frequentSongs) {
            String key = libraryModel.generateKey(song);
            Integer count = libraryModel.getPlayCounts().get(key);
            if (count == null) count = 0;

            List<String> row = Arrays.asList(
                    String.valueOf(rank),
                    song.getTitle(),
                    song.getArtist(),
                    String.valueOf(count)
            );
            tableData.add(row);
            rank++;
        }
        TablePrinter.printDynamicTable("10 Most Frequently Played Songs", tableData);
    }

    private static void printRecentSongs() {
        List<Song> recentSongs = libraryModel.getRecentSongs();
        if (recentSongs.isEmpty()) {
            System.out.println("❗No recent songs to display.");
            return;
        }
        List<List<String>> tableData = new ArrayList<>();
        List<String> header = Arrays.asList("Rank", "Title", "Artist");
        tableData.add(header);
        tableData.add(Arrays.asList("###SEPARATOR###"));

        int rank = 1;
        for (Song song : recentSongs) {
            List<String> row = Arrays.asList(
                    String.valueOf(rank),
                    song.getTitle(),
                    song.getArtist()
            );
            tableData.add(row);
            rank++;
        }
        TablePrinter.printDynamicTable("10 Most Recently Played Songs", tableData);
    }
    static void printSongSearchResults(String tableTitle, ArrayList<ArrayList<String>> songResults, String location, String sortOption) {
        if (songResults == null || songResults.isEmpty()) {
            System.out.println("❗ No Songs in Library.");
            return;
        }
        boolean isStore = location.equals("STORE");

        if (!sortOption.equals("none")) {
            switch (sortOption) {
                case "title":
                    LibraryModel.sortSearchSongList("title");
                    Collections.sort(songResults, (row1, row2) -> {
                        int cmp = row1.get(0).compareToIgnoreCase(row2.get(0));
                        if (cmp != 0) return cmp;
                        return row1.get(1).compareToIgnoreCase(row2.get(1));
                    });
                    break;
                case "artist":
                    LibraryModel.sortSearchSongList("artist");
                    Collections.sort(songResults, (row1, row2) ->
                            row1.get(1).compareToIgnoreCase(row2.get(1))
                    );
                    break;
                case "rating":
                    LibraryModel.sortSearchSongList("rating");
                    Collections.sort(songResults, (row1, row2) -> {
                        if (isStore) {
                            return row1.get(0).compareToIgnoreCase(row2.get(0));
                        }
                        int star1 = row1.get(5).contains("★") ? row1.get(5).replace("☆", "").length() : 0;
                        int star2 = row2.get(5).contains("★") ? row2.get(5).replace("☆", "").length() : 0;
                        int cmp = Integer.compare(star2, star1);
                        if (cmp != 0) return cmp;
                        cmp = row1.get(0).compareToIgnoreCase(row2.get(0));
                        if (cmp != 0) return cmp;
                        return row1.get(1).compareToIgnoreCase(row2.get(1));
                    });
                    break;
                case "shuffle":
                    Collections.shuffle(songResults);
                    break;
                default:
                    break;
            }
        }
        List<String> header = new ArrayList<>();
        header.add("No.");
        header.add("Title");
        header.add("Artist");
        header.add("Genre");
        header.add("Year");
        if (!isStore) {
            header.add("Favorite");
            header.add("Rating  ");
        }
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
        List<List<String>> tableRows = new ArrayList<>();
        tableRows.add(header);
        int index = 1;
        for (List<String> row : songResults) {
            List<String> newRow = new ArrayList<>();
            newRow.add(String.valueOf(index++));
            newRow.add(row.get(0));
            newRow.add(row.get(1));
            newRow.add(row.get(2));
            newRow.add(row.get(3));
            if (!isStore) {
                newRow.add(row.get(4));
                newRow.add(row.get(5));
            }
            if (anyAlbum) {
                String album = (row.size() > 6) ? row.get(6) : "";
                newRow.add(album == null ? "" : album);
            }
            tableRows.add(newRow);
        }
        TablePrinter.printDynamicTable(tableTitle, tableRows);
    }
}

