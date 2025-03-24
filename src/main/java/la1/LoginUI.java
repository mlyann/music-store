package la1;

import java.util.Scanner;

public class LoginUI {
    private static final transient Scanner SCANNER = new Scanner(System.in);
    private static LibraryUsers libraryUsers;

    public static void main(String[] args) {
        MusicStore musicStore = new MusicStore();
        libraryUsers = new LibraryUsers(musicStore);

        // load users from a JSON file
        libraryUsers.loadFromJSON("data/users.json");


        // Create a VICData object with the following data
        VICData vicData = new VICData("12345", "250314", "HELLOWORLD", "AB CD EFGH", "");

        boolean running = true;
        while (running) {
            System.out.println("=========================================");
            System.out.println("🎵 Welcome to the Music Library System 🎵");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            String choice = SCANNER.nextLine().trim();
            switch (choice) {
                case "1":
                    registerUser(vicData);
                    break;
                case "2":
                    loginUser(vicData);
                    break;
                case "3":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
        // Save users to a JSON file before exiting
        libraryUsers.saveToJSON("data/users.json");
    }

    private static void registerUser(VICData vicData) {
        System.out.print("Enter new username: ");
        String userName = SCANNER.nextLine().trim();

        if (libraryUsers.userExists(userName)) {
            System.out.println("⚠️ User already exists!");
            return;
        }

        System.out.print("Enter password: ");
        String password = SCANNER.nextLine().trim();

        boolean success = libraryUsers.registerUser(userName, password, vicData);
        if (success) {
            System.out.println("✅ User registered successfully!");
        } else {
            System.out.println("❌ Registration failed!");
        }
    }

    private static void loginUser(VICData vicData) {
        System.out.print("Enter username: ");
        String userName = SCANNER.nextLine().trim();

        System.out.print("Enter password: ");
        String password = SCANNER.nextLine().trim();

        if (libraryUsers.authenticateUser(userName, password, vicData)) {
            System.out.println("🔓 Login successful!");
            LibraryModel currentUserModel = libraryUsers.getLibraryModel(userName);

            // 初始化MainUI，并调用静态主菜单
            MainUI.init(currentUserModel);
            MainUI.runMainMenu();
        } else {
            System.out.println("🚫 Login failed. Incorrect username or password.");
        }
    }
}
