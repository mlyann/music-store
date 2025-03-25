package la1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.HashMap;

public class LibraryUsers {
    private final HashMap<String, LibraryModel> users;
    private final MusicStore musicStore;

    public LibraryUsers(MusicStore musicStore) {
        this.musicStore = musicStore;
        this.users = new HashMap<>();
    }

    /**
      * Register a new user and store the encrypted password
      *
      * @param userID Username
      * @param password User's plain text password
      * @param vicData VIC encryption related data
      * @return Returns true if registration is successful, false if the user already exists
      */
    public boolean registerUser(String userID, String password, VICData vicData) {
        if (users.containsKey(userID)) {
            return false;  // User already exist
        }

        // Use EncryptVIC to encrypt the password
        String encryptedPassword = EncryptVIC.encrypt(password, vicData);

        LibraryModel model = new LibraryModel(userID, musicStore);
        model.setEncryptedPassword(encryptedPassword);
        users.put(userID, model);
        return true;
    }

    /**
     * Verify if the user login information is correct
     * @param userID Username
     * @param inputPassword Plain text password entered by the user
     * @param vicData VIC encryption related data
     * @return Returns true if verification is successful, otherwise false
     */
    public boolean authenticateUser(String userID, String inputPassword, VICData vicData) {
        if (!users.containsKey(userID)) {
            return false;
        }
        LibraryModel model = users.get(userID);
        String encryptedInput = EncryptVIC.encrypt(inputPassword, vicData);
        return model.getEncryptedPassword().equals(encryptedInput);
    }

    /**
     * Get the corresponding LibraryModel instance by username
     * @param userID Username
     * @return LibraryModel instance, or null if not found
     */
    public LibraryModel getLibraryModel(String userID) {
        return users.get(userID);
    }

    /**
     * Check if the user exists
     * @param userID User ID
     * @return exist return true, otherwise false
     */
    public boolean userExists(String userID) {
        return users.containsKey(userID);
    }

    // ---- Function for JSON data ----

    // save data to JSON file and create file and directory automatically
    public void saveToJSON(String filePath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            File file = new File(filePath);

            // If the file does not exist, create it automatically
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            // Write data
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(users, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("⚠️ Failed to save data! ");
        }
    }


    // load data from JSON file
    public void loadFromJSON(String filePath) {
        Gson gson = new Gson();
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("⚠️ data.json is not exist");
            return;
        }
        try (Reader reader = new FileReader(file)) {
            HashMap<String, LibraryModel> loadedUsers = gson.fromJson(
                    reader, new TypeToken<HashMap<String, LibraryModel>>(){}.getType());

            if (loadedUsers != null) {
                for (LibraryModel model : loadedUsers.values()) {
                    model.setMusicStore(this.musicStore);  // re-reference the MusicStore

                    for (Song song : model.getUserSongs().values()) {
                        Album album = model.getUserAlbums().get(song.getAlbumTitle());
                        if (album != null) {
                            song.setAlbum(album);  // re-reference the album
                        }
                    }
                }
                users.clear();
                users.putAll(loadedUsers);

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("⚠️ Failed to load data!");
        }
    }


}
