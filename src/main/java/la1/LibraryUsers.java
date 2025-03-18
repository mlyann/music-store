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
     * 注册新用户并存储加密密码
     *
     * @param userID 用户名
     * @param password 用户的明文密码
     * @param vicData VIC加密相关数据
     * @return 注册成功返回true，用户已存在返回false
     */
    public boolean registerUser(String userID, String password, VICData vicData) {
        if (users.containsKey(userID)) {
            return false;  // 用户已存在
        }

        // 使用EncryptVIC加密密码
        String encryptedPassword = EncryptVIC.encrypt(password, vicData);

        LibraryModel model = new LibraryModel(userID, musicStore);
        model.setEncryptedPassword(encryptedPassword);
        users.put(userID, model);
        return true;
    }

    /**
     * 验证用户登录信息是否正确
     * @param userID 用户名
     * @param inputPassword 输入的明文密码
     * @param vicData VIC加密相关数据
     * @return 验证成功返回true，否则false
     */
    public boolean authenticateUser(String userID, String inputPassword, VICData vicData) {
        if (!users.containsKey(userID)) {
            return false;  // 用户不存在
        }
        LibraryModel model = users.get(userID);
        String encryptedInput = EncryptVIC.encrypt(inputPassword, vicData);
        return model.getEncryptedPassword().equals(encryptedInput);
    }

    /**
     * 根据用户名获取对应的LibraryModel实例
     * @param userID 用户名
     * @return LibraryModel实例，不存在返回null
     */
    public LibraryModel getLibraryModel(String userID) {
        return users.get(userID);
    }

    /**
     * 检查用户是否存在
     * @param userID 用户名
     * @return 存在返回true，不存在返回false
     */
    public boolean userExists(String userID) {
        return users.containsKey(userID);
    }

    /**
     * 获取MusicStore实例
     * @return musicStore
     */
    public MusicStore getMusicStore() {
        return musicStore;
    }

    // ---- Function for JSON data ----

    // 保存数据到JSON文件，自动创建文件和目录
    public void saveToJSON(String filePath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            File file = new File(filePath);

            // 若文件不存在，则自动创建
            if (!file.exists()) {
                file.getParentFile().mkdirs(); // 创建目录（如果不存在）
                file.createNewFile();          // 创建文件
            }

            // 写入数据
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(users, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("⚠️ 保存JSON文件失败！");
        }
    }


    // 从JSON文件加载用户数据
    public void loadFromJSON(String filePath) {
        Gson gson = new Gson();
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("⚠️ 文件不存在，跳过加载过程。");
            return;
        }
        try (Reader reader = new FileReader(file)) {
            HashMap<String, LibraryModel> loadedUsers = gson.fromJson(
                    reader, new TypeToken<HashMap<String, LibraryModel>>(){}.getType());

            if (loadedUsers != null) {
                for (LibraryModel model : loadedUsers.values()) {
                    model.setMusicStore(this.musicStore);  // 恢复MusicStore引用

                    for (Song song : model.getUserSongs().values()) {
                        Album album = model.getUserAlbums().get(song.getAlbumTitle());
                        if (album != null) {
                            song.setAlbum(album);  // 恢复引用
                        }
                    }
                }
                users.clear();
                users.putAll(loadedUsers);

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("⚠️ 加载JSON文件失败！");
        }
    }


}
