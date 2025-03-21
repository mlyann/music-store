package la1;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LibraryUsersTest {

    private static final String TEST_JSON_FILE = "data/test_users.json";

    private static LibraryUsers libraryUsers;
    private static VICData vicData; // 用于加密密码
    private static final String USER_ID = "testuser";
    private static final String PASSWORD = "mypassword123";

    @BeforeAll
    static void setupAll() {
        // 初始化测试的 MusicStore
        MusicStore musicStore = new MusicStore();
        libraryUsers = new LibraryUsers(musicStore);

        // 配置用于VIC加密的示例数据
        vicData = new VICData("12345", "250314", "HELLOWORLD", "AB CD EFGH", "");
    }

    @AfterAll
    static void tearDownAll() {
        // 清理测试文件
        File file = new File(TEST_JSON_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    @Order(1)
    @DisplayName("1) 测试注册新用户")
    void testRegisterUser() {
        // 初次注册应成功
        boolean registered = libraryUsers.registerUser(USER_ID, PASSWORD, vicData);
        assertTrue(registered, "首次注册应成功");

        // 重复注册同一用户ID，应失败
        boolean registeredAgain = libraryUsers.registerUser(USER_ID, PASSWORD, vicData);
        assertFalse(registeredAgain, "重复注册同一用户ID应返回false");
    }

    @Test
    @Order(2)
    @DisplayName("2) 测试用户登录认证")
    void testAuthenticateUser() {
        // 先确保已注册
        if (!libraryUsers.userExists(USER_ID)) {
            libraryUsers.registerUser(USER_ID, PASSWORD, vicData);
        }

        // 正确密码
        boolean authOk = libraryUsers.authenticateUser(USER_ID, PASSWORD, vicData);
        assertTrue(authOk, "使用正确密码应认证成功");

        // 错误密码
        boolean authFail = libraryUsers.authenticateUser(USER_ID, "wrongPass", vicData);
        assertFalse(authFail, "使用错误密码应认证失败");

        // 不存在的用户
        boolean noUser = libraryUsers.authenticateUser("notExist", PASSWORD, vicData);
        assertFalse(noUser, "不存在的用户应返回false");
    }

    @Test
    @Order(3)
    @DisplayName("3) 测试获取LibraryModel与用户存在性")
    void testGetLibraryModelAndUserExists() {
        // 先注册一个用户
        String anotherUser = "anotherUser";
        libraryUsers.registerUser(anotherUser, "pass", vicData);

        assertTrue(libraryUsers.userExists(anotherUser));
        LibraryModel model = libraryUsers.getLibraryModel(anotherUser);
        assertNotNull(model);
        assertEquals(anotherUser, model.getUserID());

        // 未注册用户
        assertNull(libraryUsers.getLibraryModel("noSuchUser"));
        assertFalse(libraryUsers.userExists("noSuchUser"));
    }

    @Test
    @Order(4)
    @DisplayName("4) 测试保存到JSON文件")
    void testSaveToJSON() throws Exception {
        // 注册几个用户
        libraryUsers.registerUser("alice", "alicePass", vicData);
        libraryUsers.registerUser("bob", "bobPass", vicData);

        // 调用保存方法
        libraryUsers.saveToJSON(TEST_JSON_FILE);

        // 检查文件是否生成
        File file = new File(TEST_JSON_FILE);
        assertTrue(file.exists(), "保存JSON后文件应存在");

        // 简单检查文件非空
        long fileSize = Files.size(Paths.get(TEST_JSON_FILE));
        assertTrue(fileSize > 0, "保存后文件内容应非空");
    }

    @Test
    @Order(5)
    @DisplayName("5) 测试从JSON文件加载用户数据")
    void testLoadFromJSON() {
        // 新建一个LibraryUsers，用于测试加载
        MusicStore newStore = new MusicStore();
        LibraryUsers newLibraryUsers = new LibraryUsers(newStore);

        // 从之前保存的文件加载
        newLibraryUsers.loadFromJSON(TEST_JSON_FILE);

        // 验证加载数据
        assertTrue(newLibraryUsers.userExists("alice"), "应加载到alice用户");
        assertTrue(newLibraryUsers.userExists("bob"), "应加载到bob用户");

        // 检查加密密码是否成功还原（只要能认证即可）
        boolean aliceAuth = newLibraryUsers.authenticateUser("alice", "alicePass", vicData);
        boolean bobAuth   = newLibraryUsers.authenticateUser("bob",   "bobPass",   vicData);

        assertTrue(aliceAuth, "alice应能认证成功");
        assertTrue(bobAuth, "bob应能认证成功");

        // 确保加载后的LibraryModel也有正确的MusicStore引用
        LibraryModel aliceModel = newLibraryUsers.getLibraryModel("alice");
        assertNotNull(aliceModel.getMusicStore(), "加载后应恢复musicStore引用");

    }

    @Test
    @Order(6)
    @DisplayName("综合测试")
    void testGeneral() {
        libraryUsers.registerUser(USER_ID, PASSWORD, vicData);
        libraryUsers.authenticateUser(USER_ID, PASSWORD, vicData);
        LibraryModel model = libraryUsers.getLibraryModel(USER_ID);
        model.searchSong("Adele", true);
        model.allSongSelection(24);
        model.searchAlbum("Coldplay", true);
        model.allAlbumSelection(1);
        libraryUsers.saveToJSON(TEST_JSON_FILE);

        MusicStore newStore = new MusicStore();
        LibraryUsers newLibraryUsers = new LibraryUsers(newStore);
        newLibraryUsers.loadFromJSON(TEST_JSON_FILE);

        File file = new File(TEST_JSON_FILE);
        if (file.exists()) {
            file.delete();
        }

        newLibraryUsers.loadFromJSON(TEST_JSON_FILE);
    }

    @Test
    @Order(7)
    @DisplayName("Test saveToJSON IOException coverage")
    void testSaveToJSONIOException() {
        LibraryUsers libraryUsers = new LibraryUsers(new MusicStore());
        // Attempt to save to an invalid path
        String invalidPath = "/System/Library/mytest.json";

        libraryUsers.saveToJSON(invalidPath);

        // Optionally verify the console output or logs to ensure catch block ran
        // (Exact assertions depend on your logging and test setup)
    }

    @Test
    @Order(8)
    @DisplayName("Test loadFromJSON IOException coverage")
    void testLoadFromJSONIOException() {
        LibraryUsers libraryUsers = new LibraryUsers(new MusicStore());
        // Provide a directory path (e.g., 'C:\Windows\System32') instead of a file
        String invalidFilePath = "/System/Library";
        libraryUsers.loadFromJSON(invalidFilePath);

        // Optionally check logs or console output if needed
    }

    @Test
    @DisplayName("Test loadFromJSON with null data")
    void testLoadFromJSONWithNullData() throws Exception {
        // Create an empty file
        Path emptyFilePath = Paths.get("data", "empty.json");
        Files.createDirectories(emptyFilePath.getParent());
        Files.writeString(emptyFilePath, "");

        LibraryUsers localLibraryUsers = new LibraryUsers(new MusicStore());
        localLibraryUsers.loadFromJSON(emptyFilePath.toString());
        // Here, gson.fromJson(...) should produce null, covering 'loadedUsers == null'
    }

    @Test
    @DisplayName("Test loadFromJSON with missing album reference")
    void testLoadFromJSONAlbumNull() throws Exception {
        // Prepare data: one user with one song referencing a non-existent album
        LibraryUsers localLibraryUsers = new LibraryUsers(new MusicStore());
        localLibraryUsers.registerUser("userX", "passX", vicData);
        LibraryModel model = localLibraryUsers.getLibraryModel("userX");
        Song song = new Song("FakeAlbumSong", "fake_album_title");
        model.getUserSongs().put(song.getTitle(), song);

        // Save to a valid JSON
        String testPath = "data/missing_album.json";
        localLibraryUsers.saveToJSON(testPath);

        // Load again to trigger 'if (album == null)'
        LibraryUsers newLibraryUsers = new LibraryUsers(new MusicStore());
        newLibraryUsers.loadFromJSON(testPath);
        // The missing album reference path is now covered
    }
}
