package la1;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaylistTest {

    private Playlist playlist;
    private Song songWithAlbum;
    private Song songWithoutAlbum;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    // 辅助方法：使用实际的 Song 类创建歌曲
    private Song createSong(String title, String artist, String genre, int year) {
        return new Song(title, artist, genre, year);
    }

    // 辅助方法：使用实际的 Album 类创建专辑
    private Album createAlbum(String title, String artist, String genre, int year, List<Song> songs) {
        return new Album(title, artist, genre, year, songs);
    }

    @BeforeEach
    public void setUp() {
        playlist = new Playlist("Test Playlist");
        // 重定向 System.out 用于捕获控制台输出
        System.setOut(new PrintStream(outContent));

        // 创建两首歌曲：一首没有专辑，一首设置了专辑信息
        songWithoutAlbum = createSong("No Album Song", "Artist1", "Rock", 2020);
        songWithAlbum = createSong("With Album Song", "Artist2", "Pop", 2021);
        // 创建一个专辑，并将其设置到 songWithAlbum 中
        Album album = createAlbum("Test Album", "Album Artist", "Pop", 2021, new ArrayList<>());
        songWithAlbum.setAlbum(album);
    }

    // ---------------------------
    // 针对 getName() 方法的测试
    // ---------------------------
    @Test
    public void testGetName() {
        assertEquals("Test Playlist", playlist.getName());
    }

    // ---------------------------
    // 针对 printAsTable 内判断 anyAlbum 的循环
    // ---------------------------
    @Test
    public void testPrintAsTableWithoutAnyAlbum() {
        // 添加的歌曲都没有专辑（toStringList 返回第 7 个元素为 null）
        // 此时 anyAlbum 应该为 false，表头中不会添加 "THE ALBUM"，且每行不追加专辑列
        playlist.addSong(songWithoutAlbum); // songWithoutAlbum 未调用 setAlbum()，其 toStringList 最后一个元素为 null
        outContent.reset();
        playlist.printAsTable();
        String output = outContent.toString();
        // 表头中不应包含 "THE ALBUM"
        assertFalse(output.contains("THE ALBUM"), "当所有歌曲无专辑时，表头中不应包含 'THE ALBUM'");
        // 由于未检测到专辑，行中也不会追加专辑信息，因此也不包含 "NO ALBUM"
        assertFalse(output.contains("NO ALBUM"), "当所有歌曲无专辑时，不应在行中显示 'NO ALBUM'");
    }

    // ---------------------------
    // 针对 if (anyAlbum) { ... } 代码块的测试
    // ---------------------------
    @Test
    public void testPrintAsTableWithAlbumColumnWhenAlbumInfoPresent() {
        // 添加一首有专辑信息的歌曲，此时 toStringList 返回的第 7 个元素不为 null
        playlist.addSong(songWithAlbum);
        outContent.reset();
        playlist.printAsTable();
        String output = outContent.toString();
        // 表头中应包含 "THE ALBUM"
        assertTrue(output.contains("THE ALBUM"), "当检测到专辑信息时，表头中应包含 'THE ALBUM'");
        // 行中应显示专辑名称
        assertTrue(output.contains("Test Album"), "行中应显示专辑名称 'Test Album'");
    }

    @Test
    public void testPrintAsTableWithAlbumColumnWhenAlbumInfoMissing() {
        // 构造一种情况：歌曲的 toStringList 返回的元素数大于 6，但第7个元素为 null
        // 为此，可以直接使用 songWithoutAlbum（其 toStringList() 实现中会在 album 为 null 时添加 null）
        playlist.addSong(songWithoutAlbum);
        // 手动构造另一首歌曲，覆盖 toStringList 返回值，模拟返回 size > 6 但第7个元素为 null
        Song songCustom = new Song("Custom Song", "Artist3", "Jazz", 2019) {
            @Override
            public ArrayList<String> toStringList() {
                // 返回 7 个元素，其中最后一个为 null
                ArrayList<String> list = new ArrayList<>(Arrays.asList(
                        getTitle(), getArtist(), getGenre(), String.valueOf(getYear()),
                        getFavourite(), getRating(), null
                ));
                return list;
            }
        };
        playlist.addSong(songCustom);
        outContent.reset();
        playlist.printAsTable();
        String output = outContent.toString();
        // 虽然行数上为 7 个元素，但由于检测到的专辑信息均为 null，所以应统一显示 "NO ALBUM"
        // 注意：只有当任一行返回非 null 第7个元素时，anyAlbum 才为 true。此处已有 songCustom 的 toStringList 返回 size=7 且第7个元素为 null，
        // 此时 for 循环中 anyAlbum 应保持 false，因此表头中不会显示 "THE ALBUM"，
        // 而每行（如果强制追加专辑列）也不会追加，因为 anyAlbum 为 false。
        // 因此，此测试与 testPrintAsTableWithoutAnyAlbum 类似。
        assertFalse(output.contains("THE ALBUM"), "当所有歌曲的专辑信息均为 null 时，不应显示专辑列");
    }
    @Test
    public void testGetSongsAndSize() {
        assertEquals(0, playlist.getSize());
        playlist.addSong(songWithoutAlbum);
        assertEquals(1, playlist.getSize());
        assertTrue(playlist.getSongs().contains(songWithoutAlbum));
    }

    @Test
    public void testInsertSong() {
        playlist.insertSong(songWithoutAlbum);
        assertEquals(songWithoutAlbum, playlist.getSongs().get(0));
    }

    @Test
    public void testGetPlayingEmpty() {
        playlist.getPlaying();
        assertTrue(outContent.toString().contains("The playlist is empty."));
        outContent.reset();
    }

    @Test
    public void testGetPlayingNonEmpty() {
        playlist.insertSong(songWithoutAlbum);
        playlist.getPlaying();
        assertTrue(outContent.toString().contains("Now playing: [No Album Song]"));
        outContent.reset();
    }

    @Test
    public void testPlayNextEmpty() {
        playlist.playNext();
        assertTrue(outContent.toString().contains("No song to play next."));
        outContent.reset();
    }

    @Test
    public void testPlayNextSingleSong() {
        playlist.insertSong(songWithoutAlbum);
        playlist.playNext();
        assertTrue(outContent.toString().contains("No song to play next."));
        outContent.reset();
    }

    @Test
    public void testPlayNextMultipleSongs() {
        // 先插入两首歌曲，注意 insertSong 在列表头部插入
        playlist.insertSong(songWithoutAlbum);   // 先插入
        playlist.insertSong(songWithAlbum);        // 再插入，此时顺序：songWithAlbum, songWithoutAlbum
        outContent.reset();
        playlist.playNext();
        // playNext 移除首个歌曲后，应播放 songWithoutAlbum
        assertTrue(outContent.toString().contains("Playing: [No Album Song]"));
        outContent.reset();
    }

    @Test
    public void testAddSongNew() {
        playlist.addSong(songWithoutAlbum);
        assertTrue(outContent.toString().contains("Song [No Album Song] added to the playlist."));
        outContent.reset();
    }

    @Test
    public void testAddSongExisting() {
        playlist.addSong(songWithoutAlbum);
        outContent.reset();
        // 再次添加相同的歌曲，应提示已存在
        playlist.addSong(songWithoutAlbum);
        assertTrue(outContent.toString().contains("Song [No Album Song] is already in the playlist."));
        outContent.reset();
    }

    @Test
    public void testRemoveSongExisting() {
        playlist.addSong(songWithoutAlbum);
        outContent.reset();
        playlist.removeSong(songWithoutAlbum);
        assertTrue(outContent.toString().contains("Song [No Album Song] removed from the playlist."));
        outContent.reset();
    }

    @Test
    public void testRemoveSongNonExisting() {
        playlist.addSong(songWithoutAlbum);
        outContent.reset();
        playlist.removeSong(songWithAlbum);
        assertTrue(outContent.toString().contains("Song [With Album Song] not found in the playlist."));
        outContent.reset();
    }

    @Test
    public void testClear() {
        playlist.addSong(songWithoutAlbum);
        playlist.addSong(songWithAlbum);
        outContent.reset();
        playlist.clear();
        assertEquals(0, playlist.getSize());
        assertTrue(outContent.toString().contains("The playlist has been cleared."));
        outContent.reset();
    }

    @Test
    public void testPrintAsTableEmpty() {
        playlist.printAsTable();
        assertTrue(outContent.toString().contains("The playlist is empty."));
        outContent.reset();
    }

    @Test
    public void testPrintAsTableNonEmpty() {
        // 添加一首无专辑和一首有专辑的歌曲
        playlist.addSong(songWithoutAlbum);
        playlist.addSong(songWithAlbum);
        outContent.reset();
        playlist.printAsTable();
        String output = outContent.toString();
        // 表头应包含 "THE ALBUM"
        assertTrue(output.contains("THE ALBUM"), "表头中应包含 'THE ALBUM'");
        // 有专辑的歌曲应显示专辑名称 "Test Album"
        assertTrue(output.contains("Test Album"), "应显示专辑 'Test Album'");
        // 无专辑的歌曲应显示 "NO ALBUM"
        assertTrue(output.contains("NO ALBUM"), "无专辑的歌曲应显示 'NO ALBUM'");
        outContent.reset();
    }

    @Test
    public void testShuffleEmpty() {
        playlist.shuffle();
        assertTrue(outContent.toString().contains("The playlist is empty."));
        outContent.reset();
    }

    @Test
    public void testShuffleNonEmpty() {
        playlist.addSong(songWithoutAlbum);
        playlist.addSong(songWithAlbum);
        outContent.reset();
        playlist.shuffle();
        String output = outContent.toString();
        assertTrue(output.contains("Shuffling the playlist..."));
        assertTrue(output.contains("The playlist has been shuffled."));
        outContent.reset();
    }

    @Test
    public void testToStringEmpty() {
        assertEquals("The playlist is empty.", playlist.toString());
    }

    @Test
    public void testToStringNonEmpty() {
        // 使用 insertSong 按顺序插入
        playlist.insertSong(songWithoutAlbum);
        playlist.insertSong(songWithAlbum);
        String str = playlist.toString();
        // 预期顺序：songWithAlbum（后插入，位于索引 0）、songWithoutAlbum（索引 1）
        assertTrue(str.contains("1) With Album Song"));
        assertTrue(str.contains("2) No Album Song"));
    }
}